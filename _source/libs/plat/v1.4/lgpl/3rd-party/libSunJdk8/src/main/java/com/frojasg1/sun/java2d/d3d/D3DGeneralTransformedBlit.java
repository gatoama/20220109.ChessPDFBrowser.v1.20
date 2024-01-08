package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import java.lang.ref.WeakReference;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.loops.TransformBlit;
import com.frojasg1.sun.java2d.pipe.Region;

final class D3DGeneralTransformedBlit extends TransformBlit {
   private final TransformBlit performop;
   private WeakReference<SurfaceData> srcTmp;

   D3DGeneralTransformedBlit(TransformBlit var1) {
      super(SurfaceType.Any, CompositeType.AnyAlpha, D3DSurfaceData.D3DSurface);
      this.performop = var1;
   }

   public synchronized void Transform(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, AffineTransform var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12) {
      Blit var13 = Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcNoEa, SurfaceType.IntArgbPre);
      SurfaceData var14 = this.srcTmp != null ? (SurfaceData)this.srcTmp.get() : null;
      var1 = convertFrom(var13, var1, var7, var8, var11, var12, var14, 3);
      this.performop.Transform(var1, var2, var3, var4, var5, var6, 0, 0, var9, var10, var11, var12);
      if (var1 != var14) {
         this.srcTmp = new WeakReference(var1);
      }

   }
}
