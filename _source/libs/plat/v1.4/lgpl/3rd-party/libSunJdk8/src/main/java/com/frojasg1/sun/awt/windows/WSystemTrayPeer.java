package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WObjectPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Dimension;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.peer.SystemTrayPeer;

final class WSystemTrayPeer extends com.frojasg1.sun.awt.windows.WObjectPeer implements SystemTrayPeer {
   WSystemTrayPeer(SystemTray var1) {
      this.target = var1;
   }

   public Dimension getTrayIconSize() {
      return new Dimension(16, 16);
   }

   public boolean isSupported() {
      return ((WToolkit)Toolkit.getDefaultToolkit()).isTraySupported();
   }

   protected void disposeImpl() {
   }
}
