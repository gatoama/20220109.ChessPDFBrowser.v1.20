package com.frojasg1.sun.net.www.http;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.frojasg1.sun.net.www.http.ClientVector;
import com.frojasg1.sun.net.www.http.HttpClient;
import com.frojasg1.sun.net.www.http.KeepAliveEntry;
import com.frojasg1.sun.net.www.http.KeepAliveKey;
import com.frojasg1.sun.security.action.GetIntegerAction;

public class KeepAliveCache extends HashMap<com.frojasg1.sun.net.www.http.KeepAliveKey, com.frojasg1.sun.net.www.http.ClientVector> implements Runnable {
   private static final long serialVersionUID = -2937172892064557949L;
   static final int MAX_CONNECTIONS = 5;
   static int result = -1;
   static final int LIFETIME = 5000;
   private Thread keepAliveTimer = null;

   static int getMaxConnections() {
      if (result == -1) {
         result = (Integer)AccessController.doPrivileged(new GetIntegerAction("http.maxConnections", 5));
         if (result <= 0) {
            result = 5;
         }
      }

      return result;
   }

   public KeepAliveCache() {
   }

   public synchronized void put(URL var1, Object var2, com.frojasg1.sun.net.www.http.HttpClient var3) {
      boolean var4 = this.keepAliveTimer == null;
      if (!var4 && !this.keepAliveTimer.isAlive()) {
         var4 = true;
      }

      if (var4) {
         this.clear();
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               ThreadGroup var1 = Thread.currentThread().getThreadGroup();

               for(ThreadGroup var2 = null; (var2 = var1.getParent()) != null; var1 = var2) {
               }

               KeepAliveCache.this.keepAliveTimer = new Thread(var1, KeepAliveCache.this, "Keep-Alive-Timer");
               KeepAliveCache.this.keepAliveTimer.setDaemon(true);
               KeepAliveCache.this.keepAliveTimer.setPriority(8);
               KeepAliveCache.this.keepAliveTimer.setContextClassLoader((ClassLoader)null);
               KeepAliveCache.this.keepAliveTimer.start();
               return null;
            }
         });
      }

      com.frojasg1.sun.net.www.http.KeepAliveKey var5 = new com.frojasg1.sun.net.www.http.KeepAliveKey(var1, var2);
      com.frojasg1.sun.net.www.http.ClientVector var6 = (com.frojasg1.sun.net.www.http.ClientVector)super.get(var5);
      if (var6 == null) {
         int var7 = var3.getKeepAliveTimeout();
         var6 = new com.frojasg1.sun.net.www.http.ClientVector(var7 > 0 ? var7 * 1000 : 5000);
         var6.put(var3);
         super.put(var5, var6);
      } else {
         var6.put(var3);
      }

   }

   public synchronized void remove(com.frojasg1.sun.net.www.http.HttpClient var1, Object var2) {
      com.frojasg1.sun.net.www.http.KeepAliveKey var3 = new com.frojasg1.sun.net.www.http.KeepAliveKey(var1.url, var2);
      com.frojasg1.sun.net.www.http.ClientVector var4 = (com.frojasg1.sun.net.www.http.ClientVector)super.get(var3);
      if (var4 != null) {
         var4.remove(var1);
         if (var4.empty()) {
            this.removeVector(var3);
         }
      }

   }

   synchronized void removeVector(com.frojasg1.sun.net.www.http.KeepAliveKey var1) {
      super.remove(var1);
   }

   public synchronized com.frojasg1.sun.net.www.http.HttpClient get(URL var1, Object var2) {
      com.frojasg1.sun.net.www.http.KeepAliveKey var3 = new com.frojasg1.sun.net.www.http.KeepAliveKey(var1, var2);
      com.frojasg1.sun.net.www.http.ClientVector var4 = (com.frojasg1.sun.net.www.http.ClientVector)super.get(var3);
      return var4 == null ? null : var4.get();
   }

   public void run() {
      do {
         try {
            Thread.sleep(5000L);
         } catch (InterruptedException var14) {
         }

         synchronized(this) {
            long var2 = System.currentTimeMillis();
            ArrayList var4 = new ArrayList();
            Iterator var5 = this.keySet().iterator();

            com.frojasg1.sun.net.www.http.KeepAliveKey var6;
            while(var5.hasNext()) {
               var6 = (com.frojasg1.sun.net.www.http.KeepAliveKey)var5.next();
               com.frojasg1.sun.net.www.http.ClientVector var7 = (com.frojasg1.sun.net.www.http.ClientVector)this.get(var6);
               synchronized(var7) {
                  int var9;
                  for(var9 = 0; var9 < var7.size(); ++var9) {
                     com.frojasg1.sun.net.www.http.KeepAliveEntry var10 = (com.frojasg1.sun.net.www.http.KeepAliveEntry)var7.elementAt(var9);
                     if (var2 - var10.idleStartTime <= (long)var7.nap) {
                        break;
                     }

                     HttpClient var11 = var10.hc;
                     var11.closeServer();
                  }

                  var7.subList(0, var9).clear();
                  if (var7.size() == 0) {
                     var4.add(var6);
                  }
               }
            }

            var5 = var4.iterator();

            while(var5.hasNext()) {
               var6 = (com.frojasg1.sun.net.www.http.KeepAliveKey)var5.next();
               this.removeVector(var6);
            }
         }
      } while(this.size() > 0);

   }

   private void writeObject(ObjectOutputStream var1) throws IOException {
      throw new NotSerializableException();
   }

   private void readObject(ObjectInputStream var1) throws IOException, ClassNotFoundException {
      throw new NotSerializableException();
   }
}
