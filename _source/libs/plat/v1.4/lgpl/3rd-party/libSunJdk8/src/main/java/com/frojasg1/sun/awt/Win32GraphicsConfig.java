package com.frojasg1.sun.awt;

import java.awt.AWTException;
import java.awt.BufferCapabilities;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.BufferCapabilities.FlipContents;
import java.awt.geom.AffineTransform;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.awt.image.WritableRaster;

import com.frojasg1.sun.awt.DisplayChangedListener;
import com.frojasg1.sun.awt.Win32GraphicsDevice;
import com.frojasg1.sun.awt.Win32GraphicsEnvironment;
import com.frojasg1.sun.awt.image.OffScreenImage;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.SurfaceManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.RenderLoops;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

public class Win32GraphicsConfig extends GraphicsConfiguration implements DisplayChangedListener, SurfaceManager.ProxiedGraphicsConfig {
   protected com.frojasg1.sun.awt.Win32GraphicsDevice screen;
   protected int visual;
   protected RenderLoops solidloops;
   private SurfaceType sTypeOrig = null;

   private static native void initIDs();

   public static Win32GraphicsConfig getConfig(com.frojasg1.sun.awt.Win32GraphicsDevice var0, int var1) {
      return new Win32GraphicsConfig(var0, var1);
   }

   /** @deprecated */
   @Deprecated
   public Win32GraphicsConfig(GraphicsDevice var1, int var2) {
      this.screen = (com.frojasg1.sun.awt.Win32GraphicsDevice)var1;
      this.visual = var2;
      ((Win32GraphicsDevice)var1).addDisplayChangedListener(this);
   }

   public GraphicsDevice getDevice() {
      return this.screen;
   }

   public int getVisual() {
      return this.visual;
   }

   public Object getProxyKey() {
      return this.screen;
   }

   public synchronized RenderLoops getSolidLoops(SurfaceType var1) {
      if (this.solidloops == null || this.sTypeOrig != var1) {
         this.solidloops = SurfaceData.makeRenderLoops(SurfaceType.OpaqueColor, CompositeType.SrcNoEa, var1);
         this.sTypeOrig = var1;
      }

      return this.solidloops;
   }

   public synchronized ColorModel getColorModel() {
      return this.screen.getColorModel();
   }

   public ColorModel getDeviceColorModel() {
      return this.screen.getDynamicColorModel();
   }

   public ColorModel getColorModel(int var1) {
      switch(var1) {
      case 1:
         return this.getColorModel();
      case 2:
         return new DirectColorModel(25, 16711680, 65280, 255, 16777216);
      case 3:
         return ColorModel.getRGBdefault();
      default:
         return null;
      }
   }

   public AffineTransform getDefaultTransform() {
      return new AffineTransform();
   }

   public AffineTransform getNormalizingTransform() {
      com.frojasg1.sun.awt.Win32GraphicsEnvironment var1 = (Win32GraphicsEnvironment)GraphicsEnvironment.getLocalGraphicsEnvironment();
      double var2 = (double)var1.getXResolution() / 72.0D;
      double var4 = (double)var1.getYResolution() / 72.0D;
      return new AffineTransform(var2, 0.0D, 0.0D, var4, 0.0D, 0.0D);
   }

   public String toString() {
      return super.toString() + "[dev=" + this.screen + ",pixfmt=" + this.visual + "]";
   }

   private native Rectangle getBounds(int var1);

   public Rectangle getBounds() {
      return this.getBounds(this.screen.getScreen());
   }

   public synchronized void displayChanged() {
      this.solidloops = null;
   }

   public void paletteChanged() {
   }

   public SurfaceData createSurfaceData(WComponentPeer var1, int var2) {
      return GDIWindowSurfaceData.createData(var1);
   }

   public Image createAcceleratedImage(Component var1, int var2, int var3) {
      ColorModel var4 = this.getColorModel(1);
      WritableRaster var5 = var4.createCompatibleWritableRaster(var2, var3);
      return new OffScreenImage(var1, var4, var5, var4.isAlphaPremultiplied());
   }

   public void assertOperationSupported(Component var1, int var2, BufferCapabilities var3) throws AWTException {
      throw new AWTException("The operation requested is not supported");
   }

   public VolatileImage createBackBuffer(WComponentPeer var1) {
      Component var2 = (Component)var1.getTarget();
      return new SunVolatileImage(var2, var2.getWidth(), var2.getHeight(), Boolean.TRUE);
   }

   public void flip(WComponentPeer var1, Component var2, VolatileImage var3, int var4, int var5, int var6, int var7, FlipContents var8) {
      Graphics var9;
      if (var8 != FlipContents.COPIED && var8 != FlipContents.UNDEFINED) {
         if (var8 == FlipContents.BACKGROUND) {
            var9 = var3.getGraphics();

            try {
               var9.setColor(var2.getBackground());
               var9.fillRect(0, 0, var3.getWidth(), var3.getHeight());
            } finally {
               var9.dispose();
            }
         }
      } else {
         var9 = var1.getGraphics();

         try {
            var9.drawImage(var3, var4, var5, var6, var7, var4, var5, var6, var7, (ImageObserver)null);
         } finally {
            var9.dispose();
         }
      }

   }

   public boolean isTranslucencyCapable() {
      return true;
   }

   static {
      initIDs();
   }
}
