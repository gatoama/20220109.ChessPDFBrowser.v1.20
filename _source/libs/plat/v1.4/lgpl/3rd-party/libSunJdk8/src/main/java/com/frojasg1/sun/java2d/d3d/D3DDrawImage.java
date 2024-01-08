package com.frojasg1.sun.java2d.d3d;

import java.awt.Color;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DBufImgOps;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.loops.TransformBlit;
import com.frojasg1.sun.java2d.pipe.DrawImage;

public class D3DDrawImage extends DrawImage {
   public D3DDrawImage() {
   }

   protected void renderImageXform(SunGraphics2D var1, Image var2, AffineTransform var3, int var4, int var5, int var6, int var7, int var8, Color var9) {
      if (var4 != 3) {
         SurfaceData var10 = var1.surfaceData;
         SurfaceData var11 = var10.getSourceSurfaceData(var2, 4, var1.imageComp, var9);
         if (var11 != null && !isBgOperation(var11, var9)) {
            SurfaceType var12 = var11.getSurfaceType();
            SurfaceType var13 = var10.getSurfaceType();
            TransformBlit var14 = TransformBlit.getFromCache(var12, var1.imageComp, var13);
            if (var14 != null) {
               var14.Transform(var11, var10, var1.composite, var1.getCompClip(), var3, var4, var5, var6, 0, 0, var7 - var5, var8 - var6);
               return;
            }
         }
      }

      super.renderImageXform(var1, var2, var3, var4, var5, var6, var7, var8, var9);
   }

   public void transformImage(SunGraphics2D var1, BufferedImage var2, BufferedImageOp var3, int var4, int var5) {
      if (var3 != null) {
         if (var3 instanceof AffineTransformOp) {
            AffineTransformOp var6 = (AffineTransformOp)var3;
            this.transformImage(var1, var2, var4, var5, var6.getTransform(), var6.getInterpolationType());
            return;
         }

         if (com.frojasg1.sun.java2d.d3d.D3DBufImgOps.renderImageWithOp(var1, var2, var3, var4, var5)) {
            return;
         }

         var2 = var3.filter(var2, (BufferedImage)null);
      }

      this.copyImage(var1, var2, var4, var5, (Color)null);
   }
}
