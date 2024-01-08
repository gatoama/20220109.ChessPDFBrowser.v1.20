package com.frojasg1.sun.net.www.http;

import com.frojasg1.sun.net.www.http.HttpClient;

class KeepAliveEntry {
   com.frojasg1.sun.net.www.http.HttpClient hc;
   long idleStartTime;

   KeepAliveEntry(HttpClient var1, long var2) {
      this.hc = var1;
      this.idleStartTime = var2;
   }
}
