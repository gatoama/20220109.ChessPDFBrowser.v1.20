package com.frojasg1.sun.nio.ch;

import com.frojasg1.sun.nio.ch.SinkChannelImpl;
import com.frojasg1.sun.nio.ch.SourceChannelImpl;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.Pipe;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.Pipe.SinkChannel;
import java.nio.channels.Pipe.SourceChannel;
import java.nio.channels.spi.SelectorProvider;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureRandom;
import java.util.Random;

class PipeImpl extends Pipe {
   private static final int NUM_SECRET_BYTES = 16;
   private static final Random RANDOM_NUMBER_GENERATOR = new SecureRandom();
   private SourceChannel source;
   private SinkChannel sink;

   PipeImpl(SelectorProvider var1) throws IOException {
      try {
         AccessController.doPrivileged(new PipeImpl.Initializer(var1));
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getCause();
      }
   }

   public SourceChannel source() {
      return this.source;
   }

   public SinkChannel sink() {
      return this.sink;
   }

   private class Initializer implements PrivilegedExceptionAction<Void> {
      private final SelectorProvider sp;
      private IOException ioe;

      private Initializer(SelectorProvider var2) {
         this.ioe = null;
         this.sp = var2;
      }

      public Void run() throws IOException {
         PipeImpl.Initializer.LoopbackConnector var1 = new PipeImpl.Initializer.LoopbackConnector();
         var1.run();
         if (this.ioe instanceof ClosedByInterruptException) {
            this.ioe = null;
            Thread var2 = new Thread(var1) {
               public void interrupt() {
               }
            };
            var2.start();

            while(true) {
               try {
                  var2.join();
                  break;
               } catch (InterruptedException var4) {
               }
            }

            Thread.currentThread().interrupt();
         }

         if (this.ioe != null) {
            throw new IOException("Unable to establish loopback connection", this.ioe);
         } else {
            return null;
         }
      }

      private class LoopbackConnector implements Runnable {
         private LoopbackConnector() {
         }

         public void run() {
            ServerSocketChannel var1 = null;
            SocketChannel var2 = null;
            SocketChannel var3 = null;

            try {
               ByteBuffer var4 = ByteBuffer.allocate(16);
               ByteBuffer var5 = ByteBuffer.allocate(16);
               InetAddress var6 = InetAddress.getByName("127.0.0.1");

               assert var6.isLoopbackAddress();

               InetSocketAddress var7 = null;

               while(true) {
                  if (var1 == null || !var1.isOpen()) {
                     var1 = ServerSocketChannel.open();
                     var1.socket().bind(new InetSocketAddress(var6, 0));
                     var7 = new InetSocketAddress(var6, var1.socket().getLocalPort());
                  }

                  var2 = SocketChannel.open(var7);
                  PipeImpl.RANDOM_NUMBER_GENERATOR.nextBytes(var4.array());

                  do {
                     var2.write(var4);
                  } while(var4.hasRemaining());

                  var4.rewind();
                  var3 = var1.accept();

                  do {
                     var3.read(var5);
                  } while(var5.hasRemaining());

                  var5.rewind();
                  if (var5.equals(var4)) {
                     PipeImpl.this.source = new com.frojasg1.sun.nio.ch.SourceChannelImpl(Initializer.this.sp, var2);
                     PipeImpl.this.sink = new com.frojasg1.sun.nio.ch.SinkChannelImpl(Initializer.this.sp, var3);
                     break;
                  }

                  var3.close();
                  var2.close();
               }
            } catch (IOException var18) {
               try {
                  if (var2 != null) {
                     var2.close();
                  }

                  if (var3 != null) {
                     var3.close();
                  }
               } catch (IOException var17) {
               }

               Initializer.this.ioe = var18;
            } finally {
               try {
                  if (var1 != null) {
                     var1.close();
                  }
               } catch (IOException var16) {
               }

            }

         }
      }
   }
}
