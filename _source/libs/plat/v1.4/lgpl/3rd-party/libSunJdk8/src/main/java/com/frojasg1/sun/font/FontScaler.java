package com.frojasg1.sun.font;

import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D.Float;
import java.lang.ref.WeakReference;
import java.lang.reflect.Constructor;

import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.FontManager;
import com.frojasg1.sun.font.FontManagerFactory;
import com.frojasg1.sun.font.FontScalerException;
import com.frojasg1.sun.font.FontUtilities;
import com.frojasg1.sun.font.NullFontScaler;
import com.frojasg1.sun.font.StrikeMetrics;
import com.frojasg1.sun.java2d.Disposer;
import com.frojasg1.sun.java2d.DisposerRecord;

public abstract class FontScaler implements DisposerRecord {
   private static FontScaler nullScaler = null;
   private static Constructor<FontScaler> scalerConstructor = null;
   protected WeakReference<com.frojasg1.sun.font.Font2D> font = null;
   protected long nativeScaler = 0L;
   protected boolean disposed = false;

   public FontScaler() {
   }

   public static FontScaler getScaler(com.frojasg1.sun.font.Font2D var0, int var1, boolean var2, int var3) {
      FontScaler var4 = null;

      try {
         Object[] var5 = new Object[]{var0, var1, var2, var3};
         var4 = (FontScaler)scalerConstructor.newInstance(var5);
         Disposer.addObjectRecord(var0, var4);
      } catch (Throwable var7) {
         var4 = nullScaler;
         FontManager var6 = FontManagerFactory.getInstance();
         var6.deRegisterBadFont(var0);
      }

      return var4;
   }

   public static synchronized FontScaler getNullScaler() {
      if (nullScaler == null) {
         nullScaler = new com.frojasg1.sun.font.NullFontScaler();
      }

      return nullScaler;
   }

   abstract StrikeMetrics getFontMetrics(long var1) throws com.frojasg1.sun.font.FontScalerException;

   abstract float getGlyphAdvance(long var1, int var3) throws com.frojasg1.sun.font.FontScalerException;

   abstract void getGlyphMetrics(long var1, int var3, Float var4) throws com.frojasg1.sun.font.FontScalerException;

   abstract long getGlyphImage(long var1, int var3) throws com.frojasg1.sun.font.FontScalerException;

   abstract java.awt.geom.Rectangle2D.Float getGlyphOutlineBounds(long var1, int var3) throws com.frojasg1.sun.font.FontScalerException;

   abstract GeneralPath getGlyphOutline(long var1, int var3, float var4, float var5) throws com.frojasg1.sun.font.FontScalerException;

   abstract GeneralPath getGlyphVectorOutline(long var1, int[] var3, int var4, float var5, float var6) throws com.frojasg1.sun.font.FontScalerException;

   public void dispose() {
   }

   abstract int getNumGlyphs() throws com.frojasg1.sun.font.FontScalerException;

   abstract int getMissingGlyphCode() throws com.frojasg1.sun.font.FontScalerException;

   abstract int getGlyphCode(char var1) throws com.frojasg1.sun.font.FontScalerException;

   abstract long getLayoutTableCache() throws com.frojasg1.sun.font.FontScalerException;

   abstract Float getGlyphPoint(long var1, int var3, int var4) throws FontScalerException;

   abstract long getUnitsPerEm();

   abstract long createScalerContext(double[] var1, int var2, int var3, float var4, float var5, boolean var6);

   abstract void invalidateScalerContext(long var1);

   static {
      Class var0 = null;
      Class[] var1 = new Class[]{Font2D.class, Integer.TYPE, Boolean.TYPE, Integer.TYPE};

      try {
         if (FontUtilities.isOpenJDK) {
            var0 = Class.forName("sun.font.FreetypeFontScaler");
         } else {
            var0 = Class.forName("sun.font.T2KFontScaler");
         }
      } catch (ClassNotFoundException var4) {
         var0 = com.frojasg1.sun.font.NullFontScaler.class;
      }

      try {
         scalerConstructor = var0.getConstructor(var1);
      } catch (NoSuchMethodException var3) {
      }

   }
}
