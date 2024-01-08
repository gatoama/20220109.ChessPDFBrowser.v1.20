package com.frojasg1.sun.java2d.loops;

import java.awt.Rectangle;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveProxy;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

public final class CustomComponent {
   public CustomComponent() {
   }

   public static void register() {
      Class var0 = CustomComponent.class;
      com.frojasg1.sun.java2d.loops.GraphicsPrimitive[] var1 = new GraphicsPrimitive[]{new com.frojasg1.sun.java2d.loops.GraphicsPrimitiveProxy(var0, "OpaqueCopyAnyToArgb", com.frojasg1.sun.java2d.loops.Blit.methodSignature, com.frojasg1.sun.java2d.loops.Blit.primTypeID, com.frojasg1.sun.java2d.loops.SurfaceType.Any, com.frojasg1.sun.java2d.loops.CompositeType.SrcNoEa, com.frojasg1.sun.java2d.loops.SurfaceType.IntArgb), new com.frojasg1.sun.java2d.loops.GraphicsPrimitiveProxy(var0, "OpaqueCopyArgbToAny", com.frojasg1.sun.java2d.loops.Blit.methodSignature, com.frojasg1.sun.java2d.loops.Blit.primTypeID, com.frojasg1.sun.java2d.loops.SurfaceType.IntArgb, com.frojasg1.sun.java2d.loops.CompositeType.SrcNoEa, com.frojasg1.sun.java2d.loops.SurfaceType.Any), new GraphicsPrimitiveProxy(var0, "XorCopyArgbToAny", com.frojasg1.sun.java2d.loops.Blit.methodSignature, Blit.primTypeID, com.frojasg1.sun.java2d.loops.SurfaceType.IntArgb, CompositeType.Xor, SurfaceType.Any)};
      GraphicsPrimitiveMgr.register(var1);
   }

   public static Region getRegionOfInterest(SurfaceData var0, SurfaceData var1, Region var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      Region var9 = Region.getInstanceXYWH(var5, var6, var7, var8);
      var9 = var9.getIntersection(var1.getBounds());
      Rectangle var10 = var0.getBounds();
      var10.translate(var5 - var3, var6 - var4);
      var9 = var9.getIntersection(var10);
      if (var2 != null) {
         var9 = var9.getIntersection(var2);
      }

      return var9;
   }
}
