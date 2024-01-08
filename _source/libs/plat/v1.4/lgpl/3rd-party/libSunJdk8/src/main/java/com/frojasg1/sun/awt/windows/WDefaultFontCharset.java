package com.frojasg1.sun.awt.windows;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import com.frojasg1.sun.awt.AWTCharset;

final class WDefaultFontCharset extends AWTCharset {
   private String fontName;

   WDefaultFontCharset(String var1) {
      super("WDefaultFontCharset", Charset.forName("windows-1252"));
      this.fontName = var1;
   }

   public CharsetEncoder newEncoder() {
      return new WDefaultFontCharset.Encoder();
   }

   private synchronized native boolean canConvert(char var1);

   private static native void initIDs();

   static {
      initIDs();
   }

   private class Encoder extends AWTCharset.Encoder {
      private Encoder() {
         super();
      }

      public boolean canEncode(char var1) {
         return WDefaultFontCharset.this.canConvert(var1);
      }
   }
}
