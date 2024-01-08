package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.misc.Cleaner;

public interface DirectBuffer {
   long address();

   Object attachment();

   Cleaner cleaner();
}
