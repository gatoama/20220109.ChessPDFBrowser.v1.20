package com.frojasg1.sun.nio.fs;

import java.io.IOException;
import java.nio.file.FileStore;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.WatchService;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalLookupService;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.nio.file.spi.FileSystemProvider;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Pattern;

import com.frojasg1.sun.nio.fs.Globs;
import com.frojasg1.sun.nio.fs.Util;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileStore;
import com.frojasg1.sun.nio.fs.WindowsFileSystemProvider;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;
import com.frojasg1.sun.nio.fs.WindowsPathParser;
import com.frojasg1.sun.nio.fs.WindowsPathType;
import com.frojasg1.sun.nio.fs.WindowsUserPrincipals;
import com.frojasg1.sun.nio.fs.WindowsWatchService;
import com.frojasg1.sun.security.action.GetPropertyAction;

class WindowsFileSystem extends FileSystem {
   private final com.frojasg1.sun.nio.fs.WindowsFileSystemProvider provider;
   private final String defaultDirectory;
   private final String defaultRoot;
   private final boolean supportsLinks;
   private final boolean supportsStreamEnumeration;
   private static final Set<String> supportedFileAttributeViews = Collections.unmodifiableSet(new HashSet(Arrays.asList("basic", "dos", "acl", "owner", "user")));
   private static final String GLOB_SYNTAX = "glob";
   private static final String REGEX_SYNTAX = "regex";

   WindowsFileSystem(WindowsFileSystemProvider var1, String var2) {
      this.provider = var1;
      com.frojasg1.sun.nio.fs.WindowsPathParser.Result var3 = com.frojasg1.sun.nio.fs.WindowsPathParser.parse(var2);
      if (var3.type() != com.frojasg1.sun.nio.fs.WindowsPathType.ABSOLUTE && var3.type() != com.frojasg1.sun.nio.fs.WindowsPathType.UNC) {
         throw new AssertionError("Default directory is not an absolute path");
      } else {
         this.defaultDirectory = var3.path();
         this.defaultRoot = var3.root();
         GetPropertyAction var4 = new GetPropertyAction("os.version");
         String var5 = (String)AccessController.doPrivileged(var4);
         String[] var6 = com.frojasg1.sun.nio.fs.Util.split(var5, '.');
         int var7 = Integer.parseInt(var6[0]);
         int var8 = Integer.parseInt(var6[1]);
         this.supportsLinks = var7 >= 6;
         this.supportsStreamEnumeration = var7 >= 6 || var7 == 5 && var8 >= 2;
      }
   }

   String defaultDirectory() {
      return this.defaultDirectory;
   }

   String defaultRoot() {
      return this.defaultRoot;
   }

   boolean supportsLinks() {
      return this.supportsLinks;
   }

   boolean supportsStreamEnumeration() {
      return this.supportsStreamEnumeration;
   }

   public FileSystemProvider provider() {
      return this.provider;
   }

   public String getSeparator() {
      return "\\";
   }

   public boolean isOpen() {
      return true;
   }

   public boolean isReadOnly() {
      return false;
   }

   public void close() throws IOException {
      throw new UnsupportedOperationException();
   }

   public Iterable<Path> getRootDirectories() {
      boolean var1 = false;

      int var10;
      try {
         var10 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetLogicalDrives();
      } catch (com.frojasg1.sun.nio.fs.WindowsException var8) {
         throw new AssertionError(var8.getMessage());
      }

      ArrayList var2 = new ArrayList();
      SecurityManager var3 = System.getSecurityManager();

      for(int var4 = 0; var4 <= 25; ++var4) {
         if ((var10 & 1 << var4) != 0) {
            StringBuilder var5 = new StringBuilder(3);
            var5.append((char)(65 + var4));
            var5.append(":\\");
            String var6 = var5.toString();
            if (var3 != null) {
               try {
                  var3.checkRead(var6);
               } catch (SecurityException var9) {
                  continue;
               }
            }

            var2.add(com.frojasg1.sun.nio.fs.WindowsPath.createFromNormalizedPath(this, var6));
         }
      }

      return Collections.unmodifiableList(var2);
   }

   public Iterable<FileStore> getFileStores() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         try {
            var1.checkPermission(new RuntimePermission("getFileStoreAttributes"));
         } catch (SecurityException var3) {
            return Collections.emptyList();
         }
      }

      return new Iterable<FileStore>() {
         public Iterator<FileStore> iterator() {
            return WindowsFileSystem.this.new FileStoreIterator();
         }
      };
   }

   public Set<String> supportedFileAttributeViews() {
      return supportedFileAttributeViews;
   }

   public final Path getPath(String var1, String... var2) {
      String var3;
      if (var2.length == 0) {
         var3 = var1;
      } else {
         StringBuilder var4 = new StringBuilder();
         var4.append(var1);
         String[] var5 = var2;
         int var6 = var2.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String var8 = var5[var7];
            if (var8.length() > 0) {
               if (var4.length() > 0) {
                  var4.append('\\');
               }

               var4.append(var8);
            }
         }

         var3 = var4.toString();
      }

      return com.frojasg1.sun.nio.fs.WindowsPath.parse(this, var3);
   }

   public UserPrincipalLookupService getUserPrincipalLookupService() {
      return WindowsFileSystem.LookupService.instance;
   }

   public PathMatcher getPathMatcher(String var1) {
      int var2 = var1.indexOf(58);
      if (var2 > 0 && var2 != var1.length()) {
         String var3 = var1.substring(0, var2);
         String var4 = var1.substring(var2 + 1);
         String var5;
         if (var3.equals("glob")) {
            var5 = Globs.toWindowsRegexPattern(var4);
         } else {
            if (!var3.equals("regex")) {
               throw new UnsupportedOperationException("Syntax '" + var3 + "' not recognized");
            }

            var5 = var4;
         }

         final Pattern var6 = Pattern.compile(var5, 66);
         return new PathMatcher() {
            public boolean matches(Path var1) {
               return var6.matcher(var1.toString()).matches();
            }
         };
      } else {
         throw new IllegalArgumentException();
      }
   }

   public WatchService newWatchService() throws IOException {
      return new com.frojasg1.sun.nio.fs.WindowsWatchService(this);
   }

   private class FileStoreIterator implements Iterator<FileStore> {
      private final Iterator<Path> roots = WindowsFileSystem.this.getRootDirectories().iterator();
      private FileStore next;

      FileStoreIterator() {
      }

      private FileStore readNext() {
         assert Thread.holdsLock(this);

         while(this.roots.hasNext()) {
            com.frojasg1.sun.nio.fs.WindowsPath var1 = (com.frojasg1.sun.nio.fs.WindowsPath)this.roots.next();

            try {
               var1.checkRead();
            } catch (SecurityException var4) {
               continue;
            }

            try {
               com.frojasg1.sun.nio.fs.WindowsFileStore var2 = com.frojasg1.sun.nio.fs.WindowsFileStore.create(var1.toString(), true);
               if (var2 != null) {
                  return var2;
               }
            } catch (IOException var3) {
            }
         }

         return null;
      }

      public synchronized boolean hasNext() {
         if (this.next != null) {
            return true;
         } else {
            this.next = this.readNext();
            return this.next != null;
         }
      }

      public synchronized FileStore next() {
         if (this.next == null) {
            this.next = this.readNext();
         }

         if (this.next == null) {
            throw new NoSuchElementException();
         } else {
            FileStore var1 = this.next;
            this.next = null;
            return var1;
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }
   }

   private static class LookupService {
      static final UserPrincipalLookupService instance = new UserPrincipalLookupService() {
         public UserPrincipal lookupPrincipalByName(String var1) throws IOException {
            return com.frojasg1.sun.nio.fs.WindowsUserPrincipals.lookup(var1);
         }

         public GroupPrincipal lookupPrincipalByGroupName(String var1) throws IOException {
            UserPrincipal var2 = com.frojasg1.sun.nio.fs.WindowsUserPrincipals.lookup(var1);
            if (!(var2 instanceof GroupPrincipal)) {
               throw new UserPrincipalNotFoundException(var1);
            } else {
               return (GroupPrincipal)var2;
            }
         }
      };

      private LookupService() {
      }
   }
}
