package com.frojasg1.sun.awt.windows;

import com.frojasg1.sun.awt.Mutex;
import com.frojasg1.sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import com.frojasg1.sun.awt.windows.WToolkit;

final class WToolkitThreadBlockedHandler extends Mutex implements ToolkitThreadBlockedHandler {
   WToolkitThreadBlockedHandler() {
   }

   public void enter() {
      if (!this.isOwned()) {
         throw new IllegalMonitorStateException();
      } else {
         this.unlock();
         this.startSecondaryEventLoop();
         this.lock();
      }
   }

   public void exit() {
      if (!this.isOwned()) {
         throw new IllegalMonitorStateException();
      } else {
         WToolkit.quitSecondaryEventLoop();
      }
   }

   private native void startSecondaryEventLoop();
}
