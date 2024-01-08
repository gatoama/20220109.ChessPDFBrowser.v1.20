package com.frojasg1.sun.security.jca;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import java.security.Security;
import java.security.Provider.Service;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.frojasg1.sun.security.jca.ProviderConfig;
import com.frojasg1.sun.security.jca.ServiceId;
import com.frojasg1.sun.security.util.Debug;

public final class ProviderList {
   static final Debug debug = Debug.getInstance("jca", "ProviderList");
   private static final com.frojasg1.sun.security.jca.ProviderConfig[] PC0 = new com.frojasg1.sun.security.jca.ProviderConfig[0];
   private static final Provider[] P0 = new Provider[0];
   static final ProviderList EMPTY;
   private static final Provider EMPTY_PROVIDER;
   private final com.frojasg1.sun.security.jca.ProviderConfig[] configs;
   private volatile boolean allLoaded;
   private final List<Provider> userList;

   static ProviderList fromSecurityProperties() {
      return (ProviderList)AccessController.doPrivileged(new PrivilegedAction<ProviderList>() {
         public ProviderList run() {
            return new ProviderList();
         }
      });
   }

   public static ProviderList add(ProviderList var0, Provider var1) {
      return insertAt(var0, var1, -1);
   }

   public static ProviderList insertAt(ProviderList var0, Provider var1, int var2) {
      if (var0.getProvider(var1.getName()) != null) {
         return var0;
      } else {
         ArrayList var3 = new ArrayList(Arrays.asList(var0.configs));
         int var4 = var3.size();
         if (var2 < 0 || var2 > var4) {
            var2 = var4;
         }

         var3.add(var2, new com.frojasg1.sun.security.jca.ProviderConfig(var1));
         return new ProviderList((com.frojasg1.sun.security.jca.ProviderConfig[])var3.toArray(PC0), true);
      }
   }

   public static ProviderList remove(ProviderList var0, String var1) {
      if (var0.getProvider(var1) == null) {
         return var0;
      } else {
         com.frojasg1.sun.security.jca.ProviderConfig[] var2 = new com.frojasg1.sun.security.jca.ProviderConfig[var0.size() - 1];
         int var3 = 0;
         com.frojasg1.sun.security.jca.ProviderConfig[] var4 = var0.configs;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            com.frojasg1.sun.security.jca.ProviderConfig var7 = var4[var6];
            if (!var7.getProvider().getName().equals(var1)) {
               var2[var3++] = var7;
            }
         }

         return new ProviderList(var2, true);
      }
   }

   public static ProviderList newList(Provider... var0) {
      com.frojasg1.sun.security.jca.ProviderConfig[] var1 = new com.frojasg1.sun.security.jca.ProviderConfig[var0.length];

      for(int var2 = 0; var2 < var0.length; ++var2) {
         var1[var2] = new com.frojasg1.sun.security.jca.ProviderConfig(var0[var2]);
      }

      return new ProviderList(var1, true);
   }

   private ProviderList(com.frojasg1.sun.security.jca.ProviderConfig[] var1, boolean var2) {
      this.userList = new AbstractList<Provider>() {
         public int size() {
            return ProviderList.this.configs.length;
         }

         public Provider get(int var1) {
            return ProviderList.this.getProvider(var1);
         }
      };
      this.configs = var1;
      this.allLoaded = var2;
   }

   private ProviderList() {
      this.userList = new AbstractList<Provider>() {
         public int size() {
            return ProviderList.this.configs.length;
         }

         public Provider get(int var1) {
            return ProviderList.this.getProvider(var1);
         }
      };
      ArrayList var1 = new ArrayList();
      int var2 = 1;

      while(true) {
         String var3 = Security.getProperty("security.provider." + var2);
         if (var3 == null) {
            break;
         }

         var3 = var3.trim();
         if (var3.length() == 0) {
            System.err.println("invalid entry for security.provider." + var2);
            break;
         }

         int var4 = var3.indexOf(32);
         com.frojasg1.sun.security.jca.ProviderConfig var5;
         if (var4 == -1) {
            var5 = new com.frojasg1.sun.security.jca.ProviderConfig(var3);
         } else {
            String var6 = var3.substring(0, var4);
            String var7 = var3.substring(var4 + 1).trim();
            var5 = new com.frojasg1.sun.security.jca.ProviderConfig(var6, var7);
         }

         if (!var1.contains(var5)) {
            var1.add(var5);
         }

         ++var2;
      }

      this.configs = (com.frojasg1.sun.security.jca.ProviderConfig[])var1.toArray(PC0);
      if (debug != null) {
         debug.println("provider configuration: " + var1);
      }

   }

   ProviderList getJarList(String[] var1) {
      ArrayList var2 = new ArrayList();
      String[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         String var6 = var3[var5];
         com.frojasg1.sun.security.jca.ProviderConfig var7 = new com.frojasg1.sun.security.jca.ProviderConfig(var6);
         com.frojasg1.sun.security.jca.ProviderConfig[] var8 = this.configs;
         int var9 = var8.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            com.frojasg1.sun.security.jca.ProviderConfig var11 = var8[var10];
            if (var11.equals(var7)) {
               var7 = var11;
               break;
            }
         }

         var2.add(var7);
      }

      com.frojasg1.sun.security.jca.ProviderConfig[] var12 = (com.frojasg1.sun.security.jca.ProviderConfig[])var2.toArray(PC0);
      return new ProviderList(var12, false);
   }

   public int size() {
      return this.configs.length;
   }

   Provider getProvider(int var1) {
      Provider var2 = this.configs[var1].getProvider();
      return var2 != null ? var2 : EMPTY_PROVIDER;
   }

   public List<Provider> providers() {
      return this.userList;
   }

   private com.frojasg1.sun.security.jca.ProviderConfig getProviderConfig(String var1) {
      int var2 = this.getIndex(var1);
      return var2 != -1 ? this.configs[var2] : null;
   }

   public Provider getProvider(String var1) {
      com.frojasg1.sun.security.jca.ProviderConfig var2 = this.getProviderConfig(var1);
      return var2 == null ? null : var2.getProvider();
   }

   public int getIndex(String var1) {
      for(int var2 = 0; var2 < this.configs.length; ++var2) {
         Provider var3 = this.getProvider(var2);
         if (var3.getName().equals(var1)) {
            return var2;
         }
      }

      return -1;
   }

   private int loadAll() {
      if (this.allLoaded) {
         return this.configs.length;
      } else {
         if (debug != null) {
            debug.println("Loading all providers");
            (new Exception("Call trace")).printStackTrace();
         }

         int var1 = 0;

         for(int var2 = 0; var2 < this.configs.length; ++var2) {
            Provider var3 = this.configs[var2].getProvider();
            if (var3 != null) {
               ++var1;
            }
         }

         if (var1 == this.configs.length) {
            this.allLoaded = true;
         }

         return var1;
      }
   }

   ProviderList removeInvalid() {
      int var1 = this.loadAll();
      if (var1 == this.configs.length) {
         return this;
      } else {
         com.frojasg1.sun.security.jca.ProviderConfig[] var2 = new com.frojasg1.sun.security.jca.ProviderConfig[var1];
         int var3 = 0;

         for(int var4 = 0; var3 < this.configs.length; ++var3) {
            com.frojasg1.sun.security.jca.ProviderConfig var5 = this.configs[var3];
            if (var5.isLoaded()) {
               var2[var4++] = var5;
            }
         }

         return new ProviderList(var2, true);
      }
   }

   public Provider[] toArray() {
      return (Provider[])this.providers().toArray(P0);
   }

   public String toString() {
      return Arrays.asList(this.configs).toString();
   }

   public Service getService(String var1, String var2) {
      for(int var3 = 0; var3 < this.configs.length; ++var3) {
         Provider var4 = this.getProvider(var3);
         Service var5 = var4.getService(var1, var2);
         if (var5 != null) {
            return var5;
         }
      }

      return null;
   }

   public List<Service> getServices(String var1, String var2) {
      return new ProviderList.ServiceList(var1, var2);
   }

   /** @deprecated */
   @Deprecated
   public List<Service> getServices(String var1, List<String> var2) {
      ArrayList var3 = new ArrayList();
      Iterator var4 = var2.iterator();

      while(var4.hasNext()) {
         String var5 = (String)var4.next();
         var3.add(new com.frojasg1.sun.security.jca.ServiceId(var1, var5));
      }

      return this.getServices(var3);
   }

   public List<Service> getServices(List<com.frojasg1.sun.security.jca.ServiceId> var1) {
      return new ProviderList.ServiceList(var1);
   }

   static {
      EMPTY = new ProviderList(PC0, true);
      EMPTY_PROVIDER = new Provider("##Empty##", 1.0D, "initialization in progress") {
         private static final long serialVersionUID = 1151354171352296389L;

         public Service getService(String var1, String var2) {
            return null;
         }
      };
   }

   private final class ServiceList extends AbstractList<Service> {
      private final String type;
      private final String algorithm;
      private final List<com.frojasg1.sun.security.jca.ServiceId> ids;
      private Service firstService;
      private List<Service> services;
      private int providerIndex;

      ServiceList(String var2, String var3) {
         this.type = var2;
         this.algorithm = var3;
         this.ids = null;
      }

      ServiceList(List<com.frojasg1.sun.security.jca.ServiceId> var2) {
         this.type = null;
         this.algorithm = null;
         this.ids = var2;
      }

      private void addService(Service var1) {
         if (this.firstService == null) {
            this.firstService = var1;
         } else {
            if (this.services == null) {
               this.services = new ArrayList(4);
               this.services.add(this.firstService);
            }

            this.services.add(var1);
         }

      }

      private Service tryGet(int var1) {
         while(var1 != 0 || this.firstService == null) {
            if (this.services != null && this.services.size() > var1) {
               return (Service)this.services.get(var1);
            }

            if (this.providerIndex >= ProviderList.this.configs.length) {
               return null;
            }

            Provider var2 = ProviderList.this.getProvider(this.providerIndex++);
            if (this.type != null) {
               Service var6 = var2.getService(this.type, this.algorithm);
               if (var6 != null) {
                  this.addService(var6);
               }
            } else {
               Iterator var3 = this.ids.iterator();

               while(var3.hasNext()) {
                  com.frojasg1.sun.security.jca.ServiceId var4 = (ServiceId)var3.next();
                  Service var5 = var2.getService(var4.type, var4.algorithm);
                  if (var5 != null) {
                     this.addService(var5);
                  }
               }
            }
         }

         return this.firstService;
      }

      public Service get(int var1) {
         Service var2 = this.tryGet(var1);
         if (var2 == null) {
            throw new IndexOutOfBoundsException();
         } else {
            return var2;
         }
      }

      public int size() {
         int var1;
         if (this.services != null) {
            var1 = this.services.size();
         } else {
            var1 = this.firstService != null ? 1 : 0;
         }

         while(this.tryGet(var1) != null) {
            ++var1;
         }

         return var1;
      }

      public boolean isEmpty() {
         return this.tryGet(0) == null;
      }

      public Iterator<Service> iterator() {
         return new Iterator<Service>() {
            int index;

            public boolean hasNext() {
               return ServiceList.this.tryGet(this.index) != null;
            }

            public Service next() {
               Service var1 = ServiceList.this.tryGet(this.index);
               if (var1 == null) {
                  throw new NoSuchElementException();
               } else {
                  ++this.index;
                  return var1;
               }
            }

            public void remove() {
               throw new UnsupportedOperationException();
            }
         };
      }
   }
}
