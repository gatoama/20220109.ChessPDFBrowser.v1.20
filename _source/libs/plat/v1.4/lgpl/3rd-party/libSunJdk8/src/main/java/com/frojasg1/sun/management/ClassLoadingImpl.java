package com.frojasg1.sun.management;

import com.frojasg1.sun.management.Util;
import com.frojasg1.sun.management.VMManagement;

import java.lang.management.ClassLoadingMXBean;
import javax.management.ObjectName;

class ClassLoadingImpl implements ClassLoadingMXBean {
   private final com.frojasg1.sun.management.VMManagement jvm;

   ClassLoadingImpl(VMManagement var1) {
      this.jvm = var1;
   }

   public long getTotalLoadedClassCount() {
      return this.jvm.getTotalClassCount();
   }

   public int getLoadedClassCount() {
      return this.jvm.getLoadedClassCount();
   }

   public long getUnloadedClassCount() {
      return this.jvm.getUnloadedClassCount();
   }

   public boolean isVerbose() {
      return this.jvm.getVerboseClass();
   }

   public void setVerbose(boolean var1) {
      com.frojasg1.sun.management.Util.checkControlAccess();
      setVerboseClass(var1);
   }

   static native void setVerboseClass(boolean var0);

   public ObjectName getObjectName() {
      return Util.newObjectName("java.lang:type=ClassLoading");
   }
}
