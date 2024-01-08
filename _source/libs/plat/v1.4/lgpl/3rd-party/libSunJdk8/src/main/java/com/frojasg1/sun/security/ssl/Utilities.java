package com.frojasg1.sun.security.ssl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIServerName;
import com.frojasg1.sun.net.util.IPAddressUtil;

final class Utilities {
   Utilities() {
   }

   static List<SNIServerName> addToSNIServerNameList(List<SNIServerName> var0, String var1) {
      SNIHostName var2 = rawToSNIHostName(var1);
      if (var2 == null) {
         return var0;
      } else {
         int var3 = var0.size();
         ArrayList var4 = var3 != 0 ? new ArrayList(var0) : new ArrayList(1);
         boolean var5 = false;

         for(int var6 = 0; var6 < var3; ++var6) {
            SNIServerName var7 = (SNIServerName)var4.get(var6);
            if (var7.getType() == 0) {
               var4.set(var6, var2);
               if (Debug.isOn("ssl")) {
                  System.out.println(Thread.currentThread().getName() + ", the previous server name in SNI (" + var7 + ") was replaced with (" + var2 + ")");
               }

               var5 = true;
               break;
            }
         }

         if (!var5) {
            var4.add(var2);
         }

         return Collections.unmodifiableList(var4);
      }
   }

   private static SNIHostName rawToSNIHostName(String var0) {
      SNIHostName var1 = null;
      if (var0 != null && var0.indexOf(46) > 0 && !var0.endsWith(".") && !IPAddressUtil.isIPv4LiteralAddress(var0) && !IPAddressUtil.isIPv6LiteralAddress(var0)) {
         try {
            var1 = new SNIHostName(var0);
         } catch (IllegalArgumentException var3) {
            if (Debug.isOn("ssl")) {
               System.out.println(Thread.currentThread().getName() + ", \"" + var0 + "\" is not a legal HostName for  server name indication");
            }
         }
      }

      return var1;
   }
}
