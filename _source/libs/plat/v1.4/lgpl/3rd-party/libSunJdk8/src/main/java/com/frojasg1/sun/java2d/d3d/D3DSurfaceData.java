package com.frojasg1.sun.java2d.d3d;

import java.awt.AlphaComposite;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.image.DataBufferNative;
import com.frojasg1.sun.awt.image.PixelConverter;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.SurfaceManager;
import com.frojasg1.sun.awt.image.WritableRasterNative;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.ScreenUpdateManager;
import com.frojasg1.sun.java2d.StateTracker;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.SurfaceDataProxy;
import com.frojasg1.sun.java2d.d3d.D3DBlitLoops;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DDrawImage;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DMaskBlit;
import com.frojasg1.sun.java2d.d3d.D3DMaskFill;
import com.frojasg1.sun.java2d.d3d.D3DPaints;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DRenderer;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceDataProxy;
import com.frojasg1.sun.java2d.d3d.D3DTextRenderer;
import com.frojasg1.sun.java2d.d3d.D3DVolatileSurfaceManager;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.MaskFill;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.ParallelogramPipe;
import com.frojasg1.sun.java2d.pipe.PixelToParallelogramConverter;
import com.frojasg1.sun.java2d.pipe.RenderBuffer;
import com.frojasg1.sun.java2d.pipe.TextPipe;
import com.frojasg1.sun.java2d.pipe.hw.AccelSurface;
import com.frojasg1.sun.java2d.pipe.hw.ExtendedBufferCapabilities;

public class D3DSurfaceData extends SurfaceData implements AccelSurface {
   public static final int D3D_DEVICE_RESOURCE = 100;
   public static final int ST_INT_ARGB = 0;
   public static final int ST_INT_ARGB_PRE = 1;
   public static final int ST_INT_ARGB_BM = 2;
   public static final int ST_INT_RGB = 3;
   public static final int ST_INT_BGR = 4;
   public static final int ST_USHORT_565_RGB = 5;
   public static final int ST_USHORT_555_RGB = 6;
   public static final int ST_BYTE_INDEXED = 7;
   public static final int ST_BYTE_INDEXED_BM = 8;
   public static final int ST_3BYTE_BGR = 9;
   public static final int SWAP_DISCARD = 1;
   public static final int SWAP_FLIP = 2;
   public static final int SWAP_COPY = 3;
   private static final String DESC_D3D_SURFACE = "D3D Surface";
   private static final String DESC_D3D_SURFACE_RTT = "D3D Surface (render-to-texture)";
   private static final String DESC_D3D_TEXTURE = "D3D Texture";
   static final SurfaceType D3DSurface;
   static final SurfaceType D3DSurfaceRTT;
   static final SurfaceType D3DTexture;
   private int type;
   private int width;
   private int height;
   private int nativeWidth;
   private int nativeHeight;
   protected WComponentPeer peer;
   private Image offscreenImage;
   protected D3DGraphicsDevice graphicsDevice;
   private int swapEffect;
   private ExtendedBufferCapabilities.VSyncType syncType;
   private int backBuffersNum;
   private WritableRasterNative wrn;
   protected static com.frojasg1.sun.java2d.d3d.D3DRenderer d3dRenderPipe;
   protected static PixelToParallelogramConverter d3dTxRenderPipe;
   protected static ParallelogramPipe d3dAAPgramPipe;
   protected static com.frojasg1.sun.java2d.d3d.D3DTextRenderer d3dTextPipe;
   protected static com.frojasg1.sun.java2d.d3d.D3DDrawImage d3dImagePipe;

   private native boolean initTexture(long var1, boolean var3, boolean var4);

   private native boolean initFlipBackbuffer(long var1, long var3, int var5, int var6, int var7);

   private native boolean initRTSurface(long var1, boolean var3);

   private native void initOps(int var1, int var2, int var3);

   protected D3DSurfaceData(WComponentPeer var1, com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var2, int var3, int var4, Image var5, ColorModel var6, int var7, int var8, ExtendedBufferCapabilities.VSyncType var9, int var10) {
      super(getCustomSurfaceType(var10), var6);
      this.graphicsDevice = var2.getD3DDevice();
      this.peer = var1;
      this.type = var10;
      this.width = var3;
      this.height = var4;
      this.offscreenImage = var5;
      this.backBuffersNum = var7;
      this.swapEffect = var8;
      this.syncType = var9;
      this.initOps(this.graphicsDevice.getScreen(), var3, var4);
      if (var10 == 1) {
         this.setSurfaceLost(true);
      } else {
         this.initSurface();
      }

      this.setBlitProxyKey(var2.getProxyKey());
   }

   public SurfaceDataProxy makeProxyFor(SurfaceData var1) {
      return D3DSurfaceDataProxy.createProxy(var1, (com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig)this.graphicsDevice.getDefaultConfiguration());
   }

   public static D3DSurfaceData createData(WComponentPeer var0, Image var1) {
      com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var2 = getGC(var0);
      if (var2 != null && var0.isAccelCapable()) {
         BufferCapabilities var3 = var0.getBackBufferCaps();
         ExtendedBufferCapabilities.VSyncType var4 = ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT;
         if (var3 instanceof ExtendedBufferCapabilities) {
            var4 = ((ExtendedBufferCapabilities)var3).getVSync();
         }

         Rectangle var5 = var0.getBounds();
         FlipContents var6 = var3.getFlipContents();
         byte var7;
         if (var6 == FlipContents.COPIED) {
            var7 = 3;
         } else if (var6 == FlipContents.PRIOR) {
            var7 = 2;
         } else {
            var7 = 1;
         }

         return new D3DSurfaceData(var0, var2, var5.width, var5.height, var1, var0.getColorModel(), var0.getBackBuffersNum(), var7, var4, 4);
      } else {
         return null;
      }
   }

   public static D3DSurfaceData createData(WComponentPeer var0) {
      com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var1 = getGC(var0);
      return var1 != null && var0.isAccelCapable() ? new D3DSurfaceData.D3DWindowSurfaceData(var0, var1) : null;
   }

   public static D3DSurfaceData createData(com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var0, int var1, int var2, ColorModel var3, Image var4, int var5) {
      if (var5 == 5) {
         boolean var6 = var3.getTransparency() == 1;
         int var7 = var6 ? 8 : 4;
         if (!var0.getD3DDevice().isCapPresent(var7)) {
            var5 = 2;
         }
      }

      D3DSurfaceData var10 = null;

      try {
         var10 = new D3DSurfaceData((WComponentPeer)null, var0, var1, var2, var4, var3, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, var5);
      } catch (InvalidPipeException var8) {
         if (var5 == 5 && ((SunVolatileImage)var4).getForcedAccelSurfaceType() != 5) {
            byte var9 = 2;
            var10 = new D3DSurfaceData((WComponentPeer)null, var0, var1, var2, var4, var3, 0, 1, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, var9);
         }
      }

      return var10;
   }

   private static SurfaceType getCustomSurfaceType(int var0) {
      switch(var0) {
      case 3:
         return D3DTexture;
      case 5:
         return D3DSurfaceRTT;
      default:
         return D3DSurface;
      }
   }

   private boolean initSurfaceNow() {
      boolean var1 = this.getTransparency() == 1;
      switch(this.type) {
      case 1:
      case 4:
         return this.initFlipBackbuffer(this.getNativeOps(), this.peer.getData(), this.backBuffersNum, this.swapEffect, this.syncType.id());
      case 2:
         return this.initRTSurface(this.getNativeOps(), var1);
      case 3:
         return this.initTexture(this.getNativeOps(), false, var1);
      case 5:
         return this.initTexture(this.getNativeOps(), true, var1);
      default:
         return false;
      }
   }

   protected void initSurface() {
      synchronized(this) {
         this.wrn = null;
      }

      class Status {
         boolean success = false;

         Status() {
         }
      }

      final Status var1 = new Status();
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var2 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var2.lock();

      try {
         var2.flushAndInvokeNow(new Runnable() {
            public void run() {
               var1.success = D3DSurfaceData.this.initSurfaceNow();
            }
         });
         if (!var1.success) {
            throw new InvalidPipeException("Error creating D3DSurface");
         }
      } finally {
         var2.unlock();
      }

   }

   public final com.frojasg1.sun.java2d.d3d.D3DContext getContext() {
      return this.graphicsDevice.getContext();
   }

   public final int getType() {
      return this.type;
   }

   private static native int dbGetPixelNative(long var0, int var2, int var3);

   private static native void dbSetPixelNative(long var0, int var2, int var3, int var4);

   public synchronized Raster getRaster(int var1, int var2, int var3, int var4) {
      if (this.wrn == null) {
         DirectColorModel var5 = (DirectColorModel)this.getColorModel();
         boolean var7 = false;
         int var8 = this.width;
         byte var10;
         if (var5.getPixelSize() > 16) {
            var10 = 3;
         } else {
            var10 = 1;
         }

         SinglePixelPackedSampleModel var6 = new SinglePixelPackedSampleModel(var10, this.width, this.height, var8, var5.getMasks());
         D3DSurfaceData.D3DDataBufferNative var9 = new D3DSurfaceData.D3DDataBufferNative(this, var10, this.width, this.height);
         this.wrn = WritableRasterNative.createNativeRaster(var6, var9);
      }

      return this.wrn;
   }

   public boolean canRenderLCDText(SunGraphics2D var1) {
      return this.graphicsDevice.isCapPresent(65536) && var1.compositeState <= 0 && var1.paintState <= 0 && var1.surfaceData.getTransparency() == 1;
   }

   void disableAccelerationForSurface() {
      if (this.offscreenImage != null) {
         SurfaceManager var1 = SurfaceManager.getManager(this.offscreenImage);
         if (var1 instanceof com.frojasg1.sun.java2d.d3d.D3DVolatileSurfaceManager) {
            this.setSurfaceLost(true);
            ((D3DVolatileSurfaceManager)var1).setAccelerationEnabled(false);
         }
      }

   }

   public void validatePipe(SunGraphics2D var1) {
      boolean var3 = false;
      if (var1.compositeState >= 2) {
         super.validatePipe(var1);
         var1.imagepipe = d3dImagePipe;
         this.disableAccelerationForSurface();
      } else {
         Object var2;
         if ((var1.compositeState > 0 || var1.paintState > 1) && (var1.compositeState != 1 || var1.paintState > 1 || ((AlphaComposite)var1.composite).getRule() != 3) && (var1.compositeState != 2 || var1.paintState > 1)) {
            super.validatePipe(var1);
            var2 = var1.textpipe;
            var3 = true;
         } else {
            var2 = d3dTextPipe;
         }

         PixelToParallelogramConverter var4 = null;
         com.frojasg1.sun.java2d.d3d.D3DRenderer var5 = null;
         if (var1.antialiasHint != 2) {
            if (var1.paintState <= 1) {
               if (var1.compositeState <= 2) {
                  var4 = d3dTxRenderPipe;
                  var5 = d3dRenderPipe;
               }
            } else if (var1.compositeState <= 1 && com.frojasg1.sun.java2d.d3d.D3DPaints.isValid(var1)) {
               var4 = d3dTxRenderPipe;
               var5 = d3dRenderPipe;
            }
         } else if (var1.paintState <= 1) {
            if (!this.graphicsDevice.isCapPresent(524288) || var1.imageComp != CompositeType.SrcOverNoEa && var1.imageComp != CompositeType.SrcOver) {
               if (var1.compositeState == 2) {
                  var4 = d3dTxRenderPipe;
                  var5 = d3dRenderPipe;
               }
            } else {
               if (!var3) {
                  super.validatePipe(var1);
                  var3 = true;
               }

               PixelToParallelogramConverter var6 = new PixelToParallelogramConverter(var1.shapepipe, d3dAAPgramPipe, 0.125D, 0.499D, false);
               var1.drawpipe = var6;
               var1.fillpipe = var6;
               var1.shapepipe = var6;
            }
         }

         if (var4 != null) {
            if (var1.transformState >= 3) {
               var1.drawpipe = var4;
               var1.fillpipe = var4;
            } else if (var1.strokeState != 0) {
               var1.drawpipe = var4;
               var1.fillpipe = var5;
            } else {
               var1.drawpipe = var5;
               var1.fillpipe = var5;
            }

            var1.shapepipe = var4;
         } else if (!var3) {
            super.validatePipe(var1);
         }

         var1.textpipe = (TextPipe)var2;
         var1.imagepipe = d3dImagePipe;
      }
   }

   protected MaskFill getMaskFill(SunGraphics2D var1) {
      return var1.paintState <= 1 || com.frojasg1.sun.java2d.d3d.D3DPaints.isValid(var1) && this.graphicsDevice.isCapPresent(16) ? super.getMaskFill(var1) : null;
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      if (var1.transformState < 3 && var1.compositeState < 2) {
         var2 += var1.transX;
         var3 += var1.transY;
         d3dRenderPipe.copyArea(var1, var2, var3, var4, var5, var6, var7);
         return true;
      } else {
         return false;
      }
   }

   public void flush() {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var1 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var1.lock();

      try {
         RenderBuffer var2 = var1.getBuffer();
         var1.ensureCapacityAndAlignment(12, 4);
         var2.putInt(72);
         var2.putLong(this.getNativeOps());
         var1.flushNow();
      } finally {
         var1.unlock();
      }

   }

   static void dispose(long var0) {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var2 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var2.lock();

      try {
         RenderBuffer var3 = var2.getBuffer();
         var2.ensureCapacityAndAlignment(12, 4);
         var3.putInt(73);
         var3.putLong(var0);
         var2.flushNow();
      } finally {
         var2.unlock();
      }

   }

   static void swapBuffers(D3DSurfaceData var0, final int var1, final int var2, final int var3, final int var4) {
      long var5 = var0.getNativeOps();
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var7 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      if (com.frojasg1.sun.java2d.d3d.D3DRenderQueue.isRenderQueueThread()) {
         if (!var7.tryLock()) {
            final Component var8 = (Component)var0.getPeer().getTarget();
            SunToolkit.executeOnEventHandlerThread(var8, new Runnable() {
               public void run() {
                  var8.repaint(var1, var2, var3, var4);
               }
            });
            return;
         }
      } else {
         var7.lock();
      }

      try {
         RenderBuffer var12 = var7.getBuffer();
         var7.ensureCapacityAndAlignment(28, 4);
         var12.putInt(80);
         var12.putLong(var5);
         var12.putInt(var1);
         var12.putInt(var2);
         var12.putInt(var3);
         var12.putInt(var4);
         var7.flushNow();
      } finally {
         var7.unlock();
      }

   }

   public Object getDestination() {
      return this.offscreenImage;
   }

   public Rectangle getBounds() {
      if (this.type != 4 && this.type != 1) {
         return new Rectangle(this.width, this.height);
      } else {
         Rectangle var1 = this.peer.getBounds();
         var1.x = var1.y = 0;
         return var1;
      }
   }

   public Rectangle getNativeBounds() {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var1 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var1.lock();

      Rectangle var2;
      try {
         var2 = new Rectangle(this.nativeWidth, this.nativeHeight);
      } finally {
         var1.unlock();
      }

      return var2;
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsDevice.getDefaultConfiguration();
   }

   public SurfaceData getReplacement() {
      return restoreContents(this.offscreenImage);
   }

   private static com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig getGC(WComponentPeer var0) {
      GraphicsConfiguration var1;
      if (var0 != null) {
         var1 = var0.getGraphicsConfiguration();
      } else {
         GraphicsEnvironment var2 = GraphicsEnvironment.getLocalGraphicsEnvironment();
         GraphicsDevice var3 = var2.getDefaultScreenDevice();
         var1 = var3.getDefaultConfiguration();
      }

      return var1 instanceof com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig ? (com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig)var1 : null;
   }

   void restoreSurface() {
      this.initSurface();
   }

   WComponentPeer getPeer() {
      return this.peer;
   }

   public void setSurfaceLost(boolean var1) {
      super.setSurfaceLost(var1);
      if (var1 && this.offscreenImage != null) {
         SurfaceManager var2 = SurfaceManager.getManager(this.offscreenImage);
         var2.acceleratedSurfaceLost();
      }

   }

   private static native long getNativeResourceNative(long var0, int var2);

   public long getNativeResource(int var1) {
      return getNativeResourceNative(this.getNativeOps(), var1);
   }

   public static native boolean updateWindowAccelImpl(long var0, long var2, int var4, int var5);

   static {
      D3DSurface = SurfaceType.Any.deriveSubType("D3D Surface", PixelConverter.ArgbPre.instance);
      D3DSurfaceRTT = D3DSurface.deriveSubType("D3D Surface (render-to-texture)");
      D3DTexture = SurfaceType.Any.deriveSubType("D3D Texture");
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var0 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      d3dImagePipe = new D3DDrawImage();
      d3dTextPipe = new com.frojasg1.sun.java2d.d3d.D3DTextRenderer(var0);
      d3dRenderPipe = new com.frojasg1.sun.java2d.d3d.D3DRenderer(var0);
      if (GraphicsPrimitive.tracingEnabled()) {
         d3dTextPipe = d3dTextPipe.traceWrap();
         d3dRenderPipe = d3dRenderPipe.traceWrap();
      }

      d3dAAPgramPipe = d3dRenderPipe.getAAParallelogramPipe();
      d3dTxRenderPipe = new PixelToParallelogramConverter(d3dRenderPipe, d3dRenderPipe, 1.0D, 0.25D, true);
      com.frojasg1.sun.java2d.d3d.D3DBlitLoops.register();
      com.frojasg1.sun.java2d.d3d.D3DMaskFill.register();
      com.frojasg1.sun.java2d.d3d.D3DMaskBlit.register();
   }

   static class D3DDataBufferNative extends DataBufferNative {
      int pixel;

      protected D3DDataBufferNative(SurfaceData var1, int var2, int var3, int var4) {
         super(var1, var2, var3, var4);
      }

      protected int getElem(final int var1, final int var2, final SurfaceData var3) {
         if (var3.isSurfaceLost()) {
            return 0;
         } else {
            com.frojasg1.sun.java2d.d3d.D3DRenderQueue var5 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
            var5.lock();

            int var4;
            try {
               var5.flushAndInvokeNow(new Runnable() {
                  public void run() {
                     D3DDataBufferNative.this.pixel = D3DSurfaceData.dbGetPixelNative(var3.getNativeOps(), var1, var2);
                  }
               });
            } finally {
               var4 = this.pixel;
               var5.unlock();
            }

            return var4;
         }
      }

      protected void setElem(final int var1, final int var2, final int var3, final SurfaceData var4) {
         if (!var4.isSurfaceLost()) {
            com.frojasg1.sun.java2d.d3d.D3DRenderQueue var5 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
            var5.lock();

            try {
               var5.flushAndInvokeNow(new Runnable() {
                  public void run() {
                     D3DSurfaceData.dbSetPixelNative(var4.getNativeOps(), var1, var2, var3);
                  }
               });
               var4.markDirty();
            } finally {
               var5.unlock();
            }

         }
      }
   }

   public static class D3DWindowSurfaceData extends D3DSurfaceData {
      StateTracker dirtyTracker = this.getStateTracker();

      public D3DWindowSurfaceData(WComponentPeer var1, D3DGraphicsConfig var2) {
         super(var1, var2, var1.getBounds().width, var1.getBounds().height, (Image)null, var1.getColorModel(), 1, 3, ExtendedBufferCapabilities.VSyncType.VSYNC_DEFAULT, 1);
      }

      public SurfaceData getReplacement() {
         ScreenUpdateManager var1 = ScreenUpdateManager.getInstance();
         return var1.getReplacementScreenSurface(this.peer, this);
      }

      public Object getDestination() {
         return this.peer.getTarget();
      }

      void disableAccelerationForSurface() {
         this.setSurfaceLost(true);
         this.invalidate();
         this.flush();
         this.peer.disableAcceleration();
         ScreenUpdateManager.getInstance().dropScreenSurface(this);
      }

      void restoreSurface() {
         if (!this.peer.isAccelCapable()) {
            throw new InvalidPipeException("Onscreen acceleration disabled for this surface");
         } else {
            Window var1 = this.graphicsDevice.getFullScreenWindow();
            if (var1 != null && var1 != this.peer.getTarget()) {
               throw new InvalidPipeException("Can't restore onscreen surface when in full-screen mode");
            } else {
               super.restoreSurface();
               this.setSurfaceLost(false);
               com.frojasg1.sun.java2d.d3d.D3DRenderQueue var2 = D3DRenderQueue.getInstance();
               var2.lock();

               try {
                  this.getContext().invalidateContext();
               } finally {
                  var2.unlock();
               }

            }
         }
      }

      public boolean isDirty() {
         return !this.dirtyTracker.isCurrent();
      }

      public void markClean() {
         this.dirtyTracker = this.getStateTracker();
      }
   }
}
