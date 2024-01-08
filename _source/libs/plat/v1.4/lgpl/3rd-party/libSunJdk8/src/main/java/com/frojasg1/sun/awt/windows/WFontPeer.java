package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.PlatformFont;
import com.frojasg1.sun.awt.windows.WFontConfiguration;

final class WFontPeer extends PlatformFont {
   private String textComponentFontName;

   public WFontPeer(String var1, int var2) {
      super(var1, var2);
      if (this.fontConfig != null) {
         this.textComponentFontName = ((WFontConfiguration)this.fontConfig).getTextComponentFontName(this.familyName, var2);
      }

   }

   protected char getMissingGlyphCharacter() {
      return '❑';
   }

   private static native void initIDs();

   static {
      initIDs();
   }
}
