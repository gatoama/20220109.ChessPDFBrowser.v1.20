package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BaseType;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class ShortSignature implements BaseType {
   private static final ShortSignature singleton = new ShortSignature();

   private ShortSignature() {
   }

   public static ShortSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitShortSignature(this);
   }
}
