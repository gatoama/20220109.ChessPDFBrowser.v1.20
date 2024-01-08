package com.frojasg1.sun.awt.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.MenuBar;
import java.awt.Rectangle;
import java.awt.peer.FramePeer;
import java.security.AccessController;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.im.InputMethodManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WMenuBarPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.security.action.GetPropertyAction;

//class WFramePeer extends WWindowPeer implements FramePeer {
abstract class WFramePeer extends WWindowPeer implements FramePeer {
   private static final boolean keepOnMinimize;

   private static native void initIDs();

   public native void setState(int var1);

   public native int getState();

   public void setExtendedState(int var1) {
      AWTAccessor.getFrameAccessor().setExtendedState((Frame)this.target, var1);
   }

   public int getExtendedState() {
      return AWTAccessor.getFrameAccessor().getExtendedState((Frame)this.target);
   }

   private native void setMaximizedBounds(int var1, int var2, int var3, int var4);

   private native void clearMaximizedBounds();

   public void setMaximizedBounds(Rectangle var1) {
      if (var1 == null) {
         this.clearMaximizedBounds();
      } else {
         Rectangle var2 = (Rectangle)var1.clone();
         this.adjustMaximizedBounds(var2);
         this.setMaximizedBounds(var2.x, var2.y, var2.width, var2.height);
      }

   }

   private void adjustMaximizedBounds(Rectangle var1) {
      GraphicsConfiguration var2 = this.getGraphicsConfiguration();
      GraphicsDevice var3 = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
      GraphicsConfiguration var4 = var3.getDefaultConfiguration();
      if (var2 != null && var2 != var4) {
         Rectangle var5 = var2.getBounds();
         Rectangle var6 = var4.getBounds();
         boolean var7 = var5.width - var6.width > 0 || var5.height - var6.height > 0;
         if (var7) {
            var1.width -= var5.width - var6.width;
            var1.height -= var5.height - var6.height;
         }
      }

   }

   public boolean updateGraphicsData(GraphicsConfiguration var1) {
      boolean var2 = super.updateGraphicsData(var1);
      Rectangle var3 = AWTAccessor.getFrameAccessor().getMaximizedBounds((Frame)this.target);
      if (var3 != null) {
         this.setMaximizedBounds(var3);
      }

      return var2;
   }

   boolean isTargetUndecorated() {
      return ((Frame)this.target).isUndecorated();
   }

   public void reshape(int var1, int var2, int var3, int var4) {
      if (((Frame)this.target).isUndecorated()) {
         super.reshape(var1, var2, var3, var4);
      } else {
         this.reshapeFrame(var1, var2, var3, var4);
      }

   }

   public Dimension getMinimumSize() {
      Dimension var1 = new Dimension();
      if (!((Frame)this.target).isUndecorated()) {
         var1.setSize(getSysMinWidth(), getSysMinHeight());
      }

      if (((Frame)this.target).getMenuBar() != null) {
         var1.height += getSysMenuHeight();
      }

      return var1;
   }

   public void setMenuBar(MenuBar var1) {
      com.frojasg1.sun.awt.windows.WMenuBarPeer var2 = (com.frojasg1.sun.awt.windows.WMenuBarPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var1);
      if (var2 != null) {
         if (var2.framePeer != this) {
            var1.removeNotify();
            var1.addNotify();
            var2 = (com.frojasg1.sun.awt.windows.WMenuBarPeer) WToolkit.targetToPeer(var1);
            if (var2 != null && var2.framePeer != this) {
               throw new IllegalStateException("Wrong parent peer");
            }
         }

         if (var2 != null) {
            this.addChildPeer(var2);
         }
      }

      this.setMenuBar0(var2);
      this.updateInsets(this.insets_);
   }

   private native void setMenuBar0(com.frojasg1.sun.awt.windows.WMenuBarPeer var1);

   WFramePeer(Frame var1) {
      super(var1);
      InputMethodManager var2 = InputMethodManager.getInstance();
      String var3 = var2.getTriggerMenuString();
      if (var3 != null) {
         this.pSetIMMOption(var3);
      }

   }

   native void createAwtFrame(com.frojasg1.sun.awt.windows.WComponentPeer var1);

   void create(WComponentPeer var1) {
      this.preCreate(var1);
      this.createAwtFrame(var1);
   }

   void initialize() {
      super.initialize();
      Frame var1 = (Frame)this.target;
      if (var1.getTitle() != null) {
         this.setTitle(var1.getTitle());
      }

      this.setResizable(var1.isResizable());
      this.setState(var1.getExtendedState());
   }

   private static native int getSysMenuHeight();

   native void pSetIMMOption(String var1);

   void notifyIMMOptionChange() {
      InputMethodManager.getInstance().notifyChangeRequest((Component)this.target);
   }

   public void setBoundsPrivate(int var1, int var2, int var3, int var4) {
      this.setBounds(var1, var2, var3, var4, 3);
   }

   public Rectangle getBoundsPrivate() {
      return this.getBounds();
   }

   public void emulateActivation(boolean var1) {
      this.synthesizeWmActivate(var1);
   }

   private native void synthesizeWmActivate(boolean var1);

   static {
      initIDs();
      keepOnMinimize = "true".equals(AccessController.doPrivileged(new GetPropertyAction("sun.awt.keepWorkingSetOnMinimize")));
   }
}
