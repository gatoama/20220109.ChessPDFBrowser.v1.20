package com.frojasg1.sun.tracing;

import com.sun.tracing.ProbeName;
import com.sun.tracing.Provider;
import com.frojasg1.sun.tracing.PrintStreamProbe;
import com.frojasg1.sun.tracing.ProbeSkeleton;
import com.frojasg1.sun.tracing.ProviderSkeleton;

import java.io.PrintStream;
import java.lang.reflect.Method;

class PrintStreamProvider extends ProviderSkeleton {
   private PrintStream stream;
   private String providerName;

   protected ProbeSkeleton createProbe(Method var1) {
      String var2 = getAnnotationString(var1, ProbeName.class, var1.getName());
      return new com.frojasg1.sun.tracing.PrintStreamProbe(this, var2, var1.getParameterTypes());
   }

   PrintStreamProvider(Class<? extends Provider> var1, PrintStream var2) {
      super(var1);
      this.stream = var2;
      this.providerName = this.getProviderName();
   }

   PrintStream getStream() {
      return this.stream;
   }

   String getName() {
      return this.providerName;
   }
}
