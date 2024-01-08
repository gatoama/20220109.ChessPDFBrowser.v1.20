package com.frojasg1.sun.rmi.transport;

import java.lang.ref.ReferenceQueue;
import java.rmi.NoSuchObjectException;
import java.rmi.Remote;
import java.rmi.dgc.VMID;
import java.rmi.server.ExportException;
import java.rmi.server.ObjID;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import com.frojasg1.sun.misc.GC;
import com.frojasg1.sun.rmi.runtime.Log;
import com.frojasg1.sun.rmi.runtime.NewThreadAction;
import com.frojasg1.sun.rmi.transport.DGCImpl;
import com.frojasg1.sun.rmi.transport.ObjectEndpoint;
import com.frojasg1.sun.rmi.transport.Target;
import com.frojasg1.sun.rmi.transport.Transport;
import com.frojasg1.sun.rmi.transport.WeakRef;
import com.frojasg1.sun.security.action.GetLongAction;

public final class ObjectTable {
   private static final long gcInterval = (Long)AccessController.doPrivileged(new GetLongAction("sun.rmi.dgc.server.gcInterval", 3600000L));
   private static final Object tableLock = new Object();
   private static final Map<com.frojasg1.sun.rmi.transport.ObjectEndpoint, com.frojasg1.sun.rmi.transport.Target> objTable = new HashMap();
   private static final Map<com.frojasg1.sun.rmi.transport.WeakRef, com.frojasg1.sun.rmi.transport.Target> implTable = new HashMap();
   private static final Object keepAliveLock = new Object();
   private static int keepAliveCount = 0;
   private static Thread reaper = null;
   static final ReferenceQueue<Object> reapQueue = new ReferenceQueue();
   private static GC.LatencyRequest gcLatencyRequest = null;

   private ObjectTable() {
   }

   static com.frojasg1.sun.rmi.transport.Target getTarget(com.frojasg1.sun.rmi.transport.ObjectEndpoint var0) {
      synchronized(tableLock) {
         return (com.frojasg1.sun.rmi.transport.Target)objTable.get(var0);
      }
   }

   public static com.frojasg1.sun.rmi.transport.Target getTarget(Remote var0) {
      synchronized(tableLock) {
         return (com.frojasg1.sun.rmi.transport.Target)implTable.get(new com.frojasg1.sun.rmi.transport.WeakRef(var0));
      }
   }

   public static Remote getStub(Remote var0) throws NoSuchObjectException {
      com.frojasg1.sun.rmi.transport.Target var1 = getTarget(var0);
      if (var1 == null) {
         throw new NoSuchObjectException("object not exported");
      } else {
         return var1.getStub();
      }
   }

   public static boolean unexportObject(Remote var0, boolean var1) throws NoSuchObjectException {
      synchronized(tableLock) {
         com.frojasg1.sun.rmi.transport.Target var3 = getTarget(var0);
         if (var3 == null) {
            throw new NoSuchObjectException("object not exported");
         } else if (var3.unexport(var1)) {
            removeTarget(var3);
            return true;
         } else {
            return false;
         }
      }
   }

   static void putTarget(com.frojasg1.sun.rmi.transport.Target var0) throws ExportException {
      com.frojasg1.sun.rmi.transport.ObjectEndpoint var1 = var0.getObjectEndpoint();
      com.frojasg1.sun.rmi.transport.WeakRef var2 = var0.getWeakImpl();
      if (com.frojasg1.sun.rmi.transport.DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
         com.frojasg1.sun.rmi.transport.DGCImpl.dgcLog.log(Log.VERBOSE, "add object " + var1);
      }

      synchronized(tableLock) {
         if (var0.getImpl() != null) {
            if (objTable.containsKey(var1)) {
               throw new ExportException("internal error: ObjID already in use");
            }

            if (implTable.containsKey(var2)) {
               throw new ExportException("object already exported");
            }

            objTable.put(var1, var0);
            implTable.put(var2, var0);
            if (!var0.isPermanent()) {
               incrementKeepAliveCount();
            }
         }

      }
   }

   private static void removeTarget(com.frojasg1.sun.rmi.transport.Target var0) {
      com.frojasg1.sun.rmi.transport.ObjectEndpoint var1 = var0.getObjectEndpoint();
      com.frojasg1.sun.rmi.transport.WeakRef var2 = var0.getWeakImpl();
      if (com.frojasg1.sun.rmi.transport.DGCImpl.dgcLog.isLoggable(Log.VERBOSE)) {
         com.frojasg1.sun.rmi.transport.DGCImpl.dgcLog.log(Log.VERBOSE, "remove object " + var1);
      }

      objTable.remove(var1);
      implTable.remove(var2);
      var0.markRemoved();
   }

   static void referenced(ObjID var0, long var1, VMID var3) {
      synchronized(tableLock) {
         com.frojasg1.sun.rmi.transport.ObjectEndpoint var5 = new com.frojasg1.sun.rmi.transport.ObjectEndpoint(var0, com.frojasg1.sun.rmi.transport.Transport.currentTransport());
         com.frojasg1.sun.rmi.transport.Target var6 = (com.frojasg1.sun.rmi.transport.Target)objTable.get(var5);
         if (var6 != null) {
            var6.referenced(var1, var3);
         }

      }
   }

   static void unreferenced(ObjID var0, long var1, VMID var3, boolean var4) {
      synchronized(tableLock) {
         com.frojasg1.sun.rmi.transport.ObjectEndpoint var6 = new com.frojasg1.sun.rmi.transport.ObjectEndpoint(var0, Transport.currentTransport());
         com.frojasg1.sun.rmi.transport.Target var7 = (com.frojasg1.sun.rmi.transport.Target)objTable.get(var6);
         if (var7 != null) {
            var7.unreferenced(var1, var3, var4);
         }

      }
   }

   static void incrementKeepAliveCount() {
      synchronized(keepAliveLock) {
         ++keepAliveCount;
         if (reaper == null) {
            reaper = (Thread)AccessController.doPrivileged(new NewThreadAction(new ObjectTable.Reaper(), "Reaper", false));
            reaper.start();
         }

         if (gcLatencyRequest == null) {
            gcLatencyRequest = GC.requestLatency(gcInterval);
         }

      }
   }

   static void decrementKeepAliveCount() {
      synchronized(keepAliveLock) {
         --keepAliveCount;
         if (keepAliveCount == 0) {
            if (reaper == null) {
               throw new AssertionError();
            }

            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  ObjectTable.reaper.interrupt();
                  return null;
               }
            });
            reaper = null;
            gcLatencyRequest.cancel();
            gcLatencyRequest = null;
         }

      }
   }

   private static class Reaper implements Runnable {
      private Reaper() {
      }

      public void run() {
         while(true) {
            try {
               com.frojasg1.sun.rmi.transport.WeakRef var1 = (com.frojasg1.sun.rmi.transport.WeakRef)ObjectTable.reapQueue.remove();
               synchronized(ObjectTable.tableLock) {
                  com.frojasg1.sun.rmi.transport.Target var3 = (Target)ObjectTable.implTable.get(var1);
                  if (var3 != null) {
                     if (!var3.isEmpty()) {
                        throw new Error("object with known references collected");
                     }

                     if (var3.isPermanent()) {
                        throw new Error("permanent object collected");
                     }

                     ObjectTable.removeTarget(var3);
                  }
               }

               if (!Thread.interrupted()) {
                  continue;
               }
            } catch (InterruptedException var6) {
            }

            return;
         }
      }
   }
}
