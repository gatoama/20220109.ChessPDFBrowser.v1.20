package com.frojasg1.sun.java2d.opengl;

import java.awt.Color;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.SurfaceDataProxy;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;

public class OGLSurfaceDataProxy extends SurfaceDataProxy {
   com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig oglgc;
   int transparency;

   public static SurfaceDataProxy createProxy(SurfaceData var0, com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig var1) {
      return (SurfaceDataProxy)(var0 instanceof OGLSurfaceData ? UNCACHED : new OGLSurfaceDataProxy(var1, var0.getTransparency()));
   }

   public OGLSurfaceDataProxy(com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig var1, int var2) {
      this.oglgc = var1;
      this.transparency = var2;
   }

   public SurfaceData validateSurfaceData(SurfaceData var1, SurfaceData var2, int var3, int var4) {
      if (var2 == null) {
         try {
            var2 = this.oglgc.createManagedSurface(var3, var4, this.transparency);
         } catch (OutOfMemoryError var6) {
            return null;
         }
      }

      return var2;
   }

   public boolean isSupportedOperation(SurfaceData var1, int var2, CompositeType var3, Color var4) {
      return var3.isDerivedFrom(CompositeType.AnyAlpha) && (var4 == null || this.transparency == 1);
   }
}
