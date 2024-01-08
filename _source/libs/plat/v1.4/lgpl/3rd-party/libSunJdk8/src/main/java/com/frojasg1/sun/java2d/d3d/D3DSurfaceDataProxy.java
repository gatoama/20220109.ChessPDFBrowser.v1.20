package com.frojasg1.sun.java2d.d3d;

import java.awt.Color;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.SurfaceDataProxy;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;

public class D3DSurfaceDataProxy extends SurfaceDataProxy {
   com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig d3dgc;
   int transparency;

   public static SurfaceDataProxy createProxy(SurfaceData var0, com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var1) {
      return (SurfaceDataProxy)(var0 instanceof D3DSurfaceData ? UNCACHED : new D3DSurfaceDataProxy(var1, var0.getTransparency()));
   }

   public D3DSurfaceDataProxy(D3DGraphicsConfig var1, int var2) {
      this.d3dgc = var1;
      this.transparency = var2;
      this.activateDisplayListener();
   }

   public SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
      if (var2 == null || var2.isSurfaceLost()) {
         try {
            var2 = this.d3dgc.createManagedSurface(var3, var4, this.transparency);
         } catch (InvalidPipeException var6) {
            this.d3dgc.getD3DDevice();
            if (!D3DGraphicsDevice.isD3DAvailable()) {
               this.invalidate();
               this.flush();
               return null;
            }
         }
      }

      return var2;
   }

   public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
      return var4 == null || this.transparency == 1;
   }
}
