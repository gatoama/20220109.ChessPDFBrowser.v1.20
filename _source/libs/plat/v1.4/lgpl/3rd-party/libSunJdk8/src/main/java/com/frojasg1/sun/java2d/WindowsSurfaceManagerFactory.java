package com.frojasg1.sun.java2d;

import java.awt.GraphicsConfiguration;
import com.frojasg1.sun.awt.image.BufImgVolatileSurfaceManager;
import com.frojasg1.sun.awt.image.SunVolatileImage;
import com.frojasg1.sun.awt.image.VolatileSurfaceManager;
import com.frojasg1.sun.java2d.SurfaceManagerFactory;
import com.frojasg1.sun.java2d.d3d.D3DGraphicsConfig;
import com.frojasg1.sun.java2d.d3d.D3DVolatileSurfaceManager;
import com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig;
import com.frojasg1.sun.java2d.opengl.WGLVolatileSurfaceManager;

public class WindowsSurfaceManagerFactory extends SurfaceManagerFactory {
   public WindowsSurfaceManagerFactory() {
   }

   public VolatileSurfaceManager createVolatileManager(SunVolatileImage var1, Object var2) {
      GraphicsConfiguration var3 = var1.getGraphicsConfig();
      if (var3 instanceof D3DGraphicsConfig) {
         return new D3DVolatileSurfaceManager(var1, var2);
      } else {
         return (VolatileSurfaceManager)(var3 instanceof WGLGraphicsConfig ? new WGLVolatileSurfaceManager(var1, var2) : new BufImgVolatileSurfaceManager(var1, var2));
      }
   }
}
