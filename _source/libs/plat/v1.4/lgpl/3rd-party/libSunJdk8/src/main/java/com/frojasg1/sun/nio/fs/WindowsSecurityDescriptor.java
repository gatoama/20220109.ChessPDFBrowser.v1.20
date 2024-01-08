package com.frojasg1.sun.nio.fs;

import java.io.IOException;
import java.nio.file.ProviderMismatchException;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.AclEntryFlag;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;
import com.frojasg1.sun.nio.fs.WindowsUserPrincipals;

class WindowsSecurityDescriptor {
   private static final Unsafe unsafe = Unsafe.getUnsafe();
   private static final short SIZEOF_ACL = 8;
   private static final short SIZEOF_ACCESS_ALLOWED_ACE = 12;
   private static final short SIZEOF_ACCESS_DENIED_ACE = 12;
   private static final short SIZEOF_SECURITY_DESCRIPTOR = 20;
   private static final short OFFSETOF_TYPE = 0;
   private static final short OFFSETOF_FLAGS = 1;
   private static final short OFFSETOF_ACCESS_MASK = 4;
   private static final short OFFSETOF_SID = 8;
   private static final WindowsSecurityDescriptor NULL_DESCRIPTOR = new WindowsSecurityDescriptor();
   private final List<Long> sidList;
   private final com.frojasg1.sun.nio.fs.NativeBuffer aclBuffer;
   private final com.frojasg1.sun.nio.fs.NativeBuffer sdBuffer;

   private WindowsSecurityDescriptor() {
      this.sidList = null;
      this.aclBuffer = null;
      this.sdBuffer = null;
   }

   private WindowsSecurityDescriptor(List<AclEntry> var1) throws IOException {
      boolean var2 = false;
      ArrayList var19 = new ArrayList(var1);
      this.sidList = new ArrayList(var19.size());

      try {
         int var3 = 8;
         Iterator var4 = var19.iterator();

         AclEntry var5;
         while(var4.hasNext()) {
            var5 = (AclEntry)var4.next();
            UserPrincipal var6 = var5.principal();
            if (!(var6 instanceof com.frojasg1.sun.nio.fs.WindowsUserPrincipals.User)) {
               throw new ProviderMismatchException();
            }

            String var7 = ((com.frojasg1.sun.nio.fs.WindowsUserPrincipals.User)var6).sidString();

            try {
               long var8 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.ConvertStringSidToSid(var7);
               this.sidList.add(var8);
               var3 += com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetLengthSid(var8) + Math.max(12, 12);
            } catch (com.frojasg1.sun.nio.fs.WindowsException var16) {
               throw new IOException("Failed to get SID for " + var6.getName() + ": " + var16.errorString());
            }
         }

         this.aclBuffer = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(var3);
         this.sdBuffer = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBuffer(20);
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.InitializeAcl(this.aclBuffer.address(), var3);

         for(int var20 = 0; var20 < var19.size(); ++var20) {
            var5 = (AclEntry)var19.get(var20);
            long var21 = (Long)this.sidList.get(var20);

            try {
               encode(var5, var21, this.aclBuffer.address());
            } catch (com.frojasg1.sun.nio.fs.WindowsException var15) {
               throw new IOException("Failed to encode ACE: " + var15.errorString());
            }
         }

         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.InitializeSecurityDescriptor(this.sdBuffer.address());
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetSecurityDescriptorDacl(this.sdBuffer.address(), this.aclBuffer.address());
         var2 = true;
      } catch (com.frojasg1.sun.nio.fs.WindowsException var17) {
         throw new IOException(var17.getMessage());
      } finally {
         if (!var2) {
            this.release();
         }

      }

   }

   void release() {
      if (this.sdBuffer != null) {
         this.sdBuffer.release();
      }

      if (this.aclBuffer != null) {
         this.aclBuffer.release();
      }

      if (this.sidList != null) {
         Iterator var1 = this.sidList.iterator();

         while(var1.hasNext()) {
            Long var2 = (Long)var1.next();
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.LocalFree(var2);
         }
      }

   }

   long address() {
      return this.sdBuffer == null ? 0L : this.sdBuffer.address();
   }

   private static AclEntry decode(long var0) throws IOException {
      byte var2 = unsafe.getByte(var0 + 0L);
      if (var2 != 0 && var2 != 1) {
         return null;
      } else {
         AclEntryType var3;
         if (var2 == 0) {
            var3 = AclEntryType.ALLOW;
         } else {
            var3 = AclEntryType.DENY;
         }

         byte var4 = unsafe.getByte(var0 + 1L);
         EnumSet var5 = EnumSet.noneOf(AclEntryFlag.class);
         if ((var4 & 1) != 0) {
            var5.add(AclEntryFlag.FILE_INHERIT);
         }

         if ((var4 & 2) != 0) {
            var5.add(AclEntryFlag.DIRECTORY_INHERIT);
         }

         if ((var4 & 4) != 0) {
            var5.add(AclEntryFlag.NO_PROPAGATE_INHERIT);
         }

         if ((var4 & 8) != 0) {
            var5.add(AclEntryFlag.INHERIT_ONLY);
         }

         int var6 = unsafe.getInt(var0 + 4L);
         EnumSet var7 = EnumSet.noneOf(AclEntryPermission.class);
         if ((var6 & 1) > 0) {
            var7.add(AclEntryPermission.READ_DATA);
         }

         if ((var6 & 2) > 0) {
            var7.add(AclEntryPermission.WRITE_DATA);
         }

         if ((var6 & 4) > 0) {
            var7.add(AclEntryPermission.APPEND_DATA);
         }

         if ((var6 & 8) > 0) {
            var7.add(AclEntryPermission.READ_NAMED_ATTRS);
         }

         if ((var6 & 16) > 0) {
            var7.add(AclEntryPermission.WRITE_NAMED_ATTRS);
         }

         if ((var6 & 32) > 0) {
            var7.add(AclEntryPermission.EXECUTE);
         }

         if ((var6 & 64) > 0) {
            var7.add(AclEntryPermission.DELETE_CHILD);
         }

         if ((var6 & 128) > 0) {
            var7.add(AclEntryPermission.READ_ATTRIBUTES);
         }

         if ((var6 & 256) > 0) {
            var7.add(AclEntryPermission.WRITE_ATTRIBUTES);
         }

         if ((var6 & 65536) > 0) {
            var7.add(AclEntryPermission.DELETE);
         }

         if ((var6 & 131072) > 0) {
            var7.add(AclEntryPermission.READ_ACL);
         }

         if ((var6 & 262144) > 0) {
            var7.add(AclEntryPermission.WRITE_ACL);
         }

         if ((var6 & 524288) > 0) {
            var7.add(AclEntryPermission.WRITE_OWNER);
         }

         if ((var6 & 1048576) > 0) {
            var7.add(AclEntryPermission.SYNCHRONIZE);
         }

         long var8 = var0 + 8L;
         UserPrincipal var10 = com.frojasg1.sun.nio.fs.WindowsUserPrincipals.fromSid(var8);
         return AclEntry.newBuilder().setType(var3).setPrincipal(var10).setFlags(var5).setPermissions(var7).build();
      }
   }

   private static void encode(AclEntry var0, long var1, long var3) throws com.frojasg1.sun.nio.fs.WindowsException {
      if (var0.type() == AclEntryType.ALLOW || var0.type() == AclEntryType.DENY) {
         boolean var5 = var0.type() == AclEntryType.ALLOW;
         Set var6 = var0.permissions();
         int var7 = 0;
         if (var6.contains(AclEntryPermission.READ_DATA)) {
            var7 |= 1;
         }

         if (var6.contains(AclEntryPermission.WRITE_DATA)) {
            var7 |= 2;
         }

         if (var6.contains(AclEntryPermission.APPEND_DATA)) {
            var7 |= 4;
         }

         if (var6.contains(AclEntryPermission.READ_NAMED_ATTRS)) {
            var7 |= 8;
         }

         if (var6.contains(AclEntryPermission.WRITE_NAMED_ATTRS)) {
            var7 |= 16;
         }

         if (var6.contains(AclEntryPermission.EXECUTE)) {
            var7 |= 32;
         }

         if (var6.contains(AclEntryPermission.DELETE_CHILD)) {
            var7 |= 64;
         }

         if (var6.contains(AclEntryPermission.READ_ATTRIBUTES)) {
            var7 |= 128;
         }

         if (var6.contains(AclEntryPermission.WRITE_ATTRIBUTES)) {
            var7 |= 256;
         }

         if (var6.contains(AclEntryPermission.DELETE)) {
            var7 |= 65536;
         }

         if (var6.contains(AclEntryPermission.READ_ACL)) {
            var7 |= 131072;
         }

         if (var6.contains(AclEntryPermission.WRITE_ACL)) {
            var7 |= 262144;
         }

         if (var6.contains(AclEntryPermission.WRITE_OWNER)) {
            var7 |= 524288;
         }

         if (var6.contains(AclEntryPermission.SYNCHRONIZE)) {
            var7 |= 1048576;
         }

         Set var8 = var0.flags();
         byte var9 = 0;
         if (var8.contains(AclEntryFlag.FILE_INHERIT)) {
            var9 = (byte)(var9 | 1);
         }

         if (var8.contains(AclEntryFlag.DIRECTORY_INHERIT)) {
            var9 = (byte)(var9 | 2);
         }

         if (var8.contains(AclEntryFlag.NO_PROPAGATE_INHERIT)) {
            var9 = (byte)(var9 | 4);
         }

         if (var8.contains(AclEntryFlag.INHERIT_ONLY)) {
            var9 = (byte)(var9 | 8);
         }

         if (var5) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.AddAccessAllowedAceEx(var3, var9, var7, var1);
         } else {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.AddAccessDeniedAceEx(var3, var9, var7, var1);
         }

      }
   }

   static WindowsSecurityDescriptor create(List<AclEntry> var0) throws IOException {
      return new WindowsSecurityDescriptor(var0);
   }

   static WindowsSecurityDescriptor fromAttribute(FileAttribute<?>... var0) throws IOException {
      WindowsSecurityDescriptor var1 = NULL_DESCRIPTOR;
      FileAttribute[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         FileAttribute var5 = var2[var4];
         if (var1 != NULL_DESCRIPTOR) {
            var1.release();
         }

         if (var5 == null) {
            throw new NullPointerException();
         }

         if (!var5.name().equals("acl:acl")) {
            throw new UnsupportedOperationException("'" + var5.name() + "' not supported as initial attribute");
         }

         List var6 = (List)var5.value();
         var1 = new WindowsSecurityDescriptor(var6);
      }

      return var1;
   }

   static List<AclEntry> getAcl(long var0) throws IOException {
      long var2 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetSecurityDescriptorDacl(var0);
      boolean var4 = false;
      int var10;
      if (var2 == 0L) {
         var10 = 0;
      } else {
         com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.AclInformation var5 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetAclInformation(var2);
         var10 = var5.aceCount();
      }

      ArrayList var11 = new ArrayList(var10);

      for(int var6 = 0; var6 < var10; ++var6) {
         long var7 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetAce(var2, var6);
         AclEntry var9 = decode(var7);
         if (var9 != null) {
            var11.add(var9);
         }
      }

      return var11;
   }
}
