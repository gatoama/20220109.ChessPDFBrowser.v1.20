package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.Iocp;
import com.frojasg1.sun.nio.ch.ThreadPool;
import com.frojasg1.sun.nio.ch.WindowsAsynchronousServerSocketChannelImpl;
import com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl;

import java.io.IOException;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.IllegalChannelGroupException;
import java.nio.channels.spi.AsynchronousChannelProvider;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

public class WindowsAsynchronousChannelProvider extends AsynchronousChannelProvider {
   private static volatile com.frojasg1.sun.nio.ch.Iocp defaultIocp;

   public WindowsAsynchronousChannelProvider() {
   }

   private com.frojasg1.sun.nio.ch.Iocp defaultIocp() throws IOException {
      if (defaultIocp == null) {
         Class var1 = WindowsAsynchronousChannelProvider.class;
         synchronized(WindowsAsynchronousChannelProvider.class) {
            if (defaultIocp == null) {
               defaultIocp = (new com.frojasg1.sun.nio.ch.Iocp(this, com.frojasg1.sun.nio.ch.ThreadPool.getDefault())).start();
            }
         }
      }

      return defaultIocp;
   }

   public AsynchronousChannelGroup openAsynchronousChannelGroup(int var1, ThreadFactory var2) throws IOException {
      return (new com.frojasg1.sun.nio.ch.Iocp(this, com.frojasg1.sun.nio.ch.ThreadPool.create(var1, var2))).start();
   }

   public AsynchronousChannelGroup openAsynchronousChannelGroup(ExecutorService var1, int var2) throws IOException {
      return (new com.frojasg1.sun.nio.ch.Iocp(this, ThreadPool.wrap(var1, var2))).start();
   }

   private com.frojasg1.sun.nio.ch.Iocp toIocp(AsynchronousChannelGroup var1) throws IOException {
      if (var1 == null) {
         return this.defaultIocp();
      } else if (!(var1 instanceof com.frojasg1.sun.nio.ch.Iocp)) {
         throw new IllegalChannelGroupException();
      } else {
         return (com.frojasg1.sun.nio.ch.Iocp)var1;
      }
   }

   public AsynchronousServerSocketChannel openAsynchronousServerSocketChannel(AsynchronousChannelGroup var1) throws IOException {
      return new com.frojasg1.sun.nio.ch.WindowsAsynchronousServerSocketChannelImpl(this.toIocp(var1));
   }

   public AsynchronousSocketChannel openAsynchronousSocketChannel(AsynchronousChannelGroup var1) throws IOException {
      return new com.frojasg1.sun.nio.ch.WindowsAsynchronousSocketChannelImpl(this.toIocp(var1));
   }
}
