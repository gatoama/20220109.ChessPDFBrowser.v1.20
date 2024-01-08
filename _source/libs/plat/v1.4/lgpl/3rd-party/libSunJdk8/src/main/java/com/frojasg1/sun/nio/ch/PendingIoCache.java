package com.frojasg1.sun.nio.ch;

import java.nio.channels.AsynchronousCloseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.ch.Groupable;
import com.frojasg1.sun.nio.ch.Iocp;
import com.frojasg1.sun.nio.ch.PendingFuture;

class PendingIoCache {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final int addressSize;
   private static final int SIZEOF_OVERLAPPED;
   private boolean closed;
   private boolean closePending;
   private final Map<Long, com.frojasg1.sun.nio.ch.PendingFuture> pendingIoMap = new HashMap();
   private long[] overlappedCache = new long[4];
   private int overlappedCacheCount = 0;

   private static int dependsArch(int var0, int var1) {
      return addressSize == 4 ? var0 : var1;
   }

   PendingIoCache() {
   }

   long add(com.frojasg1.sun.nio.ch.PendingFuture<?, ?> var1) {
      synchronized(this) {
         if (this.closed) {
            throw new AssertionError("Should not get here");
         } else {
            long var3;
            if (this.overlappedCacheCount > 0) {
               var3 = this.overlappedCache[--this.overlappedCacheCount];
            } else {
               var3 = unsafe.allocateMemory((long)SIZEOF_OVERLAPPED);
            }

            this.pendingIoMap.put(var3, var1);
            return var3;
         }
      }
   }

   <V, A> com.frojasg1.sun.nio.ch.PendingFuture<V, A> remove(long var1) {
      synchronized(this) {
         com.frojasg1.sun.nio.ch.PendingFuture var4 = (com.frojasg1.sun.nio.ch.PendingFuture)this.pendingIoMap.remove(var1);
         if (var4 != null) {
            if (this.overlappedCacheCount < this.overlappedCache.length) {
               this.overlappedCache[this.overlappedCacheCount++] = var1;
            } else {
               unsafe.freeMemory(var1);
            }

            if (this.closePending) {
               this.notifyAll();
            }
         }

         return var4;
      }
   }

   void close() {
      synchronized(this) {
         if (!this.closed) {
            if (!this.pendingIoMap.isEmpty()) {
               this.clearPendingIoMap();
            }

            while(this.overlappedCacheCount > 0) {
               unsafe.freeMemory(this.overlappedCache[--this.overlappedCacheCount]);
            }

            this.closed = true;
         }
      }
   }

   private void clearPendingIoMap() {
      assert Thread.holdsLock(this);

      this.closePending = true;

      try {
         this.wait(50L);
      } catch (InterruptedException var7) {
         Thread.currentThread().interrupt();
      }

      this.closePending = false;
      if (!this.pendingIoMap.isEmpty()) {
         Iterator var1 = this.pendingIoMap.keySet().iterator();

         while(var1.hasNext()) {
            Long var2 = (Long)var1.next();
            com.frojasg1.sun.nio.ch.PendingFuture var3 = (com.frojasg1.sun.nio.ch.PendingFuture)this.pendingIoMap.get(var2);

            assert !var3.isDone();

            com.frojasg1.sun.nio.ch.Iocp var4 = (com.frojasg1.sun.nio.ch.Iocp)((com.frojasg1.sun.nio.ch.Groupable)var3.channel()).group();
            var4.makeStale(var2);
            final com.frojasg1.sun.nio.ch.Iocp.ResultHandler var5 = (com.frojasg1.sun.nio.ch.Iocp.ResultHandler)var3.getContext();
            Runnable var6 = new Runnable() {
               public void run() {
                  var5.failed(-1, new AsynchronousCloseException());
               }
            };
            var4.executeOnPooledThread(var6);
         }

         this.pendingIoMap.clear();
      }
   }

   static {
      addressSize = unsafe.addressSize();
      SIZEOF_OVERLAPPED = dependsArch(20, 32);
   }
}
