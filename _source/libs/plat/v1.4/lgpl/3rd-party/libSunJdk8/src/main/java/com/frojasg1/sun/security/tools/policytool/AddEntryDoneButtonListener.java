package com.frojasg1.sun.security.tools.policytool;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.security.PublicKey;
import java.text.MessageFormat;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import com.frojasg1.sun.security.provider.PolicyParser;
import com.frojasg1.sun.security.tools.policytool.PolicyEntry;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

class AddEntryDoneButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;
   private boolean edit;

   AddEntryDoneButtonListener(com.frojasg1.sun.security.tools.policytool.PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3, boolean var4) {
      this.tool = var1;
      this.tw = var2;
      this.td = var3;
      this.edit = var4;
   }

   public void actionPerformed(ActionEvent var1) {
      try {
         com.frojasg1.sun.security.tools.policytool.PolicyEntry var2 = this.td.getPolicyEntryFromDialog();
         PolicyParser.GrantEntry var3 = var2.getGrantEntry();
         int var5;
         if (var3.signedBy != null) {
            String[] var4 = this.tool.parseSigners(var3.signedBy);

            for(var5 = 0; var5 < var4.length; ++var5) {
               PublicKey var6 = this.tool.getPublicKeyAlias(var4[var5]);
               if (var6 == null) {
                  MessageFormat var7 = new MessageFormat(com.frojasg1.sun.security.tools.policytool.PolicyTool.getMessage("Warning.A.public.key.for.alias.signers.i.does.not.exist.Make.sure.a.KeyStore.is.properly.configured."));
                  Object[] var8 = new Object[]{var4[var5]};
                  this.tool.warnings.addElement(var7.format(var8));
                  this.tw.displayStatusDialog(this.td, var7.format(var8));
               }
            }
         }

         JList var10 = (JList)this.tw.getComponent(3);
         if (this.edit) {
            var5 = var10.getSelectedIndex();
            this.tool.addEntry(var2, var5);
            String var11 = var2.headerToString();
            if (PolicyTool.collator.compare(var11, var10.getModel().getElementAt(var5)) != 0) {
               this.tool.modified = true;
            }

            ((DefaultListModel)var10.getModel()).set(var5, var11);
         } else {
            this.tool.addEntry(var2, -1);
            ((DefaultListModel)var10.getModel()).addElement(var2.headerToString());
            this.tool.modified = true;
         }

         this.td.setVisible(false);
         this.td.dispose();
      } catch (Exception var9) {
         this.tw.displayErrorDialog(this.td, (Throwable)var9);
      }

   }
}
