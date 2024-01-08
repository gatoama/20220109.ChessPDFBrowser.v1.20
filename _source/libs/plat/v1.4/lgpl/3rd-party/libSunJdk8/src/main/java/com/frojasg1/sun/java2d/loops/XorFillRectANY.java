package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.FillRect;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

class XorFillRectANY extends FillRect {
   XorFillRectANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void FillRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      com.frojasg1.sun.java2d.loops.PixelWriter var7 = com.frojasg1.sun.java2d.loops.GeneralRenderer.createXorPixelWriter(var1, var2);
      Region var8 = var1.getCompClip().getBoundsIntersectionXYWH(var3, var4, var5, var6);
      GeneralRenderer.doSetRect(var2, var7, var8.getLoX(), var8.getLoY(), var8.getHiX(), var8.getHiY());
   }
}
