package com.frojasg1.sun.management;

import com.frojasg1.sun.management.GarbageCollectorImpl;
import com.frojasg1.sun.management.MemoryManagerImpl;
import com.frojasg1.sun.management.MemoryPoolImpl;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;

class ManagementFactory {
   private ManagementFactory() {
   }

   private static MemoryPoolMXBean createMemoryPool(String var0, boolean var1, long var2, long var4) {
      return new com.frojasg1.sun.management.MemoryPoolImpl(var0, var1, var2, var4);
   }

   private static MemoryManagerMXBean createMemoryManager(String var0) {
      return new com.frojasg1.sun.management.MemoryManagerImpl(var0);
   }

   private static GarbageCollectorMXBean createGarbageCollector(String var0, String var1) {
      return new com.frojasg1.sun.management.GarbageCollectorImpl(var0);
   }
}
