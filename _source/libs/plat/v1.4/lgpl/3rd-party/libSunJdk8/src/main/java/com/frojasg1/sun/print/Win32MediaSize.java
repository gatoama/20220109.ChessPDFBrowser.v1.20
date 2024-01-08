package com.frojasg1.sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;

class Win32MediaSize extends MediaSizeName {
   private static ArrayList winStringTable = new ArrayList();
   private static ArrayList winEnumTable = new ArrayList();
   private static MediaSize[] predefMedia;
   private int dmPaperID;

   private Win32MediaSize(int var1) {
      super(var1);
   }

   private static synchronized int nextValue(String var0) {
      winStringTable.add(var0);
      return winStringTable.size() - 1;
   }

   public static synchronized Win32MediaSize findMediaName(String var0) {
      int var1 = winStringTable.indexOf(var0);
      return var1 != -1 ? (Win32MediaSize)winEnumTable.get(var1) : null;
   }

   public static MediaSize[] getPredefMedia() {
      return predefMedia;
   }

   public Win32MediaSize(String var1, int var2) {
      super(nextValue(var1));
      this.dmPaperID = var2;
      winEnumTable.add(this);
   }

   private MediaSizeName[] getSuperEnumTable() {
      return (MediaSizeName[])((MediaSizeName[])super.getEnumValueTable());
   }

   int getDMPaper() {
      return this.dmPaperID;
   }

   protected String[] getStringTable() {
      String[] var1 = new String[winStringTable.size()];
      return (String[])((String[])winStringTable.toArray(var1));
   }

   protected EnumSyntax[] getEnumValueTable() {
      MediaSizeName[] var1 = new MediaSizeName[winEnumTable.size()];
      return (MediaSizeName[])((MediaSizeName[])winEnumTable.toArray(var1));
   }

   static {
      Win32MediaSize var0 = new Win32MediaSize(-1);
      MediaSizeName[] var1 = var0.getSuperEnumTable();
      if (var1 != null) {
         predefMedia = new MediaSize[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            predefMedia[var2] = MediaSize.getMediaSizeForName(var1[var2]);
         }
      }

   }
}
