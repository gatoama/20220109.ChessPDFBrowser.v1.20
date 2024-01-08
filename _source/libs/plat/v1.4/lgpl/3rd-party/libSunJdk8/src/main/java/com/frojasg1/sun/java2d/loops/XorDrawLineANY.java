package com.frojasg1.sun.java2d.loops;

import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.DrawLine;
import com.frojasg1.sun.java2d.loops.GeneralRenderer;
import com.frojasg1.sun.java2d.loops.PixelWriter;
import com.frojasg1.sun.java2d.loops.SurfaceType;

class XorDrawLineANY extends DrawLine {
   XorDrawLineANY() {
      super(com.frojasg1.sun.java2d.loops.SurfaceType.AnyColor, CompositeType.Xor, SurfaceType.Any);
   }

   public void DrawLine(SunGraphics2D var1, SurfaceData var2, int var3, int var4, int var5, int var6) {
      com.frojasg1.sun.java2d.loops.PixelWriter var7 = com.frojasg1.sun.java2d.loops.GeneralRenderer.createXorPixelWriter(var1, var2);
      if (var4 >= var6) {
         com.frojasg1.sun.java2d.loops.GeneralRenderer.doDrawLine(var2, var7, (int[])null, var1.getCompClip(), var5, var6, var3, var4);
      } else {
         GeneralRenderer.doDrawLine(var2, var7, (int[])null, var1.getCompClip(), var3, var4, var5, var6);
      }

   }
}