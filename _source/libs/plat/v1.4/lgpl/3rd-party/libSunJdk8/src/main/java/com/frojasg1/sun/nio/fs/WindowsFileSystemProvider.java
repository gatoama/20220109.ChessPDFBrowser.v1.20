package com.frojasg1.sun.nio.fs;

import java.io.FilePermission;
import java.io.IOException;
import java.net.URI;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.AccessDeniedException;
import java.nio.file.AccessMode;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.FileSystemAlreadyExistsException;
import java.nio.file.LinkOption;
import java.nio.file.LinkPermission;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.DosFileAttributeView;
import java.nio.file.attribute.DosFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.ch.ThreadPool;
import com.frojasg1.sun.nio.fs.AbstractFileSystemProvider;
import com.frojasg1.sun.nio.fs.DynamicFileAttributeView;
import com.frojasg1.sun.nio.fs.FileOwnerAttributeViewImpl;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.Util;
import com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView;
import com.frojasg1.sun.nio.fs.WindowsChannelFactory;
import com.frojasg1.sun.nio.fs.WindowsDirectoryStream;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributeViews;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsFileCopy;
import com.frojasg1.sun.nio.fs.WindowsFileStore;
import com.frojasg1.sun.nio.fs.WindowsFileSystem;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;
import com.frojasg1.sun.nio.fs.WindowsPathType;
import com.frojasg1.sun.nio.fs.WindowsSecurity;
import com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor;
import com.frojasg1.sun.nio.fs.WindowsUriSupport;
import com.frojasg1.sun.nio.fs.WindowsUserDefinedFileAttributeView;

public class WindowsFileSystemProvider extends com.frojasg1.sun.nio.fs.AbstractFileSystemProvider {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final String USER_DIR = "user.dir";
   private final com.frojasg1.sun.nio.fs.WindowsFileSystem theFileSystem = new com.frojasg1.sun.nio.fs.WindowsFileSystem(this, System.getProperty("user.dir"));

   public WindowsFileSystemProvider() {
   }

   public String getScheme() {
      return "file";
   }

   private void checkUri(URI var1) {
      if (!var1.getScheme().equalsIgnoreCase(this.getScheme())) {
         throw new IllegalArgumentException("URI does not match this provider");
      } else if (var1.getAuthority() != null) {
         throw new IllegalArgumentException("Authority component present");
      } else if (var1.getPath() == null) {
         throw new IllegalArgumentException("Path component is undefined");
      } else if (!var1.getPath().equals("/")) {
         throw new IllegalArgumentException("Path component should be '/'");
      } else if (var1.getQuery() != null) {
         throw new IllegalArgumentException("Query component present");
      } else if (var1.getFragment() != null) {
         throw new IllegalArgumentException("Fragment component present");
      }
   }

   public FileSystem newFileSystem(URI var1, Map<String, ?> var2) throws IOException {
      this.checkUri(var1);
      throw new FileSystemAlreadyExistsException();
   }

   public final FileSystem getFileSystem(URI var1) {
      this.checkUri(var1);
      return this.theFileSystem;
   }

   public Path getPath(URI var1) {
      return com.frojasg1.sun.nio.fs.WindowsUriSupport.fromUri(this.theFileSystem, var1);
   }

   public FileChannel newFileChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof com.frojasg1.sun.nio.fs.WindowsPath)) {
         throw new ProviderMismatchException();
      } else {
         com.frojasg1.sun.nio.fs.WindowsPath var4 = (com.frojasg1.sun.nio.fs.WindowsPath)var1;
         com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor var5 = com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.fromAttribute(var3);

         Object var7;
         try {
            FileChannel var6 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newFileChannel(var4.getPathForWin32Calls(), var4.getPathForPermissionCheck(), var2, var5.address());
            return var6;
         } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
            var11.rethrowAsIOException(var4);
            var7 = null;
         } finally {
            if (var5 != null) {
               var5.release();
            }

         }

         return (FileChannel)var7;
      }
   }

   public AsynchronousFileChannel newAsynchronousFileChannel(Path var1, Set<? extends OpenOption> var2, ExecutorService var3, FileAttribute<?>... var4) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof com.frojasg1.sun.nio.fs.WindowsPath)) {
         throw new ProviderMismatchException();
      } else {
         com.frojasg1.sun.nio.fs.WindowsPath var5 = (com.frojasg1.sun.nio.fs.WindowsPath)var1;
         ThreadPool var6 = var3 == null ? null : ThreadPool.wrap(var3, 0);
         com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor var7 = com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.fromAttribute(var4);

         Object var9;
         try {
            AsynchronousFileChannel var8 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newAsynchronousFileChannel(var5.getPathForWin32Calls(), var5.getPathForPermissionCheck(), var2, var7.address(), var6);
            return var8;
         } catch (com.frojasg1.sun.nio.fs.WindowsException var13) {
            var13.rethrowAsIOException(var5);
            var9 = null;
         } finally {
            if (var7 != null) {
               var7.release();
            }

         }

         return (AsynchronousFileChannel)var9;
      }
   }

   public <V extends FileAttributeView> V getFileAttributeView(Path var1, Class<V> var2, LinkOption... var3) {
      com.frojasg1.sun.nio.fs.WindowsPath var4 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         boolean var5 = com.frojasg1.sun.nio.fs.Util.followLinks(var3);
         if (var2 == BasicFileAttributeView.class) {
            return (V) com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.createBasicView(var4, var5);
         } else if (var2 == DosFileAttributeView.class) {
            return (V) com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.createDosView(var4, var5);
         } else if (var2 == AclFileAttributeView.class) {
            return (V) new com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView(var4, var5);
         } else if (var2 == FileOwnerAttributeView.class) {
            return (V) new com.frojasg1.sun.nio.fs.FileOwnerAttributeViewImpl(new com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView(var4, var5));
         } else {
            return (V) (FileAttributeView)(var2 == UserDefinedFileAttributeView.class ? new com.frojasg1.sun.nio.fs.WindowsUserDefinedFileAttributeView(var4, var5) : (FileAttributeView)null);
         }
      }
   }

   public <A extends BasicFileAttributes> A readAttributes(Path var1, Class<A> var2, LinkOption... var3) throws IOException {
      Class var4;
      if (var2 == BasicFileAttributes.class) {
         var4 = BasicFileAttributeView.class;
      } else {
         if (var2 != DosFileAttributes.class) {
            if (var2 == null) {
               throw new NullPointerException();
            }

            throw new UnsupportedOperationException();
         }

         var4 = DosFileAttributeView.class;
      }

      return (A) ((BasicFileAttributeView)this.getFileAttributeView(var1, var4, var3)).readAttributes();
   }

   public com.frojasg1.sun.nio.fs.DynamicFileAttributeView getFileAttributeView(Path var1, String var2, LinkOption... var3) {
      com.frojasg1.sun.nio.fs.WindowsPath var4 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      boolean var5 = com.frojasg1.sun.nio.fs.Util.followLinks(var3);
      if (var2.equals("basic")) {
         return com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.createBasicView(var4, var5);
      } else if (var2.equals("dos")) {
         return com.frojasg1.sun.nio.fs.WindowsFileAttributeViews.createDosView(var4, var5);
      } else if (var2.equals("acl")) {
         return new com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView(var4, var5);
      } else if (var2.equals("owner")) {
         return new com.frojasg1.sun.nio.fs.FileOwnerAttributeViewImpl(new com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView(var4, var5));
      } else {
         return var2.equals("user") ? new com.frojasg1.sun.nio.fs.WindowsUserDefinedFileAttributeView(var4, var5) : null;
      }
   }

   public SeekableByteChannel newByteChannel(Path var1, Set<? extends OpenOption> var2, FileAttribute<?>... var3) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var4 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor var5 = com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.fromAttribute(var3);

      Object var7;
      try {
         FileChannel var6 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newFileChannel(var4.getPathForWin32Calls(), var4.getPathForPermissionCheck(), var2, var5.address());
         return var6;
      } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
         var11.rethrowAsIOException(var4);
         var7 = null;
      } finally {
         var5.release();
      }

      return (SeekableByteChannel)var7;
   }

   boolean implDelete(Path var1, boolean var2) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      var3.checkDelete();
      com.frojasg1.sun.nio.fs.WindowsFileAttributes var4 = null;

      try {
         var4 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var3, false);
         if (!var4.isDirectory() && !var4.isDirectoryLink()) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DeleteFile(var3.getPathForWin32Calls());
         } else {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.RemoveDirectory(var3.getPathForWin32Calls());
         }

         return true;
      } catch (com.frojasg1.sun.nio.fs.WindowsException var6) {
         if (var2 || var6.lastError() != 2 && var6.lastError() != 3) {
            if (var4 == null || !var4.isDirectory() || var6.lastError() != 145 && var6.lastError() != 183) {
               var6.rethrowAsIOException(var3);
               return false;
            } else {
               throw new DirectoryNotEmptyException(var3.getPathForExceptionMessage());
            }
         } else {
            return false;
         }
      }
   }

   public void copy(Path var1, Path var2, CopyOption... var3) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsFileCopy.copy(com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1), com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var2), var3);
   }

   public void move(Path var1, Path var2, CopyOption... var3) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsFileCopy.move(com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1), com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var2), var3);
   }

   private static boolean hasDesiredAccess(com.frojasg1.sun.nio.fs.WindowsPath var0, int var1) throws IOException {
      boolean var2 = false;
      String var3 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(var0, true);
      com.frojasg1.sun.nio.fs.NativeBuffer var4 = com.frojasg1.sun.nio.fs.WindowsAclFileAttributeView.getFileSecurity(var3, 7);

      try {
         var2 = com.frojasg1.sun.nio.fs.WindowsSecurity.checkAccessMask(var4.address(), var1, 1179785, 1179926, 1179808, 2032127);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var9) {
         var9.rethrowAsIOException(var0);
      } finally {
         var4.release();
      }

      return var2;
   }

   private void checkReadAccess(com.frojasg1.sun.nio.fs.WindowsPath var1) throws IOException {
      try {
         Set var2 = Collections.emptySet();
         FileChannel var3 = com.frojasg1.sun.nio.fs.WindowsChannelFactory.newFileChannel(var1.getPathForWin32Calls(), var1.getPathForPermissionCheck(), var2, 0L);
         var3.close();
      } catch (com.frojasg1.sun.nio.fs.WindowsException var5) {
         try {
            (new com.frojasg1.sun.nio.fs.WindowsDirectoryStream(var1, (Filter)null)).close();
         } catch (IOException var4) {
            var5.rethrowAsIOException(var1);
         }
      }

   }

   public void checkAccess(Path var1, AccessMode... var2) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      boolean var4 = false;
      boolean var5 = false;
      boolean var6 = false;
      AccessMode[] var7 = var2;
      int var8 = var2.length;

      for(int var9 = 0; var9 < var8; ++var9) {
         AccessMode var10 = var7[var9];
         switch(var10) {
         case READ:
            var4 = true;
            break;
         case WRITE:
            var5 = true;
            break;
         case EXECUTE:
            var6 = true;
            break;
         default:
            throw new AssertionError("Should not get here");
         }
      }

      if (!var5 && !var6) {
         this.checkReadAccess(var3);
      } else {
         int var12 = 0;
         if (var4) {
            var3.checkRead();
            var12 |= 1;
         }

         if (var5) {
            var3.checkWrite();
            var12 |= 2;
         }

         if (var6) {
            SecurityManager var13 = System.getSecurityManager();
            if (var13 != null) {
               var13.checkExec(var3.getPathForPermissionCheck());
            }

            var12 |= 32;
         }

         if (!hasDesiredAccess(var3, var12)) {
            throw new AccessDeniedException(var3.getPathForExceptionMessage(), (String)null, "Permissions does not allow requested access");
         } else {
            if (var5) {
               try {
                  com.frojasg1.sun.nio.fs.WindowsFileAttributes var14 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var3, true);
                  if (!var14.isDirectory() && var14.isReadOnly()) {
                     throw new AccessDeniedException(var3.getPathForExceptionMessage(), (String)null, "DOS readonly attribute is set");
                  }
               } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
                  var11.rethrowAsIOException(var3);
               }

               if (com.frojasg1.sun.nio.fs.WindowsFileStore.create(var3).isReadOnly()) {
                  throw new AccessDeniedException(var3.getPathForExceptionMessage(), (String)null, "Read-only file system");
               }
            }

         }
      }
   }

   public boolean isSameFile(Path var1, Path var2) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      if (var3.equals(var2)) {
         return true;
      } else if (var2 == null) {
         throw new NullPointerException();
      } else if (!(var2 instanceof com.frojasg1.sun.nio.fs.WindowsPath)) {
         return false;
      } else {
         com.frojasg1.sun.nio.fs.WindowsPath var4 = (com.frojasg1.sun.nio.fs.WindowsPath)var2;
         var3.checkRead();
         var4.checkRead();
         long var5 = 0L;

         try {
            var5 = var3.openForReadAttributeAccess(true);
         } catch (com.frojasg1.sun.nio.fs.WindowsException var31) {
            var31.rethrowAsIOException(var3);
         }

         boolean var11;
         try {
            com.frojasg1.sun.nio.fs.WindowsFileAttributes var7 = null;

            try {
               var7 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var5);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var29) {
               var29.rethrowAsIOException(var3);
            }

            long var8 = 0L;

            try {
               var8 = var4.openForReadAttributeAccess(true);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var28) {
               var28.rethrowAsIOException(var4);
            }

            try {
               com.frojasg1.sun.nio.fs.WindowsFileAttributes var10 = null;

               try {
                  var10 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.readAttributes(var8);
               } catch (com.frojasg1.sun.nio.fs.WindowsException var26) {
                  var26.rethrowAsIOException(var4);
               }

               var11 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.isSameFile(var7, var10);
            } finally {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var8);
            }
         } finally {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var5);
         }

         return var11;
      }
   }

   public boolean isHidden(Path var1) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var2 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      var2.checkRead();
      com.frojasg1.sun.nio.fs.WindowsFileAttributes var3 = null;

      try {
         var3 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var2, true);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var5) {
         var5.rethrowAsIOException(var2);
      }

      return var3.isDirectory() ? false : var3.isHidden();
   }

   public FileStore getFileStore(Path var1) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var2 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPermission(new RuntimePermission("getFileStoreAttributes"));
         var2.checkRead();
      }

      return com.frojasg1.sun.nio.fs.WindowsFileStore.create(var2);
   }

   public void createDirectory(Path var1, FileAttribute<?>... var2) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      var3.checkWrite();
      com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor var4 = com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.fromAttribute(var2);

      try {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateDirectory(var3.getPathForWin32Calls(), var4.address());
      } catch (com.frojasg1.sun.nio.fs.WindowsException var12) {
         if (var12.lastError() == 5) {
            try {
               if (com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var3, false).isDirectory()) {
                  throw new FileAlreadyExistsException(var3.toString());
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
            }
         }

         var12.rethrowAsIOException(var3);
      } finally {
         var4.release();
      }

   }

   public DirectoryStream<Path> newDirectoryStream(Path var1, Filter<? super Path> var2) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      var3.checkRead();
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return new com.frojasg1.sun.nio.fs.WindowsDirectoryStream(var3, var2);
      }
   }

   public void createSymbolicLink(Path var1, Path var2, FileAttribute<?>... var3) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var4 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      com.frojasg1.sun.nio.fs.WindowsPath var5 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var2);
      if (!var4.getFileSystem().supportsLinks()) {
         throw new UnsupportedOperationException("Symbolic links not supported on this operating system");
      } else if (var3.length > 0) {
         com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.fromAttribute(var3);
         throw new UnsupportedOperationException("Initial file attributesnot supported when creating symbolic link");
      } else {
         SecurityManager var6 = System.getSecurityManager();
         if (var6 != null) {
            var6.checkPermission(new LinkPermission("symbolic"));
            var4.checkWrite();
         }

         if (var5.type() == com.frojasg1.sun.nio.fs.WindowsPathType.DRIVE_RELATIVE) {
            throw new IOException("Cannot create symbolic link to working directory relative target");
         } else {
            com.frojasg1.sun.nio.fs.WindowsPath var7;
            if (var5.type() == com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE) {
               com.frojasg1.sun.nio.fs.WindowsPath var8 = var4.getParent();
               var7 = var8 == null ? var5 : var8.resolve(var5);
            } else {
               var7 = var4.resolve(var5);
            }

            int var12 = 0;

            try {
               com.frojasg1.sun.nio.fs.WindowsFileAttributes var9 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var7, false);
               if (var9.isDirectory() || var9.isDirectoryLink()) {
                  var12 |= 1;
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
            }

            try {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateSymbolicLink(var4.getPathForWin32Calls(), com.frojasg1.sun.nio.fs.WindowsPath.addPrefixIfNeeded(var5.toString()), var12);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var10) {
               if (var10.lastError() == 4392) {
                  var10.rethrowAsIOException(var4, var5);
               } else {
                  var10.rethrowAsIOException(var4);
               }
            }

         }
      }
   }

   public void createLink(Path var1, Path var2) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var3 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      com.frojasg1.sun.nio.fs.WindowsPath var4 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var2);
      SecurityManager var5 = System.getSecurityManager();
      if (var5 != null) {
         var5.checkPermission(new LinkPermission("hard"));
         var3.checkWrite();
         var4.checkWrite();
      }

      try {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateHardLink(var3.getPathForWin32Calls(), var4.getPathForWin32Calls());
      } catch (com.frojasg1.sun.nio.fs.WindowsException var7) {
         var7.rethrowAsIOException(var3, var4);
      }

   }

   public Path readSymbolicLink(Path var1) throws IOException {
      com.frojasg1.sun.nio.fs.WindowsPath var2 = com.frojasg1.sun.nio.fs.WindowsPath.toWindowsPath(var1);
      com.frojasg1.sun.nio.fs.WindowsFileSystem var3 = var2.getFileSystem();
      if (!var3.supportsLinks()) {
         throw new UnsupportedOperationException("symbolic links not supported");
      } else {
         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            FilePermission var5 = new FilePermission(var2.getPathForPermissionCheck(), "readlink");
            var4.checkPermission(var5);
         }

         String var6 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.readLink(var2);
         return com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(var3, var6);
      }
   }
}
