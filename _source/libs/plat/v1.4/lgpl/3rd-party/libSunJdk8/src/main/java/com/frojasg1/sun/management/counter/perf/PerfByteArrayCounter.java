package com.frojasg1.sun.management.counter.perf;

import java.io.ObjectStreamException;
import java.nio.ByteBuffer;
import com.frojasg1.sun.management.counter.AbstractCounter;
import com.frojasg1.sun.management.counter.ByteArrayCounter;
import com.frojasg1.sun.management.counter.Units;
import com.frojasg1.sun.management.counter.Variability;
import com.frojasg1.sun.management.counter.perf.ByteArrayCounterSnapshot;

public class PerfByteArrayCounter extends AbstractCounter implements ByteArrayCounter {
   ByteBuffer bb;
   private static final long serialVersionUID = 2545474036937279921L;

   PerfByteArrayCounter(String var1, Units var2, Variability var3, int var4, int var5, ByteBuffer var6) {
      super(var1, var2, var3, var4, var5);
      this.bb = var6;
   }

   public Object getValue() {
      return this.byteArrayValue();
   }

   public byte[] byteArrayValue() {
      this.bb.position(0);
      byte[] var1 = new byte[this.bb.limit()];
      this.bb.get(var1);
      return var1;
   }

   public byte byteAt(int var1) {
      this.bb.position(var1);
      return this.bb.get();
   }

   public String toString() {
      String var1 = this.getName() + ": " + new String(this.byteArrayValue()) + " " + this.getUnits();
      return this.isInternal() ? var1 + " [INTERNAL]" : var1;
   }

   protected Object writeReplace() throws ObjectStreamException {
      return new com.frojasg1.sun.management.counter.perf.ByteArrayCounterSnapshot(this.getName(), this.getUnits(), this.getVariability(), this.getFlags(), this.getVectorLength(), this.byteArrayValue());
   }
}
