package com.frojasg1.sun.rmi.transport.proxy;

import com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler;
import com.frojasg1.sun.rmi.transport.proxy.CGIHandler;

final class CGIGethostnameCommand implements com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler {
   CGIGethostnameCommand() {
   }

   public String getName() {
      return "gethostname";
   }

   public void execute(String var1) {
      System.out.println("Status: 200 OK");
      System.out.println("Content-type: application/octet-stream");
      System.out.println("Content-length: " + com.frojasg1.sun.rmi.transport.proxy.CGIHandler.ServerName.length());
      System.out.println("");
      System.out.print(CGIHandler.ServerName);
      System.out.flush();
   }
}
