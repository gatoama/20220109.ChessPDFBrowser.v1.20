package com.frojasg1.sun.awt.dnd;

import com.frojasg1.sun.awt.dnd.SunDropTargetContextPeer;

import java.awt.Component;
import java.awt.event.MouseEvent;

public class SunDropTargetEvent extends MouseEvent {
   public static final int MOUSE_DROPPED = 502;
   private final com.frojasg1.sun.awt.dnd.SunDropTargetContextPeer.EventDispatcher dispatcher;

   public SunDropTargetEvent(Component var1, int var2, int var3, int var4, com.frojasg1.sun.awt.dnd.SunDropTargetContextPeer.EventDispatcher var5) {
      super(var1, var2, System.currentTimeMillis(), 0, var3, var4, 0, 0, 0, false, 0);
      this.dispatcher = var5;
      this.dispatcher.registerEvent(this);
   }

   public void dispatch() {
      try {
         this.dispatcher.dispatchEvent(this);
      } finally {
         this.dispatcher.unregisterEvent(this);
      }

   }

   public void consume() {
      boolean var1 = this.isConsumed();
      super.consume();
      if (!var1 && this.isConsumed()) {
         this.dispatcher.unregisterEvent(this);
      }

   }

   public SunDropTargetContextPeer.EventDispatcher getDispatcher() {
      return this.dispatcher;
   }

   public String paramString() {
      String var1 = null;
      switch(this.id) {
      case 502:
         var1 = "MOUSE_DROPPED";
         return var1 + ",(" + this.getX() + "," + this.getY() + ")";
      default:
         return super.paramString();
      }
   }
}
