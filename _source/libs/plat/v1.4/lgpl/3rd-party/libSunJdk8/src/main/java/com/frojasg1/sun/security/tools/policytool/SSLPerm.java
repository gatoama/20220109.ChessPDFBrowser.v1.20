package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class SSLPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public SSLPerm() {
      super("SSLPermission", "javax.net.ssl.SSLPermission", new String[]{"setHostnameVerifier", "getSSLSessionContext"}, (String[])null);
   }
}
