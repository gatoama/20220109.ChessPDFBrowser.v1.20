package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class UserSaveNoButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog us;
   private int select;

   UserSaveNoButtonListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1, PolicyTool var2, com.frojasg1.sun.security.tools.policytool.ToolWindow var3, int var4) {
      this.us = var1;
      this.tool = var2;
      this.tw = var3;
      this.select = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      this.us.setVisible(false);
      this.us.dispose();
      this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
   }
}
