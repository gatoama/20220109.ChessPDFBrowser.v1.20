package com.frojasg1.sun.corba;

import com.sun.corba.se.impl.io.ValueUtility;
import java.lang.reflect.Method;

import com.frojasg1.sun.corba.JavaCorbaAccess;
import com.frojasg1.sun.misc.JavaOISAccess;
import com.frojasg1.sun.misc.Unsafe;

public class SharedSecrets {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static com.frojasg1.sun.corba.JavaCorbaAccess javaCorbaAccess;
   private static final Method getJavaOISAccessMethod;
   private static JavaOISAccess javaOISAccess;

   public SharedSecrets() {
   }

   public static JavaOISAccess getJavaOISAccess() {
      if (javaOISAccess == null) {
         try {
            javaOISAccess = (JavaOISAccess)getJavaOISAccessMethod.invoke((Object)null);
         } catch (Exception var1) {
            throw new ExceptionInInitializerError(var1);
         }
      }

      return javaOISAccess;
   }

   public static com.frojasg1.sun.corba.JavaCorbaAccess getJavaCorbaAccess() {
      if (javaCorbaAccess == null) {
         unsafe.ensureClassInitialized(ValueUtility.class);
      }

      return javaCorbaAccess;
   }

   public static void setJavaCorbaAccess(JavaCorbaAccess var0) {
      javaCorbaAccess = var0;
   }

   static {
      try {
         Class var0 = Class.forName("sun.misc.SharedSecrets");
         getJavaOISAccessMethod = var0.getMethod("getJavaOISAccess");
      } catch (Exception var1) {
         throw new ExceptionInInitializerError(var1);
      }
   }
}
