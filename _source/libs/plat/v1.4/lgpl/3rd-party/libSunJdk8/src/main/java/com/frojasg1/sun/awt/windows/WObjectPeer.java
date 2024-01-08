package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.windows.WToolkit;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

abstract class WObjectPeer {
   volatile long pData;
   private volatile boolean destroyed;
   volatile Object target;
   private volatile boolean disposed;
   volatile Error createError = null;
   private final Object stateLock = new Object();
   private volatile Map<WObjectPeer, WObjectPeer> childPeers;

   WObjectPeer() {
   }

   public static WObjectPeer getPeerForTarget(Object var0) {
      WObjectPeer var1 = (WObjectPeer) WToolkit.targetToPeer(var0);
      return var1;
   }

   public long getData() {
      return this.pData;
   }

   public Object getTarget() {
      return this.target;
   }

   public final Object getStateLock() {
      return this.stateLock;
   }

   protected abstract void disposeImpl();

   public final void dispose() {
      boolean var1 = false;
      synchronized(this) {
         if (!this.disposed) {
            var1 = true;
            this.disposed = true;
         }
      }

      if (var1) {
         if (this.childPeers != null) {
            this.disposeChildPeers();
         }

         this.disposeImpl();
      }

   }

   protected final boolean isDisposed() {
      return this.disposed;
   }

   private static native void initIDs();

   final void addChildPeer(WObjectPeer var1) {
      synchronized(this.getStateLock()) {
         if (this.childPeers == null) {
            this.childPeers = new WeakHashMap();
         }

         if (this.isDisposed()) {
            throw new IllegalStateException("Parent peer is disposed");
         } else {
            this.childPeers.put(var1, this);
         }
      }
   }

   private void disposeChildPeers() {
      synchronized(this.getStateLock()) {
         Iterator var2 = this.childPeers.keySet().iterator();

         while(var2.hasNext()) {
            WObjectPeer var3 = (WObjectPeer)var2.next();
            if (var3 != null) {
               try {
                  var3.dispose();
               } catch (Exception var6) {
               }
            }
         }

      }
   }

   static {
      initIDs();
   }
}
