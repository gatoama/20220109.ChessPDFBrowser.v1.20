package com.frojasg1.sun.awt.windows;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.WritableRaster;
import com.frojasg1.sun.awt.CustomCursor;
import com.frojasg1.sun.awt.image.ImageRepresentation;
import com.frojasg1.sun.awt.image.IntegerComponentRaster;
import com.frojasg1.sun.awt.image.ToolkitImage;

final class WCustomCursor extends CustomCursor {
   WCustomCursor(Image var1, Point var2, String var3) throws IndexOutOfBoundsException {
      super(var1, var2, var3);
   }

   protected void createNativeCursor(Image var1, int[] var2, int var3, int var4, int var5, int var6) {
      BufferedImage var7 = new BufferedImage(var3, var4, 1);
      Graphics var8 = var7.getGraphics();

      try {
         if (var1 instanceof ToolkitImage) {
            ImageRepresentation var9 = ((ToolkitImage)var1).getImageRep();
            var9.reconstruct(32);
         }

         var8.drawImage(var1, 0, 0, var3, var4, (ImageObserver)null);
      } finally {
         var8.dispose();
      }

      WritableRaster var19 = var7.getRaster();
      DataBuffer var10 = var19.getDataBuffer();
      int[] var11 = ((DataBufferInt)var10).getData();
      byte[] var12 = new byte[var3 * var4 / 8];
      int var13 = var2.length;

      int var14;
      for(var14 = 0; var14 < var13; ++var14) {
         int var15 = var14 / 8;
         int var16 = 1 << 7 - var14 % 8;
         if ((var2[var14] & -16777216) == 0) {
            var12[var15] = (byte)(var12[var15] | var16);
         }
      }

      var14 = var19.getWidth();
      if (var19 instanceof IntegerComponentRaster) {
         var14 = ((IntegerComponentRaster)var19).getScanlineStride();
      }

      this.createCursorIndirect(((DataBufferInt)var7.getRaster().getDataBuffer()).getData(), var12, var14, var19.getWidth(), var19.getHeight(), var5, var6);
   }

   private native void createCursorIndirect(int[] var1, byte[] var2, int var3, int var4, int var5, int var6, int var7);

   static native int getCursorWidth();

   static native int getCursorHeight();
}
