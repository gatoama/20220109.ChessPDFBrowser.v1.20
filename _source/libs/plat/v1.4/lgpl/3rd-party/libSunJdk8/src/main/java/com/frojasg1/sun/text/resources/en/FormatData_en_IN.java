package com.frojasg1.sun.text.resources.en;

import com.frojasg1.sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_IN extends ParallelListResourceBundle {
   public FormatData_en_IN() {
   }

   protected final Object[][] getContents() {
      return new Object[][]{{"NumberElements", new String[]{".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "�"}}, {"TimePatterns", new String[]{"h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a"}}, {"DatePatterns", new String[]{"EEEE, d MMMM, yyyy", "d MMMM, yyyy", "d MMM, yyyy", "d/M/yy"}}, {"DateTimePatterns", new String[]{"{1} {0}"}}, {"DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ"}};
   }
}
