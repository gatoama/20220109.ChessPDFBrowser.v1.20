package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ChildWindowListener implements WindowListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;

   ChildWindowListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.td = var1;
   }

   public void windowOpened(WindowEvent var1) {
   }

   public void windowClosing(WindowEvent var1) {
      this.td.setVisible(false);
      this.td.dispose();
   }

   public void windowClosed(WindowEvent var1) {
   }

   public void windowIconified(WindowEvent var1) {
   }

   public void windowDeiconified(WindowEvent var1) {
   }

   public void windowActivated(WindowEvent var1) {
   }

   public void windowDeactivated(WindowEvent var1) {
   }
}
