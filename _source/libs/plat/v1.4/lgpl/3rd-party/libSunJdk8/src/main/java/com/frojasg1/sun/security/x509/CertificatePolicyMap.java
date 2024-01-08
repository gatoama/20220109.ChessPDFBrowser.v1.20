package com.frojasg1.sun.security.x509;

import java.io.IOException;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.CertificatePolicyId;

public class CertificatePolicyMap {
   private com.frojasg1.sun.security.x509.CertificatePolicyId issuerDomain;
   private com.frojasg1.sun.security.x509.CertificatePolicyId subjectDomain;

   public CertificatePolicyMap(com.frojasg1.sun.security.x509.CertificatePolicyId var1, com.frojasg1.sun.security.x509.CertificatePolicyId var2) {
      this.issuerDomain = var1;
      this.subjectDomain = var2;
   }

   public CertificatePolicyMap(DerValue var1) throws IOException {
      if (var1.tag != 48) {
         throw new IOException("Invalid encoding for CertificatePolicyMap");
      } else {
         this.issuerDomain = new com.frojasg1.sun.security.x509.CertificatePolicyId(var1.data.getDerValue());
         this.subjectDomain = new com.frojasg1.sun.security.x509.CertificatePolicyId(var1.data.getDerValue());
      }
   }

   public com.frojasg1.sun.security.x509.CertificatePolicyId getIssuerIdentifier() {
      return this.issuerDomain;
   }

   public CertificatePolicyId getSubjectIdentifier() {
      return this.subjectDomain;
   }

   public String toString() {
      String var1 = "CertificatePolicyMap: [\nIssuerDomain:" + this.issuerDomain.toString() + "SubjectDomain:" + this.subjectDomain.toString() + "]\n";
      return var1;
   }

   public void encode(DerOutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      this.issuerDomain.encode(var2);
      this.subjectDomain.encode(var2);
      var1.write((byte)48, (DerOutputStream)var2);
   }
}
