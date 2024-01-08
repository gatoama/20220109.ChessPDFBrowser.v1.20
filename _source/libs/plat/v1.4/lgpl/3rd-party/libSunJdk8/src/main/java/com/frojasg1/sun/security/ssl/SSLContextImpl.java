package com.frojasg1.sun.security.ssl;

import java.io.FileInputStream;
import java.security.AccessController;
import java.security.AlgorithmParameters;
import java.security.CryptoPrimitive;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContextSpi;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSessionContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509ExtendedKeyManager;
import javax.net.ssl.X509ExtendedTrustManager;
import javax.net.ssl.X509KeyManager;
import javax.net.ssl.X509TrustManager;
import com.frojasg1.sun.security.action.GetPropertyAction;

public abstract class SSLContextImpl extends SSLContextSpi {
   private static final Debug debug = Debug.getInstance("ssl");
   private final EphemeralKeyManager ephemeralKeyManager = new EphemeralKeyManager();
   private final SSLSessionContextImpl clientCache = new SSLSessionContextImpl();
   private final SSLSessionContextImpl serverCache = new SSLSessionContextImpl();
   private boolean isInitialized;
   private X509ExtendedKeyManager keyManager;
   private X509TrustManager trustManager;
   private SecureRandom secureRandom;

   SSLContextImpl() {
   }

   protected void engineInit(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException {
      this.isInitialized = false;
      this.keyManager = this.chooseKeyManager(var1);
      if (var2 == null) {
         try {
            TrustManagerFactory var4 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            var4.init((KeyStore)null);
            var2 = var4.getTrustManagers();
         } catch (Exception var5) {
         }
      }

      this.trustManager = this.chooseTrustManager(var2);
      if (var3 == null) {
         this.secureRandom = JsseJce.getSecureRandom();
      } else {
         if (SunJSSE.isFIPS() && var3.getProvider() != SunJSSE.cryptoProvider) {
            throw new KeyManagementException("FIPS mode: SecureRandom must be from provider " + SunJSSE.cryptoProvider.getName());
         }

         this.secureRandom = var3;
      }

      if (debug != null && Debug.isOn("sslctx")) {
         System.out.println("trigger seeding of SecureRandom");
      }

      this.secureRandom.nextInt();
      if (debug != null && Debug.isOn("sslctx")) {
         System.out.println("done seeding SecureRandom");
      }

      this.isInitialized = true;
   }

   private X509TrustManager chooseTrustManager(TrustManager[] var1) throws KeyManagementException {
      for(int var2 = 0; var1 != null && var2 < var1.length; ++var2) {
         if (var1[var2] instanceof X509TrustManager) {
            if (SunJSSE.isFIPS() && !(var1[var2] instanceof X509TrustManagerImpl)) {
               throw new KeyManagementException("FIPS mode: only SunJSSE TrustManagers may be used");
            }

            if (var1[var2] instanceof X509ExtendedTrustManager) {
               return (X509TrustManager)var1[var2];
            }

            return new AbstractTrustManagerWrapper((X509TrustManager)var1[var2]);
         }
      }

      return DummyX509TrustManager.INSTANCE;
   }

   private X509ExtendedKeyManager chooseKeyManager(KeyManager[] var1) throws KeyManagementException {
      for(int var2 = 0; var1 != null && var2 < var1.length; ++var2) {
         KeyManager var3 = var1[var2];
         if (var3 instanceof X509KeyManager) {
            if (SunJSSE.isFIPS()) {
               if (!(var3 instanceof X509KeyManagerImpl) && !(var3 instanceof SunX509KeyManagerImpl)) {
                  throw new KeyManagementException("FIPS mode: only SunJSSE KeyManagers may be used");
               }

               return (X509ExtendedKeyManager)var3;
            }

            if (var3 instanceof X509ExtendedKeyManager) {
               return (X509ExtendedKeyManager)var3;
            }

            if (debug != null && Debug.isOn("sslctx")) {
               System.out.println("X509KeyManager passed to SSLContext.init():  need an X509ExtendedKeyManager for SSLEngine use");
            }

            return new AbstractKeyManagerWrapper((X509KeyManager)var3);
         }
      }

      return DummyX509KeyManager.INSTANCE;
   }

   protected SSLSocketFactory engineGetSocketFactory() {
      if (!this.isInitialized) {
         throw new IllegalStateException("SSLContextImpl is not initialized");
      } else {
         return new SSLSocketFactoryImpl(this);
      }
   }

   protected SSLServerSocketFactory engineGetServerSocketFactory() {
      if (!this.isInitialized) {
         throw new IllegalStateException("SSLContext is not initialized");
      } else {
         return new SSLServerSocketFactoryImpl(this);
      }
   }

   abstract SSLEngine createSSLEngineImpl();

   abstract SSLEngine createSSLEngineImpl(String var1, int var2);

   protected SSLEngine engineCreateSSLEngine() {
      if (!this.isInitialized) {
         throw new IllegalStateException("SSLContextImpl is not initialized");
      } else {
         return this.createSSLEngineImpl();
      }
   }

   protected SSLEngine engineCreateSSLEngine(String var1, int var2) {
      if (!this.isInitialized) {
         throw new IllegalStateException("SSLContextImpl is not initialized");
      } else {
         return this.createSSLEngineImpl(var1, var2);
      }
   }

   protected SSLSessionContext engineGetClientSessionContext() {
      return this.clientCache;
   }

   protected SSLSessionContext engineGetServerSessionContext() {
      return this.serverCache;
   }

   SecureRandom getSecureRandom() {
      return this.secureRandom;
   }

   X509ExtendedKeyManager getX509KeyManager() {
      return this.keyManager;
   }

   X509TrustManager getX509TrustManager() {
      return this.trustManager;
   }

   EphemeralKeyManager getEphemeralKeyManager() {
      return this.ephemeralKeyManager;
   }

   abstract ProtocolList getSuportedProtocolList();

   abstract ProtocolList getServerDefaultProtocolList();

   abstract ProtocolList getClientDefaultProtocolList();

   abstract CipherSuiteList getSupportedCipherSuiteList();

   abstract CipherSuiteList getServerDefaultCipherSuiteList();

   abstract CipherSuiteList getClientDefaultCipherSuiteList();

   ProtocolList getDefaultProtocolList(boolean var1) {
      return var1 ? this.getServerDefaultProtocolList() : this.getClientDefaultProtocolList();
   }

   CipherSuiteList getDefaultCipherSuiteList(boolean var1) {
      return var1 ? this.getServerDefaultCipherSuiteList() : this.getClientDefaultCipherSuiteList();
   }

   boolean isDefaultProtocolList(ProtocolList var1) {
      return var1 == this.getServerDefaultProtocolList() || var1 == this.getClientDefaultProtocolList();
   }

   boolean isDefaultCipherSuiteList(CipherSuiteList var1) {
      return var1 == this.getServerDefaultCipherSuiteList() || var1 == this.getClientDefaultCipherSuiteList();
   }

   private static CipherSuiteList getApplicableCipherSuiteList(ProtocolList var0, boolean var1) {
      short var2 = 1;
      if (var1) {
         var2 = 300;
      }

      Collection var3 = CipherSuite.allowedCipherSuites();
      TreeSet var4 = new TreeSet();
      if (!var0.collection().isEmpty() && var0.min.v != ProtocolVersion.NONE.v) {
         Iterator var5 = var3.iterator();

         while(true) {
            while(true) {
               CipherSuite var6;
               do {
                  do {
                     if (!var5.hasNext()) {
                        return new CipherSuiteList(var4);
                     }

                     var6 = (CipherSuite)var5.next();
                  } while(!var6.allowed);
               } while(var6.priority < var2);

               if (var6.isAvailable() && var6.obsoleted > var0.min.v && var6.supported <= var0.max.v) {
                  if (SSLAlgorithmConstraints.DEFAULT.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), var6.name, (AlgorithmParameters)null)) {
                     var4.add(var6);
                  } else if (debug != null && Debug.isOn("sslctx") && Debug.isOn("verbose")) {
                     System.out.println("Ignoring disabled cipher suite: " + var6.name);
                  }
               } else if (debug != null && Debug.isOn("sslctx") && Debug.isOn("verbose")) {
                  if (var6.obsoleted <= var0.min.v) {
                     System.out.println("Ignoring obsoleted cipher suite: " + var6);
                  } else if (var6.supported > var0.max.v) {
                     System.out.println("Ignoring unsupported cipher suite: " + var6);
                  } else {
                     System.out.println("Ignoring unavailable cipher suite: " + var6);
                  }
               }
            }
         }
      } else {
         return new CipherSuiteList(var4);
      }
   }

   private static String[] getAvailableProtocols(ProtocolVersion[] var0) {
      Object var1 = Collections.emptyList();
      if (var0 != null && var0.length != 0) {
         var1 = new ArrayList(var0.length);
         ProtocolVersion[] var2 = var0;
         int var3 = var0.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            ProtocolVersion var5 = var2[var4];
            if (ProtocolVersion.availableProtocols.contains(var5)) {
               ((List)var1).add(var5.name);
            }
         }
      }

      return (String[])((List)var1).toArray(new String[0]);
   }

   private abstract static class AbstractTLSContext extends SSLContextImpl {
      private static final ProtocolList supportedProtocolList;
      private static final ProtocolList serverDefaultProtocolList;
      private static final CipherSuiteList supportedCipherSuiteList;
      private static final CipherSuiteList serverDefaultCipherSuiteList;

      private AbstractTLSContext() {
      }

      ProtocolList getSuportedProtocolList() {
         return supportedProtocolList;
      }

      CipherSuiteList getSupportedCipherSuiteList() {
         return supportedCipherSuiteList;
      }

      ProtocolList getServerDefaultProtocolList() {
         return serverDefaultProtocolList;
      }

      CipherSuiteList getServerDefaultCipherSuiteList() {
         return serverDefaultCipherSuiteList;
      }

      SSLEngine createSSLEngineImpl() {
         return new SSLEngineImpl(this);
      }

      SSLEngine createSSLEngineImpl(String var1, int var2) {
         return new SSLEngineImpl(this, var1, var2);
      }

      static {
         if (SunJSSE.isFIPS()) {
            supportedProtocolList = new ProtocolList(new String[]{ProtocolVersion.TLS10.name, ProtocolVersion.TLS11.name, ProtocolVersion.TLS12.name});
            serverDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12}));
         } else {
            supportedProtocolList = new ProtocolList(new String[]{ProtocolVersion.SSL20Hello.name, ProtocolVersion.SSL30.name, ProtocolVersion.TLS10.name, ProtocolVersion.TLS11.name, ProtocolVersion.TLS12.name});
            serverDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.SSL20Hello, ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12}));
         }

         supportedCipherSuiteList = SSLContextImpl.getApplicableCipherSuiteList(supportedProtocolList, false);
         serverDefaultCipherSuiteList = SSLContextImpl.getApplicableCipherSuiteList(serverDefaultProtocolList, true);
      }
   }

   private static class CustomizedSSLProtocols {
      private static final String PROPERTY_NAME = "jdk.tls.client.protocols";
      static IllegalArgumentException reservedException = null;
      static ArrayList<ProtocolVersion> customizedProtocols = new ArrayList();

      private CustomizedSSLProtocols() {
      }

      static {
         String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.tls.client.protocols"));
         if (var0 != null && var0.length() != 0 && var0.length() > 1 && var0.charAt(0) == '"' && var0.charAt(var0.length() - 1) == '"') {
            var0 = var0.substring(1, var0.length() - 1);
         }

         if (var0 != null && var0.length() != 0) {
            String[] var1 = var0.split(",");

            for(int var2 = 0; var2 < var1.length; ++var2) {
               var1[var2] = var1[var2].trim();

               try {
                  ProtocolVersion var3 = ProtocolVersion.valueOf(var1[var2]);
                  if (SunJSSE.isFIPS() && (var3.v == ProtocolVersion.SSL30.v || var3.v == ProtocolVersion.SSL20Hello.v)) {
                     reservedException = new IllegalArgumentException("jdk.tls.client.protocols: " + var3 + " is not FIPS compliant");
                     break;
                  }

                  if (!customizedProtocols.contains(var3)) {
                     customizedProtocols.add(var3);
                  }
               } catch (IllegalArgumentException var4) {
                  reservedException = new IllegalArgumentException("jdk.tls.client.protocols: " + var1[var2] + " is not a standard SSL protocol name", var4);
               }
            }
         }

      }
   }

   private static class CustomizedTLSContext extends SSLContextImpl.AbstractTLSContext {
      private static final ProtocolList clientDefaultProtocolList;
      private static final CipherSuiteList clientDefaultCipherSuiteList;
      private static IllegalArgumentException reservedException = null;

      protected CustomizedTLSContext() {
//         super(null);
         if (reservedException != null) {
            throw reservedException;
         }
      }

      ProtocolList getClientDefaultProtocolList() {
         return clientDefaultProtocolList;
      }

      CipherSuiteList getClientDefaultCipherSuiteList() {
         return clientDefaultCipherSuiteList;
      }

      static {
         reservedException = SSLContextImpl.CustomizedSSLProtocols.reservedException;
         if (reservedException == null) {
            ArrayList var0 = new ArrayList();
            Iterator var1 = SSLContextImpl.CustomizedSSLProtocols.customizedProtocols.iterator();

            while(var1.hasNext()) {
               ProtocolVersion var2 = (ProtocolVersion)var1.next();
               var0.add(var2);
            }

            ProtocolVersion[] var3;
            if (var0.isEmpty()) {
               if (SunJSSE.isFIPS()) {
                  var3 = new ProtocolVersion[]{ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12};
               } else {
                  var3 = new ProtocolVersion[]{ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12};
               }
            } else {
               var3 = new ProtocolVersion[var0.size()];
               var3 = (ProtocolVersion[])var0.toArray(var3);
            }

            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(var3));
            clientDefaultCipherSuiteList = SSLContextImpl.getApplicableCipherSuiteList(clientDefaultProtocolList, true);
         } else {
            clientDefaultProtocolList = null;
            clientDefaultCipherSuiteList = null;
         }

      }
   }

   private static final class DefaultManagersHolder {
      private static final String NONE = "NONE";
      private static final String P11KEYSTORE = "PKCS11";
      private static final TrustManager[] trustManagers;
      private static final KeyManager[] keyManagers;
      static Exception reservedException = null;

      private DefaultManagersHolder() {
      }

      private static TrustManager[] getTrustManagers() throws Exception {
         KeyStore var0 = TrustManagerFactoryImpl.getCacertsKeyStore("defaultctx");
         TrustManagerFactory var1 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
         var1.init(var0);
         return var1.getTrustManagers();
      }

      private static KeyManager[] getKeyManagers() throws Exception {
         final HashMap var0 = new HashMap();
         AccessController.doPrivileged(new PrivilegedExceptionAction<Object>() {
            public Object run() throws Exception {
               var0.put("keyStore", System.getProperty("javax.net.ssl.keyStore", ""));
               var0.put("keyStoreType", System.getProperty("javax.net.ssl.keyStoreType", KeyStore.getDefaultType()));
               var0.put("keyStoreProvider", System.getProperty("javax.net.ssl.keyStoreProvider", ""));
               var0.put("keyStorePasswd", System.getProperty("javax.net.ssl.keyStorePassword", ""));
               return null;
            }
         });
         final String var1 = (String)var0.get("keyStore");
         String var2 = (String)var0.get("keyStoreType");
         String var3 = (String)var0.get("keyStoreProvider");
         if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
            System.out.println("keyStore is : " + var1);
            System.out.println("keyStore type is : " + var2);
            System.out.println("keyStore provider is : " + var3);
         }

         if ("PKCS11".equals(var2) && !"NONE".equals(var1)) {
            throw new IllegalArgumentException("if keyStoreType is PKCS11, then keyStore must be NONE");
         } else {
            FileInputStream var4 = null;
            KeyStore var5 = null;
            char[] var6 = null;

            try {
               if (var1.length() != 0 && !"NONE".equals(var1)) {
                  var4 = (FileInputStream)AccessController.doPrivileged(new PrivilegedExceptionAction<FileInputStream>() {
                     public FileInputStream run() throws Exception {
                        return new FileInputStream(var1);
                     }
                  });
               }

               String var7 = (String)var0.get("keyStorePasswd");
               if (var7.length() != 0) {
                  var6 = var7.toCharArray();
               }

               if (var2.length() != 0) {
                  if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
                     System.out.println("init keystore");
                  }

                  if (var3.length() == 0) {
                     var5 = KeyStore.getInstance(var2);
                  } else {
                     var5 = KeyStore.getInstance(var2, var3);
                  }

                  var5.load(var4, var6);
               }
            } finally {
               if (var4 != null) {
                  var4.close();
                  var4 = null;
               }

            }

            if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
               System.out.println("init keymanager of type " + KeyManagerFactory.getDefaultAlgorithm());
            }

            KeyManagerFactory var11 = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            if ("PKCS11".equals(var2)) {
               var11.init(var5, (char[])null);
            } else {
               var11.init(var5, var6);
            }

            return var11.getKeyManagers();
         }
      }

      static {
         TrustManager[] var0;
         try {
            var0 = getTrustManagers();
         } catch (Exception var4) {
            reservedException = var4;
            var0 = new TrustManager[0];
         }

         trustManagers = var0;
         if (reservedException == null) {
            KeyManager[] var1;
            try {
               var1 = getKeyManagers();
            } catch (Exception var3) {
               reservedException = var3;
               var1 = new KeyManager[0];
            }

            keyManagers = var1;
         } else {
            keyManagers = new KeyManager[0];
         }

      }
   }

   public static final class DefaultSSLContext extends SSLContextImpl.CustomizedTLSContext {
      public DefaultSSLContext() throws Exception {
         if (SSLContextImpl.DefaultManagersHolder.reservedException != null) {
            throw SSLContextImpl.DefaultManagersHolder.reservedException;
         } else {
            try {
               super.engineInit(SSLContextImpl.DefaultManagersHolder.keyManagers, SSLContextImpl.DefaultManagersHolder.trustManagers, (SecureRandom)null);
            } catch (Exception var2) {
               if (SSLContextImpl.debug != null && Debug.isOn("defaultctx")) {
                  System.out.println("default context init failed: " + var2);
               }

               throw var2;
            }
         }
      }

      protected void engineInit(KeyManager[] var1, TrustManager[] var2, SecureRandom var3) throws KeyManagementException {
         throw new KeyManagementException("Default SSLContext is initialized automatically");
      }

      static SSLContextImpl getDefaultImpl() throws Exception {
         if (SSLContextImpl.DefaultSSLContextHolder.reservedException != null) {
            throw SSLContextImpl.DefaultSSLContextHolder.reservedException;
         } else {
            return SSLContextImpl.DefaultSSLContextHolder.sslContext;
         }
      }
   }

   private static final class DefaultSSLContextHolder {
      private static final SSLContextImpl sslContext;
      static Exception reservedException = null;

      private DefaultSSLContextHolder() {
      }

      static {
         SSLContextImpl.DefaultSSLContext var0 = null;
         if (SSLContextImpl.DefaultManagersHolder.reservedException != null) {
            reservedException = SSLContextImpl.DefaultManagersHolder.reservedException;
         } else {
            try {
               var0 = new SSLContextImpl.DefaultSSLContext();
            } catch (Exception var2) {
               reservedException = var2;
            }
         }

         sslContext = var0;
      }
   }

   public static final class TLS10Context extends SSLContextImpl.AbstractTLSContext {
      private static final ProtocolList clientDefaultProtocolList;
      private static final CipherSuiteList clientDefaultCipherSuiteList;

      public TLS10Context() {
//         super(null);
      }

      ProtocolList getClientDefaultProtocolList() {
         return clientDefaultProtocolList;
      }

      CipherSuiteList getClientDefaultCipherSuiteList() {
         return clientDefaultCipherSuiteList;
      }

      static {
         if (SunJSSE.isFIPS()) {
            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.TLS10}));
         } else {
            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.SSL30, ProtocolVersion.TLS10}));
         }

         clientDefaultCipherSuiteList = SSLContextImpl.getApplicableCipherSuiteList(clientDefaultProtocolList, true);
      }
   }

   public static final class TLS11Context extends SSLContextImpl.AbstractTLSContext {
      private static final ProtocolList clientDefaultProtocolList;
      private static final CipherSuiteList clientDefaultCipherSuiteList;

      public TLS11Context() {
//         super(null);
      }

      ProtocolList getClientDefaultProtocolList() {
         return clientDefaultProtocolList;
      }

      CipherSuiteList getClientDefaultCipherSuiteList() {
         return clientDefaultCipherSuiteList;
      }

      static {
         if (SunJSSE.isFIPS()) {
            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.TLS10, ProtocolVersion.TLS11}));
         } else {
            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11}));
         }

         clientDefaultCipherSuiteList = SSLContextImpl.getApplicableCipherSuiteList(clientDefaultProtocolList, true);
      }
   }

   public static final class TLS12Context extends SSLContextImpl.AbstractTLSContext {
      private static final ProtocolList clientDefaultProtocolList;
      private static final CipherSuiteList clientDefaultCipherSuiteList;

      public TLS12Context() {
//         super(null);
      }

      ProtocolList getClientDefaultProtocolList() {
         return clientDefaultProtocolList;
      }

      CipherSuiteList getClientDefaultCipherSuiteList() {
         return clientDefaultCipherSuiteList;
      }

      static {
         if (SunJSSE.isFIPS()) {
            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12}));
         } else {
            clientDefaultProtocolList = new ProtocolList(SSLContextImpl.getAvailableProtocols(new ProtocolVersion[]{ProtocolVersion.SSL30, ProtocolVersion.TLS10, ProtocolVersion.TLS11, ProtocolVersion.TLS12}));
         }

         clientDefaultCipherSuiteList = SSLContextImpl.getApplicableCipherSuiteList(clientDefaultProtocolList, true);
      }
   }

   public static final class TLSContext extends SSLContextImpl.CustomizedTLSContext {
      public TLSContext() {
      }
   }
}
