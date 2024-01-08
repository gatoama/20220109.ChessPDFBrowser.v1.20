package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.font.GlyphList;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;

public class DrawGlyphListLCD extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "DrawGlyphListLCD(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawGlyphListLCD locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (DrawGlyphListLCD) com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawGlyphListLCD(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawGlyphListLCD(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawGlyphListLCD(SunGraphics2D var1, SurfaceData var2, GlyphList var3);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      return null;
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new DrawGlyphListLCD.TraceDrawGlyphListLCD(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphListLCD((com.frojasg1.sun.java2d.loops.SurfaceType)null, (CompositeType)null, (SurfaceType)null));
   }

   private static class TraceDrawGlyphListLCD extends DrawGlyphListLCD {
      DrawGlyphListLCD target;

      public TraceDrawGlyphListLCD(DrawGlyphListLCD var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawGlyphListLCD(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         tracePrimitive(this.target);
         this.target.DrawGlyphListLCD(var1, var2, var3);
      }
   }
}
