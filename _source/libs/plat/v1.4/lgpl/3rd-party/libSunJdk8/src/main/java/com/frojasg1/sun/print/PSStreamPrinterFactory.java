package com.frojasg1.sun.print;

import com.frojasg1.sun.print.PSStreamPrintService;

import java.io.OutputStream;
import javax.print.DocFlavor;
import javax.print.StreamPrintService;
import javax.print.StreamPrintServiceFactory;
import javax.print.DocFlavor.BYTE_ARRAY;
import javax.print.DocFlavor.INPUT_STREAM;
import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.DocFlavor.URL;

public class PSStreamPrinterFactory extends StreamPrintServiceFactory {
   static final String psMimeType = "application/postscript";
   static final DocFlavor[] supportedDocFlavors;

   public PSStreamPrinterFactory() {
   }

   public String getOutputFormat() {
      return "application/postscript";
   }

   public DocFlavor[] getSupportedDocFlavors() {
      return getFlavors();
   }

   static DocFlavor[] getFlavors() {
      DocFlavor[] var0 = new DocFlavor[supportedDocFlavors.length];
      System.arraycopy(supportedDocFlavors, 0, var0, 0, var0.length);
      return var0;
   }

   public StreamPrintService getPrintService(OutputStream var1) {
      return new PSStreamPrintService(var1);
   }

   static {
      supportedDocFlavors = new DocFlavor[]{SERVICE_FORMATTED.PAGEABLE, SERVICE_FORMATTED.PRINTABLE, BYTE_ARRAY.GIF, INPUT_STREAM.GIF, URL.GIF, BYTE_ARRAY.JPEG, INPUT_STREAM.JPEG, URL.JPEG, BYTE_ARRAY.PNG, INPUT_STREAM.PNG, URL.PNG};
   }
}
