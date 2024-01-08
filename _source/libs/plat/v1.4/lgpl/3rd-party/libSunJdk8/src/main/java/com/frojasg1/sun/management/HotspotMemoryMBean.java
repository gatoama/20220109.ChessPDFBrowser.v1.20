package com.frojasg1.sun.management;

import java.util.List;
import com.frojasg1.sun.management.counter.Counter;

public interface HotspotMemoryMBean {
   List<Counter> getInternalMemoryCounters();
}
