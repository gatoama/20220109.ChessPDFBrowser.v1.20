package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;

class URLPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public URLPerm() {
      super("URLPermission", "java.net.URLPermission", new String[]{"<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("url") + ">"}, new String[]{"<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("method.list") + ">:<" + PolicyTool.getMessage("request.headers.list") + ">"});
   }
}
