package com.frojasg1.sun.net.www.protocol.http.ntlm;

import java.io.IOException;
import java.net.InetAddress;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.frojasg1.sun.net.www.HeaderParser;
import com.frojasg1.sun.net.www.protocol.http.AuthScheme;
import com.frojasg1.sun.net.www.protocol.http.AuthenticationInfo;
import com.frojasg1.sun.net.www.protocol.http.HttpURLConnection;
import com.frojasg1.sun.net.www.protocol.http.ntlm.NTLMAuthSequence;
import com.frojasg1.sun.net.www.protocol.http.ntlm.NTLMAuthenticationCallback;
import com.frojasg1.sun.security.action.GetPropertyAction;

public class NTLMAuthentication extends AuthenticationInfo {
   private static final long serialVersionUID = 100L;
   private static final com.frojasg1.sun.net.www.protocol.http.ntlm.NTLMAuthenticationCallback NTLMAuthCallback = NTLMAuthenticationCallback.getNTLMAuthenticationCallback();
   private String hostname;
   private static String defaultDomain = (String)AccessController.doPrivileged(new GetPropertyAction("http.auth.ntlm.domain", "domain"));
   private static final boolean ntlmCache;
   String username;
   String ntdomain;
   String password;

   private void init0() {
      this.hostname = (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            String var1;
            try {
               var1 = InetAddress.getLocalHost().getHostName().toUpperCase();
            } catch (UnknownHostException var3) {
               var1 = "localhost";
            }

            return var1;
         }
      });
      int var1 = this.hostname.indexOf(46);
      if (var1 != -1) {
         this.hostname = this.hostname.substring(0, var1);
      }

   }

   public NTLMAuthentication(boolean var1, URL var2, PasswordAuthentication var3) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.NTLM, var2, "");
      this.init(var3);
   }

   private void init(PasswordAuthentication var1) {
      this.pw = var1;
      if (var1 != null) {
         String var2 = var1.getUserName();
         int var3 = var2.indexOf(92);
         if (var3 == -1) {
            this.username = var2;
            this.ntdomain = defaultDomain;
         } else {
            this.ntdomain = var2.substring(0, var3).toUpperCase();
            this.username = var2.substring(var3 + 1);
         }

         this.password = new String(var1.getPassword());
      } else {
         this.username = null;
         this.ntdomain = null;
         this.password = null;
      }

      this.init0();
   }

   public NTLMAuthentication(boolean var1, String var2, int var3, PasswordAuthentication var4) {
      super((char)(var1 ? 'p' : 's'), AuthScheme.NTLM, var2, var3, "");
      this.init(var4);
   }

   protected boolean useAuthCache() {
      return ntlmCache && super.useAuthCache();
   }

   public boolean supportsPreemptiveAuthorization() {
      return false;
   }

   public static boolean supportsTransparentAuth() {
      return true;
   }

   public static boolean isTrustedSite(URL var0) {
      return NTLMAuthCallback.isTrustedSite(var0);
   }

   public String getHeaderValue(URL var1, String var2) {
      throw new RuntimeException("getHeaderValue not supported");
   }

   public boolean isAuthorizationStale(String var1) {
      return false;
   }

   public synchronized boolean setHeaders(HttpURLConnection var1, HeaderParser var2, String var3) {
      try {
         com.frojasg1.sun.net.www.protocol.http.ntlm.NTLMAuthSequence var4 = (com.frojasg1.sun.net.www.protocol.http.ntlm.NTLMAuthSequence)var1.authObj();
         if (var4 == null) {
            var4 = new NTLMAuthSequence(this.username, this.password, this.ntdomain);
            var1.authObj(var4);
         }

         String var5 = "NTLM " + var4.getAuthHeader(var3.length() > 6 ? var3.substring(5) : null);
         var1.setAuthenticationProperty(this.getHeaderName(), var5);
         if (var4.isComplete()) {
            var1.authObj((Object)null);
         }

         return true;
      } catch (IOException var6) {
         var1.authObj((Object)null);
         return false;
      }
   }

   static {
      String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("jdk.ntlm.cache", "true"));
      ntlmCache = Boolean.parseBoolean(var0);
   }
}
