package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.BufferedMaskFill;

class D3DMaskFill extends BufferedMaskFill {
   static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new D3DMaskFill(SurfaceType.AnyColor, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueColor, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.GradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.LinearGradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueLinearGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.RadialGradientPaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueRadialGradientPaint, CompositeType.SrcNoEa), new D3DMaskFill(SurfaceType.TexturePaint, CompositeType.SrcOver), new D3DMaskFill(SurfaceType.OpaqueTexturePaint, CompositeType.SrcNoEa)};
      GraphicsPrimitiveMgr.register(var0);
   }

   protected D3DMaskFill(SurfaceType var1, CompositeType var2) {
      super(D3DRenderQueue.getInstance(), var1, var2, com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DSurface);
   }

   protected native void maskFill(int var1, int var2, int var3, int var4, int var5, int var6, int var7, byte[] var8);

   protected void validateContext(SunGraphics2D var1, Composite var2, int var3) {
      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var4;
      try {
         var4 = (D3DSurfaceData)var1.surfaceData;
      } catch (ClassCastException var6) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var4, var4, var1.getCompClip(), var2, (AffineTransform)null, var1.paint, var1, var3);
   }
}
