package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.ExtendedSocketOption;
import com.frojasg1.sun.nio.ch.Net;
import com.frojasg1.sun.nio.ch.OptionKey;

import java.net.ProtocolFamily;
import java.net.SocketOption;
import java.net.StandardProtocolFamily;
import java.net.StandardSocketOptions;
import java.util.HashMap;
import java.util.Map;

class SocketOptionRegistry {
   private SocketOptionRegistry() {
   }

   public static com.frojasg1.sun.nio.ch.OptionKey findOption(SocketOption<?> var0, ProtocolFamily var1) {
      SocketOptionRegistry.RegistryKey var2 = new SocketOptionRegistry.RegistryKey(var0, var1);
      return (com.frojasg1.sun.nio.ch.OptionKey)SocketOptionRegistry.LazyInitialization.options.get(var2);
   }

   private static class LazyInitialization {
      static final Map<SocketOptionRegistry.RegistryKey, com.frojasg1.sun.nio.ch.OptionKey> options = options();

      private LazyInitialization() {
      }

      private static Map<SocketOptionRegistry.RegistryKey, com.frojasg1.sun.nio.ch.OptionKey> options() {
         HashMap var0 = new HashMap();
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_BROADCAST, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 32));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_KEEPALIVE, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 8));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_LINGER, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 128));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_SNDBUF, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 4097));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_RCVBUF, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 4098));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.SO_REUSEADDR, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 4));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.TCP_NODELAY, com.frojasg1.sun.nio.ch.Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(6, 1));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET), new com.frojasg1.sun.nio.ch.OptionKey(0, 3));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET), new com.frojasg1.sun.nio.ch.OptionKey(0, 9));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET), new com.frojasg1.sun.nio.ch.OptionKey(0, 10));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET), new com.frojasg1.sun.nio.ch.OptionKey(0, 11));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_TOS, StandardProtocolFamily.INET6), new com.frojasg1.sun.nio.ch.OptionKey(41, 39));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_IF, StandardProtocolFamily.INET6), new com.frojasg1.sun.nio.ch.OptionKey(41, 9));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_TTL, StandardProtocolFamily.INET6), new com.frojasg1.sun.nio.ch.OptionKey(41, 10));
         var0.put(new SocketOptionRegistry.RegistryKey(StandardSocketOptions.IP_MULTICAST_LOOP, StandardProtocolFamily.INET6), new com.frojasg1.sun.nio.ch.OptionKey(41, 11));
         var0.put(new SocketOptionRegistry.RegistryKey(com.frojasg1.sun.nio.ch.ExtendedSocketOption.SO_OOBINLINE, Net.UNSPEC), new com.frojasg1.sun.nio.ch.OptionKey(65535, 256));
         return var0;
      }
   }

   private static class RegistryKey {
      private final SocketOption<?> name;
      private final ProtocolFamily family;

      RegistryKey(SocketOption<?> var1, ProtocolFamily var2) {
         this.name = var1;
         this.family = var2;
      }

      public int hashCode() {
         return this.name.hashCode() + this.family.hashCode();
      }

      public boolean equals(Object var1) {
         if (var1 == null) {
            return false;
         } else if (!(var1 instanceof SocketOptionRegistry.RegistryKey)) {
            return false;
         } else {
            SocketOptionRegistry.RegistryKey var2 = (SocketOptionRegistry.RegistryKey)var1;
            if (this.name != var2.name) {
               return false;
            } else {
               return this.family == var2.family;
            }
         }
      }
   }
}
