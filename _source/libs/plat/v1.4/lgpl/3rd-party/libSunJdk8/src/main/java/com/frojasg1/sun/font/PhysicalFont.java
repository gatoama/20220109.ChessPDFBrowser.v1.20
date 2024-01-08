package com.frojasg1.sun.font;

import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.Font2DHandle;
import com.frojasg1.sun.font.StrikeMetrics;

import java.awt.FontFormatException;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;

public abstract class PhysicalFont extends com.frojasg1.sun.font.Font2D {
   protected String platName;
   protected Object nativeNames;

   public boolean equals(Object var1) {
      return var1 != null && var1.getClass() == this.getClass() && ((Font2D)var1).fullName.equals(this.fullName);
   }

   public int hashCode() {
      return this.fullName.hashCode();
   }

   PhysicalFont(String var1, Object var2) throws FontFormatException {
      this.handle = new com.frojasg1.sun.font.Font2DHandle(this);
      this.platName = var1;
      this.nativeNames = var2;
   }

   protected PhysicalFont() {
      this.handle = new Font2DHandle(this);
   }

   Float getGlyphPoint(long var1, int var3, int var4) {
      return new Float();
   }

   abstract StrikeMetrics getFontMetrics(long var1);

   abstract float getGlyphAdvance(long var1, int var3);

   abstract void getGlyphMetrics(long var1, int var3, Float var4);

   abstract long getGlyphImage(long var1, int var3);

   abstract java.awt.geom.Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3);

   abstract GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5);

   abstract GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6);
}
