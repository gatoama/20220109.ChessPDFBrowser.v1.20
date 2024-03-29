package com.frojasg1.sun.security.jgss.krb5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.MessageProp;
import com.frojasg1.sun.security.jgss.krb5.Krb5Context;
import com.frojasg1.sun.security.jgss.krb5.MessageToken;

class MicToken extends com.frojasg1.sun.security.jgss.krb5.MessageToken {
   public MicToken(com.frojasg1.sun.security.jgss.krb5.Krb5Context var1, byte[] var2, int var3, int var4, MessageProp var5) throws GSSException {
      super(257, var1, var2, var3, var4, var5);
   }

   public MicToken(com.frojasg1.sun.security.jgss.krb5.Krb5Context var1, InputStream var2, MessageProp var3) throws GSSException {
      super(257, var1, var2, var3);
   }

   public void verify(byte[] var1, int var2, int var3) throws GSSException {
      if (!this.verifySignAndSeqNumber((byte[])null, var1, var2, var3, (byte[])null)) {
         throw new GSSException(6, -1, "Corrupt checksum or sequence number in MIC token");
      }
   }

   public void verify(InputStream var1) throws GSSException {
      Object var2 = null;

      byte[] var5;
      try {
         var5 = new byte[var1.available()];
         var1.read(var5);
      } catch (IOException var4) {
         throw new GSSException(6, -1, "Corrupt checksum or sequence number in MIC token");
      }

      this.verify(var5, 0, var5.length);
   }

   public MicToken(com.frojasg1.sun.security.jgss.krb5.Krb5Context var1, MessageProp var2, byte[] var3, int var4, int var5) throws GSSException {
      super(257, var1);
      if (var2 == null) {
         var2 = new MessageProp(0, false);
      }

      this.genSignAndSeqNumber(var2, (byte[])null, var3, var4, var5, (byte[])null);
   }

   public MicToken(com.frojasg1.sun.security.jgss.krb5.Krb5Context var1, MessageProp var2, InputStream var3) throws GSSException, IOException {
      super(257, var1);
      byte[] var4 = new byte[var3.available()];
      var3.read(var4);
      if (var2 == null) {
         var2 = new MessageProp(0, false);
      }

      this.genSignAndSeqNumber(var2, (byte[])null, var4, 0, var4.length, (byte[])null);
   }

   protected int getSealAlg(boolean var1, int var2) {
      return 65535;
   }

   public int encode(byte[] var1, int var2) throws IOException, GSSException {
      ByteArrayOutputStream var3 = new ByteArrayOutputStream();
      super.encode(var3);
      byte[] var4 = var3.toByteArray();
      System.arraycopy(var4, 0, var1, var2, var4.length);
      return var4.length;
   }

   public byte[] encode() throws IOException, GSSException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream(50);
      this.encode(var1);
      return var1.toByteArray();
   }
}
