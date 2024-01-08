package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JList;

class EditPermButtonListener extends MouseAdapter implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;
   private boolean editPolicyEntry;

   EditPermButtonListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.editPolicyEntry = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      JList var2 = (JList)this.td.getComponent(8);
      int var3 = var2.getSelectedIndex();
      if (var3 < 0) {
         this.tw.displayErrorDialog(this.td, (Throwable)(new Exception(PolicyTool.getMessage("No.permission.selected"))));
      } else {
         this.td.displayPermissionDialog(this.editPolicyEntry, true);
      }
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() == 2) {
         this.actionPerformed((ActionEvent)null);
      }

   }
}
