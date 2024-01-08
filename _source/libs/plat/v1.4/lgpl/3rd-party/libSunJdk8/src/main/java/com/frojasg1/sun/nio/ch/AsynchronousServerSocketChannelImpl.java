package com.frojasg1.sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketOption;
import java.net.StandardSocketOptions;
import java.nio.channels.AlreadyBoundException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.frojasg1.sun.net.NetHooks;
import com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl;
import com.frojasg1.sun.nio.ch.Cancellable;
import com.frojasg1.sun.nio.ch.Groupable;
import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.PendingFuture;

abstract class AsynchronousServerSocketChannelImpl extends AsynchronousServerSocketChannel implements com.frojasg1.sun.nio.ch.Cancellable, com.frojasg1.sun.nio.ch.Groupable {
   protected final FileDescriptor fd = com.frojasg1.sun.nio.ch.Net.serverSocket(true);
   protected volatile InetSocketAddress localAddress = null;
   private final Object stateLock = new Object();
   private ReadWriteLock closeLock = new ReentrantReadWriteLock();
   private volatile boolean open = true;
   private volatile boolean acceptKilled;
   private boolean isReuseAddress;

   AsynchronousServerSocketChannelImpl(com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl var1) {
      super(var1.provider());
   }

   public final boolean isOpen() {
      return this.open;
   }

   final void begin() throws IOException {
      this.closeLock.readLock().lock();
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      }
   }

   final void end() {
      this.closeLock.readLock().unlock();
   }

   abstract void implClose() throws IOException;

   public final void close() throws IOException {
      this.closeLock.writeLock().lock();

      try {
         if (!this.open) {
            return;
         }

         this.open = false;
      } finally {
         this.closeLock.writeLock().unlock();
      }

      this.implClose();
   }

   abstract Future<AsynchronousSocketChannel> implAccept(Object var1, CompletionHandler<AsynchronousSocketChannel, Object> var2);

   public final Future<AsynchronousSocketChannel> accept() {
      return this.implAccept((Object)null, (CompletionHandler)null);
   }

   public final <A> void accept(A var1, CompletionHandler<AsynchronousSocketChannel, ? super A> var2) {
      if (var2 == null) {
         throw new NullPointerException("'handler' is null");
      } else {
         this.implAccept(var1, null /*var2*/);
      }
   }

   final boolean isAcceptKilled() {
      return this.acceptKilled;
   }

   public final void onCancel(com.frojasg1.sun.nio.ch.PendingFuture<?, ?> var1) {
      this.acceptKilled = true;
   }

   public final AsynchronousServerSocketChannel bind(SocketAddress var1, int var2) throws IOException {
      InetSocketAddress var3 = var1 == null ? new InetSocketAddress(0) : com.frojasg1.sun.nio.ch.Net.checkAddress(var1);
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         var4.checkListen(var3.getPort());
      }

      try {
         this.begin();
         synchronized(this.stateLock) {
            if (this.localAddress != null) {
               throw new AlreadyBoundException();
            }

            NetHooks.beforeTcpBind(this.fd, var3.getAddress(), var3.getPort());
            com.frojasg1.sun.nio.ch.Net.bind(this.fd, var3.getAddress(), var3.getPort());
            com.frojasg1.sun.nio.ch.Net.listen(this.fd, var2 < 1 ? 50 : var2);
            this.localAddress = com.frojasg1.sun.nio.ch.Net.localAddress(this.fd);
         }
      } finally {
         this.end();
      }

      return this;
   }

   public final SocketAddress getLocalAddress() throws IOException {
      if (!this.isOpen()) {
         throw new ClosedChannelException();
      } else {
         return com.frojasg1.sun.nio.ch.Net.getRevealedLocalAddress(this.localAddress);
      }
   }

   public final <T> AsynchronousServerSocketChannel setOption(SocketOption<T> var1, T var2) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         AsynchronousServerSocketChannelImpl var3;
         try {
            this.begin();
            if (var1 == StandardSocketOptions.SO_REUSEADDR && com.frojasg1.sun.nio.ch.Net.useExclusiveBind()) {
               this.isReuseAddress = (Boolean)var2;
            } else {
               com.frojasg1.sun.nio.ch.Net.setSocketOption(this.fd, com.frojasg1.sun.nio.ch.Net.UNSPEC, var1, var2);
            }

            var3 = this;
         } finally {
            this.end();
         }

         return var3;
      }
   }

   public final <T> T getOption(SocketOption<T> var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!this.supportedOptions().contains(var1)) {
         throw new UnsupportedOperationException("'" + var1 + "' not supported");
      } else {
         Object var2;
         try {
            this.begin();
            if (var1 == StandardSocketOptions.SO_REUSEADDR && com.frojasg1.sun.nio.ch.Net.useExclusiveBind()) {
               Boolean var6 = this.isReuseAddress;
               return (T) var6;
            }

            var2 = com.frojasg1.sun.nio.ch.Net.getSocketOption(this.fd, com.frojasg1.sun.nio.ch.Net.UNSPEC, var1);
         } finally {
            this.end();
         }

         return (T) var2;
      }
   }

   public final Set<SocketOption<?>> supportedOptions() {
      return AsynchronousServerSocketChannelImpl.DefaultOptionsHolder.defaultOptions;
   }

   public final String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append(this.getClass().getName());
      var1.append('[');
      if (!this.isOpen()) {
         var1.append("closed");
      } else if (this.localAddress == null) {
         var1.append("unbound");
      } else {
         var1.append(Net.getRevealedLocalAddressAsString(this.localAddress));
      }

      var1.append(']');
      return var1.toString();
   }

   private static class DefaultOptionsHolder {
      static final Set<SocketOption<?>> defaultOptions = defaultOptions();

      private DefaultOptionsHolder() {
      }

      private static Set<SocketOption<?>> defaultOptions() {
         HashSet var0 = new HashSet(2);
         var0.add(StandardSocketOptions.SO_RCVBUF);
         var0.add(StandardSocketOptions.SO_REUSEADDR);
         return Collections.unmodifiableSet(var0);
      }
   }
}
