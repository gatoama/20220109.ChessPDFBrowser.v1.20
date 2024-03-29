package com.frojasg1.sun.management;

import java.util.List;

import com.frojasg1.sun.management.HotspotMemoryMBean;
import com.frojasg1.sun.management.VMManagement;
import com.frojasg1.sun.management.counter.Counter;

class HotspotMemory implements HotspotMemoryMBean {
   private com.frojasg1.sun.management.VMManagement jvm;
   private static final String JAVA_GC = "java.gc.";
   private static final String COM_SUN_GC = "com.sun.gc.";
   private static final String SUN_GC = "sun.gc.";
   private static final String GC_COUNTER_NAME_PATTERN = "java.gc.|com.sun.gc.|com.frojasg1.sun.gc.";

   HotspotMemory(VMManagement var1) {
      this.jvm = var1;
   }

   public List<Counter> getInternalMemoryCounters() {
      return this.jvm.getInternalCounters("java.gc.|com.sun.gc.|com.frojasg1.sun.gc.");
   }
}
