package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Iterator;
import java.util.NoSuchElementException;

class WindowsDirectoryStream implements DirectoryStream<Path> {
   private final com.frojasg1.sun.nio.fs.WindowsPath dir;
   private final Filter<? super Path> filter;
   private final long handle;
   private final String firstName;
   private final com.frojasg1.sun.nio.fs.NativeBuffer findDataBuffer;
   private final Object closeLock = new Object();
   private boolean isOpen = true;
   private Iterator<Path> iterator;

   WindowsDirectoryStream(com.frojasg1.sun.nio.fs.WindowsPath var1, Filter<? super Path> var2) throws IOException {
      this.dir = var1;
      this.filter = var2;

      try {
         String var3 = var1.getPathForWin32Calls();
         char var4 = var3.charAt(var3.length() - 1);
         if (var4 != ':' && var4 != '\\') {
            var3 = var3 + "\\*";
         } else {
            var3 = var3 + "*";
         }

         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FirstFile var5 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindFirstFile(var3);
         this.handle = var5.handle();
         this.firstName = var5.name();
         this.findDataBuffer = com.frojasg1.sun.nio.fs.WindowsFileAttributes.getBufferForFindData();
      } catch (com.frojasg1.sun.nio.fs.WindowsException var6) {
         if (var6.lastError() == 267) {
            throw new NotDirectoryException(var1.getPathForExceptionMessage());
         } else {
            var6.rethrowAsIOException(var1);
            throw new AssertionError();
         }
      }
   }

   public void close() throws IOException {
      synchronized(this.closeLock) {
         if (!this.isOpen) {
            return;
         }

         this.isOpen = false;
      }

      this.findDataBuffer.release();

      try {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindClose(this.handle);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var3) {
         var3.rethrowAsIOException(this.dir);
      }

   }

   public Iterator<Path> iterator() {
      if (!this.isOpen) {
         throw new IllegalStateException("Directory stream is closed");
      } else {
         synchronized(this) {
            if (this.iterator != null) {
               throw new IllegalStateException("Iterator already obtained");
            } else {
               this.iterator = new WindowsDirectoryStream.WindowsDirectoryIterator(this.firstName);
               return this.iterator;
            }
         }
      }
   }

   private class WindowsDirectoryIterator implements Iterator<Path> {
      private boolean atEof = false;
      private String first;
      private Path nextEntry;
      private String prefix;

      WindowsDirectoryIterator(String var2) {
         this.first = var2;
         if (WindowsDirectoryStream.this.dir.needsSlashWhenResolving()) {
            this.prefix = WindowsDirectoryStream.this.dir.toString() + "\\";
         } else {
            this.prefix = WindowsDirectoryStream.this.dir.toString();
         }

      }

      private boolean isSelfOrParent(String var1) {
         return var1.equals(".") || var1.equals("..");
      }

      private Path acceptEntry(String var1, BasicFileAttributes var2) {
         com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(WindowsDirectoryStream.this.dir.getFileSystem(), this.prefix + var1, var2);

         try {
            return WindowsDirectoryStream.this.filter.accept(var3) ? var3 : null;
         } catch (IOException var5) {
            throw new DirectoryIteratorException(var5);
         }
      }

      private Path readNextEntry() {
         if (this.first != null) {
            this.nextEntry = this.isSelfOrParent(this.first) ? null : this.acceptEntry(this.first, (BasicFileAttributes)null);
            this.first = null;
            if (this.nextEntry != null) {
               return this.nextEntry;
            }
         }

         Path var3;
         do {
            String var1;
            com.frojasg1.sun.nio.fs.WindowsFileAttributes var2;
            while(true) {
               var1 = null;
               synchronized(WindowsDirectoryStream.this.closeLock) {
                  try {
                     if (WindowsDirectoryStream.this.isOpen) {
                        var1 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindNextFile(WindowsDirectoryStream.this.handle, WindowsDirectoryStream.this.findDataBuffer.address());
                     }
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var7) {
                     IOException var5 = var7.asIOException(WindowsDirectoryStream.this.dir);
                     throw new DirectoryIteratorException(var5);
                  }

                  if (var1 == null) {
                     this.atEof = true;
                     return null;
                  }

                  if (!this.isSelfOrParent(var1)) {
                     var2 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.fromFindData(WindowsDirectoryStream.this.findDataBuffer.address());
                     break;
                  }
               }
            }

            var3 = this.acceptEntry(var1, var2);
         } while(var3 == null);

         return var3;
      }

      public synchronized boolean hasNext() {
         if (this.nextEntry == null && !this.atEof) {
            this.nextEntry = this.readNextEntry();
         }

         return this.nextEntry != null;
      }

      public synchronized Path next() {
         Path var1 = null;
         if (this.nextEntry == null && !this.atEof) {
            var1 = this.readNextEntry();
         } else {
            var1 = this.nextEntry;
            this.nextEntry = null;
         }

         if (var1 == null) {
            throw new NoSuchElementException();
         } else {
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }
}
