package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.FieldAccessor;
import com.frojasg1.sun.reflect.UnsafeBooleanFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeByteFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeCharacterFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeDoubleFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeFloatFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeIntegerFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeLongFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeObjectFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedBooleanFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedByteFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedCharacterFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedDoubleFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedFloatFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedIntegerFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedLongFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedObjectFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedShortFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticBooleanFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticByteFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticCharacterFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticDoubleFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticFloatFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticIntegerFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticLongFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticObjectFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeQualifiedStaticShortFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeShortFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticBooleanFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticByteFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticCharacterFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticDoubleFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticFloatFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticIntegerFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticLongFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticObjectFieldAccessorImpl;
import com.frojasg1.sun.reflect.UnsafeStaticShortFieldAccessorImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class UnsafeFieldAccessorFactory {
   UnsafeFieldAccessorFactory() {
   }

   static com.frojasg1.sun.reflect.FieldAccessor newFieldAccessor(Field var0, boolean var1) {
      Class var2 = var0.getType();
      boolean var3 = Modifier.isStatic(var0.getModifiers());
      boolean var4 = Modifier.isFinal(var0.getModifiers());
      boolean var5 = Modifier.isVolatile(var0.getModifiers());
      boolean var6 = var4 || var5;
      boolean var7 = var4 && (var3 || !var1);
      if (var3) {
         com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl.unsafe.ensureClassInitialized(var0.getDeclaringClass());
         if (!var6) {
            if (var2 == Boolean.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticBooleanFieldAccessorImpl(var0);
            } else if (var2 == Byte.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticByteFieldAccessorImpl(var0);
            } else if (var2 == Short.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticShortFieldAccessorImpl(var0);
            } else if (var2 == Character.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticCharacterFieldAccessorImpl(var0);
            } else if (var2 == Integer.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticIntegerFieldAccessorImpl(var0);
            } else if (var2 == Long.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticLongFieldAccessorImpl(var0);
            } else if (var2 == Float.TYPE) {
               return new com.frojasg1.sun.reflect.UnsafeStaticFloatFieldAccessorImpl(var0);
            } else {
               return (com.frojasg1.sun.reflect.FieldAccessor)(var2 == Double.TYPE ? new com.frojasg1.sun.reflect.UnsafeStaticDoubleFieldAccessorImpl(var0) : new com.frojasg1.sun.reflect.UnsafeStaticObjectFieldAccessorImpl(var0));
            }
         } else if (var2 == Boolean.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticBooleanFieldAccessorImpl(var0, var7);
         } else if (var2 == Byte.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticByteFieldAccessorImpl(var0, var7);
         } else if (var2 == Short.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticShortFieldAccessorImpl(var0, var7);
         } else if (var2 == Character.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticCharacterFieldAccessorImpl(var0, var7);
         } else if (var2 == Integer.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticIntegerFieldAccessorImpl(var0, var7);
         } else if (var2 == Long.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticLongFieldAccessorImpl(var0, var7);
         } else if (var2 == Float.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeQualifiedStaticFloatFieldAccessorImpl(var0, var7);
         } else {
            return (com.frojasg1.sun.reflect.FieldAccessor)(var2 == Double.TYPE ? new com.frojasg1.sun.reflect.UnsafeQualifiedStaticDoubleFieldAccessorImpl(var0, var7) : new com.frojasg1.sun.reflect.UnsafeQualifiedStaticObjectFieldAccessorImpl(var0, var7));
         }
      } else if (!var6) {
         if (var2 == Boolean.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeBooleanFieldAccessorImpl(var0);
         } else if (var2 == Byte.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeByteFieldAccessorImpl(var0);
         } else if (var2 == Short.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeShortFieldAccessorImpl(var0);
         } else if (var2 == Character.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeCharacterFieldAccessorImpl(var0);
         } else if (var2 == Integer.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeIntegerFieldAccessorImpl(var0);
         } else if (var2 == Long.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeLongFieldAccessorImpl(var0);
         } else if (var2 == Float.TYPE) {
            return new com.frojasg1.sun.reflect.UnsafeFloatFieldAccessorImpl(var0);
         } else {
            return (com.frojasg1.sun.reflect.FieldAccessor)(var2 == Double.TYPE ? new com.frojasg1.sun.reflect.UnsafeDoubleFieldAccessorImpl(var0) : new com.frojasg1.sun.reflect.UnsafeObjectFieldAccessorImpl(var0));
         }
      } else if (var2 == Boolean.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedBooleanFieldAccessorImpl(var0, var7);
      } else if (var2 == Byte.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedByteFieldAccessorImpl(var0, var7);
      } else if (var2 == Short.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedShortFieldAccessorImpl(var0, var7);
      } else if (var2 == Character.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedCharacterFieldAccessorImpl(var0, var7);
      } else if (var2 == Integer.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedIntegerFieldAccessorImpl(var0, var7);
      } else if (var2 == Long.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedLongFieldAccessorImpl(var0, var7);
      } else if (var2 == Float.TYPE) {
         return new com.frojasg1.sun.reflect.UnsafeQualifiedFloatFieldAccessorImpl(var0, var7);
      } else {
         return (FieldAccessor)(var2 == Double.TYPE ? new com.frojasg1.sun.reflect.UnsafeQualifiedDoubleFieldAccessorImpl(var0, var7) : new com.frojasg1.sun.reflect.UnsafeQualifiedObjectFieldAccessorImpl(var0, var7));
      }
   }
}
