package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.font.GlyphList;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.DrawGlyphListAA;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.SurfaceType;

class XorDrawGlyphListAAANY extends DrawGlyphListAA {
   XorDrawGlyphListAAANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawGlyphListAA(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
      com.frojasg1.sun.java2d.loops.PixelWriter var4 = com.frojasg1.sun.java2d.loops.GeneralRenderer.createXorPixelWriter(var1, var2);
      GeneralRenderer.doDrawGlyphList(var2, var4, var3, var1.getCompClip());
   }
}
