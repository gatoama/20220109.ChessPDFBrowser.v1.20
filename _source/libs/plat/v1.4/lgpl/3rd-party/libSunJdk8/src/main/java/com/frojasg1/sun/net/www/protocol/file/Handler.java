package com.frojasg1.sun.net.www.protocol.file;

import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import com.frojasg1.sun.net.www.ParseUtil;
import com.frojasg1.sun.net.www.protocol.file.FileURLConnection;

public class Handler extends URLStreamHandler {
   public Handler() {
   }

   private String getHost(URL var1) {
      String var2 = var1.getHost();
      if (var2 == null) {
         var2 = "";
      }

      return var2;
   }

   protected void parseURL(URL var1, String var2, int var3, int var4) {
      super.parseURL(var1, var2.replace(File.separatorChar, '/'), var3, var4);
   }

   public synchronized URLConnection openConnection(URL var1) throws IOException {
      return this.openConnection(var1, (Proxy)null);
   }

   public synchronized URLConnection openConnection(URL var1, Proxy var2) throws IOException {
      String var4 = var1.getFile();
      String var5 = var1.getHost();
      String var3 = ParseUtil.decode(var4);
      var3 = var3.replace('/', '\\');
      var3 = var3.replace('|', ':');
      if (var5 != null && !var5.equals("") && !var5.equalsIgnoreCase("localhost") && !var5.equals("~")) {
         var3 = "\\\\" + var5 + var3;
         File var6 = new File(var3);
         if (var6.exists()) {
            return this.createFileURLConnection(var1, var6);
         } else {
            URLConnection var7;
            try {
               URL var8 = new URL("ftp", var5, var4 + (var1.getRef() == null ? "" : "#" + var1.getRef()));
               if (var2 != null) {
                  var7 = var8.openConnection(var2);
               } else {
                  var7 = var8.openConnection();
               }
            } catch (IOException var10) {
               var7 = null;
            }

            if (var7 == null) {
               throw new IOException("Unable to connect to: " + var1.toExternalForm());
            } else {
               return var7;
            }
         }
      } else {
         return this.createFileURLConnection(var1, new File(var3));
      }
   }

   protected URLConnection createFileURLConnection(URL var1, File var2) {
      return new FileURLConnection(var1, var2);
   }

   protected boolean hostsEqual(URL var1, URL var2) {
      String var3 = var1.getHost();
      String var4 = var2.getHost();
      if (!"localhost".equalsIgnoreCase(var3) || var4 != null && !"".equals(var4)) {
         return !"localhost".equalsIgnoreCase(var4) || var3 != null && !"".equals(var3) ? super.hostsEqual(var1, var2) : true;
      } else {
         return true;
      }
   }
}
