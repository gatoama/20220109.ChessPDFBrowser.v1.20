package com.frojasg1.sun.java2d.opengl;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
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
import com.frojasg1.sun.awt.Win32GraphicsDevice;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.SurfaceManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.Disposer;
import com.frojasg1.sun.java2d.DisposerRecord;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.Surface;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.opengl.OGLContext;
import com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.OGLRenderQueue;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.opengl.WGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.hw.AccelDeviceEventListener;
import com.frojasg1.sun.java2d.pipe.hw.AccelDeviceEventNotifier;
import com.frojasg1.sun.java2d.pipe.hw.AccelSurface;
import com.frojasg1.sun.java2d.pipe.hw.AccelTypedVolatileImage;
import com.frojasg1.sun.java2d.pipe.hw.ContextCapabilities;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

public class WGLGraphicsConfig extends Win32GraphicsConfig implements com.frojasg1.sun.java2d.opengl.OGLGraphicsConfig {
   protected static boolean wglAvailable = initWGL();
   private static ImageCapabilities imageCaps = new WGLGraphicsConfig.WGLImageCaps();
   private BufferCapabilities bufferCaps;
   private long pConfigInfo;
   private ContextCapabilities oglCaps;
   private com.frojasg1.sun.java2d.opengl.OGLContext context;
   private Object disposerReferent = new Object();

   public static native int getDefaultPixFmt(int var0);

   private static native boolean initWGL();

   private static native long getWGLConfigInfo(int var0, int var1);

   private static native int getOGLCapabilities(long var0);

   protected WGLGraphicsConfig(Win32GraphicsDevice var1, int var2, long var3, ContextCapabilities var5) {
      super(var1, var2);
      this.pConfigInfo = var3;
      this.oglCaps = var5;
      this.context = new com.frojasg1.sun.java2d.opengl.OGLContext(com.frojasg1.sun.java2d.opengl.OGLRenderQueue.getInstance(), this);
      Disposer.addRecord(this.disposerReferent, new WGLGraphicsConfig.WGLGCDisposerRecord(this.pConfigInfo, var1.getScreen()));
   }

   public Object getProxyKey() {
      return this;
   }

   public SurfaceData createManagedSurface(int var1, int var2, int var3) {
      return com.frojasg1.sun.java2d.opengl.WGLSurfaceData.createData(this, var1, var2, this.getColorModel(var3), (Image)null, 3);
   }

   public static WGLGraphicsConfig getConfig(Win32GraphicsDevice var0, int var1) {
      if (!wglAvailable) {
         return null;
      } else {
         long var2 = 0L;
         final String[] var4 = new String[1];
         com.frojasg1.sun.java2d.opengl.OGLRenderQueue var5 = com.frojasg1.sun.java2d.opengl.OGLRenderQueue.getInstance();
         var5.lock();

         try {
            com.frojasg1.sun.java2d.opengl.OGLContext.invalidateCurrentContext();
            WGLGraphicsConfig.WGLGetConfigInfo var6 = new WGLGraphicsConfig.WGLGetConfigInfo(var0.getScreen(), var1);
            var5.flushAndInvokeNow(var6);
            var2 = var6.getConfigInfo();
            if (var2 != 0L) {
               com.frojasg1.sun.java2d.opengl.OGLContext.setScratchSurface(var2);
               var5.flushAndInvokeNow(new Runnable() {
                  public void run() {
                     var4[0] = com.frojasg1.sun.java2d.opengl.OGLContext.getOGLIdString();
                  }
               });
            }
         } finally {
            var5.unlock();
         }

         if (var2 == 0L) {
            return null;
         } else {
            int var10 = getOGLCapabilities(var2);
            com.frojasg1.sun.java2d.opengl.OGLContext.OGLContextCaps var7 = new com.frojasg1.sun.java2d.opengl.OGLContext.OGLContextCaps(var10, var4[0]);
            return new WGLGraphicsConfig(var0, var1, var2, var7);
         }
      }
   }

   public static boolean isWGLAvailable() {
      return wglAvailable;
   }

   public final boolean isCapPresent(int var1) {
      return (this.oglCaps.getCaps() & var1) != 0;
   }

   public final long getNativeConfigInfo() {
      return this.pConfigInfo;
   }

   public final com.frojasg1.sun.java2d.opengl.OGLContext getContext() {
      return this.context;
   }

   public synchronized void displayChanged() {
      super.displayChanged();
      com.frojasg1.sun.java2d.opengl.OGLRenderQueue var1 = com.frojasg1.sun.java2d.opengl.OGLRenderQueue.getInstance();
      var1.lock();

      try {
         OGLContext.invalidateCurrentContext();
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
      return "WGLGraphicsConfig[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
   }

   public SurfaceData createSurfaceData(WComponentPeer var1, int var2) {
      Object var3 = com.frojasg1.sun.java2d.opengl.WGLSurfaceData.createData(var1);
      if (var3 == null) {
         var3 = GDIWindowSurfaceData.createData(var1);
      }

      return (SurfaceData)var3;
   }

   public void assertOperationSupported(Component var1, int var2, BufferCapabilities var3) throws AWTException {
      if (var2 > 2) {
         throw new AWTException("Only double or single buffering is supported");
      } else {
         BufferCapabilities var4 = this.getBufferCapabilities();
         if (!var4.isPageFlipping()) {
            throw new AWTException("Page flipping is not supported");
         } else if (var3.getFlipContents() == FlipContents.PRIOR) {
            throw new AWTException("FlipContents.PRIOR is not supported");
         }
      }
   }

   public VolatileImage createBackBuffer(WComponentPeer var1) {
      Component var2 = (Component)var1.getTarget();
      int var3 = Math.max(1, var2.getWidth());
      int var4 = Math.max(1, var2.getHeight());
      return new SunVolatileImage(var2, var3, var4, Boolean.TRUE);
   }

   public void flip(WComponentPeer var1, Component var2, VolatileImage var3, int var4, int var5, int var6, int var7, FlipContents var8) {
      if (var8 == FlipContents.COPIED) {
         SurfaceManager var9 = SurfaceManager.getManager(var3);
         SurfaceData var10 = var9.getPrimarySurfaceData();
         if (!(var10 instanceof com.frojasg1.sun.java2d.opengl.WGLSurfaceData.WGLVSyncOffScreenSurfaceData)) {
            Graphics var29 = var1.getGraphics();

            try {
               var29.drawImage(var3, var4, var5, var6, var7, var4, var5, var6, var7, (ImageObserver)null);
            } finally {
               var29.dispose();
            }

            return;
         }

         com.frojasg1.sun.java2d.opengl.WGLSurfaceData.WGLVSyncOffScreenSurfaceData var11 = (WGLSurfaceData.WGLVSyncOffScreenSurfaceData)var10;
         SurfaceData var12 = var11.getFlipSurface();
         SunGraphics2D var13 = new SunGraphics2D(var12, Color.black, Color.white, (Font)null);

         try {
            var13.drawImage(var3, 0, 0, (ImageObserver)null);
         } finally {
            var13.dispose();
         }
      } else if (var8 == FlipContents.PRIOR) {
         return;
      }

      OGLSurfaceData.swapBuffers(var1.getData());
      if (var8 == FlipContents.BACKGROUND) {
         Graphics var30 = var3.getGraphics();

         try {
            var30.setColor(var2.getBackground());
            var30.fillRect(0, 0, var3.getWidth(), var3.getHeight());
         } finally {
            var30.dispose();
         }
      }

   }

   public BufferCapabilities getBufferCapabilities() {
      if (this.bufferCaps == null) {
         boolean var1 = this.isCapPresent(65536);
         this.bufferCaps = new WGLGraphicsConfig.WGLBufferCaps(var1);
      }

      return this.bufferCaps;
   }

   public ImageCapabilities getImageCapabilities() {
      return imageCaps;
   }

   public VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3, int var4) {
      if (var4 != 4 && var4 != 1 && var4 != 0 && var3 != 2) {
         if (var4 == 5) {
            if (!this.isCapPresent(12)) {
               return null;
            }
         } else if (var4 == 2) {
            boolean var5 = var3 == 1;
            if (!var5 && !this.isCapPresent(2)) {
               return null;
            }
         }

         AccelTypedVolatileImage var7 = new AccelTypedVolatileImage(this, var1, var2, var3, var4);
         Surface var6 = var7.getDestSurface();
         if (!(var6 instanceof AccelSurface) || ((AccelSurface)var6).getType() != var4) {
            var7.flush();
            var7 = null;
         }

         return var7;
      } else {
         return null;
      }
   }

   public ContextCapabilities getContextCapabilities() {
      return this.oglCaps;
   }

   public void addDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.addListener(var1, this.screen.getScreen());
   }

   public void removeDeviceEventListener(AccelDeviceEventListener var1) {
      AccelDeviceEventNotifier.removeListener(var1);
   }

   private static class WGLBufferCaps extends BufferCapabilities {
      public WGLBufferCaps(boolean var1) {
         super(WGLGraphicsConfig.imageCaps, WGLGraphicsConfig.imageCaps, var1 ? FlipContents.UNDEFINED : null);
      }
   }

   private static class WGLGCDisposerRecord implements DisposerRecord {
      private long pCfgInfo;
      private int screen;

      public WGLGCDisposerRecord(long var1, int var3) {
         this.pCfgInfo = var1;
      }

      public void dispose() {
         com.frojasg1.sun.java2d.opengl.OGLRenderQueue var1 = com.frojasg1.sun.java2d.opengl.OGLRenderQueue.getInstance();
         var1.lock();

         try {
            var1.flushAndInvokeNow(new Runnable() {
               public void run() {
                  AccelDeviceEventNotifier.eventOccured(WGLGCDisposerRecord.this.screen, 0);
                  AccelDeviceEventNotifier.eventOccured(WGLGCDisposerRecord.this.screen, 1);
               }
            });
         } finally {
            var1.unlock();
         }

         if (this.pCfgInfo != 0L) {
            OGLRenderQueue.disposeGraphicsConfig(this.pCfgInfo);
            this.pCfgInfo = 0L;
         }

      }
   }

   private static class WGLGetConfigInfo implements Runnable {
      private int screen;
      private int pixfmt;
      private long cfginfo;

      private WGLGetConfigInfo(int var1, int var2) {
         this.screen = var1;
         this.pixfmt = var2;
      }

      public void run() {
         this.cfginfo = WGLGraphicsConfig.getWGLConfigInfo(this.screen, this.pixfmt);
      }

      public long getConfigInfo() {
         return this.cfginfo;
      }
   }

   private static class WGLImageCaps extends ImageCapabilities {
      private WGLImageCaps() {
         super(true);
      }

      public boolean isTrueVolatile() {
         return true;
      }
   }
}
