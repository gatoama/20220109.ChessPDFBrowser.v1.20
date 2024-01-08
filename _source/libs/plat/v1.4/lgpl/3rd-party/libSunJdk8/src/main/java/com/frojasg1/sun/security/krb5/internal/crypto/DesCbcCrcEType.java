package com.frojasg1.sun.security.krb5.internal.crypto;

import com.frojasg1.sun.security.krb5.KrbCryptoException;
import com.frojasg1.sun.security.krb5.internal.KrbApErrException;
import com.frojasg1.sun.security.krb5.internal.crypto.DesCbcEType;
import com.frojasg1.sun.security.krb5.internal.crypto.crc32;

public class DesCbcCrcEType extends com.frojasg1.sun.security.krb5.internal.crypto.DesCbcEType {
   public DesCbcCrcEType() {
   }

   public int eType() {
      return 1;
   }

   public int minimumPadSize() {
      return 4;
   }

   public int confounderSize() {
      return 8;
   }

   public int checksumType() {
      return 1;
   }

   public int checksumSize() {
      return 4;
   }

   public byte[] encrypt(byte[] var1, byte[] var2, int var3) throws KrbCryptoException {
      return this.encrypt(var1, var2, var2, var3);
   }

   public byte[] decrypt(byte[] var1, byte[] var2, int var3) throws KrbApErrException, KrbCryptoException {
      return this.decrypt(var1, var2, var2, var3);
   }

   protected byte[] calculateChecksum(byte[] var1, int var2) {
      return crc32.byte2crc32sum_bytes(var1, var2);
   }
}
