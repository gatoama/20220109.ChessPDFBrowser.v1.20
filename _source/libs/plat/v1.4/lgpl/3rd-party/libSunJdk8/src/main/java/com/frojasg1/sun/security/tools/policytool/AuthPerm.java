package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;

class AuthPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public AuthPerm() {
      super("AuthPermission", "javax.security.auth.AuthPermission", new String[]{"doAs", "doAsPrivileged", "getSubject", "getSubjectFromDomainCombiner", "setReadOnly", "modifyPrincipals", "modifyPublicCredentials", "modifyPrivateCredentials", "refreshCredential", "destroyCredential", "createLoginContext.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("name") + ">", "getLoginConfiguration", "setLoginConfiguration", "createLoginConfiguration.<" + PolicyTool.getMessage("configuration.type") + ">", "refreshLoginConfiguration"}, (String[])null);
   }
}
