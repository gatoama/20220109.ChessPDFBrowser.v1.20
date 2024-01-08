package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyEntry;
import com.frojasg1.sun.security.tools.policytool.PolicyListListener;
import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;
import com.frojasg1.sun.security.tools.policytool.ToolWindow;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.DefaultListModel;
import javax.swing.JList;

class ConfirmRemovePolicyEntryOKButtonListener implements ActionListener {
   private com.frojasg1.sun.security.tools.policytool.PolicyTool tool;
   private com.frojasg1.sun.security.tools.policytool.ToolWindow tw;
   private com.frojasg1.sun.security.tools.policytool.ToolDialog us;

   ConfirmRemovePolicyEntryOKButtonListener(PolicyTool var1, com.frojasg1.sun.security.tools.policytool.ToolWindow var2, com.frojasg1.sun.security.tools.policytool.ToolDialog var3) {
      this.tool = var1;
      this.tw = var2;
      this.us = var3;
   }

   public void actionPerformed(ActionEvent var1) {
      JList var2 = (JList)this.tw.getComponent(3);
      int var3 = var2.getSelectedIndex();
      com.frojasg1.sun.security.tools.policytool.PolicyEntry[] var4 = this.tool.getEntry();
      this.tool.removeEntry(var4[var3]);
      DefaultListModel var5 = new DefaultListModel();
      var2 = new JList(var5);
      var2.setVisibleRowCount(15);
      var2.setSelectionMode(0);
      var2.addMouseListener(new com.frojasg1.sun.security.tools.policytool.PolicyListListener(this.tool, this.tw));
      var4 = this.tool.getEntry();
      if (var4 != null) {
         for(int var6 = 0; var6 < var4.length; ++var6) {
            var5.addElement(var4[var6].headerToString());
         }
      }

      this.tw.replacePolicyList(var2);
      this.us.setVisible(false);
      this.us.dispose();
   }
}
