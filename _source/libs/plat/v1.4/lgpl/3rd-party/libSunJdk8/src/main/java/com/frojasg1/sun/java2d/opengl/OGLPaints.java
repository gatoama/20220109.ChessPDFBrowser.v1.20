package com.frojasg1.sun.java2d.opengl;

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
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;

abstract class OGLPaints {
   private static Map<Integer, OGLPaints> impls = new HashMap(4, 1.0F);

   OGLPaints() {
   }

   static boolean isValid(SunGraphics2D var0) {
      OGLPaints var1 = (OGLPaints)impls.get(var0.paintState);
      return var1 != null && var1.isPaintValid(var0);
   }

   abstract boolean isPaintValid(SunGraphics2D var1);

   static {
      impls.put(2, new OGLPaints.Gradient());
      impls.put(3, new OGLPaints.LinearGradient());
      impls.put(4, new OGLPaints.RadialGradient());
      impls.put(5, new OGLPaints.Texture());
   }

   private static class Gradient extends OGLPaints {
      private Gradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         return true;
      }
   }

   private static class LinearGradient extends OGLPaints.MultiGradient {
      private LinearGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         LinearGradientPaint var2 = (LinearGradientPaint)var1.paint;
         return var2.getFractions().length == 2 && var2.getCycleMethod() != CycleMethod.REPEAT && var2.getColorSpace() != ColorSpaceType.LINEAR_RGB ? true : super.isPaintValid(var1);
      }
   }

   private abstract static class MultiGradient extends OGLPaints {
      protected MultiGradient() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         MultipleGradientPaint var2 = (MultipleGradientPaint)var1.paint;
         if (var2.getFractions().length > 12) {
            return false;
         } else {
            com.frojasg1.sun.java2d.opengl.OGLSurfaceData var3 = (com.frojasg1.sun.java2d.opengl.OGLSurfaceData)var1.surfaceData;
            com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig var4 = var3.getOGLGraphicsConfig();
            return var4.isCapPresent(524288);
         }
      }
   }

   private static class RadialGradient extends OGLPaints.MultiGradient {
      private RadialGradient() {
      }
   }

   private static class Texture extends OGLPaints {
      private Texture() {
      }

      boolean isPaintValid(SunGraphics2D var1) {
         TexturePaint var2 = (TexturePaint)var1.paint;
         com.frojasg1.sun.java2d.opengl.OGLSurfaceData var3 = (com.frojasg1.sun.java2d.opengl.OGLSurfaceData)var1.surfaceData;
         BufferedImage var4 = var2.getImage();
         if (!var3.isTexNonPow2Available()) {
            int var5 = var4.getWidth();
            int var6 = var4.getHeight();
            if ((var5 & var5 - 1) != 0 || (var6 & var6 - 1) != 0) {
               return false;
            }
         }

         SurfaceData var7 = var3.getSourceSurfaceData(var4, 0, CompositeType.SrcOver, (Color)null);
         if (!(var7 instanceof com.frojasg1.sun.java2d.opengl.OGLSurfaceData)) {
            var7 = var3.getSourceSurfaceData(var4, 0, CompositeType.SrcOver, (Color)null);
            if (!(var7 instanceof com.frojasg1.sun.java2d.opengl.OGLSurfaceData)) {
               return false;
            }
         }

         com.frojasg1.sun.java2d.opengl.OGLSurfaceData var8 = (OGLSurfaceData)var7;
         return var8.getType() == 3;
      }
   }
}
