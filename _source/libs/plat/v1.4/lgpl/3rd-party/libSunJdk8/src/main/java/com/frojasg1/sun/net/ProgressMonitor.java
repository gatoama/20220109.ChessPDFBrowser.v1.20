package com.frojasg1.sun.net;

import com.frojasg1.sun.net.DefaultProgressMeteringPolicy;
import com.frojasg1.sun.net.ProgressEvent;
import com.frojasg1.sun.net.ProgressListener;
import com.frojasg1.sun.net.ProgressMeteringPolicy;
import com.frojasg1.sun.net.ProgressSource;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

public class ProgressMonitor {
   private static com.frojasg1.sun.net.ProgressMeteringPolicy meteringPolicy = new com.frojasg1.sun.net.DefaultProgressMeteringPolicy();
   private static ProgressMonitor pm = new ProgressMonitor();
   private ArrayList<com.frojasg1.sun.net.ProgressSource> progressSourceList = new ArrayList();
   private ArrayList<com.frojasg1.sun.net.ProgressListener> progressListenerList = new ArrayList();

   public ProgressMonitor() {
   }

   public static synchronized ProgressMonitor getDefault() {
      return pm;
   }

   public static synchronized void setDefault(ProgressMonitor var0) {
      if (var0 != null) {
         pm = var0;
      }

   }

   public static synchronized void setMeteringPolicy(ProgressMeteringPolicy var0) {
      if (var0 != null) {
         meteringPolicy = var0;
      }

   }

   public ArrayList<com.frojasg1.sun.net.ProgressSource> getProgressSources() {
      ArrayList var1 = new ArrayList();

      try {
         synchronized(this.progressSourceList) {
            Iterator var3 = this.progressSourceList.iterator();

            while(var3.hasNext()) {
               com.frojasg1.sun.net.ProgressSource var4 = (com.frojasg1.sun.net.ProgressSource)var3.next();
               var1.add((com.frojasg1.sun.net.ProgressSource)var4.clone());
            }
         }
      } catch (CloneNotSupportedException var7) {
         var7.printStackTrace();
      }

      return var1;
   }

   public synchronized int getProgressUpdateThreshold() {
      return meteringPolicy.getProgressUpdateThreshold();
   }

   public boolean shouldMeterInput(URL var1, String var2) {
      return meteringPolicy.shouldMeterInput(var1, var2);
   }

   public void registerSource(com.frojasg1.sun.net.ProgressSource var1) {
      synchronized(this.progressSourceList) {
         if (this.progressSourceList.contains(var1)) {
            return;
         }

         this.progressSourceList.add(var1);
      }

      if (this.progressListenerList.size() > 0) {
         ArrayList var2 = new ArrayList();
         synchronized(this.progressListenerList) {
            Iterator var4 = this.progressListenerList.iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break;
               }

               var2.add(var4.next());
            }
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            com.frojasg1.sun.net.ProgressListener var8 = (com.frojasg1.sun.net.ProgressListener)var3.next();
            com.frojasg1.sun.net.ProgressEvent var5 = new com.frojasg1.sun.net.ProgressEvent(var1, var1.getURL(), var1.getMethod(), var1.getContentType(), var1.getState(), var1.getProgress(), var1.getExpected());
            var8.progressStart(var5);
         }
      }

   }

   public void unregisterSource(com.frojasg1.sun.net.ProgressSource var1) {
      synchronized(this.progressSourceList) {
         if (!this.progressSourceList.contains(var1)) {
            return;
         }

         var1.close();
         this.progressSourceList.remove(var1);
      }

      if (this.progressListenerList.size() > 0) {
         ArrayList var2 = new ArrayList();
         synchronized(this.progressListenerList) {
            Iterator var4 = this.progressListenerList.iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break;
               }

               var2.add(var4.next());
            }
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            com.frojasg1.sun.net.ProgressListener var8 = (com.frojasg1.sun.net.ProgressListener)var3.next();
            com.frojasg1.sun.net.ProgressEvent var5 = new com.frojasg1.sun.net.ProgressEvent(var1, var1.getURL(), var1.getMethod(), var1.getContentType(), var1.getState(), var1.getProgress(), var1.getExpected());
            var8.progressFinish(var5);
         }
      }

   }

   public void updateProgress(ProgressSource var1) {
      synchronized(this.progressSourceList) {
         if (!this.progressSourceList.contains(var1)) {
            return;
         }
      }

      if (this.progressListenerList.size() > 0) {
         ArrayList var2 = new ArrayList();
         synchronized(this.progressListenerList) {
            Iterator var4 = this.progressListenerList.iterator();

            while(true) {
               if (!var4.hasNext()) {
                  break;
               }

               var2.add(var4.next());
            }
         }

         Iterator var3 = var2.iterator();

         while(var3.hasNext()) {
            com.frojasg1.sun.net.ProgressListener var8 = (com.frojasg1.sun.net.ProgressListener)var3.next();
            com.frojasg1.sun.net.ProgressEvent var5 = new ProgressEvent(var1, var1.getURL(), var1.getMethod(), var1.getContentType(), var1.getState(), var1.getProgress(), var1.getExpected());
            var8.progressUpdate(var5);
         }
      }

   }

   public void addProgressListener(com.frojasg1.sun.net.ProgressListener var1) {
      synchronized(this.progressListenerList) {
         this.progressListenerList.add(var1);
      }
   }

   public void removeProgressListener(ProgressListener var1) {
      synchronized(this.progressListenerList) {
         this.progressListenerList.remove(var1);
      }
   }
}
