package com.frojasg1.sun.java2d.loops;

import java.awt.Composite;
import java.awt.geom.AffineTransform;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.MaskBlit;
import com.frojasg1.sun.java2d.loops.RenderCache;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

public class TransformHelper extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "TransformHelper(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static com.frojasg1.sun.java2d.loops.RenderCache helpercache = new RenderCache(10);

   public static TransformHelper locate(com.frojasg1.sun.java2d.loops.SurfaceType var0) {
      return (TransformHelper) GraphicsPrimitiveMgr.locate(primTypeID, var0, com.frojasg1.sun.java2d.loops.CompositeType.SrcNoEa, com.frojasg1.sun.java2d.loops.SurfaceType.IntArgbPre);
   }

   public static synchronized TransformHelper getFromCache(com.frojasg1.sun.java2d.loops.SurfaceType var0) {
      Object var1 = helpercache.get(var0, (com.frojasg1.sun.java2d.loops.CompositeType)null, (com.frojasg1.sun.java2d.loops.SurfaceType)null);
      if (var1 != null) {
         return (TransformHelper)var1;
      } else {
         TransformHelper var2 = locate(var0);
         if (var2 != null) {
            helpercache.put(var0, (com.frojasg1.sun.java2d.loops.CompositeType)null, (com.frojasg1.sun.java2d.loops.SurfaceType)null, var2);
         }

         return var2;
      }
   }

   protected TransformHelper(com.frojasg1.sun.java2d.loops.SurfaceType var1) {
      super(methodSignature, primTypeID, var1, com.frojasg1.sun.java2d.loops.CompositeType.SrcNoEa, com.frojasg1.sun.java2d.loops.SurfaceType.IntArgbPre);
   }

   public TransformHelper(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void Transform(com.frojasg1.sun.java2d.loops.MaskBlit var1, SurfaceData var2, SurfaceData var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int[] var16, int var17, int var18);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
      return null;
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new TransformHelper.TraceTransformHelper(this);
   }

   private static class TraceTransformHelper extends TransformHelper {
      TransformHelper target;

      public TraceTransformHelper(TransformHelper var1) {
         super(var1.getSourceType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void Transform(MaskBlit var1, SurfaceData var2, SurfaceData var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, int var12, int var13, int var14, int var15, int[] var16, int var17, int var18) {
         tracePrimitive(this.target);
         this.target.Transform(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13, var14, var15, var16, var17, var18);
      }
   }
}
