package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.StandardCharsets;
import com.frojasg1.sun.nio.cs.UTF_32Coder;
import com.frojasg1.sun.nio.cs.Unicode;

import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class UTF_32BE_BOM extends com.frojasg1.sun.nio.cs.Unicode {
   public UTF_32BE_BOM() {
      super("X-UTF-32BE-BOM", StandardCharsets.aliases_UTF_32BE_BOM);
   }

   public String historicalName() {
      return "X-UTF-32BE-BOM";
   }

   public CharsetDecoder newDecoder() {
      return new com.frojasg1.sun.nio.cs.UTF_32Coder.Decoder(this, 1);
   }

   public CharsetEncoder newEncoder() {
      return new com.frojasg1.sun.nio.cs.UTF_32Coder.Encoder(this, 1, true);
   }
}
