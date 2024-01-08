package com.frojasg1.sun.security.ssl;

import java.net.Socket;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.security.auth.x500.X500Principal;

final class SunX509KeyManagerImpl extends X509ExtendedKeyManager {
   private static final Debug debug = Debug.getInstance("ssl");
   private static final String[] STRING0 = new String[0];
   private Map<String, SunX509KeyManagerImpl.X509Credentials> credentialsMap = new HashMap();
   private final Map<String, String[]> serverAliasCache = Collections.synchronizedMap(new HashMap());

   SunX509KeyManagerImpl(KeyStore var1, char[] var2) throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException {
      if (var1 != null) {
         Enumeration var3 = var1.aliases();

         while(true) {
            String var4;
            Object var6;
            do {
               do {
                  Key var5;
                  do {
                     do {
                        do {
                           do {
                              do {
                                 if (!var3.hasMoreElements()) {
                                    return;
                                 }

                                 var4 = (String)var3.nextElement();
                              } while(!var1.isKeyEntry(var4));

                              var5 = var1.getKey(var4, var2);
                           } while(!(var5 instanceof PrivateKey));

                           var6 = var1.getCertificateChain(var4);
                        } while(var6 == null);
                     } while(((Object[])var6).length == 0);
                  } while(!(((Object[])var6)[0] instanceof X509Certificate));

                  if (!(var6 instanceof X509Certificate[])) {
                     X509Certificate[] var7 = new X509Certificate[((Object[])var6).length];
                     System.arraycopy(var6, 0, var7, 0, ((Object[])var6).length);
                     var6 = var7;
                  }

                  SunX509KeyManagerImpl.X509Credentials var9 = new SunX509KeyManagerImpl.X509Credentials((PrivateKey)var5, (X509Certificate[])((X509Certificate[])var6));
                  this.credentialsMap.put(var4, var9);
               } while(debug == null);
            } while(!Debug.isOn("keymanager"));

            System.out.println("***");
            System.out.println("found key for : " + var4);

            for(int var8 = 0; var8 < ((Object[])var6).length; ++var8) {
               System.out.println("chain [" + var8 + "] = " + ((Object[])var6)[var8]);
            }

            System.out.println("***");
         }
      }
   }

   public X509Certificate[] getCertificateChain(String var1) {
      if (var1 == null) {
         return null;
      } else {
         SunX509KeyManagerImpl.X509Credentials var2 = (SunX509KeyManagerImpl.X509Credentials)this.credentialsMap.get(var1);
         return var2 == null ? null : (X509Certificate[])var2.certificates.clone();
      }
   }

   public PrivateKey getPrivateKey(String var1) {
      if (var1 == null) {
         return null;
      } else {
         SunX509KeyManagerImpl.X509Credentials var2 = (SunX509KeyManagerImpl.X509Credentials)this.credentialsMap.get(var1);
         return var2 == null ? null : var2.privateKey;
      }
   }

   public String chooseClientAlias(String[] var1, Principal[] var2, Socket var3) {
      if (var1 == null) {
         return null;
      } else {
         for(int var4 = 0; var4 < var1.length; ++var4) {
            String[] var5 = this.getClientAliases(var1[var4], var2);
            if (var5 != null && var5.length > 0) {
               return var5[0];
            }
         }

         return null;
      }
   }

   public String chooseEngineClientAlias(String[] var1, Principal[] var2, SSLEngine var3) {
      return this.chooseClientAlias(var1, var2, (Socket)null);
   }

   public String chooseServerAlias(String var1, Principal[] var2, Socket var3) {
      if (var1 == null) {
         return null;
      } else {
         String[] var4;
         if (var2 != null && var2.length != 0) {
            var4 = this.getServerAliases(var1, var2);
         } else {
            var4 = (String[])this.serverAliasCache.get(var1);
            if (var4 == null) {
               var4 = this.getServerAliases(var1, var2);
               if (var4 == null) {
                  var4 = STRING0;
               }

               this.serverAliasCache.put(var1, var4);
            }
         }

         return var4 != null && var4.length > 0 ? var4[0] : null;
      }
   }

   public String chooseEngineServerAlias(String var1, Principal[] var2, SSLEngine var3) {
      return this.chooseServerAlias(var1, var2, (Socket)null);
   }

   public String[] getClientAliases(String var1, Principal[] var2) {
      return this.getAliases(var1, var2);
   }

   public String[] getServerAliases(String var1, Principal[] var2) {
      return this.getAliases(var1, var2);
   }

   private String[] getAliases(String var1, Principal[] var2) {
      if (var1 == null) {
         return null;
      } else {
         if (var2 == null) {
            var2 = new X500Principal[0];
         }

         if (!(var2 instanceof X500Principal[])) {
            var2 = convertPrincipals((Principal[])var2);
         }

         String var3;
         if (var1.contains("_")) {
            int var4 = var1.indexOf("_");
            var3 = var1.substring(var4 + 1);
            var1 = var1.substring(0, var4);
         } else {
            var3 = null;
         }

         X500Principal[] var13 = (X500Principal[])((X500Principal[])var2);
         ArrayList var5 = new ArrayList();
         Iterator var6 = this.credentialsMap.entrySet().iterator();

         while(true) {
            while(true) {
               String var8;
               SunX509KeyManagerImpl.X509Credentials var9;
               while(true) {
                  X509Certificate[] var10;
                  do {
                     if (!var6.hasNext()) {
                        String[] var14 = (String[])var5.toArray(STRING0);
                        return var14.length == 0 ? null : var14;
                     }

                     Entry var7 = (Entry)var6.next();
                     var8 = (String)var7.getKey();
                     var9 = (SunX509KeyManagerImpl.X509Credentials)var7.getValue();
                     var10 = var9.certificates;
                  } while(!var1.equals(var10[0].getPublicKey().getAlgorithm()));

                  if (var3 == null) {
                     break;
                  }

                  if (var10.length > 1) {
                     if (!var3.equals(var10[1].getPublicKey().getAlgorithm())) {
                        continue;
                     }
                  } else {
                     String var11 = var10[0].getSigAlgName().toUpperCase(Locale.ENGLISH);
                     String var12 = "WITH" + var3.toUpperCase(Locale.ENGLISH);
                     if (!var11.contains(var12)) {
                        continue;
                     }
                  }
                  break;
               }

               if (((Object[])var2).length == 0) {
                  var5.add(var8);
                  if (debug != null && Debug.isOn("keymanager")) {
                     System.out.println("matching alias: " + var8);
                  }
               } else {
                  Set var15 = var9.getIssuerX500Principals();

                  for(int var16 = 0; var16 < var13.length; ++var16) {
                     if (var15.contains(((Object[])var2)[var16])) {
                        var5.add(var8);
                        if (debug != null && Debug.isOn("keymanager")) {
                           System.out.println("matching alias: " + var8);
                        }
                        break;
                     }
                  }
               }
            }
         }
      }
   }

   private static X500Principal[] convertPrincipals(Principal[] var0) {
      ArrayList var1 = new ArrayList(var0.length);

      for(int var2 = 0; var2 < var0.length; ++var2) {
         Principal var3 = var0[var2];
         if (var3 instanceof X500Principal) {
            var1.add((X500Principal)var3);
         } else {
            try {
               var1.add(new X500Principal(var3.getName()));
            } catch (IllegalArgumentException var5) {
            }
         }
      }

      return (X500Principal[])var1.toArray(new X500Principal[var1.size()]);
   }

   private static class X509Credentials {
      PrivateKey privateKey;
      X509Certificate[] certificates;
      private Set<X500Principal> issuerX500Principals;

      X509Credentials(PrivateKey var1, X509Certificate[] var2) {
         this.privateKey = var1;
         this.certificates = var2;
      }

      synchronized Set<X500Principal> getIssuerX500Principals() {
         if (this.issuerX500Principals == null) {
            this.issuerX500Principals = new HashSet();

            for(int var1 = 0; var1 < this.certificates.length; ++var1) {
               this.issuerX500Principals.add(this.certificates[var1].getIssuerX500Principal());
            }
         }

         return this.issuerX500Principals;
      }
   }
}
