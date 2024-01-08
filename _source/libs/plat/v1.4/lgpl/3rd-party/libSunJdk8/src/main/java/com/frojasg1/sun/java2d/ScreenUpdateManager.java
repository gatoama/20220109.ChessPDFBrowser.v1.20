package com.frojasg1.sun.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import com.frojasg1.sun.awt.Win32GraphicsConfig;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.java2d.SunGraphics2D;
import com.frojasg1.sun.java2d.SurfaceData;
import com.frojasg1.sun.java2d.d3d.D3DScreenUpdateManager;
import com.frojasg1.sun.java2d.windows.WindowsFlags;

public class ScreenUpdateManager {
   private static ScreenUpdateManager theInstance;

   protected ScreenUpdateManager() {
   }

   public synchronized Graphics2D createGraphics(com.frojasg1.sun.java2d.SurfaceData var1, WComponentPeer var2, Color var3, Color var4, Font var5) {
      return new SunGraphics2D(var1, var3, var4, var5);
   }

   public com.frojasg1.sun.java2d.SurfaceData createScreenSurface(Win32GraphicsConfig var1, WComponentPeer var2, int var3, boolean var4) {
      return var1.createSurfaceData(var2, var3);
   }

   public void dropScreenSurface(com.frojasg1.sun.java2d.SurfaceData var1) {
   }

   public com.frojasg1.sun.java2d.SurfaceData getReplacementScreenSurface(WComponentPeer var1, com.frojasg1.sun.java2d.SurfaceData var2) {
      SurfaceData var3 = var1.getSurfaceData();
      if (var3 != null && !var3.isValid()) {
         var1.replaceSurfaceData();
         return var1.getSurfaceData();
      } else {
         return var3;
      }
   }

   public static synchronized ScreenUpdateManager getInstance() {
      if (theInstance == null) {
         if (WindowsFlags.isD3DEnabled()) {
            theInstance = new D3DScreenUpdateManager();
         } else {
            theInstance = new ScreenUpdateManager();
         }
      }

      return theInstance;
   }
}
