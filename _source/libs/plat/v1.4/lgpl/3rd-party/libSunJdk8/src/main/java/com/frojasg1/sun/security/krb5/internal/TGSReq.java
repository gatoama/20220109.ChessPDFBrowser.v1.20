package com.frojasg1.sun.security.krb5.internal;

import java.io.IOException;
import com.frojasg1.sun.security.krb5.Asn1Exception;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.internal.KDCReq;
import com.frojasg1.sun.security.krb5.internal.KDCReqBody;
import com.frojasg1.sun.security.krb5.internal.PAData;
import com.frojasg1.sun.security.util.DerValue;

public class TGSReq extends KDCReq {
   public TGSReq(PAData[] var1, KDCReqBody var2) throws IOException {
      super(var1, var2, 12);
   }

   public TGSReq(byte[] var1) throws Asn1Exception, IOException, KrbException {
      this.init(new DerValue(var1));
   }

   public TGSReq(DerValue var1) throws Asn1Exception, IOException, KrbException {
      this.init(var1);
   }

   private void init(DerValue var1) throws Asn1Exception, IOException, KrbException {
      this.init(var1, 12);
   }
}
