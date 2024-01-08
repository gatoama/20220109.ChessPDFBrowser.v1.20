package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import javax.net.ssl.SSLProtocolException;

final class ExtendedMasterSecretExtension extends HelloExtension {
   ExtendedMasterSecretExtension() {
      super(ExtensionType.EXT_EXTENDED_MASTER_SECRET);
   }

   ExtendedMasterSecretExtension(HandshakeInStream var1, int var2) throws IOException {
      super(ExtensionType.EXT_EXTENDED_MASTER_SECRET);
      if (var2 != 0) {
         throw new SSLProtocolException("Invalid " + this.type + " extension");
      }
   }

   int length() {
      return 4;
   }

   void send(HandshakeOutStream var1) throws IOException {
      var1.putInt16(this.type.id);
      var1.putInt16(0);
   }

   public String toString() {
      return "Extension " + this.type;
   }
}
