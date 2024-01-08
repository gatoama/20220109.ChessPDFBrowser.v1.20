package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class PolicyListListener extends MouseAdapter implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;

   PolicyListListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      com.frojasg1.sun.security.tools.policytool.ToolDialog var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(PolicyTool.getMessage("Policy.Entry"), this.tool, this.tw, true);
      var2.displayPolicyEntryDialog(true);
   }

   public void mouseClicked(MouseEvent var1) {
      if (var1.getClickCount() == 2) {
         this.actionPerformed((ActionEvent)null);
      }

   }
}
