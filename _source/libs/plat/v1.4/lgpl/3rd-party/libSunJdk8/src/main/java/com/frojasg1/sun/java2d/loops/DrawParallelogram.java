package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;

public class DrawParallelogram extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "DrawParallelogram(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawParallelogram locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (DrawParallelogram) GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawParallelogram(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawParallelogram(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawParallelogram(SunGraphics2D var1, SurfaceData var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("DrawParallelogram not implemented for " + var1 + " with " + var2);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new DrawParallelogram.TraceDrawParallelogram(this);
   }

   private static class TraceDrawParallelogram extends DrawParallelogram {
      DrawParallelogram target;

      public TraceDrawParallelogram(DrawParallelogram var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawParallelogram(SunGraphics2D var1, SurfaceData var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17) {
         tracePrimitive(this.target);
         this.target.DrawParallelogram(var1, var2, var3, var5, var7, var9, var11, var13, var15, var17);
      }
   }
}
