package com.frojasg1.sun.security.ssl.krb5;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Arrays;
import javax.net.ssl.SSLKeyException;
import com.frojasg1.sun.security.krb5.EncryptedData;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.ssl.Debug;
import com.frojasg1.sun.security.ssl.HandshakeInStream;
import com.frojasg1.sun.security.ssl.HandshakeMessage;
import com.frojasg1.sun.security.ssl.ProtocolVersion;

final class KerberosPreMasterSecret {
   private ProtocolVersion protocolVersion;
   private byte[] preMaster;
   private byte[] encrypted;

   KerberosPreMasterSecret(ProtocolVersion var1, SecureRandom var2, EncryptionKey var3) throws IOException {
      if (var3.getEType() == 16) {
         throw new IOException("session keys with des3-cbc-hmac-sha1-kd encryption type are not supported for TLS Kerberos cipher suites");
      } else {
         this.protocolVersion = var1;
         this.preMaster = generatePreMaster(var2, var1);

         try {
            EncryptedData var4 = new EncryptedData(var3, this.preMaster, 0);
            this.encrypted = var4.getBytes();
         } catch (KrbException var5) {
            throw (SSLKeyException)(new SSLKeyException("Kerberos premaster secret error")).initCause(var5);
         }
      }
   }

   KerberosPreMasterSecret(ProtocolVersion var1, ProtocolVersion var2, SecureRandom var3, HandshakeInStream var4, EncryptionKey var5) throws IOException {
      this.encrypted = var4.getBytes16();
      if (HandshakeMessage.debug != null && Debug.isOn("handshake") && this.encrypted != null) {
         Debug.println(System.out, "encrypted premaster secret", this.encrypted);
      }

      if (var5.getEType() == 16) {
         throw new IOException("session keys with des3-cbc-hmac-sha1-kd encryption type are not supported for TLS Kerberos cipher suites");
      } else {
         try {
            EncryptedData var6 = new EncryptedData(var5.getEType(), (Integer)null, this.encrypted);
            byte[] var7 = var6.decrypt(var5, 0);
            if (HandshakeMessage.debug != null && Debug.isOn("handshake") && this.encrypted != null) {
               Debug.println(System.out, "decrypted premaster secret", var7);
            }

            if (var7.length == 52 && var6.getEType() == 1) {
               if (paddingByteIs(var7, 52, (byte)4) || paddingByteIs(var7, 52, (byte)0)) {
                  var7 = Arrays.copyOf(var7, 48);
               }
            } else if (var7.length == 56 && var6.getEType() == 3 && paddingByteIs(var7, 56, (byte)8)) {
               var7 = Arrays.copyOf(var7, 48);
            }

            this.preMaster = var7;
            this.protocolVersion = ProtocolVersion.valueOf(this.preMaster[0], this.preMaster[1]);
            if (HandshakeMessage.debug != null && Debug.isOn("handshake")) {
               System.out.println("Kerberos PreMasterSecret version: " + this.protocolVersion);
            }
         } catch (Exception var8) {
            this.preMaster = null;
            this.protocolVersion = var1;
         }

         boolean var9 = this.protocolVersion.v != var2.v;
         if (var9 && var2.v <= 769) {
            var9 = this.protocolVersion.v != var1.v;
         }

         if (this.preMaster == null || this.preMaster.length != 48 || var9) {
            if (HandshakeMessage.debug != null && Debug.isOn("handshake")) {
               System.out.println("Kerberos PreMasterSecret error, generating random secret");
               if (this.preMaster != null) {
                  Debug.println(System.out, "Invalid secret", this.preMaster);
               }
            }

            this.preMaster = generatePreMaster(var3, var2);
            this.protocolVersion = var2;
         }

      }
   }

   private static boolean paddingByteIs(byte[] var0, int var1, byte var2) {
      for(int var3 = 48; var3 < var1; ++var3) {
         if (var0[var3] != var2) {
            return false;
         }
      }

      return true;
   }

   KerberosPreMasterSecret(ProtocolVersion var1, SecureRandom var2) {
      this.protocolVersion = var1;
      this.preMaster = generatePreMaster(var2, var1);
   }

   private static byte[] generatePreMaster(SecureRandom var0, ProtocolVersion var1) {
      byte[] var2 = new byte[48];
      var0.nextBytes(var2);
      var2[0] = var1.major;
      var2[1] = var1.minor;
      return var2;
   }

   byte[] getUnencrypted() {
      return this.preMaster;
   }

   byte[] getEncrypted() {
      return this.encrypted;
   }
}
