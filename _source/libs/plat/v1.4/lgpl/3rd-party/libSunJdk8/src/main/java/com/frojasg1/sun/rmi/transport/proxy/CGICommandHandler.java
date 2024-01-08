package com.frojasg1.sun.rmi.transport.proxy;

import com.frojasg1.sun.rmi.transport.proxy.CGIClientException;
import com.frojasg1.sun.rmi.transport.proxy.CGIServerException;

interface CGICommandHandler {
   String getName();

   void execute(String var1) throws com.frojasg1.sun.rmi.transport.proxy.CGIClientException, com.frojasg1.sun.rmi.transport.proxy.CGIServerException;
}
