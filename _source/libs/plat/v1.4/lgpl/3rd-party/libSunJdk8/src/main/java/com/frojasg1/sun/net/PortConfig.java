package com.frojasg1.sun.net;

import java.security.AccessController;
import java.security.PrivilegedAction;

public final class PortConfig {
   private static int defaultUpper;
   private static int defaultLower;
   private static final int upper;
   private static final int lower;

   public PortConfig() {
   }

   static native int getLower0();

   static native int getUpper0();

   public static int getLower() {
      return lower;
   }

   public static int getUpper() {
      return upper;
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
      int var0 = getLower0();
      if (var0 == -1) {
         var0 = defaultLower;
      }

      lower = var0;
      var0 = getUpper0();
      if (var0 == -1) {
         var0 = defaultUpper;
      }

      upper = var0;
   }
}
