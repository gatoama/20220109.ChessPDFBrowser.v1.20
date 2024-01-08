package com.frojasg1.sun.awt.windows;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.LayoutManager;
import java.awt.PrintJob;
import java.awt.Toolkit;
import java.awt.peer.ComponentPeer;
import java.awt.print.PrinterJob;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.windows.WPrintDialogPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

class WPrintDialog extends Dialog {
   protected PrintJob job;
   protected PrinterJob pjob;
   private boolean retval = false;

   WPrintDialog(Frame var1, PrinterJob var2) {
      super(var1, true);
      this.pjob = var2;
      this.setLayout((LayoutManager)null);
   }

   WPrintDialog(Dialog var1, PrinterJob var2) {
      super(var1, "", true);
      this.pjob = var2;
      this.setLayout((LayoutManager)null);
   }

   final void setPeer(ComponentPeer var1) {
      AWTAccessor.getComponentAccessor().setPeer(this, var1);
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         Container var2 = this.getParent();
         if (var2 != null && var2.getPeer() == null) {
            var2.addNotify();
         }

         if (this.getPeer() == null) {
            com.frojasg1.sun.awt.windows.WPrintDialogPeer var3 = ((WToolkit)Toolkit.getDefaultToolkit()).createWPrintDialog(this);
            this.setPeer(var3);
         }

         super.addNotify();
      }
   }

   final void setRetVal(boolean var1) {
      this.retval = var1;
   }

   final boolean getRetVal() {
      return this.retval;
   }

   private static native void initIDs();

   static {
      initIDs();
   }
}
