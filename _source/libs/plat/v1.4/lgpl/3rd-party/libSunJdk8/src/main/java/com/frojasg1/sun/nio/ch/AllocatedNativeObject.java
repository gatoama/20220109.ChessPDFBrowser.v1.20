package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.NativeObject;

class AllocatedNativeObject extends com.frojasg1.sun.nio.ch.NativeObject {
   AllocatedNativeObject(int var1, boolean var2) {
      super(var1, var2);
   }

   synchronized void free() {
      if (this.allocationAddress != 0L) {
         unsafe.freeMemory(this.allocationAddress);
         this.allocationAddress = 0L;
      }

   }
}
