package com.frojasg1.sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.LongBuffer;
import com.frojasg1.sun.management.counter.AbstractCounter;
import com.frojasg1.sun.management.counter.LongCounter;
import com.frojasg1.sun.management.counter.Units;
import com.frojasg1.sun.management.counter.Variability;
import com.frojasg1.sun.management.counter.perf.LongCounterSnapshot;

public class PerfLongCounter extends AbstractCounter implements LongCounter {
   LongBuffer lb;
   private static final long serialVersionUID = 857711729279242948L;

   PerfLongCounter(String var1, Units var2, Variability var3, int var4, LongBuffer var5) {
      super(var1, var2, var3, var4);
      this.lb = var5;
   }

   public Object getValue() {
      return new Long(this.lb.get(0));
   }

   public long longValue() {
      return this.lb.get(0);
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new com.frojasg1.sun.management.counter.perf.LongCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.longValue());
   }
}
