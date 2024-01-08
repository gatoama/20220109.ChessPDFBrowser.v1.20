package com.frojasg1.sun.security.ssl.krb5;

import java.security.AccessControlContext;
import java.security.Permission;
import java.security.Principal;
import java.util.Iterator;
import java.util.Set;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KeyTab;
import javax.security.auth.kerberos.ServicePermission;
import javax.security.auth.login.LoginException;
import com.frojasg1.sun.security.jgss.GSSCaller;
import com.frojasg1.sun.security.jgss.krb5.Krb5Util;
import com.frojasg1.sun.security.jgss.krb5.ServiceCreds;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.ssl.Krb5Proxy;

public class Krb5ProxyImpl implements Krb5Proxy {
   public Krb5ProxyImpl() {
   }

   public Subject getClientSubject(AccessControlContext var1) throws LoginException {
      return Krb5Util.getSubject(GSSCaller.CALLER_SSL_CLIENT, var1);
   }

   public Subject getServerSubject(AccessControlContext var1) throws LoginException {
      return Krb5Util.getSubject(GSSCaller.CALLER_SSL_SERVER, var1);
   }

   public Object getServiceCreds(AccessControlContext var1) throws LoginException {
      ServiceCreds var2 = Krb5Util.getServiceCreds(GSSCaller.CALLER_SSL_SERVER, (String)null, var1);
      return var2;
   }

   public String getServerPrincipalName(Object var1) {
      return ((ServiceCreds)var1).getName();
   }

   public String getPrincipalHostName(Principal var1) {
      if (var1 == null) {
         return null;
      } else {
         String var2 = null;

         try {
            PrincipalName var3 = new PrincipalName(var1.getName(), 3);
            String[] var4 = var3.getNameStrings();
            if (var4.length >= 2) {
               var2 = var4[1];
            }
         } catch (Exception var5) {
         }

         return var2;
      }
   }

   public Permission getServicePermission(String var1, String var2) {
      return new ServicePermission(var1, var2);
   }

   public boolean isRelated(Subject var1, Principal var2) {
      if (var2 == null) {
         return false;
      } else {
         Set var3 = var1.getPrincipals(Principal.class);
         if (var3.contains(var2)) {
            return true;
         } else {
            Iterator var4 = var1.getPrivateCredentials(KeyTab.class).iterator();

            KeyTab var5;
            do {
               if (!var4.hasNext()) {
                  return false;
               }

               var5 = (KeyTab)var4.next();
            } while(var5.isBound());

            return true;
         }
      }
   }
}
