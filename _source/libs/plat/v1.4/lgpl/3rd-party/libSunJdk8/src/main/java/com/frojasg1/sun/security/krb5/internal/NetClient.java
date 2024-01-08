package com.frojasg1.sun.security.krb5.internal;

import com.frojasg1.sun.security.krb5.internal.TCPClient;
import com.frojasg1.sun.security.krb5.internal.UDPClient;

import java.io.IOException;

public abstract class NetClient implements AutoCloseable {
   public NetClient() {
   }

   public static NetClient getInstance(String var0, String var1, int var2, int var3) throws IOException {
      return (NetClient)(var0.equals("TCP") ? new com.frojasg1.sun.security.krb5.internal.TCPClient(var1, var2, var3) : new com.frojasg1.sun.security.krb5.internal.UDPClient(var1, var2, var3));
   }

   public abstract void send(byte[] var1) throws IOException;

   public abstract byte[] receive() throws IOException;

   public abstract void close() throws IOException;
}
