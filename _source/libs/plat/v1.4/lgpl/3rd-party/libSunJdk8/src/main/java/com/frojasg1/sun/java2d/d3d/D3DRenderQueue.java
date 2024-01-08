package com.frojasg1.sun.java2d.d3d;

import com.frojasg1.sun.java2d.ScreenUpdateManager;
import com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager;
import com.frojasg1.sun.java2d.pipe.RenderBuffer;
import com.frojasg1.sun.java2d.pipe.RenderQueue;

public class D3DRenderQueue extends RenderQueue {
   private static D3DRenderQueue theInstance;
   private static Thread rqThread;

   private D3DRenderQueue() {
   }

   public static synchronized D3DRenderQueue getInstance() {
      if (theInstance == null) {
         theInstance = new D3DRenderQueue();
         theInstance.flushAndInvokeNow(new Runnable() {
            public void run() {
               D3DRenderQueue.rqThread = Thread.currentThread();
            }
         });
      }

      return theInstance;
   }

   public static void sync() {
      if (theInstance != null) {
         com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager var0 = (D3DScreenUpdateManager)ScreenUpdateManager.getInstance();
         var0.runUpdateNow();
         theInstance.lock();

         try {
            theInstance.ensureCapacity(4);
            theInstance.getBuffer().putInt(76);
            theInstance.flushNow();
         } finally {
            theInstance.unlock();
         }
      }

   }

   public static void restoreDevices() {
      D3DRenderQueue var0 = getInstance();
      var0.lock();

      try {
         var0.ensureCapacity(4);
         var0.getBuffer().putInt(77);
         var0.flushNow();
      } finally {
         var0.unlock();
      }

   }

   public static boolean isRenderQueueThread() {
      return Thread.currentThread() == rqThread;
   }

   public static void disposeGraphicsConfig(long var0) {
      D3DRenderQueue var2 = getInstance();
      var2.lock();

      try {
         RenderBuffer var3 = var2.getBuffer();
         var2.ensureCapacityAndAlignment(12, 4);
         var3.putInt(74);
         var3.putLong(var0);
         var2.flushNow();
      } finally {
         var2.unlock();
      }

   }

   public void flushNow() {
      this.flushBuffer((Runnable)null);
   }

   public void flushAndInvokeNow(Runnable var1) {
      this.flushBuffer(var1);
   }

   private native void flushBuffer(long var1, int var3, Runnable var4);

   private void flushBuffer(Runnable var1) {
      int var2 = this.buf.position();
      if (var2 > 0 || var1 != null) {
         this.flushBuffer(this.buf.getAddress(), var2, var1);
      }

      this.buf.clear();
      this.refSet.clear();
   }
}
