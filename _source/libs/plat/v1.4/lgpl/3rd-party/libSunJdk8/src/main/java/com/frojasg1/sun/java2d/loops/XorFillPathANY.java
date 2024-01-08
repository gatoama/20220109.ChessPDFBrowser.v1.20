package com.frojasg1.sun.java2d.loops;

import java.awt.geom.Path2D.Float;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.FillPath;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.PixelWriterDrawHandler;
import com.frojasg1.sun.java2d.loops.ProcessPath;
import com.frojasg1.sun.java2d.loops.SurfaceType;

class XorFillPathANY extends FillPath {
   XorFillPathANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void FillPath(SunGraphics2D var1, SurfaceData var2, int var3, int var4, Float var5) {
      com.frojasg1.sun.java2d.loops.PixelWriter var6 = GeneralRenderer.createXorPixelWriter(var1, var2);
      ProcessPath.fillPath(new com.frojasg1.sun.java2d.loops.PixelWriterDrawHandler(var2, var6, var1.getCompClip(), var1.strokeHint), var5, var3, var4);
   }
}
