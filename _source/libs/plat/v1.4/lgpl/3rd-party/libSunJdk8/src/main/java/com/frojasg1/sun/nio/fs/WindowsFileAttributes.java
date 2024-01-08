package com.frojasg1.sun.nio.fs;

import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileTime;
import java.security.AccessController;
import java.util.concurrent.TimeUnit;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;
import com.frojasg1.sun.security.action.GetPropertyAction;

class WindowsFileAttributes implements DosFileAttributes {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final short SIZEOF_FILE_INFORMATION = 52;
   private static final short OFFSETOF_FILE_INFORMATION_ATTRIBUTES = 0;
   private static final short OFFSETOF_FILE_INFORMATION_CREATETIME = 4;
   private static final short OFFSETOF_FILE_INFORMATION_LASTACCESSTIME = 12;
   private static final short OFFSETOF_FILE_INFORMATION_LASTWRITETIME = 20;
   private static final short OFFSETOF_FILE_INFORMATION_VOLSERIALNUM = 28;
   private static final short OFFSETOF_FILE_INFORMATION_SIZEHIGH = 32;
   private static final short OFFSETOF_FILE_INFORMATION_SIZELOW = 36;
   private static final short OFFSETOF_FILE_INFORMATION_INDEXHIGH = 44;
   private static final short OFFSETOF_FILE_INFORMATION_INDEXLOW = 48;
   private static final short SIZEOF_FILE_ATTRIBUTE_DATA = 36;
   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_ATTRIBUTES = 0;
   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_CREATETIME = 4;
   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTACCESSTIME = 12;
   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_LASTWRITETIME = 20;
   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZEHIGH = 28;
   private static final short OFFSETOF_FILE_ATTRIBUTE_DATA_SIZELOW = 32;
   private static final short SIZEOF_FIND_DATA = 592;
   private static final short OFFSETOF_FIND_DATA_ATTRIBUTES = 0;
   private static final short OFFSETOF_FIND_DATA_CREATETIME = 4;
   private static final short OFFSETOF_FIND_DATA_LASTACCESSTIME = 12;
   private static final short OFFSETOF_FIND_DATA_LASTWRITETIME = 20;
   private static final short OFFSETOF_FIND_DATA_SIZEHIGH = 28;
   private static final short OFFSETOF_FIND_DATA_SIZELOW = 32;
   private static final short OFFSETOF_FIND_DATA_RESERVED0 = 36;
   private static final long WINDOWS_EPOCH_IN_MICROSECONDS = -11644473600000000L;
   private static final boolean ensureAccurateMetadata;
   private final int fileAttrs;
   private final long creationTime;
   private final long lastAccessTime;
   private final long lastWriteTime;
   private final long size;
   private final int reparseTag;
   private final int volSerialNumber;
   private final int fileIndexHigh;
   private final int fileIndexLow;

   static FileTime toFileTime(long var0) {
      var0 /= 10L;
      var0 += -11644473600000000L;
      return FileTime.from(var0, TimeUnit.MICROSECONDS);
   }

   static long toWindowsTime(FileTime var0) {
      long var1 = var0.to(TimeUnit.MICROSECONDS);
      var1 -= -11644473600000000L;
      var1 *= 10L;
      return var1;
   }

   private WindowsFileAttributes(int var1, long var2, long var4, long var6, long var8, int var10, int var11, int var12, int var13) {
      this.fileAttrs = var1;
      this.creationTime = var2;
      this.lastAccessTime = var4;
      this.lastWriteTime = var6;
      this.size = var8;
      this.reparseTag = var10;
      this.volSerialNumber = var11;
      this.fileIndexHigh = var12;
      this.fileIndexLow = var13;
   }

   private static WindowsFileAttributes fromFileInformation(long var0, int var2) {
      int var3 = unsafe.getInt(var0 + 0L);
      long var4 = unsafe.getLong(var0 + 4L);
      long var6 = unsafe.getLong(var0 + 12L);
      long var8 = unsafe.getLong(var0 + 20L);
      long var10 = ((long)unsafe.getInt(var0 + 32L) << 32) + ((long)unsafe.getInt(var0 + 36L) & 4294967295L);
      int var12 = unsafe.getInt(var0 + 28L);
      int var13 = unsafe.getInt(var0 + 44L);
      int var14 = unsafe.getInt(var0 + 48L);
      return new WindowsFileAttributes(var3, var4, var6, var8, var10, var2, var12, var13, var14);
   }

   private static WindowsFileAttributes fromFileAttributeData(long var0, int var2) {
      int var3 = unsafe.getInt(var0 + 0L);
      long var4 = unsafe.getLong(var0 + 4L);
      long var6 = unsafe.getLong(var0 + 12L);
      long var8 = unsafe.getLong(var0 + 20L);
      long var10 = ((long)unsafe.getInt(var0 + 28L) << 32) + ((long)unsafe.getInt(var0 + 32L) & 4294967295L);
      return new WindowsFileAttributes(var3, var4, var6, var8, var10, var2, 0, 0, 0);
   }

   static com.frojasg1.sun.nio.fs.NativeBuffer getBufferForFindData() {
      return com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(592);
   }

   static WindowsFileAttributes fromFindData(long var0) {
      int var2 = unsafe.getInt(var0 + 0L);
      long var3 = unsafe.getLong(var0 + 4L);
      long var5 = unsafe.getLong(var0 + 12L);
      long var7 = unsafe.getLong(var0 + 20L);
      long var9 = ((long)unsafe.getInt(var0 + 28L) << 32) + ((long)unsafe.getInt(var0 + 32L) & 4294967295L);
      int var11 = isReparsePoint(var2) ? unsafe.getInt(var0 + 36L) : 0;
      return new WindowsFileAttributes(var2, var3, var5, var7, var9, var11, 0, 0, 0);
   }

   static WindowsFileAttributes readAttributes(long var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var2 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(52);

      WindowsFileAttributes var17;
      try {
         long var3 = var2.address();
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFileInformationByHandle(var0, var3);
         int var5 = 0;
         int var6 = unsafe.getInt(var3 + 0L);
         if (isReparsePoint(var6)) {
            short var7 = 16384;
            com.frojasg1.sun.nio.fs.NativeBuffer var8 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var7);

            try {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(var0, var8.address(), var7);
               var5 = (int)unsafe.getLong(var8.address());
            } finally {
               var8.release();
            }
         }

         var17 = fromFileInformation(var3, var5);
      } finally {
         var2.release();
      }

      return var17;
   }

   static WindowsFileAttributes get(com.frojasg1.sun.nio.fs.WindowsPath var0, boolean var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      if (!ensureAccurateMetadata) {
         com.frojasg1.sun.nio.fs.WindowsException var2 = null;
         com.frojasg1.sun.nio.fs.NativeBuffer var3 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(36);

         try {
            long var4 = var3.address();
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFileAttributesEx(var0.getPathForWin32Calls(), var4);
            int var6 = unsafe.getInt(var4 + 0L);
            if (!isReparsePoint(var6)) {
               WindowsFileAttributes var7 = fromFileAttributeData(var4, 0);
               return var7;
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var30) {
            if (var30.lastError() != 32) {
               throw var30;
            }

            var2 = var30;
         } finally {
            var3.release();
         }

         if (var2 != null) {
            String var34 = var0.getPathForWin32Calls();
            char var5 = var34.charAt(var34.length() - 1);
            if (var5 != ':' && var5 != '\\') {
               var3 = getBufferForFindData();

               WindowsFileAttributes var9;
               try {
                  long var35 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindFirstFile(var34, var3.address());
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindClose(var35);
                  WindowsFileAttributes var8 = fromFindData(var3.address());
                  if (var8.isReparsePoint()) {
                     throw var2;
                  }

                  var9 = var8;
               } catch (com.frojasg1.sun.nio.fs.WindowsException var28) {
                  throw var2;
               } finally {
                  var3.release();
               }

               return var9;
            }

            throw var2;
         }
      }

      long var32 = var0.openForReadAttributeAccess(var1);

      WindowsFileAttributes var33;
      try {
         var33 = readAttributes(var32);
      } finally {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var32);
      }

      return var33;
   }

   static boolean isSameFile(WindowsFileAttributes var0, WindowsFileAttributes var1) {
      return var0.volSerialNumber == var1.volSerialNumber && var0.fileIndexHigh == var1.fileIndexHigh && var0.fileIndexLow == var1.fileIndexLow;
   }

   static boolean isReparsePoint(int var0) {
      return (var0 & 1024) != 0;
   }

   int attributes() {
      return this.fileAttrs;
   }

   int volSerialNumber() {
      return this.volSerialNumber;
   }

   int fileIndexHigh() {
      return this.fileIndexHigh;
   }

   int fileIndexLow() {
      return this.fileIndexLow;
   }

   public long size() {
      return this.size;
   }

   public FileTime lastModifiedTime() {
      return toFileTime(this.lastWriteTime);
   }

   public FileTime lastAccessTime() {
      return toFileTime(this.lastAccessTime);
   }

   public FileTime creationTime() {
      return toFileTime(this.creationTime);
   }

   public Object fileKey() {
      return null;
   }

   boolean isReparsePoint() {
      return isReparsePoint(this.fileAttrs);
   }

   boolean isDirectoryLink() {
      return this.isSymbolicLink() && (this.fileAttrs & 16) != 0;
   }

   public boolean isSymbolicLink() {
      return this.reparseTag == -1610612724;
   }

   public boolean isDirectory() {
      if (this.isSymbolicLink()) {
         return false;
      } else {
         return (this.fileAttrs & 16) != 0;
      }
   }

   public boolean isOther() {
      if (this.isSymbolicLink()) {
         return false;
      } else {
         return (this.fileAttrs & 1088) != 0;
      }
   }

   public boolean isRegularFile() {
      return !this.isSymbolicLink() && !this.isDirectory() && !this.isOther();
   }

   public boolean isReadOnly() {
      return (this.fileAttrs & 1) != 0;
   }

   public boolean isHidden() {
      return (this.fileAttrs & 2) != 0;
   }

   public boolean isArchive() {
      return (this.fileAttrs & 32) != 0;
   }

   public boolean isSystem() {
      return (this.fileAttrs & 4) != 0;
   }

   static {
      String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.nio.fs.ensureAccurateMetadata", "false"));
      ensureAccurateMetadata = var0.length() == 0 ? true : Boolean.valueOf(var0);
   }
}
