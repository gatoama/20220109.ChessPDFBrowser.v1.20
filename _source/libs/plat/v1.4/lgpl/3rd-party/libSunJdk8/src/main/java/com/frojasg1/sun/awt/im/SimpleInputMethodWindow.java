package com.frojasg1.sun.awt.im;

import com.frojasg1.sun.awt.im.InputContext;
import com.frojasg1.sun.awt.im.InputMethodWindow;

import java.awt.Frame;

public class SimpleInputMethodWindow extends Frame implements InputMethodWindow {
   com.frojasg1.sun.awt.im.InputContext inputContext = null;
   private static final long serialVersionUID = 5093376647036461555L;

   public SimpleInputMethodWindow(String var1, com.frojasg1.sun.awt.im.InputContext var2) {
      super(var1);
      if (var2 != null) {
         this.inputContext = var2;
      }

      this.setFocusableWindowState(false);
   }

   public void setInputContext(InputContext var1) {
      this.inputContext = var1;
   }

   public java.awt.im.InputContext getInputContext() {
      return (java.awt.im.InputContext)(this.inputContext != null ? this.inputContext : super.getInputContext());
   }
}
