package com.frojasg1.sun.rmi.transport;

import com.frojasg1.sun.rmi.transport.Connection;
import com.frojasg1.sun.rmi.transport.Endpoint;

import java.rmi.RemoteException;

public interface Channel {
   com.frojasg1.sun.rmi.transport.Connection newConnection() throws RemoteException;

   Endpoint getEndpoint();

   void free(Connection var1, boolean var2) throws RemoteException;
}
