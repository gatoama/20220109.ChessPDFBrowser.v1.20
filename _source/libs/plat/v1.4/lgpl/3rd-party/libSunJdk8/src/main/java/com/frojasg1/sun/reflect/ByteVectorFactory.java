package com.frojasg1.sun.reflect;

import com.frojasg1.sun.reflect.ByteVector;
import com.frojasg1.sun.reflect.ByteVectorImpl;

class ByteVectorFactory {
   ByteVectorFactory() {
   }

   static com.frojasg1.sun.reflect.ByteVector create() {
      return new com.frojasg1.sun.reflect.ByteVectorImpl();
   }

   static com.frojasg1.sun.reflect.ByteVector create(int var0) {
      return new com.frojasg1.sun.reflect.ByteVectorImpl(var0);
   }
}
