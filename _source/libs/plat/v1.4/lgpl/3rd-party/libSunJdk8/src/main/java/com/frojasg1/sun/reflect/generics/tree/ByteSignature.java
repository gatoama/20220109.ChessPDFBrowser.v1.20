package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BaseType;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class ByteSignature implements BaseType {
   private static final ByteSignature singleton = new ByteSignature();

   private ByteSignature() {
   }

   public static ByteSignature make() {
      return singleton;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitByteSignature(this);
   }
}
