package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystemException;
import java.nio.file.NoSuchFileException;

class WindowsException extends Exception {
   static final long serialVersionUID = 2765039493083748820L;
   private int lastError;
   private String msg;

   WindowsException(int var1) {
      this.lastError = var1;
      this.msg = null;
   }

   WindowsException(String var1) {
      this.lastError = 0;
      this.msg = var1;
   }

   int lastError() {
      return this.lastError;
   }

   String errorString() {
      if (this.msg == null) {
         this.msg = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FormatMessage(this.lastError);
         if (this.msg == null) {
            this.msg = "Unknown error: 0x" + Integer.toHexString(this.lastError);
         }
      }

      return this.msg;
   }

   public String getMessage() {
      return this.errorString();
   }

   private IOException translateToIOException(String var1, String var2) {
      if (this.lastError() == 0) {
         return new IOException(this.errorString());
      } else if (this.lastError() != 2 && this.lastError() != 3) {
         if (this.lastError() != 80 && this.lastError() != 183) {
            return (IOException)(this.lastError() == 5 ? new AccessDeniedException(var1, var2, (String)null) : new FileSystemException(var1, var2, this.errorString()));
         } else {
            return new FileAlreadyExistsException(var1, var2, (String)null);
         }
      } else {
         return new NoSuchFileException(var1, var2, (String)null);
      }
   }

   void rethrowAsIOException(String var1) throws IOException {
      IOException var2 = this.translateToIOException(var1, (String)null);
      throw var2;
   }

   void rethrowAsIOException(com.frojasg1.sun.nio.fs.WindowsPath var1, com.frojasg1.sun.nio.fs.WindowsPath var2) throws IOException {
      String var3 = var1 == null ? null : var1.getPathForExceptionMessage();
      String var4 = var2 == null ? null : var2.getPathForExceptionMessage();
      IOException var5 = this.translateToIOException(var3, var4);
      throw var5;
   }

   void rethrowAsIOException(com.frojasg1.sun.nio.fs.WindowsPath var1) throws IOException {
      this.rethrowAsIOException(var1, (com.frojasg1.sun.nio.fs.WindowsPath)null);
   }

   IOException asIOException(com.frojasg1.sun.nio.fs.WindowsPath var1) {
      return this.translateToIOException(var1.getPathForExceptionMessage(), (String)null);
   }
}
