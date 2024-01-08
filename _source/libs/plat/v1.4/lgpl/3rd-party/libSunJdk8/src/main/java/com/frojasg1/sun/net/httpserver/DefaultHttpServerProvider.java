package com.frojasg1.sun.net.httpserver;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsServer;
import com.sun.net.httpserver.spi.HttpServerProvider;
import com.frojasg1.sun.net.httpserver.HttpServerImpl;
import com.frojasg1.sun.net.httpserver.HttpsServerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DefaultHttpServerProvider extends HttpServerProvider {
   public DefaultHttpServerProvider() {
   }

   public HttpServer createHttpServer(InetSocketAddress var1, int var2) throws IOException {
      return new HttpServerImpl(var1, var2);
   }

   public HttpsServer createHttpsServer(InetSocketAddress var1, int var2) throws IOException {
      return new HttpsServerImpl(var1, var2);
   }
}
