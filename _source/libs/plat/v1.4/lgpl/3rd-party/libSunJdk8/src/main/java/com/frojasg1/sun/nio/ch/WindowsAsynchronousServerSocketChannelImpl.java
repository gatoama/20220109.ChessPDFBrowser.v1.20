package com.frojasg1.sun.nio.ch;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AcceptPendingException;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.NotYetBoundException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl;
import com.frojasg1.sun.nio.ch.AsynchronousServerSocketChannelImpl;
import com.frojasg1.sun.nio.ch.CompletedFuture;
import com.frojasg1.sun.nio.ch.IOUtil;
import com.frojasg1.sun.nio.ch.Invoker;
import com.frojasg1.sun.nio.ch.Iocp;
import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.PendingFuture;
import com.frojasg1.sun.nio.ch.PendingIoCache;
import com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl;

class WindowsAsynchronousServerSocketChannelImpl extends com.frojasg1.sun.nio.ch.AsynchronousServerSocketChannelImpl implements com.frojasg1.sun.nio.ch.Iocp.OverlappedChannel {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final int DATA_BUFFER_SIZE = 88;
   private final long handle;
   private final int completionKey;
   private final com.frojasg1.sun.nio.ch.Iocp iocp;
   private final com.frojasg1.sun.nio.ch.PendingIoCache ioCache;
   private final long dataBuffer;
   private AtomicBoolean accepting = new AtomicBoolean();

   WindowsAsynchronousServerSocketChannelImpl(com.frojasg1.sun.nio.ch.Iocp var1) throws IOException {
      super(var1);
      long var2 = (long) com.frojasg1.sun.nio.ch.IOUtil.fdVal(this.fd);

      int var4;
      try {
         var4 = var1.associate(this, var2);
      } catch (IOException var6) {
         closesocket0(var2);
         throw var6;
      }

      this.handle = var2;
      this.completionKey = var4;
      this.iocp = var1;
      this.ioCache = new com.frojasg1.sun.nio.ch.PendingIoCache();
      this.dataBuffer = unsafe.allocateMemory(88L);
   }

   public <V, A> com.frojasg1.sun.nio.ch.PendingFuture<V, A> getByOverlapped(long var1) {
      return this.ioCache.remove(var1);
   }

   void implClose() throws IOException {
      closesocket0(this.handle);
      this.ioCache.close();
      this.iocp.disassociate(this.completionKey);
      unsafe.freeMemory(this.dataBuffer);
   }

   public com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl group() {
      return this.iocp;
   }

   Future<AsynchronousSocketChannel> implAccept(Object var1, CompletionHandler<AsynchronousSocketChannel, Object> var2) {
      if (!this.isOpen()) {
         ClosedChannelException var12 = new ClosedChannelException();
         if (var2 == null) {
            return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var12);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var2, var1, (Object)null, (Throwable)var12);
            return null;
         }
      } else if (this.isAcceptKilled()) {
         throw new RuntimeException("Accept not allowed due to cancellation");
      } else if (this.localAddress == null) {
         throw new NotYetBoundException();
      } else {
         com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl var3 = null;
         IOException var4 = null;

         try {
            this.begin();
            var3 = new com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl(this.iocp, false);
         } catch (IOException var10) {
            var4 = var10;
         } finally {
            this.end();
         }

         if (var4 != null) {
            if (var2 == null) {
               return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var4);
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invokeIndirectly((AsynchronousChannel)this, (CompletionHandler)var2, var1, (Object)null, (Throwable)var4);
               return null;
            }
         } else {
            AccessControlContext var5 = System.getSecurityManager() == null ? null : AccessController.getContext();
            com.frojasg1.sun.nio.ch.PendingFuture var6 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var2, var1);
            WindowsAsynchronousServerSocketChannelImpl.AcceptTask var7 = new WindowsAsynchronousServerSocketChannelImpl.AcceptTask(var3, var5, var6);
            var6.setContext(var7);
            if (!this.accepting.compareAndSet(false, true)) {
               throw new AcceptPendingException();
            } else {
               if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
                  var7.run();
               } else {
                  com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var7);
               }

               return var6;
            }
         }
      }
   }

   private static native void initIDs();

   private static native int accept0(long var0, long var2, long var4, long var6) throws IOException;

   private static native void updateAcceptContext(long var0, long var2) throws IOException;

   private static native void closesocket0(long var0) throws IOException;

   static {
      IOUtil.load();
      initIDs();
   }

   private class AcceptTask implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl channel;
      private final AccessControlContext acc;
      private final com.frojasg1.sun.nio.ch.PendingFuture<AsynchronousSocketChannel, Object> result;

      AcceptTask(com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl var2, AccessControlContext var3, com.frojasg1.sun.nio.ch.PendingFuture<AsynchronousSocketChannel, Object> var4) {
         this.channel = var2;
         this.acc = var3;
         this.result = var4;
      }

      void enableAccept() {
         WindowsAsynchronousServerSocketChannelImpl.this.accepting.set(false);
      }

      void closeChildChannel() {
         try {
            this.channel.close();
         } catch (IOException var2) {
         }

      }

      void finishAccept() throws IOException {
         WindowsAsynchronousServerSocketChannelImpl.updateAcceptContext(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle());
         InetSocketAddress var1 = com.frojasg1.sun.nio.ch.Net.localAddress(this.channel.fd);
         final InetSocketAddress var2 = Net.remoteAddress(this.channel.fd);
         this.channel.setConnected(var1, var2);
         if (this.acc != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  SecurityManager var1 = System.getSecurityManager();
                  var1.checkAccept(var2.getAddress().getHostAddress(), var2.getPort());
                  return null;
               }
            }, this.acc);
         }

      }

      public void run() {
         long var1 = 0L;

         label187: {
            try {
               WindowsAsynchronousServerSocketChannelImpl.this.begin();

               try {
                  this.channel.begin();
                  synchronized(this.result) {
                     var1 = WindowsAsynchronousServerSocketChannelImpl.this.ioCache.add(this.result);
                     int var4 = WindowsAsynchronousServerSocketChannelImpl.accept0(WindowsAsynchronousServerSocketChannelImpl.this.handle, this.channel.handle(), var1, WindowsAsynchronousServerSocketChannelImpl.this.dataBuffer);
                     if (var4 != -2) {
                        this.finishAccept();
                        this.enableAccept();
                        this.result.setResult(this.channel);
                        break label187;
                     }
                  }
               } finally {
                  this.channel.end();
               }
            } catch (Throwable var18) {
               Object var3 = var18;
               if (var1 != 0L) {
                  WindowsAsynchronousServerSocketChannelImpl.this.ioCache.remove(var1);
               }

               this.closeChildChannel();
               if (var18 instanceof ClosedChannelException) {
                  var3 = new AsynchronousCloseException();
               }

               if (!(var3 instanceof IOException) && !(var3 instanceof SecurityException)) {
                  var3 = new IOException((Throwable)var3);
               }

               this.enableAccept();
               this.result.setFailure((Throwable)var3);
               break label187;
            } finally {
               WindowsAsynchronousServerSocketChannelImpl.this.end();
            }

            return;
         }

         if (this.result.isCancelled()) {
            this.closeChildChannel();
         }

         com.frojasg1.sun.nio.ch.Invoker.invokeIndirectly(this.result);
      }

      public void completed(int var1, boolean var2) {
         try {
            if (WindowsAsynchronousServerSocketChannelImpl.this.iocp.isShutdown()) {
               throw new IOException(new ShutdownChannelGroupException());
            }

            try {
               WindowsAsynchronousServerSocketChannelImpl.this.begin();

               try {
                  this.channel.begin();
                  this.finishAccept();
               } finally {
                  this.channel.end();
               }
            } finally {
               WindowsAsynchronousServerSocketChannelImpl.this.end();
            }

            this.enableAccept();
            this.result.setResult(this.channel);
         } catch (Throwable var13) {
            Object var3 = var13;
            this.enableAccept();
            this.closeChildChannel();
            if (var13 instanceof ClosedChannelException) {
               var3 = new AsynchronousCloseException();
            }

            if (!(var3 instanceof IOException) && !(var3 instanceof SecurityException)) {
               var3 = new IOException((Throwable)var3);
            }

            this.result.setFailure((Throwable)var3);
         }

         if (this.result.isCancelled()) {
            this.closeChildChannel();
         }

         com.frojasg1.sun.nio.ch.Invoker.invokeIndirectly(this.result);
      }

      public void failed(int var1, IOException var2) {
         this.enableAccept();
         this.closeChildChannel();
         if (WindowsAsynchronousServerSocketChannelImpl.this.isOpen()) {
            this.result.setFailure(var2);
         } else {
            this.result.setFailure(new AsynchronousCloseException());
         }

         com.frojasg1.sun.nio.ch.Invoker.invokeIndirectly(this.result);
      }
   }
}
