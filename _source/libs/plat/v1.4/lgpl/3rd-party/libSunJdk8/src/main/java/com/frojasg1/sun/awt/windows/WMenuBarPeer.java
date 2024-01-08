package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WFramePeer;
import com.frojasg1.sun.awt.windows.WMenuPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.peer.MenuBarPeer;

final class WMenuBarPeer extends com.frojasg1.sun.awt.windows.WMenuPeer implements MenuBarPeer {
   final com.frojasg1.sun.awt.windows.WFramePeer framePeer;

   public native void addMenu(Menu var1);

   public native void delMenu(int var1);

   public void addHelpMenu(Menu var1) {
      this.addMenu(var1);
   }

   WMenuBarPeer(MenuBar var1) {
      this.target = var1;
      this.framePeer = (com.frojasg1.sun.awt.windows.WFramePeer) WToolkit.targetToPeer(var1.getParent());
      if (this.framePeer != null) {
         this.framePeer.addChildPeer(this);
      }

      this.create(this.framePeer);
      this.checkMenuCreation();
   }

   native void create(com.frojasg1.sun.awt.windows.WFramePeer var1);
}
