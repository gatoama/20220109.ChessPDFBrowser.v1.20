package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.peer.CanvasPeer;
import com.frojasg1.sun.awt.Graphics2Delegate;
import com.frojasg1.sun.awt.PaintEventDispatcher;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.windows.WComponentPeer;

//abstract class WCanvasPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements CanvasPeer {
abstract class WCanvasPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements CanvasPeer {
   private boolean eraseBackground;

   WCanvasPeer(Component var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      this.eraseBackground = !SunToolkit.getSunAwtNoerasebackground();
      boolean var1 = SunToolkit.getSunAwtErasebackgroundonresize();
      if (!PaintEventDispatcher.getPaintEventDispatcher().shouldDoNativeBackgroundErase((Component)this.target)) {
         this.eraseBackground = false;
      }

      this.setNativeBackgroundErase(this.eraseBackground, var1);
      super.initialize();
      Color var2 = ((Component)this.target).getBackground();
      if (var2 != null) {
         this.setBackground(var2);
      }

   }

   public void paint(Graphics var1) {
      Dimension var2 = ((Component)this.target).getSize();
      if (!(var1 instanceof Graphics2D) && !(var1 instanceof Graphics2Delegate)) {
         var1.setColor(((Component)this.target).getBackground());
         var1.fillRect(0, 0, var2.width, var2.height);
         var1.setColor(((Component)this.target).getForeground());
      } else {
         var1.clearRect(0, 0, var2.width, var2.height);
      }

      super.paint(var1);
   }

   public boolean shouldClearRectBeforePaint() {
      return this.eraseBackground;
   }

   void disableBackgroundErase() {
      this.eraseBackground = false;
      this.setNativeBackgroundErase(false, false);
   }

   private native void setNativeBackgroundErase(boolean var1, boolean var2);

   public GraphicsConfiguration getAppropriateGraphicsConfiguration(GraphicsConfiguration var1) {
      return var1;
   }
}
