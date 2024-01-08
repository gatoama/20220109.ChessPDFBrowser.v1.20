package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WTextComponentPeer;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.TextArea;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextAreaPeer;

//final class WTextAreaPeer extends com.frojasg1.sun.awt.windows.WTextComponentPeer implements TextAreaPeer {
abstract class WTextAreaPeer extends com.frojasg1.sun.awt.windows.WTextComponentPeer implements TextAreaPeer {
   public Dimension getMinimumSize() {
      return this.getMinimumSize(10, 60);
   }

   public void insert(String var1, int var2) {
      this.replaceRange(var1, var2, var2);
   }

   public native void replaceRange(String var1, int var2, int var3);

   public Dimension getPreferredSize(int var1, int var2) {
      return this.getMinimumSize(var1, var2);
   }

   public Dimension getMinimumSize(int var1, int var2) {
      FontMetrics var3 = this.getFontMetrics(((TextArea)this.target).getFont());
      return new Dimension(var3.charWidth('0') * var2 + 20, var3.getHeight() * var1 + 20);
   }

   public InputMethodRequests getInputMethodRequests() {
      return null;
   }

   WTextAreaPeer(TextArea var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);
}
