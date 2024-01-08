package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.WindowsSelectorProvider;

import java.nio.channels.spi.SelectorProvider;

public class DefaultSelectorProvider {
   private DefaultSelectorProvider() {
   }

   public static SelectorProvider create() {
      return new WindowsSelectorProvider();
   }
}
