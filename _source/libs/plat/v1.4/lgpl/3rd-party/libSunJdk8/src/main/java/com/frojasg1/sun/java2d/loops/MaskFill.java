package com.frojasg1.sun.java2d.loops;

import java.awt.Composite;
import java.awt.image.BufferedImage;
import com.frojasg1.sun.awt.image.BufImgSurfaceData;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.FillRect;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.MaskBlit;
import com.frojasg1.sun.java2d.loops.RenderCache;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

public class MaskFill extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "MaskFill(...)".toString();
   public static final String fillPgramSignature = "FillAAPgram(...)".toString();
   public static final String drawPgramSignature = "DrawAAPgram(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static com.frojasg1.sun.java2d.loops.RenderCache fillcache = new RenderCache(10);

   public static MaskFill locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (MaskFill) com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static MaskFill locatePrim(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (MaskFill) com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr.locatePrim(primTypeID, var0, var1, var2);
   }

   public static MaskFill getFromCache(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      Object var3 = fillcache.get(var0, var1, var2);
      if (var3 != null) {
         return (MaskFill)var3;
      } else {
         MaskFill var4 = locatePrim(var0, var1, var2);
         if (var4 != null) {
            fillcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected MaskFill(String var1, com.frojasg1.sun.java2d.loops.SurfaceType var2, com.frojasg1.sun.java2d.loops.CompositeType var3, com.frojasg1.sun.java2d.loops.SurfaceType var4) {
      super(var1, primTypeID, var2, var3, var4);
   }

   protected MaskFill(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public MaskFill(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10);

   public native void FillAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14);

   public native void DrawAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18);

   public boolean canDoParallelograms() {
      return this.getNativePrim() != 0L;
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      if (!com.frojasg1.sun.java2d.loops.SurfaceType.OpaqueColor.equals(var1) && !com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor.equals(var1)) {
         throw new InternalError("MaskFill can only fill with colors");
      } else if (com.frojasg1.sun.java2d.loops.CompositeType.Xor.equals(var2)) {
         throw new InternalError("Cannot construct MaskFill for XOR mode");
      } else {
         return new MaskFill.General(var1, var2, var3);
      }
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new MaskFill.TraceMaskFill(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new MaskFill((com.frojasg1.sun.java2d.loops.SurfaceType)null, (com.frojasg1.sun.java2d.loops.CompositeType)null, (com.frojasg1.sun.java2d.loops.SurfaceType)null));
   }

   private static class General extends MaskFill {
      com.frojasg1.sun.java2d.loops.FillRect fillop;
      com.frojasg1.sun.java2d.loops.MaskBlit maskop;

      public General(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
         super(var1, var2, var3);
         this.fillop = FillRect.locate(var1, CompositeType.SrcNoEa, com.frojasg1.sun.java2d.loops.SurfaceType.IntArgb);
         this.maskop = MaskBlit.locate(SurfaceType.IntArgb, var2, var3);
      }

      public void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10) {
         BufferedImage var11 = new BufferedImage(var6, var7, 2);
         SurfaceData var12 = BufImgSurfaceData.createData(var11);
         Region var13 = var1.clipRegion;
         var1.clipRegion = null;
         int var14 = var1.pixel;
         var1.pixel = var12.pixelFor(var1.getColor());
         this.fillop.FillRect(var1, var12, 0, 0, var6, var7);
         var1.pixel = var14;
         var1.clipRegion = var13;
         this.maskop.MaskBlit(var12, var2, var3, (Region)null, 0, 0, var4, var5, var6, var7, var8, var9, var10);
      }
   }

   private static class TraceMaskFill extends MaskFill {
      MaskFill target;
      MaskFill fillPgramTarget;
      MaskFill drawPgramTarget;

      public TraceMaskFill(MaskFill var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
         this.fillPgramTarget = new MaskFill(com.frojasg1.sun.java2d.loops.MaskFill.fillPgramSignature, var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.drawPgramTarget = new MaskFill(com.frojasg1.sun.java2d.loops.MaskFill.drawPgramSignature, var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void MaskFill(SunGraphics2D var1, SurfaceData var2, Composite var3, int var4, int var5, int var6, int var7, byte[] var8, int var9, int var10) {
         tracePrimitive(this.target);
         this.target.MaskFill(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
      }

      public void FillAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14) {
         tracePrimitive(this.fillPgramTarget);
         this.target.FillAAPgram(var1, var2, var3, var4, var6, var8, var10, var12, var14);
      }

      public void DrawAAPgram(SunGraphics2D var1, SurfaceData var2, Composite var3, double var4, double var6, double var8, double var10, double var12, double var14, double var16, double var18) {
         tracePrimitive(this.drawPgramTarget);
         this.target.DrawAAPgram(var1, var2, var3, var4, var6, var8, var10, var12, var14, var16, var18);
      }

      public boolean canDoParallelograms() {
         return this.target.canDoParallelograms();
      }
   }
}
