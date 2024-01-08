package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DVolatileSurfaceManager;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.ScaledBlit;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

class D3DSurfaceToGDIWindowSurfaceScale extends ScaledBlit {
   D3DSurfaceToGDIWindowSurfaceScale() {
      super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, GDIWindowSurfaceData.AnyGdi);
   }

   public void Scale(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, double var9, double var11, double var13, double var15) {
      D3DVolatileSurfaceManager.handleVItoScreenOp(var1, var2);
   }
}
