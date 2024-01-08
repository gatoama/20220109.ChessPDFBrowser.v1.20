package com.frojasg1.sun.security.ssl;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

final class HandshakeHash {
   private int version = -1;
   private ByteArrayOutputStream data = new ByteArrayOutputStream();
   private MessageDigest md5;
   private MessageDigest sha;
   private final int clonesNeeded;
   private MessageDigest finMD;

   HandshakeHash(boolean var1) {
      this.clonesNeeded = var1 ? 5 : 4;
   }

   void update(byte[] var1, int var2, int var3) {
      switch(this.version) {
      case 1:
         this.md5.update(var1, var2, var3);
         this.sha.update(var1, var2, var3);
         break;
      default:
         if (this.finMD != null) {
            this.finMD.update(var1, var2, var3);
         }

         this.data.write(var1, var2, var3);
      }

   }

   void reset() {
      if (this.version != -1) {
         throw new RuntimeException("reset() can be only be called before protocolDetermined");
      } else {
         this.data.reset();
      }
   }

   void protocolDetermined(ProtocolVersion var1) {
      if (this.version == -1) {
         this.version = var1.compareTo(ProtocolVersion.TLS12) >= 0 ? 2 : 1;
         switch(this.version) {
         case 1:
            try {
               this.md5 = CloneableDigest.getDigest("MD5", this.clonesNeeded);
               this.sha = CloneableDigest.getDigest("SHA", this.clonesNeeded);
            } catch (NoSuchAlgorithmException var3) {
               throw new RuntimeException("Algorithm MD5 or SHA not available", var3);
            }

            byte[] var2 = this.data.toByteArray();
            this.update(var2, 0, var2.length);
         case 2:
         default:
         }
      }
   }

   MessageDigest getMD5Clone() {
      if (this.version != 1) {
         throw new RuntimeException("getMD5Clone() can be only be called for TLS 1.1");
      } else {
         return cloneDigest(this.md5);
      }
   }

   MessageDigest getSHAClone() {
      if (this.version != 1) {
         throw new RuntimeException("getSHAClone() can be only be called for TLS 1.1");
      } else {
         return cloneDigest(this.sha);
      }
   }

   private static MessageDigest cloneDigest(MessageDigest var0) {
      try {
         return (MessageDigest)var0.clone();
      } catch (CloneNotSupportedException var2) {
         throw new RuntimeException("Could not clone digest", var2);
      }
   }

   private static String normalizeAlgName(String var0) {
      var0 = var0.toUpperCase(Locale.US);
      if (var0.startsWith("SHA")) {
         if (var0.length() == 3) {
            return "SHA-1";
         }

         if (var0.charAt(3) != '-') {
            return "SHA-" + var0.substring(3);
         }
      }

      return var0;
   }

   void setFinishedAlg(String var1) {
      if (var1 == null) {
         throw new RuntimeException("setFinishedAlg's argument cannot be null");
      } else if (this.finMD == null) {
         try {
            this.finMD = CloneableDigest.getDigest(normalizeAlgName(var1), 4);
         } catch (NoSuchAlgorithmException var3) {
            throw new Error(var3);
         }

         this.finMD.update(this.data.toByteArray());
      }
   }

   byte[] getAllHandshakeMessages() {
      return this.data.toByteArray();
   }

   byte[] getFinishedHash() {
      try {
         return cloneDigest(this.finMD).digest();
      } catch (Exception var2) {
         throw new Error("Error during hash calculation", var2);
      }
   }
}
