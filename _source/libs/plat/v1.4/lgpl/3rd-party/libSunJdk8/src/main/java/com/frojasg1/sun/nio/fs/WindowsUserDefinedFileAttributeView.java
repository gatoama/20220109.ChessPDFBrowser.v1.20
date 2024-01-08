package com.frojasg1.sun.nio.fs;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.AbstractUserDefinedFileAttributeView;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsChannelFactory;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

class WindowsUserDefinedFileAttributeView extends com.frojasg1.sun.nio.fs.AbstractUserDefinedFileAttributeView {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private final com.frojasg1.sun.nio.fs.WindowsPath file;
   private final boolean followLinks;

   private String join(String var1, String var2) {
      if (var2 == null) {
         throw new NullPointerException("'name' is null");
      } else {
         return var1 + ":" + var2;
      }
   }

   private String join(com.frojasg1.sun.nio.fs.WindowsPath var1, String var2) throws com.frojasg1.sun.nio.fs.WindowsException {
      return this.join(var1.getPathForWin32Calls(), var2);
   }

   WindowsUserDefinedFileAttributeView(com.frojasg1.sun.nio.fs.WindowsPath var1, boolean var2) {
      this.file = var1;
      this.followLinks = var2;
   }

   private List<String> listUsingStreamEnumeration() throws IOException {
      ArrayList var1 = new ArrayList();

      try {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FirstStream var2 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindFirstStream(this.file.getPathForWin32Calls());
         if (var2 != null) {
            long var3 = var2.handle();

            try {
               String var5 = var2.name();
               String[] var6;
               if (!var5.equals("::$DATA")) {
                  var6 = var5.split(":");
                  var1.add(var6[1]);
               }

               while((var5 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindNextStream(var3)) != null) {
                  var6 = var5.split(":");
                  var1.add(var6[1]);
               }
            } finally {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.FindClose(var3);
            }
         }
      } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
         var11.rethrowAsIOException(this.file);
      }

      return Collections.unmodifiableList(var1);
   }

   private List<String> listUsingBackupRead() throws IOException {
      long var1 = -1L;

      try {
         int var3 = 33554432;
         if (!this.followLinks && this.file.getFileSystem().supportsLinks()) {
            var3 |= 2097152;
         }

         var1 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), -2147483648, 1, 3, var3);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var37) {
         var37.rethrowAsIOException(this.file);
      }

      com.frojasg1.sun.nio.fs.NativeBuffer var4 = null;
      ArrayList var5 = new ArrayList();

      try {
         var4 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(4096);
         long var6 = var4.address();
         long var12 = 0L;

         try {
            while(true) {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.BackupResult var14 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.BackupRead(var1, var6, 20, false, var12);
               var12 = var14.context();
               if (var14.bytesTransferred() == 0) {
                  break;
               }

               int var15 = unsafe.getInt(var6 + 0L);
               long var16 = unsafe.getLong(var6 + 8L);
               int var18 = unsafe.getInt(var6 + 16L);
               if (var18 > 0) {
                  var14 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.BackupRead(var1, var6, var18, false, var12);
                  if (var14.bytesTransferred() != var18) {
                     break;
                  }
               }

               if (var15 == 4) {
                  char[] var19 = new char[var18 / 2];
                  unsafe.copyMemory((Object)null, var6, var19, (long)Unsafe.ARRAY_CHAR_BASE_OFFSET, (long)var18);
                  String[] var20 = (new String(var19)).split(":");
                  if (var20.length == 3) {
                     var5.add(var20[1]);
                  }
               }

               if (var15 == 9) {
                  throw new IOException("Spare blocks not handled");
               }

               if (var16 > 0L) {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.BackupSeek(var1, var16, var12);
               }
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var38) {
            throw new IOException(var38.errorString());
         } finally {
            if (var12 != 0L) {
               try {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.BackupRead(var1, 0L, 0, true, var12);
               } catch (com.frojasg1.sun.nio.fs.WindowsException var36) {
               }
            }

         }
      } finally {
         if (var4 != null) {
            var4.release();
         }

         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var1);
      }

      return Collections.unmodifiableList(var5);
   }

   public List<String> list() throws IOException {
      if (System.getSecurityManager() != null) {
         this.checkAccess(this.file.getPathForPermissionCheck(), true, false);
      }

      return this.file.getFileSystem().supportsStreamEnumeration() ? this.listUsingStreamEnumeration() : this.listUsingBackupRead();
   }

   public int size(String var1) throws IOException {
      if (System.getSecurityManager() != null) {
         this.checkAccess(this.file.getPathForPermissionCheck(), true, false);
      }

      FileChannel var2 = null;

      try {
         HashSet var3 = new HashSet();
         var3.add(StandardOpenOption.READ);
         if (!this.followLinks) {
            var3.add(com.frojasg1.sun.nio.fs.WindowsChannelFactory.OPEN_REPARSE_POINT);
         }

         var2 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newFileChannel(this.join(this.file, var1), (String)null, var3, 0L);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var9) {
         var9.rethrowAsIOException(this.join(this.file.getPathForPermissionCheck(), var1));
      }

      int var5;
      try {
         long var11 = var2.size();
         if (var11 > 2147483647L) {
            throw new ArithmeticException("Stream too large");
         }

         var5 = (int)var11;
      } finally {
         var2.close();
      }

      return var5;
   }

   public int read(String var1, ByteBuffer var2) throws IOException {
      if (System.getSecurityManager() != null) {
         this.checkAccess(this.file.getPathForPermissionCheck(), true, false);
      }

      FileChannel var3 = null;

      try {
         HashSet var4 = new HashSet();
         var4.add(StandardOpenOption.READ);
         if (!this.followLinks) {
            var4.add(com.frojasg1.sun.nio.fs.WindowsChannelFactory.OPEN_REPARSE_POINT);
         }

         var3 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newFileChannel(this.join(this.file, var1), (String)null, var4, 0L);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var9) {
         var9.rethrowAsIOException(this.join(this.file.getPathForPermissionCheck(), var1));
      }

      int var5;
      try {
         if (var3.size() > (long)var2.remaining()) {
            throw new IOException("Stream too large");
         }

         int var11;
         for(var11 = 0; var2.hasRemaining(); var11 += var5) {
            var5 = var3.read(var2);
            if (var5 < 0) {
               break;
            }
         }

         var5 = var11;
      } finally {
         var3.close();
      }

      return var5;
   }

   public int write(String var1, ByteBuffer var2) throws IOException {
      if (System.getSecurityManager() != null) {
         this.checkAccess(this.file.getPathForPermissionCheck(), false, true);
      }

      long var3 = -1L;

      try {
         int var5 = 33554432;
         if (!this.followLinks) {
            var5 |= 2097152;
         }

         var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateFile(this.file.getPathForWin32Calls(), -2147483648, 7, 3, var5);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var20) {
         var20.rethrowAsIOException(this.file);
      }

      try {
         HashSet var23 = new HashSet();
         if (!this.followLinks) {
            var23.add(com.frojasg1.sun.nio.fs.WindowsChannelFactory.OPEN_REPARSE_POINT);
         }

         var23.add(StandardOpenOption.CREATE);
         var23.add(StandardOpenOption.WRITE);
         var23.add(StandardOpenOption.TRUNCATE_EXISTING);
         FileChannel var6 = null;

         try {
            var6 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newFileChannel(this.join(this.file, var1), (String)null, var23, 0L);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var19) {
            var19.rethrowAsIOException(this.join(this.file.getPathForPermissionCheck(), var1));
         }

         try {
            int var7 = var2.remaining();

            while(var2.hasRemaining()) {
               var6.write(var2);
            }

            int var8 = var7;
            return var8;
         } finally {
            var6.close();
         }
      } finally {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var3);
      }
   }

   public void delete(String var1) throws IOException {
      if (System.getSecurityManager() != null) {
         this.checkAccess(this.file.getPathForPermissionCheck(), false, true);
      }

      String var2 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
      String var3 = this.join(var2, var1);

      try {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeleteFile(var3);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var5) {
         var5.rethrowAsIOException(var3);
      }

   }
}
