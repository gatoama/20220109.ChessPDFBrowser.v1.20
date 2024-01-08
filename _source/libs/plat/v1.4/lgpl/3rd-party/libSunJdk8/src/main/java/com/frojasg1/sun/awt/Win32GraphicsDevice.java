package com.frojasg1.sun.awt;

import java.awt.AWTPermission;
import java.awt.DisplayMode;
import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ColorModel;
import java.awt.peer.WindowPeer;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Vector;

import com.frojasg1.sun.awt.DisplayChangedListener;
import com.frojasg1.sun.awt.SunDisplayChanger;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig;
import com.frojasg1.sun.java2d.windows.WindowsFlags;
import com.frojasg1.sun.security.action.GetPropertyAction;

public class Win32GraphicsDevice extends GraphicsDevice implements com.frojasg1.sun.awt.DisplayChangedListener {
   int screen;
   ColorModel dynamicColorModel;
   ColorModel colorModel;
   protected GraphicsConfiguration[] configs;
   protected GraphicsConfiguration defaultConfig;
   private final String idString;
   protected String descString;
   private boolean valid;
   private com.frojasg1.sun.awt.SunDisplayChanger topLevels = new SunDisplayChanger();
   protected static boolean pfDisabled;
   private static AWTPermission fullScreenExclusivePermission;
   private DisplayMode defaultDisplayMode;
   private WindowListener fsWindowListener;

   private static native void initIDs();

   native void initDevice(int var1);

   public Win32GraphicsDevice(int var1) {
      this.screen = var1;
      this.idString = "\\Display" + this.screen;
      this.descString = "Win32GraphicsDevice[screen=" + this.screen;
      this.valid = true;
      this.initDevice(var1);
   }

   public int getType() {
      return 0;
   }

   public int getScreen() {
      return this.screen;
   }

   public boolean isValid() {
      return this.valid;
   }

   protected void invalidate(int var1) {
      this.valid = false;
      this.screen = var1;
   }

   public String getIDstring() {
      return this.idString;
   }

   public GraphicsConfiguration[] getConfigurations() {
      if (this.configs == null) {
         if (WindowsFlags.isOGLEnabled() && this.isDefaultDevice()) {
            this.defaultConfig = this.getDefaultConfiguration();
            if (this.defaultConfig != null) {
               this.configs = new GraphicsConfiguration[1];
               this.configs[0] = this.defaultConfig;
               return (GraphicsConfiguration[])this.configs.clone();
            }
         }

         int var1 = this.getMaxConfigs(this.screen);
         int var2 = this.getDefaultPixID(this.screen);
         Vector var3 = new Vector(var1);
         if (var2 == 0) {
            this.defaultConfig = com.frojasg1.sun.awt.Win32GraphicsConfig.getConfig(this, var2);
            var3.addElement(this.defaultConfig);
         } else {
            for(int var4 = 1; var4 <= var1; ++var4) {
               if (this.isPixFmtSupported(var4, this.screen)) {
                  if (var4 == var2) {
                     this.defaultConfig = com.frojasg1.sun.awt.Win32GraphicsConfig.getConfig(this, var4);
                     var3.addElement(this.defaultConfig);
                  } else {
                     var3.addElement(com.frojasg1.sun.awt.Win32GraphicsConfig.getConfig(this, var4));
                  }
               }
            }
         }

         this.configs = new GraphicsConfiguration[var3.size()];
         var3.copyInto(this.configs);
      }

      return (GraphicsConfiguration[])this.configs.clone();
   }

   protected int getMaxConfigs(int var1) {
      return pfDisabled ? 1 : this.getMaxConfigsImpl(var1);
   }

   private native int getMaxConfigsImpl(int var1);

   protected native boolean isPixFmtSupported(int var1, int var2);

   protected int getDefaultPixID(int var1) {
      return pfDisabled ? 0 : this.getDefaultPixIDImpl(var1);
   }

   private native int getDefaultPixIDImpl(int var1);

   public GraphicsConfiguration getDefaultConfiguration() {
      if (this.defaultConfig == null) {
         if (WindowsFlags.isOGLEnabled() && this.isDefaultDevice()) {
            int var1 = WGLGraphicsConfig.getDefaultPixFmt(this.screen);
            this.defaultConfig = WGLGraphicsConfig.getConfig(this, var1);
            if (WindowsFlags.isOGLVerbose()) {
               if (this.defaultConfig != null) {
                  System.out.print("OpenGL pipeline enabled");
               } else {
                  System.out.print("Could not enable OpenGL pipeline");
               }

               System.out.println(" for default config on screen " + this.screen);
            }
         }

         if (this.defaultConfig == null) {
            this.defaultConfig = Win32GraphicsConfig.getConfig(this, 0);
         }
      }

      return this.defaultConfig;
   }

   public String toString() {
      return this.valid ? this.descString + "]" : this.descString + ", removed]";
   }

   private boolean isDefaultDevice() {
      return this == GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
   }

   private static boolean isFSExclusiveModeAllowed() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         if (fullScreenExclusivePermission == null) {
            fullScreenExclusivePermission = new AWTPermission("fullScreenExclusive");
         }

         try {
            var0.checkPermission(fullScreenExclusivePermission);
         } catch (SecurityException var2) {
            return false;
         }
      }

      return true;
   }

   public boolean isFullScreenSupported() {
      return isFSExclusiveModeAllowed();
   }

   public synchronized void setFullScreenWindow(Window var1) {
      Window var2 = this.getFullScreenWindow();
      if (var1 != var2) {
         if (!this.isFullScreenSupported()) {
            super.setFullScreenWindow(var1);
         } else {
            WWindowPeer var3;
            if (var2 != null) {
               if (this.defaultDisplayMode != null) {
                  this.setDisplayMode(this.defaultDisplayMode);
                  this.defaultDisplayMode = null;
               }

               var3 = (WWindowPeer)var2.getPeer();
               if (var3 != null) {
                  var3.setFullScreenExclusiveModeState(false);
                  synchronized(var3) {
                     this.exitFullScreenExclusive(this.screen, var3);
                  }
               }

               this.removeFSWindowListener(var2);
            }

            super.setFullScreenWindow(var1);
            if (var1 != null) {
               this.defaultDisplayMode = this.getDisplayMode();
               this.addFSWindowListener(var1);
               var3 = (WWindowPeer)var1.getPeer();
               if (var3 != null) {
                  synchronized(var3) {
                     this.enterFullScreenExclusive(this.screen, var3);
                  }

                  var3.setFullScreenExclusiveModeState(true);
               }

               var3.updateGC();
            }

         }
      }
   }

   protected native void enterFullScreenExclusive(int var1, WindowPeer var2);

   protected native void exitFullScreenExclusive(int var1, WindowPeer var2);

   public boolean isDisplayChangeSupported() {
      return this.isFullScreenSupported() && this.getFullScreenWindow() != null;
   }

   public synchronized void setDisplayMode(DisplayMode var1) {
      if (!this.isDisplayChangeSupported()) {
         super.setDisplayMode(var1);
      } else if (var1 != null && (var1 = this.getMatchingDisplayMode(var1)) != null) {
         if (!this.getDisplayMode().equals(var1)) {
            Window var2 = this.getFullScreenWindow();
            if (var2 != null) {
               WWindowPeer var3 = (WWindowPeer)var2.getPeer();
               this.configDisplayMode(this.screen, var3, var1.getWidth(), var1.getHeight(), var1.getBitDepth(), var1.getRefreshRate());
               Rectangle var4 = this.getDefaultConfiguration().getBounds();
               var2.setBounds(var4.x, var4.y, var1.getWidth(), var1.getHeight());
            } else {
               throw new IllegalStateException("Must be in fullscreen mode in order to set display mode");
            }
         }
      } else {
         throw new IllegalArgumentException("Invalid display mode");
      }
   }

   protected native DisplayMode getCurrentDisplayMode(int var1);

   protected native void configDisplayMode(int var1, WindowPeer var2, int var3, int var4, int var5, int var6);

   protected native void enumDisplayModes(int var1, ArrayList var2);

   public synchronized DisplayMode getDisplayMode() {
      DisplayMode var1 = this.getCurrentDisplayMode(this.screen);
      return var1;
   }

   public synchronized DisplayMode[] getDisplayModes() {
      ArrayList var1 = new ArrayList();
      this.enumDisplayModes(this.screen, var1);
      int var2 = var1.size();
      DisplayMode[] var3 = new DisplayMode[var2];

      for(int var4 = 0; var4 < var2; ++var4) {
         var3[var4] = (DisplayMode)var1.get(var4);
      }

      return var3;
   }

   protected synchronized DisplayMode getMatchingDisplayMode(DisplayMode var1) {
      if (!this.isDisplayChangeSupported()) {
         return null;
      } else {
         DisplayMode[] var2 = this.getDisplayModes();
         DisplayMode[] var3 = var2;
         int var4 = var2.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            DisplayMode var6 = var3[var5];
            if (var1.equals(var6) || var1.getRefreshRate() == 0 && var1.getWidth() == var6.getWidth() && var1.getHeight() == var6.getHeight() && var1.getBitDepth() == var6.getBitDepth()) {
               return var6;
            }
         }

         return null;
      }
   }

   public void displayChanged() {
      this.dynamicColorModel = null;
      this.defaultConfig = null;
      this.configs = null;
      this.topLevels.notifyListeners();
   }

   public void paletteChanged() {
   }

   public void addDisplayChangedListener(com.frojasg1.sun.awt.DisplayChangedListener var1) {
      this.topLevels.add(var1);
   }

   public void removeDisplayChangedListener(DisplayChangedListener var1) {
      this.topLevels.remove(var1);
   }

   private native ColorModel makeColorModel(int var1, boolean var2);

   public ColorModel getDynamicColorModel() {
      if (this.dynamicColorModel == null) {
         this.dynamicColorModel = this.makeColorModel(this.screen, true);
      }

      return this.dynamicColorModel;
   }

   public ColorModel getColorModel() {
      if (this.colorModel == null) {
         this.colorModel = this.makeColorModel(this.screen, false);
      }

      return this.colorModel;
   }

   protected void addFSWindowListener(final Window var1) {
      this.fsWindowListener = new Win32GraphicsDevice.Win32FSWindowAdapter(this);
      EventQueue.invokeLater(new Runnable() {
         public void run() {
            var1.addWindowListener(Win32GraphicsDevice.this.fsWindowListener);
         }
      });
   }

   protected void removeFSWindowListener(Window var1) {
      var1.removeWindowListener(this.fsWindowListener);
      this.fsWindowListener = null;
   }

   static {
      String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.awt.nopixfmt"));
      pfDisabled = var0 != null;
      initIDs();
   }

   private static class Win32FSWindowAdapter extends WindowAdapter {
      private Win32GraphicsDevice device;
      private DisplayMode dm;

      Win32FSWindowAdapter(Win32GraphicsDevice var1) {
         this.device = var1;
      }

      private void setFSWindowsState(Window var1, int var2) {
         GraphicsDevice[] var3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
         GraphicsDevice[] var4;
         int var5;
         int var6;
         GraphicsDevice var7;
         if (var1 != null) {
            var4 = var3;
            var5 = var3.length;

            for(var6 = 0; var6 < var5; ++var6) {
               var7 = var4[var6];
               if (var1 == var7.getFullScreenWindow()) {
                  return;
               }
            }
         }

         var4 = var3;
         var5 = var3.length;

         for(var6 = 0; var6 < var5; ++var6) {
            var7 = var4[var6];
            Window var8 = var7.getFullScreenWindow();
            if (var8 instanceof Frame) {
               ((Frame)var8).setExtendedState(var2);
            }
         }

      }

      public void windowDeactivated(WindowEvent var1) {
         this.setFSWindowsState(var1.getOppositeWindow(), 1);
      }

      public void windowActivated(WindowEvent var1) {
         this.setFSWindowsState(var1.getOppositeWindow(), 0);
      }

      public void windowIconified(WindowEvent var1) {
         DisplayMode var2 = this.device.defaultDisplayMode;
         if (var2 != null) {
            this.dm = this.device.getDisplayMode();
            this.device.setDisplayMode(var2);
         }

      }

      public void windowDeiconified(WindowEvent var1) {
         if (this.dm != null) {
            this.device.setDisplayMode(this.dm);
            this.dm = null;
         }

      }
   }
}
