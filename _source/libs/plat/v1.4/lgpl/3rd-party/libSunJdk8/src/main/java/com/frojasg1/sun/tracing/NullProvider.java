package com.frojasg1.sun.tracing;

import com.sun.tracing.Provider;
import com.frojasg1.sun.tracing.NullProbe;
import com.frojasg1.sun.tracing.ProbeSkeleton;
import com.frojasg1.sun.tracing.ProviderSkeleton;

import java.lang.reflect.Method;

class NullProvider extends ProviderSkeleton {
   NullProvider(Class<? extends Provider> var1) {
      super(var1);
   }

   protected ProbeSkeleton createProbe(Method var1) {
      return new com.frojasg1.sun.tracing.NullProbe(var1.getParameterTypes());
   }
}
