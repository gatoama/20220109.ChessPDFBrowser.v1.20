package com.frojasg1.sun.java2d.d3d;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.ImageCapabilities;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.color.ColorSpace;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.SurfaceManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.Surface;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.pipe.hw.AccelDeviceEventListener;
import com.frojasg1.sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import com.frojasg1.sun.java2d.pipe.hw.AccelGraphicsConfig;
import com.frojasg1.sun.java2d.pipe.hw.AccelSurface;
import com.frojasg1.sun.java2d.pipe.hw.AccelTypedVolatileImage;
import com.frojasg1.sun.java2d.pipe.hw.ContextCapabilities;

public class D3DGraphicsConfig extends Win32GraphicsConfig implements AccelGraphicsConfig {
   private static ImageCapabilities imageCaps = new D3DGraphicsConfig.D3DImageCaps();
   private BufferCapabilities bufferCaps;
   private com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice device;

   protected D3DGraphicsConfig(com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var1) {
      super(var1, 0);
      this.device = var1;
   }

   public SurfaceData createManagedSurface(int var1, int var2, int var3) {
      return com.frojasg1.sun.java2d.d3d.D3DSurfaceData.createData(this, var1, var2, this.getColorModel(var3), (Image)null, 3);
   }

   public synchronized void displayChanged() {
      super.displayChanged();
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var1 = D3DRenderQueue.getInstance();
      var1.lock();

      try {
         com.frojasg1.sun.java2d.d3d.D3DContext.invalidateCurrentContext();
      } finally {
         var1.unlock();
      }

   }

   public ColorModel getColorModel(int var1) {
      switch(var1) {
      case 1:
         return new DirectColorModel(24, 16711680, 65280, 255);
      case 2:
         return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
         ColorSpace var2 = ColorSpace.getInstance(1000);
         return new DirectColorModel(var2, 32, 16711680, 65280, 255, -16777216, true, 3);
      default:
         return null;
      }
   }

   public String toString() {
      return "D3DGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
   }

   public SurfaceData createSurfaceData(WComponentPeer var1, int var2) {
      return super.createSurfaceData(var1, var2);
   }

   public void assertOperationSupported(Component var1, int var2, BufferCapabilities var3) throws AWTException {
      if (var2 >= 2 && var2 <= 4) {
         if (var3.getFlipContents() == FlipContents.COPIED && var2 != 2) {
            throw new AWTException("FlipContents.COPIED is onlysupported for 2 buffers");
         }
      } else {
         throw new AWTException("Only 2-4 buffers supported");
      }
   }

   public VolatileImage createBackBuffer(WComponentPeer var1) {
      Component var2 = (Component)var1.getTarget();
      int var3 = Math.max(1, var2.getWidth());
      int var4 = Math.max(1, var2.getHeight());
      return new SunVolatileImage(var2, var3, var4, Boolean.TRUE);
   }

   public void flip(WComponentPeer var1, Component var2, VolatileImage var3, int var4, int var5, int var6, int var7, FlipContents var8) {
      SurfaceManager var9 = SurfaceManager.getManager(var3);
      SurfaceData var10 = var9.getPrimarySurfaceData();
      Graphics var20;
      if (var10 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData) {
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData var11 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData)var10;
         D3DSurfaceData.swapBuffers(var11, var4, var5, var6, var7);
      } else {
         var20 = var1.getGraphics();

         try {
            var20.drawImage(var3, var4, var5, var6, var7, var4, var5, var6, var7, (ImageObserver)null);
         } finally {
            var20.dispose();
         }
      }

      if (var8 == FlipContents.BACKGROUND) {
         var20 = var3.getGraphics();

         try {
            var20.setColor(var2.getBackground());
            var20.fillRect(0, 0, var3.getWidth(), var3.getHeight());
         } finally {
            var20.dispose();
         }
      }

   }

   public BufferCapabilities getBufferCapabilities() {
      if (this.bufferCaps == null) {
         this.bufferCaps = new D3DGraphicsConfig.D3DBufferCaps();
      }

      return this.bufferCaps;
   }

   public ImageCapabilities getImageCapabilities() {
      return imageCaps;
   }

   D3DGraphicsDevice getD3DDevice() {
      return this.device;
   }

   public com.frojasg1.sun.java2d.d3d.D3DContext getContext() {
      return this.device.getContext();
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3, int var4) {
      if (var4 != 4 && var4 != 1 && var4 != 0 && var3 != 2) {
         boolean var5 = var3 == 1;
         if (var4 == 5) {
            int var6 = var5 ? 8 : 4;
            if (!this.device.isCapPresent(var6)) {
               return null;
            }
         } else if (var4 == 2 && !var5 && !this.device.isCapPresent(2)) {
            return null;
         }

         AccelTypedVolatileImage var8 = new AccelTypedVolatileImage(this, var1, var2, var3, var4);
         Surface var7 = var8.getDestSurface();
         if (!(var7 instanceof AccelSurface) || ((AccelSurface)var7).getType() != var4) {
            var8.flush();
            var8 = null;
         }

         return var8;
      } else {
         return null;
      }
   }

   public ContextCapabilities getContextCapabilities() {
      return this.device.getContextCapabilities();
   }

   public void addDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.addListener(var1, this.device.getScreen());
   }

   public void removeDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.removeListener(var1);
   }

   private static class D3DBufferCaps extends BufferCapabilities {
      public D3DBufferCaps() {
         super(D3DGraphicsConfig.imageCaps, D3DGraphicsConfig.imageCaps, FlipContents.UNDEFINED);
      }

      public boolean isMultiBufferAvailable() {
         return true;
      }
   }

   private static class D3DImageCaps extends ImageCapabilities {
      private D3DImageCaps() {
         super(true);
      }

      public boolean isTrueVolatile() {
         return true;
      }
   }
}
