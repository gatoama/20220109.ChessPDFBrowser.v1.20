package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.AWTEventMulticaster;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Window;
import java.awt.Window.Type;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.AffineTransform;
import java.awt.image.DataBufferInt;
import java.awt.peer.ComponentPeer;
import java.awt.peer.WindowPeer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;

import com.frojasg1.sun.awt.DisplayChangedListener;
import com.frojasg1.sun.awt.windows.WPanelPeer;
import com.frojasg1.sun.awt.*;
import com.frojasg1.sun.awt.windows.TranslucentWindowPainter;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WFileDialogPeer;
import com.frojasg1.sun.awt.windows.WPrintDialogPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.util.logging.PlatformLogger;
//public class WWindowPeer extends com.frojasg1.sun.awt.windows.WPanelPeer implements WindowPeer, DisplayChangedListener {

public abstract class WWindowPeer extends com.frojasg1.sun.awt.windows.WPanelPeer implements WindowPeer, DisplayChangedListener {
   private static final com.frojasg1.sun.util.logging.PlatformLogger log = com.frojasg1.sun.util.logging.PlatformLogger.getLogger("com.frojasg1.sun.awt.windows.WWindowPeer");
   private static final com.frojasg1.sun.util.logging.PlatformLogger screenLog = PlatformLogger.getLogger("com.frojasg1.sun.awt.windows.screen.WWindowPeer");
   private com.frojasg1.sun.awt.windows.WWindowPeer modalBlocker = null;
   private boolean isOpaque;
   private com.frojasg1.sun.awt.windows.TranslucentWindowPainter painter;
   private static final StringBuffer ACTIVE_WINDOWS_KEY = new StringBuffer("active_windows_list");
   private static PropertyChangeListener activeWindowListener = new com.frojasg1.sun.awt.windows.WWindowPeer.ActiveWindowListener();
   private static final PropertyChangeListener guiDisposedListener = new com.frojasg1.sun.awt.windows.WWindowPeer.GuiDisposedListener();
   private WindowListener windowListener;
   private volatile Type windowType;
   private volatile int sysX;
   private volatile int sysY;
   private volatile int sysW;
   private volatile int sysH;
   private float opacity;

   private static native void initIDs();

   protected void disposeImpl() {
      com.frojasg1.sun.awt.AppContext var1 = com.frojasg1.sun.awt.SunToolkit.targetToAppContext(this.target);
      synchronized(var1) {
         List var3 = (List)var1.get(ACTIVE_WINDOWS_KEY);
         if (var3 != null) {
            var3.remove(this);
         }
      }

      GraphicsConfiguration var2 = this.getGraphicsConfiguration();
      ((com.frojasg1.sun.awt.Win32GraphicsDevice)var2.getDevice()).removeDisplayChangedListener(this);
      synchronized(this.getStateLock()) {
         com.frojasg1.sun.awt.windows.TranslucentWindowPainter var4 = this.painter;
         if (var4 != null) {
            var4.flush();
         }
      }

      super.disposeImpl();
   }

   public void toFront() {
      this.updateFocusableWindowState();
      this._toFront();
   }

   private native void _toFront();

   public native void toBack();

   private native void setAlwaysOnTopNative(boolean var1);

   public void setAlwaysOnTop(boolean var1) {
      if (var1 && ((Window)this.target).isVisible() || !var1) {
         this.setAlwaysOnTopNative(var1);
      }

   }

   public void updateAlwaysOnTopState() {
      this.setAlwaysOnTop(((Window)this.target).isAlwaysOnTop());
   }

   public void updateFocusableWindowState() {
      this.setFocusableWindow(((Window)this.target).isFocusableWindow());
   }

   native void setFocusableWindow(boolean var1);

   public void setTitle(String var1) {
      if (var1 == null) {
         var1 = "";
      }

      this._setTitle(var1);
   }

   private native void _setTitle(String var1);

   public void setResizable(boolean var1) {
      this._setResizable(var1);
   }

   private native void _setResizable(boolean var1);

   WWindowPeer(Window var1) {
      super(var1);
      this.windowType = Type.NORMAL;
      this.sysX = 0;
      this.sysY = 0;
      this.sysW = 0;
      this.sysH = 0;
      this.opacity = 1.0F;
   }

   void initialize() {
      super.initialize();
      this.updateInsets(this.insets_);
      Font var1 = ((Window)this.target).getFont();
      if (var1 == null) {
         var1 = defaultFont;
         ((Window)this.target).setFont(var1);
         this.setFont(var1);
      }

      GraphicsConfiguration var2 = this.getGraphicsConfiguration();
      ((com.frojasg1.sun.awt.Win32GraphicsDevice)var2.getDevice()).addDisplayChangedListener(this);
      initActiveWindowsTracking((Window)this.target);
      this.updateIconImages();
      Shape var3 = ((Window)this.target).getShape();
      if (var3 != null) {
//         this.applyShape(Region.getInstance(var3, (AffineTransform)null));
      }

      float var4 = ((Window)this.target).getOpacity();
      if (var4 < 1.0F) {
         this.setOpacity(var4);
      }

      synchronized(this.getStateLock()) {
         this.isOpaque = true;
         this.setOpaque(((Window)this.target).isOpaque());
      }
   }

   native void createAwtWindow(com.frojasg1.sun.awt.windows.WComponentPeer var1);

   void preCreate(com.frojasg1.sun.awt.windows.WComponentPeer var1) {
      this.windowType = ((Window)this.target).getType();
   }

   void create(com.frojasg1.sun.awt.windows.WComponentPeer var1) {
      this.preCreate(var1);
      this.createAwtWindow(var1);
   }

   final com.frojasg1.sun.awt.windows.WComponentPeer getNativeParent() {
      Window var1 = ((Window)this.target).getOwner();
      return (com.frojasg1.sun.awt.windows.WComponentPeer) WToolkit.targetToPeer(var1);
   }

   protected void realShow() {
      super.show();
   }

   public void show() {
      this.updateFocusableWindowState();
      boolean var1 = ((Window)this.target).isAlwaysOnTop();
      this.updateGC();
      this.realShow();
      this.updateMinimumSize();
      if (((Window)this.target).isAlwaysOnTopSupported() && var1) {
         this.setAlwaysOnTop(var1);
      }

      synchronized(this.getStateLock()) {
         if (!this.isOpaque) {
            this.updateWindow(true);
         }
      }

      com.frojasg1.sun.awt.windows.WComponentPeer var2 = this.getNativeParent();
      if (var2 != null && var2.isLightweightFramePeer()) {
         Rectangle var3 = this.getBounds();
         this.handleExpose(0, 0, var3.width, var3.height);
      }

   }

   native void updateInsets(Insets var1);

   static native int getSysMinWidth();

   static native int getSysMinHeight();

   static native int getSysIconWidth();

   static native int getSysIconHeight();

   static native int getSysSmIconWidth();

   static native int getSysSmIconHeight();

   native void setIconImagesData(int[] var1, int var2, int var3, int[] var4, int var5, int var6);

   synchronized native void reshapeFrame(int var1, int var2, int var3, int var4);

   public boolean requestWindowFocus(CausedFocusEvent.Cause var1) {
      return !this.focusAllowedFor() ? false : this.requestWindowFocus(var1 == CausedFocusEvent.Cause.MOUSE_EVENT);
   }

   private native boolean requestWindowFocus(boolean var1);

   public boolean focusAllowedFor() {
      Window var1 = (Window)this.target;
      if (var1.isVisible() && var1.isEnabled() && var1.isFocusableWindow()) {
         return !this.isModalBlocked();
      } else {
         return false;
      }
   }

   void hide() {
      WindowListener var1 = this.windowListener;
      if (var1 != null) {
         var1.windowClosing(new WindowEvent((Window)this.target, 201));
      }

      super.hide();
   }

   void preprocessPostEvent(AWTEvent var1) {
      if (var1 instanceof WindowEvent) {
         WindowListener var2 = this.windowListener;
         if (var2 != null) {
            switch(var1.getID()) {
               case 201:
                  var2.windowClosing((WindowEvent)var1);
                  break;
               case 203:
                  var2.windowIconified((WindowEvent)var1);
            }
         }
      }

   }

   synchronized void addWindowListener(WindowListener var1) {
      this.windowListener = AWTEventMulticaster.add(this.windowListener, var1);
   }

   synchronized void removeWindowListener(WindowListener var1) {
      this.windowListener = AWTEventMulticaster.remove(this.windowListener, var1);
   }

   public void updateMinimumSize() {
      Dimension var1 = null;
      if (((Component)this.target).isMinimumSizeSet()) {
         var1 = ((Component)this.target).getMinimumSize();
      }

      if (var1 != null) {
         int var2 = getSysMinWidth();
         int var3 = getSysMinHeight();
         int var4 = var1.width >= var2 ? var1.width : var2;
         int var5 = var1.height >= var3 ? var1.height : var3;
         this.setMinSize(var4, var5);
      } else {
         this.setMinSize(0, 0);
      }

   }

   public void updateIconImages() {
      List var1 = ((Window)this.target).getIconImages();
      if (var1 != null && var1.size() != 0) {
         int var2 = getSysIconWidth();
         int var3 = getSysIconHeight();
         int var4 = getSysSmIconWidth();
         int var5 = getSysSmIconHeight();
         DataBufferInt var6 = com.frojasg1.sun.awt.SunToolkit.getScaledIconData(var1, var2, var3);
         DataBufferInt var7 = com.frojasg1.sun.awt.SunToolkit.getScaledIconData(var1, var4, var5);
         if (var6 != null && var7 != null) {
            this.setIconImagesData(var6.getData(), var2, var3, var7.getData(), var4, var5);
         } else {
            this.setIconImagesData((int[])null, 0, 0, (int[])null, 0, 0);
         }
      } else {
         this.setIconImagesData((int[])null, 0, 0, (int[])null, 0, 0);
      }

   }

   native void setMinSize(int var1, int var2);

   public boolean isModalBlocked() {
      return this.modalBlocker != null;
   }

   public void setModalBlocked(Dialog var1, boolean var2) {
      synchronized(((Component)this.getTarget()).getTreeLock()) {
         com.frojasg1.sun.awt.windows.WWindowPeer var4 = (com.frojasg1.sun.awt.windows.WWindowPeer)var1.getPeer();
         if (var2) {
            this.modalBlocker = var4;
            if (var4 instanceof com.frojasg1.sun.awt.windows.WFileDialogPeer) {
               ((com.frojasg1.sun.awt.windows.WFileDialogPeer)var4).blockWindow(this);
            } else if (var4 instanceof com.frojasg1.sun.awt.windows.WPrintDialogPeer) {
               ((com.frojasg1.sun.awt.windows.WPrintDialogPeer)var4).blockWindow(this);
            } else {
               this.modalDisable(var1, var4.getHWnd());
            }
         } else {
            this.modalBlocker = null;
            if (var4 instanceof com.frojasg1.sun.awt.windows.WFileDialogPeer) {
               ((com.frojasg1.sun.awt.windows.WFileDialogPeer)var4).unblockWindow(this);
            } else if (var4 instanceof com.frojasg1.sun.awt.windows.WPrintDialogPeer) {
               ((com.frojasg1.sun.awt.windows.WPrintDialogPeer)var4).unblockWindow(this);
            } else {
               this.modalEnable(var1);
            }
         }

      }
   }

   native void modalDisable(Dialog var1, long var2);

   native void modalEnable(Dialog var1);

   public static long[] getActiveWindowHandles(Component var0) {
      com.frojasg1.sun.awt.AppContext var1 = com.frojasg1.sun.awt.SunToolkit.targetToAppContext(var0);
      if (var1 == null) {
         return null;
      } else {
         synchronized(var1) {
            List var3 = (List)var1.get(ACTIVE_WINDOWS_KEY);
            if (var3 == null) {
               return null;
            } else {
               long[] var4 = new long[var3.size()];

               for(int var5 = 0; var5 < var3.size(); ++var5) {
                  var4[var5] = ((com.frojasg1.sun.awt.windows.WWindowPeer)var3.get(var5)).getHWnd();
               }

               return var4;
            }
         }
      }
   }

   void draggedToNewScreen() {
      com.frojasg1.sun.awt.SunToolkit.executeOnEventHandlerThread((Component)this.target, new Runnable() {
         public void run() {
            com.frojasg1.sun.awt.windows.WWindowPeer.this.displayChanged();
         }
      });
   }

   public void updateGC() {
      int var1 = this.getScreenImOn();
      if (screenLog.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("Screen number: " + var1);
      }

      com.frojasg1.sun.awt.Win32GraphicsDevice var2 = (com.frojasg1.sun.awt.Win32GraphicsDevice)this.winGraphicsConfig.getDevice();
      GraphicsDevice[] var4 = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
      com.frojasg1.sun.awt.Win32GraphicsDevice var3;
      if (var1 >= var4.length) {
         var3 = (com.frojasg1.sun.awt.Win32GraphicsDevice)GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      } else {
         var3 = (Win32GraphicsDevice)var4[var1];
      }

      this.winGraphicsConfig = (Win32GraphicsConfig)var3.getDefaultConfiguration();
      if (screenLog.isLoggable(PlatformLogger.Level.FINE) && this.winGraphicsConfig == null) {
         screenLog.fine("Assertion (winGraphicsConfig != null) failed");
      }

      if (var2 != var3) {
         var2.removeDisplayChangedListener(this);
         var3.addDisplayChangedListener(this);
      }

      com.frojasg1.sun.awt.AWTAccessor.getComponentAccessor().setGraphicsConfiguration((Component)this.target, this.winGraphicsConfig);
   }

   public void displayChanged() {
      this.updateGC();
   }

   public void paletteChanged() {
   }

   private native int getScreenImOn();

   public final native void setFullScreenExclusiveModeState(boolean var1);

   public void grab() {
      this.nativeGrab();
   }

   public void ungrab() {
      this.nativeUngrab();
   }

   private native void nativeGrab();

   private native void nativeUngrab();

   private final boolean hasWarningWindow() {
      return ((Window)this.target).getWarningString() != null;
   }

   boolean isTargetUndecorated() {
      return true;
   }

   public native void repositionSecurityWarning();

   public void setBounds(int var1, int var2, int var3, int var4, int var5) {
      this.sysX = var1;
      this.sysY = var2;
      this.sysW = var3;
      this.sysH = var4;
      super.setBounds(var1, var2, var3, var4, var5);
   }

   public void print(Graphics var1) {
      Shape var2 = AWTAccessor.getWindowAccessor().getShape((Window)this.target);
      if (var2 != null) {
         var1.setClip(var2);
      }

      super.print(var1);
   }

   private void replaceSurfaceDataRecursively(Component var1) {
      if (var1 instanceof Container) {
         Component[] var2 = ((Container)var1).getComponents();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Component var5 = var2[var4];
            this.replaceSurfaceDataRecursively(var5);
         }
      }

      ComponentPeer var6 = var1.getPeer();
      if (var6 instanceof com.frojasg1.sun.awt.windows.WComponentPeer) {
         ((WComponentPeer)var6).replaceSurfaceDataLater();
      }

   }

   public final Graphics getTranslucentGraphics() {
      synchronized(this.getStateLock()) {
         return this.isOpaque ? null : this.painter.getBackBuffer(false).getGraphics();
      }
   }

   public void setBackground(Color var1) {
      super.setBackground(var1);
      synchronized(this.getStateLock()) {
         if (!this.isOpaque && ((Window)this.target).isVisible()) {
            this.updateWindow(true);
         }

      }
   }

   private native void setOpacity(int var1);

   public void setOpacity(float var1) {
      if (((com.frojasg1.sun.awt.SunToolkit)((Window)this.target).getToolkit()).isWindowOpacitySupported()) {
         if (!(var1 < 0.0F) && !(var1 > 1.0F)) {
            if ((this.opacity == 1.0F && var1 < 1.0F || this.opacity < 1.0F && var1 == 1.0F) && !com.frojasg1.sun.awt.Win32GraphicsEnvironment.isVistaOS()) {
               this.replaceSurfaceDataRecursively((Component)this.getTarget());
            }

            this.opacity = var1;
            int var3 = (int)(var1 * 255.0F);
            if (var3 < 0) {
               var3 = 0;
            }

            if (var3 > 255) {
               var3 = 255;
            }

            this.setOpacity(var3);
            synchronized(this.getStateLock()) {
               if (!this.isOpaque && ((Window)this.target).isVisible()) {
                  this.updateWindow(true);
               }

            }
         } else {
            throw new IllegalArgumentException("The value of opacity should be in the range [0.0f .. 1.0f].");
         }
      }
   }

   private native void setOpaqueImpl(boolean var1);

   public void setOpaque(boolean var1) {
      synchronized(this.getStateLock()) {
         if (this.isOpaque == var1) {
            return;
         }
      }

      Window var2 = (Window)this.getTarget();
      if (!var1) {
         com.frojasg1.sun.awt.SunToolkit var3 = (com.frojasg1.sun.awt.SunToolkit)var2.getToolkit();
         if (!var3.isWindowTranslucencySupported() || !var3.isTranslucencyCapable(var2.getGraphicsConfiguration())) {
            return;
         }
      }

      boolean var9 = Win32GraphicsEnvironment.isVistaOS();
      if (this.isOpaque != var1 && !var9) {
         this.replaceSurfaceDataRecursively(var2);
      }

      synchronized(this.getStateLock()) {
         this.isOpaque = var1;
         this.setOpaqueImpl(var1);
         if (var1) {
            com.frojasg1.sun.awt.windows.TranslucentWindowPainter var5 = this.painter;
            if (var5 != null) {
               var5.flush();
               this.painter = null;
            }
         } else {
            this.painter = com.frojasg1.sun.awt.windows.TranslucentWindowPainter.createInstance(this);
         }
      }

      if (var9) {
         Shape var4 = var2.getShape();
         if (var4 != null) {
            var2.setShape(var4);
         }
      }

      if (var2.isVisible()) {
         this.updateWindow(true);
      }

   }

   native void updateWindowImpl(int[] var1, int var2, int var3);

   public void updateWindow() {
      this.updateWindow(false);
   }

   private void updateWindow(boolean var1) {
      Window var2 = (Window)this.target;
      synchronized(this.getStateLock()) {
         if (!this.isOpaque && var2.isVisible() && var2.getWidth() > 0 && var2.getHeight() > 0) {
            com.frojasg1.sun.awt.windows.TranslucentWindowPainter var4 = this.painter;
            if (var4 != null) {
               var4.updateWindow(var1);
            } else if (log.isLoggable(PlatformLogger.Level.FINER)) {
               log.finer("Translucent window painter is null in updateWindow");
            }

         }
      }
   }

   private static void initActiveWindowsTracking(Window var0) {
      com.frojasg1.sun.awt.AppContext var1 = com.frojasg1.sun.awt.AppContext.getAppContext();
      synchronized(var1) {
         List var3 = (List)var1.get(ACTIVE_WINDOWS_KEY);
         if (var3 == null) {
            LinkedList var7 = new LinkedList();
            var1.put(ACTIVE_WINDOWS_KEY, var7);
            var1.addPropertyChangeListener("guidisposed", guiDisposedListener);
            KeyboardFocusManager var4 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            var4.addPropertyChangeListener("activeWindow", activeWindowListener);
         }

      }
   }

   static {
      initIDs();
   }

   private static class ActiveWindowListener implements PropertyChangeListener {
      private ActiveWindowListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         Window var2 = (Window)var1.getNewValue();
         if (var2 != null) {
            com.frojasg1.sun.awt.AppContext var3 = SunToolkit.targetToAppContext(var2);
            synchronized(var3) {
               com.frojasg1.sun.awt.windows.WWindowPeer var5 = (com.frojasg1.sun.awt.windows.WWindowPeer)var2.getPeer();
               List var6 = (List)var3.get(com.frojasg1.sun.awt.windows.WWindowPeer.ACTIVE_WINDOWS_KEY);
               if (var6 != null) {
                  var6.remove(var5);
                  var6.add(var5);
               }

            }
         }
      }
   }

   private static class GuiDisposedListener implements PropertyChangeListener {
      private GuiDisposedListener() {
      }

      public void propertyChange(PropertyChangeEvent var1) {
         boolean var2 = (Boolean)var1.getNewValue();
         if (!var2 && com.frojasg1.sun.awt.windows.WWindowPeer.log.isLoggable(PlatformLogger.Level.FINE)) {
            com.frojasg1.sun.awt.windows.WWindowPeer.log.fine(" Assertion (newValue != true) failed for AppContext.GUI_DISPOSED ");
         }

         com.frojasg1.sun.awt.AppContext var3 = AppContext.getAppContext();
         synchronized(var3) {
            var3.remove(com.frojasg1.sun.awt.windows.WWindowPeer.ACTIVE_WINDOWS_KEY);
            var3.removePropertyChangeListener("guidisposed", this);
            KeyboardFocusManager var5 = KeyboardFocusManager.getCurrentKeyboardFocusManager();
            var5.removePropertyChangeListener("activeWindow", com.frojasg1.sun.awt.windows.WWindowPeer.activeWindowListener);
         }
      }
   }
}
