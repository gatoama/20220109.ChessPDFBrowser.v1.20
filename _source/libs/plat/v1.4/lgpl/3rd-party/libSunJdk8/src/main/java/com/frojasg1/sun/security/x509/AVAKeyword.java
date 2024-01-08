package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import com.frojasg1.sun.security.pkcs.PKCS9Attribute;
import com.frojasg1.sun.security.util.ObjectIdentifier;
import com.frojasg1.sun.security.x509.X500Name;

class AVAKeyword {
   private static final Map<ObjectIdentifier, AVAKeyword> oidMap = new HashMap();
   private static final Map<String, AVAKeyword> keywordMap = new HashMap();
   private String keyword;
   private ObjectIdentifier oid;
   private boolean rfc1779Compliant;
   private boolean rfc2253Compliant;

   private AVAKeyword(String var1, ObjectIdentifier var2, boolean var3, boolean var4) {
      this.keyword = var1;
      this.oid = var2;
      this.rfc1779Compliant = var3;
      this.rfc2253Compliant = var4;
      oidMap.put(var2, this);
      keywordMap.put(var1, this);
   }

   private boolean isCompliant(int var1) {
      switch(var1) {
      case 1:
         return true;
      case 2:
         return this.rfc1779Compliant;
      case 3:
         return this.rfc2253Compliant;
      default:
         throw new IllegalArgumentException("Invalid standard " + var1);
      }
   }

   static ObjectIdentifier getOID(String var0, int var1, Map<String, String> var2) throws IOException {
      var0 = var0.toUpperCase(Locale.ENGLISH);
      if (var1 == 3) {
         if (var0.startsWith(" ") || var0.endsWith(" ")) {
            throw new IOException("Invalid leading or trailing space in keyword \"" + var0 + "\"");
         }
      } else {
         var0 = var0.trim();
      }

      String var3 = (String)var2.get(var0);
      if (var3 == null) {
         AVAKeyword var4 = (AVAKeyword)keywordMap.get(var0);
         if (var4 != null && var4.isCompliant(var1)) {
            return var4.oid;
         } else {
            if (var1 == 1 && var0.startsWith("OID.")) {
               var0 = var0.substring(4);
            }

            boolean var6 = false;
            if (var0.length() != 0) {
               char var5 = var0.charAt(0);
               if (var5 >= '0' && var5 <= '9') {
                  var6 = true;
               }
            }

            if (!var6) {
               throw new IOException("Invalid keyword \"" + var0 + "\"");
            } else {
               return new ObjectIdentifier(var0);
            }
         }
      } else {
         return new ObjectIdentifier(var3);
      }
   }

   static String getKeyword(ObjectIdentifier var0, int var1) {
      return getKeyword(var0, var1, Collections.emptyMap());
   }

   static String getKeyword(ObjectIdentifier var0, int var1, Map<String, String> var2) {
      String var3 = var0.toString();
      String var4 = (String)var2.get(var3);
      if (var4 == null) {
         AVAKeyword var7 = (AVAKeyword)oidMap.get(var0);
         if (var7 != null && var7.isCompliant(var1)) {
            return var7.keyword;
         } else {
            return var1 == 3 ? var3 : "OID." + var3;
         }
      } else if (var4.length() == 0) {
         throw new IllegalArgumentException("keyword cannot be empty");
      } else {
         var4 = var4.trim();
         char var5 = var4.charAt(0);
         if (var5 < 'A' || var5 > 'z' || var5 > 'Z' && var5 < 'a') {
            throw new IllegalArgumentException("keyword does not start with letter");
         } else {
            for(int var6 = 1; var6 < var4.length(); ++var6) {
               var5 = var4.charAt(var6);
               if ((var5 < 'A' || var5 > 'z' || var5 > 'Z' && var5 < 'a') && (var5 < '0' || var5 > '9') && var5 != '_') {
                  throw new IllegalArgumentException("keyword character is not a letter, digit, or underscore");
               }
            }

            return var4;
         }
      }
   }

   static boolean hasKeyword(ObjectIdentifier var0, int var1) {
      AVAKeyword var2 = (AVAKeyword)oidMap.get(var0);
      return var2 == null ? false : var2.isCompliant(var1);
   }

   static {
      new AVAKeyword("CN", com.frojasg1.sun.security.x509.X500Name.commonName_oid, true, true);
      new AVAKeyword("C", com.frojasg1.sun.security.x509.X500Name.countryName_oid, true, true);
      new AVAKeyword("L", com.frojasg1.sun.security.x509.X500Name.localityName_oid, true, true);
      new AVAKeyword("S", com.frojasg1.sun.security.x509.X500Name.stateName_oid, false, false);
      new AVAKeyword("ST", com.frojasg1.sun.security.x509.X500Name.stateName_oid, true, true);
      new AVAKeyword("O", com.frojasg1.sun.security.x509.X500Name.orgName_oid, true, true);
      new AVAKeyword("OU", com.frojasg1.sun.security.x509.X500Name.orgUnitName_oid, true, true);
      new AVAKeyword("T", com.frojasg1.sun.security.x509.X500Name.title_oid, false, false);
      new AVAKeyword("IP", com.frojasg1.sun.security.x509.X500Name.ipAddress_oid, false, false);
      new AVAKeyword("STREET", com.frojasg1.sun.security.x509.X500Name.streetAddress_oid, true, true);
      new AVAKeyword("DC", com.frojasg1.sun.security.x509.X500Name.DOMAIN_COMPONENT_OID, false, true);
      new AVAKeyword("DNQUALIFIER", com.frojasg1.sun.security.x509.X500Name.DNQUALIFIER_OID, false, false);
      new AVAKeyword("DNQ", com.frojasg1.sun.security.x509.X500Name.DNQUALIFIER_OID, false, false);
      new AVAKeyword("SURNAME", com.frojasg1.sun.security.x509.X500Name.SURNAME_OID, false, false);
      new AVAKeyword("GIVENNAME", com.frojasg1.sun.security.x509.X500Name.GIVENNAME_OID, false, false);
      new AVAKeyword("INITIALS", com.frojasg1.sun.security.x509.X500Name.INITIALS_OID, false, false);
      new AVAKeyword("GENERATION", com.frojasg1.sun.security.x509.X500Name.GENERATIONQUALIFIER_OID, false, false);
      new AVAKeyword("EMAIL", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
      new AVAKeyword("EMAILADDRESS", PKCS9Attribute.EMAIL_ADDRESS_OID, false, false);
      new AVAKeyword("UID", com.frojasg1.sun.security.x509.X500Name.userid_oid, false, true);
      new AVAKeyword("SERIALNUMBER", X500Name.SERIALNUMBER_OID, false, false);
   }
}
