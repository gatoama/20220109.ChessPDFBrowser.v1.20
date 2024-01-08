package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Label;
import java.awt.peer.LabelPeer;

//final class WLabelPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements LabelPeer {
abstract class WLabelPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements LabelPeer {
   public Dimension getMinimumSize() {
      FontMetrics var1 = this.getFontMetrics(((Label)this.target).getFont());
      String var2 = ((Label)this.target).getText();
      if (var2 == null) {
         var2 = "";
      }

      return new Dimension(var1.stringWidth(var2) + 14, var1.getHeight() + 8);
   }

   native void lazyPaint();

   synchronized void start() {
      super.start();
      this.lazyPaint();
   }

   public boolean shouldClearRectBeforePaint() {
      return false;
   }

   public native void setText(String var1);

   public native void setAlignment(int var1);

   WLabelPeer(Label var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      Label var1 = (Label)this.target;
      String var2 = var1.getText();
      if (var2 != null) {
         this.setText(var2);
      }

      int var3 = var1.getAlignment();
      if (var3 != 0) {
         this.setAlignment(var3);
      }

      Color var4 = ((Component)this.target).getBackground();
      if (var4 != null) {
         this.setBackground(var4);
      }

      super.initialize();
   }
}
