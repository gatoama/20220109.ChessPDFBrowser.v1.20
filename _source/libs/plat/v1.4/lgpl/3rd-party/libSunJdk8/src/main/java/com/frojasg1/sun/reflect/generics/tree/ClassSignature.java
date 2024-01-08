package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature;
import com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter;
import com.frojasg1.sun.reflect.generics.tree.Signature;
import com.frojasg1.sun.reflect.generics.visitor.Visitor;

public class ClassSignature implements Signature {
   private final com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter[] formalTypeParams;
   private final com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature superclass;
   private final com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature[] superInterfaces;

   private ClassSignature(com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter[] var1, com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature var2, com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature[] var3) {
      this.formalTypeParams = var1;
      this.superclass = var2;
      this.superInterfaces = var3;
   }

   public static ClassSignature make(com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter[] var0, com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature var1, com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature[] var2) {
      return new ClassSignature(var0, var1, var2);
   }

   public FormalTypeParameter[] getFormalTypeParameters() {
      return this.formalTypeParams;
   }

   public com.frojasg1.sun.reflect.generics.tree.ClassTypeSignature getSuperclass() {
      return this.superclass;
   }

   public ClassTypeSignature[] getSuperInterfaces() {
      return this.superInterfaces;
   }

   public void accept(Visitor<?> var1) {
      var1.visitClassSignature(this);
   }
}
