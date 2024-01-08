package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.FillSpans;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.SpanIterator;

class SetFillSpansANY extends FillSpans {
   SetFillSpansANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.SrcNoEa, SurfaceType.Any);
   }

   public void FillSpans(SunGraphics2D var1, SurfaceData var2, SpanIterator var3) {
      com.frojasg1.sun.java2d.loops.PixelWriter var4 = com.frojasg1.sun.java2d.loops.GeneralRenderer.createSolidPixelWriter(var1, var2);
      int[] var5 = new int[4];

      while(var3.nextSpan(var5)) {
         GeneralRenderer.doSetRect(var2, var4, var5[0], var5[1], var5[2], var5[3]);
      }

   }
}
