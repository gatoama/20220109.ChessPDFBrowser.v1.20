package com.frojasg1.sun.nio.fs;

import com.sun.nio.file.ExtendedOpenOption;
import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.Iterator;
import java.util.Set;
import com.frojasg1.sun.misc.JavaIOFileDescriptorAccess;
import com.frojasg1.sun.misc.SharedSecrets;
import com.frojasg1.sun.nio.ch.FileChannelImpl;
import com.frojasg1.sun.nio.ch.ThreadPool;
import com.frojasg1.sun.nio.ch.WindowsAsynchronousFileChannelImpl;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;

class WindowsChannelFactory {
   private static final JavaIOFileDescriptorAccess fdAccess = SharedSecrets.getJavaIOFileDescriptorAccess();
   static final OpenOption OPEN_REPARSE_POINT = new OpenOption() {
   };

   private WindowsChannelFactory() {
   }

   static FileChannel newFileChannel(String var0, String var1, Set<? extends OpenOption> var2, long var3) throws com.frojasg1.sun.nio.fs.WindowsException {
      WindowsChannelFactory.Flags var5 = WindowsChannelFactory.Flags.toFlags(var2);
      if (!var5.read && !var5.write) {
         if (var5.append) {
            var5.write = true;
         } else {
            var5.read = true;
         }
      }

      if (var5.read && var5.append) {
         throw new IllegalArgumentException("READ + APPEND not allowed");
      } else if (var5.append && var5.truncateExisting) {
         throw new IllegalArgumentException("APPEND + TRUNCATE_EXISTING not allowed");
      } else {
         FileDescriptor var6 = open(var0, var1, var5, var3);
         return FileChannelImpl.open(var6, var0, var5.read, var5.write, var5.append, (Object)null);
      }
   }

   static AsynchronousFileChannel newAsynchronousFileChannel(String var0, String var1, Set<? extends OpenOption> var2, long var3, ThreadPool var5) throws IOException {
      WindowsChannelFactory.Flags var6 = WindowsChannelFactory.Flags.toFlags(var2);
      var6.overlapped = true;
      if (!var6.read && !var6.write) {
         var6.read = true;
      }

      if (var6.append) {
         throw new UnsupportedOperationException("APPEND not allowed");
      } else {
         FileDescriptor var7;
         try {
            var7 = open(var0, var1, var6, var3);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var12) {
            var12.rethrowAsIOException(var0);
            return null;
         }

         try {
            return WindowsAsynchronousFileChannelImpl.open(var7, var6.read, var6.write, var5);
         } catch (IOException var11) {
            long var9 = fdAccess.getHandle(var7);
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var9);
            throw var11;
         }
      }
   }

   private static FileDescriptor open(String var0, String var1, WindowsChannelFactory.Flags var2, long var3) throws com.frojasg1.sun.nio.fs.WindowsException {
      boolean var5 = false;
      int var6 = 0;
      if (var2.read) {
         var6 |= -2147483648;
      }

      if (var2.write) {
         var6 |= 1073741824;
      }

      int var7 = 0;
      if (var2.shareRead) {
         var7 |= 1;
      }

      if (var2.shareWrite) {
         var7 |= 2;
      }

      if (var2.shareDelete) {
         var7 |= 4;
      }

      int var8 = 128;
      byte var9 = 3;
      if (var2.write) {
         if (var2.createNew) {
            var9 = 1;
            var8 |= 2097152;
         } else {
            if (var2.create) {
               var9 = 4;
            }

            if (var2.truncateExisting) {
               if (var9 == 4) {
                  var5 = true;
               } else {
                  var9 = 5;
               }
            }
         }
      }

      if (var2.dsync || var2.sync) {
         var8 |= -2147483648;
      }

      if (var2.overlapped) {
         var8 |= 1073741824;
      }

      if (var2.deleteOnClose) {
         var8 |= 67108864;
      }

      boolean var10 = true;
      if (var9 != 1 && (var2.noFollowLinks || var2.openReparsePoint || var2.deleteOnClose)) {
         if (var2.noFollowLinks || var2.deleteOnClose) {
            var10 = false;
         }

         var8 |= 2097152;
      }

      if (var1 != null) {
         SecurityManager var11 = System.getSecurityManager();
         if (var11 != null) {
            if (var2.read) {
               var11.checkRead(var1);
            }

            if (var2.write) {
               var11.checkWrite(var1);
            }

            if (var2.deleteOnClose) {
               var11.checkDelete(var1);
            }
         }
      }

      long var17 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateFile(var0, var6, var7, var3, var9, var8);
      if (!var10) {
         try {
            if (com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var17).isSymbolicLink()) {
               throw new com.frojasg1.sun.nio.fs.WindowsException("File is symbolic link");
            }
         } catch (com.frojasg1.sun.nio.fs.WindowsException var16) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var17);
            throw var16;
         }
      }

      if (var5) {
         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetEndOfFile(var17);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var15) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var17);
            throw var15;
         }
      }

      if (var9 == 1 && var2.sparse) {
         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeviceIoControlSetSparse(var17);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var14) {
         }
      }

      FileDescriptor var13 = new FileDescriptor();
      fdAccess.setHandle(var13, var17);
      return var13;
   }

   private static class Flags {
      boolean read;
      boolean write;
      boolean append;
      boolean truncateExisting;
      boolean create;
      boolean createNew;
      boolean deleteOnClose;
      boolean sparse;
      boolean overlapped;
      boolean sync;
      boolean dsync;
      boolean shareRead = true;
      boolean shareWrite = true;
      boolean shareDelete = true;
      boolean noFollowLinks;
      boolean openReparsePoint;

      private Flags() {
      }

      static WindowsChannelFactory.Flags toFlags(Set<? extends OpenOption> var0) {
         WindowsChannelFactory.Flags var1 = new WindowsChannelFactory.Flags();
         Iterator var2 = var0.iterator();

         while(var2.hasNext()) {
            OpenOption var3 = (OpenOption)var2.next();
            if (var3 instanceof StandardOpenOption) {
               switch((StandardOpenOption)var3) {
               case READ:
                  var1.read = true;
                  break;
               case WRITE:
                  var1.write = true;
                  break;
               case APPEND:
                  var1.append = true;
                  break;
               case TRUNCATE_EXISTING:
                  var1.truncateExisting = true;
                  break;
               case CREATE:
                  var1.create = true;
                  break;
               case CREATE_NEW:
                  var1.createNew = true;
                  break;
               case DELETE_ON_CLOSE:
                  var1.deleteOnClose = true;
                  break;
               case SPARSE:
                  var1.sparse = true;
                  break;
               case SYNC:
                  var1.sync = true;
                  break;
               case DSYNC:
                  var1.dsync = true;
                  break;
               default:
                  throw new UnsupportedOperationException();
               }
            } else if (var3 instanceof ExtendedOpenOption) {
               switch((ExtendedOpenOption)var3) {
               case NOSHARE_READ:
                  var1.shareRead = false;
                  break;
               case NOSHARE_WRITE:
                  var1.shareWrite = false;
                  break;
               case NOSHARE_DELETE:
                  var1.shareDelete = false;
                  break;
               default:
                  throw new UnsupportedOperationException();
               }
            } else if (var3 == LinkOption.NOFOLLOW_LINKS) {
               var1.noFollowLinks = true;
            } else {
               if (var3 != WindowsChannelFactory.OPEN_REPARSE_POINT) {
                  if (var3 == null) {
                     throw new NullPointerException();
                  }

                  throw new UnsupportedOperationException();
               }

               var1.openReparsePoint = true;
            }
         }

         return var1;
      }
   }
}
