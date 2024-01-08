package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class ErrorOKButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog ed;

   ErrorOKButtonListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.ed = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.ed.setVisible(false);
      this.ed.dispose();
   }
}
