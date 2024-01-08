package com.frojasg1.sun.tracing.dtrace;

import com.frojasg1.sun.tracing.dtrace.Activation;
import com.frojasg1.sun.tracing.dtrace.JVM;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashSet;

class SystemResource extends WeakReference<com.frojasg1.sun.tracing.dtrace.Activation> {
   private long handle;
   private static ReferenceQueue<com.frojasg1.sun.tracing.dtrace.Activation> referenceQueue;
   static HashSet<SystemResource> resources;

   SystemResource(com.frojasg1.sun.tracing.dtrace.Activation var1, long var2) {
      super(var1, referenceQueue);
      this.handle = var2;
      flush();
      resources.add(this);
   }

   void dispose() {
      com.frojasg1.sun.tracing.dtrace.JVM.dispose(this.handle);
      resources.remove(this);
      this.handle = 0L;
   }

   static void flush() {
      SystemResource var0 = null;

      while((var0 = (SystemResource)referenceQueue.poll()) != null) {
         if (var0.handle != 0L) {
            var0.dispose();
         }
      }

   }

   static {
      referenceQueue = referenceQueue = new ReferenceQueue();
      resources = new HashSet();
   }
}
