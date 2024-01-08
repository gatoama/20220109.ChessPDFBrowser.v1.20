package com.frojasg1.sun.net.httpserver;

import com.frojasg1.sun.net.httpserver.ExchangeImpl;
import com.frojasg1.sun.net.httpserver.LeftOverInputStream;
import com.frojasg1.sun.net.httpserver.WriteFinishedEvent;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class UndefLengthOutputStream extends FilterOutputStream {
   private boolean closed = false;
   com.frojasg1.sun.net.httpserver.ExchangeImpl t;

   UndefLengthOutputStream(com.frojasg1.sun.net.httpserver.ExchangeImpl var1, OutputStream var2) {
      super(var2);
      this.t = var1;
   }

   public void write(int var1) throws IOException {
      if (this.closed) {
         throw new IOException("stream closed");
      } else {
         this.out.write(var1);
      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new IOException("stream closed");
      } else {
         this.out.write(var1, var2, var3);
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         this.flush();
         com.frojasg1.sun.net.httpserver.LeftOverInputStream var1 = this.t.getOriginalInputStream();
         if (!var1.isClosed()) {
            try {
               var1.close();
            } catch (IOException var3) {
            }
         }

         com.frojasg1.sun.net.httpserver.WriteFinishedEvent var2 = new com.frojasg1.sun.net.httpserver.WriteFinishedEvent(this.t);
         this.t.getHttpContext().getServerImpl().addEvent(var2);
      }
   }
}
