package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.RenderingHints;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.windows.ThemeReader;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.util.logging.PlatformLogger;

final class WDesktopProperties {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.windows.WDesktopProperties");
   private static final String PREFIX = "win.";
   private static final String FILE_PREFIX = "awt.file.";
   private static final String PROP_NAMES = "win.propNames";
   private long pData;
   private com.frojasg1.sun.awt.windows.WToolkit wToolkit;
   private HashMap<String, Object> map = new HashMap();
   static HashMap<String, String> fontNameMap;

   private static native void initIDs();

   static boolean isWindowsProperty(String var0) {
      return var0.startsWith("win.") || var0.startsWith("awt.file.") || var0.equals("awt.font.desktophints");
   }

   WDesktopProperties(WToolkit var1) {
      this.wToolkit = var1;
      this.init();
   }

   private native void init();

   private String[] getKeyNames() {
      Object[] var1 = this.map.keySet().toArray();
      String[] var2 = new String[var1.length];

      for(int var3 = 0; var3 < var1.length; ++var3) {
         var2[var3] = var1[var3].toString();
      }

      Arrays.sort(var2);
      return var2;
   }

   private native void getWindowsParameters();

   private synchronized void setBooleanProperty(String var1, boolean var2) {
      assert var1 != null;

      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var1 + "=" + var2);
      }

      this.map.put(var1, var2);
   }

   private synchronized void setIntegerProperty(String var1, int var2) {
      assert var1 != null;

      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var1 + "=" + var2);
      }

      this.map.put(var1, var2);
   }

   private synchronized void setStringProperty(String var1, String var2) {
      assert var1 != null;

      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var1 + "=" + var2);
      }

      this.map.put(var1, var2);
   }

   private synchronized void setColorProperty(String var1, int var2, int var3, int var4) {
      assert var1 != null && var2 <= 255 && var3 <= 255 && var4 <= 255;

      Color var5 = new Color(var2, var3, var4);
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var1 + "=" + var5);
      }

      this.map.put(var1, var5);
   }

   private synchronized void setFontProperty(String var1, String var2, int var3, int var4) {
      assert var1 != null && var3 <= 3 && var4 >= 0;

      String var5 = (String)fontNameMap.get(var2);
      if (var5 != null) {
         var2 = var5;
      }

      Font var6 = new Font(var2, var3, var4);
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var1 + "=" + var6);
      }

      this.map.put(var1, var6);
      String var7 = var1 + ".height";
      Integer var8 = var4;
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var7 + "=" + var8);
      }

      this.map.put(var7, var8);
   }

   private synchronized void setSoundProperty(String var1, String var2) {
      assert var1 != null && var2 != null;

      WDesktopProperties.WinPlaySound var3 = new WDesktopProperties.WinPlaySound(var2);
      if (log.isLoggable(PlatformLogger.Level.FINE)) {
         log.fine(var1 + "=" + var3);
      }

      this.map.put(var1, var3);
   }

   private native void playWindowsSound(String var1);

   synchronized Map<String, Object> getProperties() {
      ThemeReader.flush();
      this.map = new HashMap();
      this.getWindowsParameters();
      this.map.put("awt.font.desktophints", SunToolkit.getDesktopFontHints());
      this.map.put("win.propNames", this.getKeyNames());
      this.map.put("DnD.Autoscroll.cursorHysteresis", this.map.get("win.drag.x"));
      return (Map)this.map.clone();
   }

   synchronized RenderingHints getDesktopAAHints() {
      Object var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
      Integer var2 = null;
      Boolean var3 = (Boolean)this.map.get("win.text.fontSmoothingOn");
      if (var3 != null && var3.equals(Boolean.TRUE)) {
         Integer var4 = (Integer)this.map.get("win.text.fontSmoothingType");
         if (var4 != null && var4 > 1 && var4 <= 2) {
            Integer var5 = (Integer)this.map.get("win.text.fontSmoothingOrientation");
            if (var5 != null && var5 == 0) {
               var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HBGR;
            } else {
               var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB;
            }

            var2 = (Integer)this.map.get("win.text.fontSmoothingContrast");
            if (var2 == null) {
               var2 = 140;
            } else {
               var2 = var2 / 10;
            }
         } else {
            var1 = RenderingHints.VALUE_TEXT_ANTIALIAS_GASP;
         }
      }

      RenderingHints var6 = new RenderingHints((Map)null);
      var6.put(RenderingHints.KEY_TEXT_ANTIALIASING, var1);
      if (var2 != null) {
         var6.put(RenderingHints.KEY_TEXT_LCD_CONTRAST, var2);
      }

      return var6;
   }

   static {
      initIDs();
      fontNameMap = new HashMap();
      fontNameMap.put("Courier", "Monospaced");
      fontNameMap.put("MS Serif", "Microsoft Serif");
      fontNameMap.put("MS Sans Serif", "Microsoft Sans Serif");
      fontNameMap.put("Terminal", "Dialog");
      fontNameMap.put("FixedSys", "Monospaced");
      fontNameMap.put("System", "Dialog");
   }

   class WinPlaySound implements Runnable {
      String winEventName;

      WinPlaySound(String var2) {
         this.winEventName = var2;
      }

      public void run() {
         WDesktopProperties.this.playWindowsSound(this.winEventName);
      }

      public String toString() {
         return "WinPlaySound(" + this.winEventName + ")";
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else {
            try {
               return this.winEventName.equals(((WDesktopProperties.WinPlaySound)var1).winEventName);
            } catch (Exception var3) {
               return false;
            }
         }
      }

      public int hashCode() {
         return this.winEventName.hashCode();
      }
   }
}
