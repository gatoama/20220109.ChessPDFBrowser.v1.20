package com.frojasg1.sun.management.counter;

import com.frojasg1.sun.management.counter.Counter;

public interface ByteArrayCounter extends Counter {
   byte[] byteArrayValue();

   byte byteAt(int var1);
}
