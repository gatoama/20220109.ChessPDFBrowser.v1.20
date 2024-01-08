package com.frojasg1.sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;

class UTF_32Coder {
   protected static final int BOM_BIG = 65279;
   protected static final int BOM_LITTLE = -131072;
   protected static final int NONE = 0;
   protected static final int BIG = 1;
   protected static final int LITTLE = 2;

   UTF_32Coder() {
   }

   protected static class Decoder extends CharsetDecoder {
      private int currentBO;
      private int expectedBO;

      protected Decoder(Charset var1, int var2) {
         super(var1, 0.25F, 1.0F);
         this.expectedBO = var2;
         this.currentBO = 0;
      }

      private int getCP(ByteBuffer var1) {
         return this.currentBO == 1 ? (var1.get() & 255) << 24 | (var1.get() & 255) << 16 | (var1.get() & 255) << 8 | var1.get() & 255 : var1.get() & 255 | (var1.get() & 255) << 8 | (var1.get() & 255) << 16 | (var1.get() & 255) << 24;
      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         if (var1.remaining() < 4) {
            return CoderResult.UNDERFLOW;
         } else {
            int var3 = var1.position();

            CoderResult var5;
            try {
               int var4;
               if (this.currentBO == 0) {
                  var4 = (var1.get() & 255) << 24 | (var1.get() & 255) << 16 | (var1.get() & 255) << 8 | var1.get() & 255;
                  if (var4 == 65279 && this.expectedBO != 2) {
                     this.currentBO = 1;
                     var3 += 4;
                  } else if (var4 == -131072 && this.expectedBO != 1) {
                     this.currentBO = 2;
                     var3 += 4;
                  } else {
                     if (this.expectedBO == 0) {
                        this.currentBO = 1;
                     } else {
                        this.currentBO = this.expectedBO;
                     }

                     var1.position(var3);
                  }
               }

               while(var1.remaining() >= 4) {
                  var4 = this.getCP(var1);
                  if (Character.isBmpCodePoint(var4)) {
                     if (!var2.hasRemaining()) {
                        var5 = CoderResult.OVERFLOW;
                        return var5;
                     }

                     var3 += 4;
                     var2.put((char)var4);
                  } else {
                     if (!Character.isValidCodePoint(var4)) {
                        var5 = CoderResult.malformedForLength(4);
                        return var5;
                     }

                     if (var2.remaining() < 2) {
                        var5 = CoderResult.OVERFLOW;
                        return var5;
                     }

                     var3 += 4;
                     var2.put(Character.highSurrogate(var4));
                     var2.put(Character.lowSurrogate(var4));
                  }
               }

               var5 = CoderResult.UNDERFLOW;
            } finally {
               var1.position(var3);
            }

            return var5;
         }
      }

      protected void implReset() {
         this.currentBO = 0;
      }
   }

   protected static class Encoder extends CharsetEncoder {
      private boolean doBOM = false;
      private boolean doneBOM = true;
      private int byteOrder;

      protected void put(int var1, ByteBuffer var2) {
         if (this.byteOrder == 1) {
            var2.put((byte)(var1 >> 24));
            var2.put((byte)(var1 >> 16));
            var2.put((byte)(var1 >> 8));
            var2.put((byte)var1);
         } else {
            var2.put((byte)var1);
            var2.put((byte)(var1 >> 8));
            var2.put((byte)(var1 >> 16));
            var2.put((byte)(var1 >> 24));
         }

      }

      protected Encoder(Charset var1, int var2, boolean var3) {
         super(var1, 4.0F, var3 ? 8.0F : 4.0F, var2 == 1 ? new byte[]{0, 0, -1, -3} : new byte[]{-3, -1, 0, 0});
         this.byteOrder = var2;
         this.doBOM = var3;
         this.doneBOM = !var3;
      }

      protected CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         int var3 = var1.position();
         if (!this.doneBOM && var1.hasRemaining()) {
            if (var2.remaining() < 4) {
               return CoderResult.OVERFLOW;
            }

            this.put(65279, var2);
            this.doneBOM = true;
         }

         try {
            while(true) {
               while(var1.hasRemaining()) {
                  char var10 = var1.get();
                  CoderResult var5;
                  if (Character.isSurrogate(var10)) {
                     if (!Character.isHighSurrogate(var10)) {
                        var5 = CoderResult.malformedForLength(1);
                        return var5;
                     }

                     if (!var1.hasRemaining()) {
                        var5 = CoderResult.UNDERFLOW;
                        return var5;
                     }

                     char var11 = var1.get();
                     CoderResult var6;
                     if (!Character.isLowSurrogate(var11)) {
                        var6 = CoderResult.malformedForLength(1);
                        return var6;
                     }

                     if (var2.remaining() < 4) {
                        var6 = CoderResult.OVERFLOW;
                        return var6;
                     }

                     var3 += 2;
                     this.put(Character.toCodePoint(var10, var11), var2);
                  } else {
                     if (var2.remaining() < 4) {
                        var5 = CoderResult.OVERFLOW;
                        return var5;
                     }

                     ++var3;
                     this.put(var10, var2);
                  }
               }

               CoderResult var4 = CoderResult.UNDERFLOW;
               return var4;
            }
         } finally {
            var1.position(var3);
         }
      }

      protected void implReset() {
         this.doneBOM = !this.doBOM;
      }
   }
}
