package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.ConstructorAccessorImpl;

import java.lang.reflect.InvocationTargetException;

class DelegatingConstructorAccessorImpl extends com.frojasg1.sun.reflect.ConstructorAccessorImpl {
   private com.frojasg1.sun.reflect.ConstructorAccessorImpl delegate;

   DelegatingConstructorAccessorImpl(com.frojasg1.sun.reflect.ConstructorAccessorImpl var1) {
      this.setDelegate(var1);
   }

   public Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException {
      return this.delegate.newInstance(var1);
   }

   void setDelegate(com.frojasg1.sun.reflect.ConstructorAccessorImpl var1) {
      this.delegate = var1;
   }
}
