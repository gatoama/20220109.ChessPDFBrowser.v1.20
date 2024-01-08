package com.frojasg1.sun.font;

import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.FontManagerNativeLibrary;
import com.frojasg1.sun.font.FontStrike;
import com.frojasg1.sun.font.FontStrikeDesc;
import com.frojasg1.sun.font.GlyphLayout;
import com.frojasg1.sun.font.TextRecord;

import java.awt.geom.Point2D.Float;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;

public final class SunLayoutEngine implements com.frojasg1.sun.font.GlyphLayout.LayoutEngine, com.frojasg1.sun.font.GlyphLayout.LayoutEngineFactory {
   private com.frojasg1.sun.font.GlyphLayout.LayoutEngineKey key;
   private static com.frojasg1.sun.font.GlyphLayout.LayoutEngineFactory instance;
   private SoftReference cacheref = new SoftReference((Object)null);

   private static native void initGVIDs();

   public static com.frojasg1.sun.font.GlyphLayout.LayoutEngineFactory instance() {
      if (instance == null) {
         instance = new SunLayoutEngine();
      }

      return instance;
   }

   private SunLayoutEngine() {
   }

   public com.frojasg1.sun.font.GlyphLayout.LayoutEngine getEngine(com.frojasg1.sun.font.Font2D var1, int var2, int var3) {
      return this.getEngine(new com.frojasg1.sun.font.GlyphLayout.LayoutEngineKey(var1, var2, var3));
   }

   public com.frojasg1.sun.font.GlyphLayout.LayoutEngine getEngine(com.frojasg1.sun.font.GlyphLayout.LayoutEngineKey var1) {
      ConcurrentHashMap var2 = (ConcurrentHashMap)this.cacheref.get();
      if (var2 == null) {
         var2 = new ConcurrentHashMap();
         this.cacheref = new SoftReference(var2);
      }

      Object var3 = (com.frojasg1.sun.font.GlyphLayout.LayoutEngine)var2.get(var1);
      if (var3 == null) {
         com.frojasg1.sun.font.GlyphLayout.LayoutEngineKey var4 = var1.copy();
         var3 = new SunLayoutEngine(var4);
         var2.put(var4, var3);
      }

      return (com.frojasg1.sun.font.GlyphLayout.LayoutEngine)var3;
   }

   private SunLayoutEngine(com.frojasg1.sun.font.GlyphLayout.LayoutEngineKey var1) {
      this.key = var1;
   }

   public void layout(FontStrikeDesc var1, float[] var2, int var3, int var4, TextRecord var5, int var6, Float var7, com.frojasg1.sun.font.GlyphLayout.GVData var8) {
      com.frojasg1.sun.font.Font2D var9 = this.key.font();
      com.frojasg1.sun.font.FontStrike var10 = var9.getStrike(var1);
      long var11 = var9.getLayoutTableCache();
      nativeLayout(var9, var10, var2, var3, var4, var5.text, var5.start, var5.limit, var5.min, var5.max, this.key.script(), this.key.lang(), var6, var7, var8, var9.getUnitsPerEm(), var11);
   }

   private static native void nativeLayout(Font2D var0, FontStrike var1, float[] var2, int var3, int var4, char[] var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, Float var13, GlyphLayout.GVData var14, long var15, long var17);

   static {
      FontManagerNativeLibrary.load();
      initGVIDs();
   }
}
