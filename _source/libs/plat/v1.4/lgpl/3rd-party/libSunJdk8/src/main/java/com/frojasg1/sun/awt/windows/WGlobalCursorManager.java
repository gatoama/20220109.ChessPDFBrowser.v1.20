package com.frojasg1.sun.awt.windows;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Point;
import com.frojasg1.sun.awt.GlobalCursorManager;

final class WGlobalCursorManager extends GlobalCursorManager {
   private static WGlobalCursorManager manager;

   WGlobalCursorManager() {
   }

   public static GlobalCursorManager getCursorManager() {
      if (manager == null) {
         manager = new WGlobalCursorManager();
      }

      return manager;
   }

   public static void nativeUpdateCursor(Component var0) {
      getCursorManager().updateCursorLater(var0);
   }

   protected native void setCursor(Component var1, Cursor var2, boolean var3);

   protected native void getCursorPos(Point var1);

   protected native Component findHeavyweightUnderCursor(boolean var1);

   protected native Point getLocationOnScreen(Component var1);
}
