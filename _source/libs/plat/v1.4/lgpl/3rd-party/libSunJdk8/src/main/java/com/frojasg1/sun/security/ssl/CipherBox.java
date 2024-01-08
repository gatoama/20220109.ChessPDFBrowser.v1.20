package com.frojasg1.sun.security.ssl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Hashtable;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import com.frojasg1.sun.misc.HexDumpEncoder;

final class CipherBox {
   static final CipherBox NULL = new CipherBox();
   private static final Debug debug = Debug.getInstance("ssl");
   private final ProtocolVersion protocolVersion;
   private final Cipher cipher;
   private SecureRandom random;
   private final byte[] fixedIv;
   private final Key key;
   private final int mode;
   private final int tagSize;
   private final int recordIvSize;
   private final CipherSuite.CipherType cipherType;
   private static Hashtable<Integer, IvParameterSpec> masks;

   private CipherBox() {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.cipher = null;
      this.cipherType = CipherSuite.CipherType.STREAM_CIPHER;
      this.fixedIv = new byte[0];
      this.key = null;
      this.mode = 1;
      this.random = null;
      this.tagSize = 0;
      this.recordIvSize = 0;
   }

   private CipherBox(ProtocolVersion var1, CipherSuite.BulkCipher var2, SecretKey var3, IvParameterSpec var4, SecureRandom var5, boolean var6) throws NoSuchAlgorithmException {
      try {
         this.protocolVersion = var1;
         this.cipher = JsseJce.getCipher(var2.transformation);
         this.mode = var6 ? 1 : 2;
         if (var5 == null) {
            var5 = JsseJce.getSecureRandom();
         }

         this.random = var5;
         this.cipherType = var2.cipherType;
         if (var4 == null && var2.ivSize != 0 && this.mode == 2 && var1.v >= ProtocolVersion.TLS11.v) {
            var4 = getFixedMask(var2.ivSize);
         }

         if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
            var2.getClass();
            this.tagSize = 16;
            this.key = var3;
            this.fixedIv = var4.getIV();
            if (this.fixedIv == null || this.fixedIv.length != var2.fixedIvSize) {
               throw new RuntimeException("Improper fixed IV for AEAD");
            }

            this.recordIvSize = var2.ivSize - var2.fixedIvSize;
         } else {
            this.tagSize = 0;
            this.fixedIv = new byte[0];
            this.recordIvSize = 0;
            this.key = null;
            this.cipher.init(this.mode, var3, var4, var5);
         }

      } catch (NoSuchAlgorithmException var8) {
         throw var8;
      } catch (Exception var9) {
         throw new NoSuchAlgorithmException("Could not create cipher " + var2, var9);
      } catch (ExceptionInInitializerError var10) {
         throw new NoSuchAlgorithmException("Could not create cipher " + var2, var10);
      }
   }

   static CipherBox newCipherBox(ProtocolVersion var0, CipherSuite.BulkCipher var1, SecretKey var2, IvParameterSpec var3, SecureRandom var4, boolean var5) throws NoSuchAlgorithmException {
      if (!var1.allowed) {
         throw new NoSuchAlgorithmException("Unsupported cipher " + var1);
      } else {
         return var1 == CipherSuite.B_NULL ? NULL : new CipherBox(var0, var1, var2, var3, var4, var5);
      }
   }

   private static IvParameterSpec getFixedMask(int var0) {
      if (masks == null) {
         masks = new Hashtable(5);
      }

      IvParameterSpec var1 = (IvParameterSpec)masks.get(var0);
      if (var1 == null) {
         var1 = new IvParameterSpec(new byte[var0]);
         masks.put(var0, var1);
      }

      return var1;
   }

   int encrypt(byte[] var1, int var2, int var3) {
      if (this.cipher == null) {
         return var3;
      } else {
         try {
            int var4 = this.cipher.getBlockSize();
            if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
               var3 = addPadding(var1, var2, var3, var4);
            }

            if (debug != null && Debug.isOn("plaintext")) {
               try {
                  HexDumpEncoder var5 = new HexDumpEncoder();
                  System.out.println("Padded plaintext before ENCRYPTION:  len = " + var3);
                  var5.encodeBuffer(new ByteArrayInputStream(var1, var2, var3), System.out);
               } catch (IOException var7) {
               }
            }

            if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
               try {
                  return this.cipher.doFinal(var1, var2, var3, var1, var2);
               } catch (BadPaddingException | IllegalBlockSizeException var6) {
                  throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), var6);
               }
            } else {
               int var9 = this.cipher.update(var1, var2, var3, var1, var2);
               if (var9 != var3) {
                  throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
               } else {
                  return var9;
               }
            }
         } catch (ShortBufferException var8) {
            throw new ArrayIndexOutOfBoundsException(var8.toString());
         }
      }
   }

   int encrypt(ByteBuffer var1, int var2) {
      int var3 = var1.remaining();
      if (this.cipher == null) {
         var1.position(var1.limit());
         return var3;
      } else {
         int var4 = var1.position();
         int var5 = this.cipher.getBlockSize();
         if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
            var3 = addPadding(var1, var5);
            var1.position(var4);
         }

         if (debug != null && Debug.isOn("plaintext")) {
            try {
               HexDumpEncoder var6 = new HexDumpEncoder();
               System.out.println("Padded plaintext before ENCRYPTION:  len = " + var3);
               var6.encodeBuffer(var1.duplicate(), System.out);
            } catch (IOException var11) {
            }
         }

         ByteBuffer var12 = var1.duplicate();
         int var7;
         if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
            try {
               var7 = this.cipher.getOutputSize(var12.remaining());
               if (var7 > var1.remaining()) {
                  if (var2 < var4 + var7) {
                     throw new ShortBufferException("need more space in output buffer");
                  }

                  var1.limit(var4 + var7);
               }

               int var8 = this.cipher.doFinal(var12, var1);
               if (var8 != var7) {
                  throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
               } else {
                  return var8;
               }
            } catch (BadPaddingException | ShortBufferException | IllegalBlockSizeException var9) {
               throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), var9);
            }
         } else {
            try {
               var7 = this.cipher.update(var12, var1);
            } catch (ShortBufferException var10) {
               throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
            }

            if (var1.position() != var12.position()) {
               throw new RuntimeException("bytebuffer padding error");
            } else if (var7 != var3) {
               throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
            } else {
               return var7;
            }
         }
      }
   }

   int decrypt(byte[] var1, int var2, int var3, int var4) throws BadPaddingException {
      if (this.cipher == null) {
         return var3;
      } else {
         try {
            int var5;
            if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
               try {
                  var5 = this.cipher.doFinal(var1, var2, var3, var1, var2);
               } catch (IllegalBlockSizeException var8) {
                  throw new RuntimeException("Cipher error in AEAD mode in JCE provider " + this.cipher.getProvider().getName(), var8);
               }
            } else {
               var5 = this.cipher.update(var1, var2, var3, var1, var2);
               if (var5 != var3) {
                  throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
               }
            }

            if (debug != null && Debug.isOn("plaintext")) {
               try {
                  HexDumpEncoder var6 = new HexDumpEncoder();
                  System.out.println("Padded plaintext after DECRYPTION:  len = " + var5);
                  var6.encodeBuffer(new ByteArrayInputStream(var1, var2, var5), System.out);
               } catch (IOException var7) {
               }
            }

            if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
               int var10 = this.cipher.getBlockSize();
               var5 = removePadding(var1, var2, var5, var4, var10, this.protocolVersion);
               if (this.protocolVersion.v >= ProtocolVersion.TLS11.v && var5 < var10) {
                  throw new BadPaddingException("The length after padding removal (" + var5 + ") should be larger than <" + var10 + "> since explicit IV used");
               }
            }

            return var5;
         } catch (ShortBufferException var9) {
            throw new ArrayIndexOutOfBoundsException(var9.toString());
         }
      }
   }

   int decrypt(ByteBuffer var1, int var2) throws BadPaddingException {
      int var3 = var1.remaining();
      if (this.cipher == null) {
         var1.position(var1.limit());
         return var3;
      } else {
         try {
            int var4 = var1.position();
            ByteBuffer var5 = var1.duplicate();
            int var6;
            if (this.cipherType == CipherSuite.CipherType.AEAD_CIPHER) {
               try {
                  var6 = this.cipher.doFinal(var5, var1);
               } catch (IllegalBlockSizeException var9) {
                  throw new RuntimeException("Cipher error in AEAD mode \"" + var9.getMessage() + " \"in JCE provider " + this.cipher.getProvider().getName());
               }
            } else {
               var6 = this.cipher.update(var5, var1);
               if (var6 != var3) {
                  throw new RuntimeException("Cipher buffering error in JCE provider " + this.cipher.getProvider().getName());
               }
            }

            var1.limit(var4 + var6);
            if (debug != null && Debug.isOn("plaintext")) {
               try {
                  HexDumpEncoder var7 = new HexDumpEncoder();
                  System.out.println("Padded plaintext after DECRYPTION:  len = " + var6);
                  var7.encodeBuffer((ByteBuffer)var1.duplicate().position(var4), System.out);
               } catch (IOException var8) {
               }
            }

            if (this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER) {
               int var11 = this.cipher.getBlockSize();
               var1.position(var4);
               var6 = removePadding(var1, var2, var11, this.protocolVersion);
               if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
                  if (var6 < var11) {
                     throw new BadPaddingException("The length after padding removal (" + var6 + ") should be larger than <" + var11 + "> since explicit IV used");
                  }

                  var1.position(var1.limit());
               }
            }

            return var6;
         } catch (ShortBufferException var10) {
            throw new ArrayIndexOutOfBoundsException(var10.toString());
         }
      }
   }

   private static int addPadding(byte[] var0, int var1, int var2, int var3) {
      int var4 = var2 + 1;
      if (var4 % var3 != 0) {
         var4 += var3 - 1;
         var4 -= var4 % var3;
      }

      byte var5 = (byte)(var4 - var2);
      if (var0.length < var4 + var1) {
         throw new IllegalArgumentException("no space to pad buffer");
      } else {
         int var6 = 0;

         for(var1 += var2; var6 < var5; ++var6) {
            var0[var1++] = (byte)(var5 - 1);
         }

         return var4;
      }
   }

   private static int addPadding(ByteBuffer var0, int var1) {
      int var2 = var0.remaining();
      int var3 = var0.position();
      int var4 = var2 + 1;
      if (var4 % var1 != 0) {
         var4 += var1 - 1;
         var4 -= var4 % var1;
      }

      byte var5 = (byte)(var4 - var2);
      var0.limit(var4 + var3);
      int var6 = 0;

      for(var3 += var2; var6 < var5; ++var6) {
         var0.put(var3++, (byte)(var5 - 1));
      }

      var0.position(var3);
      var0.limit(var3);
      return var4;
   }

   private static int[] checkPadding(byte[] var0, int var1, int var2, byte var3) {
      if (var2 <= 0) {
         throw new RuntimeException("padding len must be positive");
      } else {
         int[] var4 = new int[]{0, 0};
         int var5 = 0;

         while(var5 <= 256) {
            for(int var6 = 0; var6 < var2 && var5 <= 256; ++var5) {
               int var10002;
               if (var0[var1 + var6] != var3) {
                  var10002 = var4[0]++;
               } else {
                  var10002 = var4[1]++;
               }

               ++var6;
            }
         }

         return var4;
      }
   }

   private static int[] checkPadding(ByteBuffer var0, byte var1) {
      if (!var0.hasRemaining()) {
         throw new RuntimeException("hasRemaining() must be positive");
      } else {
         int[] var2 = new int[]{0, 0};
         var0.mark();
         int var3 = 0;

         while(var3 <= 256) {
            for(; var0.hasRemaining() && var3 <= 256; ++var3) {
               int var10002;
               if (var0.get() != var1) {
                  var10002 = var2[0]++;
               } else {
                  var10002 = var2[1]++;
               }
            }

            var0.reset();
         }

         return var2;
      }
   }

   private static int removePadding(byte[] var0, int var1, int var2, int var3, int var4, ProtocolVersion var5) throws BadPaddingException {
      int var6 = var1 + var2 - 1;
      int var7 = var0[var6] & 255;
      int var8 = var2 - (var7 + 1);
      if (var8 - var3 < 0) {
         checkPadding(var0, var1, var2, (byte)(var7 & 255));
         throw new BadPaddingException("Invalid Padding length: " + var7);
      } else {
         int[] var9 = checkPadding(var0, var1 + var8, var7 + 1, (byte)(var7 & 255));
         if (var5.v >= ProtocolVersion.TLS10.v) {
            if (var9[0] != 0) {
               throw new BadPaddingException("Invalid TLS padding data");
            }
         } else if (var7 > var4) {
            throw new BadPaddingException("Padding length (" + var7 + ") of SSLv3 message should not be bigger than the block size (" + var4 + ")");
         }

         return var8;
      }
   }

   private static int removePadding(ByteBuffer var0, int var1, int var2, ProtocolVersion var3) throws BadPaddingException {
      int var4 = var0.remaining();
      int var5 = var0.position();
      int var6 = var5 + var4 - 1;
      int var7 = var0.get(var6) & 255;
      int var8 = var4 - (var7 + 1);
      if (var8 - var1 < 0) {
         checkPadding(var0.duplicate(), (byte)(var7 & 255));
         throw new BadPaddingException("Invalid Padding length: " + var7);
      } else {
         int[] var9 = checkPadding((ByteBuffer)var0.duplicate().position(var5 + var8), (byte)(var7 & 255));
         if (var3.v >= ProtocolVersion.TLS10.v) {
            if (var9[0] != 0) {
               throw new BadPaddingException("Invalid TLS padding data");
            }
         } else if (var7 > var2) {
            throw new BadPaddingException("Padding length (" + var7 + ") of SSLv3 message should not be bigger than the block size (" + var2 + ")");
         }

         var0.position(var5 + var8);
         var0.limit(var5 + var8);
         return var8;
      }
   }

   void dispose() {
      try {
         if (this.cipher != null) {
            this.cipher.doFinal();
         }
      } catch (Exception var2) {
      }

   }

   boolean isCBCMode() {
      return this.cipherType == CipherSuite.CipherType.BLOCK_CIPHER;
   }

   boolean isAEADMode() {
      return this.cipherType == CipherSuite.CipherType.AEAD_CIPHER;
   }

   boolean isNullCipher() {
      return this.cipher == null;
   }

   int getExplicitNonceSize() {
      switch(this.cipherType) {
      case BLOCK_CIPHER:
         if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
            return this.cipher.getBlockSize();
         }
      default:
         return 0;
      case AEAD_CIPHER:
         return this.recordIvSize;
      }
   }

   int applyExplicitNonce(Authenticator var1, byte var2, ByteBuffer var3) throws BadPaddingException {
      switch(this.cipherType) {
      case BLOCK_CIPHER:
         int var4 = var1 instanceof MAC ? ((MAC)var1).MAClen() : 0;
         if (var4 != 0 && !this.sanityCheck(var4, var3.remaining())) {
            throw new BadPaddingException("ciphertext sanity check failed");
         } else if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
            return this.cipher.getBlockSize();
         }
      default:
         return 0;
      case AEAD_CIPHER:
         if (var3.remaining() < this.recordIvSize + this.tagSize) {
            throw new BadPaddingException("Insufficient buffer remaining for AEAD cipher fragment (" + var3.remaining() + "). Needs to be more than or equal to IV size (" + this.recordIvSize + ") + tag size (" + this.tagSize + ")");
         } else {
            byte[] var5 = Arrays.copyOf(this.fixedIv, this.fixedIv.length + this.recordIvSize);
            var3.get(var5, this.fixedIv.length, this.recordIvSize);
            var3.position(var3.position() - this.recordIvSize);
            GCMParameterSpec var6 = new GCMParameterSpec(this.tagSize * 8, var5);

            try {
               this.cipher.init(this.mode, this.key, var6, this.random);
            } catch (InvalidAlgorithmParameterException | InvalidKeyException var8) {
               throw new RuntimeException("invalid key or spec in GCM mode", var8);
            }

            byte[] var7 = var1.acquireAuthenticationBytes(var2, var3.remaining() - this.recordIvSize - this.tagSize);
            this.cipher.updateAAD(var7);
            return this.recordIvSize;
         }
      }
   }

   int applyExplicitNonce(Authenticator var1, byte var2, byte[] var3, int var4, int var5) throws BadPaddingException {
      ByteBuffer var6 = ByteBuffer.wrap(var3, var4, var5);
      return this.applyExplicitNonce(var1, var2, var6);
   }

   byte[] createExplicitNonce(Authenticator var1, byte var2, int var3) {
      byte[] var4 = new byte[0];
      switch(this.cipherType) {
      case BLOCK_CIPHER:
         if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
            var4 = new byte[this.cipher.getBlockSize()];
            this.random.nextBytes(var4);
         }
         break;
      case AEAD_CIPHER:
         var4 = var1.sequenceNumber();
         byte[] var5 = Arrays.copyOf(this.fixedIv, this.fixedIv.length + var4.length);
         System.arraycopy(var4, 0, var5, this.fixedIv.length, var4.length);
         GCMParameterSpec var6 = new GCMParameterSpec(this.tagSize * 8, var5);

         try {
            this.cipher.init(this.mode, this.key, var6, this.random);
         } catch (InvalidAlgorithmParameterException | InvalidKeyException var8) {
            throw new RuntimeException("invalid key or spec in GCM mode", var8);
         }

         byte[] var7 = var1.acquireAuthenticationBytes(var2, var3);
         this.cipher.updateAAD(var7);
      }

      return var4;
   }

   private boolean sanityCheck(int var1, int var2) {
      if (!this.isCBCMode()) {
         return var2 >= var1;
      } else {
         int var3 = this.cipher.getBlockSize();
         if (var2 % var3 == 0) {
            int var4 = var1 + 1;
            var4 = var4 >= var3 ? var4 : var3;
            if (this.protocolVersion.v >= ProtocolVersion.TLS11.v) {
               var4 += var3;
            }

            return var2 >= var4;
         } else {
            return false;
         }
      }
   }
}
