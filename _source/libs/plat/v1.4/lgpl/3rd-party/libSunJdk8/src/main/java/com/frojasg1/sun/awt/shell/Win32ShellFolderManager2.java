package com.frojasg1.sun.awt.shell;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import com.frojasg1.sun.awt.OSInfo;
import com.frojasg1.sun.awt.shell.ShellFolder;
import com.frojasg1.sun.awt.shell.ShellFolderManager;
import com.frojasg1.sun.awt.shell.Win32ShellFolder2;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.misc.ThreadGroupUtils;

public class Win32ShellFolderManager2 extends com.frojasg1.sun.awt.shell.ShellFolderManager {
   private static final int VIEW_LIST = 2;
   private static final int VIEW_DETAILS = 3;
   private static final int VIEW_PARENTFOLDER = 8;
   private static final int VIEW_NEWFOLDER = 11;
   private static final Image[] STANDARD_VIEW_BUTTONS;
   private static com.frojasg1.sun.awt.shell.Win32ShellFolder2 desktop;
   private static com.frojasg1.sun.awt.shell.Win32ShellFolder2 drives;
   private static com.frojasg1.sun.awt.shell.Win32ShellFolder2 recent;
   private static com.frojasg1.sun.awt.shell.Win32ShellFolder2 network;
   private static com.frojasg1.sun.awt.shell.Win32ShellFolder2 personal;
   private static File[] roots;
   private static List topFolderList;

   public Win32ShellFolderManager2() {
   }

   public com.frojasg1.sun.awt.shell.ShellFolder createShellFolder(File var1) throws FileNotFoundException {
      try {
         return createShellFolder(getDesktop(), var1);
      } catch (InterruptedException var3) {
         throw new FileNotFoundException("Execution was interrupted");
      }
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 createShellFolder(com.frojasg1.sun.awt.shell.Win32ShellFolder2 var0, File var1) throws FileNotFoundException, InterruptedException {
      long var2;
      try {
         var2 = var0.parseDisplayName(var1.getCanonicalPath());
      } catch (IOException var9) {
         var2 = 0L;
      }

      if (var2 == 0L) {
         throw new FileNotFoundException("File " + var1.getAbsolutePath() + " not found");
      } else {
         com.frojasg1.sun.awt.shell.Win32ShellFolder2 var4;
         try {
            var4 = createShellFolderFromRelativePIDL(var0, var2);
         } finally {
            com.frojasg1.sun.awt.shell.Win32ShellFolder2.releasePIDL(var2);
         }

         return var4;
      }
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 createShellFolderFromRelativePIDL(com.frojasg1.sun.awt.shell.Win32ShellFolder2 var0, long var1) throws InterruptedException {
      while(true) {
         if (var1 != 0L) {
            long var3 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.copyFirstPIDLEntry(var1);
            if (var3 != 0L) {
               var0 = new com.frojasg1.sun.awt.shell.Win32ShellFolder2(var0, var3);
               var1 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.getNextPIDLEntry(var1);
               continue;
            }
         }

         return var0;
      }
   }

   private static Image getStandardViewButton(int var0) {
      Image var1 = STANDARD_VIEW_BUTTONS[var0];
      if (var1 != null) {
         return var1;
      } else {
         BufferedImage var2 = new BufferedImage(16, 16, 2);
         var2.setRGB(0, 0, 16, 16, com.frojasg1.sun.awt.shell.Win32ShellFolder2.getStandardViewButton0(var0), 0, 16);
         STANDARD_VIEW_BUTTONS[var0] = var2;
         return var2;
      }
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 getDesktop() {
      if (desktop == null) {
         try {
            desktop = new com.frojasg1.sun.awt.shell.Win32ShellFolder2(0);
         } catch (SecurityException var1) {
         } catch (IOException var2) {
         } catch (InterruptedException var3) {
         }
      }

      return desktop;
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 getDrives() {
      if (drives == null) {
         try {
            drives = new com.frojasg1.sun.awt.shell.Win32ShellFolder2(17);
         } catch (SecurityException var1) {
         } catch (IOException var2) {
         } catch (InterruptedException var3) {
         }
      }

      return drives;
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 getRecent() {
      if (recent == null) {
         try {
            String var0 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.getFileSystemPath(8);
            if (var0 != null) {
               recent = createShellFolder(getDesktop(), new File(var0));
            }
         } catch (SecurityException var1) {
         } catch (InterruptedException var2) {
         } catch (IOException var3) {
         }
      }

      return recent;
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 getNetwork() {
      if (network == null) {
         try {
            network = new com.frojasg1.sun.awt.shell.Win32ShellFolder2(18);
         } catch (SecurityException var1) {
         } catch (IOException var2) {
         } catch (InterruptedException var3) {
         }
      }

      return network;
   }

   static com.frojasg1.sun.awt.shell.Win32ShellFolder2 getPersonal() {
      if (personal == null) {
         try {
            String var0 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.getFileSystemPath(5);
            if (var0 != null) {
               com.frojasg1.sun.awt.shell.Win32ShellFolder2 var1 = getDesktop();
               personal = var1.getChildByPath(var0);
               if (personal == null) {
                  personal = createShellFolder(getDesktop(), new File(var0));
               }

               if (personal != null) {
                  personal.setIsPersonal();
               }
            }
         } catch (SecurityException var2) {
         } catch (InterruptedException var3) {
         } catch (IOException var4) {
         }
      }

      return personal;
   }

   public Object get(String var1) {
      com.frojasg1.sun.awt.shell.Win32ShellFolder2 var20;
      if (var1.equals("fileChooserDefaultFolder")) {
         var20 = getPersonal();
         if (var20 == null) {
            var20 = getDesktop();
         }

         return checkFile(var20);
      } else if (var1.equals("roots")) {
         if (roots == null) {
            var20 = getDesktop();
            if (var20 != null) {
               roots = new File[]{var20};
            } else {
               roots = (File[])((File[])super.get(var1));
            }
         }

         return checkFiles(roots);
      } else {
         File[] var6;
         int var8;
         ArrayList var22;
         if (var1.equals("fileChooserComboBoxFolders")) {
            var20 = getDesktop();
            if (var20 != null && checkFile(var20) != null) {
               var22 = new ArrayList();
               com.frojasg1.sun.awt.shell.Win32ShellFolder2 var21 = getDrives();
               com.frojasg1.sun.awt.shell.Win32ShellFolder2 var23 = getRecent();
               if (var23 != null && OSInfo.getWindowsVersion().compareTo(OSInfo.WINDOWS_2000) >= 0) {
                  var22.add(var23);
               }

               var22.add(var20);
               var6 = checkFiles(var20.listFiles());
               Arrays.sort(var6);
               File[] var24 = var6;
               var8 = var6.length;

               for(int var25 = 0; var25 < var8; ++var25) {
                  File var10 = var24[var25];
                  com.frojasg1.sun.awt.shell.Win32ShellFolder2 var11 = (com.frojasg1.sun.awt.shell.Win32ShellFolder2)var10;
                  if (!var11.isFileSystem() || var11.isDirectory() && !var11.isLink()) {
                     var22.add(var11);
                     if (var11.equals(var21)) {
                        File[] var12 = checkFiles(var11.listFiles());
                        if (var12 != null && var12.length > 0) {
                           List var13 = Arrays.asList(var12);
                           var11.sortChildren(var13);
                           var22.addAll(var13);
                        }
                     }
                  }
               }

               return checkFiles((List)var22);
            } else {
               return super.get(var1);
            }
         } else if (var1.equals("fileChooserShortcutPanelFolders")) {
            Toolkit var19 = Toolkit.getDefaultToolkit();
            var22 = new ArrayList();
            int var4 = 0;

            Object var5;
            do {
               var5 = var19.getDesktopProperty("win.comdlg.placesBarPlace" + var4++);

               try {
                  if (var5 instanceof Integer) {
                     var22.add(new com.frojasg1.sun.awt.shell.Win32ShellFolder2((Integer)var5));
                  } else if (var5 instanceof String) {
                     var22.add(this.createShellFolder(new File((String)var5)));
                  }
               } catch (IOException var14) {
               } catch (InterruptedException var15) {
                  return new File[0];
               }
            } while(var5 != null);

            if (var22.size() == 0) {
               var6 = new File[]{getRecent(), getDesktop(), getPersonal(), getDrives(), getNetwork()};
               int var7 = var6.length;

               for(var8 = 0; var8 < var7; ++var8) {
                  File var9 = var6[var8];
                  if (var9 != null) {
                     var22.add(var9);
                  }
               }
            }

            return checkFiles((List)var22);
         } else {
            String var2;
            if (var1.startsWith("fileChooserIcon ")) {
               var2 = var1.substring(var1.indexOf(" ") + 1);
               byte var18;
               if (!var2.equals("ListView") && !var2.equals("ViewMenu")) {
                  if (var2.equals("DetailsView")) {
                     var18 = 3;
                  } else if (var2.equals("UpFolder")) {
                     var18 = 8;
                  } else {
                     if (!var2.equals("NewFolder")) {
                        return null;
                     }

                     var18 = 11;
                  }
               } else {
                  var18 = 2;
               }

               return getStandardViewButton(var18);
            } else if (var1.startsWith("optionPaneIcon ")) {
               com.frojasg1.sun.awt.shell.Win32ShellFolder2.SystemIcon var17;
               if (var1 == "optionPaneIcon Error") {
                  var17 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.SystemIcon.IDI_ERROR;
               } else if (var1 == "optionPaneIcon Information") {
                  var17 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.SystemIcon.IDI_INFORMATION;
               } else if (var1 == "optionPaneIcon Question") {
                  var17 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.SystemIcon.IDI_QUESTION;
               } else {
                  if (var1 != "optionPaneIcon Warning") {
                     return null;
                  }

                  var17 = com.frojasg1.sun.awt.shell.Win32ShellFolder2.SystemIcon.IDI_EXCLAMATION;
               }

               return com.frojasg1.sun.awt.shell.Win32ShellFolder2.getSystemIcon(var17);
            } else {
               if (var1.startsWith("shell32Icon ") || var1.startsWith("shell32LargeIcon ")) {
                  var2 = var1.substring(var1.indexOf(" ") + 1);

                  try {
                     int var3 = Integer.parseInt(var2);
                     if (var3 >= 0) {
                        return com.frojasg1.sun.awt.shell.Win32ShellFolder2.getShell32Icon(var3, var1.startsWith("shell32LargeIcon "));
                     }
                  } catch (NumberFormatException var16) {
                  }
               }

               return null;
            }
         }
      }
   }

   private static File checkFile(File var0) {
      SecurityManager var1 = System.getSecurityManager();
      return var1 != null && var0 != null ? checkFile(var0, var1) : var0;
   }

   private static File checkFile(File var0, SecurityManager var1) {
      try {
         var1.checkRead(var0.getPath());
         if (var0 instanceof com.frojasg1.sun.awt.shell.Win32ShellFolder2) {
            com.frojasg1.sun.awt.shell.Win32ShellFolder2 var2 = (com.frojasg1.sun.awt.shell.Win32ShellFolder2)var0;
            if (var2.isLink()) {
               com.frojasg1.sun.awt.shell.Win32ShellFolder2 var3 = (com.frojasg1.sun.awt.shell.Win32ShellFolder2)var2.getLinkLocation();
               if (var3 != null) {
                  var1.checkRead(var3.getPath());
               }
            }
         }

         return var0;
      } catch (SecurityException var4) {
         return null;
      }
   }

   static File[] checkFiles(File[] var0) {
      SecurityManager var1 = System.getSecurityManager();
      return var1 != null && var0 != null && var0.length != 0 ? checkFiles(Arrays.stream(var0), var1) : var0;
   }

   private static File[] checkFiles(List<File> var0) {
      SecurityManager var1 = System.getSecurityManager();
      return var1 != null && !var0.isEmpty() ? checkFiles(var0.stream(), var1) : (File[])var0.toArray(new File[var0.size()]);
   }

   private static File[] checkFiles(Stream<File> var0, SecurityManager var1) {
      return (File[])var0.filter((var1x) -> {
         return checkFile(var1x, var1) != null;
      }).toArray((var0x) -> {
         return new File[var0x];
      });
   }

   public boolean isComputerNode(final File var1) {
      if (var1 != null && var1 == getDrives()) {
         return true;
      } else {
         String var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return var1.getAbsolutePath();
            }
         });
         return var2.startsWith("\\\\") && var2.indexOf("\\", 2) < 0;
      }
   }

   public boolean isFileSystemRoot(File var1) {
      if (var1 != null) {
         com.frojasg1.sun.awt.shell.Win32ShellFolder2 var2 = getDrives();
         if (var1 instanceof com.frojasg1.sun.awt.shell.Win32ShellFolder2) {
            com.frojasg1.sun.awt.shell.Win32ShellFolder2 var3 = (com.frojasg1.sun.awt.shell.Win32ShellFolder2)var1;
            if (!var3.isFileSystem()) {
               return false;
            }

            if (var3.parent != null) {
               return var3.parent.equals(var2);
            }
         }

         String var5 = var1.getPath();
         if (var5.length() == 3 && var5.charAt(1) == ':') {
            File[] var4 = var2.listFiles();
            return var4 != null && Arrays.asList(var4).contains(var1);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   static int compareShellFolders(com.frojasg1.sun.awt.shell.Win32ShellFolder2 var0, com.frojasg1.sun.awt.shell.Win32ShellFolder2 var1) {
      boolean var2 = var0.isSpecial();
      boolean var3 = var1.isSpecial();
      if (var2 || var3) {
         if (topFolderList == null) {
            ArrayList var4 = new ArrayList();
            var4.add(getPersonal());
            var4.add(getDesktop());
            var4.add(getDrives());
            var4.add(getNetwork());
            topFolderList = var4;
         }

         int var6 = topFolderList.indexOf(var0);
         int var5 = topFolderList.indexOf(var1);
         if (var6 >= 0 && var5 >= 0) {
            return var6 - var5;
         }

         if (var6 >= 0) {
            return -1;
         }

         if (var5 >= 0) {
            return 1;
         }
      }

      if (var2 && !var3) {
         return -1;
      } else {
         return var3 && !var2 ? 1 : compareNames(var0.getAbsolutePath(), var1.getAbsolutePath());
      }
   }

   static int compareNames(String var0, String var1) {
      int var2 = var0.compareToIgnoreCase(var1);
      return var2 != 0 ? var2 : var0.compareTo(var1);
   }

   protected com.frojasg1.sun.awt.shell.ShellFolder.Invoker createInvoker() {
      return new Win32ShellFolderManager2.ComInvoker();
   }

   static native void initializeCom();

   static native void uninitializeCom();

   static {
      WToolkit.loadLibraries();
      STANDARD_VIEW_BUTTONS = new Image[12];
      topFolderList = null;
   }

   private static class ComInvoker extends ThreadPoolExecutor implements ThreadFactory, ShellFolder.Invoker {
      private static Thread comThread;

      private ComInvoker() {
         super(1, 1, 0L, TimeUnit.DAYS, new LinkedBlockingQueue());
         this.allowCoreThreadTimeOut(false);
         this.setThreadFactory(this);
         final Runnable var1 = new Runnable() {
            public void run() {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     ComInvoker.this.shutdownNow();
                     return null;
                  }
               });
            }
         };
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Runtime.getRuntime().addShutdownHook(new Thread(var1));
               return null;
            }
         });
      }

      public synchronized Thread newThread(final Runnable var1) {
         Runnable var2 = new Runnable() {
            public void run() {
               try {
                  Win32ShellFolderManager2.initializeCom();
                  var1.run();
               } finally {
                  Win32ShellFolderManager2.uninitializeCom();
               }

            }
         };
         comThread = (Thread)AccessController.doPrivileged((PrivilegedAction)() -> {
            ThreadGroup var3 = ThreadGroupUtils.getRootThreadGroup();
            Thread var2x = new Thread(var3, var2, "Swing-Shell");
            var2x.setDaemon(true);
            return var2x;
         });
         return comThread;
      }

      public <T> T invoke(Callable<T> var1) throws Exception {
         if (Thread.currentThread() == comThread) {
            return var1.call();
         } else {
            final Future var2;
            try {
               var2 = this.submit(var1);
            } catch (RejectedExecutionException var5) {
               throw new InterruptedException(var5.getMessage());
            }

            try {
               return (T) var2.get();
            } catch (InterruptedException var6) {
               AccessController.doPrivileged(new PrivilegedAction<Void>() {
                  public Void run() {
                     var2.cancel(true);
                     return null;
                  }
               });
               throw var6;
            } catch (ExecutionException var7) {
               Throwable var4 = var7.getCause();
               if (var4 instanceof Exception) {
                  throw (Exception)var4;
               } else if (var4 instanceof Error) {
                  throw (Error)var4;
               } else {
                  throw new RuntimeException("Unexpected error", var4);
               }
            }
         }
      }
   }
}
