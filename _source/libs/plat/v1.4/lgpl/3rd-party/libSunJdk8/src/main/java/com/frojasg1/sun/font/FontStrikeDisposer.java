package com.frojasg1.sun.font;

import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.FontStrikeDesc;
import com.frojasg1.sun.font.StrikeCache;
import com.frojasg1.sun.java2d.Disposer;
import com.frojasg1.sun.java2d.DisposerRecord;

class FontStrikeDisposer implements DisposerRecord, Disposer.PollDisposable {
   com.frojasg1.sun.font.Font2D font2D;
   com.frojasg1.sun.font.FontStrikeDesc desc;
   long[] longGlyphImages;
   int[] intGlyphImages;
   int[][] segIntGlyphImages;
   long[][] segLongGlyphImages;
   long pScalerContext = 0L;
   boolean disposed = false;
   boolean comp = false;

   public FontStrikeDisposer(com.frojasg1.sun.font.Font2D var1, com.frojasg1.sun.font.FontStrikeDesc var2, long var3, int[] var5) {
      this.font2D = var1;
      this.desc = var2;
      this.pScalerContext = var3;
      this.intGlyphImages = var5;
   }

   public FontStrikeDisposer(com.frojasg1.sun.font.Font2D var1, com.frojasg1.sun.font.FontStrikeDesc var2, long var3, long[] var5) {
      this.font2D = var1;
      this.desc = var2;
      this.pScalerContext = var3;
      this.longGlyphImages = var5;
   }

   public FontStrikeDisposer(com.frojasg1.sun.font.Font2D var1, com.frojasg1.sun.font.FontStrikeDesc var2, long var3) {
      this.font2D = var1;
      this.desc = var2;
      this.pScalerContext = var3;
   }

   public FontStrikeDisposer(Font2D var1, FontStrikeDesc var2) {
      this.font2D = var1;
      this.desc = var2;
      this.comp = true;
   }

   public synchronized void dispose() {
      if (!this.disposed) {
         this.font2D.removeFromCache(this.desc);
         StrikeCache.disposeStrike(this);
         this.disposed = true;
      }

   }
}
