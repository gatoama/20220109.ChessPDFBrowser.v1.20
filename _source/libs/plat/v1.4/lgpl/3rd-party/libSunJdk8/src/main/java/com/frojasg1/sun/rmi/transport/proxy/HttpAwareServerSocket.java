package com.frojasg1.sun.rmi.transport.proxy;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import com.frojasg1.sun.rmi.runtime.Log;
import com.frojasg1.sun.rmi.transport.proxy.HttpReceiveSocket;
import com.frojasg1.sun.rmi.transport.proxy.RMIMasterSocketFactory;
import com.frojasg1.sun.rmi.transport.proxy.WrappedSocket;

class HttpAwareServerSocket extends ServerSocket {
   public HttpAwareServerSocket(int var1) throws IOException {
      super(var1);
   }

   public HttpAwareServerSocket(int var1, int var2) throws IOException {
      super(var1, var2);
   }

   public Socket accept() throws IOException {
      Socket var1 = super.accept();
      BufferedInputStream var2 = new BufferedInputStream(var1.getInputStream());
      com.frojasg1.sun.rmi.transport.proxy.RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, "socket accepted (checking for POST)");
      var2.mark(4);
      boolean var3 = var2.read() == 80 && var2.read() == 79 && var2.read() == 83 && var2.read() == 84;
      var2.reset();
      if (com.frojasg1.sun.rmi.transport.proxy.RMIMasterSocketFactory.proxyLog.isLoggable(Log.BRIEF)) {
         RMIMasterSocketFactory.proxyLog.log(Log.BRIEF, var3 ? "POST found, HTTP socket returned" : "POST not found, direct socket returned");
      }

      return (Socket)(var3 ? new HttpReceiveSocket(var1, var2, (OutputStream)null) : new com.frojasg1.sun.rmi.transport.proxy.WrappedSocket(var1, var2, (OutputStream)null));
   }

   public String toString() {
      return "HttpAware" + super.toString();
   }
}
