package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.peer.PanelPeer;
import com.frojasg1.sun.awt.SunGraphicsCallback;
import com.frojasg1.sun.awt.windows.WCanvasPeer;
import com.frojasg1.sun.awt.windows.WColor;

//class WPanelPeer extends com.frojasg1.sun.awt.windows.WCanvasPeer implements PanelPeer {
abstract class WPanelPeer extends com.frojasg1.sun.awt.windows.WCanvasPeer implements PanelPeer {
   Insets insets_;

   public void paint(Graphics var1) {
      super.paint(var1);
      SunGraphicsCallback.PaintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), var1, 3);
   }

   public void print(Graphics var1) {
      super.print(var1);
      SunGraphicsCallback.PrintHeavyweightComponentsCallback.getInstance().runComponents(((Container)this.target).getComponents(), var1, 3);
   }

   public Insets getInsets() {
      return this.insets_;
   }

   private static native void initIDs();

   WPanelPeer(Component var1) {
      super(var1);
   }

   void initialize() {
      super.initialize();
      this.insets_ = new Insets(0, 0, 0, 0);
      Color var1 = ((Component)this.target).getBackground();
      if (var1 == null) {
         var1 = com.frojasg1.sun.awt.windows.WColor.getDefaultColor(1);
         ((Component)this.target).setBackground(var1);
         this.setBackground(var1);
      }

      var1 = ((Component)this.target).getForeground();
      if (var1 == null) {
         var1 = com.frojasg1.sun.awt.windows.WColor.getDefaultColor(2);
         ((Component)this.target).setForeground(var1);
         this.setForeground(var1);
      }

   }

   public Insets insets() {
      return this.getInsets();
   }

   static {
      initIDs();
   }
}
