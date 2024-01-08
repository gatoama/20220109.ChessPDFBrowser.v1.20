package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class AddPermButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;
   private boolean editPolicyEntry;

   AddPermButtonListener(PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.editPolicyEntry = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      this.td.displayPermissionDialog(this.editPolicyEntry, false);
   }
}
