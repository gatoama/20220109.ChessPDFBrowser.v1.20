package com.frojasg1.sun.java2d.d3d;

import com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice;
import com.frojasg1.sun.java2d.d3d.D3DRenderQueue;
import com.frojasg1.sun.java2d.pipe.BufferedContext;
import com.frojasg1.sun.java2d.pipe.RenderBuffer;
import com.frojasg1.sun.java2d.pipe.RenderQueue;
import com.frojasg1.sun.java2d.pipe.hw.ContextCapabilities;

class D3DContext extends BufferedContext {
   private final com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice device;

   D3DContext(RenderQueue var1, com.frojasg1.sun.java2d.d3d.D3DGraphicsDevice var2) {
      super(var1);
      this.device = var2;
   }

   static void invalidateCurrentContext() {
      if (currentContext != null) {
         currentContext.invalidateContext();
         currentContext = null;
      }

      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var0 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      var0.ensureCapacity(4);
      var0.getBuffer().putInt(75);
      var0.flushNow();
   }

   static void setScratchSurface(D3DContext var0) {
      if (var0 != currentContext) {
         currentContext = null;
      }

      com.frojasg1.sun.java2d.d3d.D3DRenderQueue var1 = com.frojasg1.sun.java2d.d3d.D3DRenderQueue.getInstance();
      RenderBuffer var2 = var1.getBuffer();
      var1.ensureCapacity(8);
      var2.putInt(71);
      var2.putInt(var0.getDevice().getScreen());
   }

   public RenderQueue getRenderQueue() {
      return D3DRenderQueue.getInstance();
   }

   public void saveState() {
      this.invalidateContext();
      invalidateCurrentContext();
      setScratchSurface(this);
      this.rq.ensureCapacity(4);
      this.buf.putInt(78);
      this.rq.flushNow();
   }

   public void restoreState() {
      this.invalidateContext();
      invalidateCurrentContext();
      setScratchSurface(this);
      this.rq.ensureCapacity(4);
      this.buf.putInt(79);
      this.rq.flushNow();
   }

   D3DGraphicsDevice getDevice() {
      return this.device;
   }

   static class D3DContextCaps extends ContextCapabilities {
      static final int CAPS_LCD_SHADER = 65536;
      static final int CAPS_BIOP_SHADER = 131072;
      static final int CAPS_DEVICE_OK = 262144;
      static final int CAPS_AA_SHADER = 524288;

      D3DContextCaps(int var1, String var2) {
         super(var1, var2);
      }

      public String toString() {
         StringBuffer var1 = new StringBuffer(super.toString());
         if ((this.caps & 65536) != 0) {
            var1.append("CAPS_LCD_SHADER|");
         }

         if ((this.caps & 131072) != 0) {
            var1.append("CAPS_BIOP_SHADER|");
         }

         if ((this.caps & 524288) != 0) {
            var1.append("CAPS_AA_SHADER|");
         }

         if ((this.caps & 262144) != 0) {
            var1.append("CAPS_DEVICE_OK|");
         }

         return var1.toString();
      }
   }
}
