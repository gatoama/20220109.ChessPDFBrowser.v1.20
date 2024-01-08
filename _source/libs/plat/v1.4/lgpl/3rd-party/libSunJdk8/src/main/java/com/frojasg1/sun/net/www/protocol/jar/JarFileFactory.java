package com.frojasg1.sun.net.www.protocol.jar;

import java.io.FileNotFoundException;
import java.io.FilePermission;
import java.io.IOException;
import java.net.SocketPermission;
import java.net.URL;
import java.net.URLConnection;
import java.security.Permission;
import java.util.HashMap;
import java.util.jar.JarFile;
import com.frojasg1.sun.net.util.URLUtil;
import com.frojasg1.sun.net.www.protocol.jar.URLJarFile;

class JarFileFactory implements com.frojasg1.sun.net.www.protocol.jar.URLJarFile.URLJarFileCloseController {
   private static final HashMap<String, JarFile> fileCache = new HashMap();
   private static final HashMap<JarFile, URL> urlCache = new HashMap();
   private static final JarFileFactory instance = new JarFileFactory();

   private JarFileFactory() {
   }

   public static JarFileFactory getInstance() {
      return instance;
   }

   URLConnection getConnection(JarFile var1) throws IOException {
      URL var2;
      synchronized(instance) {
         var2 = (URL)urlCache.get(var1);
      }

      return var2 != null ? var2.openConnection() : null;
   }

   public JarFile get(URL var1) throws IOException {
      return this.get(var1, true);
   }

   JarFile get(URL var1, boolean var2) throws IOException {
      if (var1.getProtocol().equalsIgnoreCase("file")) {
         String var3 = var1.getHost();
         if (var3 != null && !var3.equals("") && !var3.equalsIgnoreCase("localhost")) {
            var1 = new URL("file", "", "//" + var3 + var1.getPath());
         }
      }

      JarFile var10;
      if (var2) {
         synchronized(instance) {
            var10 = this.getCachedJarFile(var1);
         }

         if (var10 == null) {
            JarFile var4 = com.frojasg1.sun.net.www.protocol.jar.URLJarFile.getJarFile(var1, this);
            synchronized(instance) {
               var10 = this.getCachedJarFile(var1);
               if (var10 == null) {
                  fileCache.put(URLUtil.urlNoFragString(var1), var4);
                  urlCache.put(var4, var1);
                  var10 = var4;
               } else if (var4 != null) {
                  var4.close();
               }
            }
         }
      } else {
         var10 = URLJarFile.getJarFile(var1, this);
      }

      if (var10 == null) {
         throw new FileNotFoundException(var1.toString());
      } else {
         return var10;
      }
   }

   public void close(JarFile var1) {
      synchronized(instance) {
         URL var3 = (URL)urlCache.remove(var1);
         if (var3 != null) {
            fileCache.remove(URLUtil.urlNoFragString(var3));
         }

      }
   }

   private JarFile getCachedJarFile(URL var1) {
      assert Thread.holdsLock(instance);

      JarFile var2 = (JarFile)fileCache.get(URLUtil.urlNoFragString(var1));
      if (var2 != null) {
         Permission var3 = this.getPermission(var2);
         if (var3 != null) {
            SecurityManager var4 = System.getSecurityManager();
            if (var4 != null) {
               try {
                  var4.checkPermission(var3);
               } catch (SecurityException var6) {
                  if (var3 instanceof FilePermission && var3.getActions().indexOf("read") != -1) {
                     var4.checkRead(var3.getName());
                  } else {
                     if (!(var3 instanceof SocketPermission) || var3.getActions().indexOf("connect") == -1) {
                        throw var6;
                     }

                     var4.checkConnect(var1.getHost(), var1.getPort());
                  }
               }
            }
         }
      }

      return var2;
   }

   private Permission getPermission(JarFile var1) {
      try {
         URLConnection var2 = this.getConnection(var1);
         if (var2 != null) {
            return var2.getPermission();
         }
      } catch (IOException var3) {
      }

      return null;
   }
}
