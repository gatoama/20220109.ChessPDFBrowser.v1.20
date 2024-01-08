package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import com.frojasg1.sun.security.util.Debug;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.util.ObjectIdentifier;
import com.frojasg1.sun.security.x509.AttributeNameEnumeration;
import com.frojasg1.sun.security.x509.CertAttrSet;
import com.frojasg1.sun.security.x509.Extension;
import com.frojasg1.sun.security.x509.PKIXExtensions;

public class InhibitAnyPolicyExtension extends Extension implements CertAttrSet<String> {
   private static final Debug debug = Debug.getInstance("certpath");
   public static final String IDENT = "x509.info.extensions.InhibitAnyPolicy";
   public static ObjectIdentifier AnyPolicy_Id;
   public static final String NAME = "InhibitAnyPolicy";
   public static final String SKIP_CERTS = "skip_certs";
   private int skipCerts = 2147483647;

   private void encodeThis() throws IOException {
      DerOutputStream var1 = new DerOutputStream();
      var1.putInteger(this.skipCerts);
      this.extensionValue = var1.toByteArray();
   }

   public InhibitAnyPolicyExtension(int var1) throws IOException {
      if (var1 < -1) {
         throw new IOException("Invalid value for skipCerts");
      } else {
         if (var1 == -1) {
            this.skipCerts = 2147483647;
         } else {
            this.skipCerts = var1;
         }

         this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.InhibitAnyPolicy_Id;
         this.critical = true;
         this.encodeThis();
      }
   }

   public InhibitAnyPolicyExtension(Boolean var1, Object var2) throws IOException {
      this.extensionId = com.frojasg1.sun.security.x509.PKIXExtensions.InhibitAnyPolicy_Id;
      if (!var1) {
         throw new IOException("Criticality cannot be false for InhibitAnyPolicy");
      } else {
         this.critical = var1;
         this.extensionValue = (byte[])((byte[])var2);
         DerValue var3 = new DerValue(this.extensionValue);
         if (var3.tag != 2) {
            throw new IOException("Invalid encoding of InhibitAnyPolicy: data not integer");
         } else if (var3.data == null) {
            throw new IOException("Invalid encoding of InhibitAnyPolicy: null data");
         } else {
            int var4 = var3.getInteger();
            if (var4 < -1) {
               throw new IOException("Invalid value for skipCerts");
            } else {
               if (var4 == -1) {
                  this.skipCerts = 2147483647;
               } else {
                  this.skipCerts = var4;
               }

            }
         }
      }
   }

   public String toString() {
      String var1 = super.toString() + "InhibitAnyPolicy: " + this.skipCerts + "\n";
      return var1;
   }

   public void encode(OutputStream var1) throws IOException {
      DerOutputStream var2 = new DerOutputStream();
      if (this.extensionValue == null) {
         this.extensionId = PKIXExtensions.InhibitAnyPolicy_Id;
         this.critical = true;
         this.encodeThis();
      }

      super.encode(var2);
      var1.write(var2.toByteArray());
   }

   public void set(String var1, Object var2) throws IOException {
      if (var1.equalsIgnoreCase("skip_certs")) {
         if (!(var2 instanceof Integer)) {
            throw new IOException("Attribute value should be of type Integer.");
         } else {
            int var3 = (Integer)var2;
            if (var3 < -1) {
               throw new IOException("Invalid value for skipCerts");
            } else {
               if (var3 == -1) {
                  this.skipCerts = 2147483647;
               } else {
                  this.skipCerts = var3;
               }

               this.encodeThis();
            }
         }
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
      }
   }

   public Integer get(String var1) throws IOException {
      if (var1.equalsIgnoreCase("skip_certs")) {
         return new Integer(this.skipCerts);
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
      }
   }

   public void delete(String var1) throws IOException {
      if (var1.equalsIgnoreCase("skip_certs")) {
         throw new IOException("Attribute skip_certs may not be deleted.");
      } else {
         throw new IOException("Attribute name not recognized by CertAttrSet:InhibitAnyPolicy.");
      }
   }

   public Enumeration<String> getElements() {
      com.frojasg1.sun.security.x509.AttributeNameEnumeration var1 = new AttributeNameEnumeration();
      var1.addElement("skip_certs");
      return var1.elements();
   }

   public String getName() {
      return "InhibitAnyPolicy";
   }

   static {
      try {
         AnyPolicy_Id = new ObjectIdentifier("2.5.29.32.0");
      } catch (IOException var1) {
      }

   }
}
