package com.frojasg1.sun.management;

import java.util.List;
import java.util.Map;
import com.frojasg1.sun.management.counter.Counter;

public interface HotspotThreadMBean {
   int getInternalThreadCount();

   Map<String, Long> getInternalThreadCpuTimes();

   List<Counter> getInternalThreadingCounters();
}
