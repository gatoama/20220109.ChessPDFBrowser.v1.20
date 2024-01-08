package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WPageDialogPeer;
import com.frojasg1.sun.awt.windows.WPrintDialog;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterJob;

final class WPageDialog extends com.frojasg1.sun.awt.windows.WPrintDialog {
   PageFormat page;
   Printable painter;

   WPageDialog(Frame var1, PrinterJob var2, PageFormat var3, Printable var4) {
      super(var1, var2);
      this.page = var3;
      this.painter = var4;
   }

   WPageDialog(Dialog var1, PrinterJob var2, PageFormat var3, Printable var4) {
      super(var1, var2);
      this.page = var3;
      this.painter = var4;
   }

   public void addNotify() {
      synchronized(this.getTreeLock()) {
         Container var2 = this.getParent();
         if (var2 != null && var2.getPeer() == null) {
            var2.addNotify();
         }

         if (this.getPeer() == null) {
            com.frojasg1.sun.awt.windows.WPageDialogPeer var3 = ((WToolkit)Toolkit.getDefaultToolkit()).createWPageDialog(this);
            this.setPeer(var3);
         }

         super.addNotify();
      }
   }

   private static native void initIDs();

   static {
      initIDs();
   }
}
