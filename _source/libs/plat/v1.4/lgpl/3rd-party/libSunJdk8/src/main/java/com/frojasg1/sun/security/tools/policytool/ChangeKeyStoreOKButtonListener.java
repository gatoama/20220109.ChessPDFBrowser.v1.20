package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import javax.swing.JTextField;

class ChangeKeyStoreOKButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;

   ChangeKeyStoreOKButtonListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
   }

   public void actionPerformed(ActionEvent var1) {
      String var2 = ((JTextField)this.td.getComponent(1)).getText().trim();
      String var3 = ((JTextField)this.td.getComponent(3)).getText().trim();
      String var4 = ((JTextField)this.td.getComponent(5)).getText().trim();
      String var5 = ((JTextField)this.td.getComponent(7)).getText().trim();

      try {
         this.tool.openKeyStore(var2.length() == 0 ? null : var2, var3.length() == 0 ? null : var3, var4.length() == 0 ? null : var4, var5.length() == 0 ? null : var5);
         this.tool.modified = true;
      } catch (Exception var9) {
         MessageFormat var7 = new MessageFormat(PolicyTool.getMessage("Unable.to.open.KeyStore.ex.toString."));
         Object[] var8 = new Object[]{var9.toString()};
         this.tw.displayErrorDialog(this.td, (String)var7.format(var8));
         return;
      }

      this.td.dispose();
   }
}
