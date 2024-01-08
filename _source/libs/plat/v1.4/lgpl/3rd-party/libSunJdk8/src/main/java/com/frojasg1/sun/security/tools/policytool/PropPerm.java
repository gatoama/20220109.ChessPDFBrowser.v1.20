package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class PropPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public PropPerm() {
      super("PropertyPermission", "java.util.PropertyPermission", new String[0], new String[]{"read", "write"});
   }
}
