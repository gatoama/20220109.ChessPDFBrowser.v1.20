package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.tree.TypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class ArrayTypeSignature implements FieldTypeSignature {
   private final com.frojasg1.sun.reflect.generics.tree.TypeSignature componentType;

   private ArrayTypeSignature(com.frojasg1.sun.reflect.generics.tree.TypeSignature var1) {
      this.componentType = var1;
   }

   public static ArrayTypeSignature make(com.frojasg1.sun.reflect.generics.tree.TypeSignature var0) {
      return new ArrayTypeSignature(var0);
   }

   public TypeSignature getComponentType() {
      return this.componentType;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitArrayTypeSignature(this);
   }
}
