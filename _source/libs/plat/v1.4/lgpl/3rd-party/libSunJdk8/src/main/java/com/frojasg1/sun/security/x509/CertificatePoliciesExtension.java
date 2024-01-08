package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.AttributeNameEnumeration;
import com.frojasg1.sun.security.x509.CertAttrSet;
import com.frojasg1.sun.security.x509.Extension;
import com.frojasg1.sun.security.x509.PKIXExtensions;
import com.frojasg1.sun.security.x509.PolicyInformation;

public class CertificatePoliciesExtension extends Extension implements CertAttrSet<String> {
   public static final String IDENT = "x509.info.extensions.CertificatePolicies";
   public static final String NAME = "CertificatePolicies";
   public static final String POLICIES = "policies";
   private List<com.frojasg1.sun.security.x509.PolicyInformation> certPolicies;

   private void encodeThis() throws IOException {
      if (this.certPolicies != null && !this.certPolicies.isEmpty()) {
         DerOutputStream var1 = new DerOutputStream();
         DerOutputStream var2 = new DerOutputStream();
         Iterator var3 = this.certPolicies.iterator();

         while(var3.hasNext()) {
            com.frojasg1.sun.security.x509.PolicyInformation var4 = (com.frojasg1.sun.security.x509.PolicyInformation)var3.next();
            var4.encode(var2);
         }

         var1.write((byte)48, (DerOutputStream)var2);
         this.extensionValue = var1.toByteArray();
      } else {
         this.extensionValue = null;
      }

   }

   public CertificatePoliciesExtension(List<com.frojasg1.sun.security.x509.PolicyInformation> var1) throws IOException {
      this(Boolean.FALSE, var1);
   }

   public CertificatePoliciesExtension(Boolean var1, List<com.frojasg1.sun.security.x509.PolicyInformation> var2) throws IOException {
      this.certPolicies = var2;
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.CertificatePolicies_Id;
      this.critical = var1;
      this.encodeThis();
   }

   public CertificatePoliciesExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.CertificatePolicies_Id;
      this.critical = var1;
      this.extensionValue = (byte[])((byte[])var2);
      DerValue var3 = new DerValue(this.extensionValue);
      if (var3.tag != 48) {
         throw new IOException("Invalid encoding for CertificatePoliciesExtension.");
      } else {
         this.certPolicies = new ArrayList();

         while(var3.data.available() != 0) {
            DerValue var4 = var3.data.getDerValue();
            com.frojasg1.sun.security.x509.PolicyInformation var5 = new com.frojasg1.sun.security.x509.PolicyInformation(var4);
            this.certPolicies.add(var5);
         }

      }
   }

   public String toString() {
      if (this.certPolicies == null) {
         return "";
      } else {
         StringBuilder var1 = new StringBuilder(super.toString());
         var1.append("CertificatePolicies [\n");
         Iterator var2 = this.certPolicies.iterator();

         while(var2.hasNext()) {
            com.frojasg1.sun.security.x509.PolicyInformation var3 = (com.frojasg1.sun.security.x509.PolicyInformation)var2.next();
            var1.append(var3.toString());
         }

         var1.append("]\n");
         return var1.toString();
      }
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      if (this.extensionValue == null) {
         this.extensionId = PKIXExtensions.CertificatePolicies_Id;
         this.critical = false;
         this.encodeThis();
      }

      super.encode(var2);
      var1.write(var2.toByteArray());
   }

   public void set(String var1, Object var2) throws IOException {
      if (var1.equalsIgnoreCase("policies")) {
         if (!(var2 instanceof List)) {
            throw new IOException("Attribute value should be of type List.");
         } else {
            this.certPolicies = (List)var2;
            this.encodeThis();
         }
      } else {
         throw new IOException("Attribute name [" + var1 + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
      }
   }

   public List<PolicyInformation> get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("policies")) {
         return this.certPolicies;
      } else {
         throw new IOException("Attribute name [" + var1 + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("policies")) {
         this.certPolicies = null;
         this.encodeThis();
      } else {
         throw new IOException("Attribute name [" + var1 + "] not recognized by CertAttrSet:CertificatePoliciesExtension.");
      }
   }

   public Enumeration<String> getElements() {
      com.frojasg1.sun.security.x509.AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("policies");
      return var1.elements();
   }

   public String getName() {
      return "CertificatePolicies";
   }
}
