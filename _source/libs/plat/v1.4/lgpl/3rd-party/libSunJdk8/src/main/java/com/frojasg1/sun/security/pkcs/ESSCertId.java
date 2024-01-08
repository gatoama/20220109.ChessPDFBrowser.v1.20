package com.frojasg1.sun.security.pkcs;

import java.io.IOException;
import com.frojasg1.sun.misc.HexDumpEncoder;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.GeneralNames;
import com.frojasg1.sun.security.x509.SerialNumber;

class ESSCertId {
   private static volatile HexDumpEncoder hexDumper;
   private byte[] certHash;
   private GeneralNames issuer;
   private SerialNumber serialNumber;

   ESSCertId(DerValue var1) throws IOException {
      this.certHash = var1.data.getDerValue().toByteArray();
      if (var1.data.available() > 0) {
         DerValue var2 = var1.data.getDerValue();
         this.issuer = new GeneralNames(var2.data.getDerValue());
         this.serialNumber = new SerialNumber(var2.data.getDerValue());
      }

   }

   public String toString() {
      StringBuffer var1 = new StringBuffer();
      var1.append("[\n\tCertificate hash (SHA-1):\n");
      if (hexDumper == null) {
         hexDumper = new HexDumpEncoder();
      }

      var1.append(hexDumper.encode(this.certHash));
      if (this.issuer != null && this.serialNumber != null) {
         var1.append("\n\tIssuer: " + this.issuer + "\n");
         var1.append("\t" + this.serialNumber);
      }

      var1.append("\n]");
      return var1.toString();
   }
}
