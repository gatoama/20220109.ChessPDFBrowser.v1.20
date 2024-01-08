package com.frojasg1.sun.awt.windows;

import java.io.FileInputStream;
import java.io.IOException;
import com.frojasg1.sun.awt.PeerEvent;
import com.frojasg1.sun.awt.SunToolkit;
import com.frojasg1.sun.awt.dnd.SunDropTargetContextPeer;
import com.frojasg1.sun.awt.dnd.SunDropTargetEvent;
import com.frojasg1.sun.awt.windows.WDropTargetContextPeerFileStream;
import com.frojasg1.sun.awt.windows.WDropTargetContextPeerIStream;

final class WDropTargetContextPeer extends SunDropTargetContextPeer {
   static WDropTargetContextPeer getWDropTargetContextPeer() {
      return new WDropTargetContextPeer();
   }

   private WDropTargetContextPeer() {
   }

   private static FileInputStream getFileStream(String var0, long var1) throws IOException {
      return new com.frojasg1.sun.awt.windows.WDropTargetContextPeerFileStream(var0, var1);
   }

   private static Object getIStream(long var0) throws IOException {
      return new com.frojasg1.sun.awt.windows.WDropTargetContextPeerIStream(var0);
   }

   protected Object getNativeData(long var1) {
      return this.getData(this.getNativeDragContext(), var1);
   }

   protected void doDropDone(boolean var1, int var2, boolean var3) {
      this.dropDone(this.getNativeDragContext(), var1, var2);
   }

   protected void eventPosted(final SunDropTargetEvent var1) {
      if (var1.getID() != 502) {
         Runnable var2 = new Runnable() {
            public void run() {
               var1.getDispatcher().unregisterAllEvents();
            }
         };
         PeerEvent var3 = new PeerEvent(var1.getSource(), var2, 0L);
         SunToolkit.executeOnEventHandlerThread(var3);
      }

   }

   private native Object getData(long var1, long var3);

   private native void dropDone(long var1, boolean var3, int var4);
}
