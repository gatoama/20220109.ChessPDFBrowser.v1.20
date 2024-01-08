package com.frojasg1.sun.java2d;

import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.image.ColorModel;
import java.awt.image.Raster;

import com.frojasg1.sun.java2d.InvalidPipeException;
import com.frojasg1.sun.java2d.StateTrackable;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.loops.SurfaceType;
import com.frojasg1.sun.java2d.pipe.NullPipe;

public class NullSurfaceData extends com.frojasg1.sun.java2d.SurfaceData {
   public static final com.frojasg1.sun.java2d.SurfaceData theInstance = new NullSurfaceData();
   private static final NullPipe nullpipe = new NullPipe();

   private NullSurfaceData() {
      super(StateTrackable.State.IMMUTABLE, SurfaceType.Any, ColorModel.getRGBdefault());
   }

   public void invalidate() {
   }

   public SurfaceData getReplacement() {
      return this;
   }

   public void validatePipe(com.frojasg1.sun.java2d.SunGraphics2D var1) {
      var1.drawpipe = nullpipe;
      var1.fillpipe = nullpipe;
      var1.shapepipe = nullpipe;
      var1.textpipe = nullpipe;
      var1.imagepipe = nullpipe;
   }

   public GraphicsConfiguration getDeviceConfiguration() {
      return null;
   }

   public Raster getRaster(int var1, int var2, int var3, int var4) {
      throw new InvalidPipeException("should be NOP");
   }

   public boolean useTightBBoxes() {
      return false;
   }

   public int pixelFor(int var1) {
      return var1;
   }

   public int rgbFor(int var1) {
      return var1;
   }

   public Rectangle getBounds() {
      return new Rectangle();
   }

   protected void checkCustomComposite() {
   }

   public boolean copyArea(SunGraphics2D var1, int var2, int var3, int var4, int var5, int var6, int var7) {
      return true;
   }

   public Object getDestination() {
      return null;
   }
}
