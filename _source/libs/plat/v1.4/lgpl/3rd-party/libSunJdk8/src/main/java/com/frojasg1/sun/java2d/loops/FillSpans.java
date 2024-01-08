package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.SpanIterator;

public class FillSpans extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "FillSpans(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static FillSpans locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (FillSpans) GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected FillSpans(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public FillSpans(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   private native void FillSpans(SunGraphics2D var1, SurfaceData var2, int var3, long var4, SpanIterator var6);

   public void FillSpans(SunGraphics2D var1, SurfaceData var2, SpanIterator var3) {
      this.FillSpans(var1, var2, var1.pixel, var3.getNativeIterator(), var3);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
      throw new InternalError("FillSpans not implemented for " + var1 + " with " + var2);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new FillSpans.TraceFillSpans(this);
   }

   private static class TraceFillSpans extends FillSpans {
      FillSpans target;

      public TraceFillSpans(FillSpans var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void FillSpans(SunGraphics2D var1, SurfaceData var2, SpanIterator var3) {
         tracePrimitive(this.target);
         this.target.FillSpans(var1, var2, var3);
      }
   }
}
