package com.frojasg1.sun.font;

import com.frojasg1.sun.font.CharToGlyphMapper;
import com.frojasg1.sun.font.FontStrike;
import com.frojasg1.sun.font.FontStrikeDesc;
import com.frojasg1.sun.font.PhysicalFont;
import com.frojasg1.sun.font.StrikeMetrics;

import java.awt.FontFormatException;
import java.awt.font.FontRenderContext;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Float;

public class NativeFont extends com.frojasg1.sun.font.PhysicalFont {
   public NativeFont(String var1, boolean var2) throws FontFormatException {
      throw new FontFormatException("NativeFont not used on Windows");
   }

   static boolean hasExternalBitmaps(String var0) {
      return false;
   }

   public CharToGlyphMapper getMapper() {
      return null;
   }

   PhysicalFont getDelegateFont() {
      return null;
   }

   FontStrike createStrike(FontStrikeDesc var1) {
      return null;
   }

   public Rectangle2D getMaxCharBounds(FontRenderContext var1) {
      return null;
   }

   StrikeMetrics getFontMetrics(long var1) {
      return null;
   }

   public GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) {
      return null;
   }

   public GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) {
      return null;
   }

   long getGlyphImage(long var1, int var3) {
      return 0L;
   }

   void getGlyphMetrics(long var1, int var3, Float var4) {
   }

   float getGlyphAdvance(long var1, int var3) {
      return 0.0F;
   }

   java.awt.geom.Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) {
      return new java.awt.geom.Rectangle2D.Float(0.0F, 0.0F, 0.0F, 0.0F);
   }
}
