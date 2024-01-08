package com.frojasg1.sun.java2d.opengl;

import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.image.ColorModel;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.VolatileSurfaceManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.WGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.hw.ExtendedBufferCapabilities;

public class WGLVolatileSurfaceManager extends VolatileSurfaceManager {
   private boolean accelerationEnabled;

   public WGLVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
      int var3 = var1.getTransparency();
      com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var4 = (com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig)var1.getGraphicsConfig();
      this.accelerationEnabled = var3 == 1 || var3 == 3 && (var4.isCapPresent(12) || var4.isCapPresent(2));
   }

   protected boolean isAccelerationEnabled() {
      return this.accelerationEnabled;
   }

   protected SurfaceData initAcceleratedSurface() {
      Component var2 = this.vImg.getComponent();
      WComponentPeer var3 = var2 != null ? (WComponentPeer)var2.getPeer() : null;

      com.frojasg1.sun.java2d.opengl.WGLSurfaceData.WGLOffScreenSurfaceData var1;
      try {
         boolean var4 = false;
         boolean var5 = false;
         if (this.context instanceof Boolean) {
            var5 = (Boolean)this.context;
            if (var5) {
               BufferCapabilities var6 = var3.getBackBufferCaps();
               if (var6 instanceof ExtendedBufferCapabilities) {
                  ExtendedBufferCapabilities var7 = (ExtendedBufferCapabilities)var6;
                  if (var7.getVSync() == ExtendedBufferCapabilities.VSyncType.VSYNC_ON && var7.getFlipContents() == FlipContents.COPIED) {
                     var4 = true;
                     var5 = false;
                  }
               }
            }
         }

         if (var5) {
            var1 = com.frojasg1.sun.java2d.opengl.WGLSurfaceData.createData(var3, this.vImg, 4);
         } else {
            com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var11 = (com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig)this.vImg.getGraphicsConfig();
            ColorModel var12 = var11.getColorModel(this.vImg.getTransparency());
            int var8 = this.vImg.getForcedAccelSurfaceType();
            if (var8 == 0) {
               var8 = var11.isCapPresent(12) ? 5 : 2;
            }

            if (var4) {
               var1 = com.frojasg1.sun.java2d.opengl.WGLSurfaceData.createData(var3, this.vImg, var8);
            } else {
               var1 = WGLSurfaceData.createData(var11, this.vImg.getWidth(), this.vImg.getHeight(), var12, this.vImg, var8);
            }
         }
      } catch (NullPointerException var9) {
         var1 = null;
      } catch (OutOfMemoryError var10) {
         var1 = null;
      }

      return var1;
   }

   protected boolean isConfigValid(GraphicsConfiguration var1) {
      return var1 == null || var1 instanceof WGLGraphicsConfig && var1 == this.vImg.getGraphicsConfig();
   }

   public void initContents() {
      if (this.vImg.getForcedAccelSurfaceType() != 3) {
         super.initContents();
      }

   }
}
