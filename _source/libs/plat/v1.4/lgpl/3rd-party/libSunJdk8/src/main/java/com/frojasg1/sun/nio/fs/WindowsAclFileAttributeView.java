package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.AbstractAclFileAttributeView;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsLinkSupport;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsPath;
import com.frojasg1.sun.nio.fs.WindowsSecurity;
import com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor;
import com.frojasg1.sun.nio.fs.WindowsUserPrincipals;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

class WindowsAclFileAttributeView extends com.frojasg1.sun.nio.fs.AbstractAclFileAttributeView {
   private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
   private final com.frojasg1.sun.nio.fs.WindowsPath file;
   private final boolean followLinks;

   WindowsAclFileAttributeView(com.frojasg1.sun.nio.fs.WindowsPath var1, boolean var2) {
      this.file = var1;
      this.followLinks = var2;
   }

   private void checkAccess(com.frojasg1.sun.nio.fs.WindowsPath var1, boolean var2, boolean var3) {
      SecurityManager var4 = System.getSecurityManager();
      if (var4 != null) {
         if (var2) {
            var4.checkRead(var1.getPathForPermissionCheck());
         }

         if (var3) {
            var4.checkWrite(var1.getPathForPermissionCheck());
         }

         var4.checkPermission(new RuntimePermission("accessUserInformation"));
      }

   }

   static com.frojasg1.sun.nio.fs.NativeBuffer getFileSecurity(String var0, int var1) throws IOException {
      int var2 = 0;

      try {
         var2 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFileSecurity(var0, var1, 0L, 0);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var5) {
         var5.rethrowAsIOException(var0);
      }

      assert var2 > 0;

      com.frojasg1.sun.nio.fs.NativeBuffer var3 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var2);

      try {
         while(true) {
            int var4 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetFileSecurity(var0, var1, var3.address(), var2);
            if (var4 <= var2) {
               return var3;
            }

            var3.release();
            var3 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var4);
            var2 = var4;
         }
      } catch (com.frojasg1.sun.nio.fs.WindowsException var6) {
         var3.release();
         var6.rethrowAsIOException(var0);
         return null;
      }
   }

   public UserPrincipal getOwner() throws IOException {
      this.checkAccess(this.file, true, false);
      String var1 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
      com.frojasg1.sun.nio.fs.NativeBuffer var2 = getFileSecurity(var1, 1);

      UserPrincipal var5;
      try {
         long var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetSecurityDescriptorOwner(var2.address());
         if (var3 == 0L) {
            throw new IOException("no owner");
         }

         var5 = com.frojasg1.sun.nio.fs.WindowsUserPrincipals.fromSid(var3);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var9) {
         var9.rethrowAsIOException(this.file);
         Object var4 = null;
         return (UserPrincipal)var4;
      } finally {
         var2.release();
      }

      return var5;
   }

   public List<AclEntry> getAcl() throws IOException {
      this.checkAccess(this.file, true, false);
      String var1 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
      com.frojasg1.sun.nio.fs.NativeBuffer var2 = getFileSecurity(var1, 4);

      List var3;
      try {
         var3 = com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.getAcl(var2.address());
      } finally {
         var2.release();
      }

      return var3;
   }

   public void setOwner(UserPrincipal var1) throws IOException {
      if (var1 == null) {
         throw new NullPointerException("'owner' is null");
      } else if (!(var1 instanceof com.frojasg1.sun.nio.fs.WindowsUserPrincipals.User)) {
         throw new ProviderMismatchException();
      } else {
         com.frojasg1.sun.nio.fs.WindowsUserPrincipals.User var2 = (com.frojasg1.sun.nio.fs.WindowsUserPrincipals.User)var1;
         this.checkAccess(this.file, false, true);
         String var3 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
         long var4 = 0L;

         try {
            var4 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.ConvertStringSidToSid(var2.sidString());
         } catch (com.frojasg1.sun.nio.fs.WindowsException var30) {
            throw new IOException("Failed to get SID for " + var2.getName() + ": " + var30.errorString());
         }

         try {
            com.frojasg1.sun.nio.fs.NativeBuffer var6 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(20);

            try {
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.InitializeSecurityDescriptor(var6.address());
               com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetSecurityDescriptorOwner(var6.address(), var4);
               com.frojasg1.sun.nio.fs.WindowsSecurity.Privilege var7 = com.frojasg1.sun.nio.fs.WindowsSecurity.enablePrivilege("SeRestorePrivilege");

               try {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetFileSecurity(var3, 1, var6.address());
               } finally {
                  var7.drop();
               }
            } catch (com.frojasg1.sun.nio.fs.WindowsException var27) {
               var27.rethrowAsIOException(this.file);
            } finally {
               var6.release();
            }
         } finally {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.LocalFree(var4);
         }

      }
   }

   public void setAcl(List<AclEntry> var1) throws IOException {
      this.checkAccess(this.file, false, true);
      String var2 = com.frojasg1.sun.nio.fs.WindowsLinkSupport.getFinalPath(this.file, this.followLinks);
      com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor var3 = com.frojasg1.sun.nio.fs.WindowsSecurityDescriptor.create(var1);

      try {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetFileSecurity(var2, 4, var3.address());
      } catch (com.frojasg1.sun.nio.fs.WindowsException var8) {
         var8.rethrowAsIOException(this.file);
      } finally {
         var3.release();
      }

   }
}
