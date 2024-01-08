package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.PolicyTool;
import com.frojasg1.sun.security.tools.policytool.Prin;
import com.frojasg1.sun.security.tools.policytool.ToolDialog;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.JComboBox;
import javax.swing.JTextField;

class PrincipalTypeMenuListener implements ItemListener {
   private com.frojasg1.sun.security.tools.policytool.ToolDialog td;

   PrincipalTypeMenuListener(com.frojasg1.sun.security.tools.policytool.ToolDialog var1) {
      this.td = var1;
   }

   public void itemStateChanged(ItemEvent var1) {
      if (var1.getStateChange() != 2) {
         JComboBox var2 = (JComboBox)this.td.getComponent(1);
         JTextField var3 = (JTextField)this.td.getComponent(2);
         JTextField var4 = (JTextField)this.td.getComponent(4);
         var2.getAccessibleContext().setAccessibleName(PolicyTool.splitToWords((String)var1.getItem()));
         com.frojasg1.sun.security.tools.policytool.Prin var5;
         if (((String)var1.getItem()).equals(com.frojasg1.sun.security.tools.policytool.ToolDialog.PRIN_TYPE)) {
            if (var3.getText() != null && var3.getText().length() > 0) {
               var5 = com.frojasg1.sun.security.tools.policytool.ToolDialog.getPrin(var3.getText(), true);
               var2.setSelectedItem(var5.CLASS);
            }

         } else {
            if (var3.getText().indexOf((String)var1.getItem()) == -1) {
               var4.setText("");
            }

            var5 = com.frojasg1.sun.security.tools.policytool.ToolDialog.getPrin((String)var1.getItem(), false);
            if (var5 != null) {
               var3.setText(var5.FULL_CLASS);
            }

         }
      }
   }
}
