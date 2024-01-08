package com.frojasg1.sun.applet;

import com.frojasg1.sun.applet.AppletEvent;
import com.frojasg1.sun.applet.AppletListener;

public class AppletEventMulticaster implements com.frojasg1.sun.applet.AppletListener {
   private final com.frojasg1.sun.applet.AppletListener a;
   private final com.frojasg1.sun.applet.AppletListener b;

   public AppletEventMulticaster(com.frojasg1.sun.applet.AppletListener var1, com.frojasg1.sun.applet.AppletListener var2) {
      this.a = var1;
      this.b = var2;
   }

   public void appletStateChanged(AppletEvent var1) {
      this.a.appletStateChanged(var1);
      this.b.appletStateChanged(var1);
   }

   public static com.frojasg1.sun.applet.AppletListener add(com.frojasg1.sun.applet.AppletListener var0, com.frojasg1.sun.applet.AppletListener var1) {
      return addInternal(var0, var1);
   }

   public static com.frojasg1.sun.applet.AppletListener remove(com.frojasg1.sun.applet.AppletListener var0, com.frojasg1.sun.applet.AppletListener var1) {
      return removeInternal(var0, var1);
   }

   private static com.frojasg1.sun.applet.AppletListener addInternal(com.frojasg1.sun.applet.AppletListener var0, com.frojasg1.sun.applet.AppletListener var1) {
      if (var0 == null) {
         return var1;
      } else {
         return (com.frojasg1.sun.applet.AppletListener)(var1 == null ? var0 : new AppletEventMulticaster(var0, var1));
      }
   }

   protected com.frojasg1.sun.applet.AppletListener remove(com.frojasg1.sun.applet.AppletListener var1) {
      if (var1 == this.a) {
         return this.b;
      } else if (var1 == this.b) {
         return this.a;
      } else {
         com.frojasg1.sun.applet.AppletListener var2 = removeInternal(this.a, var1);
         com.frojasg1.sun.applet.AppletListener var3 = removeInternal(this.b, var1);
         return (com.frojasg1.sun.applet.AppletListener)(var2 == this.a && var3 == this.b ? this : addInternal(var2, var3));
      }
   }

   private static com.frojasg1.sun.applet.AppletListener removeInternal(com.frojasg1.sun.applet.AppletListener var0, AppletListener var1) {
      if (var0 != var1 && var0 != null) {
         return var0 instanceof AppletEventMulticaster ? ((AppletEventMulticaster)var0).remove(var1) : var0;
      } else {
         return null;
      }
   }
}
