package com.frojasg1.sun.java2d.loops;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Hashtable;
import com.frojasg1.sun.awt.image.BufImgSurfaceData;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.FillRect;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.RenderCache;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

public class BlitBg extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "BlitBg(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static com.frojasg1.sun.java2d.loops.RenderCache blitcache = new RenderCache(20);

   public static BlitBg locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (BlitBg) com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static BlitBg getFromCache(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      Object var3 = blitcache.get(var0, var1, var2);
      if (var3 != null) {
         return (BlitBg)var3;
      } else {
         BlitBg var4 = locate(var0, var1, var2);
         if (var4 == null) {
            System.out.println("blitbg loop not found for:");
            System.out.println("src:  " + var0);
            System.out.println("comp: " + var1);
            System.out.println("dst:  " + var2);
         } else {
            blitcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected BlitBg(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public BlitBg(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void BlitBg(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      return new BlitBg.General(var1, var2, var3);
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new BlitBg.TraceBlitBg(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new BlitBg((com.frojasg1.sun.java2d.loops.SurfaceType)null, (com.frojasg1.sun.java2d.loops.CompositeType)null, (com.frojasg1.sun.java2d.loops.SurfaceType)null));
   }

   private static class General extends BlitBg {
      com.frojasg1.sun.java2d.loops.CompositeType compositeType;
      private static Font defaultFont = new Font("Dialog", 0, 12);

      public General(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
         super(var1, var2, var3);
         this.compositeType = var2;
      }

      public void BlitBg(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
         ColorModel var12 = var2.getColorModel();
         boolean var13 = var5 >>> 24 != 255;
         if (!var12.hasAlpha() && var13) {
            var12 = ColorModel.getRGBdefault();
         }

         WritableRaster var14 = var12.createCompatibleWritableRaster(var10, var11);
         boolean var15 = var12.isAlphaPremultiplied();
         BufferedImage var16 = new BufferedImage(var12, var14, var15, (Hashtable)null);
         SurfaceData var17 = BufImgSurfaceData.createData(var16);
         Color var18 = new Color(var5, var13);
         SunGraphics2D var19 = new SunGraphics2D(var17, var18, var18, defaultFont);
         com.frojasg1.sun.java2d.loops.FillRect var20 = FillRect.locate(SurfaceType.AnyColor, com.frojasg1.sun.java2d.loops.CompositeType.SrcNoEa, var17.getSurfaceType());
         com.frojasg1.sun.java2d.loops.Blit var21 = com.frojasg1.sun.java2d.loops.Blit.getFromCache(var1.getSurfaceType(), CompositeType.SrcOverNoEa, var17.getSurfaceType());
         com.frojasg1.sun.java2d.loops.Blit var22 = Blit.getFromCache(var17.getSurfaceType(), this.compositeType, var2.getSurfaceType());
         var20.FillRect(var19, var17, 0, 0, var10, var11);
         var21.Blit(var1, var17, AlphaComposite.SrcOver, (Region)null, var6, var7, 0, 0, var10, var11);
         var22.Blit(var17, var2, var3, var4, 0, 0, var8, var9, var10, var11);
      }
   }

   private static class TraceBlitBg extends BlitBg {
      BlitBg target;

      public TraceBlitBg(BlitBg var1) {
         super(var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void BlitBg(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11) {
         tracePrimitive(this.target);
         this.target.BlitBg(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      }
   }
}
