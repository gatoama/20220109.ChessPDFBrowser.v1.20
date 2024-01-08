package com.frojasg1.sun.security.jgss;

import com.frojasg1.sun.net.www.protocol.http.HttpCallerInfo;
import com.frojasg1.sun.security.jgss.GSSCaller;

public class HttpCaller extends GSSCaller {
   private final HttpCallerInfo hci;

   public HttpCaller(HttpCallerInfo var1) {
      super("HTTP_CLIENT");
      this.hci = var1;
   }

   public HttpCallerInfo info() {
      return this.hci;
   }
}
