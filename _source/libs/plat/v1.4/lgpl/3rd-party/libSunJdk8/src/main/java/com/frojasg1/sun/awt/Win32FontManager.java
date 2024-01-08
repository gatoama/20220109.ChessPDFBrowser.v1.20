package com.frojasg1.sun.awt;

import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.frojasg1.sun.awt.FontConfiguration;
import com.frojasg1.sun.awt.Win32GraphicsEnvironment;
import com.frojasg1.sun.awt.windows.WFontConfiguration;
import com.frojasg1.sun.font.SunFontManager;
import com.frojasg1.sun.font.TrueTypeFont;

public final class Win32FontManager extends SunFontManager {
   private static TrueTypeFont eudcFont;
   static String fontsForPrinting;

   private static native String getEUDCFontFile();

   public TrueTypeFont getEUDCFont() {
      return eudcFont;
   }

   public Win32FontManager() {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            Win32FontManager.this.registerJREFontsWithPlatform(SunFontManager.jreFontDirName);
            return null;
         }
      });
   }

   protected boolean useAbsoluteFontFileNames() {
      return false;
   }

   protected void registerFontFile(String var1, String[] var2, int var3, boolean var4) {
      if (!this.registeredFontFiles.contains(var1)) {
         this.registeredFontFiles.add(var1);
         byte var5;
         if (this.getTrueTypeFilter().accept((File)null, var1)) {
            var5 = 0;
         } else {
            if (!this.getType1Filter().accept((File)null, var1)) {
               return;
            }

            var5 = 1;
         }

         if (this.fontPath == null) {
            this.fontPath = this.getPlatformFontPath(noType1Font);
         }

         String var6 = jreFontDirName + File.pathSeparator + this.fontPath;
         StringTokenizer var7 = new StringTokenizer(var6, File.pathSeparator);
         boolean var8 = false;

         try {
            while(!var8 && var7.hasMoreTokens()) {
               String var9 = var7.nextToken();
               boolean var10 = var9.equals(jreFontDirName);
               File var11 = new File(var9, var1);
               if (var11.canRead()) {
                  var8 = true;
                  String var12 = var11.getAbsolutePath();
                  if (var4) {
                     this.registerDeferredFont(var1, var12, var2, var5, var10, var3);
                  } else {
                     this.registerFontFile(var12, var2, var5, var10, var3);
                  }
                  break;
               }
            }
         } catch (NoSuchElementException var13) {
            System.err.println(var13);
         }

         if (!var8) {
            this.addToMissingFontFileList(var1);
         }

      }
   }

   protected com.frojasg1.sun.awt.FontConfiguration createFontConfiguration() {
      WFontConfiguration var1 = new WFontConfiguration(this);
      var1.init();
      return var1;
   }

   public FontConfiguration createFontConfiguration(boolean var1, boolean var2) {
      return new WFontConfiguration(this, var1, var2);
   }

   protected void populateFontFileNameMap(HashMap<String, String> var1, HashMap<String, String> var2, HashMap<String, ArrayList<String>> var3, Locale var4) {
      populateFontFileNameMap0(var1, var2, var3, var4);
   }

   private static native void populateFontFileNameMap0(HashMap<String, String> var0, HashMap<String, String> var1, HashMap<String, ArrayList<String>> var2, Locale var3);

   protected synchronized native String getFontPath(boolean var1);

   protected String[] getDefaultPlatformFont() {
      String[] var1 = new String[]{"Arial", "c:\\windows\\fonts"};
      final String[] var2 = this.getPlatformFontDirs(true);
      if (var2.length > 1) {
         String var3 = (String)AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               for(int var1 = 0; var1 < var2.length; ++var1) {
                  String var2x = var2[var1] + File.separator + "arial.ttf";
                  File var3 = new File(var2x);
                  if (var3.exists()) {
                     return var2[var1];
                  }
               }

               return null;
            }
         });
         if (var3 != null) {
            var1[1] = var3;
         }
      } else {
         var1[1] = var2[0];
      }

      var1[1] = var1[1] + File.separator + "arial.ttf";
      return var1;
   }

   protected void registerJREFontsWithPlatform(String var1) {
      fontsForPrinting = var1;
   }

   public static void registerJREFontsForPrinting() {
      Class var1 = com.frojasg1.sun.awt.Win32GraphicsEnvironment.class;
      final String var0;
      synchronized(Win32GraphicsEnvironment.class) {
         GraphicsEnvironment.getLocalGraphicsEnvironment();
         if (fontsForPrinting == null) {
            return;
         }

         var0 = fontsForPrinting;
         fontsForPrinting = null;
      }

      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            File var1 = new File(var0);
            String[] var2 = var1.list(SunFontManager.getInstance().getTrueTypeFilter());
            if (var2 == null) {
               return null;
            } else {
               for(int var3 = 0; var3 < var2.length; ++var3) {
                  File var4 = new File(var1, var2[var3]);
                  Win32FontManager.registerFontWithPlatform(var4.getAbsolutePath());
               }

               return null;
            }
         }
      });
   }

   protected static native void registerFontWithPlatform(String var0);

   protected static native void deRegisterFontWithPlatform(String var0);

   public HashMap<String, SunFontManager.FamilyDescription> populateHardcodedFileNameMap() {
      HashMap var1 = new HashMap();
      SunFontManager.FamilyDescription var2 = new SunFontManager.FamilyDescription();
      var2.familyName = "Segoe UI";
      var2.plainFullName = "Segoe UI";
      var2.plainFileName = "segoeui.ttf";
      var2.boldFullName = "Segoe UI Bold";
      var2.boldFileName = "segoeuib.ttf";
      var2.italicFullName = "Segoe UI Italic";
      var2.italicFileName = "segoeuii.ttf";
      var2.boldItalicFullName = "Segoe UI Bold Italic";
      var2.boldItalicFileName = "segoeuiz.ttf";
      var1.put("segoe", var2);
      var2 = new SunFontManager.FamilyDescription();
      var2.familyName = "Tahoma";
      var2.plainFullName = "Tahoma";
      var2.plainFileName = "tahoma.ttf";
      var2.boldFullName = "Tahoma Bold";
      var2.boldFileName = "tahomabd.ttf";
      var1.put("tahoma", var2);
      var2 = new SunFontManager.FamilyDescription();
      var2.familyName = "Verdana";
      var2.plainFullName = "Verdana";
      var2.plainFileName = "verdana.TTF";
      var2.boldFullName = "Verdana Bold";
      var2.boldFileName = "verdanab.TTF";
      var2.italicFullName = "Verdana Italic";
      var2.italicFileName = "verdanai.TTF";
      var2.boldItalicFullName = "Verdana Bold Italic";
      var2.boldItalicFileName = "verdanaz.TTF";
      var1.put("verdana", var2);
      var2 = new SunFontManager.FamilyDescription();
      var2.familyName = "Arial";
      var2.plainFullName = "Arial";
      var2.plainFileName = "ARIAL.TTF";
      var2.boldFullName = "Arial Bold";
      var2.boldFileName = "ARIALBD.TTF";
      var2.italicFullName = "Arial Italic";
      var2.italicFileName = "ARIALI.TTF";
      var2.boldItalicFullName = "Arial Bold Italic";
      var2.boldItalicFileName = "ARIALBI.TTF";
      var1.put("arial", var2);
      var2 = new SunFontManager.FamilyDescription();
      var2.familyName = "Symbol";
      var2.plainFullName = "Symbol";
      var2.plainFileName = "Symbol.TTF";
      var1.put("symbol", var2);
      var2 = new SunFontManager.FamilyDescription();
      var2.familyName = "WingDings";
      var2.plainFullName = "WingDings";
      var2.plainFileName = "WINGDING.TTF";
      var1.put("wingdings", var2);
      return var1;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String var1 = Win32FontManager.getEUDCFontFile();
            if (var1 != null) {
               try {
                  Win32FontManager.eudcFont = new TrueTypeFont(var1, (Object)null, 0, true, false);
               } catch (FontFormatException var3) {
               }
            }

            return null;
         }
      });
      fontsForPrinting = null;
   }
}
