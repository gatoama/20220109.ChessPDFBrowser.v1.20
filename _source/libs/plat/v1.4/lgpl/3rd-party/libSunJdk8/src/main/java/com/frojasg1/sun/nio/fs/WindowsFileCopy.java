package com.frojasg1.sun.nio.fs;

import com.sun.nio.file.ExtendedCopyOption;
import com.frojasg1.sun.nio.fs.Cancellable;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributeViews;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;
import com.frojasg1.sun.nio.fs.WindowsSecurity;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.ExecutionException;

class WindowsFileCopy {
   private WindowsFileCopy() {
   }

   static void copy(final com.frojasg1.sun.nio.fs.WindowsPath var0, final com.frojasg1.sun.nio.fs.WindowsPath var1, CopyOption... var2) throws IOException {
      boolean var3 = false;
      boolean var4 = false;
      boolean var5 = true;
      boolean var6 = false;
      CopyOption[] var7 = var2;
      int var8 = var2.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         CopyOption var10 = var7[var9];
         if (var10 == StandardCopyOption.REPLACE_EXISTING) {
            var3 = true;
         } else if (var10 == LinkOption.NOFOLLOW_LINKS) {
            var5 = false;
         } else if (var10 == StandardCopyOption.COPY_ATTRIBUTES) {
            var4 = true;
         } else {
            if (var10 != ExtendedCopyOption.INTERRUPTIBLE) {
               if (var10 == null) {
                  throw new NullPointerException();
               }

               throw new UnsupportedOperationException("Unsupported copy option");
            }

            var6 = true;
         }
      }

      SecurityManager var57 = System.getSecurityManager();
      if (var57 != null) {
         var0.checkRead();
         var1.checkWrite();
      }

      com.frojasg1.sun.nio.fs.WindowsFileAttributes var58 = null;
      com.frojasg1.sun.nio.fs.WindowsFileAttributes var59 = null;
      long var60 = 0L;

      try {
         var60 = var0.openForReadAttributeAccess(var5);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var50) {
         var50.rethrowAsIOException(var0);
      }

      try {
         try {
            var58 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var60);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var49) {
            var49.rethrowAsIOException(var0);
         }

         long var12 = 0L;

         try {
            var12 = var1.openForReadAttributeAccess(false);

            try {
               var59 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var12);
               if (com.frojasg1.sun.nio.fs.WindowsFileAttributes.isSameFile(var58, var59)) {
                  return;
               }

               if (!var3) {
                  throw new FileAlreadyExistsException(var1.getPathForExceptionMessage());
               }
            } finally {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var12);
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var55) {
         }
      } finally {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var60);
      }

      if (var57 != null && var58.isSymbolicLink()) {
         var57.checkPermission(new LinkPermission("symbolic"));
      }

      final String var61 = asWin32Path(var0);
      final String var13 = asWin32Path(var1);
      if (var59 != null) {
         try {
            if (!var59.isDirectory() && !var59.isDirectoryLink()) {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeleteFile(var13);
            } else {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var13);
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var53) {
            if (var59.isDirectory() && (var53.lastError() == 145 || var53.lastError() == 183)) {
               throw new DirectoryNotEmptyException(var1.getPathForExceptionMessage());
            }

            var53.rethrowAsIOException(var1);
         }
      }

      if (!var58.isDirectory() && !var58.isDirectoryLink()) {
         final int var63 = var0.getFileSystem().supportsLinks() && !var5 ? 2048 : 0;
         if (var6) {
            com.frojasg1.sun.nio.fs.Cancellable var64 = new com.frojasg1.sun.nio.fs.Cancellable() {
               public int cancelValue() {
                  return 1;
               }

               public void implRun() throws IOException {
                  try {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CopyFileEx(var61, var13, var63, this.addressToPollForCancel());
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var2) {
                     var2.rethrowAsIOException(var0, var1);
                  }

               }
            };

            try {
               com.frojasg1.sun.nio.fs.Cancellable.runInterruptibly(var64);
            } catch (ExecutionException var51) {
               Throwable var17 = var51.getCause();
               if (var17 instanceof IOException) {
                  throw (IOException)var17;
               }

               throw new IOException(var17);
            }
         } else {
            try {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CopyFileEx(var61, var13, var63, 0L);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var45) {
               var45.rethrowAsIOException(var0, var1);
            }
         }

         if (var4) {
            try {
               copySecurityAttributes(var0, var1, var5);
            } catch (IOException var44) {
            }
         }

      } else {
         try {
            if (var58.isDirectory()) {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateDirectory(var13, 0L);
            } else {
               String var14 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.readLink(var0);
               byte var15 = 1;
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateSymbolicLink(var13, com.frojasg1.sun.nio.fs.WindowsPath.addPrefixIfNeeded(var14), var15);
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var48) {
            var48.rethrowAsIOException(var1);
         }

         if (var4) {
            com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.Dos var62 = com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.createDosView(var1, false);

            try {
               var62.setAttributes(var58);
            } catch (IOException var52) {
               if (var58.isDirectory()) {
                  try {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var13);
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var47) {
                  }
               }
            }

            try {
               copySecurityAttributes(var0, var1, var5);
            } catch (IOException var46) {
            }
         }

      }
   }

   static void move(com.frojasg1.sun.nio.fs.WindowsPath var0, com.frojasg1.sun.nio.fs.WindowsPath var1, CopyOption... var2) throws IOException {
      boolean var3 = false;
      boolean var4 = false;
      CopyOption[] var5 = var2;
      int var6 = var2.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         CopyOption var8 = var5[var7];
         if (var8 == StandardCopyOption.ATOMIC_MOVE) {
            var3 = true;
         } else if (var8 == StandardCopyOption.REPLACE_EXISTING) {
            var4 = true;
         } else if (var8 != LinkOption.NOFOLLOW_LINKS) {
            if (var8 == null) {
               throw new NullPointerException();
            }

            throw new UnsupportedOperationException("Unsupported copy option");
         }
      }

      SecurityManager var64 = System.getSecurityManager();
      if (var64 != null) {
         var0.checkWrite();
         var1.checkWrite();
      }

      String var65 = asWin32Path(var0);
      String var66 = asWin32Path(var1);
      if (var3) {
         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.MoveFileEx(var65, var66, 1);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var59) {
            if (var59.lastError() == 17) {
               throw new AtomicMoveNotSupportedException(var0.getPathForExceptionMessage(), var1.getPathForExceptionMessage(), var59.errorString());
            }

            var59.rethrowAsIOException(var0, var1);
         }

      } else {
         com.frojasg1.sun.nio.fs.WindowsFileAttributes var67 = null;
         com.frojasg1.sun.nio.fs.WindowsFileAttributes var9 = null;
         long var10 = 0L;

         try {
            var10 = var0.openForReadAttributeAccess(false);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var56) {
            var56.rethrowAsIOException(var0);
         }

         try {
            try {
               var67 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var10);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var55) {
               var55.rethrowAsIOException(var0);
            }

            long var12 = 0L;

            try {
               var12 = var1.openForReadAttributeAccess(false);

               try {
                  var9 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var12);
                  if (com.frojasg1.sun.nio.fs.WindowsFileAttributes.isSameFile(var67, var9)) {
                     return;
                  }

                  if (!var4) {
                     throw new FileAlreadyExistsException(var1.getPathForExceptionMessage());
                  }
               } finally {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var12);
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var62) {
            }
         } finally {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var10);
         }

         if (var9 != null) {
            try {
               if (!var9.isDirectory() && !var9.isDirectoryLink()) {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeleteFile(var66);
               } else {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var66);
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var60) {
               if (var9.isDirectory() && (var60.lastError() == 145 || var60.lastError() == 183)) {
                  throw new DirectoryNotEmptyException(var1.getPathForExceptionMessage());
               }

               var60.rethrowAsIOException(var1);
            }
         }

         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.MoveFileEx(var65, var66, 0);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var58) {
            if (var58.lastError() != 17) {
               var58.rethrowAsIOException(var0, var1);
            }

            if (!var67.isDirectory() && !var67.isDirectoryLink()) {
               try {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.MoveFileEx(var65, var66, 2);
               } catch (com.frojasg1.sun.nio.fs.WindowsException var54) {
                  var54.rethrowAsIOException(var0, var1);
               }

               try {
                  copySecurityAttributes(var0, var1, false);
               } catch (IOException var53) {
               }

            } else {
               assert var67.isDirectory() || var67.isDirectoryLink();

               try {
                  if (var67.isDirectory()) {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateDirectory(var66, 0L);
                  } else {
                     String var68 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.readLink(var0);
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateSymbolicLink(var66, com.frojasg1.sun.nio.fs.WindowsPath.addPrefixIfNeeded(var68), 1);
                  }
               } catch (com.frojasg1.sun.nio.fs.WindowsException var52) {
                  var52.rethrowAsIOException(var1);
               }

               com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.Dos var69 = com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.createDosView(var1, false);

               try {
                  var69.setAttributes(var67);
               } catch (IOException var51) {
                  try {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var66);
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var48) {
                  }

                  throw var51;
               }

               try {
                  copySecurityAttributes(var0, var1, false);
               } catch (IOException var50) {
               }

               try {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var65);
               } catch (com.frojasg1.sun.nio.fs.WindowsException var57) {
                  try {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var66);
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var49) {
                  }

                  if (var57.lastError() == 145 || var57.lastError() == 183) {
                     throw new DirectoryNotEmptyException(var1.getPathForExceptionMessage());
                  }

                  var57.rethrowAsIOException(var0);
               }

            }
         }
      }
   }

   private static String asWin32Path(com.frojasg1.sun.nio.fs.WindowsPath var0) throws IOException {
      try {
         return var0.getPathForWin32Calls();
      } catch (com.frojasg1.sun.nio.fs.WindowsException var2) {
         var2.rethrowAsIOException(var0);
         return null;
      }
   }

   private static void copySecurityAttributes(com.frojasg1.sun.nio.fs.WindowsPath var0, com.frojasg1.sun.nio.fs.WindowsPath var1, boolean var2) throws IOException {
      String var3 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(var0, var2);
      com.frojasg1.sun.nio.fs.WindowsSecurity.Privilege var4 = com.frojasg1.sun.nio.fs.WindowsSecurity.enablePrivilege("SeRestorePrivilege");

      try {
         byte var5 = 7;
         com.frojasg1.sun.nio.fs.NativeBuffer var6 = com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView.getFileSecurity(var3, var5);

         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetFileSecurity(var1.getPathForWin32Calls(), var5, var6.address());
         } catch (com.frojasg1.sun.nio.fs.WindowsException var16) {
            var16.rethrowAsIOException(var1);
         } finally {
            var6.release();
         }
      } finally {
         var4.drop();
      }

   }
}
