package com.frojasg1.sun.awt.windows;

import java.io.IOException;
import java.io.InputStream;

final class WDropTargetContextPeerIStream extends InputStream {
   private long istream;

   WDropTargetContextPeerIStream(long var1) throws IOException {
      if (var1 == 0L) {
         throw new IOException("No IStream");
      } else {
         this.istream = var1;
      }
   }

   public int available() throws IOException {
      if (this.istream == 0L) {
         throw new IOException("No IStream");
      } else {
         return this.Available(this.istream);
      }
   }

   private native int Available(long var1);

   public int read() throws IOException {
      if (this.istream == 0L) {
         throw new IOException("No IStream");
      } else {
         return this.Read(this.istream);
      }
   }

   private native int Read(long var1) throws IOException;

   public int read(byte[] var1, int var2, int var3) throws IOException {
      if (this.istream == 0L) {
         throw new IOException("No IStream");
      } else {
         return this.ReadBytes(this.istream, var1, var2, var3);
      }
   }

   private native int ReadBytes(long var1, byte[] var3, int var4, int var5) throws IOException;

   public void close() throws IOException {
      if (this.istream != 0L) {
         super.close();
         this.Close(this.istream);
         this.istream = 0L;
      }

   }

   private native void Close(long var1);
}
