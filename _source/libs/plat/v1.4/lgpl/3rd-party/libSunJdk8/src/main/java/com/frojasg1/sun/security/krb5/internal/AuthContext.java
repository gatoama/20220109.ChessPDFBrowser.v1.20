package com.frojasg1.sun.security.krb5.internal;

import java.util.BitSet;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.internal.Authenticator;
import com.frojasg1.sun.security.krb5.internal.HostAddress;

public class AuthContext {
   public com.frojasg1.sun.security.krb5.internal.HostAddress remoteAddress;
   public int remotePort;
   public HostAddress localAddress;
   public int localPort;
   public EncryptionKey keyBlock;
   public EncryptionKey localSubkey;
   public EncryptionKey remoteSubkey;
   public BitSet authContextFlags;
   public int remoteSeqNumber;
   public int localSeqNumber;
   public Authenticator authenticator;
   public int reqCksumType;
   public int safeCksumType;
   public byte[] initializationVector;

   public AuthContext() {
   }
}
