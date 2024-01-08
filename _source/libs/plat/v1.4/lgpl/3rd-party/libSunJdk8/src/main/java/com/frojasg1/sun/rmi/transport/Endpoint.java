package com.frojasg1.sun.rmi.transport;

import com.frojasg1.sun.rmi.transport.Channel;
import com.frojasg1.sun.rmi.transport.Target;
import com.frojasg1.sun.rmi.transport.Transport;

import java.rmi.RemoteException;

public interface Endpoint {
   Channel getChannel();

   void exportObject(Target var1) throws RemoteException;

   com.frojasg1.sun.rmi.transport.Transport getInboundTransport();

   Transport getOutboundTransport();
}
