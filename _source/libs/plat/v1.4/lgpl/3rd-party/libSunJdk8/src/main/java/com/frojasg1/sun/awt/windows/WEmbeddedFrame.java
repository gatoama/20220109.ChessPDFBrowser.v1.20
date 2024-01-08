package com.frojasg1.sun.awt.windows;

import java.awt.AWTKeyStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.InvocationEvent;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.frojasg1.sun.awt.EmbeddedFrame;
import com.frojasg1.sun.awt.image.ByteInterleavedRaster;
import com.frojasg1.sun.awt.windows.WEmbeddedFramePeer;
import com.frojasg1.sun.awt.windows.WFramePeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.security.action.GetPropertyAction;

public class WEmbeddedFrame extends EmbeddedFrame {
   private long handle;
   private int bandWidth;
   private int bandHeight;
   private int imgWid;
   private int imgHgt;
   private static int pScale;
   private static final int MAX_BAND_SIZE = 30720;
   private boolean isEmbeddedInIE;
   private static String printScale;

   public WEmbeddedFrame() {
      this(0L);
   }

   /** @deprecated */
   @Deprecated
   public WEmbeddedFrame(int var1) {
      this((long)var1);
   }

   public WEmbeddedFrame(long var1) {
      this.bandWidth = 0;
      this.bandHeight = 0;
      this.imgWid = 0;
      this.imgHgt = 0;
      this.isEmbeddedInIE = false;
      this.handle = var1;
      if (var1 != 0L) {
         this.addNotify();
         this.show();
      }

   }

   public void addNotify() {
      if (this.getPeer() == null) {
         com.frojasg1.sun.awt.windows.WToolkit var1 = (com.frojasg1.sun.awt.windows.WToolkit)Toolkit.getDefaultToolkit();
         this.setPeer(var1.createEmbeddedFrame(this));
      }

      super.addNotify();
   }

   public long getEmbedderHandle() {
      return this.handle;
   }

   void print(long var1) {
      BufferedImage var3 = null;
      int var4 = 1;
      int var5 = 1;
      if (this.isPrinterDC(var1)) {
         var4 = var5 = getPrintScaleFactor();
      }

      int var6 = this.getHeight();
      if (var3 == null) {
         this.bandWidth = this.getWidth();
         if (this.bandWidth % 4 != 0) {
            this.bandWidth += 4 - this.bandWidth % 4;
         }

         if (this.bandWidth <= 0) {
            return;
         }

         this.bandHeight = Math.min(30720 / this.bandWidth, var6);
         this.imgWid = this.bandWidth * var4;
         this.imgHgt = this.bandHeight * var5;
         var3 = new BufferedImage(this.imgWid, this.imgHgt, 5);
      }

      Graphics var7 = var3.getGraphics();
      var7.setColor(Color.white);
      Graphics2D var8 = (Graphics2D)var3.getGraphics();
      var8.translate(0, this.imgHgt);
      var8.scale((double)var4, (double)(-var5));
      ByteInterleavedRaster var9 = (ByteInterleavedRaster)var3.getRaster();
      byte[] var10 = var9.getDataStorage();

      for(int var11 = 0; var11 < var6; var11 += this.bandHeight) {
         var7.fillRect(0, 0, this.bandWidth, this.bandHeight);
         this.printComponents(var8);
         int var12 = 0;
         int var13 = this.bandHeight;
         int var14 = this.imgHgt;
         if (var11 + this.bandHeight > var6) {
            var13 = var6 - var11;
            var14 = var13 * var5;
            var12 = this.imgWid * (this.imgHgt - var14) * 3;
         }

         this.printBand(var1, var10, var12, 0, 0, this.imgWid, var14, 0, var11, this.bandWidth, var13);
         var8.translate(0, -this.bandHeight);
      }

   }

   protected static int getPrintScaleFactor() {
      if (pScale != 0) {
         return pScale;
      } else {
         if (printScale == null) {
            printScale = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
               public String run() {
                  return System.getenv("JAVA2D_PLUGIN_PRINT_SCALE");
               }
            });
         }

         byte var0 = 4;
         int var1 = var0;
         if (printScale != null) {
            try {
               var1 = Integer.parseInt(printScale);
               if (var1 > 8 || var1 < 1) {
                  var1 = var0;
               }
            } catch (NumberFormatException var3) {
            }
         }

         pScale = var1;
         return pScale;
      }
   }

   private native boolean isPrinterDC(long var1);

   private native void printBand(long var1, byte[] var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12);

   private static native void initIDs();

   public void activateEmbeddingTopLevel() {
   }

   public void synthesizeWindowActivation(boolean var1) {
      if (var1 && !EventQueue.isDispatchThread()) {
         Runnable var2 = new Runnable() {
            public void run() {
               ((com.frojasg1.sun.awt.windows.WFramePeer)WEmbeddedFrame.this.getPeer()).emulateActivation(true);
            }
         };
         com.frojasg1.sun.awt.windows.WToolkit.postEvent(com.frojasg1.sun.awt.windows.WToolkit.targetToAppContext(this), new InvocationEvent(this, var2));
      } else {
         ((com.frojasg1.sun.awt.windows.WFramePeer)this.getPeer()).emulateActivation(var1);
      }

   }

   public void registerAccelerator(AWTKeyStroke var1) {
   }

   public void unregisterAccelerator(AWTKeyStroke var1) {
   }

   public void notifyModalBlocked(Dialog var1, boolean var2) {
      try {
         ComponentPeer var3 = (ComponentPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(this);
         ComponentPeer var4 = (ComponentPeer) WToolkit.targetToPeer(var1);
         this.notifyModalBlockedImpl((com.frojasg1.sun.awt.windows.WEmbeddedFramePeer)var3, (com.frojasg1.sun.awt.windows.WWindowPeer)var4, var2);
      } catch (Exception var5) {
         var5.printStackTrace(System.err);
      }

   }

   native void notifyModalBlockedImpl(WEmbeddedFramePeer var1, WWindowPeer var2, boolean var3);

   static {
      initIDs();
      pScale = 0;
      printScale = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.pluginscalefactor"));
   }
}
