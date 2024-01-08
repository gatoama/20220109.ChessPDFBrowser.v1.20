package com.frojasg1.sun.security.krb5.internal.ccache;

import java.io.File;
import java.io.IOException;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.internal.ccache.Credentials;
import com.frojasg1.sun.security.krb5.internal.ccache.CredentialsCache;

public abstract class MemoryCredentialsCache extends com.frojasg1.sun.security.krb5.internal.ccache.CredentialsCache {
   public MemoryCredentialsCache() {
   }

   private static com.frojasg1.sun.security.krb5.internal.ccache.CredentialsCache getCCacheInstance(PrincipalName var0) {
      return null;
   }

   private static CredentialsCache getCCacheInstance(PrincipalName var0, File var1) {
      return null;
   }

   public abstract boolean exists(String var1);

   public abstract void update(com.frojasg1.sun.security.krb5.internal.ccache.Credentials var1);

   public abstract void save() throws IOException, KrbException;

   public abstract com.frojasg1.sun.security.krb5.internal.ccache.Credentials[] getCredsList();

   public abstract Credentials getCreds(PrincipalName var1);

   public abstract PrincipalName getPrimaryPrincipal();
}
