package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BaseType;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class LongSignature implements BaseType {
   private static final LongSignature singleton = new LongSignature();

   private LongSignature() {
   }

   public static LongSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitLongSignature(this);
   }
}
