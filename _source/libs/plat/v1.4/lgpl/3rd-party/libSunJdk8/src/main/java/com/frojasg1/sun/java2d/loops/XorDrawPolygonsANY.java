package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.DrawPolygons;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

class XorDrawPolygonsANY extends DrawPolygons {
   XorDrawPolygonsANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawPolygons(SunGraphics2D var1, SurfaceData var2, int[] var3, int[] var4, int[] var5, int var6, int var7, int var8, boolean var9) {
      com.frojasg1.sun.java2d.loops.PixelWriter var10 = com.frojasg1.sun.java2d.loops.GeneralRenderer.createXorPixelWriter(var1, var2);
      int var11 = 0;
      Region var12 = var1.getCompClip();

      for(int var13 = 0; var13 < var6; ++var13) {
         int var14 = var5[var13];
         GeneralRenderer.doDrawPoly(var2, var10, var3, var4, var11, var14, var12, var7, var8, var9);
         var11 += var14;
      }

   }
}
