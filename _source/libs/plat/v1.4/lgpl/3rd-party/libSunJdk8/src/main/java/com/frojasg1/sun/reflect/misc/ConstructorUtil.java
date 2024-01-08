package com.frojasg1.sun.reflect.misc;

import com.frojasg1.sun.reflect.misc.ReflectUtil;

import java.lang.reflect.Constructor;

public final class ConstructorUtil {
   private ConstructorUtil() {
   }

   public static Constructor<?> getConstructor(Class<?> var0, Class<?>[] var1) throws NoSuchMethodException {
      com.frojasg1.sun.reflect.misc.ReflectUtil.checkPackageAccess(var0);
      return var0.getConstructor(var1);
   }

   public static Constructor<?>[] getConstructors(Class<?> var0) {
      ReflectUtil.checkPackageAccess(var0);
      return var0.getConstructors();
   }
}
