package com.frojasg1.sun.font;

import com.frojasg1.sun.font.FontManager;
import com.frojasg1.sun.font.FontUtilities;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class FontManagerFactory {
   private static com.frojasg1.sun.font.FontManager instance = null;
   private static final String DEFAULT_CLASS;

   public FontManagerFactory() {
   }

   public static synchronized com.frojasg1.sun.font.FontManager getInstance() {
      if (instance != null) {
         return instance;
      } else {
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               try {
                  String var1 = System.getProperty("sun.font.fontmanager", FontManagerFactory.DEFAULT_CLASS);
                  ClassLoader var2 = ClassLoader.getSystemClassLoader();
                  Class var3 = Class.forName(var1, true, var2);
                  FontManagerFactory.instance = (FontManager)var3.newInstance();
                  return null;
               } catch (InstantiationException | IllegalAccessException | ClassNotFoundException var4) {
                  throw new InternalError(var4);
               }
            }
         });
         return instance;
      }
   }

   static {
      if (com.frojasg1.sun.font.FontUtilities.isWindows) {
         DEFAULT_CLASS = "sun.awt.Win32FontManager";
      } else if (FontUtilities.isMacOSX) {
         DEFAULT_CLASS = "sun.font.CFontManager";
      } else {
         DEFAULT_CLASS = "sun.awt.X11FontManager";
      }

   }
}
