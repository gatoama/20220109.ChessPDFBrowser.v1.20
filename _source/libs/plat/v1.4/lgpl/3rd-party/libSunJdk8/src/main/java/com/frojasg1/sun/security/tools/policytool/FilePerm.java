package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class FilePerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public FilePerm() {
      super("FilePermission", "java.io.FilePermission", new String[]{"<<ALL FILES>>"}, new String[]{"read", "write", "delete", "execute"});
   }
}
