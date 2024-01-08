package com.frojasg1.sun.java2d.windows;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.java2d.opengl.WGLGraphicsConfig;

public class WindowsFlags {
   private static boolean gdiBlitEnabled;
   private static boolean d3dEnabled;
   private static boolean d3dVerbose;
   private static boolean d3dSet;
   private static boolean d3dOnScreenEnabled;
   private static boolean oglEnabled;
   private static boolean oglVerbose;
   private static boolean offscreenSharingEnabled;
   private static boolean accelReset;
   private static boolean checkRegistry;
   private static boolean disableRegistry;
   private static boolean magPresent;
   private static boolean setHighDPIAware;
   private static String javaVersion;

   public WindowsFlags() {
   }

   private static native boolean initNativeFlags();

   public static void initFlags() {
   }

   private static boolean getBooleanProp(String var0, boolean var1) {
      String var2 = System.getProperty(var0);
      boolean var3 = var1;
      if (var2 != null) {
         if (!var2.equals("true") && !var2.equals("t") && !var2.equals("True") && !var2.equals("T") && !var2.equals("")) {
            if (var2.equals("false") || var2.equals("f") || var2.equals("False") || var2.equals("F")) {
               var3 = false;
            }
         } else {
            var3 = true;
         }
      }

      return var3;
   }

   private static boolean isBooleanPropTrueVerbose(String var0) {
      String var1 = System.getProperty(var0);
      return var1 != null && (var1.equals("True") || var1.equals("T"));
   }

   private static int getIntProp(String var0, int var1) {
      String var2 = System.getProperty(var0);
      int var3 = var1;
      if (var2 != null) {
         try {
            var3 = Integer.parseInt(var2);
         } catch (NumberFormatException var5) {
         }
      }

      return var3;
   }

   private static boolean getPropertySet(String var0) {
      String var1 = System.getProperty(var0);
      return var1 != null;
   }

   private static void initJavaFlags() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            WindowsFlags.magPresent = WindowsFlags.getBooleanProp("javax.accessibility.screen_magnifier_present", false);
            boolean var1 = !WindowsFlags.getBooleanProp("sun.java2d.noddraw", WindowsFlags.magPresent);
            boolean var2 = WindowsFlags.getBooleanProp("sun.java2d.ddoffscreen", var1);
            WindowsFlags.d3dEnabled = WindowsFlags.getBooleanProp("sun.java2d.d3d", var1 && var2);
            WindowsFlags.d3dOnScreenEnabled = WindowsFlags.getBooleanProp("sun.java2d.d3d.onscreen", WindowsFlags.d3dEnabled);
            WindowsFlags.oglEnabled = WindowsFlags.getBooleanProp("sun.java2d.opengl", false);
            if (WindowsFlags.oglEnabled) {
               WindowsFlags.oglVerbose = WindowsFlags.isBooleanPropTrueVerbose("sun.java2d.opengl");
               if (WGLGraphicsConfig.isWGLAvailable()) {
                  WindowsFlags.d3dEnabled = false;
               } else {
                  if (WindowsFlags.oglVerbose) {
                     System.out.println("Could not enable OpenGL pipeline (WGL not available)");
                  }

                  WindowsFlags.oglEnabled = false;
               }
            }

            WindowsFlags.gdiBlitEnabled = WindowsFlags.getBooleanProp("sun.java2d.gdiBlit", true);
            WindowsFlags.d3dSet = WindowsFlags.getPropertySet("sun.java2d.d3d");
            if (WindowsFlags.d3dSet) {
               WindowsFlags.d3dVerbose = WindowsFlags.isBooleanPropTrueVerbose("sun.java2d.d3d");
            }

            WindowsFlags.offscreenSharingEnabled = WindowsFlags.getBooleanProp("sun.java2d.offscreenSharing", false);
            WindowsFlags.accelReset = WindowsFlags.getBooleanProp("sun.java2d.accelReset", false);
            WindowsFlags.checkRegistry = WindowsFlags.getBooleanProp("sun.java2d.checkRegistry", false);
            WindowsFlags.disableRegistry = WindowsFlags.getBooleanProp("sun.java2d.disableRegistry", false);
            WindowsFlags.javaVersion = System.getProperty("java.version");
            if (WindowsFlags.javaVersion == null) {
               WindowsFlags.javaVersion = "default";
            } else {
               int var3 = WindowsFlags.javaVersion.indexOf(45);
               if (var3 >= 0) {
                  WindowsFlags.javaVersion = WindowsFlags.javaVersion.substring(0, var3);
               }
            }

            String var5 = System.getProperty("sun.java2d.dpiaware");
            if (var5 != null) {
               WindowsFlags.setHighDPIAware = var5.equalsIgnoreCase("true");
            } else {
               String var4 = System.getProperty("sun.java.launcher", "unknown");
               WindowsFlags.setHighDPIAware = var4.equalsIgnoreCase("SUN_STANDARD");
            }

            return null;
         }
      });
   }

   public static boolean isD3DEnabled() {
      return d3dEnabled;
   }

   public static boolean isD3DSet() {
      return d3dSet;
   }

   public static boolean isD3DOnScreenEnabled() {
      return d3dOnScreenEnabled;
   }

   public static boolean isD3DVerbose() {
      return d3dVerbose;
   }

   public static boolean isGdiBlitEnabled() {
      return gdiBlitEnabled;
   }

   public static boolean isTranslucentAccelerationEnabled() {
      return d3dEnabled;
   }

   public static boolean isOffscreenSharingEnabled() {
      return offscreenSharingEnabled;
   }

   public static boolean isMagPresent() {
      return magPresent;
   }

   public static boolean isOGLEnabled() {
      return oglEnabled;
   }

   public static boolean isOGLVerbose() {
      return oglVerbose;
   }

   static {
      WToolkit.loadLibraries();
      initJavaFlags();
      initNativeFlags();
   }
}
