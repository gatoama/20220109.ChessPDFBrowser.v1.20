package com.frojasg1.sun.net.www.protocol.https;

import com.frojasg1.sun.net.www.protocol.https.AbstractDelegateHttpsURLConnection;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

public class DelegateHttpsURLConnection extends AbstractDelegateHttpsURLConnection {
   public HttpsURLConnection httpsURLConnection;

   DelegateHttpsURLConnection(URL var1, com.frojasg1.sun.net.www.protocol.http.Handler var2, HttpsURLConnection var3) throws IOException {
      this(var1, (Proxy)null, var2, var3);
   }

   DelegateHttpsURLConnection(URL var1, Proxy var2, com.frojasg1.sun.net.www.protocol.http.Handler var3, HttpsURLConnection var4) throws IOException {
      super(var1, var2, var3);
      this.httpsURLConnection = var4;
   }

   protected SSLSocketFactory getSSLSocketFactory() {
      return this.httpsURLConnection.getSSLSocketFactory();
   }

   protected HostnameVerifier getHostnameVerifier() {
      return this.httpsURLConnection.getHostnameVerifier();
   }

   protected void dispose() throws Throwable {
      super.finalize();
   }
}
