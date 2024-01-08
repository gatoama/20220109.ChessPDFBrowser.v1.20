package com.frojasg1.sun.awt.shell;

import com.frojasg1.sun.awt.shell.DefaultShellFolder;
import com.frojasg1.sun.awt.shell.ShellFolder;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.concurrent.Callable;

class ShellFolderManager {
   ShellFolderManager() {
   }

   public com.frojasg1.sun.awt.shell.ShellFolder createShellFolder(File var1) throws FileNotFoundException {
      return new com.frojasg1.sun.awt.shell.DefaultShellFolder((com.frojasg1.sun.awt.shell.ShellFolder)null, var1);
   }

   public Object get(String var1) {
      if (var1.equals("fileChooserDefaultFolder")) {
         File var2 = new File(System.getProperty("user.home"));

         try {
            return this.createShellFolder(var2);
         } catch (FileNotFoundException var4) {
            return var2;
         }
      } else if (var1.equals("roots")) {
         return File.listRoots();
      } else if (var1.equals("fileChooserComboBoxFolders")) {
         return this.get("roots");
      } else {
         return var1.equals("fileChooserShortcutPanelFolders") ? new File[]{(File)this.get("fileChooserDefaultFolder")} : null;
      }
   }

   public boolean isComputerNode(File var1) {
      return false;
   }

   public boolean isFileSystemRoot(File var1) {
      if (var1 instanceof com.frojasg1.sun.awt.shell.ShellFolder && !((com.frojasg1.sun.awt.shell.ShellFolder)var1).isFileSystem()) {
         return false;
      } else {
         return var1.getParentFile() == null;
      }
   }

   protected com.frojasg1.sun.awt.shell.ShellFolder.Invoker createInvoker() {
      return new ShellFolderManager.DirectInvoker();
   }

   private static class DirectInvoker implements ShellFolder.Invoker {
      private DirectInvoker() {
      }

      public <T> T invoke(Callable<T> var1) throws Exception {
         return var1.call();
      }
   }
}
