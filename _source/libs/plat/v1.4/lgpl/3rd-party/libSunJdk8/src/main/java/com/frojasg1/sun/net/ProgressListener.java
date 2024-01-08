package com.frojasg1.sun.net;

import com.frojasg1.sun.net.ProgressEvent;

import java.util.EventListener;

public interface ProgressListener extends EventListener {
   void progressStart(com.frojasg1.sun.net.ProgressEvent var1);

   void progressUpdate(com.frojasg1.sun.net.ProgressEvent var1);

   void progressFinish(ProgressEvent var1);
}
