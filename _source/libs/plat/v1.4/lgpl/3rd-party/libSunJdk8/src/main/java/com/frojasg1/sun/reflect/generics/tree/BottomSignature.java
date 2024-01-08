package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class BottomSignature implements FieldTypeSignature {
   private static final BottomSignature singleton = new BottomSignature();

   private BottomSignature() {
   }

   public static BottomSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitBottomSignature(this);
   }
}
