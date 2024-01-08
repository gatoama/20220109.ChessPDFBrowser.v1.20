package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.BufferedMaskBlit;
import com.frojasg1.sun.java2d.pipe.Region;

class D3DMaskBlit extends BufferedMaskBlit {
   static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new D3DMaskBlit(SurfaceType.IntArgb, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntArgbPre, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntRgb, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntRgb, CompositeType.SrcNoEa), new D3DMaskBlit(SurfaceType.IntBgr, CompositeType.SrcOver), new D3DMaskBlit(SurfaceType.IntBgr, CompositeType.SrcNoEa)};
      GraphicsPrimitiveMgr.register(var0);
   }

   private D3DMaskBlit(SurfaceType var1, CompositeType var2) {
      super(D3DRenderQueue.getInstance(), var1, var2, com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DSurface);
   }

   protected void validateContext(SurfaceData var1, Composite var2, Region var3) {
      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var4 = (D3DSurfaceData)var1;
      com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var4, var4, var3, var2, (AffineTransform)null, (Paint)null, (SunGraphics2D)null, 0);
   }
}
