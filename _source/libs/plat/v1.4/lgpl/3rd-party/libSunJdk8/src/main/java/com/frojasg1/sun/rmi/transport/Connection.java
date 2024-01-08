package com.frojasg1.sun.rmi.transport;

import com.frojasg1.sun.rmi.transport.Channel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection {
   InputStream getInputStream() throws IOException;

   void releaseInputStream() throws IOException;

   OutputStream getOutputStream() throws IOException;

   void releaseOutputStream() throws IOException;

   boolean isReusable();

   void close() throws IOException;

   Channel getChannel();
}
