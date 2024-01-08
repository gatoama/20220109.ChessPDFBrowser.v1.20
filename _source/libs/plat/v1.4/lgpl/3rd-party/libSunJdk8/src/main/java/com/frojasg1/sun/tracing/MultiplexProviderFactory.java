package com.frojasg1.sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import com.frojasg1.sun.tracing.MultiplexProvider;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiplexProviderFactory extends ProviderFactory {
   private Set<ProviderFactory> factories;

   public MultiplexProviderFactory(Set<ProviderFactory> var1) {
      this.factories = var1;
   }

   public <T extends Provider> T createProvider(Class<T> var1) {
      HashSet var2 = new HashSet();
      Iterator var3 = this.factories.iterator();

      while(var3.hasNext()) {
         ProviderFactory var4 = (ProviderFactory)var3.next();
         var2.add(var4.createProvider(var1));
      }

      com.frojasg1.sun.tracing.MultiplexProvider var5 = new com.frojasg1.sun.tracing.MultiplexProvider(var1, var2);
      var5.init();
      return var5.newProxyInstance();
   }
}
