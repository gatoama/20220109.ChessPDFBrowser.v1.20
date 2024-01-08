package com.frojasg1.sun.font;

import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.Font2DHandle;

import java.awt.Font;

public abstract class FontAccess {
   private static FontAccess access;

   public FontAccess() {
   }

   public static synchronized void setFontAccess(FontAccess var0) {
      if (access != null) {
         throw new InternalError("Attempt to set FontAccessor twice");
      } else {
         access = var0;
      }
   }

   public static synchronized FontAccess getFontAccess() {
      return access;
   }

   public abstract Font2D getFont2D(Font var1);

   public abstract void setFont2D(Font var1, Font2DHandle var2);

   public abstract void setCreatedFont(Font var1);

   public abstract boolean isCreatedFont(Font var1);
}
