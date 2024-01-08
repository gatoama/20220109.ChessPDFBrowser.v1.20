package com.frojasg1.sun.reflect.generics.reflectiveObjects;

import com.frojasg1.sun.reflect.generics.factory.GenericsFactory;
import com.frojasg1.sun.reflect.generics.visitor.Reifier;

public abstract class LazyReflectiveObjectGenerator {
   private final GenericsFactory factory;

   protected LazyReflectiveObjectGenerator(GenericsFactory var1) {
      this.factory = var1;
   }

   private GenericsFactory getFactory() {
      return this.factory;
   }

   protected Reifier getReifier() {
      return Reifier.make(this.getFactory());
   }
}
