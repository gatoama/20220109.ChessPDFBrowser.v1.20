package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Dimension;
import java.awt.Scrollbar;
import java.awt.event.AdjustmentEvent;
import java.awt.peer.ScrollbarPeer;

//final class WScrollbarPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ScrollbarPeer {
abstract class WScrollbarPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ScrollbarPeer {
   private boolean dragInProgress = false;

   static native int getScrollbarSize(int var0);

   public Dimension getMinimumSize() {
      return ((Scrollbar)this.target).getOrientation() == 1 ? new Dimension(getScrollbarSize(1), 50) : new Dimension(50, getScrollbarSize(0));
   }

   public native void setValues(int var1, int var2, int var3, int var4);

   public native void setLineIncrement(int var1);

   public native void setPageIncrement(int var1);

   WScrollbarPeer(Scrollbar var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      Scrollbar var1 = (Scrollbar)this.target;
      this.setValues(var1.getValue(), var1.getVisibleAmount(), var1.getMinimum(), var1.getMaximum());
      super.initialize();
   }

   private void postAdjustmentEvent(final int var1, final int var2, final boolean var3) {
      final Scrollbar var4 = (Scrollbar)this.target;
      com.frojasg1.sun.awt.windows.WToolkit.executeOnEventHandlerThread(var4, new Runnable() {
         public void run() {
            var4.setValueIsAdjusting(var3);
            var4.setValue(var2);
            WScrollbarPeer.this.postEvent(new AdjustmentEvent(var4, 601, var1, var2, var3));
         }
      });
   }

   void lineUp(int var1) {
      this.postAdjustmentEvent(2, var1, false);
   }

   void lineDown(int var1) {
      this.postAdjustmentEvent(1, var1, false);
   }

   void pageUp(int var1) {
      this.postAdjustmentEvent(3, var1, false);
   }

   void pageDown(int var1) {
      this.postAdjustmentEvent(4, var1, false);
   }

   void warp(int var1) {
      this.postAdjustmentEvent(5, var1, false);
   }

   void drag(int var1) {
      if (!this.dragInProgress) {
         this.dragInProgress = true;
      }

      this.postAdjustmentEvent(5, var1, true);
   }

   void dragEnd(final int var1) {
      final Scrollbar var2 = (Scrollbar)this.target;
      if (this.dragInProgress) {
         this.dragInProgress = false;
         WToolkit.executeOnEventHandlerThread(var2, new Runnable() {
            public void run() {
               var2.setValueIsAdjusting(false);
               WScrollbarPeer.this.postEvent(new AdjustmentEvent(var2, 601, 5, var1, false));
            }
         });
      }
   }

   public boolean shouldClearRectBeforePaint() {
      return false;
   }
}
