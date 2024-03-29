package com.frojasg1.sun.security.krb5.internal.crypto;

import java.security.GeneralSecurityException;
import com.frojasg1.sun.security.krb5.KrbCryptoException;
import com.frojasg1.sun.security.krb5.internal.KrbApErrException;
import com.frojasg1.sun.security.krb5.internal.crypto.Aes256;
import com.frojasg1.sun.security.krb5.internal.crypto.EType;

public final class Aes256CtsHmacSha1EType extends EType {
   public Aes256CtsHmacSha1EType() {
   }

   public int eType() {
      return 18;
   }

   public int minimumPadSize() {
      return 0;
   }

   public int confounderSize() {
      return this.blockSize();
   }

   public int checksumType() {
      return 16;
   }

   public int checksumSize() {
      return com.frojasg1.sun.security.krb5.internal.crypto.Aes256.getChecksumLength();
   }

   public int blockSize() {
      return 16;
   }

   public int keyType() {
      return 3;
   }

   public int keySize() {
      return 32;
   }

   public byte[] encrypt(byte[] var1, byte[] var2, int var3) throws KrbCryptoException {
      byte[] var4 = new byte[this.blockSize()];
      return this.encrypt(var1, var2, var4, var3);
   }

   public byte[] encrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbCryptoException {
      try {
         return com.frojasg1.sun.security.krb5.internal.crypto.Aes256.encrypt(var2, var4, var3, var1, 0, var1.length);
      } catch (GeneralSecurityException var7) {
         KrbCryptoException var6 = new KrbCryptoException(var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }
   }

   public byte[] decrypt(byte[] var1, byte[] var2, int var3) throws KrbApErrException, KrbCryptoException {
      byte[] var4 = new byte[this.blockSize()];
      return this.decrypt(var1, var2, var4, var3);
   }

   public byte[] decrypt(byte[] var1, byte[] var2, byte[] var3, int var4) throws KrbApErrException, KrbCryptoException {
      try {
         return Aes256.decrypt(var2, var4, var3, var1, 0, var1.length);
      } catch (GeneralSecurityException var7) {
         KrbCryptoException var6 = new KrbCryptoException(var7.getMessage());
         var6.initCause(var7);
         throw var6;
      }
   }

   public byte[] decryptedData(byte[] var1) {
      return var1;
   }
}
