package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.x509.CRLNumberExtension;
import com.frojasg1.sun.security.x509.PKIXExtensions;

public class DeltaCRLIndicatorExtension extends CRLNumberExtension {
   public static final String NAME = "DeltaCRLIndicator";
   private static final String LABEL = "Base CRL Number";

   public DeltaCRLIndicatorExtension(int var1) throws IOException {
      super(com.frojasg1.sun.security.x509.PKIXExtensions.DeltaCRLIndicator_Id, true, BigInteger.valueOf((long)var1), "DeltaCRLIndicator", "Base CRL Number");
   }

   public DeltaCRLIndicatorExtension(BigInteger var1) throws IOException {
      super(com.frojasg1.sun.security.x509.PKIXExtensions.DeltaCRLIndicator_Id, true, var1, "DeltaCRLIndicator", "Base CRL Number");
   }

   public DeltaCRLIndicatorExtension(Boolean var1, Object var2) throws IOException {
      super(com.frojasg1.sun.security.x509.PKIXExtensions.DeltaCRLIndicator_Id, var1, var2, "DeltaCRLIndicator", "Base CRL Number");
   }

   public void encode(OutputStream var1) throws IOException {
      new DerOutputStream();
      super.encode(var1, PKIXExtensions.DeltaCRLIndicator_Id, true);
   }
}
