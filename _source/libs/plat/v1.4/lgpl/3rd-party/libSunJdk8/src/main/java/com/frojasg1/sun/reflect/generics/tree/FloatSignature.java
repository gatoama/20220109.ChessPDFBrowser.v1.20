package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BaseType;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class FloatSignature implements BaseType {
   private static final FloatSignature singleton = new FloatSignature();

   private FloatSignature() {
   }

   public static FloatSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitFloatSignature(this);
   }
}
