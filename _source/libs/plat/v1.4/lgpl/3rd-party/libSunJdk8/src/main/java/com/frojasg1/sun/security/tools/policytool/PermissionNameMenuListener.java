package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PermissionNameMenuListener implements ItemListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;

   PermissionNameMenuListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.td = var1;
   }

   public void itemStateChanged(ItemEvent var1) {
      if (var1.getStateChange() != 2) {
         JComboBox var2 = (JComboBox)this.td.getComponent(3);
         var2.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)var1.getItem()));
         if (((String)var1.getItem()).indexOf(com.frojasg1.sun.security.tools.policytool.ToolDialog.PERM_NAME) == -1) {
            JTextField var3 = (JTextField)this.td.getComponent(4);
            var3.setText((String)var1.getItem());
         }
      }
   }
}
