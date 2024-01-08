package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class ServicePerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public ServicePerm() {
      super("ServicePermission", "javax.security.auth.kerberos.ServicePermission", new String[0], new String[]{"initiate", "accept"});
   }
}
