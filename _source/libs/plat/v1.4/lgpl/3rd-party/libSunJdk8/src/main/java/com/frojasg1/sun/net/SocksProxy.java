package com.frojasg1.sun.net;

import java.net.Proxy;
import java.net.SocketAddress;
import java.net.Proxy.Type;

public final class SocksProxy extends Proxy {
   private final int version;

   private SocksProxy(SocketAddress var1, int var2) {
      super(Type.SOCKS, var1);
      this.version = var2;
   }

   public static SocksProxy create(SocketAddress var0, int var1) {
      return new SocksProxy(var0, var1);
   }

   public int protocolVersion() {
      return this.version;
   }
}
