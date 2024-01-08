package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl;

import java.lang.reflect.Field;

abstract class UnsafeQualifiedFieldAccessorImpl extends com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl {
   protected final boolean isReadOnly;

   UnsafeQualifiedFieldAccessorImpl(Field var1, boolean var2) {
      super(var1);
      this.isReadOnly = var2;
   }
}
