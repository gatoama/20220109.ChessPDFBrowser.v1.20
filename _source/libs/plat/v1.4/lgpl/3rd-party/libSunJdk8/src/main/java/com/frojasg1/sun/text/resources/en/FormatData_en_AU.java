package com.frojasg1.sun.text.resources.en;

import com.frojasg1.sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_AU extends ParallelListResourceBundle {
   public FormatData_en_AU() {
   }

   protected final Object[][] getContents() {
      return new Object[][]{{"TimePatterns", new String[]{"h:mm:ss a z", "h:mm:ss a", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE, d MMMM yyyy", "d MMMM yyyy", "dd/MM/yyyy", "d/MM/yy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}};
   }
}
