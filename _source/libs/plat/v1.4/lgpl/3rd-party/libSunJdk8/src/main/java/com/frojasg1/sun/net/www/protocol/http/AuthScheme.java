package com.frojasg1.sun.net.www.protocol.http;

public enum AuthScheme {
   BASIC,
   DIGEST,
   NTLM,
   NEGOTIATE,
   KERBEROS,
   UNKNOWN;

   private AuthScheme() {
   }
}
