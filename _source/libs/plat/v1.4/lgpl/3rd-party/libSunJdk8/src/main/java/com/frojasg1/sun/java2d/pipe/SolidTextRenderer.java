package com.frojasg1.sun.java2d.pipe;

import com.frojasg1.sun.font.GlyphList;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.pipe.GlyphListLoopPipe;
import com.frojasg1.sun.java2d.pipe.LoopBasedPipe;

public class SolidTextRenderer extends GlyphListLoopPipe implements LoopBasedPipe {
   public SolidTextRenderer() {
   }

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      var1.loops.drawGlyphListLoop.DrawGlyphList(var1, var1.surfaceData, var2);
   }
}