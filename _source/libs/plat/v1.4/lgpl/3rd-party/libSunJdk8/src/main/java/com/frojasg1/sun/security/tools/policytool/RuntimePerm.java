package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;

class RuntimePerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public RuntimePerm() {
      super("RuntimePermission", "java.lang.RuntimePermission", new String[]{"createClassLoader", "getClassLoader", "setContextClassLoader", "enableContextClassLoaderOverride", "setSecurityManager", "createSecurityManager", "getenv.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("environment.variable.name") + ">", "exitVM", "shutdownHooks", "setFactory", "setIO", "modifyThread", "stopThread", "modifyThreadGroup", "getProtectionDomain", "readFileDescriptor", "writeFileDescriptor", "loadLibrary.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("library.name") + ">", "accessClassInPackage.<" + com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("package.name") + ">", "defineClassInPackage.<" + PolicyTool.getMessage("package.name") + ">", "accessDeclaredMembers", "queuePrintJob", "getStackTrace", "setDefaultUncaughtExceptionHandler", "preferences", "usePolicy"}, (String[])null);
   }
}
