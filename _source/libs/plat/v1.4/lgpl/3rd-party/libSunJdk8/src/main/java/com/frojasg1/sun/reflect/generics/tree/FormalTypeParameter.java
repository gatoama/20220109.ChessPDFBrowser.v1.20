package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.tree.TypeTree;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class FormalTypeParameter implements TypeTree {
   private final String name;
   private final com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] bounds;

   private FormalTypeParameter(String var1, com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var2) {
      this.name = var1;
      this.bounds = var2;
   }

   public static FormalTypeParameter make(String var0, com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var1) {
      return new FormalTypeParameter(var0, var1);
   }

   public FieldTypeSignature[] getBounds() {
      return this.bounds;
   }

   public String getName() {
      return this.name;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitFormalTypeParameter(this);
   }
}
