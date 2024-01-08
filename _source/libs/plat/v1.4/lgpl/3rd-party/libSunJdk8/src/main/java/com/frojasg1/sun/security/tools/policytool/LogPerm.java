package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class LogPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public LogPerm() {
      super("LoggingPermission", "java.util.logging.LoggingPermission", new String[]{"control"}, (String[])null);
   }
}
