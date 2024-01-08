package com.frojasg1.sun.java2d.opengl;

import java.awt.Composite;
import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.opengl.OGLAnyCompositeBlit;
import com.frojasg1.sun.java2d.opengl.OGLBufImgOps;
import com.frojasg1.sun.java2d.opengl.OGLContext;
import com.frojasg1.sun.java2d.opengl.OGLGeneralBlit;
import com.frojasg1.sun.java2d.opengl.OGLGeneralTransformedBlit;
import com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.OGLRTTSurfaceToSurfaceBlit;
import com.frojasg1.sun.java2d.opengl.OGLRTTSurfaceToSurfaceScale;
import com.frojasg1.sun.java2d.opengl.OGLRTTSurfaceToSurfaceTransform;
import com.frojasg1.sun.java2d.opengl.OGLRenderQueue;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceToSurfaceBlit;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceToSurfaceScale;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceToSurfaceTransform;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceToSwBlit;
import com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit;
import com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale;
import com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform;
import com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit;
import com.frojasg1.sun.java2d.opengl.OGLTextureToSurfaceBlit;
import com.frojasg1.sun.java2d.opengl.OGLTextureToSurfaceScale;
import com.frojasg1.sun.java2d.opengl.OGLTextureToSurfaceTransform;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.pipe.RenderBuffer;
import com.frojasg1.sun.java2d.pipe.RenderQueue;

final class OGLBlitLoops {
   private static final int OFFSET_SRCTYPE = 16;
   private static final int OFFSET_HINT = 8;
   private static final int OFFSET_TEXTURE = 3;
   private static final int OFFSET_RTT = 2;
   private static final int OFFSET_XFORM = 1;
   private static final int OFFSET_ISOBLIT = 0;

   OGLBlitLoops() {
   }

   static void register() {
      com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit var0 = new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.IntArgbPre, 1);
      com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit var1 = new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.IntArgbPre, 1);
      com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform var2 = new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.IntArgbPre, 1);
      com.frojasg1.sun.java2d.opengl.OGLSurfaceToSwBlit var3 = new com.frojasg1.sun.java2d.opengl.OGLSurfaceToSwBlit(SurfaceType.IntArgbPre, 1);
      GraphicsPrimitive[] var4 = new GraphicsPrimitive[]{new com.frojasg1.sun.java2d.opengl.OGLSurfaceToSurfaceBlit(), new com.frojasg1.sun.java2d.opengl.OGLSurfaceToSurfaceScale(), new com.frojasg1.sun.java2d.opengl.OGLSurfaceToSurfaceTransform(), new com.frojasg1.sun.java2d.opengl.OGLRTTSurfaceToSurfaceBlit(), new com.frojasg1.sun.java2d.opengl.OGLRTTSurfaceToSurfaceScale(), new com.frojasg1.sun.java2d.opengl.OGLRTTSurfaceToSurfaceTransform(), new com.frojasg1.sun.java2d.opengl.OGLSurfaceToSwBlit(SurfaceType.IntArgb, 0), var3, var0, new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.IntRgb, 2), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.IntRgbx, 3), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.IntBgrx, 5), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.ThreeByteBgr, 11), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.Ushort565Rgb, 6), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgb, 7), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.Ushort555Rgbx, 8), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.ByteGray, 9), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceBlit(SurfaceType.UshortGray, 10), new com.frojasg1.sun.java2d.opengl.OGLGeneralBlit(com.frojasg1.sun.java2d.opengl.OGLSurfaceData.OpenGLSurface, CompositeType.AnyAlpha, var0), new com.frojasg1.sun.java2d.opengl.OGLAnyCompositeBlit(com.frojasg1.sun.java2d.opengl.OGLSurfaceData.OpenGLSurface, var3, var3, var0), new com.frojasg1.sun.java2d.opengl.OGLAnyCompositeBlit(SurfaceType.Any, (Blit)null, var3, var0), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.IntRgb, 2), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.IntRgbx, 3), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.IntBgrx, 5), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.ThreeByteBgr, 11), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.Ushort565Rgb, 6), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.Ushort555Rgb, 7), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.Ushort555Rgbx, 8), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.ByteGray, 9), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.UshortGray, 10), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceScale(SurfaceType.IntArgbPre, 1), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.IntRgb, 2), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.IntRgbx, 3), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.IntBgrx, 5), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.ThreeByteBgr, 11), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.Ushort565Rgb, 6), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgb, 7), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.Ushort555Rgbx, 8), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.ByteGray, 9), new com.frojasg1.sun.java2d.opengl.OGLSwToSurfaceTransform(SurfaceType.UshortGray, 10), var2, new com.frojasg1.sun.java2d.opengl.OGLGeneralTransformedBlit(var2), new com.frojasg1.sun.java2d.opengl.OGLTextureToSurfaceBlit(), new com.frojasg1.sun.java2d.opengl.OGLTextureToSurfaceScale(), new com.frojasg1.sun.java2d.opengl.OGLTextureToSurfaceTransform(), var1, new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.IntRgb, 2), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.IntRgbx, 3), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.IntBgr, 4), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.IntBgrx, 5), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.ThreeByteBgr, 11), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.Ushort565Rgb, 6), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.Ushort555Rgb, 7), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.Ushort555Rgbx, 8), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.ByteGray, 9), new com.frojasg1.sun.java2d.opengl.OGLSwToTextureBlit(SurfaceType.UshortGray, 10), new com.frojasg1.sun.java2d.opengl.OGLGeneralBlit(com.frojasg1.sun.java2d.opengl.OGLSurfaceData.OpenGLTexture, CompositeType.SrcNoEa, var1)};
      GraphicsPrimitiveMgr.register(var4);
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

      com.frojasg1.sun.java2d.opengl.OGLRenderQueue var21 = com.frojasg1.sun.java2d.opengl.OGLRenderQueue.getInstance();
      var21.lock();

      try {
         var21.addReference(var0);
         com.frojasg1.sun.java2d.opengl.OGLSurfaceData var22 = (com.frojasg1.sun.java2d.opengl.OGLSurfaceData)var1;
         if (var19) {
            com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig var23 = var22.getOGLGraphicsConfig();
            com.frojasg1.sun.java2d.opengl.OGLContext.setScratchSurface(var23);
         } else {
            com.frojasg1.sun.java2d.opengl.OGLContext.validateContext(var22, var22, var3, var2, var4, (Paint)null, (SunGraphics2D)null, var20);
         }

         int var27 = createPackedParams(false, var19, false, var4 != null, var5, var18);
         enqueueBlit(var21, var0, var1, var27, var6, var7, var8, var9, var10, var12, var14, var16);
         var21.flushNow();
      } finally {
         var21.unlock();
      }

   }

   static void IsoBlit(SurfaceData var0, SurfaceData var1, BufferedImage var2, BufferedImageOp var3, Composite var4, Region var5, AffineTransform var6, int var7, int var8, int var9, int var10, int var11, double var12, double var14, double var16, double var18, boolean var20) {
      int var21 = 0;
      if (var0.getTransparency() == 1) {
         var21 |= 1;
      }

      com.frojasg1.sun.java2d.opengl.OGLRenderQueue var22 = OGLRenderQueue.getInstance();
      var22.lock();

      try {
         com.frojasg1.sun.java2d.opengl.OGLSurfaceData var23 = (com.frojasg1.sun.java2d.opengl.OGLSurfaceData)var0;
         com.frojasg1.sun.java2d.opengl.OGLSurfaceData var24 = (com.frojasg1.sun.java2d.opengl.OGLSurfaceData)var1;
         int var25 = var23.getType();
         boolean var26;
         OGLSurfaceData var27;
         if (var25 == 3) {
            var26 = false;
            var27 = var24;
         } else {
            var26 = true;
            if (var25 == 5) {
               var27 = var24;
            } else {
               var27 = var23;
            }
         }

         OGLContext.validateContext(var27, var24, var5, var4, var6, (Paint)null, (SunGraphics2D)null, var21);
         if (var3 != null) {
            com.frojasg1.sun.java2d.opengl.OGLBufImgOps.enableBufImgOp(var22, var23, var2, var3);
         }

         int var28 = createPackedParams(true, var20, var26, var6 != null, var7, 0);
         enqueueBlit(var22, var0, var1, var28, var8, var9, var10, var11, var12, var14, var16, var18);
         if (var3 != null) {
            com.frojasg1.sun.java2d.opengl.OGLBufImgOps.disableBufImgOp(var22, var3);
         }

         if (var26 && var24.isOnScreen()) {
            var22.flushNow();
         }
      } finally {
         var22.unlock();
      }

   }
}
