package com.frojasg1.sun.tracing.dtrace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import com.frojasg1.sun.tracing.ProbeSkeleton;
import com.frojasg1.sun.tracing.dtrace.DTraceProvider;
import com.frojasg1.sun.tracing.dtrace.JVM;

class DTraceProbe extends ProbeSkeleton {
   private Object proxy;
   private Method declared_method;
   private Method implementing_method;

   DTraceProbe(Object var1, Method var2) {
      super(var2.getParameterTypes());
      this.proxy = var1;
      this.declared_method = var2;

      try {
         this.implementing_method = var1.getClass().getMethod(var2.getName(), var2.getParameterTypes());
      } catch (NoSuchMethodException var4) {
         throw new RuntimeException("Internal error, wrong proxy class");
      }
   }

   public boolean isEnabled() {
      return com.frojasg1.sun.tracing.dtrace.JVM.isEnabled(this.implementing_method);
   }

   public void uncheckedTrigger(Object[] var1) {
      try {
         this.implementing_method.invoke(this.proxy, var1);
      } catch (IllegalAccessException var3) {
         assert false;
      } catch (InvocationTargetException var4) {
         assert false;
      }

   }

   String getProbeName() {
      return com.frojasg1.sun.tracing.dtrace.DTraceProvider.getProbeName(this.declared_method);
   }

   String getFunctionName() {
      return com.frojasg1.sun.tracing.dtrace.DTraceProvider.getFunctionName(this.declared_method);
   }

   Method getMethod() {
      return this.implementing_method;
   }

   Class<?>[] getParameterTypes() {
      return this.parameters;
   }
}
