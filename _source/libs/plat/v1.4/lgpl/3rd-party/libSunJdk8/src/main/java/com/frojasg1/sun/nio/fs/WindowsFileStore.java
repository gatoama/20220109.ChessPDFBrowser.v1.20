package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystemException;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.FileStoreAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;

class WindowsFileStore extends FileStore {
   private final String root;
   private final com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.VolumeInformation volInfo;
   private final int volType;
   private final String displayName;

   private WindowsFileStore(String var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      assert var1.charAt(var1.length() - 1) == '\\';

      this.root = var1;
      this.volInfo = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetVolumeInformation(var1);
      this.volType = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetDriveType(var1);
      String var2 = this.volInfo.volumeName();
      if (var2.length() > 0) {
         this.displayName = var2;
      } else {
         this.displayName = this.volType == 2 ? "Removable Disk" : "";
      }

   }

   static WindowsFileStore create(String var0, boolean var1) throws IOException {
      try {
         return new WindowsFileStore(var0);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var3) {
         if (var1 && var3.lastError() == 21) {
            return null;
         } else {
            var3.rethrowAsIOException(var0);
            return null;
         }
      }
   }

   static WindowsFileStore create(com.frojasg1.sun.nio.fs.WindowsPath var0) throws IOException {
      try {
         String var1;
         if (var0.getFileSystem().supportsLinks()) {
            var1 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(var0, true);
         } else {
            com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var0, true);
            var1 = var0.getPathForWin32Calls();
         }

         try {
            return createFromPath(var1);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var3) {
            if (var3.lastError() != 144) {
               throw var3;
            } else {
               var1 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(var0);
               if (var1 == null) {
                  throw new FileSystemException(var0.getPathForExceptionMessage(), (String)null, "Couldn't resolve path");
               } else {
                  return createFromPath(var1);
               }
            }
         }
      } catch (com.frojasg1.sun.nio.fs.WindowsException var4) {
         var4.rethrowAsIOException(var0);
         return null;
      }
   }

   private static WindowsFileStore createFromPath(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      String var1 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetVolumePathName(var0);
      return new WindowsFileStore(var1);
   }

   com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.VolumeInformation volumeInformation() {
      return this.volInfo;
   }

   int volumeType() {
      return this.volType;
   }

   public String name() {
      return this.volInfo.volumeName();
   }

   public String type() {
      return this.volInfo.fileSystemName();
   }

   public boolean isReadOnly() {
      return (this.volInfo.flags() & 524288) != 0;
   }

   private com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DiskFreeSpace readDiskFreeSpace() throws IOException {
      try {
         return com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetDiskFreeSpaceEx(this.root);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var2) {
         var2.rethrowAsIOException(this.root);
         return null;
      }
   }

   public long getTotalSpace() throws IOException {
      return this.readDiskFreeSpace().totalNumberOfBytes();
   }

   public long getUsableSpace() throws IOException {
      return this.readDiskFreeSpace().freeBytesAvailable();
   }

   public long getUnallocatedSpace() throws IOException {
      return this.readDiskFreeSpace().freeBytesAvailable();
   }

   public <V extends FileStoreAttributeView> V getFileStoreAttributeView(Class<V> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         return null;
      }
   }

   public Object getAttribute(String var1) throws IOException {
      if (var1.equals("totalSpace")) {
         return this.getTotalSpace();
      } else if (var1.equals("usableSpace")) {
         return this.getUsableSpace();
      } else if (var1.equals("unallocatedSpace")) {
         return this.getUnallocatedSpace();
      } else if (var1.equals("volume:vsn")) {
         return this.volInfo.volumeSerialNumber();
      } else if (var1.equals("volume:isRemovable")) {
         return this.volType == 2;
      } else if (var1.equals("volume:isCdrom")) {
         return this.volType == 5;
      } else {
         throw new UnsupportedOperationException("'" + var1 + "' not recognized");
      }
   }

   public boolean supportsFileAttributeView(Class<? extends FileAttributeView> var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (var1 != BasicFileAttributeView.class && var1 != DosFileAttributeView.class) {
         if (var1 != AclFileAttributeView.class && var1 != FileOwnerAttributeView.class) {
            if (var1 == UserDefinedFileAttributeView.class) {
               return (this.volInfo.flags() & 262144) != 0;
            } else {
               return false;
            }
         } else {
            return (this.volInfo.flags() & 8) != 0;
         }
      } else {
         return true;
      }
   }

   public boolean supportsFileAttributeView(String var1) {
      if (!var1.equals("basic") && !var1.equals("dos")) {
         if (var1.equals("acl")) {
            return this.supportsFileAttributeView(AclFileAttributeView.class);
         } else if (var1.equals("owner")) {
            return this.supportsFileAttributeView(FileOwnerAttributeView.class);
         } else {
            return var1.equals("user") ? this.supportsFileAttributeView(UserDefinedFileAttributeView.class) : false;
         }
      } else {
         return true;
      }
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof WindowsFileStore)) {
         return false;
      } else {
         WindowsFileStore var2 = (WindowsFileStore)var1;
         return this.root.equals(var2.root);
      }
   }

   public int hashCode() {
      return this.root.hashCode();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder(this.displayName);
      if (var1.length() > 0) {
         var1.append(" ");
      }

      var1.append("(");
      var1.append(this.root.subSequence(0, this.root.length() - 1));
      var1.append(")");
      return var1.toString();
   }
}
