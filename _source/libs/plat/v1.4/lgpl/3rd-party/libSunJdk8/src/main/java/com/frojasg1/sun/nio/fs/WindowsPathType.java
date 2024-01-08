package com.frojasg1.sun.nio.fs;

enum WindowsPathType {
   ABSOLUTE,
   UNC,
   RELATIVE,
   DIRECTORY_RELATIVE,
   DRIVE_RELATIVE;

   private WindowsPathType() {
   }
}
