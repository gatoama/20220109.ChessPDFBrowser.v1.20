package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxPeer;

//final class WCheckboxPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements CheckboxPeer {
abstract class WCheckboxPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements CheckboxPeer {
   public native void setState(boolean var1);

   public native void setCheckboxGroup(CheckboxGroup var1);

   public native void setLabel(String var1);

   private static native int getCheckMarkSize();

   public Dimension getMinimumSize() {
      String var1 = ((Checkbox)this.target).getLabel();
      int var2 = getCheckMarkSize();
      if (var1 == null) {
         var1 = "";
      }

      FontMetrics var3 = this.getFontMetrics(((Checkbox)this.target).getFont());
      return new Dimension(var3.stringWidth(var1) + var2 / 2 + var2, Math.max(var3.getHeight() + 8, var2));
   }

   public boolean isFocusable() {
      return true;
   }

   WCheckboxPeer(Checkbox var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      Checkbox var1 = (Checkbox)this.target;
      this.setState(var1.getState());
      this.setCheckboxGroup(var1.getCheckboxGroup());
      Color var2 = ((Component)this.target).getBackground();
      if (var2 != null) {
         this.setBackground(var2);
      }

      super.initialize();
   }

   public boolean shouldClearRectBeforePaint() {
      return false;
   }

   void handleAction(final boolean var1) {
      final Checkbox var2 = (Checkbox)this.target;
      WToolkit.executeOnEventHandlerThread(var2, new Runnable() {
         public void run() {
            CheckboxGroup var1x = var2.getCheckboxGroup();
            if (var1x == null || var2 != var1x.getSelectedCheckbox() || !var2.getState()) {
               var2.setState(var1);
               WCheckboxPeer.this.postEvent(new ItemEvent(var2, 701, var2.getLabel(), var1 ? 1 : 2));
            }
         }
      });
   }
}
