package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.ConstructorAccessor;
import com.frojasg1.sun.reflect.MagicAccessorImpl;

import java.lang.reflect.InvocationTargetException;

abstract class ConstructorAccessorImpl extends com.frojasg1.sun.reflect.MagicAccessorImpl implements ConstructorAccessor {
   ConstructorAccessorImpl() {
   }

   public abstract Object newInstance(Object[] var1) throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}
