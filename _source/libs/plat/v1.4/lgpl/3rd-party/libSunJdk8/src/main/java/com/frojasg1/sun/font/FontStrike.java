package com.frojasg1.sun.font;

import com.frojasg1.sun.font.FontStrikeDesc;
import com.frojasg1.sun.font.FontStrikeDisposer;
import com.frojasg1.sun.font.StrikeMetrics;

import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;

public abstract class FontStrike {
   protected com.frojasg1.sun.font.FontStrikeDisposer disposer;
   protected FontStrikeDesc desc;
   protected com.frojasg1.sun.font.StrikeMetrics strikeMetrics;
   protected boolean algoStyle = false;
   protected float boldness = 1.0F;
   protected float italic = 0.0F;

   public FontStrike() {
   }

   public abstract int getNumGlyphs();

   abstract StrikeMetrics getFontMetrics();

   abstract void getGlyphImagePtrs(int[] var1, long[] var2, int var3);

   abstract long getGlyphImagePtr(int var1);

   abstract void getGlyphImageBounds(int var1, Float var2, Rectangle var3);

   abstract Float getGlyphMetrics(int var1);

   abstract Float getCharMetrics(char var1);

   abstract float getGlyphAdvance(int var1);

   abstract float getCodePointAdvance(int var1);

   abstract java.awt.geom.Rectangle2D.Float getGlyphOutlineBounds(int var1);

   abstract GeneralPath getGlyphOutline(int var1, float var2, float var3);

   abstract GeneralPath getGlyphVectorOutline(int[] var1, float var2, float var3);
}
