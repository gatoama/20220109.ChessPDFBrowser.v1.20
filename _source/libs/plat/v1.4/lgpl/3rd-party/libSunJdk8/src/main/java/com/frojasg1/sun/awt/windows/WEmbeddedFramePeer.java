package com.frojasg1.sun.awt.windows;

import java.awt.Dialog;
import java.awt.Graphics;
import java.awt.Rectangle;
import com.frojasg1.sun.awt.EmbeddedFrame;
import com.frojasg1.sun.awt.Win32GraphicsEnvironment;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WFramePeer;

//public class WEmbeddedFramePeer extends com.frojasg1.sun.awt.windows.WFramePeer {
public abstract class WEmbeddedFramePeer extends com.frojasg1.sun.awt.windows.WFramePeer {
   public WEmbeddedFramePeer(EmbeddedFrame var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   public void print(Graphics var1) {
   }

   public void updateMinimumSize() {
   }

   public void modalDisable(Dialog var1, long var2) {
      super.modalDisable(var1, var2);
      ((EmbeddedFrame)this.target).notifyModalBlocked(var1, true);
   }

   public void modalEnable(Dialog var1) {
      super.modalEnable(var1);
      ((EmbeddedFrame)this.target).notifyModalBlocked(var1, false);
   }

   public void setBoundsPrivate(int var1, int var2, int var3, int var4) {
      this.setBounds(var1, var2, var3, var4, 16387);
   }

   public native Rectangle getBoundsPrivate();

   public boolean isAccelCapable() {
      return !Win32GraphicsEnvironment.isDWMCompositionEnabled();
   }
}
