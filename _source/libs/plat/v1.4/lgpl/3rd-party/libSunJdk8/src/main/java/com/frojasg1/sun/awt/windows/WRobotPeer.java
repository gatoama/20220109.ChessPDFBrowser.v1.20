package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WObjectPeer;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.peer.RobotPeer;

final class WRobotPeer extends com.frojasg1.sun.awt.windows.WObjectPeer implements RobotPeer {
   WRobotPeer() {
      this.create();
   }

   WRobotPeer(GraphicsDevice var1) {
      this.create();
   }

   private synchronized native void _dispose();

   protected void disposeImpl() {
      this._dispose();
   }

   public native void create();

   public native void mouseMoveImpl(int var1, int var2);

   public void mouseMove(int var1, int var2) {
      this.mouseMoveImpl(var1, var2);
   }

   public native void mousePress(int var1);

   public native void mouseRelease(int var1);

   public native void mouseWheel(int var1);

   public native void keyPress(int var1);

   public native void keyRelease(int var1);

   public int getRGBPixel(int var1, int var2) {
      return this.getRGBPixels(new Rectangle(var1, var2, 1, 1))[0];
   }

   public int[] getRGBPixels(Rectangle var1) {
      int[] var2 = new int[var1.width * var1.height];
      this.getRGBPixels(var1.x, var1.y, var1.width, var1.height, var2);
      return var2;
   }

   private native void getRGBPixels(int var1, int var2, int var3, int var4, int[] var5);
}
