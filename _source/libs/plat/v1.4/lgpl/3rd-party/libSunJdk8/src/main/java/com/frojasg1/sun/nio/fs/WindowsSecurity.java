package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.WindowsException;
import com.frojasg1.sun.nio.fs.WindowsNativeDispatcher;

class WindowsSecurity {
   static final long processTokenWithDuplicateAccess = openProcessToken(2);
   static final long processTokenWithQueryAccess = openProcessToken(8);

   private WindowsSecurity() {
   }

   private static long openProcessToken(int var0) {
      try {
         return com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.OpenProcessToken(com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetCurrentProcess(), var0);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var2) {
         return 0L;
      }
   }

   static WindowsSecurity.Privilege enablePrivilege(String var0) {
      final long var1;
      try {
         var1 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.LookupPrivilegeValue(var0);
      } catch (com.frojasg1.sun.nio.fs.WindowsException var12) {
         throw new AssertionError(var12);
      }

      long var3 = 0L;
      boolean var5 = false;
      boolean var6 = false;

      try {
         var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.OpenThreadToken(com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetCurrentThread(), 32, false);
         if (var3 == 0L && processTokenWithDuplicateAccess != 0L) {
            var3 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DuplicateTokenEx(processTokenWithDuplicateAccess, 36);
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetThreadToken(0L, var3);
            var5 = true;
         }

         if (var3 != 0L) {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.AdjustTokenPrivileges(var3, var1, 2);
            var6 = true;
         }
      } catch (com.frojasg1.sun.nio.fs.WindowsException var11) {
      }

      long var3Final = var3;
      boolean var5Final = var5;
      boolean var6Final = var6;

      return new WindowsSecurity.Privilege() {
         public void drop() {
            if (var3Final != 0L) {
               try {
                  if (var5Final) {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.SetThreadToken(0L, 0L);
                  } else if (var6Final) {
                     com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.AdjustTokenPrivileges(var3Final, var1, 0);
                  }
               } catch (com.frojasg1.sun.nio.fs.WindowsException var5x) {
                  throw new AssertionError(var5x);
               } finally {
                  com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var3Final);
               }
            }

         }
      };
   }

   static boolean checkAccessMask(long var0, int var2, int var3, int var4, int var5, int var6) throws com.frojasg1.sun.nio.fs.WindowsException {
      byte var7 = 8;
      long var8 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.OpenThreadToken(com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.GetCurrentThread(), var7, false);
      if (var8 == 0L && processTokenWithDuplicateAccess != 0L) {
         var8 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.DuplicateTokenEx(processTokenWithDuplicateAccess, var7);
      }

      boolean var10 = false;
      if (var8 != 0L) {
         try {
            var10 = com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.AccessCheck(var8, var0, var2, var3, var4, var5, var6);
         } finally {
            com.frojasg1.sun.nio.fs.WindowsNativeDispatcher.CloseHandle(var8);
         }
      }

      return var10;
   }

   interface Privilege {
      void drop();
   }
}
