package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BaseType;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class CharSignature implements BaseType {
   private static final CharSignature singleton = new CharSignature();

   private CharSignature() {
   }

   public static CharSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitCharSignature(this);
   }
}
