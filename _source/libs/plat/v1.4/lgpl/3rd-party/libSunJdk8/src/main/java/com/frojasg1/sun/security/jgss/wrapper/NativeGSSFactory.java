package com.frojasg1.sun.security.jgss.wrapper;

import java.io.UnsupportedEncodingException;
import java.security.Provider;
import java.util.Vector;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.Oid;
import com.frojasg1.sun.security.jgss.GSSCaller;
import com.frojasg1.sun.security.jgss.GSSExceptionImpl;
import com.frojasg1.sun.security.jgss.GSSUtil;
import com.frojasg1.sun.security.jgss.spi.GSSContextSpi;
import com.frojasg1.sun.security.jgss.spi.GSSCredentialSpi;
import com.frojasg1.sun.security.jgss.spi.GSSNameSpi;
import com.frojasg1.sun.security.jgss.spi.MechanismFactory;
import com.frojasg1.sun.security.jgss.wrapper.GSSCredElement;
import com.frojasg1.sun.security.jgss.wrapper.GSSLibStub;
import com.frojasg1.sun.security.jgss.wrapper.GSSNameElement;
import com.frojasg1.sun.security.jgss.wrapper.NativeGSSContext;
import com.frojasg1.sun.security.jgss.wrapper.SunNativeProvider;

public final class NativeGSSFactory implements MechanismFactory {
   com.frojasg1.sun.security.jgss.wrapper.GSSLibStub cStub = null;
   private final GSSCaller caller;

   private com.frojasg1.sun.security.jgss.wrapper.GSSCredElement getCredFromSubject(com.frojasg1.sun.security.jgss.wrapper.GSSNameElement var1, boolean var2) throws GSSException {
      Oid var3 = this.cStub.getMech();
      Vector var4 = GSSUtil.searchSubject(var1, var3, var2, com.frojasg1.sun.security.jgss.wrapper.GSSCredElement.class);
      if (var4 != null && var4.isEmpty() && GSSUtil.useSubjectCredsOnly(this.caller)) {
         throw new GSSException(13);
      } else {
         com.frojasg1.sun.security.jgss.wrapper.GSSCredElement var5 = var4 != null && !var4.isEmpty() ? (com.frojasg1.sun.security.jgss.wrapper.GSSCredElement)var4.firstElement() : null;
         if (var5 != null) {
            var5.doServicePermCheck();
         }

         return var5;
      }
   }

   public NativeGSSFactory(GSSCaller var1) {
      this.caller = var1;
   }

   public void setMech(Oid var1) throws GSSException {
      this.cStub = com.frojasg1.sun.security.jgss.wrapper.GSSLibStub.getInstance(var1);
   }

   public GSSNameSpi getNameElement(String var1, Oid var2) throws GSSException {
      try {
         byte[] var3 = var1 == null ? null : var1.getBytes("UTF-8");
         return new com.frojasg1.sun.security.jgss.wrapper.GSSNameElement(var3, var2, this.cStub);
      } catch (UnsupportedEncodingException var4) {
         throw new GSSExceptionImpl(11, var4);
      }
   }

   public GSSNameSpi getNameElement(byte[] var1, Oid var2) throws GSSException {
      return new com.frojasg1.sun.security.jgss.wrapper.GSSNameElement(var1, var2, this.cStub);
   }

   public GSSCredentialSpi getCredentialElement(GSSNameSpi var1, int var2, int var3, int var4) throws GSSException {
      com.frojasg1.sun.security.jgss.wrapper.GSSNameElement var5 = null;
      if (var1 != null && !(var1 instanceof com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)) {
         var5 = (com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)this.getNameElement(var1.toString(), var1.getStringNameType());
      } else {
         var5 = (com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)var1;
      }

      if (var4 == 0) {
         var4 = 1;
      }

      com.frojasg1.sun.security.jgss.wrapper.GSSCredElement var6 = this.getCredFromSubject(var5, var4 == 1);
      if (var6 == null) {
         if (var4 == 1) {
            var6 = new com.frojasg1.sun.security.jgss.wrapper.GSSCredElement(var5, var2, var4, this.cStub);
         } else {
            if (var4 != 2) {
               throw new GSSException(11, -1, "Unknown usage mode requested");
            }

            if (var5 == null) {
               var5 = com.frojasg1.sun.security.jgss.wrapper.GSSNameElement.DEF_ACCEPTOR;
            }

            var6 = new com.frojasg1.sun.security.jgss.wrapper.GSSCredElement(var5, var3, var4, this.cStub);
         }
      }

      return var6;
   }

   public GSSContextSpi getMechanismContext(GSSNameSpi var1, GSSCredentialSpi var2, int var3) throws GSSException {
      if (var1 == null) {
         throw new GSSException(3);
      } else {
         if (!(var1 instanceof com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)) {
            var1 = (com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)this.getNameElement(((GSSNameSpi)var1).toString(), ((GSSNameSpi)var1).getStringNameType());
         }

         if (var2 == null) {
            var2 = this.getCredFromSubject((com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)null, true);
         } else if (!(var2 instanceof com.frojasg1.sun.security.jgss.wrapper.GSSCredElement)) {
            throw new GSSException(13);
         }

         return new com.frojasg1.sun.security.jgss.wrapper.NativeGSSContext((com.frojasg1.sun.security.jgss.wrapper.GSSNameElement)var1, (com.frojasg1.sun.security.jgss.wrapper.GSSCredElement)var2, var3, this.cStub);
      }
   }

   public GSSContextSpi getMechanismContext(GSSCredentialSpi var1) throws GSSException {
      if (var1 == null) {
         var1 = this.getCredFromSubject((GSSNameElement)null, false);
      } else if (!(var1 instanceof com.frojasg1.sun.security.jgss.wrapper.GSSCredElement)) {
         throw new GSSException(13);
      }

      return new com.frojasg1.sun.security.jgss.wrapper.NativeGSSContext((GSSCredElement)var1, this.cStub);
   }

   public GSSContextSpi getMechanismContext(byte[] var1) throws GSSException {
      return this.cStub.importContext(var1);
   }

   public final Oid getMechanismOid() {
      return this.cStub.getMech();
   }

   public Provider getProvider() {
      return SunNativeProvider.INSTANCE;
   }

   public Oid[] getNameTypes() throws GSSException {
      return this.cStub.inquireNamesForMech();
   }
}
