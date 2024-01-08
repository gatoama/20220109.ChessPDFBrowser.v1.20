package com.frojasg1.sun.tracing.dtrace;

import com.frojasg1.sun.tracing.dtrace.DTraceProvider;
import com.frojasg1.sun.tracing.dtrace.JVM;
import com.frojasg1.sun.tracing.dtrace.SystemResource;

class Activation {
   private com.frojasg1.sun.tracing.dtrace.SystemResource resource;
   private int referenceCount;

   Activation(String var1, com.frojasg1.sun.tracing.dtrace.DTraceProvider[] var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         RuntimePermission var4 = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
         var3.checkPermission(var4);
      }

      this.referenceCount = var2.length;
      com.frojasg1.sun.tracing.dtrace.DTraceProvider[] var8 = var2;
      int var5 = var2.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         com.frojasg1.sun.tracing.dtrace.DTraceProvider var7 = var8[var6];
         var7.setActivation(this);
      }

      this.resource = new com.frojasg1.sun.tracing.dtrace.SystemResource(this, com.frojasg1.sun.tracing.dtrace.JVM.activate(var1, var2));
   }

   void disposeProvider(com.frojasg1.sun.tracing.dtrace.DTraceProvider var1) {
      if (--this.referenceCount == 0) {
         this.resource.dispose();
      }

   }
}
