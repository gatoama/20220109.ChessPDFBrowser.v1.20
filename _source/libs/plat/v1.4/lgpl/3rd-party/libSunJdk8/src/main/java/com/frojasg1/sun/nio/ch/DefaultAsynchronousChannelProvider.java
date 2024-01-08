package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.WindowsAsynchronousChannelProvider;

import java.nio.channels.spi.AsynchronousChannelProvider;

public class DefaultAsynchronousChannelProvider {
   private DefaultAsynchronousChannelProvider() {
   }

   public static AsynchronousChannelProvider create() {
      return new WindowsAsynchronousChannelProvider();
   }
}
