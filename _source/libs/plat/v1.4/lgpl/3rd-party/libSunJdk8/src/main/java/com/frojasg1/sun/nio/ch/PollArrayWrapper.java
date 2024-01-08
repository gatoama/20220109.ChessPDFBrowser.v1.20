package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.AllocatedNativeObject;
import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.SelectionKeyImpl;

class PollArrayWrapper {
   private com.frojasg1.sun.nio.ch.AllocatedNativeObject pollArray;
   long pollArrayAddress;
   private static final short FD_OFFSET = 0;
   private static final short EVENT_OFFSET = 4;
   static short SIZE_POLLFD = 8;
   private int size;

   PollArrayWrapper(int var1) {
      int var2 = var1 * SIZE_POLLFD;
      this.pollArray = new com.frojasg1.sun.nio.ch.AllocatedNativeObject(var2, true);
      this.pollArrayAddress = this.pollArray.address();
      this.size = var1;
   }

   void addEntry(int var1, SelectionKeyImpl var2) {
      this.putDescriptor(var1, var2.channel.getFDVal());
   }

   void replaceEntry(PollArrayWrapper var1, int var2, PollArrayWrapper var3, int var4) {
      var3.putDescriptor(var4, var1.getDescriptor(var2));
      var3.putEventOps(var4, var1.getEventOps(var2));
   }

   void grow(int var1) {
      PollArrayWrapper var2 = new PollArrayWrapper(var1);

      for(int var3 = 0; var3 < this.size; ++var3) {
         this.replaceEntry(this, var3, var2, var3);
      }

      this.pollArray.free();
      this.pollArray = var2.pollArray;
      this.size = var2.size;
      this.pollArrayAddress = this.pollArray.address();
   }

   void free() {
      this.pollArray.free();
   }

   void putDescriptor(int var1, int var2) {
      this.pollArray.putInt(SIZE_POLLFD * var1 + 0, var2);
   }

   void putEventOps(int var1, int var2) {
      this.pollArray.putShort(SIZE_POLLFD * var1 + 4, (short)var2);
   }

   int getEventOps(int var1) {
      return this.pollArray.getShort(SIZE_POLLFD * var1 + 4);
   }

   int getDescriptor(int var1) {
      return this.pollArray.getInt(SIZE_POLLFD * var1 + 0);
   }

   void addWakeupSocket(int var1, int var2) {
      this.putDescriptor(var2, var1);
      this.putEventOps(var2, Net.POLLIN);
   }
}
