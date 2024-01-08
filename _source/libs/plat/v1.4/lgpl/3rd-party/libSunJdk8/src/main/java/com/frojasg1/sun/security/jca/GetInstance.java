package com.frojasg1.sun.security.jca;

import com.frojasg1.sun.security.jca.ProviderList;
import com.frojasg1.sun.security.jca.Providers;
import com.frojasg1.sun.security.jca.ServiceId;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.Provider.Service;
import java.util.Iterator;
import java.util.List;

public class GetInstance {
   private GetInstance() {
   }

   public static Service getService(String var0, String var1) throws NoSuchAlgorithmException {
      com.frojasg1.sun.security.jca.ProviderList var2 = com.frojasg1.sun.security.jca.Providers.getProviderList();
      Service var3 = var2.getService(var0, var1);
      if (var3 == null) {
         throw new NoSuchAlgorithmException(var1 + " " + var0 + " not available");
      } else {
         return var3;
      }
   }

   public static Service getService(String var0, String var1, String var2) throws NoSuchAlgorithmException, NoSuchProviderException {
      if (var2 != null && var2.length() != 0) {
         Provider var3 = com.frojasg1.sun.security.jca.Providers.getProviderList().getProvider(var2);
         if (var3 == null) {
            throw new NoSuchProviderException("no such provider: " + var2);
         } else {
            Service var4 = var3.getService(var0, var1);
            if (var4 == null) {
               throw new NoSuchAlgorithmException("no such algorithm: " + var1 + " for provider " + var2);
            } else {
               return var4;
            }
         }
      } else {
         throw new IllegalArgumentException("missing provider");
      }
   }

   public static Service getService(String var0, String var1, Provider var2) throws NoSuchAlgorithmException {
      if (var2 == null) {
         throw new IllegalArgumentException("missing provider");
      } else {
         Service var3 = var2.getService(var0, var1);
         if (var3 == null) {
            throw new NoSuchAlgorithmException("no such algorithm: " + var1 + " for provider " + var2.getName());
         } else {
            return var3;
         }
      }
   }

   public static List<Service> getServices(String var0, String var1) {
      com.frojasg1.sun.security.jca.ProviderList var2 = com.frojasg1.sun.security.jca.Providers.getProviderList();
      return var2.getServices(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public static List<Service> getServices(String var0, List<String> var1) {
      com.frojasg1.sun.security.jca.ProviderList var2 = com.frojasg1.sun.security.jca.Providers.getProviderList();
      return var2.getServices(var0, var1);
   }

   public static List<Service> getServices(List<ServiceId> var0) {
      com.frojasg1.sun.security.jca.ProviderList var1 = com.frojasg1.sun.security.jca.Providers.getProviderList();
      return var1.getServices(var0);
   }

   public static GetInstance.Instance getInstance(String var0, Class<?> var1, String var2) throws NoSuchAlgorithmException {
      ProviderList var3 = Providers.getProviderList();
      Service var4 = var3.getService(var0, var2);
      if (var4 == null) {
         throw new NoSuchAlgorithmException(var2 + " " + var0 + " not available");
      } else {
         try {
            return getInstance(var4, var1);
         } catch (NoSuchAlgorithmException var10) {
            NoSuchAlgorithmException var5 = var10;
            Iterator var6 = var3.getServices(var0, var2).iterator();

            while(true) {
               Service var7;
               do {
                  if (!var6.hasNext()) {
                     throw var5;
                  }

                  var7 = (Service)var6.next();
               } while(var7 == var4);

               try {
                  return getInstance(var7, var1);
               } catch (NoSuchAlgorithmException var9) {
                  var5 = var9;
               }
            }
         }
      }
   }

   public static GetInstance.Instance getInstance(String var0, Class<?> var1, String var2, Object var3) throws NoSuchAlgorithmException {
      List var4 = getServices(var0, var2);
      NoSuchAlgorithmException var5 = null;
      Iterator var6 = var4.iterator();

      while(var6.hasNext()) {
         Service var7 = (Service)var6.next();

         try {
            return getInstance(var7, var1, var3);
         } catch (NoSuchAlgorithmException var9) {
            var5 = var9;
         }
      }

      if (var5 != null) {
         throw var5;
      } else {
         throw new NoSuchAlgorithmException(var2 + " " + var0 + " not available");
      }
   }

   public static GetInstance.Instance getInstance(String var0, Class<?> var1, String var2, String var3) throws NoSuchAlgorithmException, NoSuchProviderException {
      return getInstance(getService(var0, var2, var3), var1);
   }

   public static GetInstance.Instance getInstance(String var0, Class<?> var1, String var2, Object var3, String var4) throws NoSuchAlgorithmException, NoSuchProviderException {
      return getInstance(getService(var0, var2, var4), var1, var3);
   }

   public static GetInstance.Instance getInstance(String var0, Class<?> var1, String var2, Provider var3) throws NoSuchAlgorithmException {
      return getInstance(getService(var0, var2, var3), var1);
   }

   public static GetInstance.Instance getInstance(String var0, Class<?> var1, String var2, Object var3, Provider var4) throws NoSuchAlgorithmException {
      return getInstance(getService(var0, var2, var4), var1, var3);
   }

   public static GetInstance.Instance getInstance(Service var0, Class<?> var1) throws NoSuchAlgorithmException {
      Object var2 = var0.newInstance((Object)null);
      checkSuperClass(var0, var2.getClass(), var1);
      return new GetInstance.Instance(var0.getProvider(), var2);
   }

   public static GetInstance.Instance getInstance(Service var0, Class<?> var1, Object var2) throws NoSuchAlgorithmException {
      Object var3 = var0.newInstance(var2);
      checkSuperClass(var0, var3.getClass(), var1);
      return new GetInstance.Instance(var0.getProvider(), var3);
   }

   public static void checkSuperClass(Service var0, Class<?> var1, Class<?> var2) throws NoSuchAlgorithmException {
      if (var2 != null) {
         if (!var2.isAssignableFrom(var1)) {
            throw new NoSuchAlgorithmException("class configured for " + var0.getType() + ": " + var0.getClassName() + " not a " + var0.getType());
         }
      }
   }

   public static final class Instance {
      public final Provider provider;
      public final Object impl;

      private Instance(Provider var1, Object var2) {
         this.provider = var1;
         this.impl = var2;
      }

      public Object[] toArray() {
         return new Object[]{this.impl, this.provider};
      }
   }
}
