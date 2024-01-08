package com.frojasg1.sun.net.httpserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.Filter.Chain;
import com.frojasg1.sun.net.httpserver.Code;
import com.frojasg1.sun.net.httpserver.ContextList;
import com.frojasg1.sun.net.httpserver.Event;
import com.frojasg1.sun.net.httpserver.ExchangeImpl;
import com.frojasg1.sun.net.httpserver.HttpConnection;
import com.frojasg1.sun.net.httpserver.HttpContextImpl;
import com.frojasg1.sun.net.httpserver.HttpError;
import com.frojasg1.sun.net.httpserver.HttpExchangeImpl;
import com.frojasg1.sun.net.httpserver.HttpsExchangeImpl;
import com.frojasg1.sun.net.httpserver.LeftOverInputStream;
import com.frojasg1.sun.net.httpserver.Request;
import com.frojasg1.sun.net.httpserver.SSLStreams;
import com.frojasg1.sun.net.httpserver.ServerConfig;
import com.frojasg1.sun.net.httpserver.TimeSource;
import com.frojasg1.sun.net.httpserver.WriteFinishedEvent;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

class ServerImpl implements com.frojasg1.sun.net.httpserver.TimeSource {
   private String protocol;
   private boolean https;
   private Executor executor;
   private HttpsConfigurator httpsConfig;
   private SSLContext sslContext;
   private com.frojasg1.sun.net.httpserver.ContextList contexts;
   private InetSocketAddress address;
   private ServerSocketChannel schan;
   private Selector selector;
   private SelectionKey listenerKey;
   private Set<com.frojasg1.sun.net.httpserver.HttpConnection> idleConnections;
   private Set<com.frojasg1.sun.net.httpserver.HttpConnection> allConnections;
   private Set<com.frojasg1.sun.net.httpserver.HttpConnection> reqConnections;
   private Set<com.frojasg1.sun.net.httpserver.HttpConnection> rspConnections;
   private List<com.frojasg1.sun.net.httpserver.Event> events;
   private Object lolock = new Object();
   private volatile boolean finished = false;
   private volatile boolean terminating = false;
   private boolean bound = false;
   private boolean started = false;
   private volatile long time;
   private volatile long subticks = 0L;
   private volatile long ticks;
   private HttpServer wrapper;
   static final int CLOCK_TICK = com.frojasg1.sun.net.httpserver.ServerConfig.getClockTick();
   static final long IDLE_INTERVAL = com.frojasg1.sun.net.httpserver.ServerConfig.getIdleInterval();
   static final int MAX_IDLE_CONNECTIONS = com.frojasg1.sun.net.httpserver.ServerConfig.getMaxIdleConnections();
   static final long TIMER_MILLIS = com.frojasg1.sun.net.httpserver.ServerConfig.getTimerMillis();
   static final long MAX_REQ_TIME = getTimeMillis(com.frojasg1.sun.net.httpserver.ServerConfig.getMaxReqTime());
   static final long MAX_RSP_TIME = getTimeMillis(com.frojasg1.sun.net.httpserver.ServerConfig.getMaxRspTime());
   static final boolean timer1Enabled;
   private Timer timer;
   private Timer timer1;
   private Logger logger;
   ServerImpl.Dispatcher dispatcher;
   static boolean debug;
   private int exchangeCount = 0;

   ServerImpl(HttpServer var1, String var2, InetSocketAddress var3, int var4) throws IOException {
      this.protocol = var2;
      this.wrapper = var1;
      this.logger = Logger.getLogger("com.sun.net.httpserver");
      com.frojasg1.sun.net.httpserver.ServerConfig.checkLegacyProperties(this.logger);
      this.https = var2.equalsIgnoreCase("https");
      this.address = var3;
      this.contexts = new com.frojasg1.sun.net.httpserver.ContextList();
      this.schan = ServerSocketChannel.open();
      if (var3 != null) {
         ServerSocket var5 = this.schan.socket();
         var5.bind(var3, var4);
         this.bound = true;
      }

      this.selector = Selector.open();
      this.schan.configureBlocking(false);
      this.listenerKey = this.schan.register(this.selector, 16);
      this.dispatcher = new ServerImpl.Dispatcher();
      this.idleConnections = Collections.synchronizedSet(new HashSet());
      this.allConnections = Collections.synchronizedSet(new HashSet());
      this.reqConnections = Collections.synchronizedSet(new HashSet());
      this.rspConnections = Collections.synchronizedSet(new HashSet());
      this.time = System.currentTimeMillis();
      this.timer = new Timer("server-timer", true);
      this.timer.schedule(new ServerImpl.ServerTimerTask(), (long)CLOCK_TICK, (long)CLOCK_TICK);
      if (timer1Enabled) {
         this.timer1 = new Timer("server-timer1", true);
         this.timer1.schedule(new ServerImpl.ServerTimerTask1(), TIMER_MILLIS, TIMER_MILLIS);
         this.logger.config("HttpServer timer1 enabled period in ms:  " + TIMER_MILLIS);
         this.logger.config("MAX_REQ_TIME:  " + MAX_REQ_TIME);
         this.logger.config("MAX_RSP_TIME:  " + MAX_RSP_TIME);
      }

      this.events = new LinkedList();
      this.logger.config("HttpServer created " + var2 + " " + var3);
   }

   public void bind(InetSocketAddress var1, int var2) throws IOException {
      if (this.bound) {
         throw new BindException("HttpServer already bound");
      } else if (var1 == null) {
         throw new NullPointerException("null address");
      } else {
         ServerSocket var3 = this.schan.socket();
         var3.bind(var1, var2);
         this.bound = true;
      }
   }

   public void start() {
      if (this.bound && !this.started && !this.finished) {
         if (this.executor == null) {
            this.executor = new ServerImpl.DefaultExecutor();
         }

         Thread var1 = new Thread(this.dispatcher);
         this.started = true;
         var1.start();
      } else {
         throw new IllegalStateException("server in wrong state");
      }
   }

   public void setExecutor(Executor var1) {
      if (this.started) {
         throw new IllegalStateException("server already started");
      } else {
         this.executor = var1;
      }
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public void setHttpsConfigurator(HttpsConfigurator var1) {
      if (var1 == null) {
         throw new NullPointerException("null HttpsConfigurator");
      } else if (this.started) {
         throw new IllegalStateException("server already started");
      } else {
         this.httpsConfig = var1;
         this.sslContext = var1.getSSLContext();
      }
   }

   public HttpsConfigurator getHttpsConfigurator() {
      return this.httpsConfig;
   }

   public void stop(int var1) {
      if (var1 < 0) {
         throw new IllegalArgumentException("negative delay parameter");
      } else {
         this.terminating = true;

         try {
            this.schan.close();
         } catch (IOException var8) {
         }

         this.selector.wakeup();
         long var2 = System.currentTimeMillis() + (long)(var1 * 1000);

         while(System.currentTimeMillis() < var2) {
            this.delay();
            if (this.finished) {
               break;
            }
         }

         this.finished = true;
         this.selector.wakeup();
         synchronized(this.allConnections) {
            Iterator var5 = this.allConnections.iterator();

            while(true) {
               if (!var5.hasNext()) {
                  break;
               }

               com.frojasg1.sun.net.httpserver.HttpConnection var6 = (com.frojasg1.sun.net.httpserver.HttpConnection)var5.next();
               var6.close();
            }
         }

         this.allConnections.clear();
         this.idleConnections.clear();
         this.timer.cancel();
         if (timer1Enabled) {
            this.timer1.cancel();
         }

      }
   }

   public synchronized com.frojasg1.sun.net.httpserver.HttpContextImpl createContext(String var1, HttpHandler var2) {
      if (var2 != null && var1 != null) {
         com.frojasg1.sun.net.httpserver.HttpContextImpl var3 = new com.frojasg1.sun.net.httpserver.HttpContextImpl(this.protocol, var1, var2, this);
         this.contexts.add(var3);
         this.logger.config("context created: " + var1);
         return var3;
      } else {
         throw new NullPointerException("null handler, or path parameter");
      }
   }

   public synchronized com.frojasg1.sun.net.httpserver.HttpContextImpl createContext(String var1) {
      if (var1 == null) {
         throw new NullPointerException("null path parameter");
      } else {
         com.frojasg1.sun.net.httpserver.HttpContextImpl var2 = new com.frojasg1.sun.net.httpserver.HttpContextImpl(this.protocol, var1, (HttpHandler)null, this);
         this.contexts.add(var2);
         this.logger.config("context created: " + var1);
         return var2;
      }
   }

   public synchronized void removeContext(String var1) throws IllegalArgumentException {
      if (var1 == null) {
         throw new NullPointerException("null path parameter");
      } else {
         this.contexts.remove(this.protocol, var1);
         this.logger.config("context removed: " + var1);
      }
   }

   public synchronized void removeContext(HttpContext var1) throws IllegalArgumentException {
      if (!(var1 instanceof com.frojasg1.sun.net.httpserver.HttpContextImpl)) {
         throw new IllegalArgumentException("wrong HttpContext type");
      } else {
         this.contexts.remove((com.frojasg1.sun.net.httpserver.HttpContextImpl)var1);
         this.logger.config("context removed: " + var1.getPath());
      }
   }

   public InetSocketAddress getAddress() {
      return (InetSocketAddress)AccessController.doPrivileged(new PrivilegedAction<InetSocketAddress>() {
         public InetSocketAddress run() {
            return (InetSocketAddress)ServerImpl.this.schan.socket().getLocalSocketAddress();
         }
      });
   }

   Selector getSelector() {
      return this.selector;
   }

   void addEvent(com.frojasg1.sun.net.httpserver.Event var1) {
      synchronized(this.lolock) {
         this.events.add(var1);
         this.selector.wakeup();
      }
   }

   static synchronized void dprint(String var0) {
      if (debug) {
         System.out.println(var0);
      }

   }

   static synchronized void dprint(Exception var0) {
      if (debug) {
         System.out.println(var0);
         var0.printStackTrace();
      }

   }

   Logger getLogger() {
      return this.logger;
   }

   private void closeConnection(com.frojasg1.sun.net.httpserver.HttpConnection var1) {
      var1.close();
      this.allConnections.remove(var1);
      switch(var1.getState()) {
      case REQUEST:
         this.reqConnections.remove(var1);
         break;
      case RESPONSE:
         this.rspConnections.remove(var1);
         break;
      case IDLE:
         this.idleConnections.remove(var1);
      }

      assert !this.reqConnections.remove(var1);

      assert !this.rspConnections.remove(var1);

      assert !this.idleConnections.remove(var1);

   }

   void logReply(int var1, String var2, String var3) {
      if (this.logger.isLoggable(Level.FINE)) {
         if (var3 == null) {
            var3 = "";
         }

         String var4;
         if (var2.length() > 80) {
            var4 = var2.substring(0, 80) + "<TRUNCATED>";
         } else {
            var4 = var2;
         }

         String var5 = var4 + " [" + var1 + " " + com.frojasg1.sun.net.httpserver.Code.msg(var1) + "] (" + var3 + ")";
         this.logger.fine(var5);
      }
   }

   long getTicks() {
      return this.ticks;
   }

   public long getTime() {
      return this.time;
   }

   void delay() {
      Thread.yield();

      try {
         Thread.sleep(200L);
      } catch (InterruptedException var2) {
      }

   }

   synchronized void startExchange() {
      ++this.exchangeCount;
   }

   synchronized int endExchange() {
      --this.exchangeCount;

      assert this.exchangeCount >= 0;

      return this.exchangeCount;
   }

   HttpServer getWrapper() {
      return this.wrapper;
   }

   void requestStarted(com.frojasg1.sun.net.httpserver.HttpConnection var1) {
      var1.creationTime = this.getTime();
      var1.setState(com.frojasg1.sun.net.httpserver.HttpConnection.State.REQUEST);
      this.reqConnections.add(var1);
   }

   void requestCompleted(com.frojasg1.sun.net.httpserver.HttpConnection var1) {
      assert var1.getState() == com.frojasg1.sun.net.httpserver.HttpConnection.State.REQUEST;

      this.reqConnections.remove(var1);
      var1.rspStartedTime = this.getTime();
      this.rspConnections.add(var1);
      var1.setState(com.frojasg1.sun.net.httpserver.HttpConnection.State.RESPONSE);
   }

   void responseCompleted(com.frojasg1.sun.net.httpserver.HttpConnection var1) {
      assert var1.getState() == com.frojasg1.sun.net.httpserver.HttpConnection.State.RESPONSE;

      this.rspConnections.remove(var1);
      var1.setState(com.frojasg1.sun.net.httpserver.HttpConnection.State.IDLE);
   }

   void logStackTrace(String var1) {
      this.logger.finest(var1);
      StringBuilder var2 = new StringBuilder();
      StackTraceElement[] var3 = Thread.currentThread().getStackTrace();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         var2.append(var3[var4].toString()).append("\n");
      }

      this.logger.finest(var2.toString());
   }

   static long getTimeMillis(long var0) {
      return var0 == -1L ? -1L : var0 * 1000L;
   }

   static {
      timer1Enabled = MAX_REQ_TIME != -1L || MAX_RSP_TIME != -1L;
      debug = com.frojasg1.sun.net.httpserver.ServerConfig.debugEnabled();
   }

   private static class DefaultExecutor implements Executor {
      private DefaultExecutor() {
      }

      public void execute(Runnable var1) {
         var1.run();
      }
   }

   class Dispatcher implements Runnable {
      final LinkedList<com.frojasg1.sun.net.httpserver.HttpConnection> connsToRegister = new LinkedList();

      Dispatcher() {
      }

      private void handleEvent(com.frojasg1.sun.net.httpserver.Event var1) {
         com.frojasg1.sun.net.httpserver.ExchangeImpl var2 = var1.exchange;
         com.frojasg1.sun.net.httpserver.HttpConnection var3 = var2.getConnection();

         try {
            if (var1 instanceof com.frojasg1.sun.net.httpserver.WriteFinishedEvent) {
               int var4 = ServerImpl.this.endExchange();
               if (ServerImpl.this.terminating && var4 == 0) {
                  ServerImpl.this.finished = true;
               }

               ServerImpl.this.responseCompleted(var3);
               com.frojasg1.sun.net.httpserver.LeftOverInputStream var5 = var2.getOriginalInputStream();
               if (!var5.isEOF()) {
                  var2.close = true;
               }

               if (!var2.close && ServerImpl.this.idleConnections.size() < ServerImpl.MAX_IDLE_CONNECTIONS) {
                  if (var5.isDataBuffered()) {
                     ServerImpl.this.requestStarted(var3);
                     this.handle(var3.getChannel(), var3);
                  } else {
                     this.connsToRegister.add(var3);
                  }
               } else {
                  var3.close();
                  ServerImpl.this.allConnections.remove(var3);
               }
            }
         } catch (IOException var6) {
            ServerImpl.this.logger.log(Level.FINER, "Dispatcher (1)", var6);
            var3.close();
         }

      }

      void reRegister(com.frojasg1.sun.net.httpserver.HttpConnection var1) {
         try {
            SocketChannel var2 = var1.getChannel();
            var2.configureBlocking(false);
            SelectionKey var3 = var2.register(ServerImpl.this.selector, 1);
            var3.attach(var1);
            var1.selectionKey = var3;
            var1.time = ServerImpl.this.getTime() + ServerImpl.IDLE_INTERVAL;
            ServerImpl.this.idleConnections.add(var1);
         } catch (IOException var4) {
            ServerImpl.dprint((Exception)var4);
            ServerImpl.this.logger.log(Level.FINER, "Dispatcher(8)", var4);
            var1.close();
         }

      }

      public void run() {
         while(!ServerImpl.this.finished) {
            try {
               List var1 = null;
               synchronized(ServerImpl.this.lolock) {
                  if (ServerImpl.this.events.size() > 0) {
                     var1 = ServerImpl.this.events;
                     ServerImpl.this.events = new LinkedList();
                  }
               }

               Iterator var2;
               if (var1 != null) {
                  var2 = var1.iterator();

                  while(var2.hasNext()) {
                     com.frojasg1.sun.net.httpserver.Event var3 = (com.frojasg1.sun.net.httpserver.Event)var2.next();
                     this.handleEvent(var3);
                  }
               }

               var2 = this.connsToRegister.iterator();

               while(var2.hasNext()) {
                  com.frojasg1.sun.net.httpserver.HttpConnection var15 = (com.frojasg1.sun.net.httpserver.HttpConnection)var2.next();
                  this.reRegister(var15);
               }

               this.connsToRegister.clear();
               ServerImpl.this.selector.select(1000L);
               Set var14 = ServerImpl.this.selector.selectedKeys();
               Iterator var16 = var14.iterator();

               while(var16.hasNext()) {
                  SelectionKey var4 = (SelectionKey)var16.next();
                  var16.remove();
                  com.frojasg1.sun.net.httpserver.HttpConnection var7;
                  if (var4.equals(ServerImpl.this.listenerKey)) {
                     if (!ServerImpl.this.terminating) {
                        SocketChannel var5 = ServerImpl.this.schan.accept();
                        if (com.frojasg1.sun.net.httpserver.ServerConfig.noDelay()) {
                           var5.socket().setTcpNoDelay(true);
                        }

                        if (var5 != null) {
                           var5.configureBlocking(false);
                           SelectionKey var6 = var5.register(ServerImpl.this.selector, 1);
                           var7 = new com.frojasg1.sun.net.httpserver.HttpConnection();
                           var7.selectionKey = var6;
                           var7.setChannel(var5);
                           var6.attach(var7);
                           ServerImpl.this.requestStarted(var7);
                           ServerImpl.this.allConnections.add(var7);
                        }
                     }
                  } else {
                     try {
                        if (var4.isReadable()) {
                           SocketChannel var17 = (SocketChannel)var4.channel();
                           var7 = (com.frojasg1.sun.net.httpserver.HttpConnection)var4.attachment();
                           var4.cancel();
                           var17.configureBlocking(true);
                           if (ServerImpl.this.idleConnections.remove(var7)) {
                              ServerImpl.this.requestStarted(var7);
                           }

                           this.handle(var17, var7);
                        } else {
                           assert false;
                        }
                     } catch (CancelledKeyException var8) {
                        this.handleException(var4, (Exception)null);
                     } catch (IOException var9) {
                        this.handleException(var4, var9);
                     }
                  }
               }

               ServerImpl.this.selector.selectNow();
            } catch (IOException var12) {
               ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", var12);
            } catch (Exception var13) {
               ServerImpl.this.logger.log(Level.FINER, "Dispatcher (7)", var13);
            }
         }

         try {
            ServerImpl.this.selector.close();
         } catch (Exception var10) {
         }

      }

      private void handleException(SelectionKey var1, Exception var2) {
         com.frojasg1.sun.net.httpserver.HttpConnection var3 = (com.frojasg1.sun.net.httpserver.HttpConnection)var1.attachment();
         if (var2 != null) {
            ServerImpl.this.logger.log(Level.FINER, "Dispatcher (2)", var2);
         }

         ServerImpl.this.closeConnection(var3);
      }

      public void handle(SocketChannel var1, com.frojasg1.sun.net.httpserver.HttpConnection var2) throws IOException {
         try {
            ServerImpl.Exchange var3 = ServerImpl.this.new Exchange(var1, ServerImpl.this.protocol, var2);
            ServerImpl.this.executor.execute(var3);
         } catch (com.frojasg1.sun.net.httpserver.HttpError var4) {
            ServerImpl.this.logger.log(Level.FINER, "Dispatcher (4)", var4);
            ServerImpl.this.closeConnection(var2);
         } catch (IOException var5) {
            ServerImpl.this.logger.log(Level.FINER, "Dispatcher (5)", var5);
            ServerImpl.this.closeConnection(var2);
         }

      }
   }

   class Exchange implements Runnable {
      SocketChannel chan;
      com.frojasg1.sun.net.httpserver.HttpConnection connection;
      com.frojasg1.sun.net.httpserver.HttpContextImpl context;
      InputStream rawin;
      OutputStream rawout;
      String protocol;
      com.frojasg1.sun.net.httpserver.ExchangeImpl tx;
      com.frojasg1.sun.net.httpserver.HttpContextImpl ctx;
      boolean rejected = false;

      Exchange(SocketChannel var2, String var3, com.frojasg1.sun.net.httpserver.HttpConnection var4) throws IOException {
         this.chan = var2;
         this.connection = var4;
         this.protocol = var3;
      }

      public void run() {
         this.context = this.connection.getHttpContext();
         SSLEngine var2 = null;
         String var3 = null;
         com.frojasg1.sun.net.httpserver.SSLStreams var4 = null;

         try {
            boolean var1;
            if (this.context != null) {
               this.rawin = this.connection.getInputStream();
               this.rawout = this.connection.getRawOutputStream();
               var1 = false;
            } else {
               var1 = true;
               if (ServerImpl.this.https) {
                  if (ServerImpl.this.sslContext == null) {
                     ServerImpl.this.logger.warning("SSL connection received. No https contxt created");
                     throw new com.frojasg1.sun.net.httpserver.HttpError("No SSL context established");
                  }

                  var4 = new com.frojasg1.sun.net.httpserver.SSLStreams(ServerImpl.this, ServerImpl.this.sslContext, this.chan);
                  this.rawin = var4.getInputStream();
                  this.rawout = var4.getOutputStream();
                  var2 = var4.getSSLEngine();
                  this.connection.sslStreams = var4;
               } else {
                  this.rawin = new BufferedInputStream(new com.frojasg1.sun.net.httpserver.Request.ReadStream(ServerImpl.this, this.chan));
                  this.rawout = new com.frojasg1.sun.net.httpserver.Request.WriteStream(ServerImpl.this, this.chan);
               }

               this.connection.raw = this.rawin;
               this.connection.rawout = this.rawout;
            }

            com.frojasg1.sun.net.httpserver.Request var5 = new com.frojasg1.sun.net.httpserver.Request(this.rawin, this.rawout);
            var3 = var5.requestLine();
            if (var3 == null) {
               ServerImpl.this.closeConnection(this.connection);
               return;
            }

            int var6 = var3.indexOf(32);
            if (var6 == -1) {
               this.reject(400, var3, "Bad request line");
               return;
            }

            String var7 = var3.substring(0, var6);
            int var8 = var6 + 1;
            var6 = var3.indexOf(32, var8);
            if (var6 == -1) {
               this.reject(400, var3, "Bad request line");
               return;
            }

            String var9 = var3.substring(var8, var6);
            URI var10 = new URI(var9);
            var8 = var6 + 1;
            String var11 = var3.substring(var8);
            Headers var12 = var5.headers();
            String var13 = var12.getFirst("Transfer-encoding");
            long var14 = 0L;
            if (var13 != null && var13.equalsIgnoreCase("chunked")) {
               var14 = -1L;
            } else {
               var13 = var12.getFirst("Content-Length");
               if (var13 != null) {
                  var14 = Long.parseLong(var13);
               }

               if (var14 == 0L) {
                  ServerImpl.this.requestCompleted(this.connection);
               }
            }

            this.ctx = ServerImpl.this.contexts.findContext(this.protocol, var10.getPath());
            if (this.ctx == null) {
               this.reject(404, var3, "No context found for request");
               return;
            }

            this.connection.setContext(this.ctx);
            if (this.ctx.getHandler() == null) {
               this.reject(500, var3, "No handler for context");
               return;
            }

            this.tx = new com.frojasg1.sun.net.httpserver.ExchangeImpl(var7, var10, var5, var14, this.connection);
            String var16 = var12.getFirst("Connection");
            Headers var17 = this.tx.getResponseHeaders();
            if (var16 != null && var16.equalsIgnoreCase("close")) {
               this.tx.close = true;
            }

            if (var11.equalsIgnoreCase("http/1.0")) {
               this.tx.http10 = true;
               if (var16 == null) {
                  this.tx.close = true;
                  var17.set("Connection", "close");
               } else if (var16.equalsIgnoreCase("keep-alive")) {
                  var17.set("Connection", "keep-alive");
                  int var18 = (int)(com.frojasg1.sun.net.httpserver.ServerConfig.getIdleInterval() / 1000L);
                  int var19 = com.frojasg1.sun.net.httpserver.ServerConfig.getMaxIdleConnections();
                  String var20 = "timeout=" + var18 + ", max=" + var19;
                  var17.set("Keep-Alive", var20);
               }
            }

            if (var1) {
               this.connection.setParameters(this.rawin, this.rawout, this.chan, var2, var4, ServerImpl.this.sslContext, this.protocol, this.ctx, this.rawin);
            }

            String var27 = var12.getFirst("Expect");
            if (var27 != null && var27.equalsIgnoreCase("100-continue")) {
               ServerImpl.this.logReply(100, var3, (String)null);
               this.sendReply(100, false, (String)null);
            }

            List var28 = this.ctx.getSystemFilters();
            List var29 = this.ctx.getFilters();
            Chain var21 = new Chain(var28, this.ctx.getHandler());
            Chain var22 = new Chain(var29, new ServerImpl.Exchange.LinkHandler(var21));
            this.tx.getRequestBody();
            this.tx.getResponseBody();
            if (ServerImpl.this.https) {
               var22.doFilter(new com.frojasg1.sun.net.httpserver.HttpsExchangeImpl(this.tx));
            } else {
               var22.doFilter(new com.frojasg1.sun.net.httpserver.HttpExchangeImpl(this.tx));
            }
         } catch (IOException var23) {
            ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (1)", var23);
            ServerImpl.this.closeConnection(this.connection);
         } catch (NumberFormatException var24) {
            this.reject(400, var3, "NumberFormatException thrown");
         } catch (URISyntaxException var25) {
            this.reject(400, var3, "URISyntaxException thrown");
         } catch (Exception var26) {
            ServerImpl.this.logger.log(Level.FINER, "ServerImpl.Exchange (2)", var26);
            ServerImpl.this.closeConnection(this.connection);
         }

      }

      void reject(int var1, String var2, String var3) {
         this.rejected = true;
         ServerImpl.this.logReply(var1, var2, var3);
         this.sendReply(var1, false, "<h1>" + var1 + com.frojasg1.sun.net.httpserver.Code.msg(var1) + "</h1>" + var3);
         ServerImpl.this.closeConnection(this.connection);
      }

      void sendReply(int var1, boolean var2, String var3) {
         try {
            StringBuilder var4 = new StringBuilder(512);
            var4.append("HTTP/1.1 ").append(var1).append(com.frojasg1.sun.net.httpserver.Code.msg(var1)).append("\r\n");
            if (var3 != null && var3.length() != 0) {
               var4.append("Content-Length: ").append(var3.length()).append("\r\n").append("Content-Type: text/html\r\n");
            } else {
               var4.append("Content-Length: 0\r\n");
               var3 = "";
            }

            if (var2) {
               var4.append("Connection: close\r\n");
            }

            var4.append("\r\n").append(var3);
            String var5 = var4.toString();
            byte[] var6 = var5.getBytes("ISO8859_1");
            this.rawout.write(var6);
            this.rawout.flush();
            if (var2) {
               ServerImpl.this.closeConnection(this.connection);
            }
         } catch (IOException var7) {
            ServerImpl.this.logger.log(Level.FINER, "ServerImpl.sendReply", var7);
            ServerImpl.this.closeConnection(this.connection);
         }

      }

      class LinkHandler implements HttpHandler {
         Chain nextChain;

         LinkHandler(Chain var2) {
            this.nextChain = var2;
         }

         public void handle(HttpExchange var1) throws IOException {
            this.nextChain.doFilter(var1);
         }
      }
   }

   class ServerTimerTask extends TimerTask {
      ServerTimerTask() {
      }

      public void run() {
         LinkedList var1 = new LinkedList();
         ServerImpl.this.time = System.currentTimeMillis();
         ServerImpl.this.ticks++;
         synchronized(ServerImpl.this.idleConnections) {
            Iterator var3 = ServerImpl.this.idleConnections.iterator();

            com.frojasg1.sun.net.httpserver.HttpConnection var4;
            while(var3.hasNext()) {
               var4 = (com.frojasg1.sun.net.httpserver.HttpConnection)var3.next();
               if (var4.time <= ServerImpl.this.time) {
                  var1.add(var4);
               }
            }

            var3 = var1.iterator();

            while(var3.hasNext()) {
               var4 = (com.frojasg1.sun.net.httpserver.HttpConnection)var3.next();
               ServerImpl.this.idleConnections.remove(var4);
               ServerImpl.this.allConnections.remove(var4);
               var4.close();
            }

         }
      }
   }

   class ServerTimerTask1 extends TimerTask {
      ServerTimerTask1() {
      }

      public void run() {
         LinkedList var1 = new LinkedList();
         ServerImpl.this.time = System.currentTimeMillis();
         Iterator var3;
         com.frojasg1.sun.net.httpserver.HttpConnection var4;
         synchronized(ServerImpl.this.reqConnections) {
            if (ServerImpl.MAX_REQ_TIME != -1L) {
               var3 = ServerImpl.this.reqConnections.iterator();

               while(var3.hasNext()) {
                  var4 = (com.frojasg1.sun.net.httpserver.HttpConnection)var3.next();
                  if (var4.creationTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_REQ_TIME <= ServerImpl.this.time) {
                     var1.add(var4);
                  }
               }

               var3 = var1.iterator();

               while(var3.hasNext()) {
                  var4 = (com.frojasg1.sun.net.httpserver.HttpConnection)var3.next();
                  ServerImpl.this.logger.log(Level.FINE, "closing: no request: " + var4);
                  ServerImpl.this.reqConnections.remove(var4);
                  ServerImpl.this.allConnections.remove(var4);
                  var4.close();
               }
            }
         }

         var1 = new LinkedList();
         synchronized(ServerImpl.this.rspConnections) {
            if (ServerImpl.MAX_RSP_TIME != -1L) {
               var3 = ServerImpl.this.rspConnections.iterator();

               while(var3.hasNext()) {
                  var4 = (com.frojasg1.sun.net.httpserver.HttpConnection)var3.next();
                  if (var4.rspStartedTime + ServerImpl.TIMER_MILLIS + ServerImpl.MAX_RSP_TIME <= ServerImpl.this.time) {
                     var1.add(var4);
                  }
               }

               var3 = var1.iterator();

               while(var3.hasNext()) {
                  var4 = (com.frojasg1.sun.net.httpserver.HttpConnection)var3.next();
                  ServerImpl.this.logger.log(Level.FINE, "closing: no response: " + var4);
                  ServerImpl.this.rspConnections.remove(var4);
                  ServerImpl.this.allConnections.remove(var4);
                  var4.close();
               }
            }

         }
      }
   }
}
