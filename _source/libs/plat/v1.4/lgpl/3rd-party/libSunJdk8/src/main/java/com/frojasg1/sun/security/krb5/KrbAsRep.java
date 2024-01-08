package com.frojasg1.sun.security.krb5;

import java.io.IOException;
import java.util.Objects;
import javax.security.auth.kerberos.KeyTab;
import com.frojasg1.sun.security.jgss.krb5.Krb5Util;
import com.frojasg1.sun.security.krb5.Asn1Exception;
import com.frojasg1.sun.security.krb5.Credentials;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbAsReq;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.KrbKdcRep;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.internal.ASRep;
import com.frojasg1.sun.security.krb5.internal.ASReq;
import com.frojasg1.sun.security.krb5.internal.EncASRepPart;
import com.frojasg1.sun.security.krb5.internal.KRBError;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.PAData;
import com.frojasg1.sun.security.krb5.internal.crypto.EType;
import com.frojasg1.sun.security.util.DerValue;

class KrbAsRep extends com.frojasg1.sun.security.krb5.KrbKdcRep {
   private ASRep rep;
   private com.frojasg1.sun.security.krb5.Credentials creds;
   private boolean DEBUG;

   KrbAsRep(byte[] var1) throws com.frojasg1.sun.security.krb5.KrbException, com.frojasg1.sun.security.krb5.Asn1Exception, IOException {
      this.DEBUG = Krb5.DEBUG;
      DerValue var2 = new DerValue(var1);

      try {
         this.rep = new ASRep(var2);
      } catch (com.frojasg1.sun.security.krb5.Asn1Exception var8) {
         this.rep = null;
         KRBError var4 = new KRBError(var2);
         String var5 = var4.getErrorString();
         String var6 = null;
         if (var5 != null && var5.length() > 0) {
            if (var5.charAt(var5.length() - 1) == 0) {
               var6 = var5.substring(0, var5.length() - 1);
            } else {
               var6 = var5;
            }
         }

         com.frojasg1.sun.security.krb5.KrbException var7;
         if (var6 == null) {
            var7 = new com.frojasg1.sun.security.krb5.KrbException(var4);
         } else {
            if (this.DEBUG) {
               System.out.println("KRBError received: " + var6);
            }

            var7 = new com.frojasg1.sun.security.krb5.KrbException(var4, var6);
         }

         var7.initCause(var8);
         throw var7;
      }
   }

   PAData[] getPA() {
      return this.rep.pAData;
   }

   void decryptUsingKeyTab(KeyTab var1, com.frojasg1.sun.security.krb5.KrbAsReq var2, com.frojasg1.sun.security.krb5.PrincipalName var3) throws com.frojasg1.sun.security.krb5.KrbException, com.frojasg1.sun.security.krb5.Asn1Exception, IOException {
      com.frojasg1.sun.security.krb5.EncryptionKey var4 = null;
      int var5 = this.rep.encPart.getEType();
      Integer var6 = this.rep.encPart.kvno;

      try {
         var4 = com.frojasg1.sun.security.krb5.EncryptionKey.findKey(var5, var6, Krb5Util.keysFromJavaxKeyTab(var1, var3));
      } catch (com.frojasg1.sun.security.krb5.KrbException var8) {
         if (var8.returnCode() == 44) {
            var4 = com.frojasg1.sun.security.krb5.EncryptionKey.findKey(var5, Krb5Util.keysFromJavaxKeyTab(var1, var3));
         }
      }

      if (var4 == null) {
         throw new com.frojasg1.sun.security.krb5.KrbException(400, "Cannot find key for type/kvno to decrypt AS REP - " + EType.toString(var5) + "/" + var6);
      } else {
         this.decrypt(var4, var2);
      }
   }

   void decryptUsingPassword(char[] var1, com.frojasg1.sun.security.krb5.KrbAsReq var2, PrincipalName var3) throws com.frojasg1.sun.security.krb5.KrbException, com.frojasg1.sun.security.krb5.Asn1Exception, IOException {
      int var4 = this.rep.encPart.getEType();
      com.frojasg1.sun.security.krb5.EncryptionKey var5 = com.frojasg1.sun.security.krb5.EncryptionKey.acquireSecretKey(var3, var1, var4, PAData.getSaltAndParams(var4, this.rep.pAData));
      this.decrypt(var5, var2);
   }

   private void decrypt(EncryptionKey var1, KrbAsReq var2) throws KrbException, Asn1Exception, IOException {
      byte[] var3 = this.rep.encPart.decrypt(var1, 3);
      byte[] var4 = this.rep.encPart.reset(var3);
      DerValue var5 = new DerValue(var4);
      EncASRepPart var6 = new EncASRepPart(var5);
      this.rep.encKDCRepPart = var6;
      ASReq var7 = var2.getMessage();
      check(true, var7, this.rep);
      this.creds = new com.frojasg1.sun.security.krb5.Credentials(this.rep.ticket, var7.reqBody.cname, var6.sname, var6.key, var6.flags, var6.authtime, var6.starttime, var6.endtime, var6.renewTill, var6.caddr);
      if (this.DEBUG) {
         System.out.println(">>> KrbAsRep cons in KrbAsReq.getReply " + var7.reqBody.cname.getNameString());
      }

   }

   com.frojasg1.sun.security.krb5.Credentials getCreds() {
      return (Credentials)Objects.requireNonNull(this.creds, "Creds not available yet.");
   }

   com.frojasg1.sun.security.krb5.internal.ccache.Credentials getCCreds() {
      return new com.frojasg1.sun.security.krb5.internal.ccache.Credentials(this.rep);
   }
}
