package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.PendingFuture;

interface Cancellable {
   void onCancel(com.frojasg1.sun.nio.ch.PendingFuture<?, ?> var1);
}
