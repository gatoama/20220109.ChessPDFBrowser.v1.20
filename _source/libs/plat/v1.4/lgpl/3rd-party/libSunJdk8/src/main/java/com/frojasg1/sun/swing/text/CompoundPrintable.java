package com.frojasg1.sun.swing.text;

import com.frojasg1.sun.swing.text.CountingPrintable;

import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

class CompoundPrintable implements com.frojasg1.sun.swing.text.CountingPrintable {
   private final Queue<com.frojasg1.sun.swing.text.CountingPrintable> printables;
   private int offset = 0;

   public CompoundPrintable(List<com.frojasg1.sun.swing.text.CountingPrintable> var1) {
      this.printables = new LinkedList(var1);
   }

   public int print(Graphics var1, PageFormat var2, int var3) throws PrinterException {
      int var4;
      for(var4 = 1; this.printables.peek() != null; this.offset += ((com.frojasg1.sun.swing.text.CountingPrintable)this.printables.poll()).getNumberOfPages()) {
         var4 = ((CountingPrintable)this.printables.peek()).print(var1, var2, var3 - this.offset);
         if (var4 == 0) {
            break;
         }
      }

      return var4;
   }

   public int getNumberOfPages() {
      return this.offset;
   }
}
