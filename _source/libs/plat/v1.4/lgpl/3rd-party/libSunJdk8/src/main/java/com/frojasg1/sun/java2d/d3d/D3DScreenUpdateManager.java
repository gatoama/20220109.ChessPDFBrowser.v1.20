package com.frojasg1.sun.java2d.d3d;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Window;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.ScreenUpdateManager;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;
import com.frojasg1.sun.java2d.windows.WindowsFlags;
import com.frojasg1.sun.misc.ThreadGroupUtils;

public class D3DScreenUpdateManager extends ScreenUpdateManager implements Runnable {
   private static final int MIN_WIN_SIZE = 150;
   private volatile boolean done = false;
   private volatile Thread screenUpdater;
   private boolean needsUpdateNow;
   private Object runLock = new Object();
   private ArrayList<com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData> d3dwSurfaces;
   private HashMap<com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData, GDIWindowSurfaceData> gdiSurfaces;

   public D3DScreenUpdateManager() {
      AccessController.doPrivileged((PrivilegedAction) () -> {
         ThreadGroup var1 = ThreadGroupUtils.getRootThreadGroup();
         Thread var2 = new Thread(var1, () -> {
            this.done = true;
            this.wakeUpUpdateThread();
         });
         var2.setContextClassLoader((ClassLoader)null);

         try {
            Runtime.getRuntime().addShutdownHook(var2);
         } catch (Exception var4) {
            this.done = true;
         }

         return null;
      });
   }

   public SurfaceData createScreenSurface(Win32GraphicsConfig var1, WComponentPeer var2, int var3, boolean var4) {
      if (!this.done && var1 instanceof com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig) {
         Object var5 = null;
         if (canUseD3DOnScreen(var2, var1, var3)) {
            try {
               var5 = com.frojasg1.sun.java2d.d3d.D3DSurfaceData.createData(var2);
            } catch (InvalidPipeException var7) {
               var5 = null;
            }
         }

         if (var5 == null) {
            var5 = GDIWindowSurfaceData.createData(var2);
         }

         if (var4) {
            this.repaintPeerTarget(var2);
         }

         return (SurfaceData)var5;
      } else {
         return super.createScreenSurface(var1, var2, var3, var4);
      }
   }

   public static boolean canUseD3DOnScreen(WComponentPeer var0, Win32GraphicsConfig var1, int var2) {
      if (!(var1 instanceof com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig)) {
         return false;
      } else {
         com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig var3 = (D3DGraphicsConfig)var1;
         D3DGraphicsDevice var4 = var3.getD3DDevice();
         String var5 = var0.getClass().getName();
         Rectangle var6 = var0.getBounds();
         Component var7 = (Component)var0.getTarget();
         Window var8 = var4.getFullScreenWindow();
         return WindowsFlags.isD3DOnScreenEnabled() && var4.isD3DEnabledOnDevice() && var0.isAccelCapable() && (var6.width > 150 || var6.height > 150) && var2 == 0 && (var8 == null || var8 == var7 && !hasHWChildren(var7)) && (var5.equals("sun.awt.windows.WCanvasPeer") || var5.equals("sun.awt.windows.WDialogPeer") || var5.equals("sun.awt.windows.WPanelPeer") || var5.equals("sun.awt.windows.WWindowPeer") || var5.equals("sun.awt.windows.WFramePeer") || var5.equals("sun.awt.windows.WEmbeddedFramePeer"));
      }
   }

   public Graphics2D createGraphics(SurfaceData var1, WComponentPeer var2, Color var3, Color var4, Font var5) {
      if (!this.done && var1 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData) {
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData var6 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData)var1;
         if (!var6.isSurfaceLost() || this.validate(var6)) {
            this.trackScreenSurface(var6);
            return new SunGraphics2D(var1, var3, var4, var5);
         }

         var1 = this.getGdiSurface(var6);
      }

      return super.createGraphics(var1, var2, var3, var4, var5);
   }

   private void repaintPeerTarget(WComponentPeer var1) {
      Component var2 = (Component)var1.getTarget();
      Rectangle var3 = AWTAccessor.getComponentAccessor().getBounds(var2);
      var1.handlePaint(0, 0, var3.width, var3.height);
   }

   private void trackScreenSurface(SurfaceData var1) {
      if (!this.done && var1 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData) {
         synchronized(this) {
            if (this.d3dwSurfaces == null) {
               this.d3dwSurfaces = new ArrayList();
            }

            com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData var3 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData)var1;
            if (!this.d3dwSurfaces.contains(var3)) {
               this.d3dwSurfaces.add(var3);
            }
         }

         this.startUpdateThread();
      }

   }

   public synchronized void dropScreenSurface(SurfaceData var1) {
      if (this.d3dwSurfaces != null && var1 instanceof com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData) {
         com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData var2 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData)var1;
         this.removeGdiSurface(var2);
         this.d3dwSurfaces.remove(var2);
      }

   }

   public SurfaceData getReplacementScreenSurface(WComponentPeer var1, SurfaceData var2) {
      SurfaceData var3 = super.getReplacementScreenSurface(var1, var2);
      this.trackScreenSurface(var3);
      return var3;
   }

   private void removeGdiSurface(com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData var1) {
      if (this.gdiSurfaces != null) {
         GDIWindowSurfaceData var2 = (GDIWindowSurfaceData)this.gdiSurfaces.get(var1);
         if (var2 != null) {
            var2.invalidate();
            this.gdiSurfaces.remove(var1);
         }
      }

   }

   private synchronized void startUpdateThread() {
      if (this.screenUpdater == null) {
         this.screenUpdater = (Thread)AccessController.doPrivileged((PrivilegedAction)() -> {
            ThreadGroup var1 = ThreadGroupUtils.getRootThreadGroup();
            Thread var2 = new Thread(var1, this, "D3D Screen Updater");
            var2.setPriority(7);
            var2.setDaemon(true);
            return var2;
         });
         this.screenUpdater.start();
      } else {
         this.wakeUpUpdateThread();
      }

   }

   public void wakeUpUpdateThread() {
      synchronized(this.runLock) {
         this.runLock.notifyAll();
      }
   }

   public void runUpdateNow() {
      synchronized(this) {
         if (this.done || this.screenUpdater == null || this.d3dwSurfaces == null || this.d3dwSurfaces.size() == 0) {
            return;
         }
      }

      synchronized(this.runLock) {
         this.needsUpdateNow = true;
         this.runLock.notifyAll();

         while(this.needsUpdateNow) {
            try {
               this.runLock.wait();
            } catch (InterruptedException var4) {
            }
         }

      }
   }

   public void run() {
      while(!this.done) {
         synchronized(this.runLock) {
            long var2 = this.d3dwSurfaces.size() > 0 ? 100L : 0L;
            if (!this.needsUpdateNow) {
               try {
                  this.runLock.wait(var2);
               } catch (InterruptedException var18) {
               }
            }
         }

         com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData[] var1 = new com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData[0];
         synchronized(this) {
            var1 = (com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData[])this.d3dwSurfaces.toArray(var1);
         }

         com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData[] var20 = var1;
         int var3 = var1.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData var5 = var20[var4];
            if (var5.isValid() && (var5.isDirty() || var5.isSurfaceLost())) {
               if (!var5.isSurfaceLost()) {
                  com.frojasg1.sun.java2d.d3d.D3DRenderQueue var6 = D3DRenderQueue.getInstance();
                  var6.lock();

                  try {
                     Rectangle var7 = var5.getBounds();
                     com.frojasg1.sun.java2d.d3d.D3DSurfaceData.swapBuffers(var5, 0, 0, var7.width, var7.height);
                     var5.markClean();
                  } finally {
                     var6.unlock();
                  }
               } else if (!this.validate(var5)) {
                  var5.getPeer().replaceSurfaceDataLater();
               }
            }
         }

         synchronized(this.runLock) {
            this.needsUpdateNow = false;
            this.runLock.notifyAll();
         }
      }

   }

   private boolean validate(com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DWindowSurfaceData var1) {
      if (var1.isSurfaceLost()) {
         try {
            var1.restoreSurface();
            Color var2 = var1.getPeer().getBackgroundNoSync();
            SunGraphics2D var3 = new SunGraphics2D(var1, var2, var2, (Font)null);
            var3.fillRect(0, 0, var1.getBounds().width, var1.getBounds().height);
            var3.dispose();
            var1.markClean();
            this.repaintPeerTarget(var1.getPeer());
         } catch (InvalidPipeException var4) {
            return false;
         }
      }

      return true;
   }

   private synchronized SurfaceData getGdiSurface(D3DSurfaceData.D3DWindowSurfaceData var1) {
      if (this.gdiSurfaces == null) {
         this.gdiSurfaces = new HashMap();
      }

      GDIWindowSurfaceData var2 = (GDIWindowSurfaceData)this.gdiSurfaces.get(var1);
      if (var2 == null) {
         var2 = GDIWindowSurfaceData.createData(var1.getPeer());
         this.gdiSurfaces.put(var1, var2);
      }

      return var2;
   }

   private static boolean hasHWChildren(Component var0) {
      if (var0 instanceof Container) {
         Component[] var1 = ((Container)var0).getComponents();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Component var4 = var1[var3];
            if (var4.getPeer() instanceof WComponentPeer || hasHWChildren(var4)) {
               return true;
            }
         }
      }

      return false;
   }
}
