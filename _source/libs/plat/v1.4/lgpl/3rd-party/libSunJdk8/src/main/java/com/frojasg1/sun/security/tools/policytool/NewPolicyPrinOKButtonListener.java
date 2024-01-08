package com.frojasg1.sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;
import com.frojasg1.sun.security.provider.PolicyParser;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.TaggedList;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

class NewPolicyPrinOKButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog listDialog;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog infoDialog;
   private boolean edit;

   NewPolicyPrinOKButtonListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3, com.frojasg1.sun.security.tools.policytool.ToolDialog var4, boolean var5) {
      this.tool = var1;
      this.tw = var2;
      this.listDialog = var3;
      this.infoDialog = var4;
      this.edit = var5;
   }

   public void actionPerformed(ActionEvent var1) {
      try {
         PolicyParser.PrincipalEntry var2 = this.infoDialog.getPrinFromDialog();
         if (var2 != null) {
            try {
               this.tool.verifyPrincipal(var2.getPrincipalClass(), var2.getPrincipalName());
            } catch (ClassNotFoundException var6) {
               MessageFormat var4 = new MessageFormat(PolicyTool.getMessage("Warning.Class.not.found.class"));
               Object[] var5 = new Object[]{var2.getPrincipalClass()};
               this.tool.warnings.addElement(var4.format(var5));
               this.tw.displayStatusDialog(this.infoDialog, var4.format(var5));
            }

            com.frojasg1.sun.security.tools.policytool.TaggedList var3 = (com.frojasg1.sun.security.tools.policytool.TaggedList)this.listDialog.getComponent(6);
            String var8 = com.frojasg1.sun.security.tools.policytool.ToolDialog.PrincipalEntryToUserFriendlyString(var2);
            if (this.edit) {
               int var9 = var3.getSelectedIndex();
               var3.replaceTaggedItem(var8, var2, var9);
            } else {
               var3.addTaggedItem(var8, var2);
            }
         }

         this.infoDialog.dispose();
      } catch (Exception var7) {
         this.tw.displayErrorDialog(this.infoDialog, (Throwable)var7);
      }

   }
}
