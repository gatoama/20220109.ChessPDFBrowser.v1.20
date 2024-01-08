package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class MBeanSvrPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public MBeanSvrPerm() {
      super("MBeanServerPermission", "javax.management.MBeanServerPermission", new String[]{"createMBeanServer", "findMBeanServer", "newMBeanServer", "releaseMBeanServer"}, (String[])null);
   }
}
