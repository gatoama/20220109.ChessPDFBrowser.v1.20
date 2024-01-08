package com.frojasg1.sun.security.ssl;

import java.math.BigInteger;
import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.CryptoPrimitive;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PrivilegedAction;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;
import javax.net.ssl.SSLHandshakeException;
import com.frojasg1.sun.security.util.KeyUtil;

final class DHCrypt {
   private BigInteger modulus;
   private BigInteger base;
   private PrivateKey privateKey;
   private BigInteger publicValue;
   private static int MAX_FAILOVER_TIMES = 2;

   DHCrypt(int var1, SecureRandom var2) {
      this(var1, (DHParameterSpec)DHCrypt.ParametersHolder.definedParams.get(var1), var2);
   }

   DHCrypt(BigInteger var1, BigInteger var2, SecureRandom var3) {
      this(var1.bitLength(), new DHParameterSpec(var1, var2), var3);
   }

   private DHCrypt(int var1, DHParameterSpec var2, SecureRandom var3) {
      try {
         KeyPairGenerator var4 = JsseJce.getKeyPairGenerator("DiffieHellman");
         if (var2 != null) {
            var4.initialize(var2, var3);
         } else {
            var4.initialize(var1, var3);
         }

         DHPublicKeySpec var5 = this.generateDHPublicKeySpec(var4);
         if (var5 == null) {
            throw new RuntimeException("Could not generate DH keypair");
         } else {
            this.publicValue = var5.getY();
            this.modulus = var5.getP();
            this.base = var5.getG();
         }
      } catch (GeneralSecurityException var6) {
         throw new RuntimeException("Could not generate DH keypair", var6);
      }
   }

   static DHPublicKeySpec getDHPublicKeySpec(PublicKey var0) {
      if (var0 instanceof DHPublicKey) {
         DHPublicKey var4 = (DHPublicKey)var0;
         DHParameterSpec var2 = var4.getParams();
         return new DHPublicKeySpec(var4.getY(), var2.getP(), var2.getG());
      } else {
         try {
            KeyFactory var1 = JsseJce.getKeyFactory("DiffieHellman");
            return (DHPublicKeySpec)var1.getKeySpec(var0, DHPublicKeySpec.class);
         } catch (Exception var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   BigInteger getModulus() {
      return this.modulus;
   }

   BigInteger getBase() {
      return this.base;
   }

   BigInteger getPublicKey() {
      return this.publicValue;
   }

   SecretKey getAgreedSecret(BigInteger var1, boolean var2) throws SSLHandshakeException {
      try {
         KeyFactory var3 = JsseJce.getKeyFactory("DiffieHellman");
         DHPublicKeySpec var4 = new DHPublicKeySpec(var1, this.modulus, this.base);
         PublicKey var5 = var3.generatePublic(var4);
         KeyAgreement var6 = JsseJce.getKeyAgreement("DiffieHellman");
         if (!var2 && !KeyUtil.isOracleJCEProvider(var6.getProvider().getName())) {
            try {
               KeyUtil.validate(var4);
            } catch (InvalidKeyException var8) {
               throw new SSLHandshakeException(var8.getMessage());
            }
         }

         var6.init(this.privateKey);
         var6.doPhase(var5, true);
         return var6.generateSecret("TlsPremasterSecret");
      } catch (GeneralSecurityException var9) {
         throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate secret")).initCause(var9);
      }
   }

   void checkConstraints(AlgorithmConstraints var1, BigInteger var2) throws SSLHandshakeException {
      try {
         KeyFactory var3 = JsseJce.getKeyFactory("DiffieHellman");
         DHPublicKeySpec var4 = new DHPublicKeySpec(var2, this.modulus, this.base);
         DHPublicKey var5 = (DHPublicKey)var3.generatePublic(var4);
         if (!var1.permits(EnumSet.of(CryptoPrimitive.KEY_AGREEMENT), var5)) {
            throw new SSLHandshakeException("DHPublicKey does not comply to algorithm constraints");
         }
      } catch (GeneralSecurityException var6) {
         throw (SSLHandshakeException)(new SSLHandshakeException("Could not generate DHPublicKey")).initCause(var6);
      }
   }

   private DHPublicKeySpec generateDHPublicKeySpec(KeyPairGenerator var1) throws GeneralSecurityException {
      boolean var2 = !KeyUtil.isOracleJCEProvider(var1.getProvider().getName());
      int var3 = 0;

      DHPublicKeySpec var5;
      while(true) {
         if (var3 > MAX_FAILOVER_TIMES) {
            return null;
         }

         KeyPair var4 = var1.generateKeyPair();
         this.privateKey = var4.getPrivate();
         var5 = getDHPublicKeySpec(var4.getPublic());
         if (!var2) {
            break;
         }

         try {
            KeyUtil.validate(var5);
            break;
         } catch (InvalidKeyException var7) {
            if (var3 == MAX_FAILOVER_TIMES) {
               throw var7;
            }

            ++var3;
         }
      }

      return var5;
   }

   private static class ParametersHolder {
      private static final boolean debugIsOn = Debug.getInstance("ssl") != null && Debug.isOn("sslctx");
      private static final BigInteger g2 = BigInteger.valueOf(2L);
      private static final BigInteger p512 = new BigInteger("D87780E15FF50B4ABBE89870188B049406B5BEA98AB23A0241D88EA75B7755E669C08093D3F0CA7FC3A5A25CF067DCB9A43DD89D1D90921C6328884461E0B6D3", 16);
      private static final BigInteger p768 = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A63A3620FFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p1024 = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE65381FFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p1536 = new BigInteger("FFFFFFFFFFFFFFFFC90FDAA22168C234C4C6628B80DC1CD129024E088A67CC74020BBEA63B139B22514A08798E3404DDEF9519B3CD3A431B302B0A6DF25F14374FE1356D6D51C245E485B576625E7EC6F44C42E9A637ED6B0BFF5CB6F406B7EDEE386BFB5A899FA5AE9F24117C4B1FE649286651ECE45B3DC2007CB8A163BF0598DA48361C55D39A69163FA8FD24CF5F83655D23DCA3AD961C62F356208552BB9ED529077096966D670C354E4ABC9804F1746C08CA237327FFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p2048 = new BigInteger("FFFFFFFFFFFFFFFFADF85458A2BB4A9AAFDC5620273D3CF1D8B9C583CE2D3695A9E13641146433FBCC939DCE249B3EF97D2FE363630C75D8F681B202AEC4617AD3DF1ED5D5FD65612433F51F5F066ED0856365553DED1AF3B557135E7F57C935984F0C70E0E68B77E2A689DAF3EFE8721DF158A136ADE73530ACCA4F483A797ABC0AB182B324FB61D108A94BB2C8E3FBB96ADAB760D7F4681D4F42A3DE394DF4AE56EDE76372BB190B07A7C8EE0A6D709E02FCE1CDF7E2ECC03404CD28342F619172FE9CE98583FF8E4F1232EEF28183C3FE3B1B4C6FAD733BB5FCBC2EC22005C58EF1837D1683B2C6F34A26C1B2EFFA886B423861285C97FFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p3072 = new BigInteger("FFFFFFFFFFFFFFFFADF85458A2BB4A9AAFDC5620273D3CF1D8B9C583CE2D3695A9E13641146433FBCC939DCE249B3EF97D2FE363630C75D8F681B202AEC4617AD3DF1ED5D5FD65612433F51F5F066ED0856365553DED1AF3B557135E7F57C935984F0C70E0E68B77E2A689DAF3EFE8721DF158A136ADE73530ACCA4F483A797ABC0AB182B324FB61D108A94BB2C8E3FBB96ADAB760D7F4681D4F42A3DE394DF4AE56EDE76372BB190B07A7C8EE0A6D709E02FCE1CDF7E2ECC03404CD28342F619172FE9CE98583FF8E4F1232EEF28183C3FE3B1B4C6FAD733BB5FCBC2EC22005C58EF1837D1683B2C6F34A26C1B2EFFA886B4238611FCFDCDE355B3B6519035BBC34F4DEF99C023861B46FC9D6E6C9077AD91D2691F7F7EE598CB0FAC186D91CAEFE130985139270B4130C93BC437944F4FD4452E2D74DD364F2E21E71F54BFF5CAE82AB9C9DF69EE86D2BC522363A0DABC521979B0DEADA1DBF9A42D5C4484E0ABCD06BFA53DDEF3C1B20EE3FD59D7C25E41D2B66C62E37FFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p4096 = new BigInteger("FFFFFFFFFFFFFFFFADF85458A2BB4A9AAFDC5620273D3CF1D8B9C583CE2D3695A9E13641146433FBCC939DCE249B3EF97D2FE363630C75D8F681B202AEC4617AD3DF1ED5D5FD65612433F51F5F066ED0856365553DED1AF3B557135E7F57C935984F0C70E0E68B77E2A689DAF3EFE8721DF158A136ADE73530ACCA4F483A797ABC0AB182B324FB61D108A94BB2C8E3FBB96ADAB760D7F4681D4F42A3DE394DF4AE56EDE76372BB190B07A7C8EE0A6D709E02FCE1CDF7E2ECC03404CD28342F619172FE9CE98583FF8E4F1232EEF28183C3FE3B1B4C6FAD733BB5FCBC2EC22005C58EF1837D1683B2C6F34A26C1B2EFFA886B4238611FCFDCDE355B3B6519035BBC34F4DEF99C023861B46FC9D6E6C9077AD91D2691F7F7EE598CB0FAC186D91CAEFE130985139270B4130C93BC437944F4FD4452E2D74DD364F2E21E71F54BFF5CAE82AB9C9DF69EE86D2BC522363A0DABC521979B0DEADA1DBF9A42D5C4484E0ABCD06BFA53DDEF3C1B20EE3FD59D7C25E41D2B669E1EF16E6F52C3164DF4FB7930E9E4E58857B6AC7D5F42D69F6D187763CF1D5503400487F55BA57E31CC7A7135C886EFB4318AED6A1E012D9E6832A907600A918130C46DC778F971AD0038092999A333CB8B7A1A1DB93D7140003C2A4ECEA9F98D0ACC0A8291CDCEC97DCF8EC9B55A7F88A46B4DB5A851F44182E1C68A007E5E655F6AFFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p6144 = new BigInteger("FFFFFFFFFFFFFFFFADF85458A2BB4A9AAFDC5620273D3CF1D8B9C583CE2D3695A9E13641146433FBCC939DCE249B3EF97D2FE363630C75D8F681B202AEC4617AD3DF1ED5D5FD65612433F51F5F066ED0856365553DED1AF3B557135E7F57C935984F0C70E0E68B77E2A689DAF3EFE8721DF158A136ADE73530ACCA4F483A797ABC0AB182B324FB61D108A94BB2C8E3FBB96ADAB760D7F4681D4F42A3DE394DF4AE56EDE76372BB190B07A7C8EE0A6D709E02FCE1CDF7E2ECC03404CD28342F619172FE9CE98583FF8E4F1232EEF28183C3FE3B1B4C6FAD733BB5FCBC2EC22005C58EF1837D1683B2C6F34A26C1B2EFFA886B4238611FCFDCDE355B3B6519035BBC34F4DEF99C023861B46FC9D6E6C9077AD91D2691F7F7EE598CB0FAC186D91CAEFE130985139270B4130C93BC437944F4FD4452E2D74DD364F2E21E71F54BFF5CAE82AB9C9DF69EE86D2BC522363A0DABC521979B0DEADA1DBF9A42D5C4484E0ABCD06BFA53DDEF3C1B20EE3FD59D7C25E41D2B669E1EF16E6F52C3164DF4FB7930E9E4E58857B6AC7D5F42D69F6D187763CF1D5503400487F55BA57E31CC7A7135C886EFB4318AED6A1E012D9E6832A907600A918130C46DC778F971AD0038092999A333CB8B7A1A1DB93D7140003C2A4ECEA9F98D0ACC0A8291CDCEC97DCF8EC9B55A7F88A46B4DB5A851F44182E1C68A007E5E0DD9020BFD64B645036C7A4E677D2C38532A3A23BA4442CAF53EA63BB454329B7624C8917BDD64B1C0FD4CB38E8C334C701C3ACDAD0657FCCFEC719B1F5C3E4E46041F388147FB4CFDB477A52471F7A9A96910B855322EDB6340D8A00EF092350511E30ABEC1FFF9E3A26E7FB29F8C183023C3587E38DA0077D9B4763E4E4B94B2BBC194C6651E77CAF992EEAAC0232A281BF6B3A739C1226116820AE8DB5847A67CBEF9C9091B462D538CD72B03746AE77F5E62292C311562A846505DC82DB854338AE49F5235C95B91178CCF2DD5CACEF403EC9D1810C6272B045B3B71F9DC6B80D63FDD4A8E9ADB1E6962A69526D43161C1A41D570D7938DAD4A40E329CD0E40E65FFFFFFFFFFFFFFFF", 16);
      private static final BigInteger p8192 = new BigInteger("FFFFFFFFFFFFFFFFADF85458A2BB4A9AAFDC5620273D3CF1D8B9C583CE2D3695A9E13641146433FBCC939DCE249B3EF97D2FE363630C75D8F681B202AEC4617AD3DF1ED5D5FD65612433F51F5F066ED0856365553DED1AF3B557135E7F57C935984F0C70E0E68B77E2A689DAF3EFE8721DF158A136ADE73530ACCA4F483A797ABC0AB182B324FB61D108A94BB2C8E3FBB96ADAB760D7F4681D4F42A3DE394DF4AE56EDE76372BB190B07A7C8EE0A6D709E02FCE1CDF7E2ECC03404CD28342F619172FE9CE98583FF8E4F1232EEF28183C3FE3B1B4C6FAD733BB5FCBC2EC22005C58EF1837D1683B2C6F34A26C1B2EFFA886B4238611FCFDCDE355B3B6519035BBC34F4DEF99C023861B46FC9D6E6C9077AD91D2691F7F7EE598CB0FAC186D91CAEFE130985139270B4130C93BC437944F4FD4452E2D74DD364F2E21E71F54BFF5CAE82AB9C9DF69EE86D2BC522363A0DABC521979B0DEADA1DBF9A42D5C4484E0ABCD06BFA53DDEF3C1B20EE3FD59D7C25E41D2B669E1EF16E6F52C3164DF4FB7930E9E4E58857B6AC7D5F42D69F6D187763CF1D5503400487F55BA57E31CC7A7135C886EFB4318AED6A1E012D9E6832A907600A918130C46DC778F971AD0038092999A333CB8B7A1A1DB93D7140003C2A4ECEA9F98D0ACC0A8291CDCEC97DCF8EC9B55A7F88A46B4DB5A851F44182E1C68A007E5E0DD9020BFD64B645036C7A4E677D2C38532A3A23BA4442CAF53EA63BB454329B7624C8917BDD64B1C0FD4CB38E8C334C701C3ACDAD0657FCCFEC719B1F5C3E4E46041F388147FB4CFDB477A52471F7A9A96910B855322EDB6340D8A00EF092350511E30ABEC1FFF9E3A26E7FB29F8C183023C3587E38DA0077D9B4763E4E4B94B2BBC194C6651E77CAF992EEAAC0232A281BF6B3A739C1226116820AE8DB5847A67CBEF9C9091B462D538CD72B03746AE77F5E62292C311562A846505DC82DB854338AE49F5235C95B91178CCF2DD5CACEF403EC9D1810C6272B045B3B71F9DC6B80D63FDD4A8E9ADB1E6962A69526D43161C1A41D570D7938DAD4A40E329CCFF46AAA36AD004CF600C8381E425A31D951AE64FDB23FCEC9509D43687FEB69EDD1CC5E0B8CC3BDF64B10EF86B63142A3AB8829555B2F747C932665CB2C0F1CC01BD70229388839D2AF05E454504AC78B7582822846C0BA35C35F5C59160CC046FD8251541FC68C9C86B022BB7099876A460E7451A8A93109703FEE1C217E6C3826E52C51AA691E0E423CFC99E9E31650C1217B624816CDAD9A95F9D5B8019488D9C0A0A1FE3075A577E23183F81D4A3F2FA4571EFC8CE0BA8A4FE8B6855DFE72B0A66EDED2FBABFBE58A30FAFABE1C5D71A87E2F741EF8C1FE86FEA6BBFDE530677F0D97D11D49F7A8443D0822E506A9F4614E011E2A94838FF88CD68C8BB7C5C6424CFFFFFFFFFFFFFFFF", 16);
      private static final BigInteger[] supportedPrimes;
      private static final int PRIME_CERTAINTY = 120;
      private static final String PROPERTY_NAME = "jdk.tls.server.defaultDHEParameters";
      private static final Pattern spacesPattern;
      private static final Pattern syntaxPattern;
      private static final Pattern paramsPattern;
      private static final Map<Integer, DHParameterSpec> definedParams;

      private ParametersHolder() {
      }

      static {
         supportedPrimes = new BigInteger[]{p512, p768, p1024, p1536, p2048, p3072, p4096, p6144, p8192};
         spacesPattern = Pattern.compile("\\s+");
         syntaxPattern = Pattern.compile("(\\{[0-9A-Fa-f]+,[0-9A-Fa-f]+\\})(,\\{[0-9A-Fa-f]+,[0-9A-Fa-f]+\\})*");
         paramsPattern = Pattern.compile("\\{([0-9A-Fa-f]+),([0-9A-Fa-f]+)\\}");
         String var0 = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               return Security.getProperty("jdk.tls.server.defaultDHEParameters");
            }
         });
         if (var0 != null && !var0.isEmpty()) {
            if (var0.length() >= 2 && var0.charAt(0) == '"' && var0.charAt(var0.length() - 1) == '"') {
               var0 = var0.substring(1, var0.length() - 1);
            }

            var0 = var0.trim();
         }

         if (var0 != null && !var0.isEmpty()) {
            Matcher var1 = spacesPattern.matcher(var0);
            var0 = var1.replaceAll("");
            if (debugIsOn) {
               System.out.println("The Security Property jdk.tls.server.defaultDHEParameters: " + var0);
            }
         }

         HashMap var10 = new HashMap();
         BigInteger var5;
         if (var0 != null && !var0.isEmpty()) {
            Matcher var2 = syntaxPattern.matcher(var0);
            if (var2.matches()) {
               Matcher var3 = paramsPattern.matcher(var0);

               while(var3.find()) {
                  String var4 = var3.group(1);
                  var5 = new BigInteger(var4, 16);
                  if (!var5.isProbablePrime(120)) {
                     if (debugIsOn) {
                        System.out.println("Prime modulus p in Security Property, jdk.tls.server.defaultDHEParameters, is not a prime: " + var4);
                     }
                  } else {
                     String var6 = var3.group(2);
                     BigInteger var7 = new BigInteger(var6, 16);
                     DHParameterSpec var8 = new DHParameterSpec(var5, var7);
                     int var9 = var5.bitLength();
                     var10.put(var9, var8);
                  }
               }
            } else if (debugIsOn) {
               System.out.println("Invalid Security Property, jdk.tls.server.defaultDHEParameters, definition");
            }
         }

         BigInteger[] var11 = supportedPrimes;
         int var12 = var11.length;

         for(int var13 = 0; var13 < var12; ++var13) {
            var5 = var11[var13];
            int var14 = var5.bitLength();
            var10.putIfAbsent(var14, new DHParameterSpec(var5, g2));
         }

         definedParams = Collections.unmodifiableMap(var10);
      }
   }
}
