package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Window;
import java.awt.dnd.DropTarget;
import java.awt.image.BufferedImage;
import java.awt.peer.ComponentPeer;
import java.awt.peer.FileDialogPeer;
import java.io.File;
import java.io.FilenameFilter;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Vector;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.CausedFocusEvent;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.pipe.Region;

//final class WFileDialogPeer extends com.frojasg1.sun.awt.windows.WWindowPeer implements FileDialogPeer {
abstract class WFileDialogPeer extends com.frojasg1.sun.awt.windows.WWindowPeer implements FileDialogPeer {
   private com.frojasg1.sun.awt.windows.WComponentPeer parent;
   private FilenameFilter fileFilter;
   private Vector<com.frojasg1.sun.awt.windows.WWindowPeer> blockedWindows = new Vector();

   private static native void setFilterString(String var0);

   public void setFilenameFilter(FilenameFilter var1) {
      this.fileFilter = var1;
   }

   boolean checkFilenameFilter(String var1) {
      FileDialog var2 = (FileDialog)this.target;
      if (this.fileFilter == null) {
         return true;
      } else {
         File var3 = new File(var1);
         return this.fileFilter.accept(new File(var3.getParent()), var3.getName());
      }
   }

   WFileDialogPeer(FileDialog var1) {
      super(var1);
   }

   void create(WComponentPeer var1) {
      this.parent = var1;
   }

   protected void checkCreation() {
   }

   void initialize() {
      this.setFilenameFilter(((FileDialog)this.target).getFilenameFilter());
   }

   private native void _dispose();

   protected void disposeImpl() {
      com.frojasg1.sun.awt.windows.WToolkit.targetDisposedPeer(this.target, this);
      this._dispose();
   }

   private native void _show();

   private native void _hide();

   public void show() {
      (new Thread(new Runnable() {
         public void run() {
            WFileDialogPeer.this._show();
         }
      })).start();
   }

   void hide() {
      this._hide();
   }

   void setHWnd(long var1) {
      if (this.hwnd != var1) {
         this.hwnd = var1;
         Iterator var3 = this.blockedWindows.iterator();

         while(var3.hasNext()) {
            com.frojasg1.sun.awt.windows.WWindowPeer var4 = (com.frojasg1.sun.awt.windows.WWindowPeer)var3.next();
            if (var1 != 0L) {
               var4.modalDisable((Dialog)this.target, var1);
            } else {
               var4.modalEnable((Dialog)this.target);
            }
         }

      }
   }

   void handleSelected(char[] var1) {
      String[] var2 = (new String(var1)).split("\u0000");
      boolean var3 = var2.length > 1;
      String var4 = null;
      String var5 = null;
      File[] var6 = null;
      int var7;
      if (var3) {
         var4 = var2[0];
         var7 = var2.length - 1;
         var6 = new File[var7];

         for(int var8 = 0; var8 < var7; ++var8) {
            var6[var8] = new File(var4, var2[var8 + 1]);
         }

         var5 = var2[1];
      } else {
         var7 = var2[0].lastIndexOf(File.separatorChar);
         if (var7 == -1) {
            var4 = "." + File.separator;
            var5 = var2[0];
         } else {
            var4 = var2[0].substring(0, var7 + 1);
            var5 = var2[0].substring(var7 + 1);
         }

         var6 = new File[]{new File(var4, var5)};
      }

      final FileDialog var9 = (FileDialog)this.target;
      AWTAccessor.FileDialogAccessor var10 = AWTAccessor.getFileDialogAccessor();
      var10.setDirectory(var9, var4);
      var10.setFile(var9, var5);
      var10.setFiles(var9, var6);
      com.frojasg1.sun.awt.windows.WToolkit.executeOnEventHandlerThread(var9, new Runnable() {
         public void run() {
            var9.setVisible(false);
         }
      });
   }

   void handleCancel() {
      final FileDialog var1 = (FileDialog)this.target;
      AWTAccessor.getFileDialogAccessor().setFile(var1, (String)null);
      AWTAccessor.getFileDialogAccessor().setFiles(var1, (File[])null);
      AWTAccessor.getFileDialogAccessor().setDirectory(var1, (String)null);
      WToolkit.executeOnEventHandlerThread(var1, new Runnable() {
         public void run() {
            var1.setVisible(false);
         }
      });
   }

   void blockWindow(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
      this.blockedWindows.add(var1);
      if (this.hwnd != 0L) {
         var1.modalDisable((Dialog)this.target, this.hwnd);
      }

   }

   void unblockWindow(com.frojasg1.sun.awt.windows.WWindowPeer var1) {
      this.blockedWindows.remove(var1);
      if (this.hwnd != 0L) {
         var1.modalEnable((Dialog)this.target);
      }

   }

   public void blockWindows(List<Window> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Window var3 = (Window)var2.next();
         com.frojasg1.sun.awt.windows.WWindowPeer var4 = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var3);
         if (var4 != null) {
            this.blockWindow(var4);
         }
      }

   }

   public native void toFront();

   public native void toBack();

   public void updateAlwaysOnTopState() {
   }

   public void setDirectory(String var1) {
   }

   public void setFile(String var1) {
   }

   public void setTitle(String var1) {
   }

   public void setResizable(boolean var1) {
   }

   void enable() {
   }

   void disable() {
   }

   public void reshape(int var1, int var2, int var3, int var4) {
   }

   public boolean handleEvent(Event var1) {
      return false;
   }

   public void setForeground(Color var1) {
   }

   public void setBackground(Color var1) {
   }

   public void setFont(Font var1) {
   }

   public void updateMinimumSize() {
   }

   public void updateIconImages() {
   }

   public boolean requestFocus(boolean var1, boolean var2) {
      return false;
   }

   public boolean requestFocus(Component var1, boolean var2, boolean var3, long var4, CausedFocusEvent.Cause var6) {
      return false;
   }

   void start() {
   }

   public void beginValidate() {
   }

   public void endValidate() {
   }

   void invalidate(int var1, int var2, int var3, int var4) {
   }

   public void addDropTarget(DropTarget var1) {
   }

   public void removeDropTarget(DropTarget var1) {
   }

   public void updateFocusableWindowState() {
   }

   public void setZOrder(ComponentPeer var1) {
   }

   private static native void initIDs();

   public void applyShape(Region var1) {
   }

   public void setOpacity(float var1) {
   }

   public void setOpaque(boolean var1) {
   }

   public void updateWindow(BufferedImage var1) {
   }

   public void createScreenSurface(boolean var1) {
   }

   public void replaceSurfaceData() {
   }

   public boolean isMultipleMode() {
      FileDialog var1 = (FileDialog)this.target;
      return AWTAccessor.getFileDialogAccessor().isMultipleMode(var1);
   }

   static {
      initIDs();
      String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            try {
               ResourceBundle var1 = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
               return var1.getString("allFiles");
            } catch (MissingResourceException var2) {
               return "All Files";
            }
         }
      });
      setFilterString(var0);
   }
}
