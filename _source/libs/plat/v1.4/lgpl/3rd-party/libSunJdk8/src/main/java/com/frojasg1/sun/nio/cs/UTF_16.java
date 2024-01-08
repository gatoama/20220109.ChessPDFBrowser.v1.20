package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.StandardCharsets;
import com.frojasg1.sun.nio.cs.Unicode;
import com.frojasg1.sun.nio.cs.UnicodeDecoder;
import com.frojasg1.sun.nio.cs.UnicodeEncoder;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

class UTF_16 extends com.frojasg1.sun.nio.cs.Unicode {
   public UTF_16() {
      super("UTF-16", StandardCharsets.aliases_UTF_16);
   }

   public String historicalName() {
      return "UTF-16";
   }

   public CharsetDecoder newDecoder() {
      return new UTF_16.Decoder(this);
   }

   public CharsetEncoder newEncoder() {
      return new UTF_16.Encoder(this);
   }

   private static class Decoder extends com.frojasg1.sun.nio.cs.UnicodeDecoder {
      public Decoder(Charset var1) {
         super(var1, 0);
      }
   }

   private static class Encoder extends UnicodeEncoder {
      public Encoder(Charset var1) {
         super(var1, 0, true);
      }
   }
}
