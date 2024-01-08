package com.frojasg1.sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.frojasg1.sun.awt.shell.ShellFolder;
import com.frojasg1.sun.awt.shell.ShellFolderColumnInfo;
import com.frojasg1.sun.awt.shell.Win32ShellFolderManager2;
import com.frojasg1.sun.java2d.Disposer;
import com.frojasg1.sun.java2d.DisposerRecord;

final class Win32ShellFolder2 extends com.frojasg1.sun.awt.shell.ShellFolder {
   public static final int DESKTOP = 0;
   public static final int INTERNET = 1;
   public static final int PROGRAMS = 2;
   public static final int CONTROLS = 3;
   public static final int PRINTERS = 4;
   public static final int PERSONAL = 5;
   public static final int FAVORITES = 6;
   public static final int STARTUP = 7;
   public static final int RECENT = 8;
   public static final int SENDTO = 9;
   public static final int BITBUCKET = 10;
   public static final int STARTMENU = 11;
   public static final int DESKTOPDIRECTORY = 16;
   public static final int DRIVES = 17;
   public static final int NETWORK = 18;
   public static final int NETHOOD = 19;
   public static final int FONTS = 20;
   public static final int TEMPLATES = 21;
   public static final int COMMON_STARTMENU = 22;
   public static final int COMMON_PROGRAMS = 23;
   public static final int COMMON_STARTUP = 24;
   public static final int COMMON_DESKTOPDIRECTORY = 25;
   public static final int APPDATA = 26;
   public static final int PRINTHOOD = 27;
   public static final int ALTSTARTUP = 29;
   public static final int COMMON_ALTSTARTUP = 30;
   public static final int COMMON_FAVORITES = 31;
   public static final int INTERNET_CACHE = 32;
   public static final int COOKIES = 33;
   public static final int HISTORY = 34;
   public static final int ATTRIB_CANCOPY = 1;
   public static final int ATTRIB_CANMOVE = 2;
   public static final int ATTRIB_CANLINK = 4;
   public static final int ATTRIB_CANRENAME = 16;
   public static final int ATTRIB_CANDELETE = 32;
   public static final int ATTRIB_HASPROPSHEET = 64;
   public static final int ATTRIB_DROPTARGET = 256;
   public static final int ATTRIB_LINK = 65536;
   public static final int ATTRIB_SHARE = 131072;
   public static final int ATTRIB_READONLY = 262144;
   public static final int ATTRIB_GHOSTED = 524288;
   public static final int ATTRIB_HIDDEN = 524288;
   public static final int ATTRIB_FILESYSANCESTOR = 268435456;
   public static final int ATTRIB_FOLDER = 536870912;
   public static final int ATTRIB_FILESYSTEM = 1073741824;
   public static final int ATTRIB_HASSUBFOLDER = -2147483648;
   public static final int ATTRIB_VALIDATE = 16777216;
   public static final int ATTRIB_REMOVABLE = 33554432;
   public static final int ATTRIB_COMPRESSED = 67108864;
   public static final int ATTRIB_BROWSABLE = 134217728;
   public static final int ATTRIB_NONENUMERATED = 1048576;
   public static final int ATTRIB_NEWCONTENT = 2097152;
   public static final int SHGDN_NORMAL = 0;
   public static final int SHGDN_INFOLDER = 1;
   public static final int SHGDN_INCLUDE_NONFILESYS = 8192;
   public static final int SHGDN_FORADDRESSBAR = 16384;
   public static final int SHGDN_FORPARSING = 32768;
   Win32ShellFolder2.FolderDisposer disposer = new Win32ShellFolder2.FolderDisposer();
   private long pIShellIcon = -1L;
   private String folderType = null;
   private String displayName = null;
   private Image smallIcon = null;
   private Image largeIcon = null;
   private Boolean isDir = null;
   private boolean isPersonal;
   private volatile Boolean cachedIsFileSystem;
   private volatile Boolean cachedIsLink;
   private static Map smallSystemImages;
   private static Map largeSystemImages;
   private static Map smallLinkedSystemImages;
   private static Map largeLinkedSystemImages;
   private static final int LVCFMT_LEFT = 0;
   private static final int LVCFMT_RIGHT = 1;
   private static final int LVCFMT_CENTER = 2;

   private static native void initIDs();

   private void setIShellFolder(long var1) {
      this.disposer.pIShellFolder = var1;
   }

   private void setRelativePIDL(long var1) {
      this.disposer.relativePIDL = var1;
   }

   private static String composePathForCsidl(int var0) throws IOException, InterruptedException {
      String var1 = getFileSystemPath(var0);
      return var1 == null ? "ShellFolder: 0x" + Integer.toHexString(var0) : var1;
   }

   Win32ShellFolder2(final int var1) throws IOException, InterruptedException {
      super((com.frojasg1.sun.awt.shell.ShellFolder)null, composePathForCsidl(var1));
      invoke(new Callable<Void>() {
         public Void call() throws InterruptedException {
            if (var1 == 0) {
               Win32ShellFolder2.this.initDesktop();
            } else {
               Win32ShellFolder2.this.initSpecial(Win32ShellFolder2.this.getDesktop().getIShellFolder(), var1);
               long var1x = Win32ShellFolder2.this.disposer.relativePIDL;
               Win32ShellFolder2.this.parent = Win32ShellFolder2.this.getDesktop();

               while(var1x != 0L) {
                  long var3 = Win32ShellFolder2.copyFirstPIDLEntry(var1x);
                  if (var3 == 0L) {
                     break;
                  }

                  var1x = Win32ShellFolder2.getNextPIDLEntry(var1x);
                  if (var1x != 0L) {
                     Win32ShellFolder2.this.parent = new Win32ShellFolder2((Win32ShellFolder2)Win32ShellFolder2.this.parent, var3);
                  } else {
                     Win32ShellFolder2.this.disposer.relativePIDL = var3;
                  }
               }
            }

            return null;
         }
      }, InterruptedException.class);
      Disposer.addRecord(this, this.disposer);
   }

   Win32ShellFolder2(Win32ShellFolder2 var1, long var2, long var4, String var6) {
      super(var1, var6 != null ? var6 : "ShellFolder: ");
      this.disposer.pIShellFolder = var2;
      this.disposer.relativePIDL = var4;
      Disposer.addRecord(this, this.disposer);
   }

   Win32ShellFolder2(final Win32ShellFolder2 var1, final long var2) throws InterruptedException {
      super(var1, (String)invoke(new Callable<String>() {
         public String call() {
            return Win32ShellFolder2.getFileSystemPath(var1.getIShellFolder(), var2);
         }
      }, RuntimeException.class));
      this.disposer.relativePIDL = var2;
      Disposer.addRecord(this, this.disposer);
   }

   private native void initDesktop();

   private native void initSpecial(long var1, int var3);

   public void setIsPersonal() {
      this.isPersonal = true;
   }

   protected Object writeReplace() throws ObjectStreamException {
      return invoke(new Callable<File>() {
         public File call() {
            if (Win32ShellFolder2.this.isFileSystem()) {
               return new File(Win32ShellFolder2.this.getPath());
            } else {
               Win32ShellFolder2 var1 = com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.getDrives();
               if (var1 != null) {
                  File[] var2 = var1.listFiles();
                  if (var2 != null) {
                     for(int var3 = 0; var3 < var2.length; ++var3) {
                        if (var2[var3] instanceof Win32ShellFolder2) {
                           Win32ShellFolder2 var4 = (Win32ShellFolder2)var2[var3];
                           if (var4.isFileSystem() && !var4.hasAttribute(33554432)) {
                              return new File(var4.getPath());
                           }
                        }
                     }
                  }
               }

               return new File("C:\\");
            }
         }
      });
   }

   protected void dispose() {
      this.disposer.dispose();
   }

   static native long getNextPIDLEntry(long var0);

   static native long copyFirstPIDLEntry(long var0);

   private static native long combinePIDLs(long var0, long var2);

   static native void releasePIDL(long var0);

   private static native void releaseIShellFolder(long var0);

   private long getIShellFolder() {
      if (this.disposer.pIShellFolder == 0L) {
         try {
            this.disposer.pIShellFolder = (Long)invoke(new Callable<Long>() {
               public Long call() {
                  assert Win32ShellFolder2.this.isDirectory();

                  assert Win32ShellFolder2.this.parent != null;

                  long var1 = Win32ShellFolder2.this.getParentIShellFolder();
                  if (var1 == 0L) {
                     throw new InternalError("Parent IShellFolder was null for " + Win32ShellFolder2.this.getAbsolutePath());
                  } else {
                     long var3 = Win32ShellFolder2.bindToObject(var1, Win32ShellFolder2.this.disposer.relativePIDL);
                     if (var3 == 0L) {
                        throw new InternalError("Unable to bind " + Win32ShellFolder2.this.getAbsolutePath() + " to parent");
                     } else {
                        return var3;
                     }
                  }
               }
            }, RuntimeException.class);
         } catch (InterruptedException var2) {
         }
      }

      return this.disposer.pIShellFolder;
   }

   public long getParentIShellFolder() {
      Win32ShellFolder2 var1 = (Win32ShellFolder2)this.getParentFile();
      return var1 == null ? this.getIShellFolder() : var1.getIShellFolder();
   }

   public long getRelativePIDL() {
      if (this.disposer.relativePIDL == 0L) {
         throw new InternalError("Should always have a relative PIDL");
      } else {
         return this.disposer.relativePIDL;
      }
   }

   private long getAbsolutePIDL() {
      if (this.parent == null) {
         return this.getRelativePIDL();
      } else {
         if (this.disposer.absolutePIDL == 0L) {
            this.disposer.absolutePIDL = combinePIDLs(((Win32ShellFolder2)this.parent).getAbsolutePIDL(), this.getRelativePIDL());
         }

         return this.disposer.absolutePIDL;
      }
   }

   public Win32ShellFolder2 getDesktop() {
      return com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.getDesktop();
   }

   public long getDesktopIShellFolder() {
      return this.getDesktop().getIShellFolder();
   }

   private static boolean pathsEqual(String var0, String var1) {
      return var0.equalsIgnoreCase(var1);
   }

   public boolean equals(Object var1) {
      if (var1 != null && var1 instanceof Win32ShellFolder2) {
         Win32ShellFolder2 var2 = (Win32ShellFolder2)var1;
         if ((this.parent != null || var2.parent == null) && (this.parent == null || var2.parent != null)) {
            if (this.isFileSystem() && var2.isFileSystem()) {
               return pathsEqual(this.getPath(), var2.getPath()) && (this.parent == var2.parent || this.parent.equals(var2.parent));
            } else if (this.parent != var2.parent && !this.parent.equals(var2.parent)) {
               return false;
            } else {
               try {
                  return pidlsEqual(this.getParentIShellFolder(), this.disposer.relativePIDL, var2.disposer.relativePIDL);
               } catch (InterruptedException var4) {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return !(var1 instanceof File) ? super.equals(var1) : pathsEqual(this.getPath(), ((File)var1).getPath());
      }
   }

   private static boolean pidlsEqual(final long var0, final long var2, final long var4) throws InterruptedException {
      return (Boolean)invoke(new Callable<Boolean>() {
         public Boolean call() {
            return Win32ShellFolder2.compareIDs(var0, var2, var4) == 0;
         }
      }, RuntimeException.class);
   }

   private static native int compareIDs(long var0, long var2, long var4);

   public boolean isFileSystem() {
      if (this.cachedIsFileSystem == null) {
         this.cachedIsFileSystem = this.hasAttribute(1073741824);
      }

      return this.cachedIsFileSystem;
   }

   public boolean hasAttribute(final int var1) {
      Boolean var2 = (Boolean)invoke(new Callable<Boolean>() {
         public Boolean call() {
            return (Win32ShellFolder2.getAttributes0(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), var1) & var1) != 0;
         }
      });
      return var2 != null && var2;
   }

   private static native int getAttributes0(long var0, long var2, int var4);

   private static String getFileSystemPath(long var0, long var2) {
      int var4 = 536936448;
      if (var0 == com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.getNetwork().getIShellFolder() && getAttributes0(var0, var2, var4) == var4) {
         String var5 = getFileSystemPath(com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.getDesktop().getIShellFolder(), getLinkLocation(var0, var2, false));
         if (var5 != null && var5.startsWith("\\\\")) {
            return var5;
         }
      }

      return getDisplayNameOf(var0, var2, 32768);
   }

   static String getFileSystemPath(final int var0) throws IOException, InterruptedException {
      String var1 = (String)invoke(new Callable<String>() {
         public String call() throws IOException {
            return Win32ShellFolder2.getFileSystemPath0(var0);
         }
      }, IOException.class);
      if (var1 != null) {
         SecurityManager var2 = System.getSecurityManager();
         if (var2 != null) {
            var2.checkRead(var1);
         }
      }

      return var1;
   }

   private static native String getFileSystemPath0(int var0) throws IOException;

   private static boolean isNetworkRoot(String var0) {
      return var0.equals("\\\\") || var0.equals("\\") || var0.equals("//") || var0.equals("/");
   }

   public File getParentFile() {
      return this.parent;
   }

   public boolean isDirectory() {
      if (this.isDir == null) {
         if (this.hasAttribute(536870912) && !this.hasAttribute(134217728)) {
            this.isDir = Boolean.TRUE;
         } else if (this.isLink()) {
            com.frojasg1.sun.awt.shell.ShellFolder var1 = this.getLinkLocation(false);
            this.isDir = var1 != null && var1.isDirectory();
         } else {
            this.isDir = Boolean.FALSE;
         }
      }

      return this.isDir;
   }

   private long getEnumObjects(final boolean var1) throws InterruptedException {
      return (Long)invoke(new Callable<Long>() {
         public Long call() {
            boolean var1x = Win32ShellFolder2.this.disposer.pIShellFolder == Win32ShellFolder2.this.getDesktopIShellFolder();
            return Win32ShellFolder2.this.getEnumObjects(Win32ShellFolder2.this.disposer.pIShellFolder, var1x, var1);
         }
      }, RuntimeException.class);
   }

   private native long getEnumObjects(long var1, boolean var3, boolean var4);

   private native long getNextChild(long var1);

   private native void releaseEnumObjects(long var1);

   private static native long bindToObject(long var0, long var2);

   public File[] listFiles(final boolean var1) {
      SecurityManager var2 = System.getSecurityManager();
      if (var2 != null) {
         var2.checkRead(this.getPath());
      }

      try {
         File[] var3 = (File[])invoke(new Callable<File[]>() {
            public File[] call() throws InterruptedException {
               if (!Win32ShellFolder2.this.isDirectory()) {
                  return null;
               } else if (Win32ShellFolder2.this.isLink() && !Win32ShellFolder2.this.hasAttribute(536870912)) {
                  return new File[0];
               } else {
                  Win32ShellFolder2 var1x = com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.getDesktop();
                  Win32ShellFolder2 var2 = com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.getPersonal();
                  long var3 = Win32ShellFolder2.this.getIShellFolder();
                  ArrayList var5 = new ArrayList();
                  long var6 = Win32ShellFolder2.this.getEnumObjects(var1);
                  if (var6 != 0L) {
                     try {
                        int var10 = 1342177280;

                        long var8;
                        do {
                           var8 = Win32ShellFolder2.this.getNextChild(var6);
                           boolean var11 = true;
                           if (var8 != 0L && (Win32ShellFolder2.getAttributes0(var3, var8, var10) & var10) != 0) {
                              Win32ShellFolder2 var12;
                              if (Win32ShellFolder2.this.equals(var1x) && var2 != null && Win32ShellFolder2.pidlsEqual(var3, var8, var2.disposer.relativePIDL)) {
                                 var12 = var2;
                              } else {
                                 var12 = new Win32ShellFolder2(Win32ShellFolder2.this, var8);
                                 var11 = false;
                              }

                              var5.add(var12);
                           }

                           if (var11) {
                              Win32ShellFolder2.releasePIDL(var8);
                           }
                        } while(var8 != 0L && !Thread.currentThread().isInterrupted());
                     } finally {
                        Win32ShellFolder2.this.releaseEnumObjects(var6);
                     }
                  }

                  return Thread.currentThread().isInterrupted() ? new File[0] : (File[])var5.toArray(new com.frojasg1.sun.awt.shell.ShellFolder[var5.size()]);
               }
            }
         }, InterruptedException.class);
         return com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.checkFiles(var3);
      } catch (InterruptedException var4) {
         return new File[0];
      }
   }

   Win32ShellFolder2 getChildByPath(final String var1) throws InterruptedException {
      return (Win32ShellFolder2)invoke(new Callable<Win32ShellFolder2>() {
         public Win32ShellFolder2 call() throws InterruptedException {
            long var1x = Win32ShellFolder2.this.getIShellFolder();
            long var3 = Win32ShellFolder2.this.getEnumObjects(true);

            Win32ShellFolder2 var5;
            long var6;
            for(var5 = null; (var6 = Win32ShellFolder2.this.getNextChild(var3)) != 0L; Win32ShellFolder2.releasePIDL(var6)) {
               if (Win32ShellFolder2.getAttributes0(var1x, var6, 1073741824) != 0) {
                  String var8 = Win32ShellFolder2.getFileSystemPath(var1x, var6);
                  if (var8 != null && var8.equalsIgnoreCase(var1)) {
                     long var9 = Win32ShellFolder2.bindToObject(var1x, var6);
                     var5 = new Win32ShellFolder2(Win32ShellFolder2.this, var9, var6, var8);
                     break;
                  }
               }
            }

            Win32ShellFolder2.this.releaseEnumObjects(var3);
            return var5;
         }
      }, InterruptedException.class);
   }

   public boolean isLink() {
      if (this.cachedIsLink == null) {
         this.cachedIsLink = this.hasAttribute(65536);
      }

      return this.cachedIsLink;
   }

   public boolean isHidden() {
      return this.hasAttribute(524288);
   }

   private static native long getLinkLocation(long var0, long var2, boolean var4);

   public com.frojasg1.sun.awt.shell.ShellFolder getLinkLocation() {
      return this.getLinkLocation(true);
   }

   private com.frojasg1.sun.awt.shell.ShellFolder getLinkLocation(final boolean var1) {
      return (com.frojasg1.sun.awt.shell.ShellFolder)invoke(new Callable<com.frojasg1.sun.awt.shell.ShellFolder>() {
         public com.frojasg1.sun.awt.shell.ShellFolder call() {
            if (!Win32ShellFolder2.this.isLink()) {
               return null;
            } else {
               Win32ShellFolder2 var1x = null;
               long var2 = Win32ShellFolder2.getLinkLocation(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), var1);
               if (var2 != 0L) {
                  try {
                     var1x = com.frojasg1.sun.awt.shell.Win32ShellFolderManager2.createShellFolderFromRelativePIDL(Win32ShellFolder2.this.getDesktop(), var2);
                  } catch (InterruptedException var5) {
                  } catch (InternalError var6) {
                  }
               }

               return var1x;
            }
         }
      });
   }

   long parseDisplayName(final String var1) throws IOException, InterruptedException {
      return (Long)invoke(new Callable<Long>() {
         public Long call() throws IOException {
            return Win32ShellFolder2.parseDisplayName0(Win32ShellFolder2.this.getIShellFolder(), var1);
         }
      }, IOException.class);
   }

   private static native long parseDisplayName0(long var0, String var2) throws IOException;

   private static native String getDisplayNameOf(long var0, long var2, int var4);

   public String getDisplayName() {
      if (this.displayName == null) {
         this.displayName = (String)invoke(new Callable<String>() {
            public String call() {
               return Win32ShellFolder2.getDisplayNameOf(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), 0);
            }
         });
      }

      return this.displayName;
   }

   private static native String getFolderType(long var0);

   public String getFolderType() {
      if (this.folderType == null) {
         final long var1 = this.getAbsolutePIDL();
         this.folderType = (String)invoke(new Callable<String>() {
            public String call() {
               return Win32ShellFolder2.getFolderType(var1);
            }
         });
      }

      return this.folderType;
   }

   private native String getExecutableType(String var1);

   public String getExecutableType() {
      return !this.isFileSystem() ? null : this.getExecutableType(this.getAbsolutePath());
   }

   private static native long getIShellIcon(long var0);

   private static native int getIconIndex(long var0, long var2);

   private static native long getIcon(String var0, boolean var1);

   private static native long extractIcon(long var0, long var2, boolean var4);

   private static native long getSystemIcon(int var0);

   private static native long getIconResource(String var0, int var1, int var2, int var3, boolean var4);

   private static native int[] getIconBits(long var0, int var2);

   private static native void disposeIcon(long var0);

   static native int[] getStandardViewButton0(int var0);

   private long getIShellIcon() {
      if (this.pIShellIcon == -1L) {
         this.pIShellIcon = getIShellIcon(this.getIShellFolder());
      }

      return this.pIShellIcon;
   }

   private static Image makeIcon(long var0, boolean var2) {
      if (var0 != 0L && var0 != -1L) {
         int var3 = var2 ? 32 : 16;
         int[] var4 = getIconBits(var0, var3);
         if (var4 != null) {
            BufferedImage var5 = new BufferedImage(var3, var3, 2);
            var5.setRGB(0, 0, var3, var3, var4, 0, var3);
            return var5;
         }
      }

      return null;
   }

   public Image getIcon(final boolean var1) {
      Image var2 = var1 ? this.largeIcon : this.smallIcon;
      if (var2 == null) {
         var2 = (Image)invoke(new Callable<Image>() {
            public Image call() {
               Image var1x = null;
               long var2;
               if (Win32ShellFolder2.this.isFileSystem()) {
                  var2 = Win32ShellFolder2.this.parent != null ? ((Win32ShellFolder2)Win32ShellFolder2.this.parent).getIShellIcon() : 0L;
                  long var4 = Win32ShellFolder2.this.getRelativePIDL();
                  int var6 = Win32ShellFolder2.getIconIndex(var2, var4);
                  if (var6 > 0) {
                     Map var7;
                     if (Win32ShellFolder2.this.isLink()) {
                        var7 = var1 ? Win32ShellFolder2.largeLinkedSystemImages : Win32ShellFolder2.smallLinkedSystemImages;
                     } else {
                        var7 = var1 ? Win32ShellFolder2.largeSystemImages : Win32ShellFolder2.smallSystemImages;
                     }

                     var1x = (Image)var7.get(var6);
                     if (var1x == null) {
                        long var8 = Win32ShellFolder2.getIcon(Win32ShellFolder2.this.getAbsolutePath(), var1);
                        var1x = Win32ShellFolder2.makeIcon(var8, var1);
                        Win32ShellFolder2.disposeIcon(var8);
                        if (var1x != null) {
                           var7.put(var6, var1x);
                        }
                     }
                  }
               }

               if (var1x == null) {
                  var2 = Win32ShellFolder2.extractIcon(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), var1);
                  var1x = Win32ShellFolder2.makeIcon(var2, var1);
                  Win32ShellFolder2.disposeIcon(var2);
               }

               if (var1x == null) {
                  var1x = Win32ShellFolder2.super.getIcon(var1);
               }

               return var1x;
            }
         });
         if (var1) {
            this.largeIcon = var2;
         } else {
            this.smallIcon = var2;
         }
      }

      return var2;
   }

   static Image getSystemIcon(Win32ShellFolder2.SystemIcon var0) {
      long var1 = getSystemIcon(var0.getIconID());
      Image var3 = makeIcon(var1, true);
      disposeIcon(var1);
      return var3;
   }

   static Image getShell32Icon(int var0, boolean var1) {
      boolean var2 = true;
      int var3 = var1 ? 32 : 16;
      Toolkit var4 = Toolkit.getDefaultToolkit();
      String var5 = (String)var4.getDesktopProperty("win.icon.shellIconBPP");
      if (var5 != null) {
         var2 = var5.equals("4");
      }

      long var6 = getIconResource("shell32.dll", var0, var3, var3, var2);
      if (var6 != 0L) {
         Image var8 = makeIcon(var6, var1);
         disposeIcon(var6);
         return var8;
      } else {
         return null;
      }
   }

   public File getCanonicalFile() throws IOException {
      return this;
   }

   public boolean isSpecial() {
      return this.isPersonal || !this.isFileSystem() || this == this.getDesktop();
   }

   public int compareTo(File var1) {
      if (!(var1 instanceof Win32ShellFolder2)) {
         return this.isFileSystem() && !this.isSpecial() ? super.compareTo(var1) : -1;
      } else {
         return Win32ShellFolderManager2.compareShellFolders(this, (Win32ShellFolder2)var1);
      }
   }

   public com.frojasg1.sun.awt.shell.ShellFolderColumnInfo[] getFolderColumns() {
      return (com.frojasg1.sun.awt.shell.ShellFolderColumnInfo[])invoke(new Callable<com.frojasg1.sun.awt.shell.ShellFolderColumnInfo[]>() {
         public com.frojasg1.sun.awt.shell.ShellFolderColumnInfo[] call() {
            com.frojasg1.sun.awt.shell.ShellFolderColumnInfo[] var1 = Win32ShellFolder2.this.doGetColumnInfo(Win32ShellFolder2.this.getIShellFolder());
            if (var1 != null) {
               ArrayList var2 = new ArrayList();

               for(int var3 = 0; var3 < var1.length; ++var3) {
                  com.frojasg1.sun.awt.shell.ShellFolderColumnInfo var4 = var1[var3];
                  if (var4 != null) {
                     var4.setAlignment(var4.getAlignment() == 1 ? 4 : (var4.getAlignment() == 2 ? 0 : 10));
                     var4.setComparator(new Win32ShellFolder2.ColumnComparator(Win32ShellFolder2.this, var3));
                     var2.add(var4);
                  }
               }

               var1 = new com.frojasg1.sun.awt.shell.ShellFolderColumnInfo[var2.size()];
               var2.toArray(var1);
            }

            return var1;
         }
      });
   }

   public Object getFolderColumnValue(final int var1) {
      return invoke(new Callable<Object>() {
         public Object call() {
            return Win32ShellFolder2.this.doGetColumnValue(Win32ShellFolder2.this.getParentIShellFolder(), Win32ShellFolder2.this.getRelativePIDL(), var1);
         }
      });
   }

   private native ShellFolderColumnInfo[] doGetColumnInfo(long var1);

   private native Object doGetColumnValue(long var1, long var3, int var5);

   private static native int compareIDsByColumn(long var0, long var2, long var4, int var6);

   public void sortChildren(final List<? extends File> var1) {
      invoke(new Callable<Void>() {
         public Void call() {
            Collections.sort(var1, new Win32ShellFolder2.ColumnComparator(Win32ShellFolder2.this, 0));
            return null;
         }
      });
   }

   static {
      initIDs();
      smallSystemImages = new HashMap();
      largeSystemImages = new HashMap();
      smallLinkedSystemImages = new HashMap();
      largeLinkedSystemImages = new HashMap();
   }

   private static class ColumnComparator implements Comparator<File> {
      private final Win32ShellFolder2 shellFolder;
      private final int columnIdx;

      public ColumnComparator(Win32ShellFolder2 var1, int var2) {
         this.shellFolder = var1;
         this.columnIdx = var2;
      }

      public int compare(final File var1, final File var2) {
         Integer var3 = (Integer) com.frojasg1.sun.awt.shell.ShellFolder.invoke(new Callable<Integer>() {
            public Integer call() {
               return var1 instanceof Win32ShellFolder2 && var2 instanceof Win32ShellFolder2 ? Win32ShellFolder2.compareIDsByColumn(ColumnComparator.this.shellFolder.getIShellFolder(), ((Win32ShellFolder2)var1).getRelativePIDL(), ((Win32ShellFolder2)var2).getRelativePIDL(), ColumnComparator.this.columnIdx) : 0;
            }
         });
         return var3 == null ? 0 : var3;
      }
   }

   static class FolderDisposer implements DisposerRecord {
      long absolutePIDL;
      long pIShellFolder;
      long relativePIDL;
      boolean disposed;

      FolderDisposer() {
      }

      public void dispose() {
         if (!this.disposed) {
            ShellFolder.invoke(new Callable<Void>() {
               public Void call() {
                  if (FolderDisposer.this.relativePIDL != 0L) {
                     Win32ShellFolder2.releasePIDL(FolderDisposer.this.relativePIDL);
                  }

                  if (FolderDisposer.this.absolutePIDL != 0L) {
                     Win32ShellFolder2.releasePIDL(FolderDisposer.this.absolutePIDL);
                  }

                  if (FolderDisposer.this.pIShellFolder != 0L) {
                     Win32ShellFolder2.releaseIShellFolder(FolderDisposer.this.pIShellFolder);
                  }

                  return null;
               }
            });
            this.disposed = true;
         }
      }
   }

   public static enum SystemIcon {
      IDI_APPLICATION(32512),
      IDI_HAND(32513),
      IDI_ERROR(32513),
      IDI_QUESTION(32514),
      IDI_EXCLAMATION(32515),
      IDI_WARNING(32515),
      IDI_ASTERISK(32516),
      IDI_INFORMATION(32516),
      IDI_WINLOGO(32517);

      private final int iconID;

      private SystemIcon(int var3) {
         this.iconID = var3;
      }

      public int getIconID() {
         return this.iconID;
      }
   }
}
