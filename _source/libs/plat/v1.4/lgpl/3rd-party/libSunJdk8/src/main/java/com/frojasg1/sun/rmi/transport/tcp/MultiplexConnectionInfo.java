package com.frojasg1.sun.rmi.transport.tcp;

import com.frojasg1.sun.rmi.transport.tcp.MultiplexInputStream;
import com.frojasg1.sun.rmi.transport.tcp.MultiplexOutputStream;

class MultiplexConnectionInfo {
   int id;
   com.frojasg1.sun.rmi.transport.tcp.MultiplexInputStream in = null;
   com.frojasg1.sun.rmi.transport.tcp.MultiplexOutputStream out = null;
   boolean closed = false;

   MultiplexConnectionInfo(int var1) {
      this.id = var1;
   }
}
