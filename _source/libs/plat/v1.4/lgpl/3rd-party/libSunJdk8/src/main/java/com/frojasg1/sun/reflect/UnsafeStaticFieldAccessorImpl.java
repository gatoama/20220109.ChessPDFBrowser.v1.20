package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.Reflection;
import com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl;

import java.lang.reflect.Field;

abstract class UnsafeStaticFieldAccessorImpl extends com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl {
   protected final Object base;

   UnsafeStaticFieldAccessorImpl(Field var1) {
      super(var1);
      this.base = unsafe.staticFieldBase(var1);
   }

   static {
      Reflection.registerFieldsToFilter(UnsafeStaticFieldAccessorImpl.class, "base");
   }
}
