package com.frojasg1.sun.nio.fs;

import java.nio.file.attribute.BasicFileAttributes;

public interface BasicFileAttributesHolder {
   BasicFileAttributes get();

   void invalidate();
}
