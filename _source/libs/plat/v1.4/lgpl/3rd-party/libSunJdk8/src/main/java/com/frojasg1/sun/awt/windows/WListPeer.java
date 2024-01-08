package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WComponentPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.peer.ListPeer;

//final class WListPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ListPeer {
abstract class WListPeer extends com.frojasg1.sun.awt.windows.WComponentPeer implements ListPeer {
   private FontMetrics fm;

   public boolean isFocusable() {
      return true;
   }

   public int[] getSelectedIndexes() {
      List var1 = (List)this.target;
      int var2 = var1.countItems();
      int[] var3 = new int[var2];
      int var4 = 0;

      for(int var5 = 0; var5 < var2; ++var5) {
         if (this.isSelected(var5)) {
            var3[var4++] = var5;
         }
      }

      int[] var6 = new int[var4];
      System.arraycopy(var3, 0, var6, 0, var4);
      return var6;
   }

   public void add(String var1, int var2) {
      this.addItem(var1, var2);
   }

   public void removeAll() {
      this.clear();
   }

   public void setMultipleMode(boolean var1) {
      this.setMultipleSelections(var1);
   }

   public Dimension getPreferredSize(int var1) {
      return this.preferredSize(var1);
   }

   public Dimension getMinimumSize(int var1) {
      return this.minimumSize(var1);
   }

   public void addItem(String var1, int var2) {
      this.addItems(new String[]{var1}, var2, this.fm.stringWidth(var1));
   }

   native void addItems(String[] var1, int var2, int var3);

   public native void delItems(int var1, int var2);

   public void clear() {
      List var1 = (List)this.target;
      this.delItems(0, var1.countItems());
   }

   public native void select(int var1);

   public native void deselect(int var1);

   public native void makeVisible(int var1);

   public native void setMultipleSelections(boolean var1);

   public native int getMaxWidth();

   public Dimension preferredSize(int var1) {
      if (this.fm == null) {
         List var2 = (List)this.target;
         this.fm = this.getFontMetrics(var2.getFont());
      }

      Dimension var3 = this.minimumSize(var1);
      var3.width = Math.max(var3.width, this.getMaxWidth() + 20);
      return var3;
   }

   public Dimension minimumSize(int var1) {
      return new Dimension(20 + this.fm.stringWidth("0123456789abcde"), this.fm.getHeight() * var1 + 4);
   }

   WListPeer(List var1) {
      super(var1);
   }

   native void create(WComponentPeer var1);

   void initialize() {
      List var1 = (List)this.target;
      this.fm = this.getFontMetrics(var1.getFont());
      Font var2 = var1.getFont();
      if (var2 != null) {
         this.setFont(var2);
      }

      int var3 = var1.countItems();
      int var5;
      if (var3 > 0) {
         String[] var4 = new String[var3];
         var5 = 0;
         boolean var6 = false;

         for(int var7 = 0; var7 < var3; ++var7) {
            var4[var7] = var1.getItem(var7);
            int var9 = this.fm.stringWidth(var4[var7]);
            if (var9 > var5) {
               var5 = var9;
            }
         }

         this.addItems(var4, 0, var5);
      }

      this.setMultipleSelections(var1.allowsMultipleSelections());
      int[] var8 = var1.getSelectedIndexes();

      for(var5 = 0; var5 < var8.length; ++var5) {
         this.select(var8[var5]);
      }

      var5 = var1.getVisibleIndex();
      if (var5 < 0 && var8.length > 0) {
         var5 = var8[0];
      }

      if (var5 >= 0) {
         this.makeVisible(var5);
      }

      super.initialize();
   }

   public boolean shouldClearRectBeforePaint() {
      return false;
   }

   private native void updateMaxItemWidth();

   native boolean isSelected(int var1);

   synchronized void _setFont(Font var1) {
      super._setFont(var1);
      this.fm = this.getFontMetrics(((List)this.target).getFont());
      this.updateMaxItemWidth();
   }

   void handleAction(final int var1, final long var2, final int var4) {
      final List var5 = (List)this.target;
      com.frojasg1.sun.awt.windows.WToolkit.executeOnEventHandlerThread(var5, new Runnable() {
         public void run() {
            var5.select(var1);
            WListPeer.this.postEvent(new ActionEvent(WListPeer.this.target, 1001, var5.getItem(var1), var2, var4));
         }
      });
   }

   void handleListChanged(final int var1) {
      final List var2 = (List)this.target;
      WToolkit.executeOnEventHandlerThread(var2, new Runnable() {
         public void run() {
            WListPeer.this.postEvent(new ItemEvent(var2, 701, var1, WListPeer.this.isSelected(var1) ? 1 : 2));
         }
      });
   }
}
