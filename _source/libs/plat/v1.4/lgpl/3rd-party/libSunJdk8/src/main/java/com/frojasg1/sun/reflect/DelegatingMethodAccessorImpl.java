package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.MethodAccessorImpl;

import java.lang.reflect.InvocationTargetException;

class DelegatingMethodAccessorImpl extends com.frojasg1.sun.reflect.MethodAccessorImpl {
   private com.frojasg1.sun.reflect.MethodAccessorImpl delegate;

   DelegatingMethodAccessorImpl(com.frojasg1.sun.reflect.MethodAccessorImpl var1) {
      this.setDelegate(var1);
   }

   public Object invoke(Object var1, Object[] var2) throws IllegalArgumentException, InvocationTargetException {
      return this.delegate.invoke(var1, var2);
   }

   void setDelegate(com.frojasg1.sun.reflect.MethodAccessorImpl var1) {
      this.delegate = var1;
   }
}
