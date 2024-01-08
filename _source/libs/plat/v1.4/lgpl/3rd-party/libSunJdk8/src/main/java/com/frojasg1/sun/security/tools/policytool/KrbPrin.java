package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Prin;

class KrbPrin extends com.frojasg1.sun.security.tools.policytool.Prin {
   public KrbPrin() {
      super("KerberosPrincipal", "javax.security.auth.kerberos.KerberosPrincipal");
   }
}
