package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLKeyException;
import javax.net.ssl.SSLProtocolException;
import com.frojasg1.sun.security.internal.spec.TlsRsaPremasterSecretParameterSpec;
import com.frojasg1.sun.security.util.KeyUtil;

final class RSAClientKeyExchange extends HandshakeMessage {
   private ProtocolVersion protocolVersion;
   SecretKey preMaster;
   private byte[] encrypted;

   RSAClientKeyExchange(ProtocolVersion var1, ProtocolVersion var2, SecureRandom var3, PublicKey var4) throws IOException {
      if (!var4.getAlgorithm().equals("RSA")) {
         throw new SSLKeyException("Public key not of type RSA: " + var4.getAlgorithm());
      } else {
         this.protocolVersion = var1;

         try {
            String var5 = var1.v >= ProtocolVersion.TLS12.v ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
            KeyGenerator var6 = JsseJce.getKeyGenerator(var5);
            var6.init(new TlsRsaPremasterSecretParameterSpec(var2.v, var1.v), var3);
            this.preMaster = var6.generateKey();
            Cipher var7 = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
            var7.init(3, var4, var3);
            this.encrypted = var7.wrap(this.preMaster);
         } catch (GeneralSecurityException var8) {
            throw (SSLKeyException)(new SSLKeyException("RSA premaster secret error")).initCause(var8);
         }
      }
   }

   private static String safeProviderName(Cipher var0) {
      try {
         return var0.getProvider().toString();
      } catch (Exception var3) {
         if (debug != null && Debug.isOn("handshake")) {
            System.out.println("Retrieving The Cipher provider name caused exception " + var3.getMessage());
         }

         try {
            return var0.toString() + " (provider name not available)";
         } catch (Exception var2) {
            if (debug != null && Debug.isOn("handshake")) {
               System.out.println("Retrieving The Cipher name caused exception " + var2.getMessage());
            }

            return "(cipher/provider names not available)";
         }
      }
   }

   RSAClientKeyExchange(ProtocolVersion var1, ProtocolVersion var2, SecureRandom var3, HandshakeInStream var4, int var5, PrivateKey var6) throws IOException {
      if (!var6.getAlgorithm().equals("RSA")) {
         throw new SSLKeyException("Private key not of type RSA: " + var6.getAlgorithm());
      } else {
         if (var1.v >= ProtocolVersion.TLS10.v) {
            this.encrypted = var4.getBytes16();
         } else {
            this.encrypted = new byte[var5];
            if (var4.read(this.encrypted) != var5) {
               throw new SSLProtocolException("SSL: read PreMasterSecret: short read");
            }
         }

         byte[] var7 = null;

         try {
            boolean var8 = false;
            Cipher var9 = JsseJce.getCipher("RSA/ECB/PKCS1Padding");

            try {
               var9.init(4, var6, new TlsRsaPremasterSecretParameterSpec(var2.v, var1.v), var3);
               var8 = !KeyUtil.isOracleJCEProvider(var9.getProvider().getName());
            } catch (UnsupportedOperationException | InvalidKeyException var13) {
               if (debug != null && Debug.isOn("handshake")) {
                  System.out.println("The Cipher provider " + safeProviderName(var9) + " caused exception: " + var13.getMessage());
               }

               var8 = true;
            }

            if (var8) {
               var9 = JsseJce.getCipher("RSA/ECB/PKCS1Padding");
               var9.init(2, var6);
               boolean var10 = false;

               try {
                  var7 = var9.doFinal(this.encrypted);
               } catch (BadPaddingException var12) {
                  var10 = true;
               }

               var7 = KeyUtil.checkTlsPreMasterSecretKey(var2.v, var1.v, var3, var7, var10);
               this.preMaster = generatePreMasterSecret(var2.v, var1.v, var7, var3);
            } else {
               this.preMaster = (SecretKey)var9.unwrap(this.encrypted, "TlsRsaPremasterSecret", 3);
            }

         } catch (InvalidKeyException var14) {
            throw new SSLException("Unable to process PreMasterSecret", var14);
         } catch (Exception var15) {
            if (debug != null && Debug.isOn("handshake")) {
               System.out.println("RSA premaster secret decryption error:");
               var15.printStackTrace(System.out);
            }

            throw new RuntimeException("Could not generate dummy secret", var15);
         }
      }
   }

   private static SecretKey generatePreMasterSecret(int var0, int var1, byte[] var2, SecureRandom var3) {
      if (debug != null && Debug.isOn("handshake")) {
         System.out.println("Generating a premaster secret");
      }

      try {
         String var4 = var0 >= ProtocolVersion.TLS12.v ? "SunTls12RsaPremasterSecret" : "SunTlsRsaPremasterSecret";
         KeyGenerator var5 = JsseJce.getKeyGenerator(var4);
         var5.init(new TlsRsaPremasterSecretParameterSpec(var0, var1, var2), var3);
         return var5.generateKey();
      } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException var6) {
         if (debug != null && Debug.isOn("handshake")) {
            System.out.println("RSA premaster secret generation error:");
            var6.printStackTrace(System.out);
         }

         throw new RuntimeException("Could not generate premaster secret", var6);
      }
   }

   int messageType() {
      return 16;
   }

   int messageLength() {
      return this.protocolVersion.v >= ProtocolVersion.TLS10.v ? this.encrypted.length + 2 : this.encrypted.length;
   }

   void send(HandshakeOutStream var1) throws IOException {
      if (this.protocolVersion.v >= ProtocolVersion.TLS10.v) {
         var1.putBytes16(this.encrypted);
      } else {
         var1.write(this.encrypted);
      }

   }

   void print(PrintStream var1) throws IOException {
      String var2 = "version not available/extractable";
      byte[] var3 = this.preMaster.getEncoded();
      if (var3 != null && var3.length >= 2) {
         var2 = ProtocolVersion.valueOf(var3[0], var3[1]).name;
      }

      var1.println("*** ClientKeyExchange, RSA PreMasterSecret, " + var2);
   }
}
