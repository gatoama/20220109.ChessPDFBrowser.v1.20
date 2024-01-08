package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WMenuBarPeer;
import com.frojasg1.sun.awt.windows.WMenuItemPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuContainer;
import java.awt.MenuItem;
import java.awt.peer.MenuPeer;

class WMenuPeer extends com.frojasg1.sun.awt.windows.WMenuItemPeer implements MenuPeer {
   public native void addSeparator();

   public void addItem(MenuItem var1) {
      com.frojasg1.sun.awt.windows.WMenuItemPeer var2 = (com.frojasg1.sun.awt.windows.WMenuItemPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var1);
   }

   public native void delItem(int var1);

   WMenuPeer() {
   }

   WMenuPeer(Menu var1) {
      this.target = var1;
      MenuContainer var2 = var1.getParent();
      if (var2 instanceof MenuBar) {
         com.frojasg1.sun.awt.windows.WMenuBarPeer var3 = (com.frojasg1.sun.awt.windows.WMenuBarPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var2);
         this.parent = var3;
         var3.addChildPeer(this);
         this.createMenu(var3);
      } else {
         if (!(var2 instanceof Menu)) {
            throw new IllegalArgumentException("unknown menu container class");
         }

         this.parent = (WMenuPeer) WToolkit.targetToPeer(var2);
         this.parent.addChildPeer(this);
         this.createSubMenu(this.parent);
      }

      this.checkMenuCreation();
   }

   native void createMenu(com.frojasg1.sun.awt.windows.WMenuBarPeer var1);

   native void createSubMenu(WMenuPeer var1);
}
