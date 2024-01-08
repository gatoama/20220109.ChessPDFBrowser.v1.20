package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.peer.TextComponentPeer;

abstract class WTextComponentPeer extends WComponentPeer implements TextComponentPeer {
   public void setEditable(boolean var1) {
      this.enableEditing(var1);
      this.setBackground(((TextComponent)this.target).getBackground());
   }

   public native String getText();

   public native void setText(String var1);

   public native int getSelectionStart();

   public native int getSelectionEnd();

   public native void select(int var1, int var2);

   WTextComponentPeer(TextComponent var1) {
      super(var1);
   }

   void initialize() {
      TextComponent var1 = (TextComponent)this.target;
      String var2 = var1.getText();
      if (var2 != null) {
         this.setText(var2);
      }

      this.select(var1.getSelectionStart(), var1.getSelectionEnd());
      this.setEditable(var1.isEditable());
      super.initialize();
   }

   native void enableEditing(boolean var1);

   public boolean isFocusable() {
      return true;
   }

   public void setCaretPosition(int var1) {
      this.select(var1, var1);
   }

   public int getCaretPosition() {
      return this.getSelectionStart();
   }

   public void valueChanged() {
      this.postEvent(new TextEvent(this.target, 900));
   }

   private static native void initIDs();

   public boolean shouldClearRectBeforePaint() {
      return false;
   }

   static {
      initIDs();
   }
}
