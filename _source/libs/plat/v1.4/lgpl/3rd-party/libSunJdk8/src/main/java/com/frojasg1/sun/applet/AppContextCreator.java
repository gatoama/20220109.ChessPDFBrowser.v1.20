package com.frojasg1.sun.applet;

import com.frojasg1.sun.awt.AppContext;
import com.frojasg1.sun.awt.SunToolkit;

class AppContextCreator extends Thread {
   Object syncObject = new Object();
   AppContext appContext = null;
   volatile boolean created = false;

   AppContextCreator(ThreadGroup var1) {
      super(var1, "AppContextCreator");
   }

   public void run() {
      this.appContext = SunToolkit.createNewAppContext();
      this.created = true;
      synchronized(this.syncObject) {
         this.syncObject.notifyAll();
      }
   }
}
