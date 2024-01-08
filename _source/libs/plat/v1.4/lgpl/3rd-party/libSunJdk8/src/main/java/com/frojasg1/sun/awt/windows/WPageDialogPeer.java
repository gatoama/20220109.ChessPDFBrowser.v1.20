package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WPageDialog;
import com.frojasg1.sun.awt.windows.WPrintDialog;
import com.frojasg1.sun.awt.windows.WPrintDialogPeer;

//final class WPageDialogPeer extends com.frojasg1.sun.awt.windows.WPrintDialogPeer {
abstract class WPageDialogPeer extends com.frojasg1.sun.awt.windows.WPrintDialogPeer {
   WPageDialogPeer(com.frojasg1.sun.awt.windows.WPageDialog var1) {
      super(var1);
   }

   private native boolean _show();

   public void show() {
      (new Thread(new Runnable() {
         public void run() {
            try {
               ((com.frojasg1.sun.awt.windows.WPrintDialog)WPageDialogPeer.this.target).setRetVal(WPageDialogPeer.this._show());
            } catch (Exception var2) {
            }

            ((com.frojasg1.sun.awt.windows.WPrintDialog)WPageDialogPeer.this.target).setVisible(false);
         }
      })).start();
   }
}
