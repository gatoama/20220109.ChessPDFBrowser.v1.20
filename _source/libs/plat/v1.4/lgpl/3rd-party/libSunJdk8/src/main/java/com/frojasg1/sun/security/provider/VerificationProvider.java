package com.frojasg1.sun.security.provider;

import java.security.AccessController;
import java.security.Provider;
import java.util.LinkedHashMap;
import com.frojasg1.sun.security.action.PutAllAction;
import com.frojasg1.sun.security.provider.SunEntries;
import com.frojasg1.sun.security.rsa.SunRsaSignEntries;

public final class VerificationProvider extends Provider {
   private static final long serialVersionUID = 7482667077568930381L;
   private static final boolean ACTIVE;

   public VerificationProvider() {
      super("SunJarVerification", 1.8D, "Jar Verification Provider");
      if (ACTIVE) {
         if (System.getSecurityManager() == null) {
            com.frojasg1.sun.security.provider.SunEntries.putEntries(this);
            SunRsaSignEntries.putEntries(this);
         } else {
            LinkedHashMap var1 = new LinkedHashMap();
            com.frojasg1.sun.security.provider.SunEntries.putEntries(var1);
            SunRsaSignEntries.putEntries(var1);
            AccessController.doPrivileged(new PutAllAction(this, var1));
         }

      }
   }

   static {
      boolean var0;
      try {
         Class.forName("sun.security.provider.Sun");
         Class.forName("sun.security.rsa.SunRsaSign");
         var0 = false;
      } catch (ClassNotFoundException var2) {
         var0 = true;
      }

      ACTIVE = var0;
   }
}
