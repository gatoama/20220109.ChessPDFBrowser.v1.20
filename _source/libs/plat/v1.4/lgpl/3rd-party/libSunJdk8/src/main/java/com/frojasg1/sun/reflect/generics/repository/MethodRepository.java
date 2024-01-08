package com.frojasg1.sun.reflect.generics.repository;

import java.lang.reflect.Type;
import com.frojasg1.sun.reflect.generics.factory.GenericsFactory;
import com.frojasg1.sun.reflect.generics.repository.ConstructorRepository;
import com.frojasg1.sun.reflect.generics.tree.MethodTypeSignature;
import com.frojasg1.sun.reflect.generics.visitor.Reifier;

public class MethodRepository extends ConstructorRepository {
   private Type returnType;

   private MethodRepository(String var1, GenericsFactory var2) {
      super(var1, var2);
   }

   public static MethodRepository make(String var0, GenericsFactory var1) {
      return new MethodRepository(var0, var1);
   }

   public Type getReturnType() {
      if (this.returnType == null) {
         Reifier var1 = this.getReifier();
         ((MethodTypeSignature)this.getTree()).getReturnType().accept(var1);
         this.returnType = var1.getResult();
      }

      return this.returnType;
   }
}
