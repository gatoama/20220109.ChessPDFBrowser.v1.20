package com.frojasg1.sun.security.krb5;

import javax.security.auth.kerberos.KeyTab;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.security.krb5.JavaxSecurityAuthKerberosAccess;

public class KerberosSecrets {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static com.frojasg1.sun.security.krb5.JavaxSecurityAuthKerberosAccess javaxSecurityAuthKerberosAccess;

   public KerberosSecrets() {
   }

   public static void setJavaxSecurityAuthKerberosAccess(com.frojasg1.sun.security.krb5.JavaxSecurityAuthKerberosAccess var0) {
      javaxSecurityAuthKerberosAccess = var0;
   }

   public static JavaxSecurityAuthKerberosAccess getJavaxSecurityAuthKerberosAccess() {
      if (javaxSecurityAuthKerberosAccess == null) {
         unsafe.ensureClassInitialized(KeyTab.class);
      }

      return javaxSecurityAuthKerberosAccess;
   }
}
