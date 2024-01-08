package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.AbstractBasicFileAttributeView;
import com.frojasg1.sun.nio.fs.Util;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsFileStore;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

import java.io.IOException;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.util.Map;
import java.util.Set;

class WindowsFileAttributeViews {
   WindowsFileAttributeViews() {
   }

   static WindowsFileAttributeViews.Basic createBasicView(com.frojasg1.sun.nio.fs.WindowsPath var0, boolean var1) {
      return new WindowsFileAttributeViews.Basic(var0, var1);
   }

   static WindowsFileAttributeViews.Dos createDosView(com.frojasg1.sun.nio.fs.WindowsPath var0, boolean var1) {
      return new WindowsFileAttributeViews.Dos(var0, var1);
   }

   private static class Basic extends com.frojasg1.sun.nio.fs.AbstractBasicFileAttributeView {
      final com.frojasg1.sun.nio.fs.WindowsPath file;
      final boolean followLinks;

      Basic(com.frojasg1.sun.nio.fs.WindowsPath var1, boolean var2) {
         this.file = var1;
         this.followLinks = var2;
      }

      public com.frojasg1.sun.nio.fs.WindowsFileAttributes readAttributes() throws IOException {
         this.file.checkRead();

         try {
            return com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(this.file, this.followLinks);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var2) {
            var2.rethrowAsIOException(this.file);
            return null;
         }
      }

      private long adjustForFatEpoch(long var1) {
         return var1 != -1L && var1 < 119600064000000000L ? 119600064000000000L : var1;
      }

      void setFileTimes(long var1, long var3, long var5) throws IOException {
         long var7 = -1L;

         try {
            int var9 = 33554432;
            if (!this.followLinks && this.file.getFileSystem().supportsLinks()) {
               var9 |= 2097152;
            }

            var7 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), 256, 7, 3, var9);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var21) {
            var21.rethrowAsIOException(this.file);
         }

         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetFileTime(var7, var1, var3, var5);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var22) {
            com.frojasg1.sun.nio.fs.WindowsException var24 = var22;
            if (this.followLinks && var22.lastError() == 87) {
               try {
                  if (com.frojasg1.sun.nio.fs.WindowsFileStore.create(this.file).type().equals("FAT")) {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetFileTime(var7, this.adjustForFatEpoch(var1), this.adjustForFatEpoch(var3), this.adjustForFatEpoch(var5));
                     var24 = null;
                  }
               } catch (SecurityException var18) {
               } catch (com.frojasg1.sun.nio.fs.WindowsException var19) {
               } catch (IOException var20) {
               }
            }

            if (var24 != null) {
               var24.rethrowAsIOException(this.file);
            }
         } finally {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var7);
         }

      }

      public void setTimes(FileTime var1, FileTime var2, FileTime var3) throws IOException {
         if (var1 != null || var2 != null || var3 != null) {
            this.file.checkWrite();
            long var4 = var3 == null ? -1L : com.frojasg1.sun.nio.fs.WindowsFileAttributes.toWindowsTime(var3);
            long var6 = var2 == null ? -1L : com.frojasg1.sun.nio.fs.WindowsFileAttributes.toWindowsTime(var2);
            long var8 = var1 == null ? -1L : com.frojasg1.sun.nio.fs.WindowsFileAttributes.toWindowsTime(var1);
            this.setFileTimes(var4, var6, var8);
         }
      }
   }

   static class Dos extends WindowsFileAttributeViews.Basic implements DosFileAttributeView {
      private static final String READONLY_NAME = "readonly";
      private static final String ARCHIVE_NAME = "archive";
      private static final String SYSTEM_NAME = "system";
      private static final String HIDDEN_NAME = "hidden";
      private static final String ATTRIBUTES_NAME = "attributes";
      static final Set<String> dosAttributeNames;

      Dos(com.frojasg1.sun.nio.fs.WindowsPath var1, boolean var2) {
         super(var1, var2);
      }

      public String name() {
         return "dos";
      }

      public void setAttribute(String var1, Object var2) throws IOException {
         if (var1.equals("readonly")) {
            this.setReadOnly((Boolean)var2);
         } else if (var1.equals("archive")) {
            this.setArchive((Boolean)var2);
         } else if (var1.equals("system")) {
            this.setSystem((Boolean)var2);
         } else if (var1.equals("hidden")) {
            this.setHidden((Boolean)var2);
         } else {
            super.setAttribute(var1, var2);
         }
      }

      public Map<String, Object> readAttributes(String[] var1) throws IOException {
         com.frojasg1.sun.nio.fs.AbstractBasicFileAttributeView.AttributesBuilder var2 = com.frojasg1.sun.nio.fs.AbstractBasicFileAttributeView.AttributesBuilder.create(dosAttributeNames, var1);
         com.frojasg1.sun.nio.fs.WindowsFileAttributes var3 = this.readAttributes();
         this.addRequestedBasicAttributes(var3, var2);
         if (var2.match("readonly")) {
            var2.add("readonly", var3.isReadOnly());
         }

         if (var2.match("archive")) {
            var2.add("archive", var3.isArchive());
         }

         if (var2.match("system")) {
            var2.add("system", var3.isSystem());
         }

         if (var2.match("hidden")) {
            var2.add("hidden", var3.isHidden());
         }

         if (var2.match("attributes")) {
            var2.add("attributes", var3.attributes());
         }

         return var2.unmodifiableMap();
      }

      private void updateAttributes(int var1, boolean var2) throws IOException {
         this.file.checkWrite();
         String var3 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(this.file, this.followLinks);

         try {
            int var4 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFileAttributes(var3);
            int var5;
            if (var2) {
               var5 = var4 | var1;
            } else {
               var5 = var4 & ~var1;
            }

            if (var5 != var4) {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetFileAttributes(var3, var5);
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var6) {
            var6.rethrowAsIOException(this.file);
         }

      }

      public void setReadOnly(boolean var1) throws IOException {
         this.updateAttributes(1, var1);
      }

      public void setHidden(boolean var1) throws IOException {
         this.updateAttributes(2, var1);
      }

      public void setArchive(boolean var1) throws IOException {
         this.updateAttributes(32, var1);
      }

      public void setSystem(boolean var1) throws IOException {
         this.updateAttributes(4, var1);
      }

      void setAttributes(com.frojasg1.sun.nio.fs.WindowsFileAttributes var1) throws IOException {
         int var2 = 0;
         if (var1.isReadOnly()) {
            var2 |= 1;
         }

         if (var1.isHidden()) {
            var2 |= 2;
         }

         if (var1.isArchive()) {
            var2 |= 32;
         }

         if (var1.isSystem()) {
            var2 |= 4;
         }

         this.updateAttributes(var2, true);
         this.setFileTimes(com.frojasg1.sun.nio.fs.WindowsFileAttributes.toWindowsTime(var1.creationTime()), com.frojasg1.sun.nio.fs.WindowsFileAttributes.toWindowsTime(var1.lastModifiedTime()), com.frojasg1.sun.nio.fs.WindowsFileAttributes.toWindowsTime(var1.lastAccessTime()));
      }

      static {
         dosAttributeNames = com.frojasg1.sun.nio.fs.Util.newSet(basicAttributeNames, "readonly", "archive", "system", "hidden", "attributes");
      }
   }
}
