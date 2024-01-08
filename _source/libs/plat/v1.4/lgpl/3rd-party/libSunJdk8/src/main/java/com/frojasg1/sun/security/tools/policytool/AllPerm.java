package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class AllPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public AllPerm() {
      super("AllPermission", "java.security.AllPermission", (String[])null, (String[])null);
   }
}
