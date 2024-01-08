package com.frojasg1.sun.java2d.d3d;

import java.awt.Color;
import java.awt.LinearGradientPaint;
import java.awt.MultipleGradientPaint;
import java.awt.TexturePaint;
import java.awt.MultipleGradientPaint.ColorSpaceType;
import java.awt.MultipleGradientPaint.CycleMethod;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;

abstract class D3DPaints {
   private static Map<Integer, D3DPaints> impls = new HashMap(4, 1.0F);

   D3DPaints() {
   }

   static boolean isValid(SunGraphics2D var0) {
      D3DPaints var1 = (D3DPaints)impls.get(var0.paintState);
      return var1 != null && var1.isPaintValid(var0);
   }

   abstract boolean isPaintValid(SunGraphics2D var1);

   static {
      impls.put(2, new D3DPaints.Gradient());
      impls.put(3, new D3DPaints.LinearGradient());
      impls.put(4, new D3DPaints.RadialGradient());
      impls.put(5, new D3DPaints.Texture());
   }

   private static class Gradient extends D3DPaints {
      private Gradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData var2 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1.surfaceData;
         com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var3 = (com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice)var2.getDeviceConfiguration().getDevice();
         return var3.isCapPresent(65536);
      }
   }

   private static class LinearGradient extends D3DPaints.MultiGradient {
      private LinearGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         LinearGradientPaint var2 = (LinearGradientPaint)var1.paint;
         if (var2.getFractions().length == 2 && var2.getCycleMethod() != CycleMethod.REPEAT && var2.getColorSpace() != ColorSpaceType.LINEAR_RGB) {
            com.frojasg1.sun.java2d.d3d.D3DSurfaceData var3 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1.surfaceData;
            com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var4 = (com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice)var3.getDeviceConfiguration().getDevice();
            if (var4.isCapPresent(65536)) {
               return true;
            }
         }

         return super.isPaintValid(var1);
      }
   }

   private abstract static class MultiGradient extends D3DPaints {
      public static final int MULTI_MAX_FRACTIONS_D3D = 8;

      protected MultiGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         MultipleGradientPaint var2 = (MultipleGradientPaint)var1.paint;
         if (var2.getFractions().length > 8) {
            return false;
         } else {
            com.frojasg1.sun.java2d.d3d.D3DSurfaceData var3 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1.surfaceData;
            com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var4 = (com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice)var3.getDeviceConfiguration().getDevice();
            return var4.isCapPresent(65536);
         }
      }
   }

   private static class RadialGradient extends D3DPaints.MultiGradient {
      private RadialGradient() {
      }
   }

   private static class Texture extends D3DPaints {
      private Texture() {
      }

      public boolean isPaintValid(SunGraphics2D var1) {
         TexturePaint var2 = (TexturePaint)var1.paint;
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData var3 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var1.surfaceData;
         BufferedImage var4 = var2.getImage();
         com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var5 = (D3DGraphicsDevice)var3.getDeviceConfiguration().getDevice();
         int var6 = var4.getWidth();
         int var7 = var4.getHeight();
         if (var5.isCapPresent(32) || (var6 & var6 - 1) == 0 && (var7 & var7 - 1) == 0) {
            if (!var5.isCapPresent(64) && var6 != var7) {
               return false;
            } else {
               SurfaceData var8 = var3.getSourceSurfaceData(var4, 0, CompositeType.SrcOver, (Color)null);
               if (!(var8 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData)) {
                  var8 = var3.getSourceSurfaceData(var4, 0, CompositeType.SrcOver, (Color)null);
                  if (!(var8 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData)) {
                     return false;
                  }
               }

               com.frojasg1.sun.java2d.d3d.D3DSurfaceData var9 = (D3DSurfaceData)var8;
               return var9.getType() == 3;
            }
         } else {
            return false;
         }
      }
   }
}
