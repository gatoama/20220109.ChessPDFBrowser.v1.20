package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.ConstructorAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

class BootstrapConstructorAccessorImpl extends com.frojasg1.sun.reflect.ConstructorAccessorImpl {
   private final Constructor<?> constructor;

   BootstrapConstructorAccessorImpl(Constructor<?> var1) {
      this.constructor = var1;
   }

   public Object newInstance(Object[] var1) throws IllegalArgumentException, InvocationTargetException {
      try {
         return com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl.unsafe.allocateInstance(this.constructor.getDeclaringClass());
      } catch (InstantiationException var3) {
         throw new InvocationTargetException(var3);
      }
   }
}
