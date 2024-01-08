package com.frojasg1.sun.font;

import com.frojasg1.sun.font.FontStrikeDesc;
import com.frojasg1.sun.font.NativeFont;
import com.frojasg1.sun.font.PhysicalStrike;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;

public class NativeStrike extends PhysicalStrike {
   com.frojasg1.sun.font.NativeFont nativeFont;

   NativeStrike(com.frojasg1.sun.font.NativeFont var1, com.frojasg1.sun.font.FontStrikeDesc var2) {
      super(var1, var2);
      throw new RuntimeException("NativeFont not used on Windows");
   }

   NativeStrike(NativeFont var1, FontStrikeDesc var2, boolean var3) {
      super(var1, var2);
      throw new RuntimeException("NativeFont not used on Windows");
   }

   void getGlyphImagePtrs(int[] var1, long[] var2, int var3) {
   }

   long getGlyphImagePtr(int var1) {
      return 0L;
   }

   long getGlyphImagePtrNoCache(int var1) {
      return 0L;
   }

   void getGlyphImageBounds(int var1, Float var2, Rectangle var3) {
   }

   Float getGlyphMetrics(int var1) {
      return null;
   }

   float getGlyphAdvance(int var1) {
      return 0.0F;
   }

   java.awt.geom.Rectangle2D.Float getGlyphOutlineBounds(int var1) {
      return null;
   }

   GeneralPath getGlyphOutline(int var1, float var2, float var3) {
      return null;
   }

   GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3) {
      return null;
   }
}
