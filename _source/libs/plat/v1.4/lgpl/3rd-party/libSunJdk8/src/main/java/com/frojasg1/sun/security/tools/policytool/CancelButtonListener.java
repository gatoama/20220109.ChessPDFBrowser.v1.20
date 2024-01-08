package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class CancelButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;

   CancelButtonListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.td = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.td.setVisible(false);
      this.td.dispose();
   }
}
