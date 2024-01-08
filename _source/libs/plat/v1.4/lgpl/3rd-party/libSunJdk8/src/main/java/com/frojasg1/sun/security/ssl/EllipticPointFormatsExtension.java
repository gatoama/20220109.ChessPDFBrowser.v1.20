package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.util.ArrayList;
import javax.net.ssl.SSLProtocolException;

final class EllipticPointFormatsExtension extends HelloExtension {
   static final int FMT_UNCOMPRESSED = 0;
   static final int FMT_ANSIX962_COMPRESSED_PRIME = 1;
   static final int FMT_ANSIX962_COMPRESSED_CHAR2 = 2;
   static final HelloExtension DEFAULT = new EllipticPointFormatsExtension(new byte[]{0});
   private final byte[] formats;

   private EllipticPointFormatsExtension(byte[] var1) {
      super(ExtensionType.EXT_EC_POINT_FORMATS);
      this.formats = var1;
   }

   EllipticPointFormatsExtension(HandshakeInStream var1, int var2) throws IOException {
      super(ExtensionType.EXT_EC_POINT_FORMATS);
      this.formats = var1.getBytes8();
      boolean var3 = false;
      byte[] var4 = this.formats;
      int var5 = var4.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         byte var7 = var4[var6];
         if (var7 == 0) {
            var3 = true;
            break;
         }
      }

      if (!var3) {
         throw new SSLProtocolException("Peer does not support uncompressed points");
      }
   }

   int length() {
      return 5 + this.formats.length;
   }

   void send(HandshakeOutStream var1) throws IOException {
      var1.putInt16(this.type.id);
      var1.putInt16(this.formats.length + 1);
      var1.putBytes8(this.formats);
   }

   private static String toString(byte var0) {
      int var1 = var0 & 255;
      switch(var1) {
      case 0:
         return "uncompressed";
      case 1:
         return "ansiX962_compressed_prime";
      case 2:
         return "ansiX962_compressed_char2";
      default:
         return "unknown-" + var1;
      }
   }

   public String toString() {
      ArrayList var1 = new ArrayList();
      byte[] var2 = this.formats;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         byte var5 = var2[var4];
         var1.add(toString(var5));
      }

      return "Extension " + this.type + ", formats: " + var1;
   }
}
