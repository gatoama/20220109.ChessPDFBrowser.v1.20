package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import com.frojasg1.sun.security.util.DerInputStream;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.CertificatePolicyId;

public class CertificatePolicySet {
   private final Vector<com.frojasg1.sun.security.x509.CertificatePolicyId> ids;

   public CertificatePolicySet(Vector<com.frojasg1.sun.security.x509.CertificatePolicyId> var1) {
      this.ids = var1;
   }

   public CertificatePolicySet(DerInputStream var1) throws IOException {
      this.ids = new Vector();
      DerValue[] var2 = var1.getSequence(5);

      for(int var3 = 0; var3 < var2.length; ++var3) {
         com.frojasg1.sun.security.x509.CertificatePolicyId var4 = new com.frojasg1.sun.security.x509.CertificatePolicyId(var2[var3]);
         this.ids.addElement(var4);
      }

   }

   public String toString() {
      String var1 = "CertificatePolicySet:[\n" + this.ids.toString() + "]\n";
      return var1;
   }

   public void encode(DerOutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();

      for(int var3 = 0; var3 < this.ids.size(); ++var3) {
         ((com.frojasg1.sun.security.x509.CertificatePolicyId)this.ids.elementAt(var3)).encode(var2);
      }

      var1.write((byte)48, (DerOutputStream)var2);
   }

   public List<CertificatePolicyId> getCertPolicyIds() {
      return Collections.unmodifiableList(this.ids);
   }
}
