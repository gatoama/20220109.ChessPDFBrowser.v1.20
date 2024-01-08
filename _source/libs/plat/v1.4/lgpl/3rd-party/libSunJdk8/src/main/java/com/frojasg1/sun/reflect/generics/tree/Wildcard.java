package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.BottomSignature;
import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.tree.TypeArgument;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class Wildcard implements TypeArgument {
   private com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] upperBounds;
   private com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] lowerBounds;
   private static final com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] emptyBounds = new com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[0];

   private Wildcard(com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var1, com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var2) {
      this.upperBounds = var1;
      this.lowerBounds = var2;
   }

   public static Wildcard make(com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var0, com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var1) {
      return new Wildcard(var0, var1);
   }

   public com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] getUpperBounds() {
      return this.upperBounds;
   }

   public FieldTypeSignature[] getLowerBounds() {
      return this.lowerBounds.length == 1 && this.lowerBounds[0] == BottomSignature.make() ? emptyBounds : this.lowerBounds;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitWildcard(this);
   }
}
