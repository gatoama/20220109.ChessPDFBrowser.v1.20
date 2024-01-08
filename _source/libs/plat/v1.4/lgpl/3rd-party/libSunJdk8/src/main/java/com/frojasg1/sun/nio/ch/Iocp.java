package com.frojasg1.sun.nio.ch;

import java.io.Closeable;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.Channel;
import java.nio.channels.ShutdownChannelGroupException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.security.AccessController;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl;
import com.frojasg1.sun.nio.ch.IOUtil;
import com.frojasg1.sun.nio.ch.Invoker;
import com.frojasg1.sun.nio.ch.PendingFuture;
import com.frojasg1.sun.nio.ch.ThreadPool;
import com.frojasg1.sun.security.action.GetPropertyAction;

class Iocp extends com.frojasg1.sun.nio.ch.AsynchronousChannelGroupImpl {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final long INVALID_HANDLE_VALUE = -1L;
   private static final boolean supportsThreadAgnosticIo;
   private final ReadWriteLock keyToChannelLock = new ReentrantReadWriteLock();
   private final Map<Integer, Iocp.OverlappedChannel> keyToChannel = new HashMap();
   private int nextCompletionKey = 1;
   private final long port = createIoCompletionPort(-1L, 0L, 0, this.fixedThreadCount());
   private boolean closed;
   private final Set<Long> staleIoSet = new HashSet();

   Iocp(AsynchronousChannelProvider var1, ThreadPool var2) throws IOException {
      super(var1, var2);
   }

   Iocp start() {
      this.startThreads(new Iocp.EventHandlerTask());
      return this;
   }

   static boolean supportsThreadAgnosticIo() {
      return supportsThreadAgnosticIo;
   }

   void implClose() {
      synchronized(this) {
         if (this.closed) {
            return;
         }

         this.closed = true;
      }

      close0(this.port);
      synchronized(this.staleIoSet) {
         Iterator var2 = this.staleIoSet.iterator();

         while(var2.hasNext()) {
            Long var3 = (Long)var2.next();
            unsafe.freeMemory(var3);
         }

         this.staleIoSet.clear();
      }
   }

   boolean isEmpty() {
      this.keyToChannelLock.writeLock().lock();

      boolean var1;
      try {
         var1 = this.keyToChannel.isEmpty();
      } finally {
         this.keyToChannelLock.writeLock().unlock();
      }

      return var1;
   }

   final Object attachForeignChannel(final Channel var1, FileDescriptor var2) throws IOException {
      int var3 = this.associate(new Iocp.OverlappedChannel() {
         public <V, A> com.frojasg1.sun.nio.ch.PendingFuture<V, A> getByOverlapped(long var1x) {
            return null;
         }

         public void close() throws IOException {
            var1.close();
         }
      }, 0L);
      return var3;
   }

   final void detachForeignChannel(Object var1) {
      this.disassociate((Integer)var1);
   }

   void closeAllChannels() {
      Iocp.OverlappedChannel[] var2 = new Iocp.OverlappedChannel[32];

      int var3;
      do {
         this.keyToChannelLock.writeLock().lock();
         var3 = 0;

         try {
            Iterator var4 = this.keyToChannel.keySet().iterator();

            while(var4.hasNext()) {
               Integer var5 = (Integer)var4.next();
               var2[var3++] = (Iocp.OverlappedChannel)this.keyToChannel.get(var5);
               if (var3 >= 32) {
                  break;
               }
            }
         } finally {
            this.keyToChannelLock.writeLock().unlock();
         }

         for(int var11 = 0; var11 < var3; ++var11) {
            try {
               var2[var11].close();
            } catch (IOException var9) {
            }
         }
      } while(var3 > 0);

   }

   private void wakeup() {
      try {
         postQueuedCompletionStatus(this.port, 0);
      } catch (IOException var2) {
         throw new AssertionError(var2);
      }
   }

   void executeOnHandlerTask(Runnable var1) {
      synchronized(this) {
         if (this.closed) {
            throw new RejectedExecutionException();
         } else {
            this.offerTask(var1);
            this.wakeup();
         }
      }
   }

   void shutdownHandlerTasks() {
      int var1 = this.threadCount();

      while(var1-- > 0) {
         this.wakeup();
      }

   }

   int associate(Iocp.OverlappedChannel var1, long var2) throws IOException {
      this.keyToChannelLock.writeLock().lock();

      try {
         if (this.isShutdown()) {
            throw new ShutdownChannelGroupException();
         } else {
            int var4;
            do {
               var4 = this.nextCompletionKey++;
            } while(var4 == 0 || this.keyToChannel.containsKey(var4));

            if (var2 != 0L) {
               createIoCompletionPort(var2, this.port, var4, 0);
            }

            this.keyToChannel.put(var4, var1);
            return var4;
         }
      } finally {
         this.keyToChannelLock.writeLock().unlock();
      }
   }

   void disassociate(int var1) {
      boolean var2 = false;
      this.keyToChannelLock.writeLock().lock();

      try {
         this.keyToChannel.remove(var1);
         if (this.keyToChannel.isEmpty()) {
            var2 = true;
         }
      } finally {
         this.keyToChannelLock.writeLock().unlock();
      }

      if (var2 && this.isShutdown()) {
         try {
            this.shutdownNow();
         } catch (IOException var6) {
         }
      }

   }

   void makeStale(Long var1) {
      synchronized(this.staleIoSet) {
         this.staleIoSet.add(var1);
      }
   }

   private void checkIfStale(long var1) {
      synchronized(this.staleIoSet) {
         boolean var4 = this.staleIoSet.remove(var1);
         if (var4) {
            unsafe.freeMemory(var1);
         }

      }
   }

   private static IOException translateErrorToIOException(int var0) {
      String var1 = getErrorMessage(var0);
      if (var1 == null) {
         var1 = "Unknown error: 0x0" + Integer.toHexString(var0);
      }

      return new IOException(var1);
   }

   private static native void initIDs();

   private static native long createIoCompletionPort(long var0, long var2, int var4, int var5) throws IOException;

   private static native void close0(long var0);

   private static native void getQueuedCompletionStatus(long var0, Iocp.CompletionStatus var2) throws IOException;

   private static native void postQueuedCompletionStatus(long var0, int var2) throws IOException;

   private static native String getErrorMessage(int var0);

   static {
      IOUtil.load();
      initIDs();
      String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("os.version"));
      String[] var1 = var0.split("\\.");
      supportsThreadAgnosticIo = Integer.parseInt(var1[0]) >= 6;
   }

   private static class CompletionStatus {
      private int error;
      private int bytesTransferred;
      private int completionKey;
      private long overlapped;

      private CompletionStatus() {
      }

      int error() {
         return this.error;
      }

      int bytesTransferred() {
         return this.bytesTransferred;
      }

      int completionKey() {
         return this.completionKey;
      }

      long overlapped() {
         return this.overlapped;
      }
   }

   private class EventHandlerTask implements Runnable {
      private EventHandlerTask() {
      }

      public void run() {
         com.frojasg1.sun.nio.ch.Invoker.GroupAndInvokeCount var1 = com.frojasg1.sun.nio.ch.Invoker.getGroupAndInvokeCount();
         boolean var2 = var1 != null;
         Iocp.CompletionStatus var3 = new Iocp.CompletionStatus();
         boolean var4 = false;

         int var25;
         while(true) {
            boolean var15 = false;

            try {
               label319: {
                  var15 = true;
                  if (var1 != null) {
                     var1.resetInvokeCount();
                  }

                  var4 = false;

                  try {
                     Iocp.getQueuedCompletionStatus(Iocp.this.port, var3);
                  } catch (IOException var22) {
                     var22.printStackTrace();
                     var15 = false;
                     break label319;
                  }

                  if (var3.completionKey() == 0 && var3.overlapped() == 0L) {
                     Runnable var24 = Iocp.this.pollTask();
                     if (var24 == null) {
                        var15 = false;
                        break;
                     }

                     var4 = true;
                     var24.run();
                     continue;
                  }

                  Iocp.OverlappedChannel var5 = null;
                  Iocp.this.keyToChannelLock.readLock().lock();

                  try {
                     var5 = (Iocp.OverlappedChannel)Iocp.this.keyToChannel.get(var3.completionKey());
                     if (var5 == null) {
                        Iocp.this.checkIfStale(var3.overlapped());
                        continue;
                     }
                  } finally {
                     Iocp.this.keyToChannelLock.readLock().unlock();
                  }

                  com.frojasg1.sun.nio.ch.PendingFuture var6 = var5.getByOverlapped(var3.overlapped());
                  if (var6 == null) {
                     Iocp.this.checkIfStale(var3.overlapped());
                     continue;
                  }

                  synchronized(var6) {
                     if (var6.isDone()) {
                        continue;
                     }
                  }

                  int var7 = var3.error();
                  Iocp.ResultHandler var8 = (Iocp.ResultHandler)var6.getContext();
                  var4 = true;
                  if (var7 == 0) {
                     var8.completed(var3.bytesTransferred(), var2);
                  } else {
                     var8.failed(var7, Iocp.translateErrorToIOException(var7));
                  }
                  continue;
               }
            } finally {
               if (var15) {
                  int var10 = Iocp.this.threadExit(this, var4);
                  if (var10 == 0 && Iocp.this.isShutdown()) {
                     Iocp.this.implClose();
                  }

               }
            }

            var25 = Iocp.this.threadExit(this, var4);
            if (var25 == 0 && Iocp.this.isShutdown()) {
               Iocp.this.implClose();
            }

            return;
         }

         var25 = Iocp.this.threadExit(this, var4);
         if (var25 == 0 && Iocp.this.isShutdown()) {
            Iocp.this.implClose();
         }

      }
   }

   interface OverlappedChannel extends Closeable {
      <V, A> com.frojasg1.sun.nio.ch.PendingFuture<V, A> getByOverlapped(long var1);
   }

   interface ResultHandler {
      void completed(int var1, boolean var2);

      void failed(int var1, IOException var2);
   }
}
