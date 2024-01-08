package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.StandardCharsets;
import com.frojasg1.sun.nio.cs.UTF_32Coder;
import com.frojasg1.sun.nio.cs.Unicode;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class UTF_32BE extends com.frojasg1.sun.nio.cs.Unicode {
   public UTF_32BE() {
      super("UTF-32BE", StandardCharsets.aliases_UTF_32BE);
   }

   public String historicalName() {
      return "UTF-32BE";
   }

   public CharsetDecoder newDecoder() {
      return new com.frojasg1.sun.nio.cs.UTF_32Coder.Decoder(this, 1);
   }

   public CharsetEncoder newEncoder() {
      return new com.frojasg1.sun.nio.cs.UTF_32Coder.Encoder(this, 1, false);
   }
}
