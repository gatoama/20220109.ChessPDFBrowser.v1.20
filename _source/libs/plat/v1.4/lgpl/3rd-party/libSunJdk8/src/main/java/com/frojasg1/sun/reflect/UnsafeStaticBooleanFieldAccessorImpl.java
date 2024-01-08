package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.UnsafeStaticFieldAccessorImpl;

import java.lang.reflect.Field;

class UnsafeStaticBooleanFieldAccessorImpl extends com.frojasg1.sun.reflect.UnsafeStaticFieldAccessorImpl {
   UnsafeStaticBooleanFieldAccessorImpl(Field var1) {
      super(var1);
   }

   public Object get(Object var1) throws IllegalArgumentException {
      return new Boolean(this.getBoolean(var1));
   }

   public boolean getBoolean(Object var1) throws IllegalArgumentException {
      return unsafe.getBoolean(this.base, this.fieldOffset);
   }

   public byte getByte(Object var1) throws IllegalArgumentException {
      throw this.newGetByteIllegalArgumentException();
   }

   public char getChar(Object var1) throws IllegalArgumentException {
      throw this.newGetCharIllegalArgumentException();
   }

   public short getShort(Object var1) throws IllegalArgumentException {
      throw this.newGetShortIllegalArgumentException();
   }

   public int getInt(Object var1) throws IllegalArgumentException {
      throw this.newGetIntIllegalArgumentException();
   }

   public long getLong(Object var1) throws IllegalArgumentException {
      throw this.newGetLongIllegalArgumentException();
   }

   public float getFloat(Object var1) throws IllegalArgumentException {
      throw this.newGetFloatIllegalArgumentException();
   }

   public double getDouble(Object var1) throws IllegalArgumentException {
      throw this.newGetDoubleIllegalArgumentException();
   }

   public void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException {
      if (this.isFinal) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      if (var2 == null) {
         this.throwSetIllegalArgumentException(var2);
      }

      if (var2 instanceof Boolean) {
         unsafe.putBoolean(this.base, this.fieldOffset, (Boolean)var2);
      } else {
         this.throwSetIllegalArgumentException(var2);
      }
   }

   public void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException {
      if (this.isFinal) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      unsafe.putBoolean(this.base, this.fieldOffset, var2);
   }

   public void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }
}
