package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.RegistryFileTypeDetector;

import java.nio.file.spi.FileTypeDetector;

public class DefaultFileTypeDetector {
   private DefaultFileTypeDetector() {
   }

   public static FileTypeDetector create() {
      return new RegistryFileTypeDetector();
   }
}
