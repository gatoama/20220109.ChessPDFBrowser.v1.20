package com.frojasg1.sun.java2d.loops;

import java.awt.geom.Path2D.Float;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;

public class FillPath extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "FillPath(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static FillPath locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (FillPath) GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected FillPath(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public FillPath(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void FillPath(SunGraphics2D var1, SurfaceData var2, int var3, int var4, Float var5);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("FillPath not implemented for " + var1 + " with " + var2);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new FillPath.TraceFillPath(this);
   }

   private static class TraceFillPath extends FillPath {
      FillPath target;

      public TraceFillPath(FillPath var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void FillPath(SunGraphics2D var1, SurfaceData var2, int var3, int var4, Float var5) {
         tracePrimitive(this.target);
         this.target.FillPath(var1, var2, var3, var4, var5);
      }
   }
}
