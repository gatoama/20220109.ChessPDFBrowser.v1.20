package com.frojasg1.sun.awt.windows;

import java.awt.Component;
import java.awt.Image;

public final class WBufferStrategy {
   public WBufferStrategy() {
   }

   private static native void initIDs(Class<?> var0);

   public static native Image getDrawBuffer(Component var0);

   static {
      initIDs(Component.class);
   }
}
