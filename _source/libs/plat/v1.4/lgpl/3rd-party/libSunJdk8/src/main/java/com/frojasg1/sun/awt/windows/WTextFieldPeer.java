package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WTextComponentPeer;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.im.InputMethodRequests;
import java.awt.peer.TextFieldPeer;

//final class WTextFieldPeer extends com.frojasg1.sun.awt.windows.WTextComponentPeer implements TextFieldPeer {
abstract class WTextFieldPeer extends com.frojasg1.sun.awt.windows.WTextComponentPeer implements TextFieldPeer {
   public Dimension getMinimumSize() {
      FontMetrics var1 = this.getFontMetrics(((TextField)this.target).getFont());
      return new Dimension(var1.stringWidth(this.getText()) + 24, var1.getHeight() + 8);
   }

   public boolean handleJavaKeyEvent(KeyEvent var1) {
      switch(var1.getID()) {
      case 400:
         if (var1.getKeyChar() == '\n' && !var1.isAltDown() && !var1.isControlDown()) {
            this.postEvent(new ActionEvent(this.target, 1001, this.getText(), var1.getWhen(), var1.getModifiers()));
            return true;
         }
      default:
         return false;
      }
   }

   public native void setEchoChar(char var1);

   public Dimension getPreferredSize(int var1) {
      return this.getMinimumSize(var1);
   }

   public Dimension getMinimumSize(int var1) {
      FontMetrics var2 = this.getFontMetrics(((TextField)this.target).getFont());
      return new Dimension(var2.charWidth('0') * var1 + 24, var2.getHeight() + 8);
   }

   public InputMethodRequests getInputMethodRequests() {
      return null;
   }

   WTextFieldPeer(TextField var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      TextField var1 = (TextField)this.target;
      if (var1.echoCharIsSet()) {
         this.setEchoChar(var1.getEchoChar());
      }

      super.initialize();
   }
}
