package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;

public class DrawPolygons extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "DrawPolygons(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawPolygons locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (DrawPolygons) GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawPolygons(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawPolygons(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawPolygons(SunGraphics2D var1, SurfaceData var2, int[] var3, int[] var4, int[] var5, int var6, int var7, int var8, boolean var9);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("DrawPolygons not implemented for " + var1 + " with " + var2);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new DrawPolygons.TraceDrawPolygons(this);
   }

   private static class TraceDrawPolygons extends DrawPolygons {
      DrawPolygons target;

      public TraceDrawPolygons(DrawPolygons var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawPolygons(SunGraphics2D var1, SurfaceData var2, int[] var3, int[] var4, int[] var5, int var6, int var7, int var8, boolean var9) {
         tracePrimitive(this.target);
         this.target.DrawPolygons(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }
   }
}
