package com.frojasg1.sun.nio.fs;

import java.io.IOError;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.NotLinkException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsFileSystem;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

class WindowsLinkSupport {
   private static final Unsafe unsafe = Unsafe.getUnsafe();

   private WindowsLinkSupport() {
   }

   static String readLink(com.frojasg1.sun.nio.fs.WindowsPath var0) throws IOException {
      long var1 = 0L;

      try {
         var1 = var0.openForReadAttributeAccess(false);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var8) {
         var8.rethrowAsIOException(var0);
      }

      String var3;
      try {
         var3 = readLinkImpl(var1);
      } finally {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var1);
      }

      return var3;
   }

   static String getFinalPath(com.frojasg1.sun.nio.fs.WindowsPath var0) throws IOException {
      long var1 = 0L;

      try {
         var1 = var0.openForReadAttributeAccess(true);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var8) {
         var8.rethrowAsIOException(var0);
      }

      try {
         String var3 = stripPrefix(com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFinalPathNameByHandle(var1));
         return var3;
      } catch (com.frojasg1.sun.nio.fs.WindowsException var9) {
         if (var9.lastError() != 124) {
            var9.rethrowAsIOException(var0);
         }
      } finally {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var1);
      }

      return null;
   }

   static String getFinalPath(com.frojasg1.sun.nio.fs.WindowsPath var0, boolean var1) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsFileSystem var2 = var0.getFileSystem();

      try {
         if (!var1 || !var2.supportsLinks()) {
            return var0.getPathForWin32Calls();
         }

         if (!com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var0, false).isSymbolicLink()) {
            return var0.getPathForWin32Calls();
         }
      } catch (com.frojasg1.sun.nio.fs.WindowsException var10) {
         var10.rethrowAsIOException(var0);
      }

      String var3 = getFinalPath(var0);
      if (var3 != null) {
         return var3;
      } else {
         com.frojasg1.sun.nio.fs.WindowsPath var4 = var0;
         int var5 = 0;

         do {
            try {
               com.frojasg1.sun.nio.fs.WindowsFileAttributes var6 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var4, false);
               if (!var6.isSymbolicLink()) {
                  return var4.getPathForWin32Calls();
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var9) {
               var9.rethrowAsIOException(var4);
            }

            com.frojasg1.sun.nio.fs.WindowsPath var11 = com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(var2, readLink(var4));
            com.frojasg1.sun.nio.fs.WindowsPath var7 = var4.getParent();
            if (var7 == null) {
               com.frojasg1.sun.nio.fs.WindowsPath var4Final = var4;
               var4 = (com.frojasg1.sun.nio.fs.WindowsPath)AccessController.doPrivileged(new PrivilegedAction<com.frojasg1.sun.nio.fs.WindowsPath>() {
                  public com.frojasg1.sun.nio.fs.WindowsPath run() {
                     return var4Final.toAbsolutePath();
                  }
               });
               var7 = var4.getParent();
            }

            var4 = var7.resolve(var11);
            ++var5;
         } while(var5 < 32);

         throw new FileSystemException(var0.getPathForExceptionMessage(), (String)null, "Too many links");
      }
   }

   static String getRealPath(com.frojasg1.sun.nio.fs.WindowsPath var0, boolean var1) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsFileSystem var2 = var0.getFileSystem();
      if (var1 && !var2.supportsLinks()) {
         var1 = false;
      }

      String var3 = null;

      try {
         var3 = var0.toAbsolutePath().toString();
      } catch (IOError var18) {
         throw (IOException)((IOException)var18.getCause());
      }

      if (var3.indexOf(46) >= 0) {
         try {
            var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFullPathName(var3);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var17) {
            var17.rethrowAsIOException(var0);
         }
      }

      StringBuilder var4 = new StringBuilder(var3.length());
      char var6 = var3.charAt(0);
      char var7 = var3.charAt(1);
      int var5;
      int var8;
      int var9;
      if ((var6 <= 'z' && var6 >= 'a' || var6 <= 'Z' && var6 >= 'A') && var7 == ':' && var3.charAt(2) == '\\') {
         var4.append(Character.toUpperCase(var6));
         var4.append(":\\");
         var5 = 3;
      } else {
         label125: {
            if (var6 == '\\' && var7 == '\\') {
               var8 = var3.length() - 1;
               var9 = var3.indexOf(92, 2);
               if (var9 != -1 && var9 != var8) {
                  var9 = var3.indexOf(92, var9 + 1);
                  if (var9 < 0) {
                     var9 = var8;
                     var4.append(var3).append("\\");
                  } else {
                     var4.append(var3, 0, var9 + 1);
                  }

                  var5 = var9 + 1;
                  break label125;
               }

               throw new FileSystemException(var0.getPathForExceptionMessage(), (String)null, "UNC has invalid share");
            }

            throw new AssertionError("path type not recognized");
         }
      }

      if (var5 >= var3.length()) {
         String var19 = var4.toString();

         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFileAttributes(var19);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var15) {
            var15.rethrowAsIOException(var3);
         }

         return var19;
      } else {
         int var10;
         for(var8 = var5; var8 < var3.length(); var8 = var10 + 1) {
            var9 = var3.indexOf(92, var8);
            var10 = var9 == -1 ? var3.length() : var9;
            String var11 = var4.toString() + var3.substring(var8, var10);

            try {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FirstFile var12 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindFirstFile(com.frojasg1.sun.nio.fs.WindowsPath.addPrefixIfNeeded(var11));
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindClose(var12.handle());
               if (var1 && com.frojasg1.sun.nio.fs.WindowsFileAttributes.isReparsePoint(var12.attributes())) {
                  String var13 = getFinalPath(var0);
                  if (var13 == null) {
                     com.frojasg1.sun.nio.fs.WindowsPath var14 = resolveAllLinks(com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(var2, var3));
                     var13 = getRealPath(var14, false);
                  }

                  return var13;
               }

               var4.append(var12.name());
               if (var9 != -1) {
                  var4.append('\\');
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var16) {
               var16.rethrowAsIOException(var3);
            }
         }

         return var4.toString();
      }
   }

   private static String readLinkImpl(long var0) throws IOException {
      short var2 = 16384;
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var2);

      String var13;
      try {
         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeviceIoControlGetReparsePoint(var0, var3.address(), var2);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var17) {
            if (var17.lastError() == 4390) {
               throw new NotLinkException((String)null, (String)null, var17.errorString());
            }

            var17.rethrowAsIOException((String)null);
         }

         int var8 = (int)unsafe.getLong(var3.address() + 0L);
         if (var8 != -1610612724) {
            throw new NotLinkException((String)null, (String)null, "Reparse point is not a symbolic link");
         }

         short var9 = unsafe.getShort(var3.address() + 8L);
         short var10 = unsafe.getShort(var3.address() + 10L);
         if (var10 % 2 != 0) {
            throw new FileSystemException((String)null, (String)null, "Symbolic link corrupted");
         }

         char[] var11 = new char[var10 / 2];
         unsafe.copyMemory((Object)null, var3.address() + 20L + (long)var9, var11, (long)Unsafe.ARRAY_CHAR_BASE_OFFSET, (long)var10);
         String var12 = stripPrefix(new String(var11));
         if (var12.length() == 0) {
            throw new IOException("Symbolic link target is invalid");
         }

         var13 = var12;
      } finally {
         var3.release();
      }

      return var13;
   }

   private static com.frojasg1.sun.nio.fs.WindowsPath resolveAllLinks(com.frojasg1.sun.nio.fs.WindowsPath var0) throws IOException {
      assert var0.isAbsolute();

      com.frojasg1.sun.nio.fs.WindowsFileSystem var1 = var0.getFileSystem();
      int var2 = 0;
      int var3 = 0;

      while(var3 < var0.getNameCount()) {
         com.frojasg1.sun.nio.fs.WindowsPath var4 = var0.getRoot().resolve(var0.subpath(0, var3 + 1));
         com.frojasg1.sun.nio.fs.WindowsFileAttributes var5 = null;

         try {
            var5 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var4, false);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
            var11.rethrowAsIOException(var4);
         }

         if (var5.isSymbolicLink()) {
            ++var2;
            if (var2 > 32) {
               throw new IOException("Too many links");
            }

            com.frojasg1.sun.nio.fs.WindowsPath var6 = com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(var1, readLink(var4));
            com.frojasg1.sun.nio.fs.WindowsPath var7 = null;
            int var8 = var0.getNameCount();
            if (var3 + 1 < var8) {
               var7 = var0.subpath(var3 + 1, var8);
            }

            var0 = var4.getParent().resolve(var6);

            try {
               String var9 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFullPathName(var0.toString());
               if (!var9.equals(var0.toString())) {
                  var0 = com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(var1, var9);
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var10) {
               var10.rethrowAsIOException(var0);
            }

            if (var7 != null) {
               var0 = var0.resolve(var7);
            }

            var3 = 0;
         } else {
            ++var3;
         }
      }

      return var0;
   }

   private static String stripPrefix(String var0) {
      if (var0.startsWith("\\\\?\\")) {
         if (var0.startsWith("\\\\?\\UNC\\")) {
            var0 = "\\" + var0.substring(7);
         } else {
            var0 = var0.substring(4);
         }

         return var0;
      } else if (var0.startsWith("\\??\\")) {
         if (var0.startsWith("\\??\\UNC\\")) {
            var0 = "\\" + var0.substring(7);
         } else {
            var0 = var0.substring(4);
         }

         return var0;
      } else {
         return var0;
      }
   }
}
