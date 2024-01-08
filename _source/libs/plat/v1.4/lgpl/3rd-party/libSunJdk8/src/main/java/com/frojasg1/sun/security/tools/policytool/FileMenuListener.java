package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.MessageFormat;
import javax.swing.JTextField;

class FileMenuListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;

   FileMenuListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2) {
      this.tool = var1;
      this.tw = var2;
   }

   public void actionPerformed(ActionEvent var1) {
      com.frojasg1.sun.security.tools.policytool.ToolDialog var2;
      if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Exit") == 0) {
         var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
         var2.displayUserSave(1);
      } else if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "New") == 0) {
         var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
         var2.displayUserSave(2);
      } else if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Open") == 0) {
         var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Save.Changes"), this.tool, this.tw, true);
         var2.displayUserSave(3);
      } else if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Save") == 0) {
         String var7 = ((JTextField)this.tw.getComponent(1)).getText();
         if (var7 != null && var7.length() != 0) {
            try {
               this.tool.savePolicy(var7);
               MessageFormat var8 = new MessageFormat(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Policy.successfully.written.to.filename"));
               Object[] var4 = new Object[]{var7};
               this.tw.displayStatusDialog((Window)null, var8.format(var4));
            } catch (FileNotFoundException var5) {
               if (var7 != null && !var7.equals("")) {
                  this.tw.displayErrorDialog((Window)null, (Throwable)var5);
               } else {
                  this.tw.displayErrorDialog((Window)null, (Throwable)(new FileNotFoundException(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("null.filename"))));
               }
            } catch (Exception var6) {
               this.tw.displayErrorDialog((Window)null, (Throwable)var6);
            }
         } else {
            com.frojasg1.sun.security.tools.policytool.ToolDialog var3 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Save.As"), this.tool, this.tw, true);
            var3.displaySaveAsDialog(0);
         }
      } else if (com.frojasg1.sun.security.tools.policytool.PolicyTool.collator.compare(var1.getActionCommand(), "Save.As") == 0) {
         var2 = new com.frojasg1.sun.security.tools.policytool.ToolDialog(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Save.As"), this.tool, this.tw, true);
         var2.displaySaveAsDialog(0);
      } else if (PolicyTool.collator.compare(var1.getActionCommand(), "View.Warning.Log") == 0) {
         this.tw.displayWarningLog((Window)null);
      }

   }
}
