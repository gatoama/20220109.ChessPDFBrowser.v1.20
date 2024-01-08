package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class InqSecContextPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public InqSecContextPerm() {
      super("InquireSecContextPermission", "com.sun.security.jgss.InquireSecContextPermission", new String[]{"KRB5_GET_SESSION_KEY", "KRB5_GET_TKT_FLAGS", "KRB5_GET_AUTHZ_DATA", "KRB5_GET_AUTHTIME"}, (String[])null);
   }
}
