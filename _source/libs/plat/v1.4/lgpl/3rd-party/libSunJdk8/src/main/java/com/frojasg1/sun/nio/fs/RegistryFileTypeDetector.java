package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.AbstractFileTypeDetector;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;

import java.io.IOException;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.PrivilegedAction;

public class RegistryFileTypeDetector extends AbstractFileTypeDetector {
   public RegistryFileTypeDetector() {
   }

   public String implProbeContentType(Path var1) throws IOException {
      if (!(var1 instanceof Path)) {
         return null;
      } else {
         Path var2 = var1.getFileName();
         if (var2 == null) {
            return null;
         } else {
            String var3 = var2.toString();
            int var4 = var3.lastIndexOf(46);
            if (var4 >= 0 && var4 != var3.length() - 1) {
               String var5 = var3.substring(var4);
               com.frojasg1.sun.nio.fs.NativeBuffer var6 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.asNativeBuffer(var5);
               com.frojasg1.sun.nio.fs.NativeBuffer var7 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.asNativeBuffer("Content Type");

               String var8;
               try {
                  var8 = queryStringValue(var6.address(), var7.address());
               } finally {
                  var7.release();
                  var6.release();
               }

               return var8;
            } else {
               return null;
            }
         }
      }
   }

   private static native String queryStringValue(long var0, long var2);

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            System.loadLibrary("nio");
            return null;
         }
      });
   }
}
