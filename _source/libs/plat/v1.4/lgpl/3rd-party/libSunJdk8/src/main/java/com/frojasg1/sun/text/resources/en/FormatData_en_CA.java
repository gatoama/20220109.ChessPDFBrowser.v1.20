package com.frojasg1.sun.text.resources.en;

import com.frojasg1.sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_CA extends ParallelListResourceBundle {
   public FormatData_en_CA() {
   }

   protected final Object[][] getContents() {
      return new Object[][]{{"TimePatterns", new String[]{"h:mm:ss 'o''clock' a z", "h:mm:ss z a", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE, MMMM d, yyyy", "MMMM d, yyyy", "d-MMM-yyyy", "dd/MM/yy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}, {"DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ"}};
   }
}
