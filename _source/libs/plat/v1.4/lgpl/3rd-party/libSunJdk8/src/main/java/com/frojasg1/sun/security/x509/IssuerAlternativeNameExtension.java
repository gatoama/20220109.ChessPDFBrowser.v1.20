package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Iterator;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.AttributeNameEnumeration;
import com.frojasg1.sun.security.x509.CertAttrSet;
import com.frojasg1.sun.security.x509.Extension;
import com.frojasg1.sun.security.x509.GeneralName;
import com.frojasg1.sun.security.x509.GeneralNames;
import com.frojasg1.sun.security.x509.PKIXExtensions;

public class IssuerAlternativeNameExtension extends Extension implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.extensions.IssuerAlternativeName";
   public static final String NAME = "IssuerAlternativeName";
   public static final String ISSUER_NAME = "issuer_name";
   com.frojasg1.sun.security.x509.GeneralNames names = null;

   private void encodeThis() throws IOException {
      if (this.names != null && !this.names.isEmpty()) {
         DerOutputStream var1 = new DerOutputStream();
         this.names.encode(var1);
         this.extensionValue = var1.toByteArray();
      } else {
         this.extensionValue = null;
      }
   }

   public IssuerAlternativeNameExtension(com.frojasg1.sun.security.x509.GeneralNames var1) throws IOException {
      this.names = var1;
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.IssuerAlternativeName_Id;
      this.critical = false;
      this.encodeThis();
   }

   public IssuerAlternativeNameExtension(Boolean var1, com.frojasg1.sun.security.x509.GeneralNames var2) throws IOException {
      this.names = var2;
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.IssuerAlternativeName_Id;
      this.critical = var1;
      this.encodeThis();
   }

   public IssuerAlternativeNameExtension() {
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.IssuerAlternativeName_Id;
      this.critical = false;
      this.names = new com.frojasg1.sun.security.x509.GeneralNames();
   }

   public IssuerAlternativeNameExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.IssuerAlternativeName_Id;
      this.critical = var1;
      this.extensionValue = (byte[])((byte[])var2);
      DerValue var3 = new DerValue(this.extensionValue);
      if (var3.data == null) {
         this.names = new com.frojasg1.sun.security.x509.GeneralNames();
      } else {
         this.names = new com.frojasg1.sun.security.x509.GeneralNames(var3);
      }
   }

   public String toString() {
      String var1 = super.toString() + "IssuerAlternativeName [\n";
      com.frojasg1.sun.security.x509.GeneralName var3;
      if (this.names == null) {
         var1 = var1 + "  null\n";
      } else {
         for(Iterator var2 = this.names.names().iterator(); var2.hasNext(); var1 = var1 + "  " + var3 + "\n") {
            var3 = (GeneralName)var2.next();
         }
      }

      var1 = var1 + "]\n";
      return var1;
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      if (this.extensionValue == null) {
         this.extensionId = PKIXExtensions.IssuerAlternativeName_Id;
         this.critical = false;
         this.encodeThis();
      }

      super.encode(var2);
      var1.write(var2.toByteArray());
   }

   public void set(String var1, Object var2) throws IOException {
      if (var1.equalsIgnoreCase("issuer_name")) {
         if (!(var2 instanceof com.frojasg1.sun.security.x509.GeneralNames)) {
            throw new IOException("Attribute value should be of type GeneralNames.");
         } else {
            this.names = (com.frojasg1.sun.security.x509.GeneralNames)var2;
            this.encodeThis();
         }
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:IssuerAlternativeName.");
      }
   }

   public GeneralNames get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("issuer_name")) {
         return this.names;
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:IssuerAlternativeName.");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("issuer_name")) {
         this.names = null;
         this.encodeThis();
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:IssuerAlternativeName.");
      }
   }

   public Enumeration<String> getElements() {
      com.frojasg1.sun.security.x509.AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("issuer_name");
      return var1.elements();
   }

   public String getName() {
      return "IssuerAlternativeName";
   }
}
