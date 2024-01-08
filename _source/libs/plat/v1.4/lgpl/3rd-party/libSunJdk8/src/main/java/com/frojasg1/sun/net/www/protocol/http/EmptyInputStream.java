package com.frojasg1.sun.net.www.protocol.http;

import java.io.InputStream;

class EmptyInputStream extends InputStream {
   EmptyInputStream() {
   }

   public int available() {
      return 0;
   }

   public int read() {
      return -1;
   }
}
