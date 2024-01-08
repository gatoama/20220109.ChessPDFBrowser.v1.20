package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.SelChImpl;
import com.frojasg1.sun.nio.ch.SelectionKeyImpl;
import com.frojasg1.sun.nio.ch.SocketChannelImpl;
import com.frojasg1.sun.nio.ch.Util;

import java.io.FileDescriptor;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.SocketChannel;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.spi.SelectorProvider;

class SinkChannelImpl extends SinkChannel implements SelChImpl {
   SocketChannel sc;

   public FileDescriptor getFD() {
      return ((com.frojasg1.sun.nio.ch.SocketChannelImpl)this.sc).getFD();
   }

   public int getFDVal() {
      return ((com.frojasg1.sun.nio.ch.SocketChannelImpl)this.sc).getFDVal();
   }

   SinkChannelImpl(SelectorProvider var1, SocketChannel var2) {
      super(var1);
      this.sc = var2;
   }

   protected void implCloseSelectableChannel() throws IOException {
      if (!this.isRegistered()) {
         this.kill();
      }

   }

   public void kill() throws IOException {
      this.sc.close();
   }

   protected void implConfigureBlocking(boolean var1) throws IOException {
      this.sc.configureBlocking(var1);
   }

   public boolean translateReadyOps(int var1, int var2, com.frojasg1.sun.nio.ch.SelectionKeyImpl var3) {
      int var4 = var3.nioInterestOps();
      int var5 = var3.nioReadyOps();
      int var6 = var2;
      if ((var1 & com.frojasg1.sun.nio.ch.Net.POLLNVAL) != 0) {
         throw new Error("POLLNVAL detected");
      } else if ((var1 & (com.frojasg1.sun.nio.ch.Net.POLLERR | com.frojasg1.sun.nio.ch.Net.POLLHUP)) != 0) {
         var3.nioReadyOps(var4);
         return (var4 & ~var5) != 0;
      } else {
         if ((var1 & com.frojasg1.sun.nio.ch.Net.POLLOUT) != 0 && (var4 & 4) != 0) {
            var6 = var2 | 4;
         }

         var3.nioReadyOps(var6);
         return (var6 & ~var5) != 0;
      }
   }

   public boolean translateAndUpdateReadyOps(int var1, com.frojasg1.sun.nio.ch.SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, var2.nioReadyOps(), var2);
   }

   public boolean translateAndSetReadyOps(int var1, com.frojasg1.sun.nio.ch.SelectionKeyImpl var2) {
      return this.translateReadyOps(var1, 0, var2);
   }

   public void translateAndSetInterestOps(int var1, SelectionKeyImpl var2) {
      if ((var1 & 4) != 0) {
         var1 = Net.POLLOUT;
      }

      var2.selector.putEventOps(var2, var1);
   }

   public int write(ByteBuffer var1) throws IOException {
      try {
         return this.sc.write(var1);
      } catch (AsynchronousCloseException var3) {
         this.close();
         throw var3;
      }
   }

   public long write(ByteBuffer[] var1) throws IOException {
      try {
         return this.sc.write(var1);
      } catch (AsynchronousCloseException var3) {
         this.close();
         throw var3;
      }
   }

   public long write(ByteBuffer[] var1, int var2, int var3) throws IOException {
      if (var2 >= 0 && var3 >= 0 && var2 <= var1.length - var3) {
         try {
            return this.write(Util.subsequence(var1, var2, var3));
         } catch (AsynchronousCloseException var5) {
            this.close();
            throw var5;
         }
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}
