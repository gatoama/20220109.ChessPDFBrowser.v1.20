package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DVolatileSurfaceManager;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

class D3DSurfaceToGDIWindowSurfaceBlit extends Blit {
   D3DSurfaceToGDIWindowSurfaceBlit() {
      super(D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, GDIWindowSurfaceData.AnyGdi);
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      D3DVolatileSurfaceManager.handleVItoScreenOp(var1, var2);
   }
}
