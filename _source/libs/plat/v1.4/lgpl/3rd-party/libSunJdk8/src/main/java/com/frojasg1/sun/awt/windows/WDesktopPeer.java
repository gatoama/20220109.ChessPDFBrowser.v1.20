package com.frojasg1.sun.awt.windows;

import java.awt.Desktop.Action;
import java.awt.peer.DesktopPeer;
import java.io.File;
import java.io.IOException;
import java.net.URI;

final class WDesktopPeer implements DesktopPeer {
   private static String ACTION_OPEN_VERB = "open";
   private static String ACTION_EDIT_VERB = "edit";
   private static String ACTION_PRINT_VERB = "print";

   WDesktopPeer() {
   }

   public boolean isSupported(Action var1) {
      return true;
   }

   public void open(File var1) throws IOException {
      this.ShellExecute(var1, ACTION_OPEN_VERB);
   }

   public void edit(File var1) throws IOException {
      this.ShellExecute(var1, ACTION_EDIT_VERB);
   }

   public void print(File var1) throws IOException {
      this.ShellExecute(var1, ACTION_PRINT_VERB);
   }

   public void mail(URI var1) throws IOException {
      this.ShellExecute(var1, ACTION_OPEN_VERB);
   }

   public void browse(URI var1) throws IOException {
      this.ShellExecute(var1, ACTION_OPEN_VERB);
   }

   private void ShellExecute(File var1, String var2) throws IOException {
      String var3 = ShellExecute(var1.getAbsolutePath(), var2);
      if (var3 != null) {
         throw new IOException("Failed to " + var2 + " " + var1 + ". Error message: " + var3);
      }
   }

   private void ShellExecute(URI var1, String var2) throws IOException {
      String var3 = ShellExecute(var1.toString(), var2);
      if (var3 != null) {
         throw new IOException("Failed to " + var2 + " " + var1 + ". Error message: " + var3);
      }
   }

   private static native String ShellExecute(String var0, String var1);
}
