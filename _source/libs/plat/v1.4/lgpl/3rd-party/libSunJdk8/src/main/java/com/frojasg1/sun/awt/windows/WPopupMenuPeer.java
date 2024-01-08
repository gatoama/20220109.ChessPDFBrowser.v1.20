package com.frojasg1.sun.awt.windows;

import java.awt.Component;
import java.awt.Container;
import java.awt.Event;
import java.awt.MenuContainer;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.peer.PopupMenuPeer;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WMenuPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

final class WPopupMenuPeer extends com.frojasg1.sun.awt.windows.WMenuPeer implements PopupMenuPeer {
   WPopupMenuPeer(PopupMenu var1) {
      this.target = var1;
      MenuContainer var2 = null;
      boolean var3 = AWTAccessor.getPopupMenuAccessor().isTrayIconPopup(var1);
      if (var3) {
         var2 = AWTAccessor.getMenuComponentAccessor().getParent(var1);
      } else {
         var2 = var1.getParent();
      }

      if (var2 instanceof Component) {
         com.frojasg1.sun.awt.windows.WComponentPeer var4 = (com.frojasg1.sun.awt.windows.WComponentPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var2);
         if (var4 == null) {
            Container var5 = com.frojasg1.sun.awt.windows.WToolkit.getNativeContainer((Component)var2);
            var4 = (com.frojasg1.sun.awt.windows.WComponentPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var5);
         }

         var4.addChildPeer(this);
         this.createMenu(var4);
         this.checkMenuCreation();
      } else {
         throw new IllegalArgumentException("illegal popup menu container class");
      }
   }

   private native void createMenu(com.frojasg1.sun.awt.windows.WComponentPeer var1);

   public void show(Event var1) {
      Component var2 = (Component)var1.target;
      com.frojasg1.sun.awt.windows.WComponentPeer var3 = (com.frojasg1.sun.awt.windows.WComponentPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var2);
      if (var3 == null) {
         Container var4 = com.frojasg1.sun.awt.windows.WToolkit.getNativeContainer(var2);
         var1.target = var4;

         for(Object var5 = var2; var5 != var4; var5 = ((Component)var5).getParent()) {
            Point var6 = ((Component)var5).getLocation();
            var1.x += var6.x;
            var1.y += var6.y;
         }
      }

      this._show(var1);
   }

   void show(Component var1, Point var2) {
      com.frojasg1.sun.awt.windows.WComponentPeer var3 = (WComponentPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var1);
      Event var4 = new Event(var1, 0L, 501, var2.x, var2.y, 0, 0);
      if (var3 == null) {
         Container var5 = WToolkit.getNativeContainer(var1);
         var4.target = var5;
      }

      var4.x = var2.x;
      var4.y = var2.y;
      this._show(var4);
   }

   private native void _show(Event var1);
}
