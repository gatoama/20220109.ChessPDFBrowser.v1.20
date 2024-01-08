package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsFileAttributes;
import com.frojasg1.sun.nio.fs.WindowsFileSystem;
import com.frojasg1.sun.nio.fs.WindowsPath;

import java.net.URI;
import java.net.URISyntaxException;

class WindowsUriSupport {
   private static final String IPV6_LITERAL_SUFFIX = ".ipv6-literal.net";

   private WindowsUriSupport() {
   }

   private static URI toUri(String var0, boolean var1, boolean var2) {
      String var3;
      String var4;
      if (var1) {
         int var5 = var0.indexOf(92, 2);
         var3 = var0.substring(2, var5);
         var4 = var0.substring(var5).replace('\\', '/');
         if (var3.endsWith(".ipv6-literal.net")) {
            var3 = var3.substring(0, var3.length() - ".ipv6-literal.net".length()).replace('-', ':').replace('s', '%');
         }
      } else {
         var3 = "";
         var4 = "/" + var0.replace('\\', '/');
      }

      if (var2) {
         var4 = var4 + "/";
      }

      try {
         return new URI("file", var3, var4, (String)null);
      } catch (URISyntaxException var7) {
         if (!var1) {
            throw new AssertionError(var7);
         } else {
            var4 = "//" + var0.replace('\\', '/');
            if (var2) {
               var4 = var4 + "/";
            }

            try {
               return new URI("file", (String)null, var4, (String)null);
            } catch (URISyntaxException var6) {
               throw new AssertionError(var6);
            }
         }
      }
   }

   static URI toUri(com.frojasg1.sun.nio.fs.WindowsPath var0) {
      var0 = var0.toAbsolutePath();
      String var1 = var0.toString();
      boolean var2 = false;
      if (!var1.endsWith("\\")) {
         try {
            var2 = com.frojasg1.sun.nio.fs.WindowsFileAttributes.get(var0, true).isDirectory();
         } catch (com.frojasg1.sun.nio.fs.WindowsException var4) {
         }
      }

      return toUri(var1, var0.isUnc(), var2);
   }

   static com.frojasg1.sun.nio.fs.WindowsPath fromUri(com.frojasg1.sun.nio.fs.WindowsFileSystem var0, URI var1) {
      if (!var1.isAbsolute()) {
         throw new IllegalArgumentException("URI is not absolute");
      } else if (var1.isOpaque()) {
         throw new IllegalArgumentException("URI is not hierarchical");
      } else {
         String var2 = var1.getScheme();
         if (var2 != null && var2.equalsIgnoreCase("file")) {
            if (var1.getFragment() != null) {
               throw new IllegalArgumentException("URI has a fragment component");
            } else if (var1.getQuery() != null) {
               throw new IllegalArgumentException("URI has a query component");
            } else {
               String var3 = var1.getPath();
               if (var3.equals("")) {
                  throw new IllegalArgumentException("URI path component is empty");
               } else {
                  String var4 = var1.getAuthority();
                  if (var4 != null && !var4.equals("")) {
                     String var5 = var1.getHost();
                     if (var5 == null) {
                        throw new IllegalArgumentException("URI authority component has undefined host");
                     }

                     if (var1.getUserInfo() != null) {
                        throw new IllegalArgumentException("URI authority component has user-info");
                     }

                     if (var1.getPort() != -1) {
                        throw new IllegalArgumentException("URI authority component has port number");
                     }

                     if (var5.startsWith("[")) {
                        var5 = var5.substring(1, var5.length() - 1).replace(':', '-').replace('%', 's');
                        var5 = var5 + ".ipv6-literal.net";
                     }

                     var3 = "\\\\" + var5 + var3;
                  } else if (var3.length() > 2 && var3.charAt(2) == ':') {
                     var3 = var3.substring(1);
                  }

                  return com.frojasg1.sun.nio.fs.WindowsPath.parse(var0, var3);
               }
            }
         } else {
            throw new IllegalArgumentException("URI scheme is not \"file\"");
         }
      }
   }
}
