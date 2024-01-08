package com.frojasg1.sun.reflect.generics.scope;

import com.frojasg1.sun.reflect.generics.scope.AbstractScope;
import com.frojasg1.sun.reflect.generics.scope.ConstructorScope;
import com.frojasg1.sun.reflect.generics.scope.DummyScope;
import com.frojasg1.sun.reflect.generics.scope.MethodScope;
import com.frojasg1.sun.reflect.generics.scope.Scope;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ClassScope extends AbstractScope<Class<?>> implements com.frojasg1.sun.reflect.generics.scope.Scope {
   private ClassScope(Class<?> var1) {
      super(var1);
   }

   protected com.frojasg1.sun.reflect.generics.scope.Scope computeEnclosingScope() {
      Class var1 = (Class)this.getRecvr();
      Method var2 = var1.getEnclosingMethod();
      if (var2 != null) {
         return MethodScope.make(var2);
      } else {
         Constructor var3 = var1.getEnclosingConstructor();
         if (var3 != null) {
            return ConstructorScope.make(var3);
         } else {
            Class var4 = var1.getEnclosingClass();
            return (Scope)(var4 != null ? make(var4) : DummyScope.make());
         }
      }
   }

   public static ClassScope make(Class<?> var0) {
      return new ClassScope(var0);
   }
}
