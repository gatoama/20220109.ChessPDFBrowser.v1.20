package com.frojasg1.sun.rmi.transport.proxy;

import com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler;

final class CGIPingCommand implements com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler {
   CGIPingCommand() {
   }

   public String getName() {
      return "ping";
   }

   public void execute(String var1) {
      System.out.println("Status: 200 OK");
      System.out.println("Content-type: application/octet-stream");
      System.out.println("Content-length: 0");
      System.out.println("");
   }
}
