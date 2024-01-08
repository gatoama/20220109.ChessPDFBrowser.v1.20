package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLProtocolException;

final class ServerNameExtension extends HelloExtension {
   static final int NAME_HEADER_LENGTH = 3;
   private Map<Integer, SNIServerName> sniMap;
   private int listLength;

   ServerNameExtension() throws IOException {
      super(ExtensionType.EXT_SERVER_NAME);
      this.listLength = 0;
      this.sniMap = Collections.emptyMap();
   }

   ServerNameExtension(List<SNIServerName> var1) throws IOException {
      super(ExtensionType.EXT_SERVER_NAME);
      this.listLength = 0;
      this.sniMap = new LinkedHashMap();

      SNIServerName var3;
      for(Iterator var2 = var1.iterator(); var2.hasNext(); this.listLength += var3.getEncoded().length + 3) {
         var3 = (SNIServerName)var2.next();
         if (this.sniMap.put(var3.getType(), var3) != null) {
            throw new RuntimeException("Duplicated server name of type " + var3.getType());
         }
      }

      if (this.listLength == 0) {
         throw new RuntimeException("The ServerNameList cannot be empty");
      }
   }

   ServerNameExtension(HandshakeInStream var1, int var2) throws IOException {
      super(ExtensionType.EXT_SERVER_NAME);
      int var3 = var2;
      if (var2 >= 2) {
         this.listLength = var1.getInt16();
         if (this.listLength == 0 || this.listLength + 2 != var2) {
            throw new SSLProtocolException("Invalid " + this.type + " extension");
         }

         var3 = var2 - 2;

         byte[] var5;
         for(this.sniMap = new LinkedHashMap(); var3 > 0; var3 -= var5.length + 3) {
            int var4 = var1.getInt8();
            var5 = var1.getBytes16();
            SNIServerName var6;
            SSLProtocolException var8;
            switch(var4) {
            case 0:
               if (var5.length == 0) {
                  throw new SSLProtocolException("Empty HostName in server name indication");
               }

               try {
                  var6 = new SNIHostName(var5);
                  break;
               } catch (IllegalArgumentException var10) {
                  var8 = new SSLProtocolException("Illegal server name, type=host_name(" + var4 + "), name=" + new String(var5, StandardCharsets.UTF_8) + ", value=" + Debug.toString(var5));
                  var8.initCause(var10);
                  throw var8;
               }
            default:
               try {
                  var6 = new ServerNameExtension.UnknownServerName(var4, var5);
               } catch (IllegalArgumentException var9) {
                  var8 = new SSLProtocolException("Illegal server name, type=(" + var4 + "), value=" + Debug.toString(var5));
                  var8.initCause(var9);
                  throw var8;
               }
            }

            if (this.sniMap.put(((SNIServerName)var6).getType(), var6) != null) {
               throw new SSLProtocolException("Duplicated server name of type " + ((SNIServerName)var6).getType());
            }
         }
      } else if (var2 == 0) {
         this.listLength = 0;
         this.sniMap = Collections.emptyMap();
      }

      if (var3 != 0) {
         throw new SSLProtocolException("Invalid server_name extension");
      }
   }

   List<SNIServerName> getServerNames() {
      return this.sniMap != null && !this.sniMap.isEmpty() ? Collections.unmodifiableList(new ArrayList(this.sniMap.values())) : Collections.emptyList();
   }

   boolean isMatched(Collection<SNIMatcher> var1) {
      if (this.sniMap != null && !this.sniMap.isEmpty()) {
         Iterator var2 = var1.iterator();

         while(var2.hasNext()) {
            SNIMatcher var3 = (SNIMatcher)var2.next();
            SNIServerName var4 = (SNIServerName)this.sniMap.get(var3.getType());
            if (var4 != null && !var3.matches(var4)) {
               return false;
            }
         }
      }

      return true;
   }

   boolean isIdentical(List<SNIServerName> var1) {
      if (var1.size() != this.sniMap.size()) {
         return false;
      } else {
         Iterator var2 = var1.iterator();

         SNIServerName var3;
         SNIServerName var4;
         do {
            if (!var2.hasNext()) {
               return true;
            }

            var3 = (SNIServerName)var2.next();
            var4 = (SNIServerName)this.sniMap.get(var3.getType());
         } while(var4 != null && var3.equals(var4));

         return false;
      }
   }

   int length() {
      return this.listLength == 0 ? 4 : 6 + this.listLength;
   }

   void send(HandshakeOutStream var1) throws IOException {
      var1.putInt16(this.type.id);
      if (this.listLength == 0) {
         var1.putInt16(this.listLength);
      } else {
         var1.putInt16(this.listLength + 2);
         var1.putInt16(this.listLength);
         Iterator var2 = this.sniMap.values().iterator();

         while(var2.hasNext()) {
            SNIServerName var3 = (SNIServerName)var2.next();
            var1.putInt8(var3.getType());
            var1.putBytes16(var3.getEncoded());
         }
      }

   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      Iterator var2 = this.sniMap.values().iterator();

      while(var2.hasNext()) {
         SNIServerName var3 = (SNIServerName)var2.next();
         var1.append("[" + var3 + "]");
      }

      return "Extension " + this.type + ", server_name: " + var1;
   }

   private static class UnknownServerName extends SNIServerName {
      UnknownServerName(int var1, byte[] var2) {
         super(var1, var2);
      }
   }
}
