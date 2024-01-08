package com.frojasg1.sun.awt.windows;

import java.awt.Adjustable;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.ScrollPane;
import java.awt.ScrollPaneAdjustable;
import java.awt.peer.ScrollPanePeer;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.PeerEvent;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WPanelPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.util.logging.PlatformLogger;

//final class WScrollPanePeer extends com.frojasg1.sun.awt.windows.WPanelPeer implements ScrollPanePeer {
abstract class WScrollPanePeer extends com.frojasg1.sun.awt.windows.WPanelPeer implements ScrollPanePeer {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WScrollPanePeer");
   int scrollbarWidth = this._getVScrollbarWidth();
   int scrollbarHeight = this._getHScrollbarHeight();
   int prevx;
   int prevy;

   static native void initIDs();

   native void create(com.frojasg1.sun.awt.windows.WComponentPeer var1);

   native int getOffset(int var1);

   WScrollPanePeer(Component var1) {
      super(var1);
   }

   void initialize() {
      super.initialize();
      this.setInsets();
      Insets var1 = this.getInsets();
      this.setScrollPosition(-var1.left, -var1.top);
   }

   public void setUnitIncrement(Adjustable var1, int var2) {
   }

   public Insets insets() {
      return this.getInsets();
   }

   private native void setInsets();

   public synchronized native void setScrollPosition(int var1, int var2);

   public int getHScrollbarHeight() {
      return this.scrollbarHeight;
   }

   private native int _getHScrollbarHeight();

   public int getVScrollbarWidth() {
      return this.scrollbarWidth;
   }

   private native int _getVScrollbarWidth();

   public Point getScrollOffset() {
      int var1 = this.getOffset(0);
      int var2 = this.getOffset(1);
      return new Point(var1, var2);
   }

   public void childResized(int var1, int var2) {
      ScrollPane var3 = (ScrollPane)this.target;
      Dimension var4 = var3.getSize();
      this.setSpans(var4.width, var4.height, var1, var2);
      this.setInsets();
   }

   synchronized native void setSpans(int var1, int var2, int var3, int var4);

   public void setValue(Adjustable var1, int var2) {
      Component var3 = this.getScrollChild();
      if (var3 != null) {
         Point var4 = var3.getLocation();
         switch(var1.getOrientation()) {
         case 0:
            this.setScrollPosition(var2, -var4.y);
            break;
         case 1:
            this.setScrollPosition(-var4.x, var2);
         }

      }
   }

   private Component getScrollChild() {
      ScrollPane var1 = (ScrollPane)this.target;
      Component var2 = null;

      try {
         var2 = var1.getComponent(0);
      } catch (ArrayIndexOutOfBoundsException var4) {
      }

      return var2;
   }

   private void postScrollEvent(int var1, int var2, int var3, boolean var4) {
      WScrollPanePeer.Adjustor var5 = new WScrollPanePeer.Adjustor(var1, var2, var3, var4);
      WToolkit.executeOnEventHandlerThread(new WScrollPanePeer.ScrollEvent(this.target, var5));
   }

   static {
      initIDs();
   }

   class Adjustor implements Runnable {
      int orient;
      int type;
      int pos;
      boolean isAdjusting;

      Adjustor(int var2, int var3, int var4, boolean var5) {
         this.orient = var2;
         this.type = var3;
         this.pos = var4;
         this.isAdjusting = var5;
      }

      public void run() {
         if (WScrollPanePeer.this.getScrollChild() != null) {
            ScrollPane var1 = (ScrollPane)WScrollPanePeer.this.target;
            ScrollPaneAdjustable var2 = null;
            if (this.orient == 1) {
               var2 = (ScrollPaneAdjustable)var1.getVAdjustable();
            } else if (this.orient == 0) {
               var2 = (ScrollPaneAdjustable)var1.getHAdjustable();
            } else if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
               WScrollPanePeer.log.fine("Assertion failed: unknown orient");
            }

            if (var2 != null) {
               int var3 = var2.getValue();
               switch(this.type) {
               case 1:
                  var3 += var2.getUnitIncrement();
                  break;
               case 2:
                  var3 -= var2.getUnitIncrement();
                  break;
               case 3:
                  var3 -= var2.getBlockIncrement();
                  break;
               case 4:
                  var3 += var2.getBlockIncrement();
                  break;
               case 5:
                  var3 = this.pos;
                  break;
               default:
                  if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE)) {
                     WScrollPanePeer.log.fine("Assertion failed: unknown type");
                  }

                  return;
               }

               var3 = Math.max(var2.getMinimum(), var3);
               var3 = Math.min(var2.getMaximum(), var3);
               var2.setValueIsAdjusting(this.isAdjusting);
               AWTAccessor.getScrollPaneAdjustableAccessor().setTypedValue(var2, var3, this.type);

               Object var4;
               for(var4 = WScrollPanePeer.this.getScrollChild(); var4 != null && !(((Component)var4).getPeer() instanceof com.frojasg1.sun.awt.windows.WComponentPeer); var4 = ((Component)var4).getParent()) {
               }

               if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINE) && var4 == null) {
                  WScrollPanePeer.log.fine("Assertion (hwAncestor != null) failed, couldn't find heavyweight ancestor of scroll pane child");
               }

               com.frojasg1.sun.awt.windows.WComponentPeer var5 = (WComponentPeer)((Component)var4).getPeer();
               var5.paintDamagedAreaImmediately();
            }
         }
      }
   }

   class ScrollEvent extends PeerEvent {
      ScrollEvent(Object var2, Runnable var3) {
         super(var2, var3, 0L);
      }

      public PeerEvent coalesceEvents(PeerEvent var1) {
         if (WScrollPanePeer.log.isLoggable(PlatformLogger.Level.FINEST)) {
            WScrollPanePeer.log.finest("ScrollEvent coalesced: " + var1);
         }

         return var1 instanceof WScrollPanePeer.ScrollEvent ? var1 : null;
      }
   }
}
