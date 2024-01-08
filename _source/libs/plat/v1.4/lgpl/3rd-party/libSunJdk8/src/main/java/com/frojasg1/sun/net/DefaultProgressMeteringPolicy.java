package com.frojasg1.sun.net;

import com.frojasg1.sun.net.ProgressMeteringPolicy;

import java.net.URL;

class DefaultProgressMeteringPolicy implements ProgressMeteringPolicy {
   DefaultProgressMeteringPolicy() {
   }

   public boolean shouldMeterInput(URL var1, String var2) {
      return false;
   }

   public int getProgressUpdateThreshold() {
      return 8192;
   }
}
