package com.frojasg1.sun.security.jgss.spi;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import com.frojasg1.sun.security.jgss.spi.GSSNameSpi;

public interface GSSCredentialSpi {
   Provider getProvider();

   void dispose() throws GSSException;

   com.frojasg1.sun.security.jgss.spi.GSSNameSpi getName() throws GSSException;

   int getInitLifetime() throws GSSException;

   int getAcceptLifetime() throws GSSException;

   boolean isInitiatorCredential() throws GSSException;

   boolean isAcceptorCredential() throws GSSException;

   Oid getMechanism();

   GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException;
}
