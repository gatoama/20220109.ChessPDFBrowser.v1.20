package com.frojasg1.sun.nio.ch;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.InterruptedByTimeoutException;
import java.nio.channels.ShutdownChannelGroupException;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl;
import com.frojasg1.sun.nio.ch.AsynchronousSocketChannelImpl;
import com.frojasg1.sun.nio.ch.CompletedFuture;
import com.frojasg1.sun.nio.ch.DirectBuffer;
import com.frojasg1.sun.nio.ch.IOUtil;
import com.frojasg1.sun.nio.ch.Invoker;
import com.frojasg1.sun.nio.ch.Iocp;
import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.PendingFuture;
import com.frojasg1.sun.nio.ch.PendingIoCache;
import com.frojasg1.sun.nio.ch.Util;

class WindowsAsynchronousSocketChannelImpl extends com.frojasg1.sun.nio.ch.AsynchronousSocketChannelImpl implements com.frojasg1.sun.nio.ch.Iocp.OverlappedChannel {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static int addressSize;
   private static final int SIZEOF_WSABUF;
   private static final int OFFSETOF_LEN = 0;
   private static final int OFFSETOF_BUF;
   private static final int MAX_WSABUF = 16;
   private static final int SIZEOF_WSABUFARRAY;
   final long handle;
   private final com.frojasg1.sun.nio.ch.Iocp iocp;
   private final int completionKey;
   private final com.frojasg1.sun.nio.ch.PendingIoCache ioCache;
   private final long readBufferArray;
   private final long writeBufferArray;

   private static int dependsArch(int var0, int var1) {
      return addressSize == 4 ? var0 : var1;
   }

   WindowsAsynchronousSocketChannelImpl(com.frojasg1.sun.nio.ch.Iocp var1, boolean var2) throws IOException {
      super(var1);
      long var3 = (long) com.frojasg1.sun.nio.ch.IOUtil.fdVal(this.fd);
      int var5 = 0;

      try {
         var5 = var1.associate(this, var3);
      } catch (ShutdownChannelGroupException var7) {
         if (var2) {
            closesocket0(var3);
            throw var7;
         }
      } catch (IOException var8) {
         closesocket0(var3);
         throw var8;
      }

      this.handle = var3;
      this.iocp = var1;
      this.completionKey = var5;
      this.ioCache = new com.frojasg1.sun.nio.ch.PendingIoCache();
      this.readBufferArray = unsafe.allocateMemory((long)SIZEOF_WSABUFARRAY);
      this.writeBufferArray = unsafe.allocateMemory((long)SIZEOF_WSABUFARRAY);
   }

   WindowsAsynchronousSocketChannelImpl(com.frojasg1.sun.nio.ch.Iocp var1) throws IOException {
      this(var1, true);
   }

   public com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl group() {
      return this.iocp;
   }

   public <V, A> com.frojasg1.sun.nio.ch.PendingFuture<V, A> getByOverlapped(long var1) {
      return this.ioCache.remove(var1);
   }

   long handle() {
      return this.handle;
   }

   void setConnected(InetSocketAddress var1, InetSocketAddress var2) {
      synchronized(this.stateLock) {
         this.state = 2;
         this.localAddress = var1;
         this.remoteAddress = var2;
      }
   }

   void implClose() throws IOException {
      closesocket0(this.handle);
      this.ioCache.close();
      unsafe.freeMemory(this.readBufferArray);
      unsafe.freeMemory(this.writeBufferArray);
      if (this.completionKey != 0) {
         this.iocp.disassociate(this.completionKey);
      }

   }

   public void onCancel(com.frojasg1.sun.nio.ch.PendingFuture<?, ?> var1) {
      if (var1.getContext() instanceof WindowsAsynchronousSocketChannelImpl.ConnectTask) {
         this.killConnect();
      }

      if (var1.getContext() instanceof WindowsAsynchronousSocketChannelImpl.ReadTask) {
         this.killReading();
      }

      if (var1.getContext() instanceof WindowsAsynchronousSocketChannelImpl.WriteTask) {
         this.killWriting();
      }

   }

   private void doPrivilegedBind(final SocketAddress var1) throws IOException {
      try {
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws IOException {
               WindowsAsynchronousSocketChannelImpl.this.bind(var1);
               return null;
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getException();
      }
   }

   <A> Future<Void> implConnect(SocketAddress var1, A var2, CompletionHandler<Void, ? super A> var3) {
      if (!this.isOpen()) {
         ClosedChannelException var13 = new ClosedChannelException();
         if (var3 == null) {
            return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var13);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this, (CompletionHandler) var3, var2, (Object)null, var13);
            return null;
         }
      } else {
         InetSocketAddress var4 = com.frojasg1.sun.nio.ch.Net.checkAddress(var1);
         SecurityManager var5 = System.getSecurityManager();
         if (var5 != null) {
            var5.checkConnect(var4.getAddress().getHostAddress(), var4.getPort());
         }

         IOException var6 = null;
         synchronized(this.stateLock) {
            if (this.state == 2) {
               throw new AlreadyConnectedException();
            }

            if (this.state == 1) {
               throw new ConnectionPendingException();
            }

            if (this.localAddress == null) {
               try {
                  InetSocketAddress var8 = new InetSocketAddress(0);
                  if (var5 == null) {
                     this.bind(var8);
                  } else {
                     this.doPrivilegedBind(var8);
                  }
               } catch (IOException var11) {
                  var6 = var11;
               }
            }

            if (var6 == null) {
               this.state = 1;
            }
         }

         if (var6 != null) {
            try {
               this.close();
            } catch (IOException var10) {
            }

            if (var3 == null) {
               return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var6);
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invoke(this, (CompletionHandler) var3, var2, (Object)null, var6);
               return null;
            }
         } else {
            com.frojasg1.sun.nio.ch.PendingFuture var7 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var3, var2);
            WindowsAsynchronousSocketChannelImpl.ConnectTask var14 = new WindowsAsynchronousSocketChannelImpl.ConnectTask(var4, var7);
            var7.setContext(var14);
            if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
               var14.run();
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var14);
            }

            return var7;
         }
      }
   }

   <V extends Number, A> Future<V> implRead(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      com.frojasg1.sun.nio.ch.PendingFuture var9 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var8, var7);
      ByteBuffer[] var10;
      if (var1) {
         var10 = var3;
      } else {
         var10 = new ByteBuffer[]{var2};
      }

      final WindowsAsynchronousSocketChannelImpl.ReadTask var11 = new WindowsAsynchronousSocketChannelImpl.ReadTask(var10, var1, var9);
      var9.setContext(var11);
      if (var4 > 0L) {
         Future var12 = this.iocp.schedule(new Runnable() {
            public void run() {
               var11.timeout();
            }
         }, var4, var6);
         var9.setTimeoutTask(var12);
      }

      if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
         var11.run();
      } else {
         com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var11);
      }

      return var9;
   }

   <V extends Number, A> Future<V> implWrite(boolean var1, ByteBuffer var2, ByteBuffer[] var3, long var4, TimeUnit var6, A var7, CompletionHandler<V, ? super A> var8) {
      com.frojasg1.sun.nio.ch.PendingFuture var9 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var8, var7);
      ByteBuffer[] var10;
      if (var1) {
         var10 = var3;
      } else {
         var10 = new ByteBuffer[]{var2};
      }

      final WindowsAsynchronousSocketChannelImpl.WriteTask var11 = new WindowsAsynchronousSocketChannelImpl.WriteTask(var10, var1, var9);
      var9.setContext(var11);
      if (var4 > 0L) {
         Future var12 = this.iocp.schedule(new Runnable() {
            public void run() {
               var11.timeout();
            }
         }, var4, var6);
         var9.setTimeoutTask(var12);
      }

      if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
         var11.run();
      } else {
         com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var11);
      }

      return var9;
   }

   private static native void initIDs();

   private static native int connect0(long var0, boolean var2, InetAddress var3, int var4, long var5) throws IOException;

   private static native void updateConnectContext(long var0) throws IOException;

   private static native int read0(long var0, int var2, long var3, long var5) throws IOException;

   private static native int write0(long var0, int var2, long var3, long var5) throws IOException;

   private static native void shutdown0(long var0, int var2) throws IOException;

   private static native void closesocket0(long var0) throws IOException;

   static {
      addressSize = unsafe.addressSize();
      SIZEOF_WSABUF = dependsArch(8, 16);
      OFFSETOF_BUF = dependsArch(4, 8);
      SIZEOF_WSABUFARRAY = 16 * SIZEOF_WSABUF;
      IOUtil.load();
      initIDs();
   }

   private class ConnectTask<A> implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final InetSocketAddress remote;
      private final com.frojasg1.sun.nio.ch.PendingFuture<Void, A> result;

      ConnectTask(InetSocketAddress var2, com.frojasg1.sun.nio.ch.PendingFuture<Void, A> var3) {
         this.remote = var2;
         this.result = var3;
      }

      private void closeChannel() {
         try {
            WindowsAsynchronousSocketChannelImpl.this.close();
         } catch (IOException var2) {
         }

      }

      private IOException toIOException(Throwable var1) {
         if (var1 instanceof IOException) {
            if (var1 instanceof ClosedChannelException) {
               var1 = new AsynchronousCloseException();
            }

            return (IOException)var1;
         } else {
            return new IOException((Throwable)var1);
         }
      }

      private void afterConnect() throws IOException {
         WindowsAsynchronousSocketChannelImpl.updateConnectContext(WindowsAsynchronousSocketChannelImpl.this.handle);
         synchronized(WindowsAsynchronousSocketChannelImpl.this.stateLock) {
            WindowsAsynchronousSocketChannelImpl.this.state = 2;
            WindowsAsynchronousSocketChannelImpl.this.remoteAddress = this.remote;
         }
      }

      public void run() {
         long var1 = 0L;
         Throwable var3 = null;

         label87: {
            try {
               WindowsAsynchronousSocketChannelImpl.this.begin();
               synchronized(this.result) {
                  var1 = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
                  int var5 = WindowsAsynchronousSocketChannelImpl.connect0(WindowsAsynchronousSocketChannelImpl.this.handle, Net.isIPv6Available(), this.remote.getAddress(), this.remote.getPort(), var1);
                  if (var5 != -2) {
                     this.afterConnect();
                     this.result.setResult(null);
                     break label87;
                  }
               }
            } catch (Throwable var12) {
               if (var1 != 0L) {
                  WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(var1);
               }

               var3 = var12;
               break label87;
            } finally {
               WindowsAsynchronousSocketChannelImpl.this.end();
            }

            return;
         }

         if (var3 != null) {
            this.closeChannel();
            this.result.setFailure(this.toIOException(var3));
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      public void completed(int var1, boolean var2) {
         Throwable var3 = null;

         try {
            WindowsAsynchronousSocketChannelImpl.this.begin();
            this.afterConnect();
            this.result.setResult(null);
         } catch (Throwable var8) {
            var3 = var8;
         } finally {
            WindowsAsynchronousSocketChannelImpl.this.end();
         }

         if (var3 != null) {
            this.closeChannel();
            this.result.setFailure(this.toIOException(var3));
         }

         if (var2) {
            com.frojasg1.sun.nio.ch.Invoker.invokeUnchecked(this.result);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }

      public void failed(int var1, IOException var2) {
         if (WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
            this.closeChannel();
            this.result.setFailure(var2);
         } else {
            this.result.setFailure(new AsynchronousCloseException());
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }
   }

   private class ReadTask<V, A> implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final ByteBuffer[] bufs;
      private final int numBufs;
      private final boolean scatteringRead;
      private final com.frojasg1.sun.nio.ch.PendingFuture<V, A> result;
      private ByteBuffer[] shadow;

      ReadTask(ByteBuffer[] var2, boolean var3, com.frojasg1.sun.nio.ch.PendingFuture<V, A> var4) {
         this.bufs = var2;
         this.numBufs = var2.length > 16 ? 16 : var2.length;
         this.scatteringRead = var3;
         this.result = var4;
      }

      void prepareBuffers() {
         this.shadow = new ByteBuffer[this.numBufs];
         long var1 = WindowsAsynchronousSocketChannelImpl.this.readBufferArray;

         for(int var3 = 0; var3 < this.numBufs; ++var3) {
            ByteBuffer var4 = this.bufs[var3];
            int var5 = var4.position();
            int var6 = var4.limit();

            assert var5 <= var6;

            int var7 = var5 <= var6 ? var6 - var5 : 0;
            long var8;
            if (!(var4 instanceof com.frojasg1.sun.nio.ch.DirectBuffer)) {
               ByteBuffer var10 = com.frojasg1.sun.nio.ch.Util.getTemporaryDirectBuffer(var7);
               this.shadow[var3] = var10;
               var8 = ((com.frojasg1.sun.nio.ch.DirectBuffer)var10).address();
            } else {
               this.shadow[var3] = var4;
               var8 = ((com.frojasg1.sun.nio.ch.DirectBuffer)var4).address() + (long)var5;
            }

            WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(var1 + (long)WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, var8);
            WindowsAsynchronousSocketChannelImpl.unsafe.putInt(var1 + 0L, var7);
            var1 += (long)WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
         }

      }

      void updateBuffers(int var1) {
         int var2;
         for(var2 = 0; var2 < this.numBufs; ++var2) {
            ByteBuffer var3 = this.shadow[var2];
            int var4 = var3.position();
            int var5 = var3.remaining();
            int var6;
            if (var1 < var5) {
               if (var1 > 0) {
                  assert (long)(var4 + var1) < 2147483647L;

                  var6 = var4 + var1;

                  try {
                     var3.position(var6);
                  } catch (IllegalArgumentException var9) {
                  }
               }
               break;
            }

            var1 -= var5;
            var6 = var4 + var5;

            try {
               var3.position(var6);
            } catch (IllegalArgumentException var10) {
            }
         }

         for(var2 = 0; var2 < this.numBufs; ++var2) {
            if (!(this.bufs[var2] instanceof com.frojasg1.sun.nio.ch.DirectBuffer)) {
               this.shadow[var2].flip();

               try {
                  this.bufs[var2].put(this.shadow[var2]);
               } catch (BufferOverflowException var8) {
               }
            }
         }

      }

      void releaseBuffers() {
         for(int var1 = 0; var1 < this.numBufs; ++var1) {
            if (!(this.bufs[var1] instanceof com.frojasg1.sun.nio.ch.DirectBuffer)) {
               com.frojasg1.sun.nio.ch.Util.releaseTemporaryDirectBuffer(this.shadow[var1]);
            }
         }

      }

      public void run() {
         long var1 = 0L;
         boolean var3 = false;
         boolean var4 = false;

         label151: {
            try {
               WindowsAsynchronousSocketChannelImpl.this.begin();
               this.prepareBuffers();
               var3 = true;
               var1 = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
               int var11 = WindowsAsynchronousSocketChannelImpl.read0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.readBufferArray, var1);
               if (var11 != -2) {
                  if (var11 != -1) {
                     throw new InternalError("Read completed immediately");
                  }

                  WindowsAsynchronousSocketChannelImpl.this.enableReading();
                  if (this.scatteringRead) {
//                     this.result.setResult(-1L);
                  } else {
//                     this.result.setResult(-1);
                  }
                  break label151;
               }

               var4 = true;
            } catch (Throwable var9) {
               Object var5 = var9;
               WindowsAsynchronousSocketChannelImpl.this.enableReading();
               if (var9 instanceof ClosedChannelException) {
                  var5 = new AsynchronousCloseException();
               }

               if (!(var5 instanceof IOException)) {
                  var5 = new IOException((Throwable)var5);
               }

               this.result.setFailure((Throwable)var5);
               break label151;
            } finally {
               if (!var4) {
                  if (var1 != 0L) {
                     WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(var1);
                  }

                  if (var3) {
                     this.releaseBuffers();
                  }
               }

               WindowsAsynchronousSocketChannelImpl.this.end();
            }

            return;
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      public void completed(int var1, boolean var2) {
         if (var1 == 0) {
            var1 = -1;
         } else {
            this.updateBuffers(var1);
         }

         this.releaseBuffers();
         synchronized(this.result) {
            if (this.result.isDone()) {
               return;
            }

            WindowsAsynchronousSocketChannelImpl.this.enableReading();
            if (this.scatteringRead) {
//               this.result.setResult((long)var1);
            } else {
//               this.result.setResult(var1);
            }
         }

         if (var2) {
            com.frojasg1.sun.nio.ch.Invoker.invokeUnchecked(this.result);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }

      public void failed(int var1, IOException var2) {
         this.releaseBuffers();
         if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
            var2 = new AsynchronousCloseException();
         }

         synchronized(this.result) {
            if (this.result.isDone()) {
               return;
            }

            WindowsAsynchronousSocketChannelImpl.this.enableReading();
            this.result.setFailure((Throwable)var2);
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      void timeout() {
         synchronized(this.result) {
            if (this.result.isDone()) {
               return;
            }

            WindowsAsynchronousSocketChannelImpl.this.enableReading(true);
            this.result.setFailure(new InterruptedByTimeoutException());
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }
   }

   private class WriteTask<V, A> implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final ByteBuffer[] bufs;
      private final int numBufs;
      private final boolean gatheringWrite;
      private final com.frojasg1.sun.nio.ch.PendingFuture<V, A> result;
      private ByteBuffer[] shadow;

      WriteTask(ByteBuffer[] var2, boolean var3, com.frojasg1.sun.nio.ch.PendingFuture<V, A> var4) {
         this.bufs = var2;
         this.numBufs = var2.length > 16 ? 16 : var2.length;
         this.gatheringWrite = var3;
         this.result = var4;
      }

      void prepareBuffers() {
         this.shadow = new ByteBuffer[this.numBufs];
         long var1 = WindowsAsynchronousSocketChannelImpl.this.writeBufferArray;

         for(int var3 = 0; var3 < this.numBufs; ++var3) {
            ByteBuffer var4 = this.bufs[var3];
            int var5 = var4.position();
            int var6 = var4.limit();

            assert var5 <= var6;

            int var7 = var5 <= var6 ? var6 - var5 : 0;
            long var8;
            if (!(var4 instanceof com.frojasg1.sun.nio.ch.DirectBuffer)) {
               ByteBuffer var10 = com.frojasg1.sun.nio.ch.Util.getTemporaryDirectBuffer(var7);
               var10.put(var4);
               var10.flip();
               var4.position(var5);
               this.shadow[var3] = var10;
               var8 = ((com.frojasg1.sun.nio.ch.DirectBuffer)var10).address();
            } else {
               this.shadow[var3] = var4;
               var8 = ((com.frojasg1.sun.nio.ch.DirectBuffer)var4).address() + (long)var5;
            }

            WindowsAsynchronousSocketChannelImpl.unsafe.putAddress(var1 + (long)WindowsAsynchronousSocketChannelImpl.OFFSETOF_BUF, var8);
            WindowsAsynchronousSocketChannelImpl.unsafe.putInt(var1 + 0L, var7);
            var1 += (long)WindowsAsynchronousSocketChannelImpl.SIZEOF_WSABUF;
         }

      }

      void updateBuffers(int var1) {
         for(int var2 = 0; var2 < this.numBufs; ++var2) {
            ByteBuffer var3 = this.bufs[var2];
            int var4 = var3.position();
            int var5 = var3.limit();
            int var6 = var4 <= var5 ? var5 - var4 : var5;
            int var7;
            if (var1 < var6) {
               if (var1 > 0) {
                  assert (long)(var4 + var1) < 2147483647L;

                  var7 = var4 + var1;

                  try {
                     var3.position(var7);
                  } catch (IllegalArgumentException var9) {
                  }
               }
               break;
            }

            var1 -= var6;
            var7 = var4 + var6;

            try {
               var3.position(var7);
            } catch (IllegalArgumentException var10) {
            }
         }

      }

      void releaseBuffers() {
         for(int var1 = 0; var1 < this.numBufs; ++var1) {
            if (!(this.bufs[var1] instanceof DirectBuffer)) {
               Util.releaseTemporaryDirectBuffer(this.shadow[var1]);
            }
         }

      }

      public void run() {
         long var1 = 0L;
         boolean var3 = false;
         boolean var4 = false;
         boolean var5 = false;

         try {
            Object var6;
            try {
               WindowsAsynchronousSocketChannelImpl.this.begin();
               this.prepareBuffers();
               var3 = true;
               var1 = WindowsAsynchronousSocketChannelImpl.this.ioCache.add(this.result);
               int var12 = WindowsAsynchronousSocketChannelImpl.write0(WindowsAsynchronousSocketChannelImpl.this.handle, this.numBufs, WindowsAsynchronousSocketChannelImpl.this.writeBufferArray, var1);
               if (var12 != -2) {
                  if (var12 == -1) {
                     var5 = true;
                     throw new ClosedChannelException();
                  }

                  throw new InternalError("Write completed immediately");
               }

               var4 = true;
               return;
            } catch (Throwable var10) {
               var6 = var10;
               WindowsAsynchronousSocketChannelImpl.this.enableWriting();
               if (!var5 && var10 instanceof ClosedChannelException) {
                  var6 = new AsynchronousCloseException();
               }
            }

            if (!(var6 instanceof IOException)) {
               var6 = new IOException((Throwable)var6);
            }

            this.result.setFailure((Throwable)var6);
         } finally {
            if (!var4) {
               if (var1 != 0L) {
                  WindowsAsynchronousSocketChannelImpl.this.ioCache.remove(var1);
               }

               if (var3) {
                  this.releaseBuffers();
               }
            }

            WindowsAsynchronousSocketChannelImpl.this.end();
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      public void completed(int var1, boolean var2) {
         this.updateBuffers(var1);
         this.releaseBuffers();
         synchronized(this.result) {
            if (this.result.isDone()) {
               return;
            }

            WindowsAsynchronousSocketChannelImpl.this.enableWriting();
            if (this.gatheringWrite) {
//               this.result.setResult((long)var1);
            } else {
//               this.result.setResult(var1);
            }
         }

         if (var2) {
            com.frojasg1.sun.nio.ch.Invoker.invokeUnchecked(this.result);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }

      public void failed(int var1, IOException var2) {
         this.releaseBuffers();
         if (!WindowsAsynchronousSocketChannelImpl.this.isOpen()) {
            var2 = new AsynchronousCloseException();
         }

         synchronized(this.result) {
            if (this.result.isDone()) {
               return;
            }

            WindowsAsynchronousSocketChannelImpl.this.enableWriting();
            this.result.setFailure((Throwable)var2);
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      void timeout() {
         synchronized(this.result) {
            if (this.result.isDone()) {
               return;
            }

            WindowsAsynchronousSocketChannelImpl.this.enableWriting(true);
            this.result.setFailure(new InterruptedByTimeoutException());
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }
   }
}
