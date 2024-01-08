package com.frojasg1.sun.awt.windows;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.NotSerializableException;
import java.util.Iterator;
import java.util.SortedMap;
import com.frojasg1.sun.awt.datatransfer.DataTransferer;
import com.frojasg1.sun.awt.datatransfer.SunClipboard;
import com.frojasg1.sun.awt.windows.WDataTransferer;

final class WClipboard extends SunClipboard {
   private boolean isClipboardViewerRegistered;

   WClipboard() {
      super("System");
   }

   public long getID() {
      return 0L;
   }

   protected void setContentsNative(Transferable var1) {
      SortedMap var2 = com.frojasg1.sun.awt.windows.WDataTransferer.getInstance().getFormatsForTransferable(var1, getDefaultFlavorTable());
      this.openClipboard(this);

      try {
         Iterator var3 = var2.keySet().iterator();

         while(var3.hasNext()) {
            Long var4 = (Long)var3.next();
            DataFlavor var5 = (DataFlavor)var2.get(var4);

            try {
               byte[] var6 = com.frojasg1.sun.awt.windows.WDataTransferer.getInstance().translateTransferable(var1, var5, var4);
               this.publishClipboardData(var4, var6);
            } catch (IOException var10) {
               if (!var5.isMimeTypeEqual("application/x-java-jvm-local-objectref") || !(var10 instanceof NotSerializableException)) {
                  var10.printStackTrace();
               }
            }
         }
      } finally {
         this.closeClipboard();
      }

   }

   private void lostSelectionOwnershipImpl() {
      this.lostOwnershipImpl();
   }

   protected void clearNativeContext() {
   }

   public native void openClipboard(SunClipboard var1) throws IllegalStateException;

   public native void closeClipboard();

   private native void publishClipboardData(long var1, byte[] var3);

   private static native void init();

   protected native long[] getClipboardFormats();

   protected native byte[] getClipboardData(long var1) throws IOException;

   protected void registerClipboardViewerChecked() {
      if (!this.isClipboardViewerRegistered) {
         this.registerClipboardViewer();
         this.isClipboardViewerRegistered = true;
      }

   }

   private native void registerClipboardViewer();

   protected void unregisterClipboardViewerChecked() {
   }

   private void handleContentsChanged() {
      if (this.areFlavorListenersRegistered()) {
         long[] var1 = null;

         try {
            this.openClipboard((SunClipboard)null);
            var1 = this.getClipboardFormats();
         } catch (IllegalStateException var6) {
         } finally {
            this.closeClipboard();
         }

         this.checkChange(var1);
      }
   }

   protected Transferable createLocaleTransferable(long[] var1) throws IOException {
      boolean var2 = false;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3] == 16L) {
            var2 = true;
            break;
         }
      }

      if (!var2) {
         return null;
      } else {
         Object var6 = null;

         final byte[] var7;
         try {
            var7 = this.getClipboardData(16L);
         } catch (IOException var5) {
            return null;
         }

         return new Transferable() {
            public DataFlavor[] getTransferDataFlavors() {
               return new DataFlavor[]{DataTransferer.javaTextEncodingFlavor};
            }

            public boolean isDataFlavorSupported(DataFlavor var1) {
               return var1.equals(DataTransferer.javaTextEncodingFlavor);
            }

            public Object getTransferData(DataFlavor var1) throws UnsupportedFlavorException {
               if (this.isDataFlavorSupported(var1)) {
                  return var7;
               } else {
                  throw new UnsupportedFlavorException(var1);
               }
            }
         };
      }
   }

   static {
      init();
   }
}
