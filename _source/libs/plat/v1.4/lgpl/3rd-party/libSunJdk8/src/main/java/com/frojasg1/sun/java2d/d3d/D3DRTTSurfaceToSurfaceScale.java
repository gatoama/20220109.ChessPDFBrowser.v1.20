package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DBlitLoops;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.ScaledBlit;
import com.frojasg1.sun.java2d.pipe.Region;

class D3DRTTSurfaceToSurfaceScale extends ScaledBlit {
   D3DRTTSurfaceToSurfaceScale() {
      super(com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DSurfaceRTT, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
   }

   public void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
      com.frojasg1.sun.java2d.d3d.D3DBlitLoops.IsoBlit(var1, var2, (BufferedImage)null, (BufferedImageOp)null, var3, var4, (AffineTransform)null, 1, var5, var6, var7, var8, var9, var11, var13, var15, true);
   }
}
