package com.frojasg1.sun.nio.cs;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CoderResult;

abstract class UnicodeDecoder extends CharsetDecoder {
   protected static final char BYTE_ORDER_MARK = '\ufeff';
   protected static final char REVERSED_MARK = '\ufffe';
   protected static final int NONE = 0;
   protected static final int BIG = 1;
   protected static final int LITTLE = 2;
   private final int expectedByteOrder;
   private int currentByteOrder;
   private int defaultByteOrder;

   public UnicodeDecoder(Charset var1, int var2) {
      super(var1, 0.5F, 1.0F);
      this.defaultByteOrder = 1;
      this.expectedByteOrder = this.currentByteOrder = var2;
   }

   public UnicodeDecoder(Charset var1, int var2, int var3) {
      this(var1, var2);
      this.defaultByteOrder = var3;
   }

   private char decode(int var1, int var2) {
      return this.currentByteOrder == 1 ? (char)(var1 << 8 | var2) : (char)(var2 << 8 | var1);
   }

   protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
      int var3 = var1.position();

      try {
         while(true) {
            while(var1.remaining() > 1) {
               int var12 = var1.get() & 255;
               int var5 = var1.get() & 255;
               char var6;
               if (this.currentByteOrder == 0) {
                  var6 = (char)(var12 << 8 | var5);
                  if (var6 == '\ufeff') {
                     this.currentByteOrder = 1;
                     var3 += 2;
                     continue;
                  }

                  if (var6 == '\ufffe') {
                     this.currentByteOrder = 2;
                     var3 += 2;
                     continue;
                  }

                  this.currentByteOrder = this.defaultByteOrder;
               }

               var6 = this.decode(var12, var5);
               CoderResult var13;
               if (var6 == '\ufffe') {
                  var13 = CoderResult.malformedForLength(2);
                  return var13;
               }

               if (!Character.isSurrogate(var6)) {
                  if (!var2.hasRemaining()) {
                     var13 = CoderResult.OVERFLOW;
                     return var13;
                  }

                  var3 += 2;
                  var2.put(var6);
               } else {
                  if (!Character.isHighSurrogate(var6)) {
                     var13 = CoderResult.malformedForLength(2);
                     return var13;
                  }

                  if (var1.remaining() < 2) {
                     var13 = CoderResult.UNDERFLOW;
                     return var13;
                  }

                  char var7 = this.decode(var1.get() & 255, var1.get() & 255);
                  CoderResult var8;
                  if (!Character.isLowSurrogate(var7)) {
                     var8 = CoderResult.malformedForLength(4);
                     return var8;
                  }

                  if (var2.remaining() < 2) {
                     var8 = CoderResult.OVERFLOW;
                     return var8;
                  }

                  var3 += 4;
                  var2.put(var6);
                  var2.put(var7);
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
      this.currentByteOrder = this.expectedByteOrder;
   }
}
