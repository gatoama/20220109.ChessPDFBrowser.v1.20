package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class SubjDelegPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public SubjDelegPerm() {
      super("SubjectDelegationPermission", "javax.management.remote.SubjectDelegationPermission", new String[0], (String[])null);
   }
}
