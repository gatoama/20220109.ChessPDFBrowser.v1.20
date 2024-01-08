package com.frojasg1.sun.rmi.transport.tcp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.rmi.ConnectIOException;
import java.rmi.RemoteException;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.WeakHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.frojasg1.sun.rmi.runtime.Log;
import com.frojasg1.sun.rmi.runtime.RuntimeUtil;
import com.frojasg1.sun.rmi.transport.Channel;
import com.frojasg1.sun.rmi.transport.Connection;
import com.frojasg1.sun.rmi.transport.Endpoint;
import com.frojasg1.sun.rmi.transport.tcp.ConnectionAcceptor;
import com.frojasg1.sun.rmi.transport.tcp.ConnectionMultiplexer;
import com.frojasg1.sun.rmi.transport.tcp.TCPConnection;
import com.frojasg1.sun.rmi.transport.tcp.TCPEndpoint;
import com.frojasg1.sun.rmi.transport.tcp.TCPTransport;
import com.frojasg1.sun.security.action.GetIntegerAction;
import com.frojasg1.sun.security.action.GetLongAction;

public class TCPChannel implements Channel {
   private final com.frojasg1.sun.rmi.transport.tcp.TCPEndpoint ep;
   private final com.frojasg1.sun.rmi.transport.tcp.TCPTransport tr;
   private final List<com.frojasg1.sun.rmi.transport.tcp.TCPConnection> freeList = new ArrayList();
   private Future<?> reaper = null;
   private boolean usingMultiplexer = false;
   private com.frojasg1.sun.rmi.transport.tcp.ConnectionMultiplexer multiplexer = null;
   private com.frojasg1.sun.rmi.transport.tcp.ConnectionAcceptor acceptor;
   private AccessControlContext okContext;
   private WeakHashMap<AccessControlContext, Reference<AccessControlContext>> authcache;
   private SecurityManager cacheSecurityManager = null;
   private static final long idleTimeout = (Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.transport.connectionTimeout", 15000L));
   private static final int handshakeTimeout = (Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.handshakeTimeout", 60000));
   private static final int responseTimeout = (Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.transport.tcp.responseTimeout", 0));
   private static final ScheduledExecutorService scheduler = ((RuntimeUtil)AccessController.doPrivileged(new RuntimeUtil.GetInstanceAction())).getScheduler();

   TCPChannel(com.frojasg1.sun.rmi.transport.tcp.TCPTransport var1, com.frojasg1.sun.rmi.transport.tcp.TCPEndpoint var2) {
      this.tr = var1;
      this.ep = var2;
   }

   public Endpoint getEndpoint() {
      return this.ep;
   }

   private void checkConnectPermission() throws SecurityException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         if (var1 != this.cacheSecurityManager) {
            this.okContext = null;
            this.authcache = new WeakHashMap();
            this.cacheSecurityManager = var1;
         }

         AccessControlContext var2 = AccessController.getContext();
         if (this.okContext == null || !this.okContext.equals(var2) && !this.authcache.containsKey(var2)) {
            var1.checkConnect(this.ep.getHost(), this.ep.getPort());
            this.authcache.put(var2, new SoftReference(var2));
         }

         this.okContext = var2;
      }
   }

   public Connection newConnection() throws RemoteException {
      com.frojasg1.sun.rmi.transport.tcp.TCPConnection var1;
      do {
         var1 = null;
         synchronized(this.freeList) {
            int var3 = this.freeList.size() - 1;
            if (var3 >= 0) {
               this.checkConnectPermission();
               var1 = (com.frojasg1.sun.rmi.transport.tcp.TCPConnection)this.freeList.get(var3);
               this.freeList.remove(var3);
            }
         }

         if (var1 != null) {
            if (!var1.isDead()) {
               com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
               return var1;
            }

            this.free(var1, false);
         }
      } while(var1 != null);

      return this.createConnection();
   }

   private Connection createConnection() throws RemoteException {
      com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.BRIEF, "create connection");
      com.frojasg1.sun.rmi.transport.tcp.TCPConnection var1;
      if (!this.usingMultiplexer) {
         Socket var2 = this.ep.newSocket();
         var1 = new com.frojasg1.sun.rmi.transport.tcp.TCPConnection(this, var2);

         try {
            DataOutputStream var3 = new DataOutputStream(var1.getOutputStream());
            this.writeTransportHeader(var3);
            if (!var1.isReusable()) {
               var3.writeByte(76);
            } else {
               var3.writeByte(75);
               var3.flush();
               int var4 = 0;

               try {
                  var4 = var2.getSoTimeout();
                  var2.setSoTimeout(handshakeTimeout);
               } catch (Exception var16) {
               }

               DataInputStream var5 = new DataInputStream(var1.getInputStream());
               byte var6 = var5.readByte();
               if (var6 != 78) {
                  throw new ConnectIOException(var6 == 79 ? "JRMP StreamProtocol not supported by server" : "non-JRMP server at remote endpoint");
               }

               String var7 = var5.readUTF();
               int var8 = var5.readInt();
               if (com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                  com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.VERBOSE, "server suggested " + var7 + ":" + var8);
               }

               com.frojasg1.sun.rmi.transport.tcp.TCPEndpoint.setLocalHost(var7);
               com.frojasg1.sun.rmi.transport.tcp.TCPEndpoint var9 = TCPEndpoint.getLocalEndpoint(0, (RMIClientSocketFactory)null, (RMIServerSocketFactory)null);
               var3.writeUTF(var9.getHost());
               var3.writeInt(var9.getPort());
               if (com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.isLoggable(Log.VERBOSE)) {
                  com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.VERBOSE, "using " + var9.getHost() + ":" + var9.getPort());
               }

               try {
                  var2.setSoTimeout(var4 != 0 ? var4 : responseTimeout);
               } catch (Exception var15) {
               }

               var3.flush();
            }
         } catch (IOException var17) {
            try {
               var1.close();
            } catch (Exception var12) {
            }

            if (var17 instanceof RemoteException) {
               throw (RemoteException)var17;
            }

            throw new ConnectIOException("error during JRMP connection establishment", var17);
         }
      } else {
         try {
            var1 = this.multiplexer.openConnection();
         } catch (IOException var14) {
            synchronized(this) {
               this.usingMultiplexer = false;
               this.multiplexer = null;
            }

            throw new ConnectIOException("error opening virtual connection over multiplexed connection", var14);
         }
      }

      return var1;
   }

   public void free(Connection var1, boolean var2) {
      if (var1 != null) {
         if (var2 && var1.isReusable()) {
            long var3 = System.currentTimeMillis();
            com.frojasg1.sun.rmi.transport.tcp.TCPConnection var5 = (com.frojasg1.sun.rmi.transport.tcp.TCPConnection)var1;
            com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.BRIEF, "reuse connection");
            synchronized(this.freeList) {
               this.freeList.add(var5);
               if (this.reaper == null) {
                  com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.BRIEF, "create reaper");
                  this.reaper = scheduler.scheduleWithFixedDelay(new Runnable() {
                     public void run() {
                        com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.VERBOSE, "wake up");
                        TCPChannel.this.freeCachedConnections();
                     }
                  }, idleTimeout, idleTimeout, TimeUnit.MILLISECONDS);
               }
            }

            var5.setLastUseTime(var3);
            var5.setExpiration(var3 + idleTimeout);
         } else {
            com.frojasg1.sun.rmi.transport.tcp.TCPTransport.tcpLog.log(Log.BRIEF, "close connection");

            try {
               var1.close();
            } catch (IOException var8) {
            }
         }

      }
   }

   private void writeTransportHeader(DataOutputStream var1) throws RemoteException {
      try {
         DataOutputStream var2 = new DataOutputStream(var1);
         var2.writeInt(1246907721);
         var2.writeShort(2);
      } catch (IOException var3) {
         throw new ConnectIOException("error writing JRMP transport header", var3);
      }
   }

   synchronized void useMultiplexer(com.frojasg1.sun.rmi.transport.tcp.ConnectionMultiplexer var1) {
      this.multiplexer = var1;
      this.usingMultiplexer = true;
   }

   void acceptMultiplexConnection(Connection var1) {
      if (this.acceptor == null) {
         this.acceptor = new com.frojasg1.sun.rmi.transport.tcp.ConnectionAcceptor(this.tr);
         this.acceptor.startNewAcceptor();
      }

      this.acceptor.accept(var1);
   }

   public void shedCache() {
      Connection[] var1;
      synchronized(this.freeList) {
         var1 = (Connection[])this.freeList.toArray(new Connection[this.freeList.size()]);
         this.freeList.clear();
      }

      int var2 = var1.length;

      while(true) {
         --var2;
         if (var2 < 0) {
            return;
         }

         Connection var3 = var1[var2];
         var1[var2] = null;

         try {
            var3.close();
         } catch (IOException var5) {
         }
      }
   }

   private void freeCachedConnections() {
      synchronized(this.freeList) {
         int var2 = this.freeList.size();
         if (var2 > 0) {
            long var3 = System.currentTimeMillis();
            ListIterator var5 = this.freeList.listIterator(var2);

            while(var5.hasPrevious()) {
               com.frojasg1.sun.rmi.transport.tcp.TCPConnection var6 = (TCPConnection)var5.previous();
               if (var6.expired(var3)) {
                  TCPTransport.tcpLog.log(Log.VERBOSE, "connection timeout expired");

                  try {
                     var6.close();
                  } catch (IOException var9) {
                  }

                  var5.remove();
               }
            }
         }

         if (this.freeList.isEmpty()) {
            this.reaper.cancel(false);
            this.reaper = null;
         }

      }
   }
}
