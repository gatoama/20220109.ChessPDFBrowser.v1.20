package com.frojasg1.sun.awt;

import java.awt.AWTError;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.peer.ComponentPeer;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.ListIterator;

import com.frojasg1.sun.awt.DisplayChangedListener;
import com.frojasg1.sun.awt.Win32GraphicsDevice;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.java2d.SunGraphicsEnvironment;
import com.frojasg1.sun.java2d.SurfaceManagerFactory;
import com.frojasg1.sun.java2d.WindowsSurfaceManagerFactory;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.windows.WindowsFlags;

public class Win32GraphicsEnvironment extends SunGraphicsEnvironment {
   private static boolean displayInitialized;
   private ArrayList<WeakReference<com.frojasg1.sun.awt.Win32GraphicsDevice>> oldDevices;
   private static volatile boolean isDWMCompositionEnabled;

   private static native void initDisplay();

   public static void initDisplayWrapper() {
      if (!displayInitialized) {
         displayInitialized = true;
         initDisplay();
      }

   }

   public Win32GraphicsEnvironment() {
   }

   protected native int getNumScreens();

   protected native int getDefaultScreen();

   public GraphicsDevice getDefaultScreenDevice() {
      GraphicsDevice[] var1 = this.getScreenDevices();
      if (var1.length == 0) {
         throw new AWTError("no screen devices");
      } else {
         int var2 = this.getDefaultScreen();
         return var1[0 < var2 && var2 < var1.length ? var2 : 0];
      }
   }

   public native int getXResolution();

   public native int getYResolution();

   public void displayChanged() {
      GraphicsDevice[] var1 = new GraphicsDevice[this.getNumScreens()];
      GraphicsDevice[] var2 = this.screens;
      int var3;
      if (var2 != null) {
         for(var3 = 0; var3 < var2.length; ++var3) {
            if (!(this.screens[var3] instanceof com.frojasg1.sun.awt.Win32GraphicsDevice)) {
               assert false : var2[var3];
            } else {
               com.frojasg1.sun.awt.Win32GraphicsDevice var4 = (com.frojasg1.sun.awt.Win32GraphicsDevice)var2[var3];
               if (!var4.isValid()) {
                  if (this.oldDevices == null) {
                     this.oldDevices = new ArrayList();
                  }

                  this.oldDevices.add(new WeakReference(var4));
               } else if (var3 < var1.length) {
                  var1[var3] = var4;
               }
            }
         }

         var2 = null;
      }

      for(var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] == null) {
            var1[var3] = this.makeScreenDevice(var3);
         }
      }

      this.screens = var1;
      GraphicsDevice[] var7 = this.screens;
      int var8 = var7.length;

      for(int var5 = 0; var5 < var8; ++var5) {
         GraphicsDevice var6 = var7[var5];
         if (var6 instanceof com.frojasg1.sun.awt.DisplayChangedListener) {
            ((DisplayChangedListener)var6).displayChanged();
         }
      }

      if (this.oldDevices != null) {
         var3 = this.getDefaultScreen();
         ListIterator var9 = this.oldDevices.listIterator();

         while(var9.hasNext()) {
            com.frojasg1.sun.awt.Win32GraphicsDevice var10 = (com.frojasg1.sun.awt.Win32GraphicsDevice)((WeakReference)var9.next()).get();
            if (var10 != null) {
               var10.invalidate(var3);
               var10.displayChanged();
            } else {
               var9.remove();
            }
         }
      }

      WToolkit.resetGC();
      this.displayChanger.notifyListeners();
   }

   protected GraphicsDevice makeScreenDevice(int var1) {
      Object var2 = null;
      if (WindowsFlags.isD3DEnabled()) {
         var2 = D3DGraphicsDevice.createDevice(var1);
      }

      if (var2 == null) {
         var2 = new Win32GraphicsDevice(var1);
      }

      return (GraphicsDevice)var2;
   }

   public boolean isDisplayLocal() {
      return true;
   }

   public boolean isFlipStrategyPreferred(ComponentPeer var1) {
      GraphicsConfiguration var2;
      if (var1 != null && (var2 = var1.getGraphicsConfiguration()) != null) {
         GraphicsDevice var3 = var2.getDevice();
         if (var3 instanceof D3DGraphicsDevice) {
            return ((D3DGraphicsDevice)var3).isD3DEnabledOnDevice();
         }
      }

      return false;
   }

   public static boolean isDWMCompositionEnabled() {
      return isDWMCompositionEnabled;
   }

   private static void dwmCompositionChanged(boolean var0) {
      isDWMCompositionEnabled = var0;
   }

   public static native boolean isVistaOS();

   static {
      WToolkit.loadLibraries();
      WindowsFlags.initFlags();
      initDisplayWrapper();
      SurfaceManagerFactory.setInstance(new WindowsSurfaceManagerFactory());
   }
}
