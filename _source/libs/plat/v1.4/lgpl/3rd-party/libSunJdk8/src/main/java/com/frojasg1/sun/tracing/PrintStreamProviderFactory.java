package com.frojasg1.sun.tracing;

import com.sun.tracing.Provider;
import com.sun.tracing.ProviderFactory;
import com.frojasg1.sun.tracing.PrintStreamProvider;

import java.io.PrintStream;

public class PrintStreamProviderFactory extends ProviderFactory {
   private PrintStream stream;

   public PrintStreamProviderFactory(PrintStream var1) {
      this.stream = var1;
   }

   public <T extends Provider> T createProvider(Class<T> var1) {
      com.frojasg1.sun.tracing.PrintStreamProvider var2 = new com.frojasg1.sun.tracing.PrintStreamProvider(var1, this.stream);
      var2.init();
      return var2.newProxyInstance();
   }
}
