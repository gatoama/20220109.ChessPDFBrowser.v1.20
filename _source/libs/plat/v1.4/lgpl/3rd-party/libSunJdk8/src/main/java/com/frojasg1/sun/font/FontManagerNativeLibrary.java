package com.frojasg1.sun.font;

import com.frojasg1.sun.font.FontUtilities;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class FontManagerNativeLibrary {
   public FontManagerNativeLibrary() {
   }

   public static void load() {
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            System.loadLibrary("awt");
            if (FontUtilities.isOpenJDK && System.getProperty("os.name").startsWith("Windows")) {
               System.loadLibrary("freetype");
            }

            System.loadLibrary("fontmanager");
            return null;
         }
      });
   }
}
