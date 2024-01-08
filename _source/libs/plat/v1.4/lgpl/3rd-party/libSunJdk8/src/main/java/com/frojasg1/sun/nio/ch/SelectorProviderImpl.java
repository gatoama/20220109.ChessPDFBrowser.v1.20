package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.DatagramChannelImpl;
import com.frojasg1.sun.nio.ch.PipeImpl;
import com.frojasg1.sun.nio.ch.ServerSocketChannelImpl;
import com.frojasg1.sun.nio.ch.SocketChannelImpl;

import java.io.IOException;
import java.net.ProtocolFamily;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.AbstractSelector;
import java.nio.channels.spi.SelectorProvider;

public abstract class SelectorProviderImpl extends SelectorProvider {
   public SelectorProviderImpl() {
   }

   public DatagramChannel openDatagramChannel() throws IOException {
      return new com.frojasg1.sun.nio.ch.DatagramChannelImpl(this);
   }

   public DatagramChannel openDatagramChannel(ProtocolFamily var1) throws IOException {
      return new com.frojasg1.sun.nio.ch.DatagramChannelImpl(this, var1);
   }

   public Pipe openPipe() throws IOException {
      return new com.frojasg1.sun.nio.ch.PipeImpl(this);
   }

   public abstract AbstractSelector openSelector() throws IOException;

   public ServerSocketChannel openServerSocketChannel() throws IOException {
      return new com.frojasg1.sun.nio.ch.ServerSocketChannelImpl(this);
   }

   public SocketChannel openSocketChannel() throws IOException {
      return new com.frojasg1.sun.nio.ch.SocketChannelImpl(this);
   }
}
