package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl;

import java.lang.reflect.Field;

class UnsafeLongFieldAccessorImpl extends com.frojasg1.sun.reflect.UnsafeFieldAccessorImpl {
   UnsafeLongFieldAccessorImpl(Field var1) {
      super(var1);
   }

   public Object get(Object var1) throws IllegalArgumentException {
      return new Long(this.getLong(var1));
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
      this.ensureObj(var1);
      return unsafe.getLong(var1, this.fieldOffset);
   }

   public float getFloat(Object var1) throws IllegalArgumentException {
      return (float)this.getLong(var1);
   }

   public double getDouble(Object var1) throws IllegalArgumentException {
      return (double)this.getLong(var1);
   }

   public void set(Object var1, Object var2) throws IllegalArgumentException, IllegalAccessException {
      this.ensureObj(var1);
      if (this.isFinal) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      if (var2 == null) {
         this.throwSetIllegalArgumentException(var2);
      }

      if (var2 instanceof Byte) {
         unsafe.putLong(var1, this.fieldOffset, (long)(Byte)var2);
      } else if (var2 instanceof Short) {
         unsafe.putLong(var1, this.fieldOffset, (long)(Short)var2);
      } else if (var2 instanceof Character) {
         unsafe.putLong(var1, this.fieldOffset, (long)(Character)var2);
      } else if (var2 instanceof Integer) {
         unsafe.putLong(var1, this.fieldOffset, (long)(Integer)var2);
      } else if (var2 instanceof Long) {
         unsafe.putLong(var1, this.fieldOffset, (Long)var2);
      } else {
         this.throwSetIllegalArgumentException(var2);
      }
   }

   public void setBoolean(Object var1, boolean var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setByte(Object var1, byte var2) throws IllegalArgumentException, IllegalAccessException {
      this.setLong(var1, (long)var2);
   }

   public void setChar(Object var1, char var2) throws IllegalArgumentException, IllegalAccessException {
      this.setLong(var1, (long)var2);
   }

   public void setShort(Object var1, short var2) throws IllegalArgumentException, IllegalAccessException {
      this.setLong(var1, (long)var2);
   }

   public void setInt(Object var1, int var2) throws IllegalArgumentException, IllegalAccessException {
      this.setLong(var1, (long)var2);
   }

   public void setLong(Object var1, long var2) throws IllegalArgumentException, IllegalAccessException {
      this.ensureObj(var1);
      if (this.isFinal) {
         this.throwFinalFieldIllegalAccessException(var2);
      }

      unsafe.putLong(var1, this.fieldOffset, var2);
   }

   public void setFloat(Object var1, float var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }

   public void setDouble(Object var1, double var2) throws IllegalArgumentException, IllegalAccessException {
      this.throwSetIllegalArgumentException(var2);
   }
}
