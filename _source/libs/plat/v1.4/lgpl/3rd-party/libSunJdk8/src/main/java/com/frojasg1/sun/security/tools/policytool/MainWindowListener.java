package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JList;

class MainWindowListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;

   MainWindowListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      com.frojasg1.sun.security.tools.policytool.ToolDialog var2;
      if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Add.Policy.Entry") == 0) {
         var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
         var2.displayPolicyEntryDialog(false);
      } else {
         int var3;
         com.frojasg1.sun.security.tools.policytool.ToolDialog var4;
         JList var5;
         if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Remove.Policy.Entry") == 0) {
            var5 = (JList)this.tw.getComponent(3);
            var3 = var5.getSelectedIndex();
            if (var3 < 0) {
               this.tw.displayErrorDialog((Window)null, (Throwable)(new Exception(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("No.Policy.Entry.selected"))));
               return;
            }

            var4 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Remove.Policy.Entry"), this.tool, this.tw, true);
            var4.displayConfirmRemovePolicyEntry();
         } else if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Edit.Policy.Entry") == 0) {
            var5 = (JList)this.tw.getComponent(3);
            var3 = var5.getSelectedIndex();
            if (var3 < 0) {
               this.tw.displayErrorDialog((Window)null, (Throwable)(new Exception(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("No.Policy.Entry.selected"))));
               return;
            }

            var4 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
            var4.displayPolicyEntryDialog(true);
         } else if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Edit") == 0) {
            var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(PolicyTool.getMessage("KeyStore"), this.tool, this.tw, true);
            var2.keyStoreDialog(0);
         }
      }

   }
}
