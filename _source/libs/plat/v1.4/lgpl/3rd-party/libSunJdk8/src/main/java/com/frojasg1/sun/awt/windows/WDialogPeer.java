package com.frojasg1.sun.awt.windows;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.Dialog.ModalityType;
import java.awt.peer.DialogPeer;
import java.util.Iterator;
import java.util.List;
import com.frojasg1.sun.awt.AWTAccessor;
import com.frojasg1.sun.awt.im.InputMethodManager;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WWindowPeer;

//final class WDialogPeer extends com.frojasg1.sun.awt.windows.WWindowPeer implements DialogPeer {
abstract class WDialogPeer extends com.frojasg1.sun.awt.windows.WWindowPeer implements DialogPeer {
   static final Color defaultBackground;
   boolean needDefaultBackground;

   WDialogPeer(Dialog var1) {
      super(var1);
      InputMethodManager var2 = InputMethodManager.getInstance();
      String var3 = var2.getTriggerMenuString();
      if (var3 != null) {
         this.pSetIMMOption(var3);
      }

   }

   native void createAwtDialog(com.frojasg1.sun.awt.windows.WComponentPeer var1);

   void create(WComponentPeer var1) {
      this.preCreate(var1);
      this.createAwtDialog(var1);
   }

   native void showModal();

   native void endModal();

   void initialize() {
      Dialog var1 = (Dialog)this.target;
      if (this.needDefaultBackground) {
         var1.setBackground(defaultBackground);
      }

      super.initialize();
      if (var1.getTitle() != null) {
         this.setTitle(var1.getTitle());
      }

      this.setResizable(var1.isResizable());
   }

   protected void realShow() {
      Dialog var1 = (Dialog)this.target;
      if (var1.getModalityType() != ModalityType.MODELESS) {
         this.showModal();
      } else {
         super.realShow();
      }

   }

   void hide() {
      Dialog var1 = (Dialog)this.target;
      if (var1.getModalityType() != ModalityType.MODELESS) {
         this.endModal();
      } else {
         super.hide();
      }

   }

   public void blockWindows(List<Window> var1) {
      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         Window var3 = (Window)var2.next();
         com.frojasg1.sun.awt.windows.WWindowPeer var4 = (WWindowPeer)AWTAccessor.getComponentAccessor().getPeer(var3);
         if (var4 != null) {
            var4.setModalBlocked((Dialog)this.target, true);
         }
      }

   }

   public Dimension getMinimumSize() {
      return ((Dialog)this.target).isUndecorated() ? super.getMinimumSize() : new Dimension(getSysMinWidth(), getSysMinHeight());
   }

   boolean isTargetUndecorated() {
      return ((Dialog)this.target).isUndecorated();
   }

   public void reshape(int var1, int var2, int var3, int var4) {
      if (((Dialog)this.target).isUndecorated()) {
         super.reshape(var1, var2, var3, var4);
      } else {
         this.reshapeFrame(var1, var2, var3, var4);
      }

   }

   private void setDefaultColor() {
      this.needDefaultBackground = true;
   }

   native void pSetIMMOption(String var1);

   void notifyIMMOptionChange() {
      InputMethodManager.getInstance().notifyChangeRequest((Component)this.target);
   }

   static {
      defaultBackground = SystemColor.control;
   }
}
