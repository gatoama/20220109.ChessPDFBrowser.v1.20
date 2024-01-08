package com.frojasg1.sun.reflect.generics.tree;

import com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature;
import com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter;
import com.frojasg1.sun.reflect.generics.tree.ReturnType;
import com.frojasg1.sun.reflect.generics.tree.Signature;
import com.frojasg1.sun.reflect.generics.tree.TypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.Visitor;

public class MethodTypeSignature implements Signature {
   private final com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter[] formalTypeParams;
   private final com.frojasg1.sun.reflect.generics.tree.TypeSignature[] parameterTypes;
   private final com.frojasg1.sun.reflect.generics.tree.ReturnType returnType;
   private final com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] exceptionTypes;

   private MethodTypeSignature(com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter[] var1, com.frojasg1.sun.reflect.generics.tree.TypeSignature[] var2, com.frojasg1.sun.reflect.generics.tree.ReturnType var3, com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var4) {
      this.formalTypeParams = var1;
      this.parameterTypes = var2;
      this.returnType = var3;
      this.exceptionTypes = var4;
   }

   public static MethodTypeSignature make(com.frojasg1.sun.reflect.generics.tree.FormalTypeParameter[] var0, com.frojasg1.sun.reflect.generics.tree.TypeSignature[] var1, com.frojasg1.sun.reflect.generics.tree.ReturnType var2, com.frojasg1.sun.reflect.generics.tree.FieldTypeSignature[] var3) {
      return new MethodTypeSignature(var0, var1, var2, var3);
   }

   public FormalTypeParameter[] getFormalTypeParameters() {
      return this.formalTypeParams;
   }

   public TypeSignature[] getParameterTypes() {
      return this.parameterTypes;
   }

   public ReturnType getReturnType() {
      return this.returnType;
   }

   public FieldTypeSignature[] getExceptionTypes() {
      return this.exceptionTypes;
   }

   public void accept(Visitor<?> var1) {
      var1.visitMethodTypeSignature(this);
   }
}
