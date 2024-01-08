package com.frojasg1.sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.loops.TransformBlit;
import com.frojasg1.sun.java2d.opengl.OGLBlitLoops;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.Region;

class OGLSwToSurfaceTransform extends TransformBlit {
   private int typeval;

   OGLSwToSurfaceTransform(SurfaceType var1, int var2) {
      super(var1, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
      this.typeval = var2;
   }

   public void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      com.frojasg1.sun.java2d.opengl.OGLBlitLoops.Blit(var1, var2, var3, var4, var5, var6, var7, var8, var7 + var11, var8 + var12, (double)var9, (double)var10, (double)(var9 + var11), (double)(var10 + var12), this.typeval, false);
   }
}
