package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BaseType;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class BooleanSignature implements BaseType {
   private static final BooleanSignature singleton = new BooleanSignature();

   private BooleanSignature() {
   }

   public static BooleanSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitBooleanSignature(this);
   }
}
