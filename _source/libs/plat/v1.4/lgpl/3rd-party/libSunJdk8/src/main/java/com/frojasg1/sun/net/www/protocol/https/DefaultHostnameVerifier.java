package com.frojasg1.sun.net.www.protocol.https;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public final class DefaultHostnameVerifier implements HostnameVerifier {
   public DefaultHostnameVerifier() {
   }

   public boolean verify(String var1, SSLSession var2) {
      return false;
   }
}
