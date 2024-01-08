package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveCancelButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog us;

   UserSaveCancelButtonListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.us = var1;
   }

   public void actionPerformed(ActionEvent var1) {
      this.us.setVisible(false);
      this.us.dispose();
   }
}
