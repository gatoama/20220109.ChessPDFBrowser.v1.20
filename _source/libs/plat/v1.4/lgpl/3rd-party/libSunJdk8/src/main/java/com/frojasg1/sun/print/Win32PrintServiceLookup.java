package com.frojasg1.sun.print;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import javax.print.DocFlavor;
import javax.print.MultiDocPrintService;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.PrinterName;

import com.frojasg1.sun.print.Win32PrintService;
import com.frojasg1.sun.security.action.GetPropertyAction;

public class Win32PrintServiceLookup extends PrintServiceLookup {
   private String defaultPrinter;
   private PrintService defaultPrintService;
   private String[] printers;
   private PrintService[] printServices;
   private static Win32PrintServiceLookup win32PrintLUS;

   public static Win32PrintServiceLookup getWin32PrintLUS() {
      if (win32PrintLUS == null) {
         PrintServiceLookup.lookupDefaultPrintService();
      }

      return win32PrintLUS;
   }

   public Win32PrintServiceLookup() {
      if (win32PrintLUS == null) {
         win32PrintLUS = this;
         String var1 = (String)AccessController.doPrivileged(new GetPropertyAction("os.name"));
         if (var1 != null && var1.startsWith("Windows 98")) {
            return;
         }

         Win32PrintServiceLookup.PrinterChangeListener var2 = new Win32PrintServiceLookup.PrinterChangeListener();
         var2.setDaemon(true);
         var2.start();
      }

   }

   public synchronized PrintService[] getPrintServices() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      if (this.printServices == null) {
         this.refreshServices();
      }

      return this.printServices;
   }

   private synchronized void refreshServices() {
      this.printers = this.getAllPrinterNames();
      if (this.printers == null) {
         this.printServices = new PrintService[0];
      } else {
         PrintService[] var1 = new PrintService[this.printers.length];
         PrintService var2 = this.getDefaultPrintService();

         int var3;
         for(var3 = 0; var3 < this.printers.length; ++var3) {
            if (var2 != null && this.printers[var3].equals(var2.getName())) {
               var1[var3] = var2;
            } else if (this.printServices == null) {
               var1[var3] = new com.frojasg1.sun.print.Win32PrintService(this.printers[var3]);
            } else {
               int var4;
               for(var4 = 0; var4 < this.printServices.length; ++var4) {
                  if (this.printServices[var4] != null && this.printers[var3].equals(this.printServices[var4].getName())) {
                     var1[var3] = this.printServices[var4];
                     this.printServices[var4] = null;
                     break;
                  }
               }

               if (var4 == this.printServices.length) {
                  var1[var3] = new com.frojasg1.sun.print.Win32PrintService(this.printers[var3]);
               }
            }
         }

         if (this.printServices != null) {
            for(var3 = 0; var3 < this.printServices.length; ++var3) {
               if (this.printServices[var3] instanceof com.frojasg1.sun.print.Win32PrintService && !this.printServices[var3].equals(this.defaultPrintService)) {
                  ((com.frojasg1.sun.print.Win32PrintService)this.printServices[var3]).invalidateService();
               }
            }
         }

         this.printServices = var1;
      }
   }

   public synchronized PrintService getPrintServiceByName(String var1) {
      if (var1 != null && !var1.equals("")) {
         PrintService[] var2 = this.getPrintServices();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var2[var3].getName().equals(var1)) {
               return var2[var3];
            }
         }

         return null;
      } else {
         return null;
      }
   }

   boolean matchingService(PrintService var1, PrintServiceAttributeSet var2) {
      if (var2 != null) {
         Attribute[] var3 = var2.toArray();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            PrintServiceAttribute var4 = var1.getAttribute((Class<PrintServiceAttribute>)var3[var5].getCategory());
            if (var4 == null || !var4.equals(var3[var5])) {
               return false;
            }
         }
      }

      return true;
   }

   public PrintService[] getPrintServices(DocFlavor var1, AttributeSet var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPrintJobAccess();
      }

      HashPrintRequestAttributeSet var4 = null;
      HashPrintServiceAttributeSet var5 = null;
      Attribute[] var6;
      if (var2 != null && !var2.isEmpty()) {
         var4 = new HashPrintRequestAttributeSet();
         var5 = new HashPrintServiceAttributeSet();
         var6 = var2.toArray();

         for(int var7 = 0; var7 < var6.length; ++var7) {
            if (var6[var7] instanceof PrintRequestAttribute) {
               var4.add(var6[var7]);
            } else if (var6[var7] instanceof PrintServiceAttribute) {
               var5.add(var6[var7]);
            }
         }
      }

      var6 = null;
      PrintService[] var11;
      if (var5 != null && var5.get(PrinterName.class) != null) {
         PrinterName var12 = (PrinterName)var5.get(PrinterName.class);
         PrintService var8 = this.getPrintServiceByName(var12.getValue());
         if (var8 != null && this.matchingService(var8, var5)) {
            var11 = new PrintService[]{var8};
         } else {
            var11 = new PrintService[0];
         }
      } else {
         var11 = this.getPrintServices();
      }

      if (var11.length == 0) {
         return var11;
      } else {
         ArrayList var13 = new ArrayList();

         for(int var14 = 0; var14 < var11.length; ++var14) {
            try {
               if (var11[var14].getUnsupportedAttributes(var1, var4) == null) {
                  var13.add(var11[var14]);
               }
            } catch (IllegalArgumentException var10) {
            }
         }

         var11 = new PrintService[var13.size()];
         return (PrintService[])((PrintService[])var13.toArray(var11));
      }
   }

   public MultiDocPrintService[] getMultiDocPrintServices(DocFlavor[] var1, AttributeSet var2) {
      SecurityManager var3 = System.getSecurityManager();
      if (var3 != null) {
         var3.checkPrintJobAccess();
      }

      return new MultiDocPrintService[0];
   }

   public synchronized PrintService getDefaultPrintService() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      this.defaultPrinter = this.getDefaultPrinterName();
      if (this.defaultPrinter == null) {
         return null;
      } else if (this.defaultPrintService != null && this.defaultPrintService.getName().equals(this.defaultPrinter)) {
         return this.defaultPrintService;
      } else {
         this.defaultPrintService = null;
         if (this.printServices != null) {
            for(int var2 = 0; var2 < this.printServices.length; ++var2) {
               if (this.defaultPrinter.equals(this.printServices[var2].getName())) {
                  this.defaultPrintService = this.printServices[var2];
                  break;
               }
            }
         }

         if (this.defaultPrintService == null) {
            this.defaultPrintService = new Win32PrintService(this.defaultPrinter);
         }

         return this.defaultPrintService;
      }
   }

   private native String getDefaultPrinterName();

   private native String[] getAllPrinterNames();

   private native long notifyFirstPrinterChange(String var1);

   private native void notifyClosePrinterChange(long var1);

   private native int notifyPrinterChange(long var1);

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("awt");
            return null;
         }
      });
   }

   class PrinterChangeListener extends Thread {
      long chgObj = Win32PrintServiceLookup.this.notifyFirstPrinterChange((String)null);

      PrinterChangeListener() {
      }

      public void run() {
         if (this.chgObj != -1L) {
            while(true) {
               if (Win32PrintServiceLookup.this.notifyPrinterChange(this.chgObj) == 0) {
                  Win32PrintServiceLookup.this.notifyClosePrinterChange(this.chgObj);
                  break;
               }

               try {
                  Win32PrintServiceLookup.this.refreshServices();
               } catch (SecurityException var2) {
                  break;
               }
            }
         }

      }
   }
}
