package com.frojasg1.sun.net.spi.nameservice;

import java.net.InetAddress;
import java.net.UnknownHostException;

public interface NameService {
   InetAddress[] lookupAllHostAddr(String var1) throws UnknownHostException;

   String getHostByAddr(byte[] var1) throws UnknownHostException;
}
