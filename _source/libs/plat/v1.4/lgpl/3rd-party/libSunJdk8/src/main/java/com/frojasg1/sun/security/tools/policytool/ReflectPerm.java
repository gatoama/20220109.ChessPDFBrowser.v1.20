package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class ReflectPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public ReflectPerm() {
      super("ReflectPermission", "java.lang.reflect.ReflectPermission", new String[]{"suppressAccessChecks"}, (String[])null);
   }
}
