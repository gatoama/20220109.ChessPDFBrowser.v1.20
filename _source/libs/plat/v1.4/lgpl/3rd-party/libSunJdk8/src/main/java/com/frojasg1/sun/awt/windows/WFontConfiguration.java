package com.frojasg1.sun.awt.windows;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Hashtable;
import com.frojasg1.sun.awt.FontConfiguration;
import com.frojasg1.sun.awt.FontDescriptor;
import com.frojasg1.sun.awt.windows.WDefaultFontCharset;
import com.frojasg1.sun.font.SunFontManager;

public final class WFontConfiguration extends FontConfiguration {
   private boolean useCompatibilityFallbacks;
   private static HashMap subsetCharsetMap = new HashMap();
   private static HashMap subsetEncodingMap = new HashMap();
   private static String textInputCharset;

   public WFontConfiguration(SunFontManager var1) {
      super(var1);
      this.useCompatibilityFallbacks = "windows-1252".equals(encoding);
      this.initTables(encoding);
   }

   public WFontConfiguration(SunFontManager var1, boolean var2, boolean var3) {
      super(var1, var2, var3);
      this.useCompatibilityFallbacks = "windows-1252".equals(encoding);
   }

   protected void initReorderMap() {
      if (encoding.equalsIgnoreCase("windows-31j")) {
         localeMap = new Hashtable();
         localeMap.put("dialoginput.plain.japanese", "MS Mincho");
         localeMap.put("dialoginput.bold.japanese", "MS Mincho");
         localeMap.put("dialoginput.italic.japanese", "MS Mincho");
         localeMap.put("dialoginput.bolditalic.japanese", "MS Mincho");
      }

      this.reorderMap = new HashMap();
      this.reorderMap.put("UTF-8.hi", "devanagari");
      this.reorderMap.put("windows-1255", "hebrew");
      this.reorderMap.put("x-windows-874", "thai");
      this.reorderMap.put("windows-31j", "japanese");
      this.reorderMap.put("x-windows-949", "korean");
      this.reorderMap.put("GBK", "chinese-ms936");
      this.reorderMap.put("GB18030", "chinese-gb18030");
      this.reorderMap.put("x-windows-950", "chinese-ms950");
      this.reorderMap.put("x-MS950-HKSCS", this.split("chinese-ms950,chinese-hkscs"));
   }

   protected void setOsNameAndVersion() {
      super.setOsNameAndVersion();
      if (osName.startsWith("Windows")) {
         int var1 = osName.indexOf(32);
         if (var1 == -1) {
            osName = null;
         } else {
            int var2 = osName.indexOf(32, var1 + 1);
            if (var2 == -1) {
               osName = osName.substring(var1 + 1);
            } else {
               osName = osName.substring(var1 + 1, var2);
            }
         }

         osVersion = null;
      }

   }

   public String getFallbackFamilyName(String var1, String var2) {
      if (this.useCompatibilityFallbacks) {
         String var3 = this.getCompatibilityFamilyName(var1);
         if (var3 != null) {
            return var3;
         }
      }

      return var2;
   }

   protected String makeAWTFontName(String var1, String var2) {
      String var3 = (String)subsetCharsetMap.get(var2);
      if (var3 == null) {
         var3 = "DEFAULT_CHARSET";
      }

      return var1 + "," + var3;
   }

   protected String getEncoding(String var1, String var2) {
      String var3 = (String)subsetEncodingMap.get(var2);
      if (var3 == null) {
         var3 = "default";
      }

      return var3;
   }

   protected Charset getDefaultFontCharset(String var1) {
      return new com.frojasg1.sun.awt.windows.WDefaultFontCharset(var1);
   }

   public String getFaceNameFromComponentFontName(String var1) {
      return var1;
   }

   protected String getFileNameFromComponentFontName(String var1) {
      return this.getFileNameFromPlatformName(var1);
   }

   public String getTextComponentFontName(String var1, int var2) {
      FontDescriptor[] var3 = this.getFontDescriptors(var1, var2);
      String var4 = this.findFontWithCharset(var3, textInputCharset);
      if (var4 == null) {
         var4 = this.findFontWithCharset(var3, "DEFAULT_CHARSET");
      }

      return var4;
   }

   private String findFontWithCharset(FontDescriptor[] var1, String var2) {
      String var3 = null;

      for(int var4 = 0; var4 < var1.length; ++var4) {
         String var5 = var1[var4].getNativeName();
         if (var5.endsWith(var2)) {
            var3 = var5;
         }
      }

      return var3;
   }

   private void initTables(String var1) {
      subsetCharsetMap.put("alphabetic", "ANSI_CHARSET");
      subsetCharsetMap.put("alphabetic/1252", "ANSI_CHARSET");
      subsetCharsetMap.put("alphabetic/default", "DEFAULT_CHARSET");
      subsetCharsetMap.put("arabic", "ARABIC_CHARSET");
      subsetCharsetMap.put("chinese-ms936", "GB2312_CHARSET");
      subsetCharsetMap.put("chinese-gb18030", "GB2312_CHARSET");
      subsetCharsetMap.put("chinese-ms950", "CHINESEBIG5_CHARSET");
      subsetCharsetMap.put("chinese-hkscs", "CHINESEBIG5_CHARSET");
      subsetCharsetMap.put("cyrillic", "RUSSIAN_CHARSET");
      subsetCharsetMap.put("devanagari", "DEFAULT_CHARSET");
      subsetCharsetMap.put("dingbats", "SYMBOL_CHARSET");
      subsetCharsetMap.put("greek", "GREEK_CHARSET");
      subsetCharsetMap.put("hebrew", "HEBREW_CHARSET");
      subsetCharsetMap.put("japanese", "SHIFTJIS_CHARSET");
      subsetCharsetMap.put("korean", "HANGEUL_CHARSET");
      subsetCharsetMap.put("latin", "ANSI_CHARSET");
      subsetCharsetMap.put("symbol", "SYMBOL_CHARSET");
      subsetCharsetMap.put("thai", "THAI_CHARSET");
      subsetEncodingMap.put("alphabetic", "default");
      subsetEncodingMap.put("alphabetic/1252", "windows-1252");
      subsetEncodingMap.put("alphabetic/default", var1);
      subsetEncodingMap.put("arabic", "windows-1256");
      subsetEncodingMap.put("chinese-ms936", "GBK");
      subsetEncodingMap.put("chinese-gb18030", "GB18030");
      if ("x-MS950-HKSCS".equals(var1)) {
         subsetEncodingMap.put("chinese-ms950", "x-MS950-HKSCS");
      } else {
         subsetEncodingMap.put("chinese-ms950", "x-windows-950");
      }

      subsetEncodingMap.put("chinese-hkscs", "sun.awt.HKSCS");
      subsetEncodingMap.put("cyrillic", "windows-1251");
      subsetEncodingMap.put("devanagari", "UTF-16LE");
      subsetEncodingMap.put("dingbats", "sun.awt.windows.WingDings");
      subsetEncodingMap.put("greek", "windows-1253");
      subsetEncodingMap.put("hebrew", "windows-1255");
      subsetEncodingMap.put("japanese", "windows-31j");
      subsetEncodingMap.put("korean", "x-windows-949");
      subsetEncodingMap.put("latin", "windows-1252");
      subsetEncodingMap.put("symbol", "sun.awt.Symbol");
      subsetEncodingMap.put("thai", "x-windows-874");
      if ("windows-1256".equals(var1)) {
         textInputCharset = "ARABIC_CHARSET";
      } else if ("GBK".equals(var1)) {
         textInputCharset = "GB2312_CHARSET";
      } else if ("GB18030".equals(var1)) {
         textInputCharset = "GB2312_CHARSET";
      } else if ("x-windows-950".equals(var1)) {
         textInputCharset = "CHINESEBIG5_CHARSET";
      } else if ("x-MS950-HKSCS".equals(var1)) {
         textInputCharset = "CHINESEBIG5_CHARSET";
      } else if ("windows-1251".equals(var1)) {
         textInputCharset = "RUSSIAN_CHARSET";
      } else if ("UTF-8".equals(var1)) {
         textInputCharset = "DEFAULT_CHARSET";
      } else if ("windows-1253".equals(var1)) {
         textInputCharset = "GREEK_CHARSET";
      } else if ("windows-1255".equals(var1)) {
         textInputCharset = "HEBREW_CHARSET";
      } else if ("windows-31j".equals(var1)) {
         textInputCharset = "SHIFTJIS_CHARSET";
      } else if ("x-windows-949".equals(var1)) {
         textInputCharset = "HANGEUL_CHARSET";
      } else if ("x-windows-874".equals(var1)) {
         textInputCharset = "THAI_CHARSET";
      } else {
         textInputCharset = "DEFAULT_CHARSET";
      }

   }
}
