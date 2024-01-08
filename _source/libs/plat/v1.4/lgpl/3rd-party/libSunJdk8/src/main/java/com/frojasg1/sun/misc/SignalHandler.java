package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.NativeSignalHandler;
import com.frojasg1.sun.misc.Signal;

public interface SignalHandler {
   SignalHandler SIG_DFL = new com.frojasg1.sun.misc.NativeSignalHandler(0L);
   SignalHandler SIG_IGN = new com.frojasg1.sun.misc.NativeSignalHandler(1L);

   void handle(Signal var1);
}
