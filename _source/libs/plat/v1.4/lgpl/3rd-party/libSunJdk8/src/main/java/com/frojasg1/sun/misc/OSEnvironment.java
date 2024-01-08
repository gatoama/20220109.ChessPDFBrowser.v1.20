package com.frojasg1.sun.misc;

import com.frojasg1.sun.io.Win32ErrorMode;

public class OSEnvironment {
   public OSEnvironment() {
   }

   public static void initialize() {
      Win32ErrorMode.initialize();
   }
}
