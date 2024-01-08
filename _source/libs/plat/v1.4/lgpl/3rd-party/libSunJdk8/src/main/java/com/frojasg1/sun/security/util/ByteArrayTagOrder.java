package com.frojasg1.sun.security.util;

import java.util.Comparator;

public class ByteArrayTagOrder implements Comparator<byte[]> {
   public ByteArrayTagOrder() {
   }

   public final int compare(byte[] var1, byte[] var2) {
      return (var1[0] | 32) - (var2[0] | 32);
   }
}
