package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.UnsafeStaticFieldAccessorImpl;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedStaticFieldAccessorImpl extends com.frojasg1.sun.reflect.UnsafeStaticFieldAccessorImpl {
   protected final boolean isReadOnly;

   UnsafeQualifiedStaticFieldAccessorImpl(Field var1, boolean var2) {
      super(var1);
      this.isReadOnly = var2;
   }
}
