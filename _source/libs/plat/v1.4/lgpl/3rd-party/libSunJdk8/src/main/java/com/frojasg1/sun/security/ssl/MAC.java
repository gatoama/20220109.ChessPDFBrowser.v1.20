package com.frojasg1.sun.security.ssl;

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.SecretKey;

final class MAC extends Authenticator {
   static final MAC NULL = new MAC();
   private static final byte[] nullMAC = new byte[0];
   private final CipherSuite.MacAlg macAlg;
   private final Mac mac;

   private MAC() {
      this.macAlg = CipherSuite.M_NULL;
      this.mac = null;
   }

   MAC(CipherSuite.MacAlg var1, ProtocolVersion var2, SecretKey var3) throws NoSuchAlgorithmException, InvalidKeyException {
      super(var2);
      this.macAlg = var1;
      boolean var5 = var2.v >= ProtocolVersion.TLS10.v;
      String var4;
      if (var1 == CipherSuite.M_MD5) {
         var4 = var5 ? "HmacMD5" : "SslMacMD5";
      } else if (var1 == CipherSuite.M_SHA) {
         var4 = var5 ? "HmacSHA1" : "SslMacSHA1";
      } else if (var1 == CipherSuite.M_SHA256) {
         var4 = "HmacSHA256";
      } else {
         if (var1 != CipherSuite.M_SHA384) {
            throw new RuntimeException("Unknown Mac " + var1);
         }

         var4 = "HmacSHA384";
      }

      this.mac = JsseJce.getMac(var4);
      this.mac.init(var3);
   }

   int MAClen() {
      return this.macAlg.size;
   }

   int hashBlockLen() {
      return this.macAlg.hashBlockSize;
   }

   int minimalPaddingLen() {
      return this.macAlg.minimalPaddingSize;
   }

   final byte[] compute(byte var1, byte[] var2, int var3, int var4, boolean var5) {
      if (this.macAlg.size == 0) {
         return nullMAC;
      } else {
         if (!var5) {
            byte[] var6 = this.acquireAuthenticationBytes(var1, var4);
            this.mac.update(var6);
         }

         this.mac.update(var2, var3, var4);
         return this.mac.doFinal();
      }
   }

   final byte[] compute(byte var1, ByteBuffer var2, boolean var3) {
      if (this.macAlg.size == 0) {
         return nullMAC;
      } else {
         if (!var3) {
            byte[] var4 = this.acquireAuthenticationBytes(var1, var2.remaining());
            this.mac.update(var4);
         }

         this.mac.update(var2);
         return this.mac.doFinal();
      }
   }
}
