package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Button;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.peer.ButtonPeer;

//final class WButtonPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ButtonPeer {
abstract class WButtonPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ButtonPeer {
   public Dimension getMinimumSize() {
      FontMetrics var1 = this.getFontMetrics(((Button)this.target).getFont());
      String var2 = ((Button)this.target).getLabel();
      if (var2 == null) {
         var2 = "";
      }

      return new Dimension(var1.stringWidth(var2) + 14, var1.getHeight() + 8);
   }

   public boolean isFocusable() {
      return true;
   }

   public native void setLabel(String var1);

   WButtonPeer(Button var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   public void handleAction(final long var1, final int var3) {
      WToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
         public void run() {
            WButtonPeer.this.postEvent(new ActionEvent(WButtonPeer.this.target, 1001, ((Button)WButtonPeer.this.target).getActionCommand(), var1, var3));
         }
      }, var1);
   }

   public boolean shouldClearRectBeforePaint() {
      return false;
   }

   private static native void initIDs();

   public boolean handleJavaKeyEvent(KeyEvent var1) {
      switch(var1.getID()) {
      case 402:
         if (var1.getKeyCode() == 32) {
            this.handleAction(var1.getWhen(), var1.getModifiers());
         }
      default:
         return false;
      }
   }

   static {
      initIDs();
   }
}
