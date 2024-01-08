package com.frojasg1.sun.awt.image;

import java.security.AccessController;
import java.security.PrivilegedAction;

class NativeLibLoader {
   NativeLibLoader() {
   }

   static void loadLibraries() {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
   }
}
