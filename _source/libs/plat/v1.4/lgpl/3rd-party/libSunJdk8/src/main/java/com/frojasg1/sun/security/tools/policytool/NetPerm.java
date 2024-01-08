package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class NetPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public NetPerm() {
      super("NetPermission", "java.net.NetPermission", new String[]{"setDefaultAuthenticator", "requestPasswordAuthentication", "specifyStreamHandler", "setProxySelector", "getProxySelector", "setCookieHandler", "getCookieHandler", "setResponseCache", "getResponseCache"}, (String[])null);
   }
}
