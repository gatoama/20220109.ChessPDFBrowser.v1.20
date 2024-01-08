package com.frojasg1.sun.java2d.d3d;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D.Float;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.pipe.BufferedRenderPipe;
import com.frojasg1.sun.java2d.pipe.ParallelogramPipe;
import com.frojasg1.sun.java2d.pipe.RenderQueue;
import com.frojasg1.sun.java2d.pipe.SpanIterator;

class D3DRenderer extends BufferedRenderPipe {
   D3DRenderer(RenderQueue var1) {
      super(var1);
   }

   protected void validateContext(SunGraphics2D var1) {
      int var2 = var1.paint.getTransparency() == 1 ? 1 : 0;

      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var3;
      try {
         var3 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1.surfaceData;
      } catch (ClassCastException var5) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var3, var3, var1.getCompClip(), var1.composite, (AffineTransform)null, var1.paint, var1, var2);
   }

   protected void validateContextAA(SunGraphics2D var1) {
      byte var2 = 0;

      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var3;
      try {
         var3 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1.surfaceData;
      } catch (ClassCastException var5) {
         throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
      }

      com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var3, var3, var1.getCompClip(), var1.composite, (AffineTransform)null, var1.paint, var1, var2);
   }

   void copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      this.rq.lock();

      try {
         int var8 = var1.surfaceData.getTransparency() == 1 ? 1 : 0;

         com.frojasg1.sun.java2d.d3d.D3DSurfaceData var9;
         try {
            var9 = (D3DSurfaceData)var1.surfaceData;
         } catch (ClassCastException var14) {
            throw new InvalidPipeException("wrong surface data type: " + var1.surfaceData);
         }

         com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var9, var9, var1.getCompClip(), var1.composite, (AffineTransform)null, (Paint)null, (SunGraphics2D)null, var8);
         this.rq.ensureCapacity(28);
         this.buf.putInt(30);
         this.buf.putInt(var2).putInt(var3).putInt(var4).putInt(var5);
         this.buf.putInt(var6).putInt(var7);
      } finally {
         this.rq.unlock();
      }

   }

   protected native void drawPoly(int[] var1, int[] var2, int var3, boolean var4, int var5, int var6);

   D3DRenderer traceWrap() {
      return new D3DRenderer.Tracer(this);
   }

   private class Tracer extends D3DRenderer {
      private D3DRenderer d3dr;

      Tracer(D3DRenderer var2) {
         super(var2.rq);
         this.d3dr = var2;
      }

      public ParallelogramPipe getAAParallelogramPipe() {
         final ParallelogramPipe var1 = this.d3dr.getAAParallelogramPipe();
         return new ParallelogramPipe() {
            public void fillParallelogram(SunGraphics2D var1x, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
               GraphicsPrimitive.tracePrimitive("D3DFillAAParallelogram");
               var1.fillParallelogram(var1x, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20);
            }

            public void drawParallelogram(SunGraphics2D var1x, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
               GraphicsPrimitive.tracePrimitive("D3DDrawAAParallelogram");
               var1.drawParallelogram(var1x, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24);
            }
         };
      }

      protected void validateContext(SunGraphics2D var1) {
         this.d3dr.validateContext(var1);
      }

      public void drawLine(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
         GraphicsPrimitive.tracePrimitive("D3DDrawLine");
         this.d3dr.drawLine(var1, var2, var3, var4, var5);
      }

      public void drawRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
         GraphicsPrimitive.tracePrimitive("D3DDrawRect");
         this.d3dr.drawRect(var1, var2, var3, var4, var5);
      }

      protected void drawPoly(SunGraphics2D var1, int[] var2, int[] var3, int var4, boolean var5) {
         GraphicsPrimitive.tracePrimitive("D3DDrawPoly");
         this.d3dr.drawPoly(var1, var2, var3, var4, var5);
      }

      public void fillRect(SunGraphics2D var1, int var2, int var3, int var4, int var5) {
         GraphicsPrimitive.tracePrimitive("D3DFillRect");
         this.d3dr.fillRect(var1, var2, var3, var4, var5);
      }

      protected void drawPath(SunGraphics2D var1, Float var2, int var3, int var4) {
         GraphicsPrimitive.tracePrimitive("D3DDrawPath");
         this.d3dr.drawPath(var1, var2, var3, var4);
      }

      protected void fillPath(SunGraphics2D var1, Float var2, int var3, int var4) {
         GraphicsPrimitive.tracePrimitive("D3DFillPath");
         this.d3dr.fillPath(var1, var2, var3, var4);
      }

      protected void fillSpans(SunGraphics2D var1, SpanIterator var2, int var3, int var4) {
         GraphicsPrimitive.tracePrimitive("D3DFillSpans");
         this.d3dr.fillSpans(var1, var2, var3, var4);
      }

      public void fillParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20) {
         GraphicsPrimitive.tracePrimitive("D3DFillParallelogram");
         this.d3dr.fillParallelogram(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20);
      }

      public void drawParallelogram(SunGraphics2D var1, double var2, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18, double var20, double var22, double var24) {
         GraphicsPrimitive.tracePrimitive("D3DDrawParallelogram");
         this.d3dr.drawParallelogram(var1, var2, var4, var6, var8, var10, var12, var14, var16, var18, var20, var22, var24);
      }

      public void copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
         GraphicsPrimitive.tracePrimitive("D3DCopyArea");
         this.d3dr.copyArea(var1, var2, var3, var4, var5, var6, var7);
      }
   }
}
