package com.frojasg1.sun.net.httpserver;

import com.sun.net.httpserver.Authenticator;
import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.Authenticator.Failure;
import com.sun.net.httpserver.Authenticator.Result;
import com.sun.net.httpserver.Authenticator.Retry;
import com.sun.net.httpserver.Authenticator.Success;
import com.sun.net.httpserver.Filter.Chain;
import com.frojasg1.sun.net.httpserver.ExchangeImpl;

import java.io.IOException;
import java.io.InputStream;

public class AuthFilter extends Filter {
   private Authenticator authenticator;

   public AuthFilter(Authenticator var1) {
      this.authenticator = var1;
   }

   public String description() {
      return "Authentication filter";
   }

   public void setAuthenticator(Authenticator var1) {
      this.authenticator = var1;
   }

   public void consumeInput(HttpExchange var1) throws IOException {
      InputStream var2 = var1.getRequestBody();
      byte[] var3 = new byte[4096];

      while(var2.read(var3) != -1) {
      }

      var2.close();
   }

   public void doFilter(HttpExchange var1, Chain var2) throws IOException {
      if (this.authenticator != null) {
         Result var3 = this.authenticator.authenticate(var1);
         if (var3 instanceof Success) {
            Success var4 = (Success)var3;
            com.frojasg1.sun.net.httpserver.ExchangeImpl var5 = com.frojasg1.sun.net.httpserver.ExchangeImpl.get(var1);
            var5.setPrincipal(var4.getPrincipal());
            var2.doFilter(var1);
         } else if (var3 instanceof Retry) {
            Retry var6 = (Retry)var3;
            this.consumeInput(var1);
            var1.sendResponseHeaders(var6.getResponseCode(), -1L);
         } else if (var3 instanceof Failure) {
            Failure var7 = (Failure)var3;
            this.consumeInput(var1);
            var1.sendResponseHeaders(var7.getResponseCode(), -1L);
         }
      } else {
         var2.doFilter(var1);
      }

   }
}
