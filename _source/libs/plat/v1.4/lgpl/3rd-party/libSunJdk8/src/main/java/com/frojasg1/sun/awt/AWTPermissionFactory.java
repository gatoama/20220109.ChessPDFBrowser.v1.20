package com.frojasg1.sun.awt;

import java.awt.AWTPermission;
import com.frojasg1.sun.security.util.PermissionFactory;

public class AWTPermissionFactory implements PermissionFactory<AWTPermission> {
   public AWTPermissionFactory() {
   }

   public AWTPermission newPermission(String var1) {
      return new AWTPermission(var1);
   }
}
