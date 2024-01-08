package com.frojasg1.sun.java2d.pipe;

import com.frojasg1.sun.font.GlyphList;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.pipe.GlyphListLoopPipe;

public class LCDTextRenderer extends GlyphListLoopPipe {
   public LCDTextRenderer() {
   }

   protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
      var1.loops.drawGlyphListLCDLoop.DrawGlyphListLCD(var1, var1.surfaceData, var2);
   }
}
