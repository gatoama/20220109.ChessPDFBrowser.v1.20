package com.frojasg1.sun.security.internal.spec;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.spec.AlgorithmParameterSpec;

/** @deprecated */
@Deprecated
public class TlsRsaPremasterSecretParameterSpec implements AlgorithmParameterSpec {
   private final byte[] encodedSecret;
   private static final String PROP_NAME = "com.sun.net.ssl.rsaPreMasterSecretFix";
   private static final boolean rsaPreMasterSecretFix = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
      public Boolean run() {
         String var1 = System.getProperty("com.sun.net.ssl.rsaPreMasterSecretFix");
         return var1 != null && var1.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;
      }
   });
   private final int clientVersion;
   private final int serverVersion;

   public TlsRsaPremasterSecretParameterSpec(int var1, int var2) {
      this.clientVersion = this.checkVersion(var1);
      this.serverVersion = this.checkVersion(var2);
      this.encodedSecret = null;
   }

   public TlsRsaPremasterSecretParameterSpec(int var1, int var2, byte[] var3) {
      this.clientVersion = this.checkVersion(var1);
      this.serverVersion = this.checkVersion(var2);
      if (var3 != null && var3.length == 48) {
         this.encodedSecret = (byte[])var3.clone();
      } else {
         throw new IllegalArgumentException("Encoded secret is not exactly 48 bytes");
      }
   }

   public int getClientVersion() {
      return this.clientVersion;
   }

   public int getServerVersion() {
      return this.serverVersion;
   }

   public int getMajorVersion() {
      return !rsaPreMasterSecretFix && this.clientVersion < 770 ? this.serverVersion >>> 8 & 255 : this.clientVersion >>> 8 & 255;
   }

   public int getMinorVersion() {
      return !rsaPreMasterSecretFix && this.clientVersion < 770 ? this.serverVersion & 255 : this.clientVersion & 255;
   }

   private int checkVersion(int var1) {
      if (var1 >= 0 && var1 <= 65535) {
         return var1;
      } else {
         throw new IllegalArgumentException("Version must be between 0 and 65,535");
      }
   }

   public byte[] getEncodedSecret() {
      return this.encodedSecret == null ? null : (byte[])this.encodedSecret.clone();
   }
}
