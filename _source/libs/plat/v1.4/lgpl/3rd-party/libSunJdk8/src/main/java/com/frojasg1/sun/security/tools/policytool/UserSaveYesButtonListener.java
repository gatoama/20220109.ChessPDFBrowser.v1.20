package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class UserSaveYesButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog us;
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private int select;

   UserSaveYesButtonListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1, com.frojasg1.sun.security.tools.policytool.PolicyTool var2, com.frojasg1.sun.security.tools.policytool.ToolWindow var3, int var4) {
      this.us = var1;
      this.tool = var2;
      this.tw = var3;
      this.select = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      this.us.setVisible(false);
      this.us.dispose();

      try {
         String var2 = ((JTextField)this.tw.getComponent(1)).getText();
         if (var2 != null && !var2.equals("")) {
            this.tool.savePolicy(var2);
            MessageFormat var3 = new MessageFormat(PolicyTool.getMessage("Policy.successfully.written.to.filename"));
            Object[] var4 = new Object[]{var2};
            this.tw.displayStatusDialog((Window)null, var3.format(var4));
            this.us.userSaveContinue(this.tool, this.tw, this.us, this.select);
         } else {
            this.us.displaySaveAsDialog(this.select);
         }
      } catch (Exception var5) {
         this.tw.displayErrorDialog((Window)null, (Throwable)var5);
      }

   }
}
