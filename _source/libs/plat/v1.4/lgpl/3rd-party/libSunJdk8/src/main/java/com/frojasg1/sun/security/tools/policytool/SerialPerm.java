package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class SerialPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public SerialPerm() {
      super("SerializablePermission", "java.io.SerializablePermission", new String[]{"enableSubclassImplementation", "enableSubstitution"}, (String[])null);
   }
}
