package com.frojasg1.sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.ScaledBlit;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.opengl.OGLBlitLoops;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.Region;

class OGLSwToSurfaceScale extends ScaledBlit {
   private int typeval;

   OGLSwToSurfaceScale(SurfaceType var1, int var2) {
      super(var1, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
      this.typeval = var2;
   }

   public void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
      com.frojasg1.sun.java2d.opengl.OGLBlitLoops.Blit(var1, var2, var3, var4, (AffineTransform)null, 1, var5, var6, var7, var8, var9, var11, var13, var15, this.typeval, false);
   }
}
