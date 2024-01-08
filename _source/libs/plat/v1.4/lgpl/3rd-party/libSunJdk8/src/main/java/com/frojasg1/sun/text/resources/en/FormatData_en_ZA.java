package com.frojasg1.sun.text.resources.en;

import com.frojasg1.sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_ZA extends ParallelListResourceBundle {
   public FormatData_en_ZA() {
   }

   protected final Object[][] getContents() {
      return new Object[][]{{"NumberPatterns", new String[]{"#,##0.###;-#,##0.###", "¤ #,##0.00;¤-#,##0.00", "#,##0%"}}, {"TimePatterns", new String[]{"h:mm:ss a", "h:mm:ss a", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE dd MMMM yyyy", "dd MMMM yyyy", "dd MMM yyyy", "yyyy/MM/dd"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}};
   }
}
