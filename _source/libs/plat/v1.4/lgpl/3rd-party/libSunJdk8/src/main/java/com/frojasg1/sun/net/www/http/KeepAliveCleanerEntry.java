package com.frojasg1.sun.net.www.http;

import com.frojasg1.sun.net.www.http.HttpClient;
import com.frojasg1.sun.net.www.http.KeepAliveStream;

class KeepAliveCleanerEntry {
   com.frojasg1.sun.net.www.http.KeepAliveStream kas;
   com.frojasg1.sun.net.www.http.HttpClient hc;

   public KeepAliveCleanerEntry(com.frojasg1.sun.net.www.http.KeepAliveStream var1, com.frojasg1.sun.net.www.http.HttpClient var2) {
      this.kas = var1;
      this.hc = var2;
   }

   protected KeepAliveStream getKeepAliveStream() {
      return this.kas;
   }

   protected HttpClient getHttpClient() {
      return this.hc;
   }

   protected void setQueuedForCleanup() {
      this.kas.queuedForCleanup = true;
   }

   protected boolean getQueuedForCleanup() {
      return this.kas.queuedForCleanup;
   }
}
