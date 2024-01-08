package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class MgmtPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public MgmtPerm() {
      super("ManagementPermission", "java.lang.management.ManagementPermission", new String[]{"control", "monitor"}, (String[])null);
   }
}
