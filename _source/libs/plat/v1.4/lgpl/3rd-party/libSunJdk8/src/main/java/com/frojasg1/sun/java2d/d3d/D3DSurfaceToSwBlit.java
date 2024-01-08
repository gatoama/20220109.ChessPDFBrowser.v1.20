package com.frojasg1.sun.java2d.d3d;

import java.awt.Composite;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DContext;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.d3d.D3DSurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.pipe.RenderBuffer;

class D3DSurfaceToSwBlit extends Blit {
   private int typeval;

   D3DSurfaceToSwBlit(SurfaceType var1, int var2) {
      super(com.frojasg1.sun.java2d.d3d.D3DSurfaceData.D3DSurface, CompositeType.SrcNoEa, var1);
      this.typeval = var2;
   }

   public void Blit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10) {
      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var11 = D3DRenderQueue.getInstance();
      var11.lock();

      try {
         var11.addReference(var2);
         RenderBuffer var12 = var11.getBuffer();
         com.frojasg1.sun.java2d.d3d.D3DContext.setScratchSurface(((D3DSurfaceData)var1).getContext());
         var11.ensureCapacityAndAlignment(48, 32);
         var12.putInt(34);
         var12.putInt(var5).putInt(var6);
         var12.putInt(var7).putInt(var8);
         var12.putInt(var9).putInt(var10);
         var12.putInt(this.typeval);
         var12.putLong(var1.getNativeOps());
         var12.putLong(var2.getNativeOps());
         var11.flushNow();
      } finally {
         var11.unlock();
      }

   }
}
