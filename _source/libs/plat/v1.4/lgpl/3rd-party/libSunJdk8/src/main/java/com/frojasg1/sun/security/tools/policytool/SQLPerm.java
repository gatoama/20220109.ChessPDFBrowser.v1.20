package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class SQLPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public SQLPerm() {
      super("SQLPermission", "java.sql.SQLPermission", new String[]{"setLog", "callAbort", "setSyncFactory", "setNetworkTimeout"}, (String[])null);
   }
}
