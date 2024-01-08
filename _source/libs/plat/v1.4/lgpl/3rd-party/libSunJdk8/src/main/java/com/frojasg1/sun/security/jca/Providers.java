package com.frojasg1.sun.security.jca;

import com.frojasg1.sun.security.jca.ProviderList;

import java.security.Provider;

public class Providers {
   private static final ThreadLocal<com.frojasg1.sun.security.jca.ProviderList> threadLists = new InheritableThreadLocal();
   private static volatile int threadListsUsed;
   private static volatile com.frojasg1.sun.security.jca.ProviderList providerList;
   private static final String BACKUP_PROVIDER_CLASSNAME = "sun.security.provider.VerificationProvider";
   private static final String[] jarVerificationProviders;

   private Providers() {
   }

   public static Provider getSunProvider() {
      try {
         Class var0 = Class.forName(jarVerificationProviders[0]);
         return (Provider)var0.newInstance();
      } catch (Exception var3) {
         try {
            Class var1 = Class.forName("sun.security.provider.VerificationProvider");
            return (Provider)var1.newInstance();
         } catch (Exception var2) {
            throw new RuntimeException("Sun provider not found", var3);
         }
      }
   }

   public static Object startJarVerification() {
      com.frojasg1.sun.security.jca.ProviderList var0 = getProviderList();
      com.frojasg1.sun.security.jca.ProviderList var1 = var0.getJarList(jarVerificationProviders);
      return beginThreadProviderList(var1);
   }

   public static void stopJarVerification(Object var0) {
      endThreadProviderList((com.frojasg1.sun.security.jca.ProviderList)var0);
   }

   public static com.frojasg1.sun.security.jca.ProviderList getProviderList() {
      com.frojasg1.sun.security.jca.ProviderList var0 = getThreadProviderList();
      if (var0 == null) {
         var0 = getSystemProviderList();
      }

      return var0;
   }

   public static void setProviderList(com.frojasg1.sun.security.jca.ProviderList var0) {
      if (getThreadProviderList() == null) {
         setSystemProviderList(var0);
      } else {
         changeThreadProviderList(var0);
      }

   }

   public static com.frojasg1.sun.security.jca.ProviderList getFullProviderList() {
      Class var1 = Providers.class;
      com.frojasg1.sun.security.jca.ProviderList var0;
      synchronized(Providers.class) {
         var0 = getThreadProviderList();
         if (var0 != null) {
            com.frojasg1.sun.security.jca.ProviderList var2 = var0.removeInvalid();
            if (var2 != var0) {
               changeThreadProviderList(var2);
               var0 = var2;
            }

            return var0;
         }
      }

      var0 = getSystemProviderList();
      com.frojasg1.sun.security.jca.ProviderList var5 = var0.removeInvalid();
      if (var5 != var0) {
         setSystemProviderList(var5);
         var0 = var5;
      }

      return var0;
   }

   private static com.frojasg1.sun.security.jca.ProviderList getSystemProviderList() {
      return providerList;
   }

   private static void setSystemProviderList(com.frojasg1.sun.security.jca.ProviderList var0) {
      providerList = var0;
   }

   public static com.frojasg1.sun.security.jca.ProviderList getThreadProviderList() {
      return threadListsUsed == 0 ? null : (com.frojasg1.sun.security.jca.ProviderList)threadLists.get();
   }

   private static void changeThreadProviderList(com.frojasg1.sun.security.jca.ProviderList var0) {
      threadLists.set(var0);
   }

   public static synchronized com.frojasg1.sun.security.jca.ProviderList beginThreadProviderList(com.frojasg1.sun.security.jca.ProviderList var0) {
      if (com.frojasg1.sun.security.jca.ProviderList.debug != null) {
         com.frojasg1.sun.security.jca.ProviderList.debug.println("ThreadLocal providers: " + var0);
      }

      com.frojasg1.sun.security.jca.ProviderList var1 = (com.frojasg1.sun.security.jca.ProviderList)threadLists.get();
      ++threadListsUsed;
      threadLists.set(var0);
      return var1;
   }

   public static synchronized void endThreadProviderList(com.frojasg1.sun.security.jca.ProviderList var0) {
      if (var0 == null) {
         if (com.frojasg1.sun.security.jca.ProviderList.debug != null) {
            com.frojasg1.sun.security.jca.ProviderList.debug.println("Disabling ThreadLocal providers");
         }

         threadLists.remove();
      } else {
         if (com.frojasg1.sun.security.jca.ProviderList.debug != null) {
            com.frojasg1.sun.security.jca.ProviderList.debug.println("Restoring previous ThreadLocal providers: " + var0);
         }

         threadLists.set(var0);
      }

      --threadListsUsed;
   }

   static {
      providerList = com.frojasg1.sun.security.jca.ProviderList.EMPTY;
      providerList = ProviderList.fromSecurityProperties();
      jarVerificationProviders = new String[]{"sun.security.provider.Sun", "sun.security.rsa.SunRsaSign", "sun.security.ec.SunEC", "sun.security.provider.VerificationProvider"};
   }
}
