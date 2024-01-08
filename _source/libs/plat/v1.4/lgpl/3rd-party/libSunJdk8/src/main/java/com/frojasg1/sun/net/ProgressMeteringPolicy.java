package com.frojasg1.sun.net;

import java.net.URL;

public interface ProgressMeteringPolicy {
   boolean shouldMeterInput(URL var1, String var2);

   int getProgressUpdateThreshold();
}
