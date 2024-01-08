package com.frojasg1.sun.management;

import com.frojasg1.sun.management.FileSystemImpl;

import java.io.File;
import java.io.IOException;

public abstract class FileSystem {
   private static final Object lock = new Object();
   private static FileSystem fs;

   protected FileSystem() {
   }

   public static FileSystem open() {
      synchronized(lock) {
         if (fs == null) {
            fs = new FileSystemImpl();
         }

         return fs;
      }
   }

   public abstract boolean supportsFileSecurity(File var1) throws IOException;

   public abstract boolean isAccessUserOnly(File var1) throws IOException;
}
