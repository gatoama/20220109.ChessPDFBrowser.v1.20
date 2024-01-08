package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.Lock;

public final class ConditionLock extends Lock {
   private int state = 0;

   public ConditionLock() {
   }

   public ConditionLock(int var1) {
      this.state = var1;
   }

   public synchronized void lockWhen(int var1) throws InterruptedException {
      while(this.state != var1) {
         this.wait();
      }

      this.lock();
   }

   public synchronized void unlockWith(int var1) {
      this.state = var1;
      this.unlock();
   }
}
