package com.frojasg1.sun.tracing.dtrace;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import com.frojasg1.sun.tracing.dtrace.Activation;
import com.frojasg1.sun.tracing.dtrace.DTraceProvider;
import com.frojasg1.sun.tracing.dtrace.JVM;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public final class DTraceProviderFactory extends ProviderFactory {
   public DTraceProviderFactory() {
   }

   public <T extends Provider> T createProvider(Class<T> var1) {
      com.frojasg1.sun.tracing.dtrace.DTraceProvider var2 = new com.frojasg1.sun.tracing.dtrace.DTraceProvider(var1);
      Provider var3 = var2.newProxyInstance();
      var2.setProxy(var3);
      var2.init();
      new com.frojasg1.sun.tracing.dtrace.Activation(var2.getModuleName(), new com.frojasg1.sun.tracing.dtrace.DTraceProvider[]{var2});
      return (T) var3;
   }

   public Map<Class<? extends Provider>, Provider> createProviders(Set<Class<? extends Provider>> var1, String var2) {
      HashMap var3 = new HashMap();
      HashSet var4 = new HashSet();
      Iterator var5 = var1.iterator();

      while(var5.hasNext()) {
         Class var6 = (Class)var5.next();
         com.frojasg1.sun.tracing.dtrace.DTraceProvider var7 = new com.frojasg1.sun.tracing.dtrace.DTraceProvider(var6);
         var4.add(var7);
         var3.put(var6, var7.newProxyInstance());
      }

      new com.frojasg1.sun.tracing.dtrace.Activation(var2, (com.frojasg1.sun.tracing.dtrace.DTraceProvider[])var4.toArray(new com.frojasg1.sun.tracing.dtrace.DTraceProvider[0]));
      return var3;
   }

   public static boolean isSupported() {
      try {
         SecurityManager var0 = System.getSecurityManager();
         if (var0 != null) {
            RuntimePermission var1 = new RuntimePermission("com.sun.tracing.dtrace.createProvider");
            var0.checkPermission(var1);
         }

         return com.frojasg1.sun.tracing.dtrace.JVM.isSupported();
      } catch (SecurityException var2) {
         return false;
      }
   }
}
