package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.StandardCharsets;
import com.frojasg1.sun.nio.cs.Unicode;
import com.frojasg1.sun.nio.cs.UnicodeDecoder;
import com.frojasg1.sun.nio.cs.UnicodeEncoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16LE extends com.frojasg1.sun.nio.cs.Unicode {
   public UTF_16LE() {
      super("UTF-16LE", StandardCharsets.aliases_UTF_16LE);
   }

   public String historicalName() {
      return "UnicodeLittleUnmarked";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_16LE.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_16LE.Encoder(this);
   }

   private static class Decoder extends com.frojasg1.sun.nio.cs.UnicodeDecoder {
      public Decoder(Charset var1) {
         super(var1, 2);
      }
   }

   private static class Encoder extends UnicodeEncoder {
      public Encoder(Charset var1) {
         super(var1, 1, false);
      }
   }
}
