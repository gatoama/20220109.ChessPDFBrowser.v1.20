package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class MBeanPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public MBeanPerm() {
      super("MBeanPermission", "javax.management.MBeanPermission", new String[0], new String[]{"addNotificationListener", "getAttribute", "getClassLoader", "getClassLoaderFor", "getClassLoaderRepository", "getDomains", "getMBeanInfo", "getObjectInstance", "instantiate", "invoke", "isInstanceOf", "queryMBeans", "queryNames", "registerMBean", "removeNotificationListener", "setAttribute", "unregisterMBean"});
   }
}
