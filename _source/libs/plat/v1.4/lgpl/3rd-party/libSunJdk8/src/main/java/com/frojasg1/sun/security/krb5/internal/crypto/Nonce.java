package com.frojasg1.sun.security.krb5.internal.crypto;

import com.frojasg1.sun.security.krb5.Confounder;

public class Nonce {
   public Nonce() {
   }

   public static synchronized int value() {
      return Confounder.intValue() & 2147483647;
   }
}
