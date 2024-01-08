package com.frojasg1.sun.java2d.windows;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.Raster;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.Win32GraphicsDevice;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.ScreenUpdateManager;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.SurfaceDataProxy;
import com.frojasg1.sun.java2d.loops.CompositeType;
import com.frojasg1.sun.java2d.loops.GraphicsPrimitive;
import com.frojasg1.sun.java2d.loops.RenderLoops;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.loops.XORComposite;
import com.frojasg1.sun.java2d.pipe.PixelToShapeConverter;
import com.frojasg1.sun.java2d.pipe.Region;
import com.frojasg1.sun.java2d.windows.GDIBlitLoops;
import com.frojasg1.sun.java2d.windows.GDIRenderer;
import com.frojasg1.sun.java2d.windows.WindowsFlags;

public class GDIWindowSurfaceData extends SurfaceData {
   private WComponentPeer peer;
   private Win32GraphicsConfig graphicsConfig;
   private RenderLoops solidloops;
   public static final String DESC_GDI = "GDI";
   public static final SurfaceType AnyGdi;
   public static final SurfaceType IntRgbGdi;
   public static final SurfaceType Ushort565RgbGdi;
   public static final SurfaceType Ushort555RgbGdi;
   public static final SurfaceType ThreeByteBgrGdi;
   protected static com.frojasg1.sun.java2d.windows.GDIRenderer gdiPipe;
   protected static PixelToShapeConverter gdiTxPipe;

   private static native void initIDs(Class var0);

   public static SurfaceType getSurfaceType(ColorModel var0) {
      switch(var0.getPixelSize()) {
      case 8:
         if (var0.getColorSpace().getType() == 6 && var0 instanceof ComponentColorModel) {
            return SurfaceType.ByteGray;
         } else {
            if (var0 instanceof IndexColorModel && isOpaqueGray((IndexColorModel)var0)) {
               return SurfaceType.Index8Gray;
            }

            return SurfaceType.ByteIndexedOpaque;
         }
      case 15:
         return Ushort555RgbGdi;
      case 16:
         if (var0 instanceof DirectColorModel && ((DirectColorModel)var0).getBlueMask() == 62) {
            return SurfaceType.Ushort555Rgbx;
         }

         return Ushort565RgbGdi;
      case 24:
      case 32:
         if (var0 instanceof DirectColorModel) {
            if (((DirectColorModel)var0).getRedMask() == 16711680) {
               return IntRgbGdi;
            }

            return SurfaceType.IntRgbx;
         }

         return ThreeByteBgrGdi;
      default:
         throw new InvalidPipeException("Unsupported bit depth: " + var0.getPixelSize());
      }
   }

   public static GDIWindowSurfaceData createData(WComponentPeer var0) {
      SurfaceType var1 = getSurfaceType(var0.getDeviceColorModel());
      return new GDIWindowSurfaceData(var0, var1);
   }

   public SurfaceDataProxy makeProxyFor(SurfaceData var1) {
      return SurfaceDataProxy.UNCACHED;
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      throw new InternalError("not implemented yet");
   }

   public void validatePipe(SunGraphics2D var1) {
      if (var1.antialiasHint == 2 || var1.paintState > 1 || var1.compositeState > 0 && var1.compositeState != 2) {
         super.validatePipe(var1);
      } else {
         if (var1.clipState == 2) {
            super.validatePipe(var1);
         } else {
            switch(var1.textAntialiasHint) {
            case 0:
            case 1:
               var1.textpipe = solidTextRenderer;
               break;
            case 2:
               var1.textpipe = aaTextRenderer;
               break;
            default:
               switch(var1.getFontInfo().aaHint) {
               case 2:
                  var1.textpipe = aaTextRenderer;
                  break;
               case 3:
               case 5:
               default:
                  var1.textpipe = solidTextRenderer;
                  break;
               case 4:
               case 6:
                  var1.textpipe = lcdTextRenderer;
               }
            }
         }

         var1.imagepipe = imagepipe;
         if (var1.transformState >= 3) {
            var1.drawpipe = gdiTxPipe;
            var1.fillpipe = gdiTxPipe;
         } else if (var1.strokeState != 0) {
            var1.drawpipe = gdiTxPipe;
            var1.fillpipe = gdiPipe;
         } else {
            var1.drawpipe = gdiPipe;
            var1.fillpipe = gdiPipe;
         }

         var1.shapepipe = gdiPipe;
         if (var1.loops == null) {
            var1.loops = this.getRenderLoops(var1);
         }
      }

   }

   public RenderLoops getRenderLoops(SunGraphics2D var1) {
      return var1.paintState <= 1 && var1.compositeState <= 0 ? this.solidloops : super.getRenderLoops(var1);
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return this.graphicsConfig;
   }

   private native void initOps(WComponentPeer var1, int var2, int var3, int var4, int var5, int var6);

   private GDIWindowSurfaceData(WComponentPeer var1, SurfaceType var2) {
      super(var2, var1.getDeviceColorModel());
      ColorModel var3 = var1.getDeviceColorModel();
      this.peer = var1;
      int var4 = 0;
      int var5 = 0;
      int var6 = 0;
      int var7;
      switch(var3.getPixelSize()) {
      case 24:
      case 32:
         if (var3 instanceof DirectColorModel) {
            var7 = 32;
         } else {
            var7 = 24;
         }
         break;
      default:
         var7 = var3.getPixelSize();
      }

      if (var3 instanceof DirectColorModel) {
         DirectColorModel var8 = (DirectColorModel)var3;
         var4 = var8.getRedMask();
         var5 = var8.getGreenMask();
         var6 = var8.getBlueMask();
      }

      this.graphicsConfig = (Win32GraphicsConfig)var1.getGraphicsConfiguration();
      this.solidloops = this.graphicsConfig.getSolidLoops(var2);
      Win32GraphicsDevice var9 = (Win32GraphicsDevice)this.graphicsConfig.getDevice();
      this.initOps(var1, var7, var4, var5, var6, var9.getScreen());
      this.setBlitProxyKey(this.graphicsConfig.getProxyKey());
   }

   public SurfaceData getReplacement() {
      ScreenUpdateManager var1 = ScreenUpdateManager.getInstance();
      return var1.getReplacementScreenSurface(this.peer, this);
   }

   public Rectangle getBounds() {
      Rectangle var1 = this.peer.getBounds();
      var1.x = var1.y = 0;
      return var1;
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      CompositeType var8 = var1.imageComp;
      if (var1.transformState < 3 && var1.clipState != 2 && (CompositeType.SrcOverNoEa.equals(var8) || CompositeType.SrcNoEa.equals(var8))) {
         var2 += var1.transX;
         var3 += var1.transY;
         int var9 = var2 + var6;
         int var10 = var3 + var7;
         int var11 = var9 + var4;
         int var12 = var10 + var5;
         Region var13 = var1.getCompClip();
         if (var9 < var13.getLoX()) {
            var9 = var13.getLoX();
         }

         if (var10 < var13.getLoY()) {
            var10 = var13.getLoY();
         }

         if (var11 > var13.getHiX()) {
            var11 = var13.getHiX();
         }

         if (var12 > var13.getHiY()) {
            var12 = var13.getHiY();
         }

         if (var9 < var11 && var10 < var12) {
            gdiPipe.devCopyArea(this, var9 - var6, var10 - var7, var6, var7, var11 - var9, var12 - var10);
         }

         return true;
      } else {
         return false;
      }
   }

   private native void invalidateSD();

   public void invalidate() {
      if (this.isValid()) {
         this.invalidateSD();
         super.invalidate();
      }

   }

   public Object getDestination() {
      return this.peer.getTarget();
   }

   public WComponentPeer getPeer() {
      return this.peer;
   }

   static {
      AnyGdi = SurfaceType.IntRgb.deriveSubType("GDI");
      IntRgbGdi = SurfaceType.IntRgb.deriveSubType("GDI");
      Ushort565RgbGdi = SurfaceType.Ushort565Rgb.deriveSubType("GDI");
      Ushort555RgbGdi = SurfaceType.Ushort555Rgb.deriveSubType("GDI");
      ThreeByteBgrGdi = SurfaceType.ThreeByteBgr.deriveSubType("GDI");
      initIDs(XORComposite.class);
      if (WindowsFlags.isGdiBlitEnabled()) {
         GDIBlitLoops.register();
      }

      gdiPipe = new GDIRenderer();
      if (GraphicsPrimitive.tracingEnabled()) {
         gdiPipe = gdiPipe.traceWrap();
      }

      gdiTxPipe = new PixelToShapeConverter(gdiPipe);
   }
}
