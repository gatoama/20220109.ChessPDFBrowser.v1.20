package com.frojasg1.sun.rmi.transport.proxy;

import com.frojasg1.sun.rmi.transport.proxy.HttpAwareServerSocket;
import com.frojasg1.sun.rmi.transport.proxy.HttpSendSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.rmi.server.RMISocketFactory;

public class RMIHttpToCGISocketFactory extends RMISocketFactory {
   public RMIHttpToCGISocketFactory() {
   }

   public Socket createSocket(String var1, int var2) throws IOException {
      return new com.frojasg1.sun.rmi.transport.proxy.HttpSendSocket(var1, var2, new URL("http", var1, "/cgi-bin/java-rmi.cgi?forward=" + var2));
   }

   public ServerSocket createServerSocket(int var1) throws IOException {
      return new com.frojasg1.sun.rmi.transport.proxy.HttpAwareServerSocket(var1);
   }
}
