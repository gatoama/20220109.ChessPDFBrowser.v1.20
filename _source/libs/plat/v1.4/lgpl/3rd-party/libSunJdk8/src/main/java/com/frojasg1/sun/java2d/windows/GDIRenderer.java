package com.frojasg1.sun.java2d.windows;

import java.awt.Composite;
import java.awt.Shape;
import java.awt.geom.Path2D.Float;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.pipe.LoopPipe;
import com.frojasg1.sun.java2d.pipe.PixelDrawPipe;
import com.frojasg1.sun.java2d.pipe.PixelFillPipe;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.pipe.ShapeDrawPipe;
import com.frojasg1.sun.java2d.pipe.ShapeSpanIterator;
import com.frojasg1.sun.java2d.pipe.SpanIterator;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

public class GDIRenderer implements PixelDrawPipe, PixelFillPipe, ShapeDrawPipe {
   public GDIRenderer() {
   }

   native void doDrawLine(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8);

   public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      int var6 = var1.transX;
      int var7 = var1.transY;

      try {
         this.doDrawLine((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var6, var3 + var7, var4 + var6, var5 + var7);
      } catch (ClassCastException var9) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doDrawRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8);

   public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      try {
         this.doDrawRect((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } catch (ClassCastException var7) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doDrawRoundRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public void drawRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      try {
         this.doDrawRoundRect((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } catch (ClassCastException var9) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doDrawOval(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8);

   public void drawOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      try {
         this.doDrawOval((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } catch (ClassCastException var7) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doDrawArc(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public void drawArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      try {
         this.doDrawArc((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } catch (ClassCastException var9) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doDrawPoly(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int[] var7, int[] var8, int var9, boolean var10);

   public void drawPolyline(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      try {
         this.doDrawPoly((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var1.transX, var1.transY, var2, var3, var4, false);
      } catch (ClassCastException var6) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   public void drawPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      try {
         this.doDrawPoly((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var1.transX, var1.transY, var2, var3, var4, true);
      } catch (ClassCastException var6) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doFillRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8);

   public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      try {
         this.doFillRect((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } catch (ClassCastException var7) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doFillRoundRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public void fillRoundRect(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      try {
         this.doFillRoundRect((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } catch (ClassCastException var9) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doFillOval(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8);

   public void fillOval(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
      try {
         this.doFillOval((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5);
      } catch (ClassCastException var7) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doFillArc(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10);

   public void fillArc(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      try {
         this.doFillArc((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var2 + var1.transX, var3 + var1.transY, var4, var5, var6, var7);
      } catch (ClassCastException var9) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doFillPoly(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int[] var7, int[] var8, int var9);

   public void fillPolygon(SunGraphics2D var1, int[] var2, int[] var3, int var4) {
      try {
         this.doFillPoly((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var1.transX, var1.transY, var2, var3, var4);
      } catch (ClassCastException var6) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   native void doShape(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, Float var7, boolean var8);

   void doShape(SunGraphics2D var1, Shape var2, boolean var3) {
      Float var4;
      int var5;
      int var6;
      if (var1.transformState <= 1) {
         if (var2 instanceof Float) {
            var4 = (Float)var2;
         } else {
            var4 = new Float(var2);
         }

         var5 = var1.transX;
         var6 = var1.transY;
      } else {
         var4 = new Float(var2, var1.transform);
         var5 = 0;
         var6 = 0;
      }

      try {
         this.doShape((com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData, var1.getCompClip(), var1.composite, var1.eargb, var5, var6, var4, var3);
      } catch (ClassCastException var8) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }
   }

   public void doFillSpans(SunGraphics2D var1, SpanIterator var2) {
      int[] var3 = new int[4];

      com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var4;
      try {
         var4 = (com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData)var1.surfaceData;
      } catch (ClassCastException var8) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      Region var5 = var1.getCompClip();
      Composite var6 = var1.composite;
      int var7 = var1.eargb;

      while(var2.nextSpan(var3)) {
         this.doFillRect(var4, var5, var6, var7, var3[0], var3[1], var3[2] - var3[0], var3[3] - var3[1]);
      }

   }

   public void draw(SunGraphics2D var1, Shape var2) {
      if (var1.strokeState == 0) {
         this.doShape(var1, var2, false);
      } else if (var1.strokeState < 3) {
         ShapeSpanIterator var3 = LoopPipe.getStrokeSpans(var1, var2);

         try {
            this.doFillSpans(var1, var3);
         } finally {
            var3.dispose();
         }
      } else {
         this.doShape(var1, var1.stroke.createStrokedShape(var2), true);
      }

   }

   public void fill(SunGraphics2D var1, Shape var2) {
      this.doShape(var1, var2, true);
   }

   public native void devCopyArea(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, int var2, int var3, int var4, int var5, int var6, int var7);

   public GDIRenderer traceWrap() {
      return new GDIRenderer.Tracer();
   }

   public static class Tracer extends GDIRenderer {
      public Tracer() {
      }

      void doDrawLine(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("GDIDrawLine");
         super.doDrawLine(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      void doDrawRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("GDIDrawRect");
         super.doDrawRect(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      void doDrawRoundRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("GDIDrawRoundRect");
         super.doDrawRoundRect(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      void doDrawOval(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("GDIDrawOval");
         super.doDrawOval(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      void doDrawArc(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("GDIDrawArc");
         super.doDrawArc(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      void doDrawPoly(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int[] var7, int[] var8, int var9, boolean var10) {
         GraphicsPrimitive.tracePrimitive("GDIDrawPoly");
         super.doDrawPoly(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      void doFillRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("GDIFillRect");
         super.doFillRect(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      void doFillRoundRect(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("GDIFillRoundRect");
         super.doFillRoundRect(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      void doFillOval(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8) {
         GraphicsPrimitive.tracePrimitive("GDIFillOval");
         super.doFillOval(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      void doFillArc(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10) {
         GraphicsPrimitive.tracePrimitive("GDIFillArc");
         super.doFillArc(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      void doFillPoly(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, int[] var7, int[] var8, int var9) {
         GraphicsPrimitive.tracePrimitive("GDIFillPoly");
         super.doFillPoly(var1, var2, var3, var4, var5, var6, var7, var8, var9);
      }

      void doShape(com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData var1, Region var2, Composite var3, int var4, int var5, int var6, Float var7, boolean var8) {
         GraphicsPrimitive.tracePrimitive(var8 ? "GDIFillShape" : "GDIDrawShape");
         super.doShape(var1, var2, var3, var4, var5, var6, var7, var8);
      }

      public void devCopyArea(GDIWindowSurfaceData var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         GraphicsPrimitive.tracePrimitive("GDICopyArea");
         super.devCopyArea(var1, var2, var3, var4, var5, var6, var7);
      }
   }
}
