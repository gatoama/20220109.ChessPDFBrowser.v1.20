package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.DrawRect;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.SurfaceType;

class XorDrawRectANY extends DrawRect {
   XorDrawRectANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawRect(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      com.frojasg1.sun.java2d.loops.PixelWriter var7 = com.frojasg1.sun.java2d.loops.GeneralRenderer.createXorPixelWriter(var1, var2);
      GeneralRenderer.doDrawRect(var7, var1, var2, var3, var4, var5, var6);
   }
}
