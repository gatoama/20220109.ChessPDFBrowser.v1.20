package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;

import java.io.IOException;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.UserPrincipalNotFoundException;

class WindowsUserPrincipals {
   private WindowsUserPrincipals() {
   }

   static UserPrincipal fromSid(long var0) throws IOException {
      String var2;
      try {
         var2 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.ConvertSidToStringSid(var0);
         if (var2 == null) {
            throw new AssertionError();
         }
      } catch (com.frojasg1.sun.nio.fs.WindowsException var7) {
         throw new IOException("Unable to convert SID to String: " + var7.errorString());
      }

      com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.Account var3 = null;

      String var4;
      try {
         var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.LookupAccountSid(var0);
         var4 = var3.domain() + "\\" + var3.name();
      } catch (com.frojasg1.sun.nio.fs.WindowsException var6) {
         var4 = var2;
      }

      int var5 = var3 == null ? 8 : var3.use();
      return (UserPrincipal)(var5 != 2 && var5 != 5 && var5 != 4 ? new WindowsUserPrincipals.User(var2, var5, var4) : new WindowsUserPrincipals.Group(var2, var5, var4));
   }

   static UserPrincipal lookup(String var0) throws IOException {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPermission(new RuntimePermission("lookupUserInformation"));
      }

      boolean var2 = false;

      int var13;
      try {
         var13 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.LookupAccountName(var0, 0L, 0);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var10) {
         if (var10.lastError() == 1332) {
            throw new UserPrincipalNotFoundException(var0);
         }

         throw new IOException(var0 + ": " + var10.errorString());
      }

      assert var13 > 0;

      com.frojasg1.sun.nio.fs.NativeBuffer var3 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var13);

      UserPrincipal var5;
      try {
         int var4 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.LookupAccountName(var0, var3.address(), var13);
         if (var4 != var13) {
            throw new AssertionError("SID change during lookup");
         }

         var5 = fromSid(var3.address());
      } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
         throw new IOException(var0 + ": " + var11.errorString());
      } finally {
         var3.release();
      }

      return var5;
   }

   static class Group extends WindowsUserPrincipals.User implements GroupPrincipal {
      Group(String var1, int var2, String var3) {
         super(var1, var2, var3);
      }
   }

   static class User implements UserPrincipal {
      private final String sidString;
      private final int sidType;
      private final String accountName;

      User(String var1, int var2, String var3) {
         this.sidString = var1;
         this.sidType = var2;
         this.accountName = var3;
      }

      String sidString() {
         return this.sidString;
      }

      public String getName() {
         return this.accountName;
      }

      public String toString() {
         String var1;
         switch(this.sidType) {
         case 1:
            var1 = "User";
            break;
         case 2:
            var1 = "Group";
            break;
         case 3:
            var1 = "Domain";
            break;
         case 4:
            var1 = "Alias";
            break;
         case 5:
            var1 = "Well-known group";
            break;
         case 6:
            var1 = "Deleted";
            break;
         case 7:
            var1 = "Invalid";
            break;
         case 8:
         default:
            var1 = "Unknown";
            break;
         case 9:
            var1 = "Computer";
         }

         return this.accountName + " (" + var1 + ")";
      }

      public boolean equals(Object var1) {
         if (var1 == this) {
            return true;
         } else if (!(var1 instanceof WindowsUserPrincipals.User)) {
            return false;
         } else {
            WindowsUserPrincipals.User var2 = (WindowsUserPrincipals.User)var1;
            return this.sidString.equals(var2.sidString);
         }
      }

      public int hashCode() {
         return this.sidString.hashCode();
      }
   }
}
