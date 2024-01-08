package com.frojasg1.sun.awt.windows;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.VolatileImage;
import java.security.AccessController;
import com.frojasg1.sun.awt.image.BufImgSurfaceData;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.DestSurfaceProvider;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.Surface;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.opengl.WGLSurfaceData;
import com.frojasg1.sun.java2d.pipe.BufferedContext;
import com.frojasg1.sun.java2d.pipe.RenderQueue;
import com.frojasg1.sun.java2d.pipe.hw.AccelGraphicsConfig;
import com.frojasg1.sun.java2d.pipe.hw.AccelSurface;
import com.frojasg1.sun.security.action.GetPropertyAction;

abstract class TranslucentWindowPainter {
   protected Window window;
   protected com.frojasg1.sun.awt.windows.WWindowPeer peer;
   private static final boolean forceOpt = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forceopt", "false")));
   private static final boolean forceSW = Boolean.valueOf((String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.twp.forcesw", "false")));

   public static TranslucentWindowPainter createInstance(com.frojasg1.sun.awt.windows.WWindowPeer var0) {
      GraphicsConfiguration var1 = var0.getGraphicsConfiguration();
      if (!forceSW && var1 instanceof AccelGraphicsConfig) {
         String var2 = var1.getClass().getSimpleName();
         AccelGraphicsConfig var3 = (AccelGraphicsConfig)var1;
         if ((var3.getContextCapabilities().getCaps() & 256) != 0 || forceOpt) {
            if (var2.startsWith("D3D")) {
               return new TranslucentWindowPainter.VIOptD3DWindowPainter(var0);
            }

            if (forceOpt && var2.startsWith("WGL")) {
               return new TranslucentWindowPainter.VIOptWGLWindowPainter(var0);
            }
         }
      }

      return new TranslucentWindowPainter.BIWindowPainter(var0);
   }

   protected TranslucentWindowPainter(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
      this.peer = var1;
      this.window = (Window)var1.getTarget();
   }

   protected abstract Image getBackBuffer(boolean var1);

   protected abstract boolean update(Image var1);

   public abstract void flush();

   public void updateWindow(boolean var1) {
      boolean var2 = false;
      Image var3 = this.getBackBuffer(var1);

      while(!var2) {
         if (var1) {
            Graphics2D var4 = (Graphics2D)var3.getGraphics();

            try {
               this.window.paintAll(var4);
            } finally {
               var4.dispose();
            }
         }

         var2 = this.update(var3);
         if (!var2) {
            var1 = true;
            var3 = this.getBackBuffer(true);
         }
      }

   }

   private static final Image clearImage(Image var0) {
      Graphics2D var1 = (Graphics2D)var0.getGraphics();
      int var2 = var0.getWidth((ImageObserver)null);
      int var3 = var0.getHeight((ImageObserver)null);
      var1.setComposite(AlphaComposite.Src);
      var1.setColor(new Color(0, 0, 0, 0));
      var1.fillRect(0, 0, var2, var3);
      return var0;
   }

   private static class BIWindowPainter extends TranslucentWindowPainter {
      private BufferedImage backBuffer;

      protected BIWindowPainter(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
         super(var1);
      }

      protected Image getBackBuffer(boolean var1) {
         int var2 = this.window.getWidth();
         int var3 = this.window.getHeight();
         if (this.backBuffer == null || this.backBuffer.getWidth() != var2 || this.backBuffer.getHeight() != var3) {
            this.flush();
            this.backBuffer = new BufferedImage(var2, var3, 3);
         }

         return var1 ? (BufferedImage)TranslucentWindowPainter.clearImage(this.backBuffer) : this.backBuffer;
      }

      protected boolean update(Image var1) {
         VolatileImage var2 = null;
         int[] var4;
         BufferedImage var8;
         if (var1 instanceof BufferedImage) {
            var8 = (BufferedImage)var1;
            var4 = ((DataBufferInt)var8.getRaster().getDataBuffer()).getData();
            this.peer.updateWindowImpl(var4, var8.getWidth(), var8.getHeight());
            return true;
         } else {
            if (var1 instanceof VolatileImage) {
               var2 = (VolatileImage)var1;
               if (var1 instanceof DestSurfaceProvider) {
                  Surface var3 = ((DestSurfaceProvider)var1).getDestSurface();
                  if (var3 instanceof BufImgSurfaceData) {
                     int var9 = var2.getWidth();
                     int var5 = var2.getHeight();
                     BufImgSurfaceData var6 = (BufImgSurfaceData)var3;
                     int[] var7 = ((DataBufferInt)var6.getRaster(0, 0, var9, var5).getDataBuffer()).getData();
                     this.peer.updateWindowImpl(var7, var9, var5);
                     return true;
                  }
               }
            }

            var8 = (BufferedImage)TranslucentWindowPainter.clearImage(this.backBuffer);
            var4 = ((DataBufferInt)var8.getRaster().getDataBuffer()).getData();
            this.peer.updateWindowImpl(var4, var8.getWidth(), var8.getHeight());
            return var2 != null ? !var2.contentsLost() : true;
         }
      }

      public void flush() {
         if (this.backBuffer != null) {
            this.backBuffer.flush();
            this.backBuffer = null;
         }

      }
   }

   private static class VIOptD3DWindowPainter extends TranslucentWindowPainter.VIOptWindowPainter {
      protected VIOptD3DWindowPainter(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
         super(var1);
      }

      protected boolean updateWindowAccel(long var1, int var3, int var4) {
         return D3DSurfaceData.updateWindowAccelImpl(var1, this.peer.getData(), var3, var4);
      }
   }

   private static class VIOptWGLWindowPainter extends TranslucentWindowPainter.VIOptWindowPainter {
      protected VIOptWGLWindowPainter(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
         super(var1);
      }

      protected boolean updateWindowAccel(long var1, int var3, int var4) {
         return WGLSurfaceData.updateWindowAccelImpl(var1, this.peer, var3, var4);
      }
   }

   private abstract static class VIOptWindowPainter extends TranslucentWindowPainter.VIWindowPainter {
      protected VIOptWindowPainter(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
         super(var1);
      }

      protected abstract boolean updateWindowAccel(long var1, int var3, int var4);

      protected boolean update(Image var1) {
         if (var1 instanceof DestSurfaceProvider) {
            Surface var2 = ((DestSurfaceProvider)var1).getDestSurface();
            if (var2 instanceof AccelSurface) {
               final int var3 = var1.getWidth((ImageObserver)null);
               final int var4 = var1.getHeight((ImageObserver)null);
               final boolean[] var5 = new boolean[]{false};
               final AccelSurface var6 = (AccelSurface)var2;
               RenderQueue var7 = var6.getContext().getRenderQueue();
               var7.lock();

               try {
                  BufferedContext.validateContext(var6);
                  var7.flushAndInvokeNow(new Runnable() {
                     public void run() {
                        long var1 = var6.getNativeOps();
                        var5[0] = com.frojasg1.sun.awt.windows.TranslucentWindowPainter.VIOptWindowPainter.this.updateWindowAccel(var1, var3, var4);
                     }
                  });
               } catch (InvalidPipeException var12) {
               } finally {
                  var7.unlock();
               }

               return var5[0];
            }
         }

         return super.update(var1);
      }
   }

   private static class VIWindowPainter extends TranslucentWindowPainter.BIWindowPainter {
      private VolatileImage viBB;

      protected VIWindowPainter(WWindowPeer var1) {
         super(var1);
      }

      protected Image getBackBuffer(boolean var1) {
         int var2 = this.window.getWidth();
         int var3 = this.window.getHeight();
         GraphicsConfiguration var4 = this.peer.getGraphicsConfiguration();
         if (this.viBB == null || this.viBB.getWidth() != var2 || this.viBB.getHeight() != var3 || this.viBB.validate(var4) == 2) {
            this.flush();
            if (var4 instanceof AccelGraphicsConfig) {
               AccelGraphicsConfig var5 = (AccelGraphicsConfig)var4;
               this.viBB = var5.createCompatibleVolatileImage(var2, var3, 3, 2);
            }

            if (this.viBB == null) {
               this.viBB = var4.createCompatibleVolatileImage(var2, var3, 3);
            }

            this.viBB.validate(var4);
         }

         return (Image)(var1 ? TranslucentWindowPainter.clearImage(this.viBB) : this.viBB);
      }

      public void flush() {
         if (this.viBB != null) {
            this.viBB.flush();
            this.viBB = null;
         }

      }
   }
}
