package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.misc.Cleaner;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.NativeBuffers;

class NativeBuffer {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private final long address;
   private final int size;
   private final Cleaner cleaner;
   private Object owner;

   NativeBuffer(int var1) {
      this.address = unsafe.allocateMemory((long)var1);
      this.size = var1;
      this.cleaner = Cleaner.create(this, new NativeBuffer.Deallocator(this.address));
   }

   void release() {
      com.frojasg1.sun.nio.fs.NativeBuffers.releaseNativeBuffer(this);
   }

   long address() {
      return this.address;
   }

   int size() {
      return this.size;
   }

   Cleaner cleaner() {
      return this.cleaner;
   }

   void setOwner(Object var1) {
      this.owner = var1;
   }

   Object owner() {
      return this.owner;
   }

   private static class Deallocator implements Runnable {
      private final long address;

      Deallocator(long var1) {
         this.address = var1;
      }

      public void run() {
         NativeBuffer.unsafe.freeMemory(this.address);
      }
   }
}
