package com.frojasg1.sun.java2d.opengl;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.opengl.OGLBlitLoops;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.Region;

class OGLSurfaceToSurfaceBlit extends Blit {
   OGLSurfaceToSurfaceBlit() {
      super(com.frojasg1.sun.java2d.opengl.OGLSurfaceData.OpenGLSurface, CompositeType.AnyAlpha, OGLSurfaceData.OpenGLSurface);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      com.frojasg1.sun.java2d.opengl.OGLBlitLoops.IsoBlit(var1, var2, (BufferedImage)null, (BufferedImageOp)null, var3, var4, (AffineTransform)null, 1, var5, var6, var5 + var9, var6 + var10, (double)var7, (double)var8, (double)(var7 + var9), (double)(var8 + var10), false);
   }
}
