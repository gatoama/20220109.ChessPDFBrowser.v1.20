package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import javax.crypto.BadPaddingException;
import javax.net.ssl.SSLException;
import com.frojasg1.sun.misc.HexDumpEncoder;

final class EngineInputRecord extends InputRecord {
   private SSLEngineImpl engine;
   private static ByteBuffer tmpBB = ByteBuffer.allocate(0);
   private boolean internalData;

   EngineInputRecord(SSLEngineImpl var1) {
      this.engine = var1;
   }

   byte contentType() {
      return this.internalData ? super.contentType() : 23;
   }

   int bytesInCompletePacket(ByteBuffer var1) throws SSLException {
      if (var1.remaining() < 5) {
         return -1;
      } else {
         int var2 = var1.position();
         byte var3 = var1.get(var2);
         boolean var4 = false;
         int var8;
         if (!this.formatVerified && var3 != 22 && var3 != 21) {
            boolean var9 = (var3 & 128) != 0;
            if (!var9 || var1.get(var2 + 2) != 1 && var1.get(var2 + 2) != 4) {
               throw new SSLException("Unrecognized SSL message, plaintext connection?");
            }

            ProtocolVersion var6 = ProtocolVersion.valueOf(var1.get(var2 + 3), var1.get(var2 + 4));
            checkRecordVersion(var6, true);
            int var7 = var9 ? 127 : 63;
            var8 = ((var3 & var7) << 8) + (var1.get(var2 + 1) & 255) + (var9 ? 2 : 3);
         } else {
            ProtocolVersion var5 = ProtocolVersion.valueOf(var1.get(var2 + 1), var1.get(var2 + 2));
            checkRecordVersion(var5, false);
            this.formatVerified = true;
            var8 = ((var1.get(var2 + 3) & 255) << 8) + (var1.get(var2 + 4) & 255) + 5;
         }

         return var8;
      }
   }

   ByteBuffer decrypt(Authenticator var1, CipherBox var2, ByteBuffer var3) throws BadPaddingException {
      if (this.internalData) {
         this.decrypt(var1, var2);
         return tmpBB;
      } else {
         BadPaddingException var4 = null;
         int var5 = var1 instanceof MAC ? ((MAC)var1).MAClen() : 0;
         int var6 = var3.remaining();
         if (!var2.isNullCipher()) {
            try {
               int var7 = var2.applyExplicitNonce(var1, this.contentType(), var3);
               if (var2.isAEADMode()) {
                  var3.position(var3.position() + var7);
               }

               var2.decrypt(var3, var5);
               var3.position(var7);
            } catch (BadPaddingException var10) {
               var4 = var10;
            }
         }

         if (var1 instanceof MAC && var5 != 0) {
            MAC var11 = (MAC)var1;
            int var8 = var3.limit() - var5;
            if (var3.remaining() < var5) {
               if (var4 == null) {
                  var4 = new BadPaddingException("bad record");
               }

               var8 = var6 - var5;
               var3.limit(var6);
            }

            if (checkMacTags(this.contentType(), var3, var11, false) && var4 == null) {
               var4 = new BadPaddingException("bad record MAC");
            }

            if (var2.isCBCMode()) {
               int var9 = calculateRemainingLen(var11, var6, var8);
               if (var9 > this.buf.length) {
                  throw new RuntimeException("Internal buffer capacity error");
               }

               checkMacTags(this.contentType(), this.buf, 0, var9, var11, true);
            }

            var3.limit(var8);
         }

         if (var4 != null) {
            throw var4;
         } else {
            return var3.slice();
         }
      }
   }

   private static boolean checkMacTags(byte var0, ByteBuffer var1, MAC var2, boolean var3) {
      int var4 = var1.position();
      int var5 = var2.MAClen();
      int var6 = var1.limit();
      int var7 = var6 - var5;
      var1.limit(var7);
      byte[] var8 = var2.compute(var0, var1, var3);
      if (var8 != null && var5 == var8.length) {
         var1.position(var7);
         var1.limit(var6);

         boolean var10;
         try {
            int[] var9 = compareMacTags(var1, var8);
            var10 = var9[0] != 0;
         } finally {
            var1.position(var4);
            var1.limit(var7);
         }

         return var10;
      } else {
         throw new RuntimeException("Internal MAC error");
      }
   }

   private static int[] compareMacTags(ByteBuffer var0, byte[] var1) {
      int[] var2 = new int[]{0, 0};

      for(int var3 = 0; var3 < var1.length; ++var3) {
         int var10002;
         if (var0.get() != var1[var3]) {
            var10002 = var2[0]++;
         } else {
            var10002 = var2[1]++;
         }
      }

      return var2;
   }

   void writeBuffer(OutputStream var1, byte[] var2, int var3, int var4) throws IOException {
      ByteBuffer var5 = (ByteBuffer)((ByteBuffer)ByteBuffer.allocate(var4).put(var2, 0, var4).flip());
      this.engine.writer.putOutboundDataSync(var5);
   }

   ByteBuffer read(ByteBuffer var1) throws IOException {
      if (this.formatVerified && var1.get(var1.position()) == 23) {
         this.internalData = false;
         int var2 = var1.position();
         int var3 = var1.limit();
         ProtocolVersion var4 = ProtocolVersion.valueOf(var1.get(var2 + 1), var1.get(var2 + 2));
         checkRecordVersion(var4, false);
         int var5 = this.bytesInCompletePacket(var1);

         assert var5 > 0;

         if (debug != null && Debug.isOn("packet")) {
            try {
               HexDumpEncoder var6 = new HexDumpEncoder();
               ByteBuffer var7 = var1.duplicate();
               var7.limit(var2 + var5);
               System.out.println("[Raw read (bb)]: length = " + var5);
               var6.encodeBuffer(var7, System.out);
            } catch (IOException var8) {
            }
         }

         var1.position(var2 + 5);
         var1.limit(var2 + var5);
         ByteBuffer var9 = var1.slice();
         var1.position(var1.limit());
         var1.limit(var3);
         return var9;
      } else {
         this.internalData = true;
         this.read(new ByteBufferInputStream(var1), (OutputStream)null);
         return tmpBB;
      }
   }
}
