package com.frojasg1.sun.management;

import java.util.List;

import com.frojasg1.sun.management.HotspotRuntimeMBean;
import com.frojasg1.sun.management.VMManagement;
import com.frojasg1.sun.management.counter.Counter;

class HotspotRuntime implements HotspotRuntimeMBean {
   private com.frojasg1.sun.management.VMManagement jvm;
   private static final String JAVA_RT = "java.rt.";
   private static final String COM_SUN_RT = "com.sun.rt.";
   private static final String SUN_RT = "sun.rt.";
   private static final String JAVA_PROPERTY = "java.property.";
   private static final String COM_SUN_PROPERTY = "com.sun.property.";
   private static final String SUN_PROPERTY = "sun.property.";
   private static final String RT_COUNTER_NAME_PATTERN = "java.rt.|com.sun.rt.|com.frojasg1.sun.rt.|java.property.|com.sun.property.|com.frojasg1.sun.property.";

   HotspotRuntime(VMManagement var1) {
      this.jvm = var1;
   }

   public long getSafepointCount() {
      return this.jvm.getSafepointCount();
   }

   public long getTotalSafepointTime() {
      return this.jvm.getTotalSafepointTime();
   }

   public long getSafepointSyncTime() {
      return this.jvm.getSafepointSyncTime();
   }

   public List<Counter> getInternalRuntimeCounters() {
      return this.jvm.getInternalCounters("java.rt.|com.sun.rt.|com.frojasg1.sun.rt.|java.property.|com.sun.property.|com.frojasg1.sun.property.");
   }
}
