package com.frojasg1.sun.text.resources.en;

import com.frojasg1.sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_IE extends ParallelListResourceBundle {
   public FormatData_en_IE() {
   }

   protected final Object[][] getContents() {
      return new Object[][]{{"TimePatterns", new String[]{"HH:mm:ss 'o''clock' z", "HH:mm:ss z", "HH:mm:ss", "HH:mm"}}, {"DatePatterns", new String[]{"dd MMMM yyyy", "dd MMMM yyyy", "dd-MMM-yyyy", "dd/MM/yy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}, {"DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ"}};
   }
}
