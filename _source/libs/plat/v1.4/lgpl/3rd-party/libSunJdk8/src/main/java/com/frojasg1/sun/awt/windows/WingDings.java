package com.frojasg1.sun.awt.windows;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

public final class WingDings extends Charset {
   public WingDings() {
      super("WingDings", (String[])null);
   }

   public CharsetEncoder newEncoder() {
      return new WingDings.Encoder(this);
   }

   public CharsetDecoder newDecoder() {
      throw new Error("Decoder isn't implemented for WingDings Charset");
   }

   public boolean contains(Charset var1) {
      return var1 instanceof WingDings;
   }

   private static class Encoder extends CharsetEncoder {
      private static byte[] table = new byte[]{0, 35, 34, 0, 0, 0, 41, 62, 81, 42, 0, 0, 65, 63, 0, 0, 0, 0, 0, -4, 0, 0, 0, -5, 0, 0, 0, 0, 0, 0, 86, 0, 88, 89, 0, 0, 0, 0, 0, 0, 0, 0, -75, 0, 0, 0, 0, 0, -74, 0, 0, 0, -83, -81, -84, 0, 0, 0, 0, 0, 0, 0, 0, 124, 123, 0, 0, 0, 84, 0, 0, 0, 0, 0, 0, 0, 0, -90, 0, 0, 0, 113, 114, 0, 0, 0, 117, 0, 0, 0, 0, 0, 0, 125, 126, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -127, -126, -125, -124, -123, -122, -121, -120, -119, -118, -116, -115, -114, -113, -112, -111, -110, -109, -108, -107, -24, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -24, -40, 0, 0, -60, -58, 0, 0, -16, 0, 0, 0, 0, 0, 0, 0, 0, 0, -36, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

      public Encoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
      }

      public boolean canEncode(char var1) {
         if (var1 >= 9985 && var1 <= 10174) {
            return table[var1 - 9984] != 0;
         } else {
            return false;
         }
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();

         assert var4 <= var5;

         var4 = var4 <= var5 ? var4 : var5;
         byte[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         assert var7 <= var8;

         var7 = var7 <= var8 ? var7 : var8;

         try {
            while(var4 < var5) {
               char var9 = var3[var4];
               CoderResult var10;
               if (var8 - var7 < 1) {
                  var10 = CoderResult.OVERFLOW;
                  return var10;
               }

               if (!this.canEncode(var9)) {
                  var10 = CoderResult.unmappableForLength(1);
                  return var10;
               }

               ++var4;
               var6[var7++] = table[var9 - 9984];
            }

            CoderResult var14 = CoderResult.UNDERFLOW;
            return var14;
         } finally {
            var1.position(var4 - var1.arrayOffset());
            var2.position(var7 - var2.arrayOffset());
         }
      }

      public boolean isLegalReplacement(byte[] var1) {
         return true;
      }
   }
}
