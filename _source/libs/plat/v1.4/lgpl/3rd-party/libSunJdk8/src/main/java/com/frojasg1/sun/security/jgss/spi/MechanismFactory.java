package com.frojasg1.sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import com.frojasg1.sun.security.jgss.spi.GSSContextSpi;
import com.frojasg1.sun.security.jgss.spi.GSSCredentialSpi;
import com.frojasg1.sun.security.jgss.spi.GSSNameSpi;

public interface MechanismFactory {
   Oid getMechanismOid();

   Provider getProvider();

   Oid[] getNameTypes() throws GSSException;

   com.frojasg1.sun.security.jgss.spi.GSSCredentialSpi getCredentialElement(com.frojasg1.sun.security.jgss.spi.GSSNameSpi var1, int var2, int var3, int var4) throws GSSException;

   com.frojasg1.sun.security.jgss.spi.GSSNameSpi getNameElement(String var1, Oid var2) throws GSSException;

   com.frojasg1.sun.security.jgss.spi.GSSNameSpi getNameElement(byte[] var1, Oid var2) throws GSSException;

   com.frojasg1.sun.security.jgss.spi.GSSContextSpi getMechanismContext(GSSNameSpi var1, com.frojasg1.sun.security.jgss.spi.GSSCredentialSpi var2, int var3) throws GSSException;

   com.frojasg1.sun.security.jgss.spi.GSSContextSpi getMechanismContext(GSSCredentialSpi var1) throws GSSException;

   GSSContextSpi getMechanismContext(byte[] var1) throws GSSException;
}
