package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.StandardCharsets;
import com.frojasg1.sun.nio.cs.UTF_32Coder;
import com.frojasg1.sun.nio.cs.Unicode;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class UTF_32 extends com.frojasg1.sun.nio.cs.Unicode {
   public UTF_32() {
      super("UTF-32", StandardCharsets.aliases_UTF_32);
   }

   public String historicalName() {
      return "UTF-32";
   }

   public CharsetDecoder newDecoder() {
      return new com.frojasg1.sun.nio.cs.UTF_32Coder.Decoder(this, 0);
   }

   public CharsetEncoder newEncoder() {
      return new com.frojasg1.sun.nio.cs.UTF_32Coder.Encoder(this, 1, false);
   }
}
