package com.frojasg1.sun.font;

import com.frojasg1.sun.font.CreatedFontTracker;
import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.Font2DHandle;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;

public interface FontManager {
   int NO_FALLBACK = 0;
   int PHYSICAL_FALLBACK = 1;
   int LOGICAL_FALLBACK = 2;

   boolean registerFont(Font var1);

   void deRegisterBadFont(com.frojasg1.sun.font.Font2D var1);

   com.frojasg1.sun.font.Font2D findFont2D(String var1, int var2, int var3);

   Font2D createFont2D(File var1, int var2, boolean var3, CreatedFontTracker var4) throws FontFormatException;

   boolean usingPerAppContextComposites();

   com.frojasg1.sun.font.Font2DHandle getNewComposite(String var1, int var2, Font2DHandle var3);

   void preferLocaleFonts();

   void preferProportionalFonts();
}
