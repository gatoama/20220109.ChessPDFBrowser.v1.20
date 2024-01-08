package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class SocketPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public SocketPerm() {
      super("SocketPermission", "java.net.SocketPermission", new String[0], new String[]{"accept", "connect", "listen", "resolve"});
   }
}
