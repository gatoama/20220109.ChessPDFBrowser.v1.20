package com.frojasg1.sun.management;

import java.util.List;
import com.frojasg1.sun.management.counter.Counter;

public interface HotspotRuntimeMBean {
   long getSafepointCount();

   long getTotalSafepointTime();

   long getSafepointSyncTime();

   List<Counter> getInternalRuntimeCounters();
}
