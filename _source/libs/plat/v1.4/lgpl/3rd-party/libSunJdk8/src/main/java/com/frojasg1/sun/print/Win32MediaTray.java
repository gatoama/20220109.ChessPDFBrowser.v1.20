package com.frojasg1.sun.print;

import java.util.ArrayList;
import javax.print.attribute.EnumSyntax;
import javax.print.attribute.standard.MediaTray;

public class Win32MediaTray extends MediaTray {
   static final Win32MediaTray ENVELOPE_MANUAL = new Win32MediaTray(0, 6);
   static final Win32MediaTray AUTO = new Win32MediaTray(1, 7);
   static final Win32MediaTray TRACTOR = new Win32MediaTray(2, 8);
   static final Win32MediaTray SMALL_FORMAT = new Win32MediaTray(3, 9);
   static final Win32MediaTray LARGE_FORMAT = new Win32MediaTray(4, 10);
   static final Win32MediaTray FORMSOURCE = new Win32MediaTray(5, 15);
   private static ArrayList winStringTable = new ArrayList();
   private static ArrayList winEnumTable = new ArrayList();
   public int winID;
   private static final String[] myStringTable = new String[]{"Manual-Envelope", "Automatic-Feeder", "Tractor-Feeder", "Small-Format", "Large-Format", "Form-Source"};
   private static final MediaTray[] myEnumValueTable;

   private Win32MediaTray(int var1, int var2) {
      super(var1);
      this.winID = var2;
   }

   private static synchronized int nextValue(String var0) {
      winStringTable.add(var0);
      return getTraySize() - 1;
   }

   protected Win32MediaTray(int var1, String var2) {
      super(nextValue(var2));
      this.winID = var1;
      winEnumTable.add(this);
   }

   public int getDMBinID() {
      return this.winID;
   }

   protected static int getTraySize() {
      return myStringTable.length + winStringTable.size();
   }

   protected String[] getStringTable() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < myStringTable.length; ++var2) {
         var1.add(myStringTable[var2]);
      }

      var1.addAll(winStringTable);
      String[] var3 = new String[var1.size()];
      return (String[])((String[])var1.toArray(var3));
   }

   protected EnumSyntax[] getEnumValueTable() {
      ArrayList var1 = new ArrayList();

      for(int var2 = 0; var2 < myEnumValueTable.length; ++var2) {
         var1.add(myEnumValueTable[var2]);
      }

      var1.addAll(winEnumTable);
      MediaTray[] var3 = new MediaTray[var1.size()];
      return (MediaTray[])((MediaTray[])var1.toArray(var3));
   }

   static {
      myEnumValueTable = new MediaTray[]{ENVELOPE_MANUAL, AUTO, TRACTOR, SMALL_FORMAT, LARGE_FORMAT, FORMSOURCE};
   }
}
