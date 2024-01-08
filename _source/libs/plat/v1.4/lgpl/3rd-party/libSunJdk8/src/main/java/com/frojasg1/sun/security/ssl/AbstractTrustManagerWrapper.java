package com.frojasg1.sun.security.ssl;

import java.net.Socket;
import java.security.AlgorithmConstraints;
import java.security.Timestamp;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashSet;
import javax.net.ssl.ExtendedSSLSession;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509TrustManager;
import com.frojasg1.sun.security.provider.certpath.AlgorithmChecker;

final class AbstractTrustManagerWrapper extends X509ExtendedTrustManager implements X509TrustManager {
   private final X509TrustManager tm;

   AbstractTrustManagerWrapper(X509TrustManager var1) {
      this.tm = var1;
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      this.tm.checkClientTrusted(var1, var2);
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2) throws CertificateException {
      this.tm.checkServerTrusted(var1, var2);
   }

   public X509Certificate[] getAcceptedIssuers() {
      return this.tm.getAcceptedIssuers();
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
      this.tm.checkClientTrusted(var1, var2);
      this.checkAdditionalTrust(var1, var2, var3, true);
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2, Socket var3) throws CertificateException {
      this.tm.checkServerTrusted(var1, var2);
      this.checkAdditionalTrust(var1, var2, var3, false);
   }

   public void checkClientTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
      this.tm.checkClientTrusted(var1, var2);
      this.checkAdditionalTrust(var1, var2, var3, true);
   }

   public void checkServerTrusted(X509Certificate[] var1, String var2, SSLEngine var3) throws CertificateException {
      this.tm.checkServerTrusted(var1, var2);
      this.checkAdditionalTrust(var1, var2, var3, false);
   }

   private void checkAdditionalTrust(X509Certificate[] var1, String var2, Socket var3, boolean var4) throws CertificateException {
      if (var3 != null && var3.isConnected() && var3 instanceof SSLSocket) {
         SSLSocket var5 = (SSLSocket)var3;
         SSLSession var6 = var5.getHandshakeSession();
         if (var6 == null) {
            throw new CertificateException("No handshake session");
         }

         String var7 = var5.getSSLParameters().getEndpointIdentificationAlgorithm();
         if (var7 != null && var7.length() != 0) {
            String var8 = var6.getPeerHost();
            X509TrustManagerImpl.checkIdentity(var8, var1[0], var7);
         }

         ProtocolVersion var12 = ProtocolVersion.valueOf(var6.getProtocol());
         SSLAlgorithmConstraints var9 = null;
         if (var12.v >= ProtocolVersion.TLS12.v) {
            if (var6 instanceof ExtendedSSLSession) {
               ExtendedSSLSession var10 = (ExtendedSSLSession)var6;
               String[] var11 = var10.getLocalSupportedSignatureAlgorithms();
               var9 = new SSLAlgorithmConstraints(var5, var11, true);
            } else {
               var9 = new SSLAlgorithmConstraints(var5, true);
            }
         } else {
            var9 = new SSLAlgorithmConstraints(var5, true);
         }

         this.checkAlgorithmConstraints(var1, var9, var4);
      }

   }

   private void checkAdditionalTrust(X509Certificate[] var1, String var2, SSLEngine var3, boolean var4) throws CertificateException {
      if (var3 != null) {
         SSLSession var5 = var3.getHandshakeSession();
         if (var5 == null) {
            throw new CertificateException("No handshake session");
         }

         String var6 = var3.getSSLParameters().getEndpointIdentificationAlgorithm();
         if (var6 != null && var6.length() != 0) {
            String var7 = var5.getPeerHost();
            X509TrustManagerImpl.checkIdentity(var7, var1[0], var6);
         }

         ProtocolVersion var11 = ProtocolVersion.valueOf(var5.getProtocol());
         SSLAlgorithmConstraints var8 = null;
         if (var11.v >= ProtocolVersion.TLS12.v) {
            if (var5 instanceof ExtendedSSLSession) {
               ExtendedSSLSession var9 = (ExtendedSSLSession)var5;
               String[] var10 = var9.getLocalSupportedSignatureAlgorithms();
               var8 = new SSLAlgorithmConstraints(var3, var10, true);
            } else {
               var8 = new SSLAlgorithmConstraints(var3, true);
            }
         } else {
            var8 = new SSLAlgorithmConstraints(var3, true);
         }

         this.checkAlgorithmConstraints(var1, var8, var4);
      }

   }

   private void checkAlgorithmConstraints(X509Certificate[] var1, AlgorithmConstraints var2, boolean var3) throws CertificateException {
      try {
         int var4 = var1.length - 1;
         HashSet var5 = new HashSet();
         X509Certificate[] var6 = this.tm.getAcceptedIssuers();
         if (var6 != null && var6.length > 0) {
            Collections.addAll(var5, var6);
         }

         if (var5.contains(var1[var4])) {
            --var4;
         }

         if (var4 >= 0) {
            AlgorithmChecker var7 = new AlgorithmChecker(var2, (Timestamp)null, var3 ? "tls client" : "tls server");
            var7.init(false);

            for(int var8 = var4; var8 >= 0; --var8) {
               X509Certificate var9 = var1[var8];
               var7.check(var9, Collections.emptySet());
            }
         }

      } catch (CertPathValidatorException var10) {
         throw new CertificateException("Certificates do not conform to algorithm constraints", var10);
      }
   }
}
