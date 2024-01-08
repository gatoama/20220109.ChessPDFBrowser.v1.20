package com.frojasg1.sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.font.GlyphList;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.opengl.OGLContext;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.BufferedTextPipe;
import com.frojasg1.sun.java2d.pipe.RenderQueue;

class OGLTextRenderer extends BufferedTextPipe {
   OGLTextRenderer(RenderQueue var1) {
      super(var1);
   }

   protected native void drawGlyphList(int var1, boolean var2, boolean var3, boolean var4, int var5, float var6, float var7, long[] var8, float[] var9);

   protected void validateContext(SunGraphics2D var1, Composite var2) {
      com.frojasg1.sun.java2d.opengl.OGLSurfaceData var3 = (OGLSurfaceData)var1.surfaceData;
      OGLContext.validateContext(var3, var3, var1.getCompClip(), var2, (AffineTransform)null, var1.paint, var1, 0);
   }

   OGLTextRenderer traceWrap() {
      return new OGLTextRenderer.Tracer(this);
   }

   private static class Tracer extends OGLTextRenderer {
      Tracer(OGLTextRenderer var1) {
         super(var1.rq);
      }

      protected void drawGlyphList(SunGraphics2D var1, GlyphList var2) {
         GraphicsPrimitive.tracePrimitive("OGLDrawGlyphs");
         super.drawGlyphList(var1, var2);
      }
   }
}
