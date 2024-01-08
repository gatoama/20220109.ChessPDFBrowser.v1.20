package com.frojasg1.sun.security.krb5.internal;

import java.security.AccessController;
import com.frojasg1.sun.security.action.GetPropertyAction;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.KrbApErrException;
import com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash;
import com.frojasg1.sun.security.krb5.internal.rcache.DflCache;
import com.frojasg1.sun.security.krb5.internal.rcache.MemoryCache;

public abstract class ReplayCache {
   public ReplayCache() {
   }

   public static ReplayCache getInstance(String var0) {
      if (var0 == null) {
         return new MemoryCache();
      } else if (!var0.equals("dfl") && !var0.startsWith("dfl:")) {
         if (var0.equals("none")) {
            return new ReplayCache() {
               public void checkAndStore(com.frojasg1.sun.security.krb5.internal.KerberosTime var1, AuthTimeWithHash var2) throws com.frojasg1.sun.security.krb5.internal.KrbApErrException {
               }
            };
         } else {
            throw new IllegalArgumentException("Unknown type: " + var0);
         }
      } else {
         return new DflCache(var0);
      }
   }

   public static ReplayCache getInstance() {
      String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.security.krb5.rcache"));
      return getInstance(var0);
   }

   public abstract void checkAndStore(KerberosTime var1, AuthTimeWithHash var2) throws KrbApErrException;
}
