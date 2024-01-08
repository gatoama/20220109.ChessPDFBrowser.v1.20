package com.frojasg1.sun.security.ssl;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.net.Socket;
import java.security.AlgorithmConstraints;
import java.security.KeyStore;
import java.security.Principal;
import java.security.PrivateKey;
import java.security.Timestamp;
import java.security.KeyStore.Builder;
import java.security.KeyStore.Entry;
import java.security.KeyStore.PrivateKeyEntry;
import java.security.cert.CertPathValidatorException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509KeyManager;
import com.frojasg1.sun.security.provider.certpath.AlgorithmChecker;

final class X509KeyManagerImpl extends X509ExtendedKeyManager implements X509KeyManager {
   private static final Debug debug = Debug.getInstance("ssl");
   private static final boolean useDebug;
   private static Date verificationDate;
   private final List<Builder> builders;
   private final AtomicLong uidCounter;
   private final Map<String, Reference<PrivateKeyEntry>> entryCacheMap;

   X509KeyManagerImpl(Builder var1) {
      this(Collections.singletonList(var1));
   }

   X509KeyManagerImpl(List<Builder> var1) {
      this.builders = var1;
      this.uidCounter = new AtomicLong();
      this.entryCacheMap = Collections.synchronizedMap(new X509KeyManagerImpl.SizedMap());
   }

   public X509Certificate[] getCertificateChain(String var1) {
      PrivateKeyEntry var2 = this.getEntry(var1);
      return var2 == null ? null : (X509Certificate[])((X509Certificate[])var2.getCertificateChain());
   }

   public PrivateKey getPrivateKey(String var1) {
      PrivateKeyEntry var2 = this.getEntry(var1);
      return var2 == null ? null : var2.getPrivateKey();
   }

   public String chooseClientAlias(String[] var1, Principal[] var2, Socket var3) {
      return this.chooseAlias(getKeyTypes(var1), var2, X509KeyManagerImpl.CheckType.CLIENT, this.getAlgorithmConstraints(var3));
   }

   public String chooseEngineClientAlias(String[] var1, Principal[] var2, SSLEngine var3) {
      return this.chooseAlias(getKeyTypes(var1), var2, X509KeyManagerImpl.CheckType.CLIENT, this.getAlgorithmConstraints(var3));
   }

   public String chooseServerAlias(String var1, Principal[] var2, Socket var3) {
      return this.chooseAlias(getKeyTypes(var1), var2, X509KeyManagerImpl.CheckType.SERVER, this.getAlgorithmConstraints(var3), X509TrustManagerImpl.getRequestedServerNames(var3), "HTTPS");
   }

   public String chooseEngineServerAlias(String var1, Principal[] var2, SSLEngine var3) {
      return this.chooseAlias(getKeyTypes(var1), var2, X509KeyManagerImpl.CheckType.SERVER, this.getAlgorithmConstraints(var3), X509TrustManagerImpl.getRequestedServerNames(var3), "HTTPS");
   }

   public String[] getClientAliases(String var1, Principal[] var2) {
      return this.getAliases(var1, var2, X509KeyManagerImpl.CheckType.CLIENT, (AlgorithmConstraints)null);
   }

   public String[] getServerAliases(String var1, Principal[] var2) {
      return this.getAliases(var1, var2, X509KeyManagerImpl.CheckType.SERVER, (AlgorithmConstraints)null);
   }

   private AlgorithmConstraints getAlgorithmConstraints(Socket var1) {
      if (var1 != null && var1.isConnected() && var1 instanceof SSLSocket) {
         SSLSocket var2 = (SSLSocket)var1;
         SSLSession var3 = var2.getHandshakeSession();
         if (var3 != null) {
            ProtocolVersion var4 = ProtocolVersion.valueOf(var3.getProtocol());
            if (var4.v >= ProtocolVersion.TLS12.v) {
               String[] var5 = null;
               if (var3 instanceof ExtendedSSLSession) {
                  ExtendedSSLSession var6 = (ExtendedSSLSession)var3;
                  var5 = var6.getPeerSupportedSignatureAlgorithms();
               }

               return new SSLAlgorithmConstraints(var2, var5, true);
            }
         }

         return new SSLAlgorithmConstraints(var2, true);
      } else {
         return new SSLAlgorithmConstraints((SSLSocket)null, true);
      }
   }

   private AlgorithmConstraints getAlgorithmConstraints(SSLEngine var1) {
      if (var1 != null) {
         SSLSession var2 = var1.getHandshakeSession();
         if (var2 != null) {
            ProtocolVersion var3 = ProtocolVersion.valueOf(var2.getProtocol());
            if (var3.v >= ProtocolVersion.TLS12.v) {
               String[] var4 = null;
               if (var2 instanceof ExtendedSSLSession) {
                  ExtendedSSLSession var5 = (ExtendedSSLSession)var2;
                  var4 = var5.getPeerSupportedSignatureAlgorithms();
               }

               return new SSLAlgorithmConstraints(var1, var4, true);
            }
         }
      }

      return new SSLAlgorithmConstraints(var1, true);
   }

   private String makeAlias(X509KeyManagerImpl.EntryStatus var1) {
      return this.uidCounter.incrementAndGet() + "." + var1.builderIndex + "." + var1.alias;
   }

   private PrivateKeyEntry getEntry(String var1) {
      if (var1 == null) {
         return null;
      } else {
         Reference var2 = (Reference)this.entryCacheMap.get(var1);
         PrivateKeyEntry var3 = var2 != null ? (PrivateKeyEntry)var2.get() : null;
         if (var3 != null) {
            return var3;
         } else {
            int var4 = var1.indexOf(46);
            int var5 = var1.indexOf(46, var4 + 1);
            if (var4 != -1 && var5 != var4) {
               try {
                  int var6 = Integer.parseInt(var1.substring(var4 + 1, var5));
                  String var7 = var1.substring(var5 + 1);
                  Builder var8 = (Builder)this.builders.get(var6);
                  KeyStore var9 = var8.getKeyStore();
                  Entry var10 = var9.getEntry(var7, var8.getProtectionParameter(var1));
                  if (!(var10 instanceof PrivateKeyEntry)) {
                     return null;
                  } else {
                     var3 = (PrivateKeyEntry)var10;
                     this.entryCacheMap.put(var1, new SoftReference(var3));
                     return var3;
                  }
               } catch (Exception var11) {
                  return null;
               }
            } else {
               return null;
            }
         }
      }
   }

   private static List<X509KeyManagerImpl.KeyType> getKeyTypes(String... var0) {
      if (var0 != null && var0.length != 0 && var0[0] != null) {
         ArrayList var1 = new ArrayList(var0.length);
         String[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            String var5 = var2[var4];
            var1.add(new X509KeyManagerImpl.KeyType(var5));
         }

         return var1;
      } else {
         return null;
      }
   }

   private String chooseAlias(List<X509KeyManagerImpl.KeyType> var1, Principal[] var2, X509KeyManagerImpl.CheckType var3, AlgorithmConstraints var4) {
      return this.chooseAlias(var1, var2, var3, var4, (List)null, (String)null);
   }

   private String chooseAlias(List<X509KeyManagerImpl.KeyType> var1, Principal[] var2, X509KeyManagerImpl.CheckType var3, AlgorithmConstraints var4, List<SNIServerName> var5, String var6) {
      if (var1 != null && !var1.isEmpty()) {
         Set var7 = this.getIssuerSet(var2);
         ArrayList var8 = null;
         int var9 = 0;

         for(int var10 = this.builders.size(); var9 < var10; ++var9) {
            try {
               List var11 = this.getAliases(var9, var1, var7, false, var3, var4, var5, var6);
               if (var11 != null) {
                  X509KeyManagerImpl.EntryStatus var12 = (X509KeyManagerImpl.EntryStatus)var11.get(0);
                  if (var12.checkResult == X509KeyManagerImpl.CheckResult.OK) {
                     if (useDebug) {
                        debug.println("KeyMgr: choosing key: " + var12);
                     }

                     return this.makeAlias(var12);
                  }

                  if (var8 == null) {
                     var8 = new ArrayList();
                  }

                  var8.addAll(var11);
               }
            } catch (Exception var13) {
            }
         }

         if (var8 == null) {
            if (useDebug) {
               debug.println("KeyMgr: no matching key found");
            }

            return null;
         } else {
            Collections.sort(var8);
            if (useDebug) {
               debug.println("KeyMgr: no good matching key found, returning best match out of:");
               debug.println(var8.toString());
            }

            return this.makeAlias((X509KeyManagerImpl.EntryStatus)var8.get(0));
         }
      } else {
         return null;
      }
   }

   public String[] getAliases(String var1, Principal[] var2, X509KeyManagerImpl.CheckType var3, AlgorithmConstraints var4) {
      if (var1 == null) {
         return null;
      } else {
         Set var5 = this.getIssuerSet(var2);
         List var6 = getKeyTypes(var1);
         ArrayList var7 = null;
         int var8 = 0;

         for(int var9 = this.builders.size(); var8 < var9; ++var8) {
            try {
               List var10 = this.getAliases(var8, var6, var5, true, var3, var4, (List)null, (String)null);
               if (var10 != null) {
                  if (var7 == null) {
                     var7 = new ArrayList();
                  }

                  var7.addAll(var10);
               }
            } catch (Exception var11) {
            }
         }

         if (var7 != null && !var7.isEmpty()) {
            Collections.sort(var7);
            if (useDebug) {
               debug.println("KeyMgr: getting aliases: " + var7);
            }

            return this.toAliases(var7);
         } else {
            if (useDebug) {
               debug.println("KeyMgr: no matching alias found");
            }

            return null;
         }
      }
   }

   private String[] toAliases(List<X509KeyManagerImpl.EntryStatus> var1) {
      String[] var2 = new String[var1.size()];
      int var3 = 0;

      X509KeyManagerImpl.EntryStatus var5;
      for(Iterator var4 = var1.iterator(); var4.hasNext(); var2[var3++] = this.makeAlias(var5)) {
         var5 = (X509KeyManagerImpl.EntryStatus)var4.next();
      }

      return var2;
   }

   private Set<Principal> getIssuerSet(Principal[] var1) {
      return var1 != null && var1.length != 0 ? new HashSet(Arrays.asList(var1)) : null;
   }

   private List<X509KeyManagerImpl.EntryStatus> getAliases(int var1, List<X509KeyManagerImpl.KeyType> var2, Set<Principal> var3, boolean var4, X509KeyManagerImpl.CheckType var5, AlgorithmConstraints var6, List<SNIServerName> var7, String var8) throws Exception {
      Builder var9 = (Builder)this.builders.get(var1);
      KeyStore var10 = var9.getKeyStore();
      ArrayList var11 = null;
      Date var12 = verificationDate;
      boolean var13 = false;
      Enumeration var14 = var10.aliases();

      while(true) {
         while(true) {
            String var15;
            Certificate[] var16;
            boolean var17;
            int var19;
            do {
               do {
                  do {
                     do {
                        if (!var14.hasMoreElements()) {
                           return var11;
                        }

                        var15 = (String)var14.nextElement();
                     } while(!var10.isKeyEntry(var15));

                     var16 = var10.getCertificateChain(var15);
                  } while(var16 == null);
               } while(var16.length == 0);

               var17 = false;
               Certificate[] var18 = var16;
               var19 = var16.length;

               for(int var20 = 0; var20 < var19; ++var20) {
                  Certificate var21 = var18[var20];
                  if (!(var21 instanceof X509Certificate)) {
                     var17 = true;
                     break;
                  }
               }
            } while(var17);

            int var26 = -1;
            var19 = 0;

            for(Iterator var27 = var2.iterator(); var27.hasNext(); ++var19) {
               X509KeyManagerImpl.KeyType var29 = (X509KeyManagerImpl.KeyType)var27.next();
               if (var29.matches(var16)) {
                  var26 = var19;
                  break;
               }
            }

            if (var26 == -1) {
               if (useDebug) {
                  debug.println("Ignoring alias " + var15 + ": key algorithm does not match");
               }
            } else {
               if (var3 != null) {
                  boolean var28 = false;
                  Certificate[] var30 = var16;
                  int var22 = var16.length;

                  for(int var23 = 0; var23 < var22; ++var23) {
                     Certificate var24 = var30[var23];
                     X509Certificate var25 = (X509Certificate)var24;
                     if (var3.contains(var25.getIssuerX500Principal())) {
                        var28 = true;
                        break;
                     }
                  }

                  if (!var28) {
                     if (useDebug) {
                        debug.println("Ignoring alias " + var15 + ": issuers do not match");
                     }
                     continue;
                  }
               }

               if (var6 != null && !conformsToAlgorithmConstraints(var6, var16, var5.getValidator())) {
                  if (useDebug) {
                     debug.println("Ignoring alias " + var15 + ": certificate list does not conform to algorithm constraints");
                  }
               } else {
                  if (var12 == null) {
                     var12 = new Date();
                  }

                  X509KeyManagerImpl.CheckResult var31 = var5.check((X509Certificate)var16[0], var12, var7, var8);
                  X509KeyManagerImpl.EntryStatus var32 = new X509KeyManagerImpl.EntryStatus(var1, var26, var15, var16, var31);
                  if (!var13 && var31 == X509KeyManagerImpl.CheckResult.OK && var26 == 0) {
                     var13 = true;
                  }

                  if (var13 && !var4) {
                     return Collections.singletonList(var32);
                  }

                  if (var11 == null) {
                     var11 = new ArrayList();
                  }

                  var11.add(var32);
               }
            }
         }
      }
   }

   private static boolean conformsToAlgorithmConstraints(AlgorithmConstraints var0, Certificate[] var1, String var2) {
      AlgorithmChecker var3 = new AlgorithmChecker(var0, (Timestamp)null, var2);

      try {
         var3.init(false);
      } catch (CertPathValidatorException var8) {
         if (useDebug) {
            debug.println("Cannot initialize algorithm constraints checker: " + var8);
         }

         return false;
      }

      for(int var4 = var1.length - 1; var4 >= 0; --var4) {
         Certificate var5 = var1[var4];

         try {
            var3.check(var5, Collections.emptySet());
         } catch (CertPathValidatorException var7) {
            if (useDebug) {
               debug.println("Certificate (" + var5 + ") does not conform to algorithm constraints: " + var7);
            }

            return false;
         }
      }

      return true;
   }

   static {
      useDebug = debug != null && Debug.isOn("keymanager");
   }

   private static enum CheckResult {
      OK,
      INSENSITIVE,
      EXPIRED,
      EXTENSION_MISMATCH;

      private CheckResult() {
      }
   }

   private static enum CheckType {
      NONE(Collections.emptySet()),
      CLIENT(new HashSet(Arrays.asList("2.5.29.37.0", "1.3.6.1.5.5.7.3.2"))),
      SERVER(new HashSet(Arrays.asList("2.5.29.37.0", "1.3.6.1.5.5.7.3.1", "2.16.840.1.113730.4.1", "1.3.6.1.4.1.311.10.3.3")));

      final Set<String> validEku;

      private CheckType(Set<String> var3) {
         this.validEku = var3;
      }

      private static boolean getBit(boolean[] var0, int var1) {
         return var1 < var0.length && var0[var1];
      }

      X509KeyManagerImpl.CheckResult check(X509Certificate var1, Date var2, List<SNIServerName> var3, String var4) {
         if (this == NONE) {
            return X509KeyManagerImpl.CheckResult.OK;
         } else {
            String var7;
            try {
               List var5 = var1.getExtendedKeyUsage();
               if (var5 != null && Collections.disjoint(this.validEku, var5)) {
                  return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
               }

               boolean[] var6 = var1.getKeyUsage();
               if (var6 != null) {
                  var7 = var1.getPublicKey().getAlgorithm();
                  boolean var8 = getBit(var6, 0);
                  byte var10 = -1;
                  switch(var7.hashCode()) {
                  case 2180:
                     if (var7.equals("DH")) {
                        var10 = 2;
                     }
                     break;
                  case 2206:
                     if (var7.equals("EC")) {
                        var10 = 3;
                     }
                     break;
                  case 67986:
                     if (var7.equals("DSA")) {
                        var10 = 1;
                     }
                     break;
                  case 81440:
                     if (var7.equals("RSA")) {
                        var10 = 0;
                     }
                  }

                  switch(var10) {
                  case 0:
                     if (var8 || this != CLIENT && getBit(var6, 2)) {
                        break;
                     }

                     return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
                  case 1:
                     if (!var8) {
                        return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
                     }
                     break;
                  case 2:
                     if (!getBit(var6, 4)) {
                        return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
                     }
                     break;
                  case 3:
                     if (!var8) {
                        return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
                     }

                     if (this == SERVER && !getBit(var6, 4)) {
                        return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
                     }
                  }
               }
            } catch (CertificateException var14) {
               return X509KeyManagerImpl.CheckResult.EXTENSION_MISMATCH;
            }

            try {
               var1.checkValidity(var2);
            } catch (CertificateException var11) {
               return X509KeyManagerImpl.CheckResult.EXPIRED;
            }

            if (var3 != null && !var3.isEmpty()) {
               Iterator var15 = var3.iterator();

               while(var15.hasNext()) {
                  Object var16 = (SNIServerName)var15.next();
                  if (((SNIServerName)var16).getType() == 0) {
                     if (!(var16 instanceof SNIHostName)) {
                        try {
                           var16 = new SNIHostName(((SNIServerName)var16).getEncoded());
                        } catch (IllegalArgumentException var12) {
                           if (X509KeyManagerImpl.useDebug) {
                              X509KeyManagerImpl.debug.println("Illegal server name: " + var16);
                           }

                           return X509KeyManagerImpl.CheckResult.INSENSITIVE;
                        }
                     }

                     var7 = ((SNIHostName)var16).getAsciiName();

                     try {
                        X509TrustManagerImpl.checkIdentity(var7, var1, var4);
                        break;
                     } catch (CertificateException var13) {
                        if (X509KeyManagerImpl.useDebug) {
                           X509KeyManagerImpl.debug.println("Certificate identity does not match Server Name Inidication (SNI): " + var7);
                        }

                        return X509KeyManagerImpl.CheckResult.INSENSITIVE;
                     }
                  }
               }
            }

            return X509KeyManagerImpl.CheckResult.OK;
         }
      }

      public String getValidator() {
         if (this == CLIENT) {
            return "tls client";
         } else {
            return this == SERVER ? "tls server" : "generic";
         }
      }
   }

   private static class EntryStatus implements Comparable<X509KeyManagerImpl.EntryStatus> {
      final int builderIndex;
      final int keyIndex;
      final String alias;
      final X509KeyManagerImpl.CheckResult checkResult;

      EntryStatus(int var1, int var2, String var3, Certificate[] var4, X509KeyManagerImpl.CheckResult var5) {
         this.builderIndex = var1;
         this.keyIndex = var2;
         this.alias = var3;
         this.checkResult = var5;
      }

      public int compareTo(X509KeyManagerImpl.EntryStatus var1) {
         int var2 = this.checkResult.compareTo(var1.checkResult);
         return var2 == 0 ? this.keyIndex - var1.keyIndex : var2;
      }

      public String toString() {
         String var1 = this.alias + " (verified: " + this.checkResult + ")";
         return this.builderIndex == 0 ? var1 : "Builder #" + this.builderIndex + ", alias: " + var1;
      }
   }

   private static class KeyType {
      final String keyAlgorithm;
      final String sigKeyAlgorithm;

      KeyType(String var1) {
         int var2 = var1.indexOf("_");
         if (var2 == -1) {
            this.keyAlgorithm = var1;
            this.sigKeyAlgorithm = null;
         } else {
            this.keyAlgorithm = var1.substring(0, var2);
            this.sigKeyAlgorithm = var1.substring(var2 + 1);
         }

      }

      boolean matches(Certificate[] var1) {
         if (!var1[0].getPublicKey().getAlgorithm().equals(this.keyAlgorithm)) {
            return false;
         } else if (this.sigKeyAlgorithm == null) {
            return true;
         } else if (var1.length > 1) {
            return this.sigKeyAlgorithm.equals(var1[1].getPublicKey().getAlgorithm());
         } else {
            X509Certificate var2 = (X509Certificate)var1[0];
            String var3 = var2.getSigAlgName().toUpperCase(Locale.ENGLISH);
            String var4 = "WITH" + this.sigKeyAlgorithm.toUpperCase(Locale.ENGLISH);
            return var3.contains(var4);
         }
      }
   }

   private static class SizedMap<K, V> extends LinkedHashMap<K, V> {
      private static final long serialVersionUID = -8211222668790986062L;

      private SizedMap() {
      }

      protected boolean removeEldestEntry(java.util.Map.Entry<K, V> var1) {
         return this.size() > 10;
      }
   }
}
