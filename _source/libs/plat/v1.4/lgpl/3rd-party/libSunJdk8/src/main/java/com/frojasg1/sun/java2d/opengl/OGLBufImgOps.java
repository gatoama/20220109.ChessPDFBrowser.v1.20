package com.frojasg1.sun.java2d.opengl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ConvolveOp;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.opengl.OGLBlitLoops;
import com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.BufferedBufImgOps;

class OGLBufImgOps extends BufferedBufImgOps {
   OGLBufImgOps() {
   }

   static boolean renderImageWithOp(SunGraphics2D var0, BufferedImage var1, BufferedImageOp var2, int var3, int var4) {
      if (var2 instanceof ConvolveOp) {
         if (!isConvolveOpValid((ConvolveOp)var2)) {
            return false;
         }
      } else if (var2 instanceof RescaleOp) {
         if (!isRescaleOpValid((RescaleOp)var2, var1)) {
            return false;
         }
      } else {
         if (!(var2 instanceof LookupOp)) {
            return false;
         }

         if (!isLookupOpValid((LookupOp)var2, var1)) {
            return false;
         }
      }

      SurfaceData var5 = var0.surfaceData;
      if (var5 instanceof com.frojasg1.sun.java2d.opengl.OGLSurfaceData && var0.interpolationType != 3 && var0.compositeState <= 1) {
         SurfaceData var6 = var5.getSourceSurfaceData(var1, 0, CompositeType.SrcOver, (Color)null);
         if (!(var6 instanceof com.frojasg1.sun.java2d.opengl.OGLSurfaceData)) {
            var6 = var5.getSourceSurfaceData(var1, 0, CompositeType.SrcOver, (Color)null);
            if (!(var6 instanceof com.frojasg1.sun.java2d.opengl.OGLSurfaceData)) {
               return false;
            }
         }

         com.frojasg1.sun.java2d.opengl.OGLSurfaceData var7 = (OGLSurfaceData)var6;
         com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig var8 = var7.getOGLGraphicsConfig();
         if (var7.getType() == 3 && var8.isCapPresent(262144)) {
            int var9 = var1.getWidth();
            int var10 = var1.getHeight();
            com.frojasg1.sun.java2d.opengl.OGLBlitLoops.IsoBlit(var6, var5, var1, var2, var0.composite, var0.getCompClip(), var0.transform, var0.interpolationType, 0, 0, var9, var10, (double)var3, (double)var4, (double)(var3 + var9), (double)(var4 + var10), true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
