package com.frojasg1.sun.security.ssl;

import java.security.AlgorithmConstraints;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.PrivateKey;
import java.security.Security;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import com.frojasg1.sun.security.util.KeyUtil;

final class SignatureAndHashAlgorithm {
   static final int SUPPORTED_ALG_PRIORITY_MAX_NUM = 240;
   private static final Set<CryptoPrimitive> SIGNATURE_PRIMITIVE_SET;
   private static final Map<Integer, SignatureAndHashAlgorithm> supportedMap;
   private static final Map<Integer, SignatureAndHashAlgorithm> priorityMap;
   private SignatureAndHashAlgorithm.HashAlgorithm hash;
   private int id;
   private String algorithm;
   private int priority;

   private SignatureAndHashAlgorithm(SignatureAndHashAlgorithm.HashAlgorithm var1, SignatureAndHashAlgorithm.SignatureAlgorithm var2, String var3, int var4) {
      this.hash = var1;
      this.algorithm = var3;
      this.id = (var1.value & 255) << 8 | var2.value & 255;
      this.priority = var4;
   }

   private SignatureAndHashAlgorithm(String var1, int var2, int var3) {
      this.hash = SignatureAndHashAlgorithm.HashAlgorithm.valueOf(var2 >> 8 & 255);
      this.algorithm = var1;
      this.id = var2;
      this.priority = 240 + var3 + 1;
   }

   static SignatureAndHashAlgorithm valueOf(int var0, int var1, int var2) {
      var0 &= 255;
      var1 &= 255;
      int var3 = var0 << 8 | var1;
      SignatureAndHashAlgorithm var4 = (SignatureAndHashAlgorithm)supportedMap.get(var3);
      if (var4 == null) {
         var4 = new SignatureAndHashAlgorithm("Unknown (hash:0x" + Integer.toString(var0, 16) + ", signature:0x" + Integer.toString(var1, 16) + ")", var3, var2);
      }

      return var4;
   }

   int getHashValue() {
      return this.id >> 8 & 255;
   }

   int getSignatureValue() {
      return this.id & 255;
   }

   String getAlgorithmName() {
      return this.algorithm;
   }

   static int sizeInRecord() {
      return 2;
   }

   static Collection<SignatureAndHashAlgorithm> getSupportedAlgorithms(AlgorithmConstraints var0) {
      ArrayList var1 = new ArrayList();
      Iterator var2 = priorityMap.values().iterator();

      while(var2.hasNext()) {
         SignatureAndHashAlgorithm var3 = (SignatureAndHashAlgorithm)var2.next();
         if (var3.priority <= 240 && var0.permits(SIGNATURE_PRIMITIVE_SET, var3.algorithm, (AlgorithmParameters)null)) {
            var1.add(var3);
         }
      }

      return var1;
   }

   static Collection<SignatureAndHashAlgorithm> getSupportedAlgorithms(AlgorithmConstraints var0, Collection<SignatureAndHashAlgorithm> var1) {
      ArrayList var2 = new ArrayList();
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         SignatureAndHashAlgorithm var4 = (SignatureAndHashAlgorithm)var3.next();
         if (var4.priority <= 240 && var0.permits(SIGNATURE_PRIMITIVE_SET, var4.algorithm, (AlgorithmParameters)null)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   static String[] getAlgorithmNames(Collection<SignatureAndHashAlgorithm> var0) {
      ArrayList var1 = new ArrayList();
      if (var0 != null) {
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            SignatureAndHashAlgorithm var3 = (SignatureAndHashAlgorithm)var2.next();
            var1.add(var3.algorithm);
         }
      }

      String[] var4 = new String[var1.size()];
      return (String[])var1.toArray(var4);
   }

   static Set<String> getHashAlgorithmNames(Collection<SignatureAndHashAlgorithm> var0) {
      HashSet var1 = new HashSet();
      if (var0 != null) {
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            SignatureAndHashAlgorithm var3 = (SignatureAndHashAlgorithm)var2.next();
            if (var3.hash.value > 0) {
               var1.add(var3.hash.standardName);
            }
         }
      }

      return var1;
   }

   static String getHashAlgorithmName(SignatureAndHashAlgorithm var0) {
      return var0.hash.standardName;
   }

   private static void supports(SignatureAndHashAlgorithm.HashAlgorithm var0, SignatureAndHashAlgorithm.SignatureAlgorithm var1, String var2, int var3) {
      SignatureAndHashAlgorithm var4 = new SignatureAndHashAlgorithm(var0, var1, var2, var3);
      if (supportedMap.put(var4.id, var4) != null) {
         throw new RuntimeException("Duplicate SignatureAndHashAlgorithm definition, id: " + var4.id);
      } else if (priorityMap.put(var4.priority, var4) != null) {
         throw new RuntimeException("Duplicate SignatureAndHashAlgorithm definition, priority: " + var4.priority);
      }
   }

   static SignatureAndHashAlgorithm getPreferableAlgorithm(Collection<SignatureAndHashAlgorithm> var0, String var1) {
      return getPreferableAlgorithm(var0, var1, (PrivateKey)null);
   }

   static SignatureAndHashAlgorithm getPreferableAlgorithm(Collection<SignatureAndHashAlgorithm> var0, String var1, PrivateKey var2) {
      int var3 = getMaxDigestLength(var2);
      Iterator var4 = var0.iterator();

      SignatureAndHashAlgorithm var5;
      int var6;
      do {
         do {
            if (!var4.hasNext()) {
               return null;
            }

            var5 = (SignatureAndHashAlgorithm)var4.next();
            var6 = var5.id & 255;
         } while(var1 != null && (!var1.equalsIgnoreCase("rsa") || var6 != SignatureAndHashAlgorithm.SignatureAlgorithm.RSA.value) && (!var1.equalsIgnoreCase("dsa") || var6 != SignatureAndHashAlgorithm.SignatureAlgorithm.DSA.value) && (!var1.equalsIgnoreCase("ecdsa") || var6 != SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA.value) && (!var1.equalsIgnoreCase("ec") || var6 != SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA.value));
      } while(var5.priority > 240 || var5.hash.length > var3);

      return var5;
   }

   private static int getMaxDigestLength(PrivateKey var0) {
      int var1 = 2147483647;
      if (var0 != null && "rsa".equalsIgnoreCase(var0.getAlgorithm())) {
         int var2 = KeyUtil.getKeySize(var0);
         if (var2 >= 768) {
            var1 = SignatureAndHashAlgorithm.HashAlgorithm.SHA512.length;
         } else if (var2 >= 512 && var2 < 768) {
            var1 = SignatureAndHashAlgorithm.HashAlgorithm.SHA256.length;
         } else if (var2 > 0 && var2 < 512) {
            var1 = SignatureAndHashAlgorithm.HashAlgorithm.SHA1.length;
         }
      }

      return var1;
   }

   static {
      SIGNATURE_PRIMITIVE_SET = Collections.unmodifiableSet(EnumSet.of(CryptoPrimitive.SIGNATURE));
      supportedMap = Collections.synchronizedSortedMap(new TreeMap());
      priorityMap = Collections.synchronizedSortedMap(new TreeMap());
      synchronized(supportedMap) {
         short var1 = 240;
         int var4 = var1 - 1;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.MD5, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA, "MD5withRSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA1, SignatureAndHashAlgorithm.SignatureAlgorithm.DSA, "SHA1withDSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA1, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA, "SHA1withRSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA1, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA, "SHA1withECDSA", var4);
         if (Security.getProvider("SunMSCAPI") == null) {
            --var4;
            supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA224, SignatureAndHashAlgorithm.SignatureAlgorithm.DSA, "SHA224withDSA", var4);
            --var4;
            supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA224, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA, "SHA224withRSA", var4);
            --var4;
            supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA224, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA, "SHA224withECDSA", var4);
         }

         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA256, SignatureAndHashAlgorithm.SignatureAlgorithm.DSA, "SHA256withDSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA256, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA, "SHA256withRSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA256, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA, "SHA256withECDSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA384, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA, "SHA384withRSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA384, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA, "SHA384withECDSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA512, SignatureAndHashAlgorithm.SignatureAlgorithm.RSA, "SHA512withRSA", var4);
         --var4;
         supports(SignatureAndHashAlgorithm.HashAlgorithm.SHA512, SignatureAndHashAlgorithm.SignatureAlgorithm.ECDSA, "SHA512withECDSA", var4);
      }
   }

   static enum HashAlgorithm {
      UNDEFINED("undefined", "", -1, -1),
      NONE("none", "NONE", 0, -1),
      MD5("md5", "MD5", 1, 16),
      SHA1("sha1", "SHA-1", 2, 20),
      SHA224("sha224", "SHA-224", 3, 28),
      SHA256("sha256", "SHA-256", 4, 32),
      SHA384("sha384", "SHA-384", 5, 48),
      SHA512("sha512", "SHA-512", 6, 64);

      final String name;
      final String standardName;
      final int value;
      final int length;

      private HashAlgorithm(String var3, String var4, int var5, int var6) {
         this.name = var3;
         this.standardName = var4;
         this.value = var5;
         this.length = var6;
      }

      static SignatureAndHashAlgorithm.HashAlgorithm valueOf(int var0) {
         SignatureAndHashAlgorithm.HashAlgorithm var1 = UNDEFINED;
         switch(var0) {
         case 0:
            var1 = NONE;
            break;
         case 1:
            var1 = MD5;
            break;
         case 2:
            var1 = SHA1;
            break;
         case 3:
            var1 = SHA224;
            break;
         case 4:
            var1 = SHA256;
            break;
         case 5:
            var1 = SHA384;
            break;
         case 6:
            var1 = SHA512;
         }

         return var1;
      }
   }

   static enum SignatureAlgorithm {
      UNDEFINED("undefined", -1),
      ANONYMOUS("anonymous", 0),
      RSA("rsa", 1),
      DSA("dsa", 2),
      ECDSA("ecdsa", 3);

      final String name;
      final int value;

      private SignatureAlgorithm(String var3, int var4) {
         this.name = var3;
         this.value = var4;
      }

      static SignatureAndHashAlgorithm.SignatureAlgorithm valueOf(int var0) {
         SignatureAndHashAlgorithm.SignatureAlgorithm var1 = UNDEFINED;
         switch(var0) {
         case 0:
            var1 = ANONYMOUS;
            break;
         case 1:
            var1 = RSA;
            break;
         case 2:
            var1 = DSA;
            break;
         case 3:
            var1 = ECDSA;
         }

         return var1;
      }
   }
}
