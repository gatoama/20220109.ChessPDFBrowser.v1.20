package com.frojasg1.sun.awt.windows;

import java.awt.Component;
import java.awt.Window;
import java.awt.peer.ComponentPeer;
import com.frojasg1.sun.awt.CausedFocusEvent;
import com.frojasg1.sun.awt.KeyboardFocusManagerPeerImpl;

final class WKeyboardFocusManagerPeer extends KeyboardFocusManagerPeerImpl {
   private static final WKeyboardFocusManagerPeer inst = new WKeyboardFocusManagerPeer();

   static native void setNativeFocusOwner(ComponentPeer var0);

   static native Component getNativeFocusOwner();

   static native Window getNativeFocusedWindow();

   public static WKeyboardFocusManagerPeer getInstance() {
      return inst;
   }

   private WKeyboardFocusManagerPeer() {
   }

   public void setCurrentFocusOwner(Component var1) {
      setNativeFocusOwner(var1 != null ? var1.getPeer() : null);
   }

   public Component getCurrentFocusOwner() {
      return getNativeFocusOwner();
   }

   public void setCurrentFocusedWindow(Window var1) {
      throw new RuntimeException("not implemented");
   }

   public Window getCurrentFocusedWindow() {
      return getNativeFocusedWindow();
   }

   public static boolean deliverFocus(Component var0, Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      return KeyboardFocusManagerPeerImpl.deliverFocus(var0, var1, var2, var3, var4, var6, getNativeFocusOwner());
   }
}
