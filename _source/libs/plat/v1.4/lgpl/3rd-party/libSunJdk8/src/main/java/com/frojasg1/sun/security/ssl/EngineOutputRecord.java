package com.frojasg1.sun.security.ssl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

final class EngineOutputRecord extends OutputRecord {
   private SSLEngineImpl engine;
   private EngineWriter writer;
   private boolean finishedMsg = false;

   EngineOutputRecord(byte var1, SSLEngineImpl var2) {
      super(var1, recordSize(var1));
      this.engine = var2;
      this.writer = var2.writer;
   }

   private static int recordSize(byte var0) {
      switch(var0) {
      case 20:
      case 21:
         return 539;
      case 22:
         return 16921;
      case 23:
         return 0;
      default:
         throw new RuntimeException("Unknown record type: " + var0);
      }
   }

   void setFinishedMsg() {
      this.finishedMsg = true;
   }

   public void flush() throws IOException {
      this.finishedMsg = false;
   }

   boolean isFinishedMsg() {
      return this.finishedMsg;
   }

   void writeBuffer(OutputStream var1, byte[] var2, int var3, int var4, int var5) throws IOException {
      ByteBuffer var6 = (ByteBuffer)ByteBuffer.allocate(var4).put(var2, var3, var4).flip();
      this.writer.putOutboundData(var6);
   }

   void write(Authenticator var1, CipherBox var2) throws IOException {
      switch(this.contentType()) {
      case 20:
      case 21:
      case 22:
         if (!this.isEmpty()) {
            this.encrypt(var1, var2);
            this.write((OutputStream)null, false, (ByteArrayOutputStream)null);
         }

         return;
      default:
         throw new RuntimeException("unexpected byte buffers");
      }
   }

   void write(EngineArgs var1, Authenticator var2, CipherBox var3) throws IOException {
      assert this.contentType() == 23;

      if (var2 != MAC.NULL) {
         if (var1.getAppRemaining() != 0) {
            int var4;
            if (this.engine.needToSplitPayload(var3, this.protocolVersion)) {
               this.write(var1, var2, var3, 1);
               var1.resetLim();
               var4 = Math.min(var1.getAppRemaining(), 15846);
            } else {
               var4 = Math.min(var1.getAppRemaining(), 16384);
            }

            if (var4 > 0) {
               this.write(var1, var2, var3, var4);
            }

         }
      }
   }

   void write(EngineArgs var1, Authenticator var2, CipherBox var3, int var4) throws IOException {
      ByteBuffer var5 = var1.netData;
      int var6 = var5.position();
      int var7 = var5.limit();
      int var8 = var6 + 5 + var3.getExplicitNonceSize();
      var5.position(var8);
      var1.gather(var4);
      var5.limit(var5.position());
      var5.position(var8);
      if (var2 instanceof MAC) {
         MAC var9 = (MAC)var2;
         if (var9.MAClen() != 0) {
            byte[] var10 = var9.compute(this.contentType(), var5, false);
            var5.limit(var5.limit() + var10.length);
            var5.put(var10);
            var5.limit(var5.position());
            var5.position(var8);
         }
      }

      if (!var3.isNullCipher()) {
         if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && (var3.isCBCMode() || var3.isAEADMode())) {
            byte[] var11 = var3.createExplicitNonce(var2, this.contentType(), var5.remaining());
            var5.position(var6 + 5);
            var5.put(var11);
            if (!var3.isAEADMode()) {
               var5.position(var6 + 5);
            }
         }

         var3.encrypt(var5, var7);
         if (debug != null && (Debug.isOn("record") || Debug.isOn("handshake") && this.contentType() == 20)) {
            System.out.println(Thread.currentThread().getName() + ", WRITE: " + this.protocolVersion + " " + InputRecord.contentName(this.contentType()) + ", length = " + var4);
         }
      } else {
         var5.position(var5.limit());
      }

      int var12 = var5.limit() - var6 - 5;
      var5.put(var6, this.contentType());
      var5.put(var6 + 1, this.protocolVersion.major);
      var5.put(var6 + 2, this.protocolVersion.minor);
      var5.put(var6 + 3, (byte)(var12 >> 8));
      var5.put(var6 + 4, (byte)var12);
      var5.limit(var7);
   }
}
