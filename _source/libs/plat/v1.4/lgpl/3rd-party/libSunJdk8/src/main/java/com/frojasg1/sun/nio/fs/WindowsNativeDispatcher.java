package com.frojasg1.sun.nio.fs;

import java.security.AccessController;
import java.security.PrivilegedAction;
import com.frojasg1.sun.misc.Unsafe;
import com.frojasg1.sun.nio.fs.NativeBuffer;
import com.frojasg1.sun.nio.fs.NativeBuffers;
import com.frojasg1.sun.nio.fs.WindowsException;

class WindowsNativeDispatcher {
   private static final Unsafe unsafe = Unsafe.getUnsafe();

   private WindowsNativeDispatcher() {
   }

   static native long CreateEvent(boolean var0, boolean var1) throws com.frojasg1.sun.nio.fs.WindowsException;

   static long CreateFile(String var0, int var1, int var2, long var3, int var5, int var6) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var7 = asNativeBuffer(var0);

      long var8;
      try {
         var8 = CreateFile0(var7.address(), var1, var2, var3, var5, var6);
      } finally {
         var7.release();
      }

      return var8;
   }

   static long CreateFile(String var0, int var1, int var2, int var3, int var4) throws com.frojasg1.sun.nio.fs.WindowsException {
      return CreateFile(var0, var1, var2, 0L, var3, var4);
   }

   private static native long CreateFile0(long var0, int var2, int var3, long var4, int var6, int var7) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void CloseHandle(long var0);

   static void DeleteFile(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      try {
         DeleteFile0(var1.address());
      } finally {
         var1.release();
      }

   }

   private static native void DeleteFile0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void CreateDirectory(String var0, long var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = asNativeBuffer(var0);

      try {
         CreateDirectory0(var3.address(), var1);
      } finally {
         var3.release();
      }

   }

   private static native void CreateDirectory0(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void RemoveDirectory(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      try {
         RemoveDirectory0(var1.address());
      } finally {
         var1.release();
      }

   }

   private static native void RemoveDirectory0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void DeviceIoControlSetSparse(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void DeviceIoControlGetReparsePoint(long var0, long var2, int var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.FirstFile FindFirstFile(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      WindowsNativeDispatcher.FirstFile var3;
      try {
         WindowsNativeDispatcher.FirstFile var2 = new WindowsNativeDispatcher.FirstFile();
         FindFirstFile0(var1.address(), var2);
         var3 = var2;
      } finally {
         var1.release();
      }

      return var3;
   }

   private static native void FindFirstFile0(long var0, WindowsNativeDispatcher.FirstFile var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static long FindFirstFile(String var0, long var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = asNativeBuffer(var0);

      long var4;
      try {
         var4 = FindFirstFile1(var3.address(), var1);
      } finally {
         var3.release();
      }

      return var4;
   }

   private static native long FindFirstFile1(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native String FindNextFile(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.FirstStream FindFirstStream(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      WindowsNativeDispatcher.FirstStream var3;
      try {
         WindowsNativeDispatcher.FirstStream var2 = new WindowsNativeDispatcher.FirstStream();
         FindFirstStream0(var1.address(), var2);
         if (var2.handle() == -1L) {
            var3 = null;
            return var3;
         }

         var3 = var2;
      } finally {
         var1.release();
      }

      return var3;
   }

   private static native void FindFirstStream0(long var0, WindowsNativeDispatcher.FirstStream var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native String FindNextStream(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void FindClose(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void GetFileInformationByHandle(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void CopyFileEx(String var0, String var1, int var2, long var3) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var5 = asNativeBuffer(var0);
      com.frojasg1.sun.nio.fs.NativeBuffer var6 = asNativeBuffer(var1);

      try {
         CopyFileEx0(var5.address(), var6.address(), var2, var3);
      } finally {
         var6.release();
         var5.release();
      }

   }

   private static native void CopyFileEx0(long var0, long var2, int var4, long var5) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void MoveFileEx(String var0, String var1, int var2) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = asNativeBuffer(var0);
      com.frojasg1.sun.nio.fs.NativeBuffer var4 = asNativeBuffer(var1);

      try {
         MoveFileEx0(var3.address(), var4.address(), var2);
      } finally {
         var4.release();
         var3.release();
      }

   }

   private static native void MoveFileEx0(long var0, long var2, int var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static int GetFileAttributes(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      int var2;
      try {
         var2 = GetFileAttributes0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native int GetFileAttributes0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void SetFileAttributes(String var0, int var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var2 = asNativeBuffer(var0);

      try {
         SetFileAttributes0(var2.address(), var1);
      } finally {
         var2.release();
      }

   }

   private static native void SetFileAttributes0(long var0, int var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void GetFileAttributesEx(String var0, long var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = asNativeBuffer(var0);

      try {
         GetFileAttributesEx0(var3.address(), var1);
      } finally {
         var3.release();
      }

   }

   private static native void GetFileAttributesEx0(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void SetFileTime(long var0, long var2, long var4, long var6) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void SetEndOfFile(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native int GetLogicalDrives() throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.VolumeInformation GetVolumeInformation(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      WindowsNativeDispatcher.VolumeInformation var3;
      try {
         WindowsNativeDispatcher.VolumeInformation var2 = new WindowsNativeDispatcher.VolumeInformation();
         GetVolumeInformation0(var1.address(), var2);
         var3 = var2;
      } finally {
         var1.release();
      }

      return var3;
   }

   private static native void GetVolumeInformation0(long var0, WindowsNativeDispatcher.VolumeInformation var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static int GetDriveType(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      int var2;
      try {
         var2 = GetDriveType0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native int GetDriveType0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.DiskFreeSpace GetDiskFreeSpaceEx(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      WindowsNativeDispatcher.DiskFreeSpace var3;
      try {
         WindowsNativeDispatcher.DiskFreeSpace var2 = new WindowsNativeDispatcher.DiskFreeSpace();
         GetDiskFreeSpaceEx0(var1.address(), var2);
         var3 = var2;
      } finally {
         var1.release();
      }

      return var3;
   }

   private static native void GetDiskFreeSpaceEx0(long var0, WindowsNativeDispatcher.DiskFreeSpace var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static String GetVolumePathName(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      String var2;
      try {
         var2 = GetVolumePathName0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native String GetVolumePathName0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void InitializeSecurityDescriptor(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void InitializeAcl(long var0, int var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static int GetFileSecurity(String var0, int var1, long var2, int var4) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var5 = asNativeBuffer(var0);

      int var6;
      try {
         var6 = GetFileSecurity0(var5.address(), var1, var2, var4);
      } finally {
         var5.release();
      }

      return var6;
   }

   private static native int GetFileSecurity0(long var0, int var2, long var3, int var5) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void SetFileSecurity(String var0, int var1, long var2) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var4 = asNativeBuffer(var0);

      try {
         SetFileSecurity0(var4.address(), var1, var2);
      } finally {
         var4.release();
      }

   }

   static native void SetFileSecurity0(long var0, int var2, long var3) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native long GetSecurityDescriptorOwner(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void SetSecurityDescriptorOwner(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native long GetSecurityDescriptorDacl(long var0);

   static native void SetSecurityDescriptorDacl(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.AclInformation GetAclInformation(long var0) {
      WindowsNativeDispatcher.AclInformation var2 = new WindowsNativeDispatcher.AclInformation();
      GetAclInformation0(var0, var2);
      return var2;
   }

   private static native void GetAclInformation0(long var0, WindowsNativeDispatcher.AclInformation var2);

   static native long GetAce(long var0, int var2);

   static native void AddAccessAllowedAceEx(long var0, int var2, int var3, long var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void AddAccessDeniedAceEx(long var0, int var2, int var3, long var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.Account LookupAccountSid(long var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      WindowsNativeDispatcher.Account var2 = new WindowsNativeDispatcher.Account();
      LookupAccountSid0(var0, var2);
      return var2;
   }

   private static native void LookupAccountSid0(long var0, WindowsNativeDispatcher.Account var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static int LookupAccountName(String var0, long var1, int var3) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var4 = asNativeBuffer(var0);

      int var5;
      try {
         var5 = LookupAccountName0(var4.address(), var1, var3);
      } finally {
         var4.release();
      }

      return var5;
   }

   private static native int LookupAccountName0(long var0, long var2, int var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native int GetLengthSid(long var0);

   static native String ConvertSidToStringSid(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static long ConvertStringSidToSid(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      long var2;
      try {
         var2 = ConvertStringSidToSid0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native long ConvertStringSidToSid0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native long GetCurrentProcess();

   static native long GetCurrentThread();

   static native long OpenProcessToken(long var0, int var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native long OpenThreadToken(long var0, int var2, boolean var3) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native long DuplicateTokenEx(long var0, int var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void SetThreadToken(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native int GetTokenInformation(long var0, int var2, long var3, int var5) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void AdjustTokenPrivileges(long var0, long var2, int var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native boolean AccessCheck(long var0, long var2, int var4, int var5, int var6, int var7, int var8) throws com.frojasg1.sun.nio.fs.WindowsException;

   static long LookupPrivilegeValue(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      long var2;
      try {
         var2 = LookupPrivilegeValue0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native long LookupPrivilegeValue0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void CreateSymbolicLink(String var0, String var1, int var2) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = asNativeBuffer(var0);
      com.frojasg1.sun.nio.fs.NativeBuffer var4 = asNativeBuffer(var1);

      try {
         CreateSymbolicLink0(var3.address(), var4.address(), var2);
      } finally {
         var4.release();
         var3.release();
      }

   }

   private static native void CreateSymbolicLink0(long var0, long var2, int var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static void CreateHardLink(String var0, String var1) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var2 = asNativeBuffer(var0);
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = asNativeBuffer(var1);

      try {
         CreateHardLink0(var2.address(), var3.address());
      } finally {
         var3.release();
         var2.release();
      }

   }

   private static native void CreateHardLink0(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static String GetFullPathName(String var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      com.frojasg1.sun.nio.fs.NativeBuffer var1 = asNativeBuffer(var0);

      String var2;
      try {
         var2 = GetFullPathName0(var1.address());
      } finally {
         var1.release();
      }

      return var2;
   }

   private static native String GetFullPathName0(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native String GetFinalPathNameByHandle(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native String FormatMessage(int var0);

   static native void LocalFree(long var0);

   static native long CreateIoCompletionPort(long var0, long var2, long var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.CompletionStatus GetQueuedCompletionStatus(long var0) throws com.frojasg1.sun.nio.fs.WindowsException {
      WindowsNativeDispatcher.CompletionStatus var2 = new WindowsNativeDispatcher.CompletionStatus();
      GetQueuedCompletionStatus0(var0, var2);
      return var2;
   }

   private static native void GetQueuedCompletionStatus0(long var0, WindowsNativeDispatcher.CompletionStatus var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void PostQueuedCompletionStatus(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void ReadDirectoryChangesW(long var0, long var2, int var4, boolean var5, int var6, long var7, long var9) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void CancelIo(long var0) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native int GetOverlappedResult(long var0, long var2) throws com.frojasg1.sun.nio.fs.WindowsException;

   static WindowsNativeDispatcher.BackupResult BackupRead(long var0, long var2, int var4, boolean var5, long var6) throws com.frojasg1.sun.nio.fs.WindowsException {
      WindowsNativeDispatcher.BackupResult var8 = new WindowsNativeDispatcher.BackupResult();
      BackupRead0(var0, var2, var4, var5, var6, var8);
      return var8;
   }

   private static native void BackupRead0(long var0, long var2, int var4, boolean var5, long var6, WindowsNativeDispatcher.BackupResult var8) throws com.frojasg1.sun.nio.fs.WindowsException;

   static native void BackupSeek(long var0, long var2, long var4) throws com.frojasg1.sun.nio.fs.WindowsException;

   static com.frojasg1.sun.nio.fs.NativeBuffer asNativeBuffer(String var0) {
      int var1 = var0.length() << 1;
      int var2 = var1 + 2;
      com.frojasg1.sun.nio.fs.NativeBuffer var3 = com.frojasg1.sun.nio.fs.NativeBuffers.getNativeBufferFromCache(var2);
      if (var3 == null) {
         var3 = com.frojasg1.sun.nio.fs.NativeBuffers.allocNativeBuffer(var2);
      } else if (var3.owner() == var0) {
         return var3;
      }

      char[] var4 = var0.toCharArray();
      unsafe.copyMemory(var4, (long)Unsafe.ARRAY_CHAR_BASE_OFFSET, (Object)null, var3.address(), (long)var1);
      unsafe.putChar(var3.address() + (long)var1, '\u0000');
      var3.setOwner(var0);
      return var3;
   }

   private static native void initIDs();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            System.loadLibrary("nio");
            return null;
         }
      });
      initIDs();
   }

   static class Account {
      private String domain;
      private String name;
      private int use;

      private Account() {
      }

      public String domain() {
         return this.domain;
      }

      public String name() {
         return this.name;
      }

      public int use() {
         return this.use;
      }
   }

   static class AclInformation {
      private int aceCount;

      private AclInformation() {
      }

      public int aceCount() {
         return this.aceCount;
      }
   }

   static class BackupResult {
      private int bytesTransferred;
      private long context;

      private BackupResult() {
      }

      int bytesTransferred() {
         return this.bytesTransferred;
      }

      long context() {
         return this.context;
      }
   }

   static class CompletionStatus {
      private int error;
      private int bytesTransferred;
      private long completionKey;

      private CompletionStatus() {
      }

      int error() {
         return this.error;
      }

      int bytesTransferred() {
         return this.bytesTransferred;
      }

      long completionKey() {
         return this.completionKey;
      }
   }

   static class DiskFreeSpace {
      private long freeBytesAvailable;
      private long totalNumberOfBytes;
      private long totalNumberOfFreeBytes;

      private DiskFreeSpace() {
      }

      public long freeBytesAvailable() {
         return this.freeBytesAvailable;
      }

      public long totalNumberOfBytes() {
         return this.totalNumberOfBytes;
      }

      public long totalNumberOfFreeBytes() {
         return this.totalNumberOfFreeBytes;
      }
   }

   static class FirstFile {
      private long handle;
      private String name;
      private int attributes;

      private FirstFile() {
      }

      public long handle() {
         return this.handle;
      }

      public String name() {
         return this.name;
      }

      public int attributes() {
         return this.attributes;
      }
   }

   static class FirstStream {
      private long handle;
      private String name;

      private FirstStream() {
      }

      public long handle() {
         return this.handle;
      }

      public String name() {
         return this.name;
      }
   }

   static class VolumeInformation {
      private String fileSystemName;
      private String volumeName;
      private int volumeSerialNumber;
      private int flags;

      private VolumeInformation() {
      }

      public String fileSystemName() {
         return this.fileSystemName;
      }

      public String volumeName() {
         return this.volumeName;
      }

      public int volumeSerialNumber() {
         return this.volumeSerialNumber;
      }

      public int flags() {
         return this.flags;
      }
   }
}
