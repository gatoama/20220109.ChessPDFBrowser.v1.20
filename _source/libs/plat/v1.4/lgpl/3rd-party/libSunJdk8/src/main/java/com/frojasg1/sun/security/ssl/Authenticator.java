package com.frojasg1.sun.security.ssl;

import java.util.Arrays;

class Authenticator {
   private final byte[] block;
   private static final int BLOCK_SIZE_SSL = 11;
   private static final int BLOCK_SIZE_TLS = 13;

   Authenticator() {
      this.block = new byte[0];
   }

   Authenticator(ProtocolVersion var1) {
      if (var1.v >= ProtocolVersion.TLS10.v) {
         this.block = new byte[13];
         this.block[9] = var1.major;
         this.block[10] = var1.minor;
      } else {
         this.block = new byte[11];
      }

   }

   final boolean seqNumOverflow() {
      return this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1 && this.block[2] == -1 && this.block[3] == -1 && this.block[4] == -1 && this.block[5] == -1 && this.block[6] == -1;
   }

   final boolean seqNumIsHuge() {
      return this.block.length != 0 && this.block[0] == -1 && this.block[1] == -1;
   }

   final byte[] sequenceNumber() {
      return Arrays.copyOf(this.block, 8);
   }

   final byte[] acquireAuthenticationBytes(byte var1, int var2) {
      byte[] var3 = (byte[])this.block.clone();
      if (this.block.length != 0) {
         var3[8] = var1;
         var3[var3.length - 2] = (byte)(var2 >> 8);
         var3[var3.length - 1] = (byte)var2;

         for(int var4 = 7; var4 >= 0 && ++this.block[var4] == 0; --var4) {
         }
      }

      return var3;
   }
}
