package com.frojasg1.sun.nio.fs;

import com.sun.nio.file.ExtendedWatchEventModifier;
import com.frojasg1.sun.nio.fs.AbstractPath;
import com.frojasg1.sun.nio.fs.BasicFileAttributesHolder;
import com.frojasg1.sun.nio.fs.Util;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileSystem;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPathParser;
import com.frojasg1.sun.nio.fs.WindowsPathType;
import com.frojasg1.sun.nio.fs.WindowsUriSupport;
import com.frojasg1.sun.nio.fs.WindowsWatchService;

import java.io.IOError;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.nio.file.InvalidPathException;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.ProviderMismatchException;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchEvent.Modifier;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

class WindowsPath extends com.frojasg1.sun.nio.fs.AbstractPath {
   private static final int MAX_PATH = 247;
   private static final int MAX_LONG_PATH = 32000;
   private final com.frojasg1.sun.nio.fs.WindowsFileSystem fs;
   private final com.frojasg1.sun.nio.fs.WindowsPathType type;
   private final String root;
   private final String path;
   private volatile WeakReference<String> pathForWin32Calls;
   private volatile Integer[] offsets;
   private int hash;

   private WindowsPath(com.frojasg1.sun.nio.fs.WindowsFileSystem var1, com.frojasg1.sun.nio.fs.WindowsPathType var2, String var3, String var4) {
      this.fs = var1;
      this.type = var2;
      this.root = var3;
      this.path = var4;
   }

   static WindowsPath parse(com.frojasg1.sun.nio.fs.WindowsFileSystem var0, String var1) {
      com.frojasg1.sun.nio.fs.WindowsPathParser.Result var2 = com.frojasg1.sun.nio.fs.WindowsPathParser.parse(var1);
      return new WindowsPath(var0, var2.type(), var2.root(), var2.path());
   }

   static WindowsPath createFromNormalizedPath(com.frojasg1.sun.nio.fs.WindowsFileSystem var0, String var1, BasicFileAttributes var2) {
      try {
         com.frojasg1.sun.nio.fs.WindowsPathParser.Result var3 = com.frojasg1.sun.nio.fs.WindowsPathParser.parseNormalizedPath(var1);
         return (WindowsPath)(var2 == null ? new WindowsPath(var0, var3.type(), var3.root(), var3.path()) : new WindowsPath.WindowsPathWithAttributes(var0, var3.type(), var3.root(), var3.path(), var2));
      } catch (InvalidPathException var4) {
         throw new AssertionError(var4.getMessage());
      }
   }

   static WindowsPath createFromNormalizedPath(com.frojasg1.sun.nio.fs.WindowsFileSystem var0, String var1) {
      return createFromNormalizedPath(var0, var1, (BasicFileAttributes)null);
   }

   String getPathForExceptionMessage() {
      return this.path;
   }

   String getPathForPermissionCheck() {
      return this.path;
   }

   String getPathForWin32Calls() throws com.frojasg1.sun.nio.fs.WindowsException {
      if (this.isAbsolute() && this.path.length() <= 247) {
         return this.path;
      } else {
         WeakReference var1 = this.pathForWin32Calls;
         String var2 = var1 != null ? (String)var1.get() : null;
         if (var2 != null) {
            return var2;
         } else {
            var2 = this.getAbsolutePath();
            if (var2.length() > 247) {
               if (var2.length() > 32000) {
                  throw new com.frojasg1.sun.nio.fs.WindowsException("Cannot access file with path exceeding 32000 characters");
               }

               var2 = addPrefixIfNeeded(com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFullPathName(var2));
            }

            if (this.type != com.frojasg1.sun.nio.fs.WindowsPathType.DRIVE_RELATIVE) {
               synchronized(this.path) {
                  this.pathForWin32Calls = new WeakReference(var2);
               }
            }

            return var2;
         }
      }
   }

   private String getAbsolutePath() throws com.frojasg1.sun.nio.fs.WindowsException {
      if (this.isAbsolute()) {
         return this.path;
      } else {
         String var1;
         if (this.type == com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE) {
            var1 = this.getFileSystem().defaultDirectory();
            if (this.isEmpty()) {
               return var1;
            } else if (var1.endsWith("\\")) {
               return var1 + this.path;
            } else {
               StringBuilder var6 = new StringBuilder(var1.length() + this.path.length() + 1);
               return var6.append(var1).append('\\').append(this.path).toString();
            }
         } else if (this.type == com.frojasg1.sun.nio.fs.WindowsPathType.DIRECTORY_RELATIVE) {
            var1 = this.getFileSystem().defaultRoot();
            return var1 + this.path.substring(1);
         } else {
            String var5;
            if (isSameDrive(this.root, this.getFileSystem().defaultRoot())) {
               var1 = this.path.substring(this.root.length());
               var5 = this.getFileSystem().defaultDirectory();
               String var3;
               if (var5.endsWith("\\")) {
                  var3 = var5 + var1;
               } else {
                  var3 = var5 + "\\" + var1;
               }

               return var3;
            } else {
               try {
                  int var2 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetDriveType(this.root + "\\");
                  if (var2 == 0 || var2 == 1) {
                     throw new com.frojasg1.sun.nio.fs.WindowsException("");
                  }

                  var1 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFullPathName(this.root + ".");
               } catch (com.frojasg1.sun.nio.fs.WindowsException var4) {
                  throw new com.frojasg1.sun.nio.fs.WindowsException("Unable to get working directory of drive '" + Character.toUpperCase(this.root.charAt(0)) + "'");
               }

               var5 = var1;
               if (var1.endsWith("\\")) {
                  var5 = var1 + this.path.substring(this.root.length());
               } else if (this.path.length() > this.root.length()) {
                  var5 = var1 + "\\" + this.path.substring(this.root.length());
               }

               return var5;
            }
         }
      }
   }

   private static boolean isSameDrive(String var0, String var1) {
      return Character.toUpperCase(var0.charAt(0)) == Character.toUpperCase(var1.charAt(0));
   }

   static String addPrefixIfNeeded(String var0) {
      if (var0.length() > 247) {
         if (var0.startsWith("\\\\")) {
            var0 = "\\\\?\\UNC" + var0.substring(1, var0.length());
         } else {
            var0 = "\\\\?\\" + var0;
         }
      }

      return var0;
   }

   public com.frojasg1.sun.nio.fs.WindowsFileSystem getFileSystem() {
      return this.fs;
   }

   private boolean isEmpty() {
      return this.path.length() == 0;
   }

   private WindowsPath emptyPath() {
      return new WindowsPath(this.getFileSystem(), com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE, "", "");
   }

   public Path getFileName() {
      int var1 = this.path.length();
      if (var1 == 0) {
         return this;
      } else if (this.root.length() == var1) {
         return null;
      } else {
         int var2 = this.path.lastIndexOf(92);
         if (var2 < this.root.length()) {
            var2 = this.root.length();
         } else {
            ++var2;
         }

         return new WindowsPath(this.getFileSystem(), com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE, "", this.path.substring(var2));
      }
   }

   public WindowsPath getParent() {
      if (this.root.length() == this.path.length()) {
         return null;
      } else {
         int var1 = this.path.lastIndexOf(92);
         return var1 < this.root.length() ? this.getRoot() : new WindowsPath(this.getFileSystem(), this.type, this.root, this.path.substring(0, var1));
      }
   }

   public WindowsPath getRoot() {
      return this.root.length() == 0 ? null : new WindowsPath(this.getFileSystem(), this.type, this.root, this.root);
   }

   com.frojasg1.sun.nio.fs.WindowsPathType type() {
      return this.type;
   }

   boolean isUnc() {
      return this.type == com.frojasg1.sun.nio.fs.WindowsPathType.UNC;
   }

   boolean needsSlashWhenResolving() {
      if (this.path.endsWith("\\")) {
         return false;
      } else {
         return this.path.length() > this.root.length();
      }
   }

   public boolean isAbsolute() {
      return this.type == com.frojasg1.sun.nio.fs.WindowsPathType.ABSOLUTE || this.type == com.frojasg1.sun.nio.fs.WindowsPathType.UNC;
   }

   static WindowsPath toWindowsPath(Path var0) {
      if (var0 == null) {
         throw new NullPointerException();
      } else if (!(var0 instanceof WindowsPath)) {
         throw new ProviderMismatchException();
      } else {
         return (WindowsPath)var0;
      }
   }

   public WindowsPath relativize(Path var1) {
      WindowsPath var2 = toWindowsPath(var1);
      if (this.equals(var2)) {
         return this.emptyPath();
      } else if (this.type != var2.type) {
         throw new IllegalArgumentException("'other' is different type of Path");
      } else if (!this.root.equalsIgnoreCase(var2.root)) {
         throw new IllegalArgumentException("'other' has different root");
      } else {
         int var3 = this.getNameCount();
         int var4 = var2.getNameCount();
         int var5 = var3 > var4 ? var4 : var3;

         int var6;
         for(var6 = 0; var6 < var5 && this.getName(var6).equals(var2.getName(var6)); ++var6) {
         }

         StringBuilder var7 = new StringBuilder();

         int var8;
         for(var8 = var6; var8 < var3; ++var8) {
            var7.append("..\\");
         }

         for(var8 = var6; var8 < var4; ++var8) {
            var7.append(var2.getName(var8).toString());
            var7.append("\\");
         }

         var7.setLength(var7.length() - 1);
         return createFromNormalizedPath(this.getFileSystem(), var7.toString());
      }
   }

   public Path normalize() {
      int var1 = this.getNameCount();
      if (var1 != 0 && !this.isEmpty()) {
         boolean[] var2 = new boolean[var1];
         int var3 = var1;

         int var6;
         do {
            int var5 = -1;

            for(var6 = 0; var6 < var1; ++var6) {
               if (!var2[var6]) {
                  String var7 = this.elementAsString(var6);
                  if (var7.length() > 2) {
                     var5 = var6;
                  } else if (var7.length() == 1) {
                     if (var7.charAt(0) == '.') {
                        var2[var6] = true;
                        --var3;
                     } else {
                        var5 = var6;
                     }
                  } else if (var7.charAt(0) == '.' && var7.charAt(1) == '.') {
                     if (var5 >= 0) {
                        var2[var5] = true;
                        var2[var6] = true;
                        var3 -= 2;
                        var5 = -1;
                     } else if (this.isAbsolute() || this.type == com.frojasg1.sun.nio.fs.WindowsPathType.DIRECTORY_RELATIVE) {
                        boolean var8 = false;

                        for(int var9 = 0; var9 < var6; ++var9) {
                           if (!var2[var9]) {
                              var8 = true;
                              break;
                           }
                        }

                        if (!var8) {
                           var2[var6] = true;
                           --var3;
                        }
                     }
                  } else {
                     var5 = var6;
                  }
               }
            }
         } while(var3 > var3);

         if (var3 == var1) {
            return this;
         } else if (var3 == 0) {
            return this.root.length() == 0 ? this.emptyPath() : this.getRoot();
         } else {
            StringBuilder var10 = new StringBuilder();
            if (this.root != null) {
               var10.append(this.root);
            }

            for(var6 = 0; var6 < var1; ++var6) {
               if (!var2[var6]) {
                  var10.append(this.getName(var6));
                  var10.append("\\");
               }
            }

            var10.setLength(var10.length() - 1);
            return createFromNormalizedPath(this.getFileSystem(), var10.toString());
         }
      } else {
         return this;
      }
   }

   public WindowsPath resolve(Path var1) {
      WindowsPath var2 = toWindowsPath(var1);
      if (var2.isEmpty()) {
         return this;
      } else if (var2.isAbsolute()) {
         return var2;
      } else {
         String var3;
         switch(var2.type) {
         case RELATIVE:
            if (!this.path.endsWith("\\") && this.root.length() != this.path.length()) {
               var3 = this.path + "\\" + var2.path;
            } else {
               var3 = this.path + var2.path;
            }

            return new WindowsPath(this.getFileSystem(), this.type, this.root, var3);
         case DIRECTORY_RELATIVE:
            if (this.root.endsWith("\\")) {
               var3 = this.root + var2.path.substring(1);
            } else {
               var3 = this.root + var2.path;
            }

            return createFromNormalizedPath(this.getFileSystem(), var3);
         case DRIVE_RELATIVE:
            if (!this.root.endsWith("\\")) {
               return var2;
            } else {
               var3 = this.root.substring(0, this.root.length() - 1);
               if (!var3.equalsIgnoreCase(var2.root)) {
                  return var2;
               }

               String var4 = var2.path.substring(var2.root.length());
               String var5;
               if (this.path.endsWith("\\")) {
                  var5 = this.path + var4;
               } else {
                  var5 = this.path + "\\" + var4;
               }

               return createFromNormalizedPath(this.getFileSystem(), var5);
            }
         default:
            throw new AssertionError();
         }
      }
   }

   private void initOffsets() {
      if (this.offsets == null) {
         ArrayList var1 = new ArrayList();
         if (this.isEmpty()) {
            var1.add(0);
         } else {
            int var2 = this.root.length();
            int var3 = this.root.length();

            while(var3 < this.path.length()) {
               if (this.path.charAt(var3) != '\\') {
                  ++var3;
               } else {
                  var1.add(var2);
                  ++var3;
                  var2 = var3;
               }
            }

            if (var2 != var3) {
               var1.add(var2);
            }
         }

         synchronized(this) {
            if (this.offsets == null) {
               this.offsets = (Integer[])var1.toArray(new Integer[var1.size()]);
            }
         }
      }

   }

   public int getNameCount() {
      this.initOffsets();
      return this.offsets.length;
   }

   private String elementAsString(int var1) {
      this.initOffsets();
      return var1 == this.offsets.length - 1 ? this.path.substring(this.offsets[var1]) : this.path.substring(this.offsets[var1], this.offsets[var1 + 1] - 1);
   }

   public WindowsPath getName(int var1) {
      this.initOffsets();
      if (var1 >= 0 && var1 < this.offsets.length) {
         return new WindowsPath(this.getFileSystem(), com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE, "", this.elementAsString(var1));
      } else {
         throw new IllegalArgumentException();
      }
   }

   public WindowsPath subpath(int var1, int var2) {
      this.initOffsets();
      if (var1 < 0) {
         throw new IllegalArgumentException();
      } else if (var1 >= this.offsets.length) {
         throw new IllegalArgumentException();
      } else if (var2 > this.offsets.length) {
         throw new IllegalArgumentException();
      } else if (var1 >= var2) {
         throw new IllegalArgumentException();
      } else {
         StringBuilder var3 = new StringBuilder();
         Integer[] var4 = new Integer[var2 - var1];

         for(int var5 = var1; var5 < var2; ++var5) {
            var4[var5 - var1] = var3.length();
            var3.append(this.elementAsString(var5));
            if (var5 != var2 - 1) {
               var3.append("\\");
            }
         }

         return new WindowsPath(this.getFileSystem(), com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE, "", var3.toString());
      }
   }

   public boolean startsWith(Path var1) {
      if (!(Objects.requireNonNull(var1) instanceof WindowsPath)) {
         return false;
      } else {
         WindowsPath var2 = (WindowsPath)var1;
         if (!this.root.equalsIgnoreCase(var2.root)) {
            return false;
         } else if (var2.isEmpty()) {
            return this.isEmpty();
         } else {
            int var3 = this.getNameCount();
            int var4 = var2.getNameCount();
            if (var4 <= var3) {
               String var5;
               String var6;
               do {
                  --var4;
                  if (var4 < 0) {
                     return true;
                  }

                  var5 = this.elementAsString(var4);
                  var6 = var2.elementAsString(var4);
               } while(var5.equalsIgnoreCase(var6));

               return false;
            } else {
               return false;
            }
         }
      }
   }

   public boolean endsWith(Path var1) {
      if (!(Objects.requireNonNull(var1) instanceof WindowsPath)) {
         return false;
      } else {
         WindowsPath var2 = (WindowsPath)var1;
         if (var2.path.length() > this.path.length()) {
            return false;
         } else if (var2.isEmpty()) {
            return this.isEmpty();
         } else {
            int var3 = this.getNameCount();
            int var4 = var2.getNameCount();
            if (var4 > var3) {
               return false;
            } else {
               if (var2.root.length() > 0) {
                  if (var4 < var3) {
                     return false;
                  }

                  if (!this.root.equalsIgnoreCase(var2.root)) {
                     return false;
                  }
               }

               int var5 = var3 - var4;

               String var6;
               String var7;
               do {
                  --var4;
                  if (var4 < 0) {
                     return true;
                  }

                  var6 = this.elementAsString(var5 + var4);
                  var7 = var2.elementAsString(var4);
               } while(var6.equalsIgnoreCase(var7));

               return false;
            }
         }
      }
   }

   public int compareTo(Path var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         String var2 = this.path;
         String var3 = ((WindowsPath)var1).path;
         int var4 = var2.length();
         int var5 = var3.length();
         int var6 = Math.min(var4, var5);

         for(int var7 = 0; var7 < var6; ++var7) {
            char var8 = var2.charAt(var7);
            char var9 = var3.charAt(var7);
            if (var8 != var9) {
               var8 = Character.toUpperCase(var8);
               var9 = Character.toUpperCase(var9);
               if (var8 != var9) {
                  return var8 - var9;
               }
            }
         }

         return var4 - var5;
      }
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof WindowsPath) {
         return this.compareTo((Path)var1) == 0;
      } else {
         return false;
      }
   }

   public int hashCode() {
      int var1 = this.hash;
      if (var1 == 0) {
         for(int var2 = 0; var2 < this.path.length(); ++var2) {
            var1 = 31 * var1 + Character.toUpperCase(this.path.charAt(var2));
         }

         this.hash = var1;
      }

      return var1;
   }

   public String toString() {
      return this.path;
   }

   long openForReadAttributeAccess(boolean var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      int var2 = 33554432;
      if (!var1 && this.getFileSystem().supportsLinks()) {
         var2 |= 2097152;
      }

      return com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CreateFile(this.getPathForWin32Calls(), 128, 7, 0L, 3, var2);
   }

   void checkRead() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkRead(this.getPathForPermissionCheck());
      }

   }

   void checkWrite() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkWrite(this.getPathForPermissionCheck());
      }

   }

   void checkDelete() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkDelete(this.getPathForPermissionCheck());
      }

   }

   public URI toUri() {
      return com.frojasg1.sun.nio.fs.WindowsUriSupport.toUri(this);
   }

   public WindowsPath toAbsolutePath() {
      if (this.isAbsolute()) {
         return this;
      } else {
         SecurityManager var1 = System.getSecurityManager();
         if (var1 != null) {
            var1.checkPropertyAccess("user.dir");
         }

         try {
            return createFromNormalizedPath(this.getFileSystem(), this.getAbsolutePath());
         } catch (com.frojasg1.sun.nio.fs.WindowsException var3) {
            throw new IOError(new IOException(var3.getMessage()));
         }
      }
   }

   public WindowsPath toRealPath(LinkOption... var1) throws IOException {
      this.checkRead();
      String var2 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getRealPath(this, com.frojasg1.sun.nio.fs.Util.followLinks(var1));
      return createFromNormalizedPath(this.getFileSystem(), var2);
   }

   public WatchKey register(WatchService var1, Kind<?>[] var2, Modifier... var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException();
      } else if (!(var1 instanceof com.frojasg1.sun.nio.fs.WindowsWatchService)) {
         throw new ProviderMismatchException();
      } else {
         SecurityManager var4 = System.getSecurityManager();
         if (var4 != null) {
            boolean var5 = false;
            int var6 = var3.length;
            if (var6 > 0) {
               var3 = (Modifier[])Arrays.copyOf(var3, var6);
               int var7 = 0;

               while(var7 < var6) {
                  if (var3[var7++] == ExtendedWatchEventModifier.FILE_TREE) {
                     var5 = true;
                     break;
                  }
               }
            }

            String var8 = this.getPathForPermissionCheck();
            var4.checkRead(var8);
            if (var5) {
               var4.checkRead(var8 + "\\-");
            }
         }

         return ((com.frojasg1.sun.nio.fs.WindowsWatchService)var1).register(this, var2, var3);
      }
   }

   private static class WindowsPathWithAttributes extends WindowsPath implements BasicFileAttributesHolder {
      final WeakReference<BasicFileAttributes> ref;

      WindowsPathWithAttributes(com.frojasg1.sun.nio.fs.WindowsFileSystem var1, com.frojasg1.sun.nio.fs.WindowsPathType var2, String var3, String var4, BasicFileAttributes var5) {
//         super(var1, var2, var3, var4, null);
         super(var1, var2, var3, var4);
         this.ref = new WeakReference(var5);
      }

      public BasicFileAttributes get() {
         return (BasicFileAttributes)this.ref.get();
      }

      public void invalidate() {
         this.ref.clear();
      }
   }
}
