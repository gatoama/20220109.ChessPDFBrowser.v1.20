package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.WindowsFileSystemProvider;

import java.nio.file.spi.FileSystemProvider;

public class DefaultFileSystemProvider {
   private DefaultFileSystemProvider() {
   }

   public static FileSystemProvider create() {
      return new WindowsFileSystemProvider();
   }
}
