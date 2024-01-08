package com.frojasg1.sun.management.counter.perf;

import com.frojasg1.sun.management.counter.AbstractCounter;
import com.frojasg1.sun.management.counter.LongCounter;
import com.frojasg1.sun.management.counter.Units;
import com.frojasg1.sun.management.counter.Variability;

class LongCounterSnapshot extends AbstractCounter implements LongCounter {
   long value;
   private static final long serialVersionUID = 2054263861474565758L;

   LongCounterSnapshot(String var1, Units var2, Variability var3, int var4, long var5) {
      super(var1, var2, var3, var4);
      this.value = var5;
   }

   public Object getValue() {
      return new Long(this.value);
   }

   public long longValue() {
      return this.value;
   }
}
