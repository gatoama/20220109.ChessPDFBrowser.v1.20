package com.frojasg1.sun.security.krb5;

import java.io.IOException;

import com.frojasg1.sun.security.krb5.Asn1Exception;
import com.frojasg1.sun.security.krb5.Config;
import com.frojasg1.sun.security.krb5.EncryptedData;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.RealmException;
import com.frojasg1.sun.security.krb5.internal.ASReq;
import com.frojasg1.sun.security.krb5.internal.HostAddresses;
import com.frojasg1.sun.security.krb5.internal.KDCOptions;
import com.frojasg1.sun.security.krb5.internal.KDCReqBody;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.PAData;
import com.frojasg1.sun.security.krb5.internal.PAEncTSEnc;
import com.frojasg1.sun.security.krb5.internal.Ticket;
import com.frojasg1.sun.security.krb5.internal.crypto.Nonce;

public class KrbAsReq {
   private ASReq asReqMessg;
   private boolean DEBUG;

   public KrbAsReq(EncryptionKey var1, KDCOptions var2, com.frojasg1.sun.security.krb5.PrincipalName var3, com.frojasg1.sun.security.krb5.PrincipalName var4, KerberosTime var5, KerberosTime var6, KerberosTime var7, int[] var8, HostAddresses var9) throws com.frojasg1.sun.security.krb5.KrbException, IOException {
      this.DEBUG = Krb5.DEBUG;
      if (var2 == null) {
         var2 = new KDCOptions();
      }

      if (!var2.get(2) && !var2.get(4) && !var2.get(28) && !var2.get(30) && !var2.get(31)) {
         if (!var2.get(6) && var5 != null) {
            var5 = null;
         }

         if (!var2.get(8) && var7 != null) {
            var7 = null;
         }

         PAData[] var10 = null;
         if (var1 != null) {
            PAEncTSEnc var11 = new PAEncTSEnc();
            byte[] var12 = var11.asn1Encode();
            com.frojasg1.sun.security.krb5.EncryptedData var13 = new com.frojasg1.sun.security.krb5.EncryptedData(var1, var12, 1);
            var10 = new PAData[]{new PAData(2, var13.asn1Encode())};
         }

         if (var3.getRealm() == null) {
            throw new RealmException(601, "default realm not specified ");
         } else {
            if (this.DEBUG) {
               System.out.println(">>> KrbAsReq creating message");
            }

            if (var9 == null && Config.getInstance().useAddresses()) {
               var9 = HostAddresses.getLocalAddresses();
            }

            if (var4 == null) {
               String var14 = var3.getRealmAsString();
               var4 = PrincipalName.tgsService(var14, var14);
            }

            if (var6 == null) {
               var6 = new KerberosTime(0L);
            }

            KDCReqBody var15 = new KDCReqBody(var2, var3, var4, var5, var6, var7, Nonce.value(), var8, var9, (EncryptedData)null, (Ticket[])null);
            this.asReqMessg = new ASReq(var10, var15);
         }
      } else {
         throw new KrbException(101);
      }
   }

   byte[] encoding() throws IOException, Asn1Exception {
      return this.asReqMessg.asn1Encode();
   }

   ASReq getMessage() {
      return this.asReqMessg;
   }
}
