package com.frojasg1.sun.java2d.loops;

import java.awt.image.WritableRaster;

abstract class PixelWriter {
   protected WritableRaster dstRast;

   PixelWriter() {
   }

   public void setRaster(WritableRaster var1) {
      this.dstRast = var1;
   }

   public abstract void writePixel(int var1, int var2);
}
