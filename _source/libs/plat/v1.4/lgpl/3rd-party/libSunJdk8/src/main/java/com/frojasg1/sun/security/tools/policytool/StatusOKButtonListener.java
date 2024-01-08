package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class StatusOKButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog sd;

   StatusOKButtonListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.sd = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.sd.setVisible(false);
      this.sd.dispose();
   }
}
