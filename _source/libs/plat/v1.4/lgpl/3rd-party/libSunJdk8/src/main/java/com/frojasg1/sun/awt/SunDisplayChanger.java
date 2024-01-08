package com.frojasg1.sun.awt;

import java.awt.IllegalComponentStateException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import com.frojasg1.sun.awt.DisplayChangedListener;
import com.frojasg1.sun.util.logging.PlatformLogger;

public class SunDisplayChanger {
   private static final PlatformLogger log = PlatformLogger.getLogger("sun.awt.multiscreen.SunDisplayChanger");
   private Map<com.frojasg1.sun.awt.DisplayChangedListener, Void> listeners = Collections.synchronizedMap(new WeakHashMap(1));

   public SunDisplayChanger() {
   }

   public void add(com.frojasg1.sun.awt.DisplayChangedListener var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && var1 == null) {
         log.fine("Assertion (theListener != null) failed");
      }

      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("Adding listener: " + var1);
      }

      this.listeners.put(var1, null);
   }

   public void remove(com.frojasg1.sun.awt.DisplayChangedListener var1) {
      if (log.isLoggable(PlatformLogger.Level.FINE) && var1 == null) {
         log.fine("Assertion (theListener != null) failed");
      }

      if (log.isLoggable(PlatformLogger.Level.FINER)) {
         log.finer("Removing listener: " + var1);
      }

      this.listeners.remove(var1);
   }

   public void notifyListeners() {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         log.finest("notifyListeners");
      }

      HashSet var1;
      synchronized(this.listeners) {
         var1 = new HashSet(this.listeners.keySet());
      }

      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         com.frojasg1.sun.awt.DisplayChangedListener var3 = (com.frojasg1.sun.awt.DisplayChangedListener)var2.next();

         try {
            if (log.isLoggable(PlatformLogger.Level.FINEST)) {
               log.finest("displayChanged for listener: " + var3);
            }

            var3.displayChanged();
         } catch (IllegalComponentStateException var5) {
            this.listeners.remove(var3);
         }
      }

   }

   public void notifyPaletteChanged() {
      if (log.isLoggable(PlatformLogger.Level.FINEST)) {
         log.finest("notifyPaletteChanged");
      }

      HashSet var1;
      synchronized(this.listeners) {
         var1 = new HashSet(this.listeners.keySet());
      }

      Iterator var2 = var1.iterator();

      while(var2.hasNext()) {
         com.frojasg1.sun.awt.DisplayChangedListener var3 = (DisplayChangedListener)var2.next();

         try {
            if (log.isLoggable(PlatformLogger.Level.FINEST)) {
               log.finest("paletteChanged for listener: " + var3);
            }

            var3.paletteChanged();
         } catch (IllegalComponentStateException var5) {
            this.listeners.remove(var3);
         }
      }

   }
}
