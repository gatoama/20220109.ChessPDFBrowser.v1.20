package com.frojasg1.sun.security.krb5;

import java.io.IOException;

import com.frojasg1.sun.security.krb5.Credentials;
import com.frojasg1.sun.security.krb5.EncryptedData;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.KrbTgsReq;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.Realm;
import com.frojasg1.sun.security.krb5.internal.AuthorizationData;
import com.frojasg1.sun.security.krb5.internal.EncKrbCredPart;
import com.frojasg1.sun.security.krb5.internal.HostAddress;
import com.frojasg1.sun.security.krb5.internal.HostAddresses;
import com.frojasg1.sun.security.krb5.internal.KDCOptions;
import com.frojasg1.sun.security.krb5.internal.KRBCred;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.KrbCredInfo;
import com.frojasg1.sun.security.krb5.internal.Ticket;
import com.frojasg1.sun.security.krb5.internal.TicketFlags;
import com.frojasg1.sun.security.util.DerValue;

public class KrbCred {
   private static boolean DEBUG;
   private byte[] obuf = null;
   private KRBCred credMessg = null;
   private Ticket ticket = null;
   private EncKrbCredPart encPart = null;
   private com.frojasg1.sun.security.krb5.Credentials creds = null;
   private KerberosTime timeStamp = null;

   public KrbCred(com.frojasg1.sun.security.krb5.Credentials var1, com.frojasg1.sun.security.krb5.Credentials var2, com.frojasg1.sun.security.krb5.EncryptionKey var3) throws com.frojasg1.sun.security.krb5.KrbException, IOException {
      com.frojasg1.sun.security.krb5.PrincipalName var4 = var1.getClient();
      com.frojasg1.sun.security.krb5.PrincipalName var5 = var1.getServer();
      com.frojasg1.sun.security.krb5.PrincipalName var6 = var2.getServer();
      if (!var2.getClient().equals(var4)) {
         throw new com.frojasg1.sun.security.krb5.KrbException(60, "Client principal does not match");
      } else {
         KDCOptions var7 = new KDCOptions();
         var7.set(2, true);
         var7.set(1, true);
         HostAddresses var8 = null;
         if (var6.getNameType() == 3) {
            var8 = new HostAddresses(var6);
         }

         com.frojasg1.sun.security.krb5.KrbTgsReq var9 = new KrbTgsReq(var7, var1, var5, (KerberosTime)null, (KerberosTime)null, (KerberosTime)null, (int[])null, var8, (AuthorizationData)null, (Ticket[])null, (com.frojasg1.sun.security.krb5.EncryptionKey)null);
         this.credMessg = this.createMessage(var9.sendAndGetCreds(), var3);
         this.obuf = this.credMessg.asn1Encode();
      }
   }

   KRBCred createMessage(com.frojasg1.sun.security.krb5.Credentials var1, com.frojasg1.sun.security.krb5.EncryptionKey var2) throws com.frojasg1.sun.security.krb5.KrbException, IOException {
      com.frojasg1.sun.security.krb5.EncryptionKey var3 = var1.getSessionKey();
      com.frojasg1.sun.security.krb5.PrincipalName var4 = var1.getClient();
      Realm var5 = var4.getRealm();
      com.frojasg1.sun.security.krb5.PrincipalName var6 = var1.getServer();
      KrbCredInfo var7 = new KrbCredInfo(var3, var4, var1.flags, var1.authTime, var1.startTime, var1.endTime, var1.renewTill, var6, var1.cAddr);
      this.timeStamp = KerberosTime.now();
      KrbCredInfo[] var8 = new KrbCredInfo[]{var7};
      EncKrbCredPart var9 = new EncKrbCredPart(var8, this.timeStamp, (Integer)null, (Integer)null, (HostAddress)null, (HostAddresses)null);
      com.frojasg1.sun.security.krb5.EncryptedData var10 = new EncryptedData(var2, var9.asn1Encode(), 14);
      Ticket[] var11 = new Ticket[]{var1.ticket};
      this.credMessg = new KRBCred(var11, var10);
      return this.credMessg;
   }

   public KrbCred(byte[] var1, com.frojasg1.sun.security.krb5.EncryptionKey var2) throws KrbException, IOException {
      this.credMessg = new KRBCred(var1);
      this.ticket = this.credMessg.tickets[0];
      if (this.credMessg.encPart.getEType() == 0) {
         var2 = com.frojasg1.sun.security.krb5.EncryptionKey.NULL_KEY;
      }

      byte[] var3 = this.credMessg.encPart.decrypt(var2, 14);
      byte[] var4 = this.credMessg.encPart.reset(var3);
      DerValue var5 = new DerValue(var4);
      EncKrbCredPart var6 = new EncKrbCredPart(var5);
      this.timeStamp = var6.timeStamp;
      KrbCredInfo var7 = var6.ticketInfo[0];
      EncryptionKey var8 = var7.key;
      com.frojasg1.sun.security.krb5.PrincipalName var9 = var7.pname;
      TicketFlags var10 = var7.flags;
      KerberosTime var11 = var7.authtime;
      KerberosTime var12 = var7.starttime;
      KerberosTime var13 = var7.endtime;
      KerberosTime var14 = var7.renewTill;
      PrincipalName var15 = var7.sname;
      HostAddresses var16 = var7.caddr;
      if (DEBUG) {
         System.out.println(">>>Delegated Creds have pname=" + var9 + " sname=" + var15 + " authtime=" + var11 + " starttime=" + var12 + " endtime=" + var13 + "renewTill=" + var14);
      }

      this.creds = new com.frojasg1.sun.security.krb5.Credentials(this.ticket, var9, var15, var8, var10, var11, var12, var13, var14, var16);
   }

   public com.frojasg1.sun.security.krb5.Credentials[] getDelegatedCreds() {
      com.frojasg1.sun.security.krb5.Credentials[] var1 = new Credentials[]{this.creds};
      return var1;
   }

   public byte[] getMessage() {
      return this.obuf;
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
