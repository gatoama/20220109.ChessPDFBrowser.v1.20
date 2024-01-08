package com.frojasg1.sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import com.frojasg1.sun.tracing.NullProvider;

public class NullProviderFactory extends ProviderFactory {
   public NullProviderFactory() {
   }

   public <T extends Provider> T createProvider(Class<T> var1) {
      com.frojasg1.sun.tracing.NullProvider var2 = new com.frojasg1.sun.tracing.NullProvider(var1);
      var2.init();
      return var2.newProxyInstance();
   }
}
