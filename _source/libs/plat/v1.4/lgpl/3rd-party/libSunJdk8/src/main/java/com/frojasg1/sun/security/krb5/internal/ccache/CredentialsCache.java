package com.frojasg1.sun.security.krb5.internal.ccache;

import java.io.IOException;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.LoginOptions;
import com.frojasg1.sun.security.krb5.internal.ccache.Credentials;
import com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache;

public abstract class CredentialsCache {
   static CredentialsCache singleton = null;
   static String cacheName;
   private static boolean DEBUG;

   public CredentialsCache() {
   }

   public static CredentialsCache getInstance(PrincipalName var0) {
      return com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.acquireInstance(var0, (String)null);
   }

   public static CredentialsCache getInstance(String var0) {
      return var0.length() >= 5 && var0.substring(0, 5).equalsIgnoreCase("FILE:") ? com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.acquireInstance((PrincipalName)null, var0.substring(5)) : com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.acquireInstance((PrincipalName)null, var0);
   }

   public static CredentialsCache getInstance(PrincipalName var0, String var1) {
      return var1 != null && var1.length() >= 5 && var1.regionMatches(true, 0, "FILE:", 0, 5) ? com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.acquireInstance(var0, var1.substring(5)) : com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.acquireInstance(var0, var1);
   }

   public static CredentialsCache getInstance() {
      return com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.acquireInstance();
   }

   public static CredentialsCache create(PrincipalName var0, String var1) {
      if (var1 == null) {
         throw new RuntimeException("cache name error");
      } else if (var1.length() >= 5 && var1.regionMatches(true, 0, "FILE:", 0, 5)) {
         var1 = var1.substring(5);
         return com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.New(var0, var1);
      } else {
         return com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache.New(var0, var1);
      }
   }

   public static CredentialsCache create(PrincipalName var0) {
      return FileCredentialsCache.New(var0);
   }

   public static String cacheName() {
      return cacheName;
   }

   public abstract PrincipalName getPrimaryPrincipal();

   public abstract void update(com.frojasg1.sun.security.krb5.internal.ccache.Credentials var1);

   public abstract void save() throws IOException, KrbException;

   public abstract com.frojasg1.sun.security.krb5.internal.ccache.Credentials[] getCredsList();

   public abstract com.frojasg1.sun.security.krb5.internal.ccache.Credentials getDefaultCreds();

   public abstract com.frojasg1.sun.security.krb5.internal.ccache.Credentials getCreds(PrincipalName var1);

   public abstract Credentials getCreds(LoginOptions var1, PrincipalName var2);

   static {
      DEBUG = Krb5.DEBUG;
   }
}
