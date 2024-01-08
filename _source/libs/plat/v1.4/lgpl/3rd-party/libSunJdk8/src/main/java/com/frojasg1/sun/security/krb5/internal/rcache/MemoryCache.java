package com.frojasg1.sun.security.krb5.internal.rcache;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.KrbApErrException;
import com.frojasg1.sun.security.krb5.internal.ReplayCache;
import com.frojasg1.sun.security.krb5.internal.rcache.AuthList;
import com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash;

public class MemoryCache extends ReplayCache {
   private static final int lifespan = KerberosTime.getDefaultSkew();
   private static final boolean DEBUG;
   private final Map<String, com.frojasg1.sun.security.krb5.internal.rcache.AuthList> content = new ConcurrentHashMap();

   public MemoryCache() {
   }

   public synchronized void checkAndStore(KerberosTime var1, AuthTimeWithHash var2) throws KrbApErrException {
      String var3 = var2.client + "|" + var2.server;
      ((com.frojasg1.sun.security.krb5.internal.rcache.AuthList)this.content.computeIfAbsent(var3, (var0) -> {
         return new com.frojasg1.sun.security.krb5.internal.rcache.AuthList(lifespan);
      })).put(var2, var1);
      if (DEBUG) {
         System.out.println("MemoryCache: add " + var2 + " to " + var3);
      }

   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.content.values().iterator();

      while(var2.hasNext()) {
         com.frojasg1.sun.security.krb5.internal.rcache.AuthList var3 = (AuthList)var2.next();
         var1.append(var3.toString());
      }

      return var1.toString();
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
