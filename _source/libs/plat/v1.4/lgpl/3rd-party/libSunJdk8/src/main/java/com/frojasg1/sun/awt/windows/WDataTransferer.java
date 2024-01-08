package com.frojasg1.sun.awt.windows;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorTable;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedMap;
import com.frojasg1.sun.awt.datatransfer.DataTransferer;
import com.frojasg1.sun.awt.datatransfer.ToolkitThreadBlockedHandler;
import com.frojasg1.sun.awt.image.ImageRepresentation;
import com.frojasg1.sun.awt.image.ToolkitImage;
import com.frojasg1.sun.awt.windows.EHTMLReadMode;
import com.frojasg1.sun.awt.windows.HTMLCodec;
import com.frojasg1.sun.awt.windows.WToolkitThreadBlockedHandler;

final class WDataTransferer extends DataTransferer {
   private static final String[] predefinedClipboardNames = new String[]{"", "TEXT", "BITMAP", "METAFILEPICT", "SYLK", "DIF", "TIFF", "OEM TEXT", "DIB", "PALETTE", "PENDATA", "RIFF", "WAVE", "UNICODE TEXT", "ENHMETAFILE", "HDROP", "LOCALE", "DIBV5"};
   private static final Map<String, Long> predefinedClipboardNameMap;
   public static final int CF_TEXT = 1;
   public static final int CF_METAFILEPICT = 3;
   public static final int CF_DIB = 8;
   public static final int CF_ENHMETAFILE = 14;
   public static final int CF_HDROP = 15;
   public static final int CF_LOCALE = 16;
   public static final long CF_HTML;
   public static final long CFSTR_INETURL;
   public static final long CF_PNG;
   public static final long CF_JFIF;
   public static final long CF_FILEGROUPDESCRIPTORW;
   public static final long CF_FILEGROUPDESCRIPTORA;
   private static final Long L_CF_LOCALE;
   private static final DirectColorModel directColorModel;
   private static final int[] bandmasks;
   private static WDataTransferer transferer;
   private final ToolkitThreadBlockedHandler handler = new com.frojasg1.sun.awt.windows.WToolkitThreadBlockedHandler();
   private static final byte[] UNICODE_NULL_TERMINATOR;

   private WDataTransferer() {
   }

   static synchronized WDataTransferer getInstanceImpl() {
      if (transferer == null) {
         transferer = new WDataTransferer();
      }

      return transferer;
   }

   public SortedMap<Long, DataFlavor> getFormatsForFlavors(DataFlavor[] var1, FlavorTable var2) {
      SortedMap var3 = super.getFormatsForFlavors(var1, var2);
      var3.remove(L_CF_LOCALE);
      return var3;
   }

   public String getDefaultUnicodeEncoding() {
      return "utf-16le";
   }

   public byte[] translateTransferable(Transferable var1, DataFlavor var2, long var3) throws IOException {
      Object var5 = null;
      byte[] var6;
      if (var3 == CF_HTML) {
         if (var1.isDataFlavorSupported(DataFlavor.selectionHtmlFlavor)) {
            var6 = super.translateTransferable(var1, DataFlavor.selectionHtmlFlavor, var3);
         } else if (var1.isDataFlavorSupported(DataFlavor.allHtmlFlavor)) {
            var6 = super.translateTransferable(var1, DataFlavor.allHtmlFlavor, var3);
         } else {
            var6 = com.frojasg1.sun.awt.windows.HTMLCodec.convertToHTMLFormat(super.translateTransferable(var1, var2, var3));
         }
      } else {
         var6 = super.translateTransferable(var1, var2, var3);
      }

      return var6;
   }

   public Object translateStream(InputStream var1, DataFlavor var2, long var3, Transferable var5) throws IOException {
      if (var3 == CF_HTML && var2.isFlavorTextType()) {
         var1 = new com.frojasg1.sun.awt.windows.HTMLCodec((InputStream)var1, com.frojasg1.sun.awt.windows.EHTMLReadMode.getEHTMLReadMode(var2));
      }

      return super.translateStream((InputStream)var1, var2, var3, var5);
   }

   public Object translateBytes(byte[] var1, DataFlavor var2, long var3, Transferable var5) throws IOException {
      String var6;
      if (var3 != CF_FILEGROUPDESCRIPTORA && var3 != CF_FILEGROUPDESCRIPTORW) {
         if (var3 == CFSTR_INETURL && URL.class.equals(var2.getRepresentationClass())) {
            var6 = getDefaultTextCharset();
            if (var5 != null && var5.isDataFlavorSupported(javaTextEncodingFlavor)) {
               try {
                  var6 = new String((byte[])((byte[])var5.getTransferData(javaTextEncodingFlavor)), "UTF-8");
               } catch (UnsupportedFlavorException var10) {
               }
            }

            return new URL(new String(var1, var6));
         } else {
            return super.translateBytes(var1, var2, var3, var5);
         }
      } else if (var1 != null && DataFlavor.javaFileListFlavor.equals(var2)) {
         var6 = new String(var1, 0, var1.length, "UTF-16LE");
         String[] var7 = var6.split("\u0000");
         if (0 == var7.length) {
            return null;
         } else {
            File[] var8 = new File[var7.length];

            for(int var9 = 0; var9 < var7.length; ++var9) {
               var8[var9] = new File(var7[var9]);
               var8[var9].deleteOnExit();
            }

            return Arrays.asList(var8);
         }
      } else {
         throw new IOException("data translation failed");
      }
   }

   public boolean isLocaleDependentTextFormat(long var1) {
      return var1 == 1L || var1 == CFSTR_INETURL;
   }

   public boolean isFileFormat(long var1) {
      return var1 == 15L || var1 == CF_FILEGROUPDESCRIPTORA || var1 == CF_FILEGROUPDESCRIPTORW;
   }

   protected Long getFormatForNativeAsLong(String var1) {
      Long var2 = (Long)predefinedClipboardNameMap.get(var1);
      if (var2 == null) {
         var2 = registerClipboardFormat(var1);
      }

      return var2;
   }

   protected String getNativeForFormat(long var1) {
      return var1 < (long)predefinedClipboardNames.length ? predefinedClipboardNames[(int)var1] : getClipboardFormatName(var1);
   }

   public ToolkitThreadBlockedHandler getToolkitThreadBlockedHandler() {
      return this.handler;
   }

   private static native long registerClipboardFormat(String var0);

   private static native String getClipboardFormatName(long var0);

   public boolean isImageFormat(long var1) {
      return var1 == 8L || var1 == 14L || var1 == 3L || var1 == CF_PNG || var1 == CF_JFIF;
   }

   protected byte[] imageToPlatformBytes(Image var1, long var2) throws IOException {
      String var4 = null;
      if (var2 == CF_PNG) {
         var4 = "image/png";
      } else if (var2 == CF_JFIF) {
         var4 = "image/jpeg";
      }

      if (var4 != null) {
         return this.imageToStandardBytes(var1, var4);
      } else {
         boolean var5 = false;
         boolean var6 = false;
         int var21;
         int var22;
         if (var1 instanceof ToolkitImage) {
            ImageRepresentation var7 = ((ToolkitImage)var1).getImageRep();
            var7.reconstruct(32);
            var21 = var7.getWidth();
            var22 = var7.getHeight();
         } else {
            var21 = var1.getWidth((ImageObserver)null);
            var22 = var1.getHeight((ImageObserver)null);
         }

         int var23 = var21 * 3 % 4;
         int var8 = var23 > 0 ? 4 - var23 : 0;
         ColorSpace var9 = ColorSpace.getInstance(1000);
         int[] var10 = new int[]{8, 8, 8};
         int[] var11 = new int[]{2, 1, 0};
         ComponentColorModel var12 = new ComponentColorModel(var9, var10, false, false, 1, 0);
         WritableRaster var13 = Raster.createInterleavedRaster(0, var21, var22, var21 * 3 + var8, 3, var11, (Point)null);
         BufferedImage var14 = new BufferedImage(var12, var13, false, (Hashtable)null);
         AffineTransform var15 = new AffineTransform(1.0F, 0.0F, 0.0F, -1.0F, 0.0F, (float)var22);
         Graphics2D var16 = var14.createGraphics();

         try {
            var16.drawImage(var1, var15, (ImageObserver)null);
         } finally {
            var16.dispose();
         }

         DataBufferByte var17 = (DataBufferByte)var13.getDataBuffer();
         byte[] var18 = var17.getData();
         return this.imageDataToPlatformImageBytes(var18, var21, var22, var2);
      }
   }

   protected ByteArrayOutputStream convertFileListToBytes(ArrayList<String> var1) throws IOException {
      ByteArrayOutputStream var2 = new ByteArrayOutputStream();
      if (var1.isEmpty()) {
         var2.write(UNICODE_NULL_TERMINATOR);
      } else {
         for(int var3 = 0; var3 < var1.size(); ++var3) {
            byte[] var4 = ((String)var1.get(var3)).getBytes(this.getDefaultUnicodeEncoding());
            var2.write(var4, 0, var4.length);
            var2.write(UNICODE_NULL_TERMINATOR);
         }
      }

      var2.write(UNICODE_NULL_TERMINATOR);
      return var2;
   }

   private native byte[] imageDataToPlatformImageBytes(byte[] var1, int var2, int var3, long var4);

   protected Image platformImageBytesToImage(byte[] var1, long var2) throws IOException {
      String var4 = null;
      if (var2 == CF_PNG) {
         var4 = "image/png";
      } else if (var2 == CF_JFIF) {
         var4 = "image/jpeg";
      }

      if (var4 != null) {
         return this.standardImageBytesToImage(var1, var4);
      } else {
         int[] var5 = this.platformImageBytesToImageData(var1, var2);
         if (var5 == null) {
            throw new IOException("data translation failed");
         } else {
            int var6 = var5.length - 2;
            int var7 = var5[var6];
            int var8 = var5[var6 + 1];
            DataBufferInt var9 = new DataBufferInt(var5, var6);
            WritableRaster var10 = Raster.createPackedRaster(var9, var7, var8, var7, bandmasks, (Point)null);
            return new BufferedImage(directColorModel, var10, false, (Hashtable)null);
         }
      }
   }

   private native int[] platformImageBytesToImageData(byte[] var1, long var2) throws IOException;

   protected native String[] dragQueryFile(byte[] var1);

   static {
      HashMap var0 = new HashMap(predefinedClipboardNames.length, 1.0F);

      for(int var1 = 1; var1 < predefinedClipboardNames.length; ++var1) {
         var0.put(predefinedClipboardNames[var1], (long)var1);
      }

      predefinedClipboardNameMap = Collections.synchronizedMap(var0);
      CF_HTML = registerClipboardFormat("HTML Format");
      CFSTR_INETURL = registerClipboardFormat("UniformResourceLocator");
      CF_PNG = registerClipboardFormat("PNG");
      CF_JFIF = registerClipboardFormat("JFIF");
      CF_FILEGROUPDESCRIPTORW = registerClipboardFormat("FileGroupDescriptorW");
      CF_FILEGROUPDESCRIPTORA = registerClipboardFormat("FileGroupDescriptor");
      L_CF_LOCALE = (Long)predefinedClipboardNameMap.get(predefinedClipboardNames[16]);
      directColorModel = new DirectColorModel(24, 16711680, 65280, 255);
      bandmasks = new int[]{directColorModel.getRedMask(), directColorModel.getGreenMask(), directColorModel.getBlueMask()};
      UNICODE_NULL_TERMINATOR = new byte[]{0, 0};
   }
}
