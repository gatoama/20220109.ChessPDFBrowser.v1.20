package com.frojasg1.sun.net.httpserver;

import com.frojasg1.sun.net.httpserver.Event;
import com.frojasg1.sun.net.httpserver.ExchangeImpl;

class WriteFinishedEvent extends com.frojasg1.sun.net.httpserver.Event {
   WriteFinishedEvent(com.frojasg1.sun.net.httpserver.ExchangeImpl var1) {
      super(var1);

      assert !var1.writefinished;

      var1.writefinished = true;
   }
}
