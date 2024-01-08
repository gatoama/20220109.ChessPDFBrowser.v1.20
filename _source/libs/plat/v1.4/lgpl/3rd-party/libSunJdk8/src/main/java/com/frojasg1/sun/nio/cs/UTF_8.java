package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.ArrayDecoder;
import com.frojasg1.sun.nio.cs.ArrayEncoder;
import com.frojasg1.sun.nio.cs.StandardCharsets;
import com.frojasg1.sun.nio.cs.Surrogate;
import com.frojasg1.sun.nio.cs.Unicode;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.CodingErrorAction;

class UTF_8 extends com.frojasg1.sun.nio.cs.Unicode {
   public UTF_8() {
      super("UTF-8", StandardCharsets.aliases_UTF_8);
   }

   public String historicalName() {
      return "UTF8";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_8.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_8.Encoder(this);
   }

   private static final void updatePositions(Buffer var0, int var1, Buffer var2, int var3) {
      var0.position(var1 - var0.arrayOffset());
      var2.position(var3 - var2.arrayOffset());
   }

   private static class Decoder extends CharsetDecoder implements ArrayDecoder {
      private Decoder(Charset var1) {
         super(var1, 1.0F, 1.0F);
      }

      private static boolean isNotContinuation(int var0) {
         return (var0 & 192) != 128;
      }

      private static boolean isMalformed3(int var0, int var1, int var2) {
         return var0 == -32 && (var1 & 224) == 128 || (var1 & 192) != 128 || (var2 & 192) != 128;
      }

      private static boolean isMalformed3_2(int var0, int var1) {
         return var0 == -32 && (var1 & 224) == 128 || (var1 & 192) != 128;
      }

      private static boolean isMalformed4(int var0, int var1, int var2) {
         return (var0 & 192) != 128 || (var1 & 192) != 128 || (var2 & 192) != 128;
      }

      private static boolean isMalformed4_2(int var0, int var1) {
         return var0 == 240 && (var1 < 144 || var1 > 191) || var0 == 244 && (var1 & 240) != 128 || (var1 & 192) != 128;
      }

      private static boolean isMalformed4_3(int var0) {
         return (var0 & 192) != 128;
      }

      private static CoderResult lookupN(ByteBuffer var0, int var1) {
         for(int var2 = 1; var2 < var1; ++var2) {
            if (isNotContinuation(var0.get())) {
               return CoderResult.malformedForLength(var2);
            }
         }

         return CoderResult.malformedForLength(var1);
      }

      private static CoderResult malformedN(ByteBuffer var0, int var1) {
         switch(var1) {
         case 1:
         case 2:
            return CoderResult.malformedForLength(1);
         case 3:
            byte var4 = var0.get();
            byte var5 = var0.get();
            return CoderResult.malformedForLength((var4 != -32 || (var5 & 224) != 128) && !isNotContinuation(var5) ? 2 : 1);
         case 4:
            int var2 = var0.get() & 255;
            int var3 = var0.get() & 255;
            if (var2 <= 244 && (var2 != 240 || var3 >= 144 && var3 <= 191) && (var2 != 244 || (var3 & 240) == 128) && !isNotContinuation(var3)) {
               if (isNotContinuation(var0.get())) {
                  return CoderResult.malformedForLength(2);
               }

               return CoderResult.malformedForLength(3);
            }

            return CoderResult.malformedForLength(1);
         default:
            assert false;

            return null;
         }
      }

      private static CoderResult malformed(ByteBuffer var0, int var1, CharBuffer var2, int var3, int var4) {
         var0.position(var1 - var0.arrayOffset());
         CoderResult var5 = malformedN(var0, var4);
         UTF_8.updatePositions(var0, var1, var2, var3);
         return var5;
      }

      private static CoderResult malformed(ByteBuffer var0, int var1, int var2) {
         var0.position(var1);
         CoderResult var3 = malformedN(var0, var2);
         var0.position(var1);
         return var3;
      }

      private static CoderResult malformedForLength(ByteBuffer var0, int var1, CharBuffer var2, int var3, int var4) {
         UTF_8.updatePositions(var0, var1, var2, var3);
         return CoderResult.malformedForLength(var4);
      }

      private static CoderResult malformedForLength(ByteBuffer var0, int var1, int var2) {
         var0.position(var1);
         return CoderResult.malformedForLength(var2);
      }

      private static CoderResult xflow(Buffer var0, int var1, int var2, Buffer var3, int var4, int var5) {
         UTF_8.updatePositions(var0, var1, var3, var4);
         return var5 != 0 && var2 - var1 >= var5 ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
      }

      private static CoderResult xflow(Buffer var0, int var1, int var2) {
         var0.position(var1);
         return var2 != 0 && var0.remaining() >= var2 ? CoderResult.OVERFLOW : CoderResult.UNDERFLOW;
      }

      private CoderResult decodeArrayLoop(ByteBuffer var1, CharBuffer var2) {
         byte[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         char[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         for(int var9 = var7 + Math.min(var5 - var4, var8 - var7); var7 < var9 && var3[var4] >= 0; var6[var7++] = (char)var3[var4++]) {
         }

         while(true) {
            while(var4 < var5) {
               byte var10 = var3[var4];
               if (var10 < 0) {
                  if (var10 >> 5 == -2 && (var10 & 30) != 0) {
                     if (var5 - var4 < 2 || var7 >= var8) {
                        return xflow(var1, var4, var5, var2, var7, 2);
                     }

                     byte var17 = var3[var4 + 1];
                     if (isNotContinuation(var17)) {
                        return malformedForLength(var1, var4, var2, var7, 1);
                     }

                     var6[var7++] = (char)(var10 << 6 ^ var17 ^ 3968);
                     var4 += 2;
                  } else {
                     int var11;
                     byte var12;
                     byte var13;
                     if (var10 >> 4 == -2) {
                        var11 = var5 - var4;
                        if (var11 < 3 || var7 >= var8) {
                           if (var11 > 1 && isMalformed3_2(var10, var3[var4 + 1])) {
                              return malformedForLength(var1, var4, var2, var7, 1);
                           }

                           return xflow(var1, var4, var5, var2, var7, 3);
                        }

                        var12 = var3[var4 + 1];
                        var13 = var3[var4 + 2];
                        if (isMalformed3(var10, var12, var13)) {
                           return malformed(var1, var4, var2, var7, 3);
                        }

                        char var18 = (char)(var10 << 12 ^ var12 << 6 ^ var13 ^ -123008);
                        if (Character.isSurrogate(var18)) {
                           return malformedForLength(var1, var4, var2, var7, 3);
                        }

                        var6[var7++] = var18;
                        var4 += 3;
                     } else {
                        if (var10 >> 3 != -2) {
                           return malformed(var1, var4, var2, var7, 1);
                        }

                        var11 = var5 - var4;
                        if (var11 >= 4 && var8 - var7 >= 2) {
                           var12 = var3[var4 + 1];
                           var13 = var3[var4 + 2];
                           byte var14 = var3[var4 + 3];
                           int var15 = var10 << 18 ^ var12 << 12 ^ var13 << 6 ^ var14 ^ 3678080;
                           if (!isMalformed4(var12, var13, var14) && Character.isSupplementaryCodePoint(var15)) {
                              var6[var7++] = Character.highSurrogate(var15);
                              var6[var7++] = Character.lowSurrogate(var15);
                              var4 += 4;
                              continue;
                           }

                           return malformed(var1, var4, var2, var7, 4);
                        }

                        int var16 = var10 & 255;
                        if (var16 <= 244 && (var11 <= 1 || !isMalformed4_2(var16, var3[var4 + 1] & 255))) {
                           if (var11 > 2 && isMalformed4_3(var3[var4 + 2])) {
                              return malformedForLength(var1, var4, var2, var7, 2);
                           }

                           return xflow(var1, var4, var5, var2, var7, 4);
                        }

                        return malformedForLength(var1, var4, var2, var7, 1);
                     }
                  }
               } else {
                  if (var7 >= var8) {
                     return xflow(var1, var4, var5, var2, var7, 1);
                  }

                  var6[var7++] = (char)var10;
                  ++var4;
               }
            }

            return xflow(var1, var4, var5, var2, var7, 0);
         }
      }

      private CoderResult decodeBufferLoop(ByteBuffer var1, CharBuffer var2) {
         int var3 = var1.position();
         int var4 = var1.limit();

         while(true) {
            while(var3 < var4) {
               byte var5 = var1.get();
               if (var5 < 0) {
                  if (var5 >> 5 == -2 && (var5 & 30) != 0) {
                     if (var4 - var3 < 2 || var2.remaining() < 1) {
                        return xflow(var1, var3, 2);
                     }

                     byte var12 = var1.get();
                     if (isNotContinuation(var12)) {
                        return malformedForLength(var1, var3, 1);
                     }

                     var2.put((char)(var5 << 6 ^ var12 ^ 3968));
                     var3 += 2;
                  } else {
                     int var6;
                     byte var7;
                     byte var8;
                     if (var5 >> 4 == -2) {
                        var6 = var4 - var3;
                        if (var6 < 3 || var2.remaining() < 1) {
                           if (var6 > 1 && isMalformed3_2(var5, var1.get())) {
                              return malformedForLength(var1, var3, 1);
                           }

                           return xflow(var1, var3, 3);
                        }

                        var7 = var1.get();
                        var8 = var1.get();
                        if (isMalformed3(var5, var7, var8)) {
                           return malformed(var1, var3, 3);
                        }

                        char var13 = (char)(var5 << 12 ^ var7 << 6 ^ var8 ^ -123008);
                        if (Character.isSurrogate(var13)) {
                           return malformedForLength(var1, var3, 3);
                        }

                        var2.put(var13);
                        var3 += 3;
                     } else {
                        if (var5 >> 3 != -2) {
                           return malformed(var1, var3, 1);
                        }

                        var6 = var4 - var3;
                        if (var6 >= 4 && var2.remaining() >= 2) {
                           var7 = var1.get();
                           var8 = var1.get();
                           byte var9 = var1.get();
                           int var10 = var5 << 18 ^ var7 << 12 ^ var8 << 6 ^ var9 ^ 3678080;
                           if (!isMalformed4(var7, var8, var9) && Character.isSupplementaryCodePoint(var10)) {
                              var2.put(Character.highSurrogate(var10));
                              var2.put(Character.lowSurrogate(var10));
                              var3 += 4;
                              continue;
                           }

                           return malformed(var1, var3, 4);
                        }

                        int var11 = var5 & 255;
                        if (var11 <= 244 && (var6 <= 1 || !isMalformed4_2(var11, var1.get() & 255))) {
                           if (var6 > 2 && isMalformed4_3(var1.get())) {
                              return malformedForLength(var1, var3, 2);
                           }

                           return xflow(var1, var3, 4);
                        }

                        return malformedForLength(var1, var3, 1);
                     }
                  }
               } else {
                  if (var2.remaining() < 1) {
                     return xflow(var1, var3, 1);
                  }

                  var2.put((char)var5);
                  ++var3;
               }
            }

            return xflow(var1, var3, 0);
         }
      }

      protected CoderResult decodeLoop(ByteBuffer var1, CharBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.decodeArrayLoop(var1, var2) : this.decodeBufferLoop(var1, var2);
      }

      private static ByteBuffer getByteBuffer(ByteBuffer var0, byte[] var1, int var2) {
         if (var0 == null) {
            var0 = ByteBuffer.wrap(var1);
         }

         var0.position(var2);
         return var0;
      }

      public int decode(byte[] var1, int var2, int var3, char[] var4) {
         int var5 = var2 + var3;
         int var6 = 0;
         int var7 = Math.min(var3, var4.length);

         ByteBuffer var8;
         for(var8 = null; var6 < var7 && var1[var2] >= 0; var4[var6++] = (char)var1[var2++]) {
         }

         while(true) {
            while(true) {
               while(var2 < var5) {
                  byte var9 = var1[var2++];
                  if (var9 < 0) {
                     byte var10;
                     if (var9 >> 5 != -2 || (var9 & 30) == 0) {
                        byte var11;
                        if (var9 >> 4 == -2) {
                           if (var2 + 1 < var5) {
                              var10 = var1[var2++];
                              var11 = var1[var2++];
                              if (isMalformed3(var9, var10, var11)) {
                                 if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                    return -1;
                                 }

                                 var4[var6++] = this.replacement().charAt(0);
                                 var2 -= 3;
                                 var8 = getByteBuffer(var8, var1, var2);
                                 var2 += malformedN(var8, 3).length();
                              } else {
                                 char var15 = (char)(var9 << 12 ^ var10 << 6 ^ var11 ^ -123008);
                                 if (Character.isSurrogate(var15)) {
                                    if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                       return -1;
                                    }

                                    var4[var6++] = this.replacement().charAt(0);
                                 } else {
                                    var4[var6++] = var15;
                                 }
                              }
                           } else {
                              if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                 return -1;
                              }

                              if (var2 >= var5 || !isMalformed3_2(var9, var1[var2])) {
                                 var4[var6++] = this.replacement().charAt(0);
                                 return var6;
                              }

                              var4[var6++] = this.replacement().charAt(0);
                           }
                        } else if (var9 >> 3 != -2) {
                           if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                              return -1;
                           }

                           var4[var6++] = this.replacement().charAt(0);
                        } else if (var2 + 2 < var5) {
                           var10 = var1[var2++];
                           var11 = var1[var2++];
                           byte var12 = var1[var2++];
                           int var13 = var9 << 18 ^ var10 << 12 ^ var11 << 6 ^ var12 ^ 3678080;
                           if (!isMalformed4(var10, var11, var12) && Character.isSupplementaryCodePoint(var13)) {
                              var4[var6++] = Character.highSurrogate(var13);
                              var4[var6++] = Character.lowSurrogate(var13);
                           } else {
                              if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                                 return -1;
                              }

                              var4[var6++] = this.replacement().charAt(0);
                              var2 -= 4;
                              var8 = getByteBuffer(var8, var1, var2);
                              var2 += malformedN(var8, 4).length();
                           }
                        } else {
                           if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                              return -1;
                           }

                           int var14 = var9 & 255;
                           if (var14 <= 244 && (var2 >= var5 || !isMalformed4_2(var14, var1[var2] & 255))) {
                              ++var2;
                              if (var2 >= var5 || !isMalformed4_3(var1[var2])) {
                                 var4[var6++] = this.replacement().charAt(0);
                                 return var6;
                              }

                              var4[var6++] = this.replacement().charAt(0);
                           } else {
                              var4[var6++] = this.replacement().charAt(0);
                           }
                        }
                     } else {
                        if (var2 >= var5) {
                           if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                              return -1;
                           }

                           var4[var6++] = this.replacement().charAt(0);
                           return var6;
                        }

                        var10 = var1[var2++];
                        if (isNotContinuation(var10)) {
                           if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                              return -1;
                           }

                           var4[var6++] = this.replacement().charAt(0);
                           --var2;
                        } else {
                           var4[var6++] = (char)(var9 << 6 ^ var10 ^ 3968);
                        }
                     }
                  } else {
                     var4[var6++] = (char)var9;
                  }
               }

               return var6;
            }
         }
      }
   }

   private static final class Encoder extends CharsetEncoder implements ArrayEncoder {
      private com.frojasg1.sun.nio.cs.Surrogate.Parser sgp;
      private byte repl;

      private Encoder(Charset var1) {
         super(var1, 1.1F, 3.0F);
         this.repl = 63;
      }

      public boolean canEncode(char var1) {
         return !Character.isSurrogate(var1);
      }

      public boolean isLegalReplacement(byte[] var1) {
         return var1.length == 1 && var1[0] >= 0 || super.isLegalReplacement(var1);
      }

      private static CoderResult overflow(CharBuffer var0, int var1, ByteBuffer var2, int var3) {
         UTF_8.updatePositions(var0, var1, var2, var3);
         return CoderResult.OVERFLOW;
      }

      private static CoderResult overflow(CharBuffer var0, int var1) {
         var0.position(var1);
         return CoderResult.OVERFLOW;
      }

      private CoderResult encodeArrayLoop(CharBuffer var1, ByteBuffer var2) {
         char[] var3 = var1.array();
         int var4 = var1.arrayOffset() + var1.position();
         int var5 = var1.arrayOffset() + var1.limit();
         byte[] var6 = var2.array();
         int var7 = var2.arrayOffset() + var2.position();
         int var8 = var2.arrayOffset() + var2.limit();

         for(int var9 = var7 + Math.min(var5 - var4, var8 - var7); var7 < var9 && var3[var4] < 128; var6[var7++] = (byte)var3[var4++]) {
         }

         for(; var4 < var5; ++var4) {
            char var10 = var3[var4];
            if (var10 < 128) {
               if (var7 >= var8) {
                  return overflow(var1, var4, var2, var7);
               }

               var6[var7++] = (byte)var10;
            } else if (var10 < 2048) {
               if (var8 - var7 < 2) {
                  return overflow(var1, var4, var2, var7);
               }

               var6[var7++] = (byte)(192 | var10 >> 6);
               var6[var7++] = (byte)(128 | var10 & 63);
            } else if (Character.isSurrogate(var10)) {
               if (this.sgp == null) {
                  this.sgp = new com.frojasg1.sun.nio.cs.Surrogate.Parser();
               }

               int var11 = this.sgp.parse(var10, var3, var4, var5);
               if (var11 < 0) {
                  UTF_8.updatePositions(var1, var4, var2, var7);
                  return this.sgp.error();
               }

               if (var8 - var7 < 4) {
                  return overflow(var1, var4, var2, var7);
               }

               var6[var7++] = (byte)(240 | var11 >> 18);
               var6[var7++] = (byte)(128 | var11 >> 12 & 63);
               var6[var7++] = (byte)(128 | var11 >> 6 & 63);
               var6[var7++] = (byte)(128 | var11 & 63);
               ++var4;
            } else {
               if (var8 - var7 < 3) {
                  return overflow(var1, var4, var2, var7);
               }

               var6[var7++] = (byte)(224 | var10 >> 12);
               var6[var7++] = (byte)(128 | var10 >> 6 & 63);
               var6[var7++] = (byte)(128 | var10 & 63);
            }
         }

         UTF_8.updatePositions(var1, var4, var2, var7);
         return CoderResult.UNDERFLOW;
      }

      private CoderResult encodeBufferLoop(CharBuffer var1, ByteBuffer var2) {
         int var3;
         for(var3 = var1.position(); var1.hasRemaining(); ++var3) {
            char var4 = var1.get();
            if (var4 < 128) {
               if (!var2.hasRemaining()) {
                  return overflow(var1, var3);
               }

               var2.put((byte)var4);
            } else if (var4 < 2048) {
               if (var2.remaining() < 2) {
                  return overflow(var1, var3);
               }

               var2.put((byte)(192 | var4 >> 6));
               var2.put((byte)(128 | var4 & 63));
            } else if (Character.isSurrogate(var4)) {
               if (this.sgp == null) {
                  this.sgp = new com.frojasg1.sun.nio.cs.Surrogate.Parser();
               }

               int var5 = this.sgp.parse(var4, var1);
               if (var5 < 0) {
                  var1.position(var3);
                  return this.sgp.error();
               }

               if (var2.remaining() < 4) {
                  return overflow(var1, var3);
               }

               var2.put((byte)(240 | var5 >> 18));
               var2.put((byte)(128 | var5 >> 12 & 63));
               var2.put((byte)(128 | var5 >> 6 & 63));
               var2.put((byte)(128 | var5 & 63));
               ++var3;
            } else {
               if (var2.remaining() < 3) {
                  return overflow(var1, var3);
               }

               var2.put((byte)(224 | var4 >> 12));
               var2.put((byte)(128 | var4 >> 6 & 63));
               var2.put((byte)(128 | var4 & 63));
            }
         }

         var1.position(var3);
         return CoderResult.UNDERFLOW;
      }

      protected final CoderResult encodeLoop(CharBuffer var1, ByteBuffer var2) {
         return var1.hasArray() && var2.hasArray() ? this.encodeArrayLoop(var1, var2) : this.encodeBufferLoop(var1, var2);
      }

      protected void implReplaceWith(byte[] var1) {
         this.repl = var1[0];
      }

      public int encode(char[] var1, int var2, int var3, byte[] var4) {
         int var5 = var2 + var3;
         int var6 = 0;

         for(int var7 = var6 + Math.min(var3, var4.length); var6 < var7 && var1[var2] < 128; var4[var6++] = (byte)var1[var2++]) {
         }

         while(var2 < var5) {
            char var8 = var1[var2++];
            if (var8 < 128) {
               var4[var6++] = (byte)var8;
            } else if (var8 < 2048) {
               var4[var6++] = (byte)(192 | var8 >> 6);
               var4[var6++] = (byte)(128 | var8 & 63);
            } else if (Character.isSurrogate(var8)) {
               if (this.sgp == null) {
                  this.sgp = new Surrogate.Parser();
               }

               int var9 = this.sgp.parse(var8, var1, var2 - 1, var5);
               if (var9 < 0) {
                  if (this.malformedInputAction() != CodingErrorAction.REPLACE) {
                     return -1;
                  }

                  var4[var6++] = this.repl;
               } else {
                  var4[var6++] = (byte)(240 | var9 >> 18);
                  var4[var6++] = (byte)(128 | var9 >> 12 & 63);
                  var4[var6++] = (byte)(128 | var9 >> 6 & 63);
                  var4[var6++] = (byte)(128 | var9 & 63);
                  ++var2;
               }
            } else {
               var4[var6++] = (byte)(224 | var8 >> 12);
               var4[var6++] = (byte)(128 | var8 >> 6 & 63);
               var4[var6++] = (byte)(128 | var8 & 63);
            }
         }

         return var6;
      }
   }
}
