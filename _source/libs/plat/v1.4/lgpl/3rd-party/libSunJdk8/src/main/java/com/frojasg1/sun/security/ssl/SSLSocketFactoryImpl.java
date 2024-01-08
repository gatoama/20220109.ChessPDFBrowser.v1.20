package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocketFactory;

public final class SSLSocketFactoryImpl extends SSLSocketFactory {
   private SSLContextImpl context;

   public SSLSocketFactoryImpl() throws Exception {
      this.context = SSLContextImpl.DefaultSSLContext.getDefaultImpl();
   }

   SSLSocketFactoryImpl(SSLContextImpl var1) {
      this.context = var1;
   }

   public Socket createSocket() {
      return new SSLSocketImpl(this.context);
   }

   public Socket createSocket(String var1, int var2) throws IOException, UnknownHostException {
      return new SSLSocketImpl(this.context, var1, var2);
   }

   public Socket createSocket(Socket var1, String var2, int var3, boolean var4) throws IOException {
      return new SSLSocketImpl(this.context, var1, var2, var3, var4);
   }

   public Socket createSocket(Socket var1, InputStream var2, boolean var3) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("the existing socket cannot be null");
      } else {
         return new SSLSocketImpl(this.context, var1, var2, var3);
      }
   }

   public Socket createSocket(InetAddress var1, int var2) throws IOException {
      return new SSLSocketImpl(this.context, var1, var2);
   }

   public Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws IOException {
      return new SSLSocketImpl(this.context, var1, var2, var3, var4);
   }

   public Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
      return new SSLSocketImpl(this.context, var1, var2, var3, var4);
   }

   public String[] getDefaultCipherSuites() {
      return this.context.getDefaultCipherSuiteList(false).toStringArray();
   }

   public String[] getSupportedCipherSuites() {
      return this.context.getSupportedCipherSuiteList().toStringArray();
   }
}
