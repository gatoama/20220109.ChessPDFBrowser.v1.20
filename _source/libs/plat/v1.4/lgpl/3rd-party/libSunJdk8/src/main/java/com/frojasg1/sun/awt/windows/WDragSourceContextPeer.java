package com.frojasg1.sun.awt.windows;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.InvalidDnDOperationException;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.util.Map;
import com.frojasg1.sun.awt.dnd.SunDragSourceContextPeer;
import com.frojasg1.sun.awt.windows.WDropTargetContextPeer;
import com.frojasg1.sun.awt.windows.WToolkit;

final class WDragSourceContextPeer extends SunDragSourceContextPeer {
   private static final WDragSourceContextPeer theInstance = new WDragSourceContextPeer((DragGestureEvent)null);

   public void startSecondaryEventLoop() {
      com.frojasg1.sun.awt.windows.WToolkit.startSecondaryEventLoop();
   }

   public void quitSecondaryEventLoop() {
      WToolkit.quitSecondaryEventLoop();
   }

   private WDragSourceContextPeer(DragGestureEvent var1) {
      super(var1);
   }

   static WDragSourceContextPeer createDragSourceContextPeer(DragGestureEvent var0) throws InvalidDnDOperationException {
      theInstance.setTrigger(var0);
      return theInstance;
   }

   protected void startDrag(Transferable var1, long[] var2, Map var3) {
      long var4 = 0L;
      var4 = this.createDragSource(this.getTrigger().getComponent(), var1, this.getTrigger().getTriggerEvent(), this.getTrigger().getSourceAsDragGestureRecognizer().getSourceActions(), var2, var3);
      if (var4 == 0L) {
         throw new InvalidDnDOperationException("failed to create native peer");
      } else {
         int[] var6 = null;
         Point var7 = null;
         Image var8 = this.getDragImage();
         int var9 = -1;
         int var10 = -1;
         if (var8 != null) {
            try {
               var9 = var8.getWidth((ImageObserver)null);
               var10 = var8.getHeight((ImageObserver)null);
               if (var9 < 0 || var10 < 0) {
                  throw new InvalidDnDOperationException("drag image is not ready");
               }

               var7 = this.getDragImageOffset();
               BufferedImage var11 = new BufferedImage(var9, var10, 2);
               var11.getGraphics().drawImage(var8, 0, 0, (ImageObserver)null);
               var6 = ((DataBufferInt)var11.getData().getDataBuffer()).getData();
            } catch (Throwable var12) {
               throw new InvalidDnDOperationException("drag image creation problem: " + var12.getMessage());
            }
         }

         this.setNativeContext(var4);
         com.frojasg1.sun.awt.windows.WDropTargetContextPeer.setCurrentJVMLocalSourceTransferable(var1);
         if (var6 != null) {
            this.doDragDrop(this.getNativeContext(), this.getCursor(), var6, var9, var10, var7.x, var7.y);
         } else {
            this.doDragDrop(this.getNativeContext(), this.getCursor(), (int[])null, -1, -1, 0, 0);
         }

      }
   }

   native long createDragSource(Component var1, Transferable var2, InputEvent var3, int var4, long[] var5, Map var6);

   native void doDragDrop(long var1, Cursor var3, int[] var4, int var5, int var6, int var7, int var8);

   protected native void setNativeCursor(long var1, Cursor var3, int var4);
}
