package com.frojasg1.sun.nio.fs;

import com.sun.nio.file.ExtendedWatchEventModifier;
import com.sun.nio.file.SensitivityWatchEventModifier;
import java.io.IOException;
import java.nio.file.NotDirectoryException;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.AbstractPoller;
import com.frojasg1.sun.nio.fs.AbstractWatchKey;
import com.frojasg1.sun.nio.fs.AbstractWatchService;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsFileSystem;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;

class WindowsWatchService extends com.frojasg1.sun.nio.fs.AbstractWatchService {
   private static final int WAKEUP_COMPLETION_KEY = 0;
   private final WindowsWatchService.Poller poller;
   private static final int ALL_FILE_NOTIFY_EVENTS = 351;

   WindowsWatchService(com.frojasg1.sun.nio.fs.WindowsFileSystem var1) throws IOException {
      long var2 = 0L;

      try {
         var2 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateIoCompletionPort(-1L, 0L, 0L);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var5) {
         throw new IOException(var5.getMessage());
      }

      this.poller = new WindowsWatchService.Poller(var1, this, var2);
      this.poller.start();
   }

   WatchKey register(Path var1, Kind<?>[] var2, Modifier... var3) throws IOException {
      return this.poller.register(var1, var2, var3);
   }

   void implClose() throws IOException {
      this.poller.close();
   }

   private static class FileKey {
      private final int volSerialNumber;
      private final int fileIndexHigh;
      private final int fileIndexLow;

      FileKey(int var1, int var2, int var3) {
         this.volSerialNumber = var1;
         this.fileIndexHigh = var2;
         this.fileIndexLow = var3;
      }

      public int hashCode() {
         return this.volSerialNumber ^ this.fileIndexHigh ^ this.fileIndexLow;
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof WindowsWatchService.FileKey)) {
            return false;
         } else {
            WindowsWatchService.FileKey var2 = (WindowsWatchService.FileKey)var1;
            if (this.volSerialNumber != var2.volSerialNumber) {
               return false;
            } else if (this.fileIndexHigh != var2.fileIndexHigh) {
               return false;
            } else {
               return this.fileIndexLow == var2.fileIndexLow;
            }
         }
      }
   }

   private static class Poller extends com.frojasg1.sun.nio.fs.AbstractPoller {
      private static final Unsafe UNSAFE = Unsafe.getUnsafe();
      private static final short SIZEOF_DWORD = 4;
      private static final short SIZEOF_OVERLAPPED = 32;
      private static final short OFFSETOF_HEVENT;
      private static final short OFFSETOF_NEXTENTRYOFFSET = 0;
      private static final short OFFSETOF_ACTION = 4;
      private static final short OFFSETOF_FILENAMELENGTH = 8;
      private static final short OFFSETOF_FILENAME = 12;
      private static final int CHANGES_BUFFER_SIZE = 16384;
      private final com.frojasg1.sun.nio.fs.WindowsFileSystem fs;
      private final WindowsWatchService watcher;
      private final long port;
      private final Map<Integer, WindowsWatchService.WindowsWatchKey> ck2key;
      private final Map<WindowsWatchService.FileKey, WindowsWatchService.WindowsWatchKey> fk2key;
      private int lastCompletionKey;

      Poller(com.frojasg1.sun.nio.fs.WindowsFileSystem var1, WindowsWatchService var2, long var3) {
         this.fs = var1;
         this.watcher = var2;
         this.port = var3;
         this.ck2key = new HashMap();
         this.fk2key = new HashMap();
         this.lastCompletionKey = 0;
      }

      void wakeup() throws IOException {
         try {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.PostQueuedCompletionStatus(this.port, 0L);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var2) {
            throw new IOException(var2.getMessage());
         }
      }

      Object implRegister(Path var1, Set<? extends Kind<?>> var2, Modifier... var3) {
         com.frojasg1.sun.nio.fs.WindowsPath var4 = (com.frojasg1.sun.nio.fs.WindowsPath)var1;
         boolean var5 = false;
         Modifier[] var6 = var3;
         int var7 = var3.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Modifier var9 = var6[var8];
            if (var9 == ExtendedWatchEventModifier.FILE_TREE) {
               var5 = true;
            } else {
               if (var9 == null) {
                  return new NullPointerException();
               }

               if (!(var9 instanceof SensitivityWatchEventModifier)) {
                  return new UnsupportedOperationException("Modifier not supported");
               }
            }
         }

         long var34;
         try {
            var34 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateFile(var4.getPathForWin32Calls(), 1, 7, 3, 1107296256);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var29) {
            return var29.asIOException(var4);
         }

         boolean var35 = false;

         try {
            com.frojasg1.sun.nio.fs.WindowsFileAttributes var36;
            try {
               var36 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var34);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var30) {
               IOException var11 = var30.asIOException(var4);
               return var11;
            }

            if (!var36.isDirectory()) {
               NotDirectoryException var37 = new NotDirectoryException(var4.getPathForExceptionMessage());
               return var37;
            } else {
               WindowsWatchService.FileKey var10 = new WindowsWatchService.FileKey(var36.volSerialNumber(), var36.fileIndexHigh(), var36.fileIndexLow());
               WindowsWatchService.WindowsWatchKey var38 = (WindowsWatchService.WindowsWatchKey)this.fk2key.get(var10);
               if (var38 != null && var5 == var38.watchSubtree()) {
                  var38.setEvents(var2);
                  WindowsWatchService.WindowsWatchKey var39 = var38;
                  return var39;
               } else {
                  int var12 = ++this.lastCompletionKey;
                  if (var12 == 0) {
                     var12 = ++this.lastCompletionKey;
                  }

                  try {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateIoCompletionPort(var34, this.port, (long)var12);
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var32) {
                     IOException var14 = new IOException(var32.getMessage());
                     return var14;
                  }

                  short var13 = 16420;
                  com.frojasg1.sun.nio.fs.NativeBuffer var40 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var13);
                  long var15 = var40.address();
                  long var17 = var15 + (long)var13 - 32L;
                  long var19 = var17 - 4L;
                  UNSAFE.setMemory(var17, 32L, (byte)0);

                  try {
                     this.createAndAttachEvent(var17);
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.ReadDirectoryChangesW(var34, var15, 16384, var5, 351, var19, var17);
                  } catch (com.frojasg1.sun.nio.fs.WindowsException var31) {
                     this.closeAttachedEvent(var17);
                     var40.release();
                     IOException var22 = new IOException(var31.getMessage());
                     return var22;
                  }

                  WindowsWatchService.WindowsWatchKey var21;
                  if (var38 == null) {
                     var21 = (new WindowsWatchService.WindowsWatchKey(var4, this.watcher, var10)).init(var34, var2, var5, var40, var19, var17, var12);
                     this.fk2key.put(var10, var21);
                  } else {
                     this.ck2key.remove(var38.completionKey());
                     this.releaseResources(var38);
                     var21 = var38.init(var34, var2, var5, var40, var19, var17, var12);
                  }

                  this.ck2key.put(var12, var21);
                  var35 = true;
                  WindowsWatchService.WindowsWatchKey var41 = var21;
                  return var41;
               }
            }
         } finally {
            if (!var35) {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var34);
            }

         }
      }

      private void releaseResources(WindowsWatchService.WindowsWatchKey var1) {
         if (!var1.isErrorStartingOverlapped()) {
            try {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CancelIo(var1.handle());
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetOverlappedResult(var1.handle(), var1.overlappedAddress());
            } catch (com.frojasg1.sun.nio.fs.WindowsException var3) {
            }
         }

         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var1.handle());
         this.closeAttachedEvent(var1.overlappedAddress());
         var1.buffer().cleaner().clean();
      }

      private void createAndAttachEvent(long var1) throws com.frojasg1.sun.nio.fs.WindowsException {
         long var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateEvent(false, false);
         UNSAFE.putAddress(var1 + (long)OFFSETOF_HEVENT, var3);
      }

      private void closeAttachedEvent(long var1) {
         long var3 = UNSAFE.getAddress(var1 + (long)OFFSETOF_HEVENT);
         if (var3 != 0L && var3 != -1L) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var3);
         }

      }

      void implCancelKey(WatchKey var1) {
         WindowsWatchService.WindowsWatchKey var2 = (WindowsWatchService.WindowsWatchKey)var1;
         if (var2.isValid()) {
            this.fk2key.remove(var2.fileKey());
            this.ck2key.remove(var2.completionKey());
            var2.invalidate();
         }

      }

      void implCloseAll() {
         this.ck2key.values().forEach(WindowsWatchService.WindowsWatchKey::invalidate);
         this.fk2key.clear();
         this.ck2key.clear();
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(this.port);
      }

      private Kind<?> translateActionToEvent(int var1) {
         switch(var1) {
         case 1:
         case 5:
            return StandardWatchEventKinds.ENTRY_CREATE;
         case 2:
         case 4:
            return StandardWatchEventKinds.ENTRY_DELETE;
         case 3:
            return StandardWatchEventKinds.ENTRY_MODIFY;
         default:
            return null;
         }
      }

      private void processEvents(WindowsWatchService.WindowsWatchKey var1, int var2) {
         long var3 = var1.buffer().address();

         int var5;
         do {
            int var6 = UNSAFE.getInt(var3 + 4L);
            Kind var7 = this.translateActionToEvent(var6);
            if (var1.events().contains(var7)) {
               int var8 = UNSAFE.getInt(var3 + 8L);
               if (var8 % 2 != 0) {
                  throw new AssertionError("FileNameLength is not a multiple of 2");
               }

               char[] var9 = new char[var8 / 2];
               UNSAFE.copyMemory((Object)null, var3 + 12L, var9, (long)Unsafe.ARRAY_CHAR_BASE_OFFSET, (long)var8);
               com.frojasg1.sun.nio.fs.WindowsPath var10 = com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(this.fs, new String(var9));
               var1.signalEvent(var7, var10);
            }

            var5 = UNSAFE.getInt(var3 + 0L);
            var3 += (long)var5;
         } while(var5 != 0);

      }

      public void run() {
         while(true) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CompletionStatus var1;
            try {
               var1 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetQueuedCompletionStatus(this.port);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var8) {
               var8.printStackTrace();
               return;
            }

            if (var1.completionKey() == 0L) {
               boolean var9 = this.processRequests();
               if (var9) {
                  return;
               }
            } else {
               WindowsWatchService.WindowsWatchKey var2 = (WindowsWatchService.WindowsWatchKey)this.ck2key.get((int)var1.completionKey());
               if (var2 != null) {
                  boolean var3 = false;
                  int var4 = var1.error();
                  int var5 = var1.bytesTransferred();
                  if (var4 == 1022) {
                     var2.signalEvent(StandardWatchEventKinds.OVERFLOW, (Object)null);
                  } else if (var4 != 0 && var4 != 234) {
                     var3 = true;
                  } else {
                     if (var5 > 0) {
                        this.processEvents(var2, var5);
                     } else if (var4 == 0) {
                        var2.signalEvent(StandardWatchEventKinds.OVERFLOW, (Object)null);
                     }

                     try {
                        com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.ReadDirectoryChangesW(var2.handle(), var2.buffer().address(), 16384, var2.watchSubtree(), 351, var2.countAddress(), var2.overlappedAddress());
                     } catch (com.frojasg1.sun.nio.fs.WindowsException var7) {
                        var3 = true;
                        var2.setErrorStartingOverlapped(true);
                     }
                  }

                  if (var3) {
                     this.implCancelKey(var2);
                     var2.signal();
                  }
               }
            }
         }
      }

      static {
         OFFSETOF_HEVENT = (short)(UNSAFE.addressSize() == 4 ? 16 : 24);
      }
   }

   private static class WindowsWatchKey extends com.frojasg1.sun.nio.fs.AbstractWatchKey {
      private final WindowsWatchService.FileKey fileKey;
      private volatile long handle = -1L;
      private Set<? extends Kind<?>> events;
      private boolean watchSubtree;
      private com.frojasg1.sun.nio.fs.NativeBuffer buffer;
      private long countAddress;
      private long overlappedAddress;
      private int completionKey;
      private boolean errorStartingOverlapped;

      WindowsWatchKey(Path var1, com.frojasg1.sun.nio.fs.AbstractWatchService var2, WindowsWatchService.FileKey var3) {
         super(var1, var2);
         this.fileKey = var3;
      }

      WindowsWatchService.WindowsWatchKey init(long var1, Set<? extends Kind<?>> var3, boolean var4, com.frojasg1.sun.nio.fs.NativeBuffer var5, long var6, long var8, int var10) {
         this.handle = var1;
         this.events = var3;
         this.watchSubtree = var4;
         this.buffer = var5;
         this.countAddress = var6;
         this.overlappedAddress = var8;
         this.completionKey = var10;
         return this;
      }

      long handle() {
         return this.handle;
      }

      Set<? extends Kind<?>> events() {
         return this.events;
      }

      void setEvents(Set<? extends Kind<?>> var1) {
         this.events = var1;
      }

      boolean watchSubtree() {
         return this.watchSubtree;
      }

      com.frojasg1.sun.nio.fs.NativeBuffer buffer() {
         return this.buffer;
      }

      long countAddress() {
         return this.countAddress;
      }

      long overlappedAddress() {
         return this.overlappedAddress;
      }

      WindowsWatchService.FileKey fileKey() {
         return this.fileKey;
      }

      int completionKey() {
         return this.completionKey;
      }

      void setErrorStartingOverlapped(boolean var1) {
         this.errorStartingOverlapped = var1;
      }

      boolean isErrorStartingOverlapped() {
         return this.errorStartingOverlapped;
      }

      void invalidate() {
         ((WindowsWatchService)this.watcher()).poller.releaseResources(this);
         this.handle = -1L;
         this.buffer = null;
         this.countAddress = 0L;
         this.overlappedAddress = 0L;
         this.errorStartingOverlapped = false;
      }

      public boolean isValid() {
         return this.handle != -1L;
      }

      public void cancel() {
         if (this.isValid()) {
            ((WindowsWatchService)this.watcher()).poller.cancel(this);
         }

      }
   }
}
