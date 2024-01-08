package com.frojasg1.sun.java2d.windows;

import java.awt.Composite;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData;

public class GDIBlitLoops extends Blit {
   int rmask;
   int gmask;
   int bmask;
   boolean indexed;

   public static void register() {
      GraphicsPrimitive[] var0 = new GraphicsPrimitive[]{new GDIBlitLoops(SurfaceType.IntRgb, com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData.AnyGdi), new GDIBlitLoops(SurfaceType.Ushort555Rgb, com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData.AnyGdi, 31744, 992, 31), new GDIBlitLoops(SurfaceType.Ushort565Rgb, com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData.AnyGdi, 63488, 2016, 31), new GDIBlitLoops(SurfaceType.ThreeByteBgr, com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData.AnyGdi), new GDIBlitLoops(SurfaceType.ByteIndexedOpaque, com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData.AnyGdi, true), new GDIBlitLoops(SurfaceType.Index8Gray, com.frojasg1.sun.java2d.windows.GDIWindowSurfaceData.AnyGdi, true), new GDIBlitLoops(SurfaceType.ByteGray, GDIWindowSurfaceData.AnyGdi)};
      GraphicsPrimitiveMgr.register(var0);
   }

   public GDIBlitLoops(SurfaceType var1, SurfaceType var2) {
      this(var1, var2, 0, 0, 0);
   }

   public GDIBlitLoops(SurfaceType var1, SurfaceType var2, boolean var3) {
      this(var1, var2, 0, 0, 0);
      this.indexed = var3;
   }

   public GDIBlitLoops(SurfaceType var1, SurfaceType var2, int var3, int var4, int var5) {
      super(var1, CompositeType.SrcNoEa, var2);
      this.indexed = false;
      this.rmask = var3;
      this.gmask = var4;
      this.bmask = var5;
   }

   public native void nativeBlit(SurfaceData var1, SurfaceData var2, Region var3, int var4, int var5, int var6, int var7, int var8, int var9, int var10, int var11, int var12, boolean var13);

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      this.nativeBlit(var1, var2, var4, var5, var6, var7, var8, var9, var10, this.rmask, this.gmask, this.bmask, this.indexed);
   }
}
