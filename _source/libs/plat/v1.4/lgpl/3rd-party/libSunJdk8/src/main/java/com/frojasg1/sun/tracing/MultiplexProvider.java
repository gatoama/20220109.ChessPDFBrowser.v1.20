package com.frojasg1.sun.tracing;

import com.sun.tracing.Provider;
import com.frojasg1.sun.tracing.MultiplexProbe;
import com.frojasg1.sun.tracing.ProbeSkeleton;
import com.frojasg1.sun.tracing.ProviderSkeleton;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Set;

class MultiplexProvider extends ProviderSkeleton {
   private Set<Provider> providers;

   protected ProbeSkeleton createProbe(Method var1) {
      return new com.frojasg1.sun.tracing.MultiplexProbe(var1, this.providers);
   }

   MultiplexProvider(Class<? extends Provider> var1, Set<Provider> var2) {
      super(var1);
      this.providers = var2;
   }

   public void dispose() {
      Iterator var1 = this.providers.iterator();

      while(var1.hasNext()) {
         Provider var2 = (Provider)var1.next();
         var2.dispose();
      }

      super.dispose();
   }
}
