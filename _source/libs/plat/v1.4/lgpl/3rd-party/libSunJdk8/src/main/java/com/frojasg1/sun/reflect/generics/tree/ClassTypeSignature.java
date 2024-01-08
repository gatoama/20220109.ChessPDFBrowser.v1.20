package com.frojasg1.sun.reflect.generics.tree;

import java.util.List;

import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.tree.SimpleClassTypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.TypeTreeVisitor;

public class ClassTypeSignature implements FieldTypeSignature {
   private final List<com.frojasg1.sun.reflect.generics.tree.SimpleClassTypeSignature> path;

   private ClassTypeSignature(List<com.frojasg1.sun.reflect.generics.tree.SimpleClassTypeSignature> var1) {
      this.path = var1;
   }

   public static ClassTypeSignature make(List<com.frojasg1.sun.reflect.generics.tree.SimpleClassTypeSignature> var0) {
      return new ClassTypeSignature(var0);
   }

   public List<SimpleClassTypeSignature> getPath() {
      return this.path;
   }

   public void accept(TypeTreeVisitor<?> var1) {
      var1.visitClassTypeSignature(this);
   }
}
