package com.frojasg1.sun.java2d.loops;

import java.awt.Composite;
import java.lang.ref.WeakReference;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.Blit;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr;
import com.frojasg1.sun.java2d.loops.RenderCache;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.Region;

public class MaskBlit extends com.frojasg1.sun.java2d.loops.GraphicsPrimitive {
   public static final String methodSignature = "MaskBlit(...)".toString();
   public static final int primTypeID = makePrimTypeID();
   private static com.frojasg1.sun.java2d.loops.RenderCache blitcache = new RenderCache(20);

   public static MaskBlit locate(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      return (MaskBlit) com.frojasg1.sun.java2d.loops.GraphicsPrimitiveMgr.locate(primTypeID, var0, var1, var2);
   }

   public static MaskBlit getFromCache(com.frojasg1.sun.java2d.loops.SurfaceType var0, com.frojasg1.sun.java2d.loops.CompositeType var1, com.frojasg1.sun.java2d.loops.SurfaceType var2) {
      Object var3 = blitcache.get(var0, var1, var2);
      if (var3 != null) {
         return (MaskBlit)var3;
      } else {
         MaskBlit var4 = locate(var0, var1, var2);
         if (var4 == null) {
            System.out.println("mask blit loop not found for:");
            System.out.println("src:  " + var0);
            System.out.println("comp: " + var1);
            System.out.println("dst:  " + var2);
         } else {
            blitcache.put(var0, var1, var2, var4);
         }

         return var4;
      }
   }

   protected MaskBlit(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      super(methodSignature, primTypeID, var1, var2, var3);
   }

   public MaskBlit(long var1, com.frojasg1.sun.java2d.loops.SurfaceType var3, com.frojasg1.sun.java2d.loops.CompositeType var4, com.frojasg1.sun.java2d.loops.SurfaceType var5) {
      super(var1, methodSignature, primTypeID, var3, var4, var5);
   }

   public native void MaskBlit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, byte[] var11, int var12, int var13);

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive makePrimitive(com.frojasg1.sun.java2d.loops.SurfaceType var1, com.frojasg1.sun.java2d.loops.CompositeType var2, com.frojasg1.sun.java2d.loops.SurfaceType var3) {
      if (com.frojasg1.sun.java2d.loops.CompositeType.Xor.equals(var2)) {
         throw new InternalError("Cannot construct MaskBlit for XOR mode");
      } else {
         MaskBlit.General var4 = new MaskBlit.General(var1, var2, var3);
         this.setupGeneralBinaryOp(var4);
         return var4;
      }
   }

   public com.frojasg1.sun.java2d.loops.GraphicsPrimitive traceWrap() {
      return new MaskBlit.TraceMaskBlit(this);
   }

   static {
      GraphicsPrimitiveMgr.registerGeneral(new MaskBlit((com.frojasg1.sun.java2d.loops.SurfaceType)null, (com.frojasg1.sun.java2d.loops.CompositeType)null, (com.frojasg1.sun.java2d.loops.SurfaceType)null));
   }

   private static class General extends MaskBlit implements GeneralBinaryOp {
      com.frojasg1.sun.java2d.loops.Blit convertsrc;
      com.frojasg1.sun.java2d.loops.Blit convertdst;
      MaskBlit performop;
      com.frojasg1.sun.java2d.loops.Blit convertresult;
      WeakReference srcTmp;
      WeakReference dstTmp;

      public General(com.frojasg1.sun.java2d.loops.SurfaceType var1, CompositeType var2, SurfaceType var3) {
         super(var1, var2, var3);
      }

      public void setPrimitives(com.frojasg1.sun.java2d.loops.Blit var1, com.frojasg1.sun.java2d.loops.Blit var2, com.frojasg1.sun.java2d.loops.GraphicsPrimitive var3, Blit var4) {
         this.convertsrc = var1;
         this.convertdst = var2;
         this.performop = (MaskBlit)var3;
         this.convertresult = var4;
      }

      public synchronized void MaskBlit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, byte[] var11, int var12, int var13) {
         SurfaceData var14;
         int var17;
         int var18;
         SurfaceData var21;
         if (this.convertsrc == null) {
            var14 = var1;
            var17 = var5;
            var18 = var6;
         } else {
            var21 = null;
            if (this.srcTmp != null) {
               var21 = (SurfaceData)this.srcTmp.get();
            }

            var14 = convertFrom(this.convertsrc, var1, var5, var6, var9, var10, var21);
            var17 = 0;
            var18 = 0;
            if (var14 != var21) {
               this.srcTmp = new WeakReference(var14);
            }
         }

         SurfaceData var15;
         Region var16;
         int var19;
         int var20;
         if (this.convertdst == null) {
            var15 = var2;
            var19 = var7;
            var20 = var8;
            var16 = var4;
         } else {
            var21 = null;
            if (this.dstTmp != null) {
               var21 = (SurfaceData)this.dstTmp.get();
            }

            var15 = convertFrom(this.convertdst, var2, var7, var8, var9, var10, var21);
            var19 = 0;
            var20 = 0;
            var16 = null;
            if (var15 != var21) {
               this.dstTmp = new WeakReference(var15);
            }
         }

         this.performop.MaskBlit(var14, var15, var3, var16, var17, var18, var19, var20, var9, var10, var11, var12, var13);
         if (this.convertresult != null) {
            convertTo(this.convertresult, var15, var2, var4, var7, var8, var9, var10);
         }

      }
   }

   private static class TraceMaskBlit extends MaskBlit {
      MaskBlit target;

      public TraceMaskBlit(MaskBlit var1) {
         super(var1.getNativePrim(), var1.getSourceType(), var1.getCompositeType(), var1.getDestType());
         this.target = var1;
      }

      public GraphicsPrimitive traceWrap() {
         return this;
      }

      public void MaskBlit(SurfaceData var1, SurfaceData var2, Composite var3, Region var4, int var5, int var6, int var7, int var8, int var9, int var10, byte[] var11, int var12, int var13) {
         tracePrimitive(this.target);
         this.target.MaskBlit(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12, var13);
      }
   }
}
