package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.MagicAccessorImpl;
import com.frojasg1.sun.reflect.MethodAccessor;

import java.lang.reflect.InvocationTargetException;

abstract class MethodAccessorImpl extends com.frojasg1.sun.reflect.MagicAccessorImpl implements MethodAccessor {
   MethodAccessorImpl() {
   }

   public abstract Object invoke(Object var1, Object[] var2) throws IllegalArgumentException, InvocationTargetException;
}
