package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

class ToolWindowListener implements WindowListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;

   ToolWindowListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void windowOpened(WindowEvent var1) {
   }

   public void windowClosing(WindowEvent var1) {
      com.frojasg1.sun.security.tools.policytool.ToolDialog var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
      var2.displayUserSave(1);
   }

   public void windowClosed(WindowEvent var1) {
      System.exit(0);
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
