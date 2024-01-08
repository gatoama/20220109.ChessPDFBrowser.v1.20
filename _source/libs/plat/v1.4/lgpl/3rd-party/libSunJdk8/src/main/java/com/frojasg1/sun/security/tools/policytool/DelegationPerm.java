package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class DelegationPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public DelegationPerm() {
      super("DelegationPermission", "javax.security.auth.kerberos.DelegationPermission", new String[0], (String[])null);
   }
}
