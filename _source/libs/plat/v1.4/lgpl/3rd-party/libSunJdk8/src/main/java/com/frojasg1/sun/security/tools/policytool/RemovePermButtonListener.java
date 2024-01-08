package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.TaggedList;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class RemovePermButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;
   private boolean edit;

   RemovePermButtonListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.edit = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      com.frojasg1.sun.security.tools.policytool.TaggedList var2 = (com.frojasg1.sun.security.tools.policytool.TaggedList)this.td.getComponent(8);
      int var3 = var2.getSelectedIndex();
      if (var3 < 0) {
         this.tw.displayErrorDialog(this.td, (Throwable)(new Exception(PolicyTool.getMessage("No.permission.selected"))));
      } else {
         var2.removeTaggedItem(var3);
      }
   }
}
