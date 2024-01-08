package com.frojasg1.sun.awt.image;

import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.VolatileSurfaceManager;
import com.frojasg1.sun.java2d.SurfaceData;

public class BufImgVolatileSurfaceManager extends VolatileSurfaceManager {
   public BufImgVolatileSurfaceManager(SunVolatileImage var1, Object var2) {
      super(var1, var2);
   }

   protected boolean isAccelerationEnabled() {
      return false;
   }

   protected SurfaceData initAcceleratedSurface() {
      return null;
   }
}
