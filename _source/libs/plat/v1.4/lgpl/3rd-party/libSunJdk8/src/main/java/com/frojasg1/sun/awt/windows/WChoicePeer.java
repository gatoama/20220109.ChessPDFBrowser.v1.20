package com.frojasg1.sun.awt.windows;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.peer.ChoicePeer;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WToolkit;
import com.frojasg1.sun.awt.windows.WWindowPeer;

//final class WChoicePeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ChoicePeer {
abstract class WChoicePeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ChoicePeer {
   private WindowListener windowListener;

   public Dimension getMinimumSize() {
      FontMetrics var1 = this.getFontMetrics(((Choice)this.target).getFont());
      Choice var2 = (Choice)this.target;
      int var3 = 0;

      for(int var4 = var2.getItemCount(); var4-- > 0; var3 = Math.max(var1.stringWidth(var2.getItem(var4)), var3)) {
      }

      return new Dimension(28 + var3, Math.max(var1.getHeight() + 6, 15));
   }

   public boolean isFocusable() {
      return true;
   }

   public native void select(int var1);

   public void add(String var1, int var2) {
      this.addItem(var1, var2);
   }

   public boolean shouldClearRectBeforePaint() {
      return false;
   }

   public native void removeAll();

   public native void remove(int var1);

   public void addItem(String var1, int var2) {
      this.addItems(new String[]{var1}, var2);
   }

   public native void addItems(String[] var1, int var2);

   public synchronized native void reshape(int var1, int var2, int var3, int var4);

   WChoicePeer(Choice var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      Choice var1 = (Choice)this.target;
      int var2 = var1.getItemCount();
      if (var2 > 0) {
         String[] var3 = new String[var2];

         for(int var4 = 0; var4 < var2; ++var4) {
            var3[var4] = var1.getItem(var4);
         }

         this.addItems(var3, 0);
         if (var1.getSelectedIndex() >= 0) {
            this.select(var1.getSelectedIndex());
         }
      }

      Window var5 = SunToolkit.getContainingWindow((Component)this.target);
      if (var5 != null) {
         com.frojasg1.sun.awt.windows.WWindowPeer var6 = (com.frojasg1.sun.awt.windows.WWindowPeer)var5.getPeer();
         if (var6 != null) {
            this.windowListener = new WindowAdapter() {
               public void windowIconified(WindowEvent var1) {
                  WChoicePeer.this.closeList();
               }

               public void windowClosing(WindowEvent var1) {
                  WChoicePeer.this.closeList();
               }
            };
            var6.addWindowListener(this.windowListener);
         }
      }

      super.initialize();
   }

   protected void disposeImpl() {
      Window var1 = SunToolkit.getContainingWindow((Component)this.target);
      if (var1 != null) {
         com.frojasg1.sun.awt.windows.WWindowPeer var2 = (WWindowPeer)var1.getPeer();
         if (var2 != null) {
            var2.removeWindowListener(this.windowListener);
         }
      }

      super.disposeImpl();
   }

   void handleAction(final int var1) {
      final Choice var2 = (Choice)this.target;
      WToolkit.executeOnEventHandlerThread(var2, new Runnable() {
         public void run() {
            var2.select(var1);
            WChoicePeer.this.postEvent(new ItemEvent(var2, 701, var2.getItem(var1), 1));
         }
      });
   }

   int getDropDownHeight() {
      Choice var1 = (Choice)this.target;
      FontMetrics var2 = this.getFontMetrics(var1.getFont());
      int var3 = Math.min(var1.getItemCount(), 8);
      return var2.getHeight() * var3;
   }

   native void closeList();
}
