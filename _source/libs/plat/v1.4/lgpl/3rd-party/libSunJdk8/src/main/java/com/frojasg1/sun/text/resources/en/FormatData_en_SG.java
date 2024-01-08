package com.frojasg1.sun.text.resources.en;

import com.frojasg1.sun.util.resources.ParallelListResourceBundle;

public class FormatData_en_SG extends ParallelListResourceBundle {
   public FormatData_en_SG() {
   }

   protected final Object[][] getContents() {
      return new Object[][]{{"NumberPatterns", new String[]{"#,##0.###", "¤#,##0.00", "#,##0%"}}, {"NumberElements", new String[]{".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", "NaN"}}, {"DatePatterns", new String[]{"EEEE, d MMMM, yyyy", "d MMMM, yyyy", "d MMM, yyyy", "d/M/yy"}}};
   }
}
