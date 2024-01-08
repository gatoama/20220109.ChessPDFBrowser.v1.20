package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.font.GlyphList;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.MaskFill;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

public class DrawGlyphList extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "DrawGlyphList(...)".toString();
   public static final int primTypeID = makePrimTypeID();

   public static DrawGlyphList locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (DrawGlyphList) com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   protected DrawGlyphList(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public DrawGlyphList(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      return new DrawGlyphList.General(var1, var2, var3);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new DrawGlyphList.TraceDrawGlyphList(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new DrawGlyphList((com.frojasg1.sun.java2d.loops.SurfaceType)null, (com.frojasg1.sun.java2d.loops.CompositeType)null, (com.frojasg1.sun.java2d.loops.SurfaceType)null));
   }

   private static class General extends DrawGlyphList {
      com.frojasg1.sun.java2d.loops.MaskFill maskop;

      public General(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
         this.maskop = MaskFill.locate(var1, var2, var3);
      }

      public void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         int[] var4 = var3.getBounds();
         int var5 = var3.getNumGlyphs();
         Region var6 = var1.getCompClip();
         int var7 = var6.getLoX();
         int var8 = var6.getLoY();
         int var9 = var6.getHiX();
         int var10 = var6.getHiY();

         for(int var11 = 0; var11 < var5; ++var11) {
            var3.setGlyphIndex(var11);
            int[] var12 = var3.getMetrics();
            int var13 = var12[0];
            int var14 = var12[1];
            int var15 = var12[2];
            int var16 = var13 + var15;
            int var17 = var14 + var12[3];
            int var18 = 0;
            if (var13 < var7) {
               var18 = var7 - var13;
               var13 = var7;
            }

            if (var14 < var8) {
               var18 += (var8 - var14) * var15;
               var14 = var8;
            }

            if (var16 > var9) {
               var16 = var9;
            }

            if (var17 > var10) {
               var17 = var10;
            }

            if (var16 > var13 && var17 > var14) {
               byte[] var19 = var3.getGrayBits();
               this.maskop.MaskFill(var1, var2, var1.composite, var13, var14, var16 - var13, var17 - var14, var19, var18, var15);
            }
         }

      }
   }

   private static class TraceDrawGlyphList extends DrawGlyphList {
      DrawGlyphList target;

      public TraceDrawGlyphList(DrawGlyphList var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void DrawGlyphList(SunGraphics2D var1, SurfaceData var2, GlyphList var3) {
         tracePrimitive(this.target);
         this.target.DrawGlyphList(var1, var2, var3);
      }
   }
}
