package com.frojasg1.sun.net.httpserver;

import com.frojasg1.sun.net.httpserver.ExchangeImpl;
import com.frojasg1.sun.net.httpserver.LeftOverInputStream;
import com.frojasg1.sun.net.httpserver.StreamClosedException;
import com.frojasg1.sun.net.httpserver.WriteFinishedEvent;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

class FixedLengthOutputStream extends FilterOutputStream {
   private long remaining;
   private boolean eof = false;
   private boolean closed = false;
   com.frojasg1.sun.net.httpserver.ExchangeImpl t;

   FixedLengthOutputStream(com.frojasg1.sun.net.httpserver.ExchangeImpl var1, OutputStream var2, long var3) {
      super(var2);
      this.t = var1;
      this.remaining = var3;
   }

   public void write(int var1) throws IOException {
      if (this.closed) {
         throw new IOException("stream closed");
      } else {
         this.eof = this.remaining == 0L;
         if (this.eof) {
            throw new com.frojasg1.sun.net.httpserver.StreamClosedException();
         } else {
            this.out.write(var1);
            --this.remaining;
         }
      }
   }

   public void write(byte[] var1, int var2, int var3) throws IOException {
      if (this.closed) {
         throw new IOException("stream closed");
      } else {
         this.eof = this.remaining == 0L;
         if (this.eof) {
            throw new com.frojasg1.sun.net.httpserver.StreamClosedException();
         } else if ((long)var3 > this.remaining) {
            throw new IOException("too many bytes to write to stream");
         } else {
            this.out.write(var1, var2, var3);
            this.remaining -= (long)var3;
         }
      }
   }

   public void close() throws IOException {
      if (!this.closed) {
         this.closed = true;
         if (this.remaining > 0L) {
            this.t.close();
            throw new IOException("insufficient bytes written to stream");
         } else {
            this.flush();
            this.eof = true;
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
}
