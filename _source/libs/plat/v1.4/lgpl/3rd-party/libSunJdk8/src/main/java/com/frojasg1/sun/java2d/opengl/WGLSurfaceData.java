package com.frojasg1.sun.java2d.opengl;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.opengl.OGLSurfaceData;
import com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig;

public abstract class WGLSurfaceData extends OGLSurfaceData {
   protected WComponentPeer peer;
   private com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig graphicsConfig;

   private native void initOps(long var1, WComponentPeer var3, long var4);

   protected native boolean initPbuffer(long var1, long var3, boolean var5, int var6, int var7);

   protected WGLSurfaceData(WComponentPeer var1, com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var2, ColorModel var3, int var4) {
      super(var2, var3, var4);
      this.peer = var1;
      this.graphicsConfig = var2;
      long var5 = var2.getNativeConfigInfo();
      long var7 = var1 != null ? var1.getHWnd() : 0L;
      this.initOps(var5, var1, var7);
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsConfig;
   }

   public static WGLSurfaceData.WGLWindowSurfaceData createData(WComponentPeer var0) {
      if (var0.isAccelCapable() && SunToolkit.isContainingTopLevelOpaque((Component)var0.getTarget())) {
         com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var1 = getGC(var0);
         return new WGLSurfaceData.WGLWindowSurfaceData(var0, var1);
      } else {
         return null;
      }
   }

   public static WGLSurfaceData.WGLOffScreenSurfaceData createData(WComponentPeer var0, Image var1, int var2) {
      if (var0.isAccelCapable() && SunToolkit.isContainingTopLevelOpaque((Component)var0.getTarget())) {
         com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var3 = getGC(var0);
         Rectangle var4 = var0.getBounds();
         return (WGLSurfaceData.WGLOffScreenSurfaceData)(var2 == 4 ? new WGLSurfaceData.WGLOffScreenSurfaceData(var0, var3, var4.width, var4.height, var1, var0.getColorModel(), var2) : new WGLSurfaceData.WGLVSyncOffScreenSurfaceData(var0, var3, var4.width, var4.height, var1, var0.getColorModel(), var2));
      } else {
         return null;
      }
   }

   public static WGLSurfaceData.WGLOffScreenSurfaceData createData(com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var0, int var1, int var2, ColorModel var3, Image var4, int var5) {
      return new WGLSurfaceData.WGLOffScreenSurfaceData((WComponentPeer)null, var0, var1, var2, var4, var3, var5);
   }

   public static com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig getGC(WComponentPeer var0) {
      if (var0 != null) {
         return (com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig)var0.getGraphicsConfiguration();
      } else {
         GraphicsEnvironment var1 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var2 = var1.getDefaultScreenDevice();
         return (com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig)var2.getDefaultConfiguration();
      }
   }

   public static native boolean updateWindowAccelImpl(long var0, WComponentPeer var2, int var3, int var4);

   public static class WGLOffScreenSurfaceData extends WGLSurfaceData {
      private Image offscreenImage;
      private int width;
      private int height;

      public WGLOffScreenSurfaceData(WComponentPeer var1, com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7) {
         super(var1, var2, var6, var7);
         this.width = var3;
         this.height = var4;
         this.offscreenImage = var5;
         this.initSurface(var3, var4);
      }

      public SurfaceData getReplacement() {
         return restoreContents(this.offscreenImage);
      }

      public Rectangle getBounds() {
         if (this.type == 4) {
            Rectangle var1 = this.peer.getBounds();
            var1.x = var1.y = 0;
            return var1;
         } else {
            return new Rectangle(this.width, this.height);
         }
      }

      public Object getDestination() {
         return this.offscreenImage;
      }
   }

   public static class WGLVSyncOffScreenSurfaceData extends WGLSurfaceData.WGLOffScreenSurfaceData {
      private WGLSurfaceData.WGLOffScreenSurfaceData flipSurface;

      public WGLVSyncOffScreenSurfaceData(WComponentPeer var1, com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7) {
         super(var1, var2, var3, var4, var5, var6, var7);
         this.flipSurface = createData(var1, var5, 4);
      }

      public SurfaceData getFlipSurface() {
         return this.flipSurface;
      }

      public void flush() {
         this.flipSurface.flush();
         super.flush();
      }
   }

   public static class WGLWindowSurfaceData extends WGLSurfaceData {
      public WGLWindowSurfaceData(WComponentPeer var1, WGLGraphicsConfig var2) {
         super(var1, var2, var1.getColorModel(), 1);
      }

      public SurfaceData getReplacement() {
         return this.peer.getSurfaceData();
      }

      public Rectangle getBounds() {
         Rectangle var1 = this.peer.getBounds();
         var1.x = var1.y = 0;
         return var1;
      }

      public Object getDestination() {
         return this.peer.getTarget();
      }
   }
}
