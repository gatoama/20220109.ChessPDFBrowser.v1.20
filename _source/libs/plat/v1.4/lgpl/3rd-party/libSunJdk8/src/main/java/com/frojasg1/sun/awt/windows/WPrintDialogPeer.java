package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Font;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.DialogPeer;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.CausedFocusEvent;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WPrintDialog;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.pipe.Region;

//class WPrintDialogPeer extends com.frojasg1.sun.awt.windows.WWindowPeer implements DialogPeer {
abstract class WPrintDialogPeer extends com.frojasg1.sun.awt.windows.WWindowPeer implements DialogPeer {
   private com.frojasg1.sun.awt.windows.WComponentPeer parent;
   private Vector<com.frojasg1.sun.awt.windows.WWindowPeer> blockedWindows = new Vector();

   WPrintDialogPeer(com.frojasg1.sun.awt.windows.WPrintDialog var1) {
      super(var1);
   }

   void create(WComponentPeer var1) {
      this.parent = var1;
   }

   protected void checkCreation() {
   }

   protected void disposeImpl() {
      WToolkit.targetDisposedPeer(this.target, this);
   }

   private native boolean _show();

   public void show() {
      (new Thread(new Runnable() {
         public void run() {
            try {
               ((com.frojasg1.sun.awt.windows.WPrintDialog)WPrintDialogPeer.this.target).setRetVal(WPrintDialogPeer.this._show());
            } catch (Exception var2) {
            }

            ((com.frojasg1.sun.awt.windows.WPrintDialog)WPrintDialogPeer.this.target).setVisible(false);
         }
      })).start();
   }

   synchronized void setHWnd(long var1) {
      this.hwnd = var1;
      Iterator var3 = this.blockedWindows.iterator();

      while(var3.hasNext()) {
         com.frojasg1.sun.awt.windows.WWindowPeer var4 = (com.frojasg1.sun.awt.windows.WWindowPeer)var3.next();
         if (var1 != 0L) {
            var4.modalDisable((Dialog)this.target, var1);
         } else {
            var4.modalEnable((Dialog)this.target);
         }
      }

   }

   synchronized void blockWindow(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
      this.blockedWindows.add(var1);
      if (this.hwnd != 0L) {
         var1.modalDisable((Dialog)this.target, this.hwnd);
      }

   }

   synchronized void unblockWindow(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
      this.blockedWindows.remove(var1);
      if (this.hwnd != 0L) {
         var1.modalEnable((Dialog)this.target);
      }

   }

   public void blockWindows(List<Window> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Window var3 = (Window)var2.next();
         com.frojasg1.sun.awt.windows.WWindowPeer var4 = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var3);
         if (var4 != null) {
            this.blockWindow(var4);
         }
      }

   }

   public native void toFront();

   public native void toBack();

   void initialize() {
   }

   public void updateAlwaysOnTopState() {
   }

   public void setResizable(boolean var1) {
   }

   void hide() {
   }

   void enable() {
   }

   void disable() {
   }

   public void reshape(int var1, int var2, int var3, int var4) {
   }

   public boolean handleEvent(Event var1) {
      return false;
   }

   public void setForeground(Color var1) {
   }

   public void setBackground(Color var1) {
   }

   public void setFont(Font var1) {
   }

   public void updateMinimumSize() {
   }

   public void updateIconImages() {
   }

   public boolean requestFocus(boolean var1, boolean var2) {
      return false;
   }

   public boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      return false;
   }

   public void updateFocusableWindowState() {
   }

   void start() {
   }

   public void beginValidate() {
   }

   public void endValidate() {
   }

   void invalidate(int var1, int var2, int var3, int var4) {
   }

   public void addDropTarget(DropTarget var1) {
   }

   public void removeDropTarget(DropTarget var1) {
   }

   public void setZOrder(ComponentPeer var1) {
   }

   private static native void initIDs();

   public void applyShape(Region var1) {
   }

   public void setOpacity(float var1) {
   }

   public void setOpaque(boolean var1) {
   }

   public void updateWindow(BufferedImage var1) {
   }

   public void createScreenSurface(boolean var1) {
   }

   public void replaceSurfaceData() {
   }

   static {
      initIDs();
   }
}
