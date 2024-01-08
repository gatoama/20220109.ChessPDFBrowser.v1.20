package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class MBeanTrustPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public MBeanTrustPerm() {
      super("MBeanTrustPermission", "javax.management.MBeanTrustPermission", new String[]{"register"}, (String[])null);
   }
}
