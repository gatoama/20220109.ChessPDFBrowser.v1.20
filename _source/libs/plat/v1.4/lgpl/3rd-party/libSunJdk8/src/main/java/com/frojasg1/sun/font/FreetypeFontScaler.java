package com.frojasg1.sun.font;

import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.FontManagerNativeLibrary;
import com.frojasg1.sun.font.FontScaler;
import com.frojasg1.sun.font.FontScalerException;
import com.frojasg1.sun.font.NullFontScaler;
import com.frojasg1.sun.font.StrikeMetrics;
import com.frojasg1.sun.font.Type1Font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.lang.ref.WeakReference;

class FreetypeFontScaler extends com.frojasg1.sun.font.FontScaler {
   private static final int TRUETYPE_FONT = 1;
   private static final int TYPE1_FONT = 2;

   private static native void initIDs(Class var0);

   private void invalidateScaler() throws com.frojasg1.sun.font.FontScalerException {
      this.nativeScaler = 0L;
      this.font = null;
      throw new com.frojasg1.sun.font.FontScalerException();
   }

   public FreetypeFontScaler(com.frojasg1.sun.font.Font2D var1, int var2, boolean var3, int var4) {
      byte var5 = 1;
      if (var1 instanceof Type1Font) {
         var5 = 2;
      }

      this.nativeScaler = this.initNativeScaler(var1, var5, var2, var3, var4);
      this.font = new WeakReference(var1);
   }

   synchronized com.frojasg1.sun.font.StrikeMetrics getFontMetrics(long var1) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getFontMetricsNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler) : com.frojasg1.sun.font.FontScaler.getNullScaler().getFontMetrics(0L);
   }

   synchronized float getGlyphAdvance(long var1, int var3) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphAdvanceNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3) : com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphAdvance(0L, var3);
   }

   synchronized void getGlyphMetrics(long var1, int var3, Float var4) throws com.frojasg1.sun.font.FontScalerException {
      if (this.nativeScaler != 0L) {
         this.getGlyphMetricsNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3, var4);
      } else {
         com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphMetrics(0L, var3, var4);
      }
   }

   synchronized long getGlyphImage(long var1, int var3) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphImageNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3) : com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphImage(0L, var3);
   }

   synchronized java.awt.geom.Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphOutlineBoundsNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3) : com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphOutlineBounds(0L, var3);
   }

   synchronized GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphOutlineNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3, var4, var5) : com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphOutline(0L, var3, var4, var5);
   }

   synchronized GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphVectorOutlineNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3, var4, var5, var6) : com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphVectorOutline(0L, var3, var4, var5, var6);
   }

   synchronized long getLayoutTableCache() throws com.frojasg1.sun.font.FontScalerException {
      return this.getLayoutTableCacheNative(this.nativeScaler);
   }

   public synchronized void dispose() {
      if (this.nativeScaler != 0L) {
         this.disposeNativeScaler((com.frojasg1.sun.font.Font2D)this.font.get(), this.nativeScaler);
         this.nativeScaler = 0L;
      }

   }

   synchronized int getNumGlyphs() throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getNumGlyphsNative(this.nativeScaler) : com.frojasg1.sun.font.FontScaler.getNullScaler().getNumGlyphs();
   }

   synchronized int getMissingGlyphCode() throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getMissingGlyphCodeNative(this.nativeScaler) : com.frojasg1.sun.font.FontScaler.getNullScaler().getMissingGlyphCode();
   }

   synchronized int getGlyphCode(char var1) throws com.frojasg1.sun.font.FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphCodeNative((com.frojasg1.sun.font.Font2D)this.font.get(), this.nativeScaler, var1) : com.frojasg1.sun.font.FontScaler.getNullScaler().getGlyphCode(var1);
   }

   synchronized Float getGlyphPoint(long var1, int var3, int var4) throws FontScalerException {
      return this.nativeScaler != 0L ? this.getGlyphPointNative((com.frojasg1.sun.font.Font2D)this.font.get(), var1, this.nativeScaler, var3, var4) : FontScaler.getNullScaler().getGlyphPoint(var1, var3, var4);
   }

   synchronized long getUnitsPerEm() {
      return this.getUnitsPerEMNative(this.nativeScaler);
   }

   long createScalerContext(double[] var1, int var2, int var3, float var4, float var5, boolean var6) {
      return this.nativeScaler != 0L ? this.createScalerContextNative(this.nativeScaler, var1, var2, var3, var4, var5) : com.frojasg1.sun.font.NullFontScaler.getNullScalerContext();
   }

   private native long initNativeScaler(com.frojasg1.sun.font.Font2D var1, int var2, int var3, boolean var4, int var5);

   private native StrikeMetrics getFontMetricsNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4);

   private native float getGlyphAdvanceNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int var6);

   private native void getGlyphMetricsNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int var6, Float var7);

   private native long getGlyphImageNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int var6);

   private native java.awt.geom.Rectangle2D.Float getGlyphOutlineBoundsNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int var6);

   private native GeneralPath getGlyphOutlineNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int var6, float var7, float var8);

   private native GeneralPath getGlyphVectorOutlineNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int[] var6, int var7, float var8, float var9);

   native Float getGlyphPointNative(com.frojasg1.sun.font.Font2D var1, long var2, long var4, int var6, int var7);

   private native long getLayoutTableCacheNative(long var1);

   private native void disposeNativeScaler(com.frojasg1.sun.font.Font2D var1, long var2);

   private native int getGlyphCodeNative(Font2D var1, long var2, char var4);

   private native int getNumGlyphsNative(long var1);

   private native int getMissingGlyphCodeNative(long var1);

   private native long getUnitsPerEMNative(long var1);

   native long createScalerContextNative(long var1, double[] var3, int var4, int var5, float var6, float var7);

   void invalidateScalerContext(long var1) {
   }

   static {
      FontManagerNativeLibrary.load();
      initIDs(FreetypeFontScaler.class);
   }
}
