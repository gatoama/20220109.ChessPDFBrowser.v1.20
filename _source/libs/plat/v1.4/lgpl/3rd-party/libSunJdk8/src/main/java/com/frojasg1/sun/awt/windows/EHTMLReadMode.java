package com.frojasg1.sun.awt.windows;

import java.awt.datatransfer.DataFlavor;

enum EHTMLReadMode {
   HTML_READ_ALL,
   HTML_READ_FRAGMENT,
   HTML_READ_SELECTION;

   private EHTMLReadMode() {
   }

   public static EHTMLReadMode getEHTMLReadMode(DataFlavor var0) {
      EHTMLReadMode var1 = HTML_READ_SELECTION;
      String var2 = var0.getParameter("document");
      if ("all".equals(var2)) {
         var1 = HTML_READ_ALL;
      } else if ("fragment".equals(var2)) {
         var1 = HTML_READ_FRAGMENT;
      }

      return var1;
   }
}
