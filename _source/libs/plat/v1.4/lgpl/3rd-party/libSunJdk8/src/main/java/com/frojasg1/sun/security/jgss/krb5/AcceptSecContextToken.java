package com.frojasg1.sun.security.jgss.krb5;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import org.ietf.jgss.GSSException;
import com.frojasg1.sun.security.action.GetBooleanAction;
import com.frojasg1.sun.security.jgss.krb5.InitialToken;
import com.frojasg1.sun.security.jgss.krb5.Krb5Context;
import com.frojasg1.sun.security.krb5.Credentials;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbApRep;
import com.frojasg1.sun.security.krb5.KrbApReq;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.util.DerValue;

class AcceptSecContextToken extends com.frojasg1.sun.security.jgss.krb5.InitialToken {
   private KrbApRep apRep = null;

   public AcceptSecContextToken(com.frojasg1.sun.security.jgss.krb5.Krb5Context var1, KrbApReq var2) throws KrbException, IOException, GSSException {
      boolean var3 = (Boolean)AccessController.doPrivileged(new GetBooleanAction("sun.security.krb5.acceptor.subkey"));
      boolean var4 = true;
      EncryptionKey var5 = null;
      if (var3) {
         var5 = new EncryptionKey(var2.getCreds().getSessionKey());
         var1.setKey(2, var5);
      }

      this.apRep = new KrbApRep(var2, var4, var5);
      var1.resetMySequenceNumber(this.apRep.getSeqNumber());
   }

   public AcceptSecContextToken(com.frojasg1.sun.security.jgss.krb5.Krb5Context var1, Credentials var2, KrbApReq var3, InputStream var4) throws IOException, GSSException, KrbException {
      int var5 = var4.read() << 8 | var4.read();
      if (var5 != 512) {
         throw new GSSException(10, -1, "AP_REP token id does not match!");
      } else {
         byte[] var6 = (new DerValue(var4)).toByteArray();
         KrbApRep var7 = new KrbApRep(var6, var2, var3);
         EncryptionKey var8 = var7.getSubKey();
         if (var8 != null) {
            var1.setKey(2, var8);
         }

         Integer var9 = var7.getSeqNumber();
         int var10 = var9 != null ? var9 : 0;
         var1.resetPeerSequenceNumber(var10);
      }
   }

   public final byte[] encode() throws IOException {
      byte[] var1 = this.apRep.getMessage();
      byte[] var2 = new byte[2 + var1.length];
      writeInt(512, var2, 0);
      System.arraycopy(var1, 0, var2, 2, var1.length);
      return var2;
   }
}
