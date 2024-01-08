package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.security.AlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPublicKeySpec;
import java.util.EnumSet;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.net.ssl.SSLHandshakeException;

final class ECDHCrypt {
   private PrivateKey privateKey;
   private ECPublicKey publicKey;

   ECDHCrypt(PrivateKey var1, PublicKey var2) {
      this.privateKey = var1;
      this.publicKey = (ECPublicKey)var2;
   }

   ECDHCrypt(int var1, SecureRandom var2) {
      try {
         KeyPairGenerator var3 = JsseJce.getKeyPairGenerator("EC");
         ECGenParameterSpec var4 = EllipticCurvesExtension.getECGenParamSpec(var1);
         var3.initialize(var4, var2);
         KeyPair var5 = var3.generateKeyPair();
         this.privateKey = var5.getPrivate();
         this.publicKey = (ECPublicKey)var5.getPublic();
      } catch (GeneralSecurityException var6) {
         throw new RuntimeException("Could not generate DH keypair", var6);
      }
   }

   ECDHCrypt(ECParameterSpec var1, SecureRandom var2) {
      try {
         KeyPairGenerator var3 = JsseJce.getKeyPairGenerator("EC");
         var3.initialize(var1, var2);
         KeyPair var4 = var3.generateKeyPair();
         this.privateKey = var4.getPrivate();
         this.publicKey = (ECPublicKey)var4.getPublic();
      } catch (GeneralSecurityException var5) {
         throw new RuntimeException("Could not generate DH keypair", var5);
      }
   }

   PublicKey getPublicKey() {
      return this.publicKey;
   }

   SecretKey getAgreedSecret(PublicKey var1) throws SSLHandshakeException {
      try {
         KeyAgreement var2 = JsseJce.getKeyAgreement("ECDH");
         var2.init(this.privateKey);
         var2.doPhase(var1, true);
         return var2.generateSecret("TlsPremasterSecret");
      } catch (GeneralSecurityException var3) {
         throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate secret")).initCause(var3);
      }
   }

   SecretKey getAgreedSecret(byte[] var1) throws SSLHandshakeException {
      try {
         ECParameterSpec var2 = this.publicKey.getParams();
         ECPoint var3 = JsseJce.decodePoint(var1, var2.getCurve());
         KeyFactory var4 = JsseJce.getKeyFactory("EC");
         ECPublicKeySpec var5 = new ECPublicKeySpec(var3, var2);
         PublicKey var6 = var4.generatePublic(var5);
         return this.getAgreedSecret(var6);
      } catch (IOException | GeneralSecurityException var7) {
         throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate secret")).initCause(var7);
      }
   }

   void checkConstraints(AlgorithmConstraints var1, byte[] var2) throws SSLHandshakeException {
      try {
         ECParameterSpec var3 = this.publicKey.getParams();
         ECPoint var4 = JsseJce.decodePoint(var2, var3.getCurve());
         ECPublicKeySpec var5 = new ECPublicKeySpec(var4, var3);
         KeyFactory var6 = JsseJce.getKeyFactory("EC");
         ECPublicKey var7 = (ECPublicKey)var6.generatePublic(var5);
         if (!var1.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), var7)) {
            throw new SSLHandshakeException("ECPublicKey does not comply to algorithm constraints");
         }
      } catch (IOException | GeneralSecurityException var8) {
         throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate ECPublicKey")).initCause(var8);
      }
   }
}
