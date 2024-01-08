package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.Enumeration;
import com.frojasg1.sun.security.util.DerInputStream;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.AttributeNameEnumeration;
import com.frojasg1.sun.security.x509.CertAttrSet;
import com.frojasg1.sun.security.x509.SerialNumber;

public class CertificateSerialNumber implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.serialNumber";
   public static final String NAME = "serialNumber";
   public static final String NUMBER = "number";
   private com.frojasg1.sun.security.x509.SerialNumber serial;

   public CertificateSerialNumber(BigInteger var1) {
      this.serial = new com.frojasg1.sun.security.x509.SerialNumber(var1);
   }

   public CertificateSerialNumber(int var1) {
      this.serial = new com.frojasg1.sun.security.x509.SerialNumber(var1);
   }

   public CertificateSerialNumber(DerInputStream var1) throws IOException {
      this.serial = new com.frojasg1.sun.security.x509.SerialNumber(var1);
   }

   public CertificateSerialNumber(InputStream var1) throws IOException {
      this.serial = new com.frojasg1.sun.security.x509.SerialNumber(var1);
   }

   public CertificateSerialNumber(DerValue var1) throws IOException {
      this.serial = new com.frojasg1.sun.security.x509.SerialNumber(var1);
   }

   public String toString() {
      return this.serial == null ? "" : this.serial.toString();
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      this.serial.encode(var2);
      var1.write(var2.toByteArray());
   }

   public void set(String var1, Object var2) throws IOException {
      if (!(var2 instanceof com.frojasg1.sun.security.x509.SerialNumber)) {
         throw new IOException("Attribute must be of type SerialNumber.");
      } else if (var1.equalsIgnoreCase("number")) {
         this.serial = (com.frojasg1.sun.security.x509.SerialNumber)var2;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
      }
   }

   public SerialNumber get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("number")) {
         return this.serial;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("number")) {
         this.serial = null;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:CertificateSerialNumber.");
      }
   }

   public Enumeration<String> getElements() {
      com.frojasg1.sun.security.x509.AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("number");
      return var1.elements();
   }

   public String getName() {
      return "serialNumber";
   }
}
