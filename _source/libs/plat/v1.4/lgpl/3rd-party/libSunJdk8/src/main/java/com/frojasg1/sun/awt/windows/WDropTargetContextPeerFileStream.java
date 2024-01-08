package com.frojasg1.sun.awt.windows;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

final class WDropTargetContextPeerFileStream extends FileInputStream {
   private long stgmedium;

   WDropTargetContextPeerFileStream(String var1, long var2) throws FileNotFoundException {
      super(var1);
      this.stgmedium = var2;
   }

   public void close() throws IOException {
      if (this.stgmedium != 0L) {
         super.close();
         this.freeStgMedium(this.stgmedium);
         this.stgmedium = 0L;
      }

   }

   private native void freeStgMedium(long var1);
}
