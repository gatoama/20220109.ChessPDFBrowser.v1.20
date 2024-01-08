package com.frojasg1.sun.awt;

import com.frojasg1.sun.awt.AppContext;

public class AWTSecurityManager extends SecurityManager {
   public AWTSecurityManager() {
   }

   public AppContext getAppContext() {
      return null;
   }
}
