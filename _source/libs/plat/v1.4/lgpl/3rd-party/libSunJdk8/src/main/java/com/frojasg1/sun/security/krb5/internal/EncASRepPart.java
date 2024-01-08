package com.frojasg1.sun.security.krb5.internal;

import java.io.IOException;
import com.frojasg1.sun.security.krb5.Asn1Exception;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.internal.EncKDCRepPart;
import com.frojasg1.sun.security.krb5.internal.HostAddresses;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.LastReq;
import com.frojasg1.sun.security.krb5.internal.TicketFlags;
import com.frojasg1.sun.security.util.DerValue;

public class EncASRepPart extends EncKDCRepPart {
   public EncASRepPart(EncryptionKey var1, LastReq var2, int var3, com.frojasg1.sun.security.krb5.internal.KerberosTime var4, TicketFlags var5, com.frojasg1.sun.security.krb5.internal.KerberosTime var6, com.frojasg1.sun.security.krb5.internal.KerberosTime var7, com.frojasg1.sun.security.krb5.internal.KerberosTime var8, KerberosTime var9, PrincipalName var10, HostAddresses var11) {
      super(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, 25);
   }

   public EncASRepPart(byte[] var1) throws Asn1Exception, IOException, KrbException {
      this.init(new DerValue(var1));
   }

   public EncASRepPart(DerValue var1) throws Asn1Exception, IOException, KrbException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, KrbException {
      this.init(var1, 25);
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      return this.asn1Encode(25);
   }
}
