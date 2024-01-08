package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.dnd.DropTarget;
import java.awt.event.ComponentEvent;
import com.frojasg1.sun.awt.LightweightFrame;
import com.frojasg1.sun.awt.OverrideNativeWindowHandle;
import com.frojasg1.sun.awt.windows.WFramePeer;
import com.frojasg1.sun.swing.JLightweightFrame;
import com.frojasg1.sun.swing.SwingAccessor;

//public class WLightweightFramePeer extends com.frojasg1.sun.awt.windows.WFramePeer implements OverrideNativeWindowHandle {
public abstract class WLightweightFramePeer extends com.frojasg1.sun.awt.windows.WFramePeer implements OverrideNativeWindowHandle {
   public WLightweightFramePeer(LightweightFrame var1) {
      super(var1);
   }

   private LightweightFrame getLwTarget() {
      return (LightweightFrame)this.target;
   }

   public Graphics getGraphics() {
      return this.getLwTarget().getGraphics();
   }

   private native void overrideNativeHandle(long var1);

   public void overrideWindowHandle(long var1) {
      this.overrideNativeHandle(var1);
   }

   public void show() {
      super.show();
      this.postEvent(new ComponentEvent((Component)this.getTarget(), 102));
   }

   public void hide() {
      super.hide();
      this.postEvent(new ComponentEvent((Component)this.getTarget(), 103));
   }

   public void reshape(int var1, int var2, int var3, int var4) {
      super.reshape(var1, var2, var3, var4);
      this.postEvent(new ComponentEvent((Component)this.getTarget(), 100));
      this.postEvent(new ComponentEvent((Component)this.getTarget(), 101));
   }

   public void handleEvent(AWTEvent var1) {
      if (var1.getID() == 501) {
         this.emulateActivation(true);
      }

      super.handleEvent(var1);
   }

   public void grab() {
      this.getLwTarget().grabFocus();
   }

   public void ungrab() {
      this.getLwTarget().ungrabFocus();
   }

   public void updateCursorImmediately() {
      SwingAccessor.getJLightweightFrameAccessor().updateCursor((JLightweightFrame)this.getLwTarget());
   }

   public boolean isLightweightFramePeer() {
      return true;
   }

   public void addDropTarget(DropTarget var1) {
      this.getLwTarget().addDropTarget(var1);
   }

   public void removeDropTarget(DropTarget var1) {
      this.getLwTarget().removeDropTarget(var1);
   }
}
