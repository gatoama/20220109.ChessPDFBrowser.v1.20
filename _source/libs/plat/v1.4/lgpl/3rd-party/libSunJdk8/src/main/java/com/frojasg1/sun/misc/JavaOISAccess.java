package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.ObjectInputFilter;

import java.io.InvalidClassException;
import java.io.ObjectInputStream;

public interface JavaOISAccess {
   void setObjectInputFilter(ObjectInputStream var1, com.frojasg1.sun.misc.ObjectInputFilter var2);

   ObjectInputFilter getObjectInputFilter(ObjectInputStream var1);

   void checkArray(ObjectInputStream var1, Class<?> var2, int var3) throws InvalidClassException;
}
