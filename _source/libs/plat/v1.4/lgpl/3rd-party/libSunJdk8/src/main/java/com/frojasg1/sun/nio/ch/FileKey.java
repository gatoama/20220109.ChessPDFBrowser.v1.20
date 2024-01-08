package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.IOUtil;

import java.io.FileDescriptor;
import java.io.IOException;

public class FileKey {
   private long dwVolumeSerialNumber;
   private long nFileIndexHigh;
   private long nFileIndexLow;

   private FileKey() {
   }

   public static FileKey create(FileDescriptor var0) {
      FileKey var1 = new FileKey();

      try {
         var1.init(var0);
         return var1;
      } catch (IOException var3) {
         throw new Error(var3);
      }
   }

   public int hashCode() {
      return (int)(this.dwVolumeSerialNumber ^ this.dwVolumeSerialNumber >>> 32) + (int)(this.nFileIndexHigh ^ this.nFileIndexHigh >>> 32) + (int)(this.nFileIndexLow ^ this.nFileIndexHigh >>> 32);
   }

   public boolean equals(Object var1) {
      if (var1 == this) {
         return true;
      } else if (!(var1 instanceof FileKey)) {
         return false;
      } else {
         FileKey var2 = (FileKey)var1;
         return this.dwVolumeSerialNumber == var2.dwVolumeSerialNumber && this.nFileIndexHigh == var2.nFileIndexHigh && this.nFileIndexLow == var2.nFileIndexLow;
      }
   }

   private native void init(FileDescriptor var1) throws IOException;

   private static native void initIDs();

   static {
      IOUtil.load();
      initIDs();
   }
}
