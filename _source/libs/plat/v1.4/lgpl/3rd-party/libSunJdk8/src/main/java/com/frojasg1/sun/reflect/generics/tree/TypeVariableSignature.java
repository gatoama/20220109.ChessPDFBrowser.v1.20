package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class TypeVariableSignature implements FieldTypeSignature {
   private final String identifier;

   private TypeVariableSignature(String var1) {
      this.identifier = var1;
   }

   public static TypeVariableSignature make(String var0) {
      return new TypeVariableSignature(var0);
   }

   public String getIdentifier() {
      return this.identifier;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitTypeVariableSignature(this);
   }
}
