package com.frojasg1.sun.java2d.d3d;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.image.ColorModel;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.SurfaceManager;
import com.frojasg1.sun.awt.image.VolatileSurfaceManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

public class D3DVolatileSurfaceManager extends VolatileSurfaceManager {
   private boolean accelerationEnabled;
   private int restoreCountdown;

   public D3DVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
      int var3 = var1.getTransparency();
      com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var4 = (D3DGraphicsDevice)var1.getGraphicsConfig().getDevice();
      this.accelerationEnabled = var3 == 1 || var3 == 3 && (var4.isCapPresent(2) || var4.isCapPresent(4));
   }

   protected boolean isAccelerationEnabled() {
      return this.accelerationEnabled;
   }

   public void setAccelerationEnabled(boolean var1) {
      this.accelerationEnabled = var1;
   }

   protected SurfaceData initAcceleratedSurface() {
      Component var2 = this.vImg.getComponent();
      WComponentPeer var3 = var2 != null ? (WComponentPeer)var2.getPeer() : null;

      com.frojasg1.sun.java2d.d3d.D3DSurfaceData var1;
      try {
         boolean var4 = false;
         if (this.context instanceof Boolean) {
            var4 = (Boolean)this.context;
         }

         if (var4) {
            var1 = com.frojasg1.sun.java2d.d3d.D3DSurfaceData.createData(var3, this.vImg);
         } else {
            com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var5 = (D3DGraphicsConfig)this.vImg.getGraphicsConfig();
            ColorModel var6 = var5.getColorModel(this.vImg.getTransparency());
            int var7 = this.vImg.getForcedAccelSurfaceType();
            if (var7 == 0) {
               var7 = 5;
            }

            var1 = com.frojasg1.sun.java2d.d3d.D3DSurfaceData.createData(var5, this.vImg.getWidth(), this.vImg.getHeight(), var6, this.vImg, var7);
         }
      } catch (NullPointerException var8) {
         var1 = null;
      } catch (OutOfMemoryError var9) {
         var1 = null;
      } catch (InvalidPipeException var10) {
         var1 = null;
      }

      return var1;
   }

   protected boolean isConfigValid(GraphicsConfiguration var1) {
      return var1 == null || var1 == this.vImg.getGraphicsConfig();
   }

   private synchronized void setRestoreCountdown(int var1) {
      this.restoreCountdown = var1;
   }

   protected void restoreAcceleratedSurface() {
      synchronized(this) {
         if (this.restoreCountdown > 0) {
            --this.restoreCountdown;
            throw new InvalidPipeException("Will attempt to restore surface  in " + this.restoreCountdown);
         }
      }

      SurfaceData var1 = this.initAcceleratedSurface();
      if (var1 != null) {
         this.sdAccel = var1;
      } else {
         throw new InvalidPipeException("could not restore surface");
      }
   }

   public SurfaceData restoreContents() {
      this.acceleratedSurfaceLost();
      return super.restoreContents();
   }

   static void handleVItoScreenOp(SurfaceData var0, SurfaceData var1) {
      if (var0 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData && var1 instanceof GDIWindowSurfaceData) {
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData var2 = (D3DSurfaceData)var0;
         SurfaceManager var3 = SurfaceManager.getManager((Image)var2.getDestination());
         if (var3 instanceof D3DVolatileSurfaceManager) {
            D3DVolatileSurfaceManager var4 = (D3DVolatileSurfaceManager)var3;
            if (var4 != null) {
               var2.setSurfaceLost(true);
               GDIWindowSurfaceData var5 = (GDIWindowSurfaceData)var1;
               WComponentPeer var6 = var5.getPeer();
               if (D3DScreenUpdateManager.canUseD3DOnScreen(var6, (Win32GraphicsConfig)var6.getGraphicsConfiguration(), var6.getBackBuffersNum())) {
                  var4.setRestoreCountdown(10);
               } else {
                  var4.setAccelerationEnabled(false);
               }
            }
         }
      }

   }

   public void initContents() {
      if (this.vImg.getForcedAccelSurfaceType() != 3) {
         super.initContents();
      }

   }
}
