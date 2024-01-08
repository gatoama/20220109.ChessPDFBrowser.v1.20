package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.TrayIcon;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import java.awt.peer.TrayIconPeer;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.image.IntegerComponentRaster;
import com.frojasg1.sun.awt.windows.WObjectPeer;
import com.frojasg1.sun.awt.windows.WPopupMenuPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

//final class WTrayIconPeer extends com.frojasg1.sun.awt.windows.WObjectPeer implements TrayIconPeer {
abstract class WTrayIconPeer extends com.frojasg1.sun.awt.windows.WObjectPeer implements TrayIconPeer {
   static final int TRAY_ICON_WIDTH = 16;
   static final int TRAY_ICON_HEIGHT = 16;
   static final int TRAY_ICON_MASK_SIZE = 32;
   WTrayIconPeer.IconObserver observer = new WTrayIconPeer.IconObserver();
   boolean firstUpdate = true;
   Frame popupParent = new Frame("PopupMessageWindow");
   PopupMenu popup;

   protected void disposeImpl() {
      if (this.popupParent != null) {
         this.popupParent.dispose();
      }

      this.popupParent.dispose();
      this._dispose();
      com.frojasg1.sun.awt.windows.WToolkit.targetDisposedPeer(this.target, this);
   }

   WTrayIconPeer(TrayIcon var1) {
      this.target = var1;
      this.popupParent.addNotify();
      this.create();
      this.updateImage();
   }

   public void updateImage() {
      Image var1 = ((TrayIcon)this.target).getImage();
      if (var1 != null) {
         this.updateNativeImage(var1);
      }

   }

   public native void setToolTip(String var1);

   public synchronized void showPopupMenu(final int var1, final int var2) {
      if (!this.isDisposed()) {
         SunToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
            public void run() {
               PopupMenu var1x = ((TrayIcon)WTrayIconPeer.this.target).getPopupMenu();
               if (WTrayIconPeer.this.popup != var1x) {
                  if (WTrayIconPeer.this.popup != null) {
                     WTrayIconPeer.this.popupParent.remove(WTrayIconPeer.this.popup);
                  }

                  if (var1x != null) {
                     WTrayIconPeer.this.popupParent.add(var1x);
                  }

                  WTrayIconPeer.this.popup = var1x;
               }

               if (WTrayIconPeer.this.popup != null) {
                  ((com.frojasg1.sun.awt.windows.WPopupMenuPeer)WTrayIconPeer.this.popup.getPeer()).show(WTrayIconPeer.this.popupParent, new Point(var1, var2));
               }

            }
         });
      }
   }

   public void displayMessage(String var1, String var2, String var3) {
      if (var1 == null) {
         var1 = "";
      }

      if (var2 == null) {
         var2 = "";
      }

      this._displayMessage(var1, var2, var3);
   }

   synchronized void updateNativeImage(Image var1) {
      if (!this.isDisposed()) {
         boolean var2 = ((TrayIcon)this.target).isImageAutoSize();
         BufferedImage var3 = new BufferedImage(16, 16, 2);
         Graphics2D var4 = var3.createGraphics();
         if (var4 != null) {
            try {
               var4.setPaintMode();
               var4.drawImage(var1, 0, 0, var2 ? 16 : var1.getWidth(this.observer), var2 ? 16 : var1.getHeight(this.observer), this.observer);
               this.createNativeImage(var3);
               this.updateNativeIcon(!this.firstUpdate);
               if (this.firstUpdate) {
                  this.firstUpdate = false;
               }
            } finally {
               var4.dispose();
            }
         }

      }
   }

   void createNativeImage(BufferedImage var1) {
      WritableRaster var2 = var1.getRaster();
      byte[] var3 = new byte[32];
      int[] var4 = ((DataBufferInt)var2.getDataBuffer()).getData();
      int var5 = var4.length;
      int var6 = var2.getWidth();

      for(int var7 = 0; var7 < var5; ++var7) {
         int var8 = var7 / 8;
         int var9 = 1 << 7 - var7 % 8;
         if ((var4[var7] & -16777216) == 0 && var8 < var3.length) {
            var3[var8] = (byte)(var3[var8] | var9);
         }
      }

      if (var2 instanceof IntegerComponentRaster) {
         var6 = ((IntegerComponentRaster)var2).getScanlineStride();
      }

      this.setNativeIcon(((DataBufferInt)var1.getRaster().getDataBuffer()).getData(), var3, var6, var2.getWidth(), var2.getHeight());
   }

   void postEvent(AWTEvent var1) {
      com.frojasg1.sun.awt.windows.WToolkit.postEvent(WToolkit.targetToAppContext(this.target), var1);
   }

   native void create();

   synchronized native void _dispose();

   native void updateNativeIcon(boolean var1);

   native void setNativeIcon(int[] var1, byte[] var2, int var3, int var4, int var5);

   native void _displayMessage(String var1, String var2, String var3);

   class IconObserver implements ImageObserver {
      IconObserver() {
      }

      public boolean imageUpdate(Image var1, int var2, int var3, int var4, int var5, int var6) {
         if (var1 == ((TrayIcon)WTrayIconPeer.this.target).getImage() && !WTrayIconPeer.this.isDisposed()) {
            if ((var2 & 51) != 0) {
               WTrayIconPeer.this.updateNativeImage(var1);
            }

            return (var2 & 32) == 0;
         } else {
            return false;
         }
      }
   }
}
