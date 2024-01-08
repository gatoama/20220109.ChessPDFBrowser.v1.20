package com.frojasg1.sun.awt.windows;

import java.awt.AWTEvent;
import java.awt.Font;
import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionEvent;
import java.awt.peer.MenuItemPeer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.frojasg1.sun.awt.windows.WMenuBarPeer;
import com.frojasg1.sun.awt.windows.WMenuPeer;
import com.frojasg1.sun.awt.windows.WObjectPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.util.logging.PlatformLogger;

class WMenuItemPeer extends com.frojasg1.sun.awt.windows.WObjectPeer implements MenuItemPeer {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.WMenuItemPeer");
   String shortcutLabel;
   protected com.frojasg1.sun.awt.windows.WMenuPeer parent;
   private final boolean isCheckbox;
   private static Font defaultMenuFont;

   private synchronized native void _dispose();

   protected void disposeImpl() {
      com.frojasg1.sun.awt.windows.WToolkit.targetDisposedPeer(this.target, this);
      this._dispose();
   }

   public void setEnabled(boolean var1) {
      this.enable(var1);
   }

   public void enable() {
      this.enable(true);
   }

   public void disable() {
      this.enable(false);
   }

   private void readShortcutLabel() {
      com.frojasg1.sun.awt.windows.WMenuPeer var1;
      for(var1 = this.parent; var1 != null && !(var1 instanceof com.frojasg1.sun.awt.windows.WMenuBarPeer); var1 = var1.parent) {
      }

      if (var1 instanceof com.frojasg1.sun.awt.windows.WMenuBarPeer) {
         MenuShortcut var2 = ((MenuItem)this.target).getShortcut();
         this.shortcutLabel = var2 != null ? var2.toString() : null;
      } else {
         this.shortcutLabel = null;
      }

   }

   public void setLabel(String var1) {
      this.readShortcutLabel();
      this._setLabel(var1);
   }

   public native void _setLabel(String var1);

   protected WMenuItemPeer() {
      this.isCheckbox = false;
   }

   WMenuItemPeer(MenuItem var1) {
      this(var1, false);
   }

   WMenuItemPeer(MenuItem var1, boolean var2) {
      this.target = var1;
      this.parent = (com.frojasg1.sun.awt.windows.WMenuPeer) com.frojasg1.sun.awt.windows.WToolkit.targetToPeer(var1.getParent());
      this.isCheckbox = var2;
      this.parent.addChildPeer(this);
      this.create(this.parent);
      this.checkMenuCreation();
      this.readShortcutLabel();
   }

   void checkMenuCreation() {
      if (this.pData == 0L) {
         if (this.createError != null) {
            throw this.createError;
         } else {
            throw new InternalError("couldn't create menu peer");
         }
      }
   }

   void postEvent(AWTEvent var1) {
      com.frojasg1.sun.awt.windows.WToolkit.postEvent(com.frojasg1.sun.awt.windows.WToolkit.targetToAppContext(this.target), var1);
   }

   native void create(com.frojasg1.sun.awt.windows.WMenuPeer var1);

   native void enable(boolean var1);

   void handleAction(final long var1, final int var3) {
      WToolkit.executeOnEventHandlerThread(this.target, new Runnable() {
         public void run() {
            WMenuItemPeer.this.postEvent(new ActionEvent(WMenuItemPeer.this.target, 1001, ((MenuItem)WMenuItemPeer.this.target).getActionCommand(), var1, var3));
         }
      });
   }

   static Font getDefaultFont() {
      return defaultMenuFont;
   }

   private static native void initIDs();

   private native void _setFont(Font var1);

   public void setFont(Font var1) {
      this._setFont(var1);
   }

   static {
      initIDs();
      defaultMenuFont = (Font)AccessController.doPrivileged(new PrivilegedAction<Font>() {
         public Font run() {
            try {
               ResourceBundle var1 = ResourceBundle.getBundle("sun.awt.windows.awtLocalization");
               return Font.decode(var1.getString("menuFont"));
            } catch (MissingResourceException var2) {
               if (WMenuItemPeer.log.isLoggable(PlatformLogger.Level.FINE)) {
                  WMenuItemPeer.log.fine("WMenuItemPeer: " + var2.getMessage() + ". Using default MenuItem font.", (Throwable)var2);
               }

               return new Font("SanSerif", 0, 11);
            }
         }
      });
   }
}
