package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.UnsafeQualifiedFieldAccessorImpl;

import java.lang.reflect.Field;

class UnsafeQualifiedDoubleFieldAccessorImpl extends com.frojasg1.sun.reflect.UnsafeQualifiedFieldAccessorImpl {
   UnsafeQualifiedDoubleFieldAccessorImpl(Field var1, boolean var2) {
      super(var1, var2);
   }

   public Object get(Object var1) throws IllegalArgumentException {
      return new Double(this.getDouble(var1));
   }

   public boolean getBoolean(Object var1) throws IllegalArgumentException {
      throw this.newGetBooleanIllegalArgumentException();
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
      this.ensureObj(var1);
      return unsafe.getDoubleVolatile(var1, this.fieldOffset);
   }

   public void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException {
      this.ensureObj(var1);
      if (this.isReadOnly) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      if (var2 == null) {
         this.throwSetIllegalArgumentException(var2);
      }

      if (var2 instanceof Byte) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (double)(Byte)var2);
      } else if (var2 instanceof Short) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (double)(Short)var2);
      } else if (var2 instanceof Character) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (double)(Character)var2);
      } else if (var2 instanceof Integer) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (double)(Integer)var2);
      } else if (var2 instanceof Long) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (double)(Long)var2);
      } else if (var2 instanceof Float) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (double)(Float)var2);
      } else if (var2 instanceof Double) {
         unsafe.putDoubleVolatile(var1, this.fieldOffset, (Double)var2);
      } else {
         this.throwSetIllegalArgumentException(var2);
      }
   }

   public void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException {
      this.setDouble(var1, (double)var2);
   }

   public void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException {
      this.ensureObj(var1);
      if (this.isReadOnly) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      unsafe.putDoubleVolatile(var1, this.fieldOffset, var2);
   }
}
