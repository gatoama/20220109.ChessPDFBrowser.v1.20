package com.frojasg1.sun.management;

import com.frojasg1.sun.management.FileSystem;

import java.io.File;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class FileSystemImpl extends FileSystem {
   public FileSystemImpl() {
   }

   public boolean supportsFileSecurity(File var1) throws IOException {
      return isSecuritySupported0(var1.getAbsolutePath());
   }

   public boolean isAccessUserOnly(File var1) throws IOException {
      String var2 = var1.getAbsolutePath();
      if (!isSecuritySupported0(var2)) {
         throw new UnsupportedOperationException("File system does not support file security");
      } else {
         return isAccessUserOnly0(var2);
      }
   }

   static native void init0();

   static native boolean isSecuritySupported0(String var0) throws IOException;

   static native boolean isAccessUserOnly0(String var0) throws IOException;

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("management");
            return null;
         }
      });
      init0();
   }
}
