package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WMenuItemPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.CheckboxMenuItem;
import java.awt.event.ItemEvent;
import java.awt.peer.CheckboxMenuItemPeer;

final class WCheckboxMenuItemPeer extends com.frojasg1.sun.awt.windows.WMenuItemPeer implements CheckboxMenuItemPeer {
   public native void setState(boolean var1);

   WCheckboxMenuItemPeer(CheckboxMenuItem var1) {
      super(var1, true);
      this.setState(var1.getState());
   }

   public void handleAction(final boolean var1) {
      final CheckboxMenuItem var2 = (CheckboxMenuItem)this.target;
      WToolkit.executeOnEventHandlerThread(var2, new Runnable() {
         public void run() {
            var2.setState(var1);
            WCheckboxMenuItemPeer.this.postEvent(new ItemEvent(var2, 701, var2.getLabel(), var1 ? 1 : 2));
         }
      });
   }
}
