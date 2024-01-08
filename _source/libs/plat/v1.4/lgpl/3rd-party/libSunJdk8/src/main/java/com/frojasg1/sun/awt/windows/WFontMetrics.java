package com.frojasg1.sun.awt.windows;

import java.awt.Font;
import java.awt.FontMetrics;
import java.util.Hashtable;

final class WFontMetrics extends FontMetrics {
   int[] widths;
   int ascent;
   int descent;
   int leading;
   int height;
   int maxAscent;
   int maxDescent;
   int maxHeight;
   int maxAdvance;
   static Hashtable table;

   public WFontMetrics(Font var1) {
      super(var1);
      this.init();
   }

   public int getLeading() {
      return this.leading;
   }

   public int getAscent() {
      return this.ascent;
   }

   public int getDescent() {
      return this.descent;
   }

   public int getHeight() {
      return this.height;
   }

   public int getMaxAscent() {
      return this.maxAscent;
   }

   public int getMaxDescent() {
      return this.maxDescent;
   }

   public int getMaxAdvance() {
      return this.maxAdvance;
   }

   public native int stringWidth(String var1);

   public native int charsWidth(char[] var1, int var2, int var3);

   public native int bytesWidth(byte[] var1, int var2, int var3);

   public int[] getWidths() {
      return this.widths;
   }

   native void init();

   static FontMetrics getFontMetrics(Font var0) {
      Object var1 = (FontMetrics)table.get(var0);
      if (var1 == null) {
         table.put(var0, var1 = new WFontMetrics(var0));
      }

      return (FontMetrics)var1;
   }

   private static native void initIDs();

   static {
      initIDs();
      table = new Hashtable();
   }
}
