package com.frojasg1.sun.net.httpserver;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.frojasg1.sun.net.httpserver.HttpContextImpl;
import com.frojasg1.sun.net.httpserver.ServerImpl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executor;

public class HttpServerImpl extends HttpServer {
   com.frojasg1.sun.net.httpserver.ServerImpl server;

   HttpServerImpl() throws IOException {
      this(new InetSocketAddress(80), 0);
   }

   HttpServerImpl(InetSocketAddress var1, int var2) throws IOException {
      this.server = new com.frojasg1.sun.net.httpserver.ServerImpl(this, "http", var1, var2);
   }

   public void bind(InetSocketAddress var1, int var2) throws IOException {
      this.server.bind(var1, var2);
   }

   public void start() {
      this.server.start();
   }

   public void setExecutor(Executor var1) {
      this.server.setExecutor(var1);
   }

   public Executor getExecutor() {
      return this.server.getExecutor();
   }

   public void stop(int var1) {
      this.server.stop(var1);
   }

   public com.frojasg1.sun.net.httpserver.HttpContextImpl createContext(String var1, HttpHandler var2) {
      return this.server.createContext(var1, var2);
   }

   public com.frojasg1.sun.net.httpserver.HttpContextImpl createContext(String var1) {
      return this.server.createContext(var1);
   }

   public void removeContext(String var1) throws IllegalArgumentException {
      this.server.removeContext(var1);
   }

   public void removeContext(HttpContext var1) throws IllegalArgumentException {
      this.server.removeContext(var1);
   }

   public InetSocketAddress getAddress() {
      return this.server.getAddress();
   }
}
