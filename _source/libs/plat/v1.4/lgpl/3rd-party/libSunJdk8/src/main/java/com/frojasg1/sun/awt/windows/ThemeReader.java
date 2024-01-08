package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public final class ThemeReader {
   private static final Map<String, Long> widgetToTheme = new HashMap();
   private static final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   private static final Lock readLock;
   private static final Lock writeLock;
   private static volatile boolean valid;
   private static volatile boolean isThemed;
   static volatile boolean xpStyleEnabled;

   public ThemeReader() {
   }

   static void flush() {
      valid = false;
   }

   private static native boolean initThemes();

   public static boolean isThemed() {
      writeLock.lock();

      boolean var0;
      try {
         isThemed = initThemes();
         var0 = isThemed;
      } finally {
         writeLock.unlock();
      }

      return var0;
   }

   public static boolean isXPStyleEnabled() {
      return xpStyleEnabled;
   }

   private static Long getThemeImpl(String var0) {
      Long var1 = (Long)widgetToTheme.get(var0);
      if (var1 == null) {
         int var2 = var0.indexOf("::");
         if (var2 > 0) {
            setWindowTheme(var0.substring(0, var2));
            var1 = openTheme(var0.substring(var2 + 2));
            setWindowTheme((String)null);
         } else {
            var1 = openTheme(var0);
         }

         widgetToTheme.put(var0, var1);
      }

      return var1;
   }

   private static Long getTheme(String var0) {
      if (!isThemed) {
         throw new IllegalStateException("Themes are not loaded");
      } else {
         if (!valid) {
            readLock.unlock();
            writeLock.lock();

            try {
               if (!valid) {
                  Iterator var1 = widgetToTheme.values().iterator();

                  while(var1.hasNext()) {
                     Long var2 = (Long)var1.next();
                     closeTheme(var2);
                  }

                  widgetToTheme.clear();
                  valid = true;
               }
            } finally {
               readLock.lock();
               writeLock.unlock();
            }
         }

         Long var11 = (Long)widgetToTheme.get(var0);
         if (var11 == null) {
            readLock.unlock();
            writeLock.lock();

            try {
               var11 = getThemeImpl(var0);
            } finally {
               readLock.lock();
               writeLock.unlock();
            }
         }

         return var11;
      }
   }

   private static native void paintBackground(int[] var0, long var1, int var3, int var4, int var5, int var6, int var7, int var8, int var9);

   public static void paintBackground(int[] var0, String var1, int var2, int var3, int var4, int var5, int var6, int var7, int var8) {
      readLock.lock();

      try {
         paintBackground(var0, getTheme(var1), var2, var3, var4, var5, var6, var7, var8);
      } finally {
         readLock.unlock();
      }

   }

   private static native Insets getThemeMargins(long var0, int var2, int var3, int var4);

   public static Insets getThemeMargins(String var0, int var1, int var2, int var3) {
      readLock.lock();

      Insets var4;
      try {
         var4 = getThemeMargins(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native boolean isThemePartDefined(long var0, int var2, int var3);

   public static boolean isThemePartDefined(String var0, int var1, int var2) {
      readLock.lock();

      boolean var3;
      try {
         var3 = isThemePartDefined(getTheme(var0), var1, var2);
      } finally {
         readLock.unlock();
      }

      return var3;
   }

   private static native Color getColor(long var0, int var2, int var3, int var4);

   public static Color getColor(String var0, int var1, int var2, int var3) {
      readLock.lock();

      Color var4;
      try {
         var4 = getColor(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native int getInt(long var0, int var2, int var3, int var4);

   public static int getInt(String var0, int var1, int var2, int var3) {
      readLock.lock();

      int var4;
      try {
         var4 = getInt(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native int getEnum(long var0, int var2, int var3, int var4);

   public static int getEnum(String var0, int var1, int var2, int var3) {
      readLock.lock();

      int var4;
      try {
         var4 = getEnum(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native boolean getBoolean(long var0, int var2, int var3, int var4);

   public static boolean getBoolean(String var0, int var1, int var2, int var3) {
      readLock.lock();

      boolean var4;
      try {
         var4 = getBoolean(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native boolean getSysBoolean(long var0, int var2);

   public static boolean getSysBoolean(String var0, int var1) {
      readLock.lock();

      boolean var2;
      try {
         var2 = getSysBoolean(getTheme(var0), var1);
      } finally {
         readLock.unlock();
      }

      return var2;
   }

   private static native Point getPoint(long var0, int var2, int var3, int var4);

   public static Point getPoint(String var0, int var1, int var2, int var3) {
      readLock.lock();

      Point var4;
      try {
         var4 = getPoint(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native Dimension getPosition(long var0, int var2, int var3, int var4);

   public static Dimension getPosition(String var0, int var1, int var2, int var3) {
      readLock.lock();

      Dimension var4;
      try {
         var4 = getPosition(getTheme(var0), var1, var2, var3);
      } finally {
         readLock.unlock();
      }

      return var4;
   }

   private static native Dimension getPartSize(long var0, int var2, int var3);

   public static Dimension getPartSize(String var0, int var1, int var2) {
      readLock.lock();

      Dimension var3;
      try {
         var3 = getPartSize(getTheme(var0), var1, var2);
      } finally {
         readLock.unlock();
      }

      return var3;
   }

   private static native long openTheme(String var0);

   private static native void closeTheme(long var0);

   private static native void setWindowTheme(String var0);

   private static native long getThemeTransitionDuration(long var0, int var2, int var3, int var4, int var5);

   public static long getThemeTransitionDuration(String var0, int var1, int var2, int var3, int var4) {
      readLock.lock();

      long var5;
      try {
         var5 = getThemeTransitionDuration(getTheme(var0), var1, var2, var3, var4);
      } finally {
         readLock.unlock();
      }

      return var5;
   }

   public static native boolean isGetThemeTransitionDurationDefined();

   private static native Insets getThemeBackgroundContentMargins(long var0, int var2, int var3, int var4, int var5);

   public static Insets getThemeBackgroundContentMargins(String var0, int var1, int var2, int var3, int var4) {
      readLock.lock();

      Insets var5;
      try {
         var5 = getThemeBackgroundContentMargins(getTheme(var0), var1, var2, var3, var4);
      } finally {
         readLock.unlock();
      }

      return var5;
   }

   static {
      readLock = readWriteLock.readLock();
      writeLock = readWriteLock.writeLock();
      valid = false;
   }
}
