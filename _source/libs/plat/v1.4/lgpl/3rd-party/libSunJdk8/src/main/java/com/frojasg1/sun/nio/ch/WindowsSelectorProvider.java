package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.SelectorProviderImpl;
import com.frojasg1.sun.nio.ch.WindowsSelectorImpl;

import java.io.IOException;
import java.nio.channels.spi.AbstractSelector;

public class WindowsSelectorProvider extends SelectorProviderImpl {
   public WindowsSelectorProvider() {
   }

   public AbstractSelector openSelector() throws IOException {
      return new com.frojasg1.sun.nio.ch.WindowsSelectorImpl(this);
   }
}
