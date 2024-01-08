package com.frojasg1.sun.security.internal.spec;

import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.SecretKey;

/** @deprecated */
@Deprecated
public class TlsMasterSecretParameterSpec implements AlgorithmParameterSpec {
   private final SecretKey premasterSecret;
   private final int majorVersion;
   private final int minorVersion;
   private final byte[] clientRandom;
   private final byte[] serverRandom;
   private final byte[] extendedMasterSecretSessionHash;
   private final String prfHashAlg;
   private final int prfHashLength;
   private final int prfBlockSize;

   public TlsMasterSecretParameterSpec(SecretKey var1, int var2, int var3, byte[] var4, byte[] var5, String var6, int var7, int var8) {
      this(var1, var2, var3, var4, var5, new byte[0], var6, var7, var8);
   }

   public TlsMasterSecretParameterSpec(SecretKey var1, int var2, int var3, byte[] var4, String var5, int var6, int var7) {
      this(var1, var2, var3, new byte[0], new byte[0], var4, var5, var6, var7);
   }

   private TlsMasterSecretParameterSpec(SecretKey var1, int var2, int var3, byte[] var4, byte[] var5, byte[] var6, String var7, int var8, int var9) {
      if (var1 == null) {
         throw new NullPointerException("premasterSecret must not be null");
      } else {
         this.premasterSecret = var1;
         this.majorVersion = checkVersion(var2);
         this.minorVersion = checkVersion(var3);
         this.clientRandom = (byte[])var4.clone();
         this.serverRandom = (byte[])var5.clone();
         this.extendedMasterSecretSessionHash = var6 != null ? (byte[])var6.clone() : new byte[0];
         this.prfHashAlg = var7;
         this.prfHashLength = var8;
         this.prfBlockSize = var9;
      }
   }

   static int checkVersion(int var0) {
      if (var0 >= 0 && var0 <= 255) {
         return var0;
      } else {
         throw new IllegalArgumentException("Version must be between 0 and 255");
      }
   }

   public SecretKey getPremasterSecret() {
      return this.premasterSecret;
   }

   public int getMajorVersion() {
      return this.majorVersion;
   }

   public int getMinorVersion() {
      return this.minorVersion;
   }

   public byte[] getClientRandom() {
      return (byte[])this.clientRandom.clone();
   }

   public byte[] getServerRandom() {
      return (byte[])this.serverRandom.clone();
   }

   public byte[] getExtendedMasterSecretSessionHash() {
      return (byte[])this.extendedMasterSecretSessionHash.clone();
   }

   public String getPRFHashAlg() {
      return this.prfHashAlg;
   }

   public int getPRFHashLength() {
      return this.prfHashLength;
   }

   public int getPRFBlockSize() {
      return this.prfBlockSize;
   }
}
