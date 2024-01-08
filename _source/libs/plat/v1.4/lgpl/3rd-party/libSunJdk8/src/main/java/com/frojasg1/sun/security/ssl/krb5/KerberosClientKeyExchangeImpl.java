package com.frojasg1.sun.security.ssl.krb5;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import javax.security.auth.kerberos.KerberosKey;
import javax.security.auth.kerberos.KerberosPrincipal;
import javax.security.auth.kerberos.KerberosTicket;
import javax.security.auth.kerberos.ServicePermission;
import com.frojasg1.sun.security.jgss.GSSCaller;
import com.frojasg1.sun.security.jgss.krb5.Krb5Util;
import com.frojasg1.sun.security.jgss.krb5.ServiceCreds;
import com.frojasg1.sun.security.krb5.EncryptedData;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.internal.EncTicketPart;
import com.frojasg1.sun.security.krb5.internal.Ticket;
import com.frojasg1.sun.security.ssl.Debug;
import com.frojasg1.sun.security.ssl.HandshakeInStream;
import com.frojasg1.sun.security.ssl.HandshakeMessage;
import com.frojasg1.sun.security.ssl.HandshakeOutStream;
import com.frojasg1.sun.security.ssl.KerberosClientKeyExchange;
import com.frojasg1.sun.security.ssl.Krb5Helper;
import com.frojasg1.sun.security.ssl.ProtocolVersion;
import com.frojasg1.sun.security.ssl.krb5.KerberosPreMasterSecret;

public final class KerberosClientKeyExchangeImpl extends KerberosClientKeyExchange {
   private com.frojasg1.sun.security.ssl.krb5.KerberosPreMasterSecret preMaster;
   private byte[] encodedTicket;
   private KerberosPrincipal peerPrincipal;
   private KerberosPrincipal localPrincipal;

   public KerberosClientKeyExchangeImpl() {
   }

   public void init(String var1, AccessControlContext var2, ProtocolVersion var3, SecureRandom var4) throws IOException {
      KerberosTicket var5 = getServiceTicket(var1, var2);
      this.encodedTicket = var5.getEncoded();
      this.peerPrincipal = var5.getServer();
      this.localPrincipal = var5.getClient();
      EncryptionKey var6 = new EncryptionKey(var5.getSessionKeyType(), var5.getSessionKey().getEncoded());
      this.preMaster = new com.frojasg1.sun.security.ssl.krb5.KerberosPreMasterSecret(var3, var4, var6);
   }

   public void init(ProtocolVersion var1, ProtocolVersion var2, SecureRandom var3, HandshakeInStream var4, AccessControlContext var5, Object var6) throws IOException {
      this.encodedTicket = var4.getBytes16();
      if (debug != null && Debug.isOn("verbose")) {
         Debug.println(System.out, "encoded Kerberos service ticket", this.encodedTicket);
      }

      EncryptionKey var7 = null;

      try {
         Ticket var8 = new Ticket(this.encodedTicket);
         EncryptedData var9 = var8.encPart;
         PrincipalName var10 = var8.sname;
         final ServiceCreds var11 = (ServiceCreds)var6;
         final KerberosPrincipal var12 = new KerberosPrincipal(var10.toString());
         if (var11.getName() == null) {
            SecurityManager var13 = System.getSecurityManager();

            try {
               if (var13 != null) {
                  var13.checkPermission(Krb5Helper.getServicePermission(var10.toString(), "accept"), var5);
               }
            } catch (SecurityException var22) {
               var6 = null;
               if (debug != null && Debug.isOn("handshake")) {
                  System.out.println("Permission to access Kerberos secret key denied");
               }

               throw new IOException("Kerberos service not allowedy");
            }
         }

         KerberosKey[] var25 = (KerberosKey[])AccessController.doPrivileged(new PrivilegedAction<KerberosKey[]>() {
            public KerberosKey[] run() {
               return var11.getKKeys(var12);
            }
         });
         if (var25.length == 0) {
            throw new IOException("Found no key for " + var12 + (var11.getName() == null ? "" : ", this keytab is for " + var11.getName() + " only"));
         }

         int var14 = var9.getEType();
         Integer var15 = var9.getKeyVersionNumber();
         KerberosKey var16 = null;

         try {
            var16 = findKey(var14, var15, var25);
         } catch (KrbException var21) {
            throw new IOException("Cannot find key matching version number", var21);
         }

         if (var16 == null) {
            throw new IOException("Cannot find key of appropriate type to decrypt ticket - need etype " + var14);
         }

         EncryptionKey var17 = new EncryptionKey(var14, var16.getEncoded());
         byte[] var18 = var9.decrypt(var17, 2);
         byte[] var19 = var9.reset(var18);
         EncTicketPart var20 = new EncTicketPart(var19);
         this.peerPrincipal = new KerberosPrincipal(var20.cname.getName());
         this.localPrincipal = new KerberosPrincipal(var10.getName());
         var7 = var20.key;
         if (debug != null && Debug.isOn("handshake")) {
            System.out.println("server principal: " + var10);
            System.out.println("cname: " + var20.cname.toString());
         }
      } catch (IOException var23) {
         throw var23;
      } catch (Exception var24) {
         if (debug != null && Debug.isOn("handshake")) {
            System.out.println("KerberosWrapper error getting session key, generating random secret (" + var24.getMessage() + ")");
         }

         var7 = null;
      }

      var4.getBytes16();
      if (var7 != null) {
         this.preMaster = new com.frojasg1.sun.security.ssl.krb5.KerberosPreMasterSecret(var1, var2, var3, var4, var7);
      } else {
         this.preMaster = new com.frojasg1.sun.security.ssl.krb5.KerberosPreMasterSecret(var2, var3);
      }

   }

   public int messageLength() {
      return 6 + this.encodedTicket.length + this.preMaster.getEncrypted().length;
   }

   public void send(HandshakeOutStream var1) throws IOException {
      var1.putBytes16(this.encodedTicket);
      var1.putBytes16((byte[])null);
      var1.putBytes16(this.preMaster.getEncrypted());
   }

   public void print(PrintStream var1) throws IOException {
      var1.println("*** ClientKeyExchange, Kerberos");
      if (debug != null && Debug.isOn("verbose")) {
         Debug.println(var1, "Kerberos service ticket", this.encodedTicket);
         Debug.println(var1, "Random Secret", this.preMaster.getUnencrypted());
         Debug.println(var1, "Encrypted random Secret", this.preMaster.getEncrypted());
      }

   }

   private static KerberosTicket getServiceTicket(String var0, final AccessControlContext var1) throws IOException {
      String var2;
      if ("localhost".equals(var0) || "localhost.localdomain".equals(var0)) {
         if (debug != null && Debug.isOn("handshake")) {
            System.out.println("Get the local hostname");
         }

         var2 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               try {
                  return InetAddress.getLocalHost().getHostName();
               } catch (UnknownHostException var2) {
                  if (HandshakeMessage.debug != null && Debug.isOn("handshake")) {
                     System.out.println("Warning, cannot get the local hostname: " + var2.getMessage());
                  }

                  return null;
               }
            }
         });
         if (var2 != null) {
            var0 = var2;
         }
      }

      var2 = "host/" + var0;

      PrincipalName var3;
      try {
         var3 = new PrincipalName(var2, 3);
      } catch (SecurityException var12) {
         throw var12;
      } catch (Exception var13) {
         IOException var5 = new IOException("Invalid service principal name: " + var2);
         var5.initCause(var13);
         throw var5;
      }

      String var4 = var3.getRealmAsString();
      final String var14 = var3.toString();
      final String var6 = "krbtgt/" + var4 + "@" + var4;
      final Object var7 = null;
      SecurityManager var8 = System.getSecurityManager();
      if (var8 != null) {
         var8.checkPermission(new ServicePermission(var14, "initiate"), var1);
      }

      try {
         KerberosTicket var9 = (KerberosTicket)AccessController.doPrivileged(new PrivilegedExceptionAction<KerberosTicket>() {
            public KerberosTicket run() throws Exception {
               return Krb5Util.getTicketFromSubjectAndTgs(GSSCaller.CALLER_SSL_CLIENT, (String)var7, var14, var6, var1);
            }
         });
         if (var9 == null) {
            throw new IOException("Failed to find any kerberos service ticket for " + var14);
         } else {
            return var9;
         }
      } catch (PrivilegedActionException var11) {
         IOException var10 = new IOException("Attempt to obtain kerberos service ticket for " + var14 + " failed!");
         var10.initCause(var11);
         throw var10;
      }
   }

   public byte[] getUnencryptedPreMasterSecret() {
      return this.preMaster.getUnencrypted();
   }

   public KerberosPrincipal getPeerPrincipal() {
      return this.peerPrincipal;
   }

   public KerberosPrincipal getLocalPrincipal() {
      return this.localPrincipal;
   }

   private static boolean versionMatches(Integer var0, int var1) {
      return var0 != null && var0 != 0 && var1 != 0 ? var0.equals(var1) : true;
   }

   private static KerberosKey findKey(int var0, Integer var1, KerberosKey[] var2) throws KrbException {
      boolean var4 = false;
      int var5 = 0;
      KerberosKey var6 = null;

      int var3;
      int var7;
      int var8;
      for(var7 = 0; var7 < var2.length; ++var7) {
         var3 = var2[var7].getKeyType();
         if (var0 == var3) {
            var8 = var2[var7].getVersionNumber();
            var4 = true;
            if (versionMatches(var1, var8)) {
               return var2[var7];
            }

            if (var8 > var5) {
               var6 = var2[var7];
               var5 = var8;
            }
         }
      }

      if (var0 == 1 || var0 == 3) {
         for(var7 = 0; var7 < var2.length; ++var7) {
            var3 = var2[var7].getKeyType();
            if (var3 == 1 || var3 == 3) {
               var8 = var2[var7].getVersionNumber();
               var4 = true;
               if (versionMatches(var1, var8)) {
                  return new KerberosKey(var2[var7].getPrincipal(), var2[var7].getEncoded(), var0, var8);
               }

               if (var8 > var5) {
                  var6 = new KerberosKey(var2[var7].getPrincipal(), var2[var7].getEncoded(), var0, var8);
                  var5 = var8;
               }
            }
         }
      }

      return var4 ? var6 : null;
   }
}
