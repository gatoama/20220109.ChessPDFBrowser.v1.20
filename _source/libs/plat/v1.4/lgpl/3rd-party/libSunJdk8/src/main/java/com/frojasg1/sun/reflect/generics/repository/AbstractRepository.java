package com.frojasg1.sun.reflect.generics.repository;

import com.frojasg1.sun.reflect.generics.factory.GenericsFactory;
import com.frojasg1.sun.reflect.generics.tree.Tree;
import com.frojasg1.sun.reflect.generics.visitor.Reifier;

public abstract class AbstractRepository<T extends Tree> {
   private final GenericsFactory factory;
   private final T tree;

   private GenericsFactory getFactory() {
      return this.factory;
   }

   protected T getTree() {
      return this.tree;
   }

   protected Reifier getReifier() {
      return Reifier.make(this.getFactory());
   }

   protected AbstractRepository(String var1, GenericsFactory var2) {
      this.tree = this.parse(var1);
      this.factory = var2;
   }

   protected abstract T parse(String var1);
}
