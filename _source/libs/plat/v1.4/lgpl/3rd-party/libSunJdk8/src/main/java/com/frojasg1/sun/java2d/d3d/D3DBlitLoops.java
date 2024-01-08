package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import com.frojasg1.sun.java2d.ScreenUpdateManager;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DBufImgOps;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DGeneralBlit;
import com.frojasg1.sun.java2d.d3d.D3DGeneralTransformedBlit;
import com.frojasg1.sun.java2d.d3d.D3DRTTSurfaceToSurfaceBlit;
import com.frojasg1.sun.java2d.d3d.D3DRTTSurfaceToSurfaceScale;
import com.frojasg1.sun.java2d.d3d.D3DRTTSurfaceToSurfaceTransform;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToGDIWindowSurfaceBlit;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToGDIWindowSurfaceScale;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToGDIWindowSurfaceTransform;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToSurfaceBlit;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToSurfaceScale;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToSurfaceTransform;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceToSwBlit;
import com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit;
import com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale;
import com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform;
import com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit;
import com.frojasg1.sun.java2d.d3d.D3DTextureToSurfaceBlit;
import com.frojasg1.sun.java2d.d3d.D3DTextureToSurfaceScale;
import com.frojasg1.sun.java2d.d3d.D3DTextureToSurfaceTransform;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.pipe.RenderBuffer;
import com.frojasg1.sun.java2d.pipe.RenderQueue;

final class D3DBlitLoops {
   private static final int OFFSET_SRCTYPE = 16;
   private static final int OFFSET_HINT = 8;
   private static final int OFFSET_TEXTURE = 3;
   private static final int OFFSET_RTT = 2;
   private static final int OFFSET_XFORM = 1;
   private static final int OFFSET_ISOBLIT = 0;

   D3DBlitLoops() {
   }

   static void register() {
      com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit var0 = new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.IntArgbPre, 1);
      com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit var1 = new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.IntArgbPre, 1);
      com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform var2 = new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.IntArgbPre, 1);
      GraphicsPrimitive[] var3 = new GraphicsPrimitive[]{new com.frojasg1.sun.java2d.d3d.D3DSurfaceToGDIWindowSurfaceBlit(), new com.frojasg1.sun.java2d.d3d.D3DSurfaceToGDIWindowSurfaceScale(), new com.frojasg1.sun.java2d.d3d.D3DSurfaceToGDIWindowSurfaceTransform(), new com.frojasg1.sun.java2d.d3d.D3DSurfaceToSurfaceBlit(), new com.frojasg1.sun.java2d.d3d.D3DSurfaceToSurfaceScale(), new com.frojasg1.sun.java2d.d3d.D3DSurfaceToSurfaceTransform(), new com.frojasg1.sun.java2d.d3d.D3DRTTSurfaceToSurfaceBlit(), new com.frojasg1.sun.java2d.d3d.D3DRTTSurfaceToSurfaceScale(), new com.frojasg1.sun.java2d.d3d.D3DRTTSurfaceToSurfaceTransform(), new com.frojasg1.sun.java2d.d3d.D3DSurfaceToSwBlit(SurfaceType.IntArgb, 0), var0, new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.IntArgb, 0), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.IntRgb, 3), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.ThreeByteBgr, 9), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.Ushort565Rgb, 5), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.Ushort555Rgb, 6), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceBlit(SurfaceType.ByteIndexed, 7), new com.frojasg1.sun.java2d.d3d.D3DGeneralBlit(com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DSurface, CompositeType.AnyAlpha, var0), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.IntArgb, 0), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.IntArgbPre, 1), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.IntRgb, 3), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.ThreeByteBgr, 9), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.Ushort565Rgb, 5), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.Ushort555Rgb, 6), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceScale(SurfaceType.ByteIndexed, 7), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.IntArgb, 0), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.IntRgb, 3), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.ThreeByteBgr, 9), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.Ushort565Rgb, 5), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.Ushort555Rgb, 6), new com.frojasg1.sun.java2d.d3d.D3DSwToSurfaceTransform(SurfaceType.ByteIndexed, 7), var2, new com.frojasg1.sun.java2d.d3d.D3DGeneralTransformedBlit(var2), new com.frojasg1.sun.java2d.d3d.D3DTextureToSurfaceBlit(), new com.frojasg1.sun.java2d.d3d.D3DTextureToSurfaceScale(), new com.frojasg1.sun.java2d.d3d.D3DTextureToSurfaceTransform(), var1, new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.IntRgb, 3), new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.IntArgb, 0), new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.ThreeByteBgr, 9), new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.Ushort565Rgb, 5), new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.Ushort555Rgb, 6), new com.frojasg1.sun.java2d.d3d.D3DSwToTextureBlit(SurfaceType.ByteIndexed, 7), new com.frojasg1.sun.java2d.d3d.D3DGeneralBlit(com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DTexture, CompositeType.SrcNoEa, var1)};
      GraphicsPrimitiveMgr.register(var3);
   }

   private static int createPackedParams(boolean var0, boolean var1, boolean var2, boolean var3, int var4, int var5) {
      return var5 << 16 | var4 << 8 | (var1 ? 1 : 0) << 3 | (var2 ? 1 : 0) << 2 | (var3 ? 1 : 0) << 1 | (var0 ? 1 : 0) << 0;
   }

   private static void enqueueBlit(RenderQueue var0, SurfaceData var1, SurfaceData var2, int var3, int var4, int var5, int var6, int var7, double var8, double var10, double var12, double var14) {
      RenderBuffer var16 = var0.getBuffer();
      var0.ensureCapacityAndAlignment(72, 24);
      var16.putInt(31);
      var16.putInt(var3);
      var16.putInt(var4).putInt(var5);
      var16.putInt(var6).putInt(var7);
      var16.putDouble(var8).putDouble(var10);
      var16.putDouble(var12).putDouble(var14);
      var16.putLong(var1.getNativeOps());
      var16.putLong(var2.getNativeOps());
   }

   static void Blit(SurfaceData var0, SurfaceData var1, Composite var2, Region var3, AffineTransform var4, int var5, int var6, int var7, int var8, int var9, double var10, double var12, double var14, double var16, int var18, boolean var19) {
      int var20 = 0;
      if (var0.getTransparency() == 1) {
         var20 |= 1;
      }

      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var21 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1;
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var22 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var22.lock();

      try {
         var22.addReference(var0);
         if (var19) {
            com.frojasg1.sun.java2d.d3d.D3DContext.setScratchSurface(var21.getContext());
         } else {
            com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var21, var21, var3, var2, var4, (Paint)null, (SunGraphics2D)null, var20);
         }

         int var23 = createPackedParams(false, var19, false, var4 != null, var5, var18);
         enqueueBlit(var22, var0, var1, var23, var6, var7, var8, var9, var10, var12, var14, var16);
         var22.flushNow();
      } finally {
         var22.unlock();
      }

      if (var21.getType() == 1) {
         com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager var27 = (com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager)ScreenUpdateManager.getInstance();
         var27.runUpdateNow();
      }

   }

   static void IsoBlit(SurfaceData var0, SurfaceData var1, BufferedImage var2, BufferedImageOp var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, double var12, double var14, double var16, double var18, boolean var20) {
      int var21 = 0;
      if (var0.getTransparency() == 1) {
         var21 |= 1;
      }

      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var22 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1;
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var23 = D3DRenderQueue.getInstance();
      boolean var24 = false;
      var23.lock();

      try {
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData var25 = (D3DSurfaceData)var0;
         int var26 = var25.getType();
         if (var26 == 3) {
            var24 = false;
         } else {
            var24 = true;
         }

         com.frojasg1.sun.java2d.d3d.D3DContext.validateContext(var25, var22, var5, var4, var6, (Paint)null, (SunGraphics2D)null, var21);
         if (var3 != null) {
            com.frojasg1.sun.java2d.d3d.D3DBufImgOps.enableBufImgOp(var23, var25, var2, var3);
         }

         int var28 = createPackedParams(true, var20, var24, var6 != null, var7, 0);
         enqueueBlit(var23, var0, var1, var28, var8, var9, var10, var11, var12, var14, var16, var18);
         if (var3 != null) {
            com.frojasg1.sun.java2d.d3d.D3DBufImgOps.disableBufImgOp(var23, var3);
         }
      } finally {
         var23.unlock();
      }

      if (var24 && var22.getType() == 1) {
         com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager var32 = (D3DScreenUpdateManager)ScreenUpdateManager.getInstance();
         var32.runUpdateNow();
      }

   }
}
