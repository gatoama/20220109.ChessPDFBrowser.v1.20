package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class PrivCredPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public PrivCredPerm() {
      super("PrivateCredentialPermission", "javax.security.auth.PrivateCredentialPermission", new String[0], new String[]{"read"});
   }
}
