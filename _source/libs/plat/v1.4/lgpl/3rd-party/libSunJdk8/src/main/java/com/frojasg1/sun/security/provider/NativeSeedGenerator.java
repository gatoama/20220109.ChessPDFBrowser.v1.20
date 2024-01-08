package com.frojasg1.sun.security.provider;

import com.frojasg1.sun.security.provider.SeedGenerator;

import java.io.IOException;

class NativeSeedGenerator extends com.frojasg1.sun.security.provider.SeedGenerator {
   NativeSeedGenerator(String var1) throws IOException {
      if (!nativeGenerateSeed(new byte[2])) {
         throw new IOException("Required native CryptoAPI features not  available on this machine");
      }
   }

   private static native boolean nativeGenerateSeed(byte[] var0);

   void getSeedBytes(byte[] var1) {
      if (!nativeGenerateSeed(var1)) {
         throw new InternalError("Unexpected CryptoAPI failure generating seed");
      }
   }
}
