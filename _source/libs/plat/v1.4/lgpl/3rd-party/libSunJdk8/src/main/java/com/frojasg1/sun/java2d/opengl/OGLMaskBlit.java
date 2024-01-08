package com.frojasg1.sun.java2d.opengl;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.opengl.OGLContext;
import com.frojasg1.sun.java2d.opengl.OGLRenderQueue;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.BufferedMaskBlit;
import com.frojasg1.sun.java2d.pipe.Region;

class OGLMaskBlit extends BufferedMaskBlit {
   static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new OGLMaskBlit(SurfaceType.IntArgb, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntArgbPre, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntRgb, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntRgb, CompositeType.SrcNoEa), new OGLMaskBlit(SurfaceType.IntBgr, CompositeType.SrcOver), new OGLMaskBlit(SurfaceType.IntBgr, CompositeType.SrcNoEa)};
      GraphicsPrimitiveMgr.register(var0);
   }

   private OGLMaskBlit(SurfaceType var1, CompositeType var2) {
      super(OGLRenderQueue.getInstance(), var1, var2, com.frojasg1.sun.java2d.opengl.OGLSurfaceData.OpenGLSurface);
   }

   protected void validateContext(SurfaceData var1, Composite var2, Region var3) {
      com.frojasg1.sun.java2d.opengl.OGLSurfaceData var4 = (OGLSurfaceData)var1;
      OGLContext.validateContext(var4, var4, var3, var2, (AffineTransform)null, (Paint)null, (SunGraphics2D)null, 0);
   }
}
