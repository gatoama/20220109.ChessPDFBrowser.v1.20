package com.frojasg1.sun.nio.ch;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileLock;
import java.nio.channels.NonReadableChannelException;
import java.nio.channels.NonWritableChannelException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.Future;
import com.frojasg1.sun.misc.JavaIOFileDescriptorAccess;
import com.frojasg1.sun.misc.SharedSecrets;
import com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl;
import com.frojasg1.sun.nio.ch.AsynchronousFileChannelImpl;
import com.frojasg1.sun.nio.ch.CompletedFuture;
import com.frojasg1.sun.nio.ch.DirectBuffer;
import com.frojasg1.sun.nio.ch.FileDispatcher;
import com.frojasg1.sun.nio.ch.FileDispatcherImpl;
import com.frojasg1.sun.nio.ch.FileLockImpl;
import com.frojasg1.sun.nio.ch.Groupable;
import com.frojasg1.sun.nio.ch.IOUtil;
import com.frojasg1.sun.nio.ch.Invoker;
import com.frojasg1.sun.nio.ch.Iocp;
import com.frojasg1.sun.nio.ch.PendingFuture;
import com.frojasg1.sun.nio.ch.PendingIoCache;
import com.frojasg1.sun.nio.ch.ThreadPool;
import com.frojasg1.sun.nio.ch.Util;

public class WindowsAsynchronousFileChannelImpl extends com.frojasg1.sun.nio.ch.AsynchronousFileChannelImpl implements com.frojasg1.sun.nio.ch.Iocp.OverlappedChannel, com.frojasg1.sun.nio.ch.Groupable {
   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
   private static final int ERROR_HANDLE_EOF = 38;
   private static final com.frojasg1.sun.nio.ch.FileDispatcher nd = new com.frojasg1.sun.nio.ch.FileDispatcherImpl();
   private final long handle;
   private final int completionKey;
   private final com.frojasg1.sun.nio.ch.Iocp iocp;
   private final boolean isDefaultIocp;
   private final com.frojasg1.sun.nio.ch.PendingIoCache ioCache;
   static final int NO_LOCK = -1;
   static final int LOCKED = 0;

   private WindowsAsynchronousFileChannelImpl(FileDescriptor var1, boolean var2, boolean var3, com.frojasg1.sun.nio.ch.Iocp var4, boolean var5) throws IOException {
      super(var1, var2, var3, var4.executor());
      this.handle = fdAccess.getHandle(var1);
      this.iocp = var4;
      this.isDefaultIocp = var5;
      this.ioCache = new com.frojasg1.sun.nio.ch.PendingIoCache();
      this.completionKey = var4.associate(this, this.handle);
   }

   public static AsynchronousFileChannel open(FileDescriptor var0, boolean var1, boolean var2, com.frojasg1.sun.nio.ch.ThreadPool var3) throws IOException {
      com.frojasg1.sun.nio.ch.Iocp var4;
      boolean var5;
      if (var3 == null) {
         var4 = WindowsAsynchronousFileChannelImpl.DefaultIocpHolder.defaultIocp;
         var5 = true;
      } else {
         var4 = (new com.frojasg1.sun.nio.ch.Iocp((AsynchronousChannelProvider)null, var3)).start();
         var5 = false;
      }

      try {
         return new WindowsAsynchronousFileChannelImpl(var0, var1, var2, var4, var5);
      } catch (IOException var7) {
         if (!var5) {
            var4.implClose();
         }

         throw var7;
      }
   }

   public <V, A> com.frojasg1.sun.nio.ch.PendingFuture<V, A> getByOverlapped(long var1) {
      return this.ioCache.remove(var1);
   }

   public void close() throws IOException {
      this.closeLock.writeLock().lock();

      label47: {
         try {
            if (!this.closed) {
               this.closed = true;
               break label47;
            }
         } finally {
            this.closeLock.writeLock().unlock();
         }

         return;
      }

      this.invalidateAllLocks();
      close0(this.handle);
      this.ioCache.close();
      this.iocp.disassociate(this.completionKey);
      if (!this.isDefaultIocp) {
         this.iocp.detachFromThreadPool();
      }

   }

   public com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl group() {
      return this.iocp;
   }

   private static IOException toIOException(Throwable var0) {
      if (var0 instanceof IOException) {
         if (var0 instanceof ClosedChannelException) {
            var0 = new AsynchronousCloseException();
         }

         return (IOException)var0;
      } else {
         return new IOException((Throwable)var0);
      }
   }

   public long size() throws IOException {
      long var1;
      try {
         this.begin();
         var1 = nd.size(this.fdObj);
      } finally {
         this.end();
      }

      return var1;
   }

   public AsynchronousFileChannel truncate(long var1) throws IOException {
      if (var1 < 0L) {
         throw new IllegalArgumentException("Negative size");
      } else if (!this.writing) {
         throw new NonWritableChannelException();
      } else {
         try {
            this.begin();
            if (var1 > nd.size(this.fdObj)) {
               WindowsAsynchronousFileChannelImpl var3 = this;
               return var3;
            }

            nd.truncate(this.fdObj, var1);
         } finally {
            this.end();
         }

         return this;
      }
   }

   public void force(boolean var1) throws IOException {
      try {
         this.begin();
         nd.force(this.fdObj, var1);
      } finally {
         this.end();
      }

   }

   <A> Future<FileLock> implLock(long var1, long var3, boolean var5, A var6, CompletionHandler<FileLock, ? super A> var7) {
      if (var5 && !this.reading) {
         throw new NonReadableChannelException();
      } else if (!var5 && !this.writing) {
         throw new NonWritableChannelException();
      } else {
         com.frojasg1.sun.nio.ch.FileLockImpl var8 = this.addToFileLockTable(var1, var3, var5);
         if (var8 == null) {
            ClosedChannelException var15 = new ClosedChannelException();
            if (var7 == null) {
               return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var15);
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invoke(this, (CompletionHandler) var7, var6, (Object)null, var15);
               return null;
            }
         } else {
            com.frojasg1.sun.nio.ch.PendingFuture var9 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var7, var6);
            WindowsAsynchronousFileChannelImpl.LockTask var10 = new WindowsAsynchronousFileChannelImpl.LockTask(var1, var8, var9);
            var9.setContext(var10);
            if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
               var10.run();
            } else {
               boolean var11 = false;

               try {
                  com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var10);
                  var11 = true;
               } finally {
                  if (!var11) {
                     this.removeFromFileLockTable(var8);
                  }

               }
            }

            return var9;
         }
      }
   }

   public FileLock tryLock(long var1, long var3, boolean var5) throws IOException {
      if (var5 && !this.reading) {
         throw new NonReadableChannelException();
      } else if (!var5 && !this.writing) {
         throw new NonWritableChannelException();
      } else {
         com.frojasg1.sun.nio.ch.FileLockImpl var6 = this.addToFileLockTable(var1, var3, var5);
         if (var6 == null) {
            throw new ClosedChannelException();
         } else {
            boolean var7 = false;

            com.frojasg1.sun.nio.ch.FileLockImpl var9;
            try {
               this.begin();
               int var8 = nd.lock(this.fdObj, false, var1, var3, var5);
               if (var8 == -1) {
                  var9 = null;
                  return var9;
               }

               var7 = true;
               var9 = var6;
            } finally {
               if (!var7) {
                  this.removeFromFileLockTable(var6);
               }

               this.end();
            }

            return var9;
         }
      }
   }

   protected void implRelease(com.frojasg1.sun.nio.ch.FileLockImpl var1) throws IOException {
      nd.release(this.fdObj, var1.position(), var1.size());
   }

   <A> Future<Integer> implRead(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5) {
      if (!this.reading) {
         throw new NonReadableChannelException();
      } else if (var2 < 0L) {
         throw new IllegalArgumentException("Negative position");
      } else if (var1.isReadOnly()) {
         throw new IllegalArgumentException("Read-only buffer");
      } else if (!this.isOpen()) {
         ClosedChannelException var11 = new ClosedChannelException();
         if (var5 == null) {
            return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var11);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this, (CompletionHandler) var5, var4, (Object)null, var11);
            return null;
         }
      } else {
         int var6 = var1.position();
         int var7 = var1.limit();

         assert var6 <= var7;

         int var8 = var6 <= var7 ? var7 - var6 : 0;
         if (var8 == 0) {
            if (var5 == null) {
               return com.frojasg1.sun.nio.ch.CompletedFuture.withResult(0);
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invoke(this, var5, var4, 0, (Throwable)null);
               return null;
            }
         } else {
            com.frojasg1.sun.nio.ch.PendingFuture var9 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var5, var4);
            WindowsAsynchronousFileChannelImpl.ReadTask var10 = new WindowsAsynchronousFileChannelImpl.ReadTask(var1, var6, var8, var2, var9);
            var9.setContext(var10);
            if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
               var10.run();
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var10);
            }

            return var9;
         }
      }
   }

   <A> Future<Integer> implWrite(ByteBuffer var1, long var2, A var4, CompletionHandler<Integer, ? super A> var5) {
      if (!this.writing) {
         throw new NonWritableChannelException();
      } else if (var2 < 0L) {
         throw new IllegalArgumentException("Negative position");
      } else if (!this.isOpen()) {
         ClosedChannelException var11 = new ClosedChannelException();
         if (var5 == null) {
            return com.frojasg1.sun.nio.ch.CompletedFuture.withFailure(var11);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this, (CompletionHandler) var5, var4, (Object)null, var11);
            return null;
         }
      } else {
         int var6 = var1.position();
         int var7 = var1.limit();

         assert var6 <= var7;

         int var8 = var6 <= var7 ? var7 - var6 : 0;
         if (var8 == 0) {
            if (var5 == null) {
               return com.frojasg1.sun.nio.ch.CompletedFuture.withResult(0);
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invoke(this, var5, var4, 0, (Throwable)null);
               return null;
            }
         } else {
            com.frojasg1.sun.nio.ch.PendingFuture var9 = new com.frojasg1.sun.nio.ch.PendingFuture(this, var5, var4);
            WindowsAsynchronousFileChannelImpl.WriteTask var10 = new WindowsAsynchronousFileChannelImpl.WriteTask(var1, var6, var8, var2, var9);
            var9.setContext(var10);
            if (com.frojasg1.sun.nio.ch.Iocp.supportsThreadAgnosticIo()) {
               var10.run();
            } else {
               com.frojasg1.sun.nio.ch.Invoker.invokeOnThreadInThreadPool(this, var10);
            }

            return var9;
         }
      }
   }

   private static native int readFile(long var0, long var2, int var4, long var5, long var7) throws IOException;

   private static native int writeFile(long var0, long var2, int var4, long var5, long var7) throws IOException;

   private static native int lockFile(long var0, long var2, long var4, boolean var6, long var7) throws IOException;

   private static native void close0(long var0);

   static {
      IOUtil.load();
   }

   private static class DefaultIocpHolder {
      static final com.frojasg1.sun.nio.ch.Iocp defaultIocp = defaultIocp();

      private DefaultIocpHolder() {
      }

      private static com.frojasg1.sun.nio.ch.Iocp defaultIocp() {
         try {
            return (new com.frojasg1.sun.nio.ch.Iocp((AsynchronousChannelProvider)null, ThreadPool.createDefault())).start();
         } catch (IOException var1) {
            throw new InternalError(var1);
         }
      }
   }

   private class LockTask<A> implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final long position;
      private final com.frojasg1.sun.nio.ch.FileLockImpl fli;
      private final com.frojasg1.sun.nio.ch.PendingFuture<FileLock, A> result;

      LockTask(long var2, FileLockImpl var4, com.frojasg1.sun.nio.ch.PendingFuture<FileLock, A> var5) {
         this.position = var2;
         this.fli = var4;
         this.result = var5;
      }

      public void run() {
         long var1 = 0L;
         boolean var3 = false;

         try {
            WindowsAsynchronousFileChannelImpl.this.begin();
            var1 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
            synchronized(this.result) {
               int var5 = WindowsAsynchronousFileChannelImpl.lockFile(WindowsAsynchronousFileChannelImpl.this.handle, this.position, this.fli.size(), this.fli.isShared(), var1);
               if (var5 == -2) {
                  var3 = true;
                  return;
               }

               this.result.setResult(this.fli);
            }
         } catch (Throwable var12) {
            WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
            this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(var12));
         } finally {
            if (!var3 && var1 != 0L) {
               WindowsAsynchronousFileChannelImpl.this.ioCache.remove(var1);
            }

            WindowsAsynchronousFileChannelImpl.this.end();
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      public void completed(int var1, boolean var2) {
         this.result.setResult(this.fli);
         if (var2) {
            com.frojasg1.sun.nio.ch.Invoker.invokeUnchecked(this.result);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }

      public void failed(int var1, IOException var2) {
         WindowsAsynchronousFileChannelImpl.this.removeFromFileLockTable(this.fli);
         if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
            this.result.setFailure(var2);
         } else {
            this.result.setFailure(new AsynchronousCloseException());
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }
   }

   private class ReadTask<A> implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final ByteBuffer dst;
      private final int pos;
      private final int rem;
      private final long position;
      private final com.frojasg1.sun.nio.ch.PendingFuture<Integer, A> result;
      private volatile ByteBuffer buf;

      ReadTask(ByteBuffer var2, int var3, int var4, long var5, com.frojasg1.sun.nio.ch.PendingFuture<Integer, A> var7) {
         this.dst = var2;
         this.pos = var3;
         this.rem = var4;
         this.position = var5;
         this.result = var7;
      }

      void releaseBufferIfSubstituted() {
         if (this.buf != this.dst) {
            com.frojasg1.sun.nio.ch.Util.releaseTemporaryDirectBuffer(this.buf);
         }

      }

      void updatePosition(int var1) {
         if (var1 > 0) {
            if (this.buf == this.dst) {
               try {
                  this.dst.position(this.pos + var1);
               } catch (IllegalArgumentException var4) {
               }
            } else {
               this.buf.position(var1).flip();

               try {
                  this.dst.put(this.buf);
               } catch (BufferOverflowException var3) {
               }
            }
         }

      }

      public void run() {
         boolean var1 = true;
         long var2 = 0L;
         long var4;
         if (this.dst instanceof com.frojasg1.sun.nio.ch.DirectBuffer) {
            this.buf = this.dst;
            var4 = ((com.frojasg1.sun.nio.ch.DirectBuffer)this.dst).address() + (long)this.pos;
         } else {
            this.buf = com.frojasg1.sun.nio.ch.Util.getTemporaryDirectBuffer(this.rem);
            var4 = ((com.frojasg1.sun.nio.ch.DirectBuffer)this.buf).address();
         }

         boolean var6 = false;

         label123: {
            try {
               WindowsAsynchronousFileChannelImpl.this.begin();
               var2 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
               int var13 = WindowsAsynchronousFileChannelImpl.readFile(WindowsAsynchronousFileChannelImpl.this.handle, var4, this.rem, this.position, var2);
               if (var13 != -2) {
                  if (var13 != -1) {
                     throw new InternalError("Unexpected result: " + var13);
                  }

                  this.result.setResult(var13);
                  break label123;
               }

               var6 = true;
            } catch (Throwable var11) {
               this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(var11));
               break label123;
            } finally {
               if (!var6) {
                  if (var2 != 0L) {
                     WindowsAsynchronousFileChannelImpl.this.ioCache.remove(var2);
                  }

                  this.releaseBufferIfSubstituted();
               }

               WindowsAsynchronousFileChannelImpl.this.end();
            }

            return;
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      public void completed(int var1, boolean var2) {
         this.updatePosition(var1);
         this.releaseBufferIfSubstituted();
         this.result.setResult(var1);
         if (var2) {
            com.frojasg1.sun.nio.ch.Invoker.invokeUnchecked(this.result);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }

      public void failed(int var1, IOException var2) {
         if (var1 == 38) {
            this.completed(-1, false);
         } else {
            this.releaseBufferIfSubstituted();
            if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
               this.result.setFailure(var2);
            } else {
               this.result.setFailure(new AsynchronousCloseException());
            }

            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }
   }

   private class WriteTask<A> implements Runnable, com.frojasg1.sun.nio.ch.Iocp.ResultHandler {
      private final ByteBuffer src;
      private final int pos;
      private final int rem;
      private final long position;
      private final com.frojasg1.sun.nio.ch.PendingFuture<Integer, A> result;
      private volatile ByteBuffer buf;

      WriteTask(ByteBuffer var2, int var3, int var4, long var5, com.frojasg1.sun.nio.ch.PendingFuture<Integer, A> var7) {
         this.src = var2;
         this.pos = var3;
         this.rem = var4;
         this.position = var5;
         this.result = var7;
      }

      void releaseBufferIfSubstituted() {
         if (this.buf != this.src) {
            com.frojasg1.sun.nio.ch.Util.releaseTemporaryDirectBuffer(this.buf);
         }

      }

      void updatePosition(int var1) {
         if (var1 > 0) {
            try {
               this.src.position(this.pos + var1);
            } catch (IllegalArgumentException var3) {
            }
         }

      }

      public void run() {
         boolean var1 = true;
         long var2 = 0L;
         long var4;
         if (this.src instanceof com.frojasg1.sun.nio.ch.DirectBuffer) {
            this.buf = this.src;
            var4 = ((com.frojasg1.sun.nio.ch.DirectBuffer)this.src).address() + (long)this.pos;
         } else {
            this.buf = Util.getTemporaryDirectBuffer(this.rem);
            this.buf.put(this.src);
            this.buf.flip();
            this.src.position(this.pos);
            var4 = ((DirectBuffer)this.buf).address();
         }

         label63: {
            try {
               WindowsAsynchronousFileChannelImpl.this.begin();
               var2 = WindowsAsynchronousFileChannelImpl.this.ioCache.add(this.result);
               int var12 = WindowsAsynchronousFileChannelImpl.writeFile(WindowsAsynchronousFileChannelImpl.this.handle, var4, this.rem, this.position, var2);
               if (var12 != -2) {
                  throw new InternalError("Unexpected result: " + var12);
               }
            } catch (Throwable var10) {
               this.result.setFailure(WindowsAsynchronousFileChannelImpl.toIOException(var10));
               if (var2 != 0L) {
                  WindowsAsynchronousFileChannelImpl.this.ioCache.remove(var2);
               }

               this.releaseBufferIfSubstituted();
               break label63;
            } finally {
               WindowsAsynchronousFileChannelImpl.this.end();
            }

            return;
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }

      public void completed(int var1, boolean var2) {
         this.updatePosition(var1);
         this.releaseBufferIfSubstituted();
         this.result.setResult(var1);
         if (var2) {
            com.frojasg1.sun.nio.ch.Invoker.invokeUnchecked(this.result);
         } else {
            com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
         }

      }

      public void failed(int var1, IOException var2) {
         this.releaseBufferIfSubstituted();
         if (WindowsAsynchronousFileChannelImpl.this.isOpen()) {
            this.result.setFailure(var2);
         } else {
            this.result.setFailure(new AsynchronousCloseException());
         }

         com.frojasg1.sun.nio.ch.Invoker.invoke(this.result);
      }
   }
}
