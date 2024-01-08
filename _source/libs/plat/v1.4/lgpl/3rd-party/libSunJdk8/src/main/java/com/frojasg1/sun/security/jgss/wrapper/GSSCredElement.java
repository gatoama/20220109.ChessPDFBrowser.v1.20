package com.frojasg1.sun.security.jgss.wrapper;

import java.security.Provider;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import com.frojasg1.sun.security.jgss.GSSUtil;
import com.frojasg1.sun.security.jgss.spi.GSSCredentialSpi;
import com.frojasg1.sun.security.jgss.spi.GSSNameSpi;
import com.frojasg1.sun.security.jgss.wrapper.GSSLibStub;
import com.frojasg1.sun.security.jgss.wrapper.GSSNameElement;
import com.frojasg1.sun.security.jgss.wrapper.Krb5Util;
import com.frojasg1.sun.security.jgss.wrapper.SunNativeProvider;

public class GSSCredElement implements GSSCredentialSpi {
   private int usage;
   long pCred;
   private com.frojasg1.sun.security.jgss.wrapper.GSSNameElement name = null;
   private com.frojasg1.sun.security.jgss.wrapper.GSSLibStub cStub;

   void doServicePermCheck() throws GSSException {
      if (GSSUtil.isKerberosMech(this.cStub.getMech()) && System.getSecurityManager() != null) {
         String var1;
         if (this.isInitiatorCredential()) {
            var1 = com.frojasg1.sun.security.jgss.wrapper.Krb5Util.getTGSName(this.name);
            com.frojasg1.sun.security.jgss.wrapper.Krb5Util.checkServicePermission(var1, "initiate");
         }

         if (this.isAcceptorCredential() && this.name != com.frojasg1.sun.security.jgss.wrapper.GSSNameElement.DEF_ACCEPTOR) {
            var1 = this.name.getKrbName();
            com.frojasg1.sun.security.jgss.wrapper.Krb5Util.checkServicePermission(var1, "accept");
         }
      }

   }

   GSSCredElement(long var1, com.frojasg1.sun.security.jgss.wrapper.GSSNameElement var3, Oid var4) throws GSSException {
      this.pCred = var1;
      this.cStub = com.frojasg1.sun.security.jgss.wrapper.GSSLibStub.getInstance(var4);
      this.usage = 1;
      this.name = var3;
   }

   GSSCredElement(com.frojasg1.sun.security.jgss.wrapper.GSSNameElement var1, int var2, int var3, com.frojasg1.sun.security.jgss.wrapper.GSSLibStub var4) throws GSSException {
      this.cStub = var4;
      this.usage = var3;
      if (var1 != null) {
         this.name = var1;
         this.doServicePermCheck();
         this.pCred = this.cStub.acquireCred(this.name.pName, var2, var3);
      } else {
         this.pCred = this.cStub.acquireCred(0L, var2, var3);
         this.name = new com.frojasg1.sun.security.jgss.wrapper.GSSNameElement(this.cStub.getCredName(this.pCred), this.cStub);
         this.doServicePermCheck();
      }

   }

   public Provider getProvider() {
      return SunNativeProvider.INSTANCE;
   }

   public void dispose() throws GSSException {
      this.name = null;
      if (this.pCred != 0L) {
         this.pCred = this.cStub.releaseCred(this.pCred);
      }

   }

   public com.frojasg1.sun.security.jgss.wrapper.GSSNameElement getName() throws GSSException {
      return this.name == GSSNameElement.DEF_ACCEPTOR ? null : this.name;
   }

   public int getInitLifetime() throws GSSException {
      return this.isInitiatorCredential() ? this.cStub.getCredTime(this.pCred) : 0;
   }

   public int getAcceptLifetime() throws GSSException {
      return this.isAcceptorCredential() ? this.cStub.getCredTime(this.pCred) : 0;
   }

   public boolean isInitiatorCredential() {
      return this.usage != 2;
   }

   public boolean isAcceptorCredential() {
      return this.usage != 1;
   }

   public Oid getMechanism() {
      return this.cStub.getMech();
   }

   public String toString() {
      return "N/A";
   }

   protected void finalize() throws Throwable {
      this.dispose();
   }

   public GSSCredentialSpi impersonate(GSSNameSpi var1) throws GSSException {
      throw new GSSException(11, -1, "Not supported yet");
   }
}
