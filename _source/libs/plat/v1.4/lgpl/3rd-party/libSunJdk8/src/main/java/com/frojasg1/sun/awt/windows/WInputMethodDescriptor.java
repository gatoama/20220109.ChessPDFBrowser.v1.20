package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WInputMethod;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.im.spi.InputMethod;
import java.awt.im.spi.InputMethodDescriptor;
import java.util.Locale;

final class WInputMethodDescriptor implements InputMethodDescriptor {
   WInputMethodDescriptor() {
   }

   public Locale[] getAvailableLocales() {
      Locale[] var1 = getAvailableLocalesInternal();
      Locale[] var2 = new Locale[var1.length];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      return var2;
   }

   static Locale[] getAvailableLocalesInternal() {
      return getNativeAvailableLocales();
   }

   public boolean hasDynamicLocaleList() {
      return true;
   }

   public synchronized String getInputMethodDisplayName(Locale var1, Locale var2) {
      String var3 = "System Input Methods";
      if (Locale.getDefault().equals(var2)) {
         var3 = Toolkit.getProperty("AWT.HostInputMethodDisplayName", var3);
      }

      return var3;
   }

   public Image getInputMethodIcon(Locale var1) {
      return null;
   }

   public InputMethod createInputMethod() throws Exception {
      return new com.frojasg1.sun.awt.windows.WInputMethod();
   }

   private static native Locale[] getNativeAvailableLocales();
}
