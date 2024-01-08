package com.frojasg1.sun.java2d.d3d;

import java.awt.Dialog;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.WindowPeer;
import java.util.ArrayList;
import com.frojasg1.sun.awt.Win32GraphicsDevice;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.pipe.hw.ContextCapabilities;
import com.frojasg1.sun.java2d.windows.WindowsFlags;
import com.frojasg1.sun.misc.PerfCounter;

public class D3DGraphicsDevice extends Win32GraphicsDevice {
   private com.frojasg1.sun.java2d.d3d.D3DContext context;
   private static boolean d3dAvailable;
   private ContextCapabilities d3dCaps;
   private boolean fsStatus;
   private Rectangle ownerOrigBounds = null;
   private boolean ownerWasVisible;
   private Window realFSWindow;
   private WindowListener fsWindowListener;
   private boolean fsWindowWasAlwaysOnTop;

   private static native boolean initD3D();

   public static D3DGraphicsDevice createDevice(int var0) {
      if (!d3dAvailable) {
         return null;
      } else {
         ContextCapabilities var1 = getDeviceCaps(var0);
         if ((var1.getCaps() & 262144) == 0) {
            if (WindowsFlags.isD3DVerbose()) {
               System.out.println("Could not enable Direct3D pipeline on screen " + var0);
            }

            return null;
         } else {
            if (WindowsFlags.isD3DVerbose()) {
               System.out.println("Direct3D pipeline enabled on screen " + var0);
            }

            D3DGraphicsDevice var2 = new D3DGraphicsDevice(var0, var1);
            return var2;
         }
      }
   }

   private static native int getDeviceCapsNative(int var0);

   private static native String getDeviceIdNative(int var0);

   private static ContextCapabilities getDeviceCaps(final int var0) {
      com.frojasg1.sun.java2d.d3d.D3DContext.D3DContextCaps var1 = null;
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var2 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var2.lock();

      try {
         class Result {
            int caps;
            String id;

            Result() {
            }
         }

         final Result var3 = new Result();
         var2.flushAndInvokeNow(new Runnable() {
            public void run() {
               var3.caps = D3DGraphicsDevice.getDeviceCapsNative(var0);
               var3.id = D3DGraphicsDevice.getDeviceIdNative(var0);
            }
         });
         var1 = new com.frojasg1.sun.java2d.d3d.D3DContext.D3DContextCaps(var3.caps, var3.id);
      } finally {
         var2.unlock();
      }

      return var1 != null ? var1 : new com.frojasg1.sun.java2d.d3d.D3DContext.D3DContextCaps(0, (String)null);
   }

   public final boolean isCapPresent(int var1) {
      return (this.d3dCaps.getCaps() & var1) != 0;
   }

   private D3DGraphicsDevice(int var1, ContextCapabilities var2) {
      super(var1);
      this.descString = "D3DGraphicsDevice[screen=" + var1;
      this.d3dCaps = var2;
      this.context = new com.frojasg1.sun.java2d.d3d.D3DContext(com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance(), this);
   }

   public boolean isD3DEnabledOnDevice() {
      return this.isValid() && this.isCapPresent(262144);
   }

   public static boolean isD3DAvailable() {
      return d3dAvailable;
   }

   private Frame getToplevelOwner(Window var1) {
      Window var2 = var1;

      do {
         if (var2 == null) {
            return null;
         }

         var2 = var2.getOwner();
      } while(!(var2 instanceof Frame));

      return (Frame)var2;
   }

   private static native boolean enterFullScreenExclusiveNative(int var0, long var1);

   protected void enterFullScreenExclusive(final int var1, WindowPeer var2) {
      final WWindowPeer var3 = (WWindowPeer)this.realFSWindow.getPeer();
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var4 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var4.lock();

      try {
         var4.flushAndInvokeNow(new Runnable() {
            public void run() {
               long var1x = var3.getHWnd();
               if (var1x == 0L) {
                  D3DGraphicsDevice.this.fsStatus = false;
               } else {
                  D3DGraphicsDevice.this.fsStatus = D3DGraphicsDevice.enterFullScreenExclusiveNative(var1, var1x);
               }
            }
         });
      } finally {
         var4.unlock();
      }

      if (!this.fsStatus) {
         super.enterFullScreenExclusive(var1, var2);
      }

   }

   private static native boolean exitFullScreenExclusiveNative(int var0);

   protected void exitFullScreenExclusive(final int var1, WindowPeer var2) {
      if (this.fsStatus) {
         com.frojasg1.sun.java2d.d3d.D3DRenderQueue var3 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
         var3.lock();

         try {
            var3.flushAndInvokeNow(new Runnable() {
               public void run() {
                  D3DGraphicsDevice.exitFullScreenExclusiveNative(var1);
               }
            });
         } finally {
            var3.unlock();
         }
      } else {
         super.exitFullScreenExclusive(var1, var2);
      }

   }

   protected void addFSWindowListener(Window var1) {
      if (!(var1 instanceof Frame) && !(var1 instanceof Dialog) && (this.realFSWindow = this.getToplevelOwner(var1)) != null) {
         this.ownerOrigBounds = this.realFSWindow.getBounds();
         WWindowPeer var2 = (WWindowPeer)this.realFSWindow.getPeer();
         this.ownerWasVisible = this.realFSWindow.isVisible();
         Rectangle var3 = var1.getBounds();
         var2.reshape(var3.x, var3.y, var3.width, var3.height);
         var2.setVisible(true);
      } else {
         this.realFSWindow = var1;
      }

      this.fsWindowWasAlwaysOnTop = this.realFSWindow.isAlwaysOnTop();
      ((WWindowPeer)this.realFSWindow.getPeer()).setAlwaysOnTop(true);
      this.fsWindowListener = new D3DGraphicsDevice.D3DFSWindowAdapter();
      this.realFSWindow.addWindowListener(this.fsWindowListener);
   }

   protected void removeFSWindowListener(Window var1) {
      this.realFSWindow.removeWindowListener(this.fsWindowListener);
      this.fsWindowListener = null;
      WWindowPeer var2 = (WWindowPeer)this.realFSWindow.getPeer();
      if (var2 != null) {
         if (this.ownerOrigBounds != null) {
            if (this.ownerOrigBounds.width == 0) {
               this.ownerOrigBounds.width = 1;
            }

            if (this.ownerOrigBounds.height == 0) {
               this.ownerOrigBounds.height = 1;
            }

            var2.reshape(this.ownerOrigBounds.x, this.ownerOrigBounds.y, this.ownerOrigBounds.width, this.ownerOrigBounds.height);
            if (!this.ownerWasVisible) {
               var2.setVisible(false);
            }

            this.ownerOrigBounds = null;
         }

         if (!this.fsWindowWasAlwaysOnTop) {
            var2.setAlwaysOnTop(false);
         }
      }

      this.realFSWindow = null;
   }

   private static native DisplayMode getCurrentDisplayModeNative(int var0);

   protected DisplayMode getCurrentDisplayMode(final int var1) {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var2 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var2.lock();

      DisplayMode var4;
      try {
         class Result {
            DisplayMode dm = null;

            Result() {
            }
         }

         final Result var3 = new Result();
         var2.flushAndInvokeNow(new Runnable() {
            public void run() {
               var3.dm = D3DGraphicsDevice.getCurrentDisplayModeNative(var1);
            }
         });
         if (var3.dm != null) {
            var4 = var3.dm;
            return var4;
         }

         var4 = super.getCurrentDisplayMode(var1);
      } finally {
         var2.unlock();
      }

      return var4;
   }

   private static native void configDisplayModeNative(int var0, long var1, int var3, int var4, int var5, int var6);

   protected void configDisplayMode(final int var1, WindowPeer var2, final int var3, final int var4, final int var5, final int var6) {
      if (!this.fsStatus) {
         super.configDisplayMode(var1, var2, var3, var4, var5, var6);
      } else {
         final WWindowPeer var7 = (WWindowPeer)this.realFSWindow.getPeer();
         if (this.getFullScreenWindow() != this.realFSWindow) {
            Rectangle var8 = this.getDefaultConfiguration().getBounds();
            var7.reshape(var8.x, var8.y, var3, var4);
         }

         com.frojasg1.sun.java2d.d3d.D3DRenderQueue var12 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
         var12.lock();

         try {
            var12.flushAndInvokeNow(new Runnable() {
               public void run() {
                  long var1x = var7.getHWnd();
                  if (var1x != 0L) {
                     D3DGraphicsDevice.configDisplayModeNative(var1, var1x, var3, var4, var5, var6);
                  }
               }
            });
         } finally {
            var12.unlock();
         }

      }
   }

   private static native void enumDisplayModesNative(int var0, ArrayList var1);

   protected void enumDisplayModes(final int var1, final ArrayList var2) {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var3 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var3.lock();

      try {
         var3.flushAndInvokeNow(new Runnable() {
            public void run() {
               D3DGraphicsDevice.enumDisplayModesNative(var1, var2);
            }
         });
         if (var2.size() == 0) {
            var2.add(getCurrentDisplayModeNative(var1));
         }
      } finally {
         var3.unlock();
      }

   }

   private static native long getAvailableAcceleratedMemoryNative(int var0);

   public int getAvailableAcceleratedMemory() {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var1 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var1.lock();

      int var3;
      try {
         class Result {
            long mem = 0L;

            Result() {
            }
         }

         final Result var2 = new Result();
         var1.flushAndInvokeNow(new Runnable() {
            public void run() {
               var2.mem = D3DGraphicsDevice.getAvailableAcceleratedMemoryNative(D3DGraphicsDevice.this.getScreen());
            }
         });
         var3 = (int)var2.mem;
      } finally {
         var1.unlock();
      }

      return var3;
   }

   public GraphicsConfiguration[] getConfigurations() {
      if (this.configs == null && this.isD3DEnabledOnDevice()) {
         this.defaultConfig = this.getDefaultConfiguration();
         if (this.defaultConfig != null) {
            this.configs = new GraphicsConfiguration[1];
            this.configs[0] = this.defaultConfig;
            return (GraphicsConfiguration[])this.configs.clone();
         }
      }

      return super.getConfigurations();
   }

   public GraphicsConfiguration getDefaultConfiguration() {
      if (this.defaultConfig == null) {
         if (this.isD3DEnabledOnDevice()) {
            this.defaultConfig = new D3DGraphicsConfig(this);
         } else {
            this.defaultConfig = super.getDefaultConfiguration();
         }
      }

      return this.defaultConfig;
   }

   private static native boolean isD3DAvailableOnDeviceNative(int var0);

   public static boolean isD3DAvailableOnDevice(final int var0) {
      if (!d3dAvailable) {
         return false;
      } else {
         com.frojasg1.sun.java2d.d3d.D3DRenderQueue var1 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
         var1.lock();

         boolean var3;
         try {
            class Result {
               boolean avail = false;

               Result() {
               }
            }

            final Result var2 = new Result();
            var1.flushAndInvokeNow(new Runnable() {
               public void run() {
                  var2.avail = D3DGraphicsDevice.isD3DAvailableOnDeviceNative(var0);
               }
            });
            var3 = var2.avail;
         } finally {
            var1.unlock();
         }

         return var3;
      }
   }

   com.frojasg1.sun.java2d.d3d.D3DContext getContext() {
      return this.context;
   }

   ContextCapabilities getContextCapabilities() {
      return this.d3dCaps;
   }

   public void displayChanged() {
      super.displayChanged();
      if (d3dAvailable) {
         this.d3dCaps = getDeviceCaps(this.getScreen());
      }

   }

   protected void invalidate(int var1) {
      super.invalidate(var1);
      this.d3dCaps = new com.frojasg1.sun.java2d.d3d.D3DContext.D3DContextCaps(0, (String)null);
   }

   static {
      Toolkit.getDefaultToolkit();
      d3dAvailable = initD3D();
      if (d3dAvailable) {
         pfDisabled = true;
         PerfCounter.getD3DAvailable().set(1L);
      } else {
         PerfCounter.getD3DAvailable().set(0L);
      }

   }

   private static class D3DFSWindowAdapter extends WindowAdapter {
      private D3DFSWindowAdapter() {
      }

      public void windowDeactivated(WindowEvent var1) {
         com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
         com.frojasg1.sun.java2d.d3d.D3DRenderQueue.restoreDevices();
      }

      public void windowActivated(WindowEvent var1) {
         com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
         D3DRenderQueue.restoreDevices();
      }
   }
}
