package com.frojasg1.sun.awt;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.ComponentColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;

public class Win32ColorModel24 extends ComponentColorModel {
   public Win32ColorModel24() {
      super(ColorSpace.getInstance(1000), new int[]{8, 8, 8}, false, false, 1, 0);
   }

   public WritableRaster createCompatibleWritableRaster(int var1, int var2) {
      int[] var3 = new int[]{2, 1, 0};
      return Raster.createInterleavedRaster(0, var1, var2, var1 * 3, 3, var3, (Point)null);
   }

   public SampleModel createCompatibleSampleModel(int var1, int var2) {
      int[] var3 = new int[]{2, 1, 0};
      return new PixelInterleavedSampleModel(0, var1, var2, 3, var1 * 3, var3);
   }
}
