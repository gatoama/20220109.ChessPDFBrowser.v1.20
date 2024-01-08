package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.Queue;
import com.frojasg1.sun.misc.Request;

public class RequestProcessor implements Runnable {
   private static com.frojasg1.sun.misc.Queue<com.frojasg1.sun.misc.Request> requestQueue;
   private static Thread dispatcher;

   public RequestProcessor() {
   }

   public static void postRequest(com.frojasg1.sun.misc.Request var0) {
      lazyInitialize();
      requestQueue.enqueue(var0);
   }

   public void run() {
      lazyInitialize();

      while(true) {
         while(true) {
            try {
               com.frojasg1.sun.misc.Request var1 = (Request)requestQueue.dequeue();

               try {
                  var1.execute();
               } catch (Throwable var3) {
               }
            } catch (InterruptedException var4) {
            }
         }
      }
   }

   public static synchronized void startProcessing() {
      if (dispatcher == null) {
         dispatcher = new Thread(new RequestProcessor(), "Request Processor");
         dispatcher.setPriority(7);
         dispatcher.start();
      }

   }

   private static synchronized void lazyInitialize() {
      if (requestQueue == null) {
         requestQueue = new Queue();
      }

   }
}
