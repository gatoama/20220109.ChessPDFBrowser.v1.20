package com.frojasg1.sun.management.counter;

import com.frojasg1.sun.management.counter.Counter;

public interface LongArrayCounter extends Counter {
   long[] longArrayValue();

   long longAt(int var1);
}
