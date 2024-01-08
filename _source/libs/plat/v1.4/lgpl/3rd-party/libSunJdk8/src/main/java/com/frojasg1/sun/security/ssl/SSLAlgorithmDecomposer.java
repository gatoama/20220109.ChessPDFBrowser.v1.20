package com.frojasg1.sun.security.ssl;

import java.util.HashSet;
import java.util.Set;
import com.frojasg1.sun.security.util.AlgorithmDecomposer;

class SSLAlgorithmDecomposer extends AlgorithmDecomposer {
   private final boolean onlyX509;

   SSLAlgorithmDecomposer(boolean var1) {
      this.onlyX509 = var1;
   }

   SSLAlgorithmDecomposer() {
      this(false);
   }

   private Set<String> decomposes(CipherSuite.KeyExchange var1) {
      HashSet var2 = new HashSet();
      switch(var1) {
      case K_NULL:
         if (!this.onlyX509) {
            var2.add("K_NULL");
         }
         break;
      case K_RSA:
         var2.add("RSA");
         break;
      case K_RSA_EXPORT:
         var2.add("RSA");
         var2.add("RSA_EXPORT");
         break;
      case K_DH_RSA:
         var2.add("RSA");
         var2.add("DH");
         var2.add("DiffieHellman");
         var2.add("DH_RSA");
         break;
      case K_DH_DSS:
         var2.add("DSA");
         var2.add("DSS");
         var2.add("DH");
         var2.add("DiffieHellman");
         var2.add("DH_DSS");
         break;
      case K_DHE_DSS:
         var2.add("DSA");
         var2.add("DSS");
         var2.add("DH");
         var2.add("DHE");
         var2.add("DiffieHellman");
         var2.add("DHE_DSS");
         break;
      case K_DHE_RSA:
         var2.add("RSA");
         var2.add("DH");
         var2.add("DHE");
         var2.add("DiffieHellman");
         var2.add("DHE_RSA");
         break;
      case K_DH_ANON:
         if (!this.onlyX509) {
            var2.add("ANON");
            var2.add("DH");
            var2.add("DiffieHellman");
            var2.add("DH_ANON");
         }
         break;
      case K_ECDH_ECDSA:
         var2.add("ECDH");
         var2.add("ECDSA");
         var2.add("ECDH_ECDSA");
         break;
      case K_ECDH_RSA:
         var2.add("ECDH");
         var2.add("RSA");
         var2.add("ECDH_RSA");
         break;
      case K_ECDHE_ECDSA:
         var2.add("ECDHE");
         var2.add("ECDSA");
         var2.add("ECDHE_ECDSA");
         break;
      case K_ECDHE_RSA:
         var2.add("ECDHE");
         var2.add("RSA");
         var2.add("ECDHE_RSA");
         break;
      case K_ECDH_ANON:
         if (!this.onlyX509) {
            var2.add("ECDH");
            var2.add("ANON");
            var2.add("ECDH_ANON");
         }
         break;
      case K_KRB5:
         if (!this.onlyX509) {
            var2.add("KRB5");
         }
         break;
      case K_KRB5_EXPORT:
         if (!this.onlyX509) {
            var2.add("KRB5_EXPORT");
         }
      }

      return var2;
   }

   private Set<String> decomposes(CipherSuite.BulkCipher var1) {
      HashSet var2 = new HashSet();
      if (var1.transformation != null) {
         var2.addAll(super.decompose(var1.transformation));
      }

      if (var1 == CipherSuite.B_NULL) {
         var2.add("C_NULL");
      } else if (var1 == CipherSuite.B_RC2_40) {
         var2.add("RC2_CBC_40");
      } else if (var1 == CipherSuite.B_RC4_40) {
         var2.add("RC4_40");
      } else if (var1 == CipherSuite.B_RC4_128) {
         var2.add("RC4_128");
      } else if (var1 == CipherSuite.B_DES_40) {
         var2.add("DES40_CBC");
         var2.add("DES_CBC_40");
      } else if (var1 == CipherSuite.B_DES) {
         var2.add("DES_CBC");
      } else if (var1 == CipherSuite.B_3DES) {
         var2.add("3DES_EDE_CBC");
      } else if (var1 == CipherSuite.B_AES_128) {
         var2.add("AES_128_CBC");
      } else if (var1 == CipherSuite.B_AES_256) {
         var2.add("AES_256_CBC");
      } else if (var1 == CipherSuite.B_AES_128_GCM) {
         var2.add("AES_128_GCM");
      } else if (var1 == CipherSuite.B_AES_256_GCM) {
         var2.add("AES_256_GCM");
      }

      return var2;
   }

   private Set<String> decomposes(CipherSuite.MacAlg var1, CipherSuite.BulkCipher var2) {
      HashSet var3 = new HashSet();
      if (var1 == CipherSuite.M_NULL && var2.cipherType != CipherSuite.CipherType.AEAD_CIPHER) {
         var3.add("M_NULL");
      } else if (var1 == CipherSuite.M_MD5) {
         var3.add("MD5");
         var3.add("HmacMD5");
      } else if (var1 == CipherSuite.M_SHA) {
         var3.add("SHA1");
         var3.add("SHA-1");
         var3.add("HmacSHA1");
      } else if (var1 == CipherSuite.M_SHA256) {
         var3.add("SHA256");
         var3.add("SHA-256");
         var3.add("HmacSHA256");
      } else if (var1 == CipherSuite.M_SHA384) {
         var3.add("SHA384");
         var3.add("SHA-384");
         var3.add("HmacSHA384");
      }

      return var3;
   }

   private Set<String> decompose(CipherSuite.KeyExchange var1, CipherSuite.BulkCipher var2, CipherSuite.MacAlg var3) {
      HashSet var4 = new HashSet();
      if (var1 != null) {
         var4.addAll(this.decomposes(var1));
      }

      if (this.onlyX509) {
         return var4;
      } else {
         if (var2 != null) {
            var4.addAll(this.decomposes(var2));
         }

         if (var3 != null) {
            var4.addAll(this.decomposes(var3, var2));
         }

         return var4;
      }
   }

   public Set<String> decompose(String var1) {
      if (var1.startsWith("SSL_") || var1.startsWith("TLS_")) {
         CipherSuite var2 = null;

         try {
            var2 = CipherSuite.valueOf(var1);
         } catch (IllegalArgumentException var4) {
         }

         if (var2 != null) {
            return this.decompose(var2.keyExchange, var2.cipher, var2.macAlg);
         }
      }

      return super.decompose(var1);
   }
}
