package com.frojasg1.sun.rmi.transport;

import java.io.IOException;
import java.rmi.server.UID;
import com.frojasg1.sun.rmi.server.MarshalOutputStream;
import com.frojasg1.sun.rmi.transport.Connection;
import com.frojasg1.sun.rmi.transport.DGCAckHandler;

class ConnectionOutputStream extends MarshalOutputStream {
   private final com.frojasg1.sun.rmi.transport.Connection conn;
   private final boolean resultStream;
   private final UID ackID;
   private com.frojasg1.sun.rmi.transport.DGCAckHandler dgcAckHandler = null;

   ConnectionOutputStream(Connection var1, boolean var2) throws IOException {
      super(var1.getOutputStream());
      this.conn = var1;
      this.resultStream = var2;
      this.ackID = var2 ? new UID() : null;
   }

   void writeID() throws IOException {
      assert this.resultStream;

      this.ackID.write(this);
   }

   boolean isResultStream() {
      return this.resultStream;
   }

   void saveObject(Object var1) {
      if (this.dgcAckHandler == null) {
         this.dgcAckHandler = new com.frojasg1.sun.rmi.transport.DGCAckHandler(this.ackID);
      }

      this.dgcAckHandler.add(var1);
   }

   DGCAckHandler getDGCAckHandler() {
      return this.dgcAckHandler;
   }

   void done() {
      if (this.dgcAckHandler != null) {
         this.dgcAckHandler.startTimer();
      }

   }
}
