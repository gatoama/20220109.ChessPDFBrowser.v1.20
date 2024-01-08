package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;

class SecurityPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public SecurityPerm() {
      super("SecurityPermission", "java.security.SecurityPermission", new String[]{"createAccessControlContext", "getDomainCombiner", "getPolicy", "setPolicy", "createPolicy.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("policy.type") + ">", "getProperty.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("property.name") + ">", "setProperty.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("property.name") + ">", "insertProvider.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("provider.name") + ">", "removeProvider.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("provider.name") + ">", "clearProviderProperties.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("provider.name") + ">", "putProviderProperty.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("provider.name") + ">", "removeProviderProperty.<" + PolicyTool.getMessage("provider.name") + ">"}, (String[])null);
   }
}
