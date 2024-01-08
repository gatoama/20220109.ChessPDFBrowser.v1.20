package com.frojasg1.sun.print;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URI;
import java.util.Locale;
import java.util.Vector;
import javax.print.CancelablePrintJob;
import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.DocFlavor.BYTE_ARRAY;
import javax.print.DocFlavor.INPUT_STREAM;
import javax.print.DocFlavor.URL;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.DocAttributeSet;
import javax.print.attribute.HashPrintJobAttributeSet;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintJobAttribute;
import javax.print.attribute.PrintJobAttributeSet;
import javax.print.attribute.PrintRequestAttribute;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.DocumentName;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.JobOriginatingUserName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.event.PrintJobAttributeListener;
import javax.print.event.PrintJobEvent;
import javax.print.event.PrintJobListener;
import com.frojasg1.sun.awt.windows.WPrinterJob;
import com.frojasg1.sun.print.ImagePrinter;
import com.frojasg1.sun.print.PrintJobAttributeException;
import com.frojasg1.sun.print.PrintJobFlavorException;
import com.frojasg1.sun.print.Win32PrintService;

public class Win32PrintJob implements CancelablePrintJob {
   private transient Vector jobListeners;
   private transient Vector attrListeners;
   private transient Vector listenedAttributeSets;
   private com.frojasg1.sun.print.Win32PrintService service;
   private boolean fidelity;
   private boolean printing = false;
   private boolean printReturned = false;
   private PrintRequestAttributeSet reqAttrSet = null;
   private PrintJobAttributeSet jobAttrSet = null;
   private PrinterJob job;
   private Doc doc;
   private String mDestination = null;
   private InputStream instream = null;
   private Reader reader = null;
   private String jobName = "Java Printing";
   private int copies = 0;
   private MediaSizeName mediaName = null;
   private MediaSize mediaSize = null;
   private OrientationRequested orient = null;
   private long hPrintJob;
   private static final int PRINTBUFFERLEN = 8192;

   Win32PrintJob(Win32PrintService var1) {
      this.service = var1;
   }

   public PrintService getPrintService() {
      return this.service;
   }

   public PrintJobAttributeSet getAttributes() {
      synchronized(this) {
         if (this.jobAttrSet == null) {
            HashPrintJobAttributeSet var2 = new HashPrintJobAttributeSet();
            return AttributeSetUtilities.unmodifiableView(var2);
         } else {
            return this.jobAttrSet;
         }
      }
   }

   public void addPrintJobListener(PrintJobListener var1) {
      synchronized(this) {
         if (var1 != null) {
            if (this.jobListeners == null) {
               this.jobListeners = new Vector();
            }

            this.jobListeners.add(var1);
         }
      }
   }

   public void removePrintJobListener(PrintJobListener var1) {
      synchronized(this) {
         if (var1 != null && this.jobListeners != null) {
            this.jobListeners.remove(var1);
            if (this.jobListeners.isEmpty()) {
               this.jobListeners = null;
            }

         }
      }
   }

   private void closeDataStreams() {
      if (this.doc != null) {
         Object var1 = null;

         try {
            var1 = this.doc.getPrintData();
         } catch (IOException var25) {
            return;
         }

         if (this.instream != null) {
            try {
               this.instream.close();
            } catch (IOException var23) {
            } finally {
               this.instream = null;
            }
         } else if (this.reader != null) {
            try {
               this.reader.close();
            } catch (IOException var21) {
            } finally {
               this.reader = null;
            }
         } else if (var1 instanceof InputStream) {
            try {
               ((InputStream)var1).close();
            } catch (IOException var20) {
            }
         } else if (var1 instanceof Reader) {
            try {
               ((Reader)var1).close();
            } catch (IOException var19) {
            }
         }

      }
   }

   private void notifyEvent(int var1) {
      switch(var1) {
      case 101:
      case 102:
      case 103:
      case 105:
      case 106:
         this.closeDataStreams();
      case 104:
      default:
         synchronized(this) {
            if (this.jobListeners != null) {
               PrintJobEvent var4 = new PrintJobEvent(this, var1);

               for(int var5 = 0; var5 < this.jobListeners.size(); ++var5) {
                  PrintJobListener var3 = (PrintJobListener)((PrintJobListener)this.jobListeners.elementAt(var5));
                  switch(var1) {
                  case 101:
                     var3.printJobCanceled(var4);
                     break;
                  case 102:
                     var3.printJobCompleted(var4);
                     break;
                  case 103:
                     var3.printJobFailed(var4);
                  case 104:
                  default:
                     break;
                  case 105:
                     var3.printJobNoMoreEvents(var4);
                     break;
                  case 106:
                     var3.printDataTransferCompleted(var4);
                  }
               }
            }

         }
      }
   }

   public void addPrintJobAttributeListener(PrintJobAttributeListener var1, PrintJobAttributeSet var2) {
      synchronized(this) {
         if (var1 != null) {
            if (this.attrListeners == null) {
               this.attrListeners = new Vector();
               this.listenedAttributeSets = new Vector();
            }

            this.attrListeners.add(var1);
            if (var2 == null) {
               var2 = new HashPrintJobAttributeSet();
            }

            this.listenedAttributeSets.add(var2);
         }
      }
   }

   public void removePrintJobAttributeListener(PrintJobAttributeListener var1) {
      synchronized(this) {
         if (var1 != null && this.attrListeners != null) {
            int var3 = this.attrListeners.indexOf(var1);
            if (var3 != -1) {
               this.attrListeners.remove(var3);
               this.listenedAttributeSets.remove(var3);
               if (this.attrListeners.isEmpty()) {
                  this.attrListeners = null;
                  this.listenedAttributeSets = null;
               }

            }
         }
      }
   }

   public void print(Doc var1, PrintRequestAttributeSet var2) throws PrintException {
      synchronized(this) {
         if (this.printing) {
            throw new PrintException("already printing");
         }

         this.printing = true;
      }

      PrinterState var3 = (PrinterState)this.service.getAttribute(PrinterState.class);
      if (var3 == PrinterState.STOPPED) {
         PrinterStateReasons var4 = (PrinterStateReasons)this.service.getAttribute(PrinterStateReasons.class);
         if (var4 != null && var4.containsKey(PrinterStateReason.SHUTDOWN)) {
            throw new PrintException("PrintService is no longer available.");
         }
      }

      if ((PrinterIsAcceptingJobs)this.service.getAttribute(PrinterIsAcceptingJobs.class) == PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS) {
         throw new PrintException("Printer is not accepting job.");
      } else {
         this.doc = var1;
         DocFlavor var41 = var1.getDocFlavor();

         Object var5;
         try {
            var5 = var1.getPrintData();
         } catch (IOException var35) {
            this.notifyEvent(103);
            throw new PrintException("can't get print data: " + var35.toString());
         }

         if (var5 == null) {
            throw new PrintException("Null print data.");
         } else if (var41 != null && this.service.isDocFlavorSupported(var41)) {
            this.initializeAttributeSets(var1, var2);
            this.getAttributeValues(var41);
            String var6 = var41.getRepresentationClassName();
            if (!var41.equals(INPUT_STREAM.GIF) && !var41.equals(INPUT_STREAM.JPEG) && !var41.equals(INPUT_STREAM.PNG) && !var41.equals(BYTE_ARRAY.GIF) && !var41.equals(BYTE_ARRAY.JPEG) && !var41.equals(BYTE_ARRAY.PNG)) {
               if (!var41.equals(URL.GIF) && !var41.equals(URL.JPEG) && !var41.equals(URL.PNG)) {
                  if (var6.equals("java.awt.print.Pageable")) {
                     try {
                        this.pageableJob((Pageable)var1.getPrintData());
                        this.service.wakeNotifier();
                     } catch (ClassCastException var26) {
                        this.notifyEvent(103);
                        throw new PrintException(var26);
                     } catch (IOException var27) {
                        this.notifyEvent(103);
                        throw new PrintException(var27);
                     }
                  } else if (var6.equals("java.awt.print.Printable")) {
                     try {
                        this.printableJob((Printable)var1.getPrintData());
                        this.service.wakeNotifier();
                     } catch (ClassCastException var28) {
                        this.notifyEvent(103);
                        throw new PrintException(var28);
                     } catch (IOException var29) {
                        this.notifyEvent(103);
                        throw new PrintException(var29);
                     }
                  } else if (!var6.equals("[B") && !var6.equals("java.io.InputStream") && !var6.equals("java.net.URL")) {
                     this.notifyEvent(103);
                     throw new PrintException("unrecognized class: " + var6);
                  } else {
                     if (var6.equals("java.net.URL")) {
                        java.net.URL var7 = (java.net.URL)var5;

                        try {
                           this.instream = var7.openStream();
                        } catch (IOException var31) {
                           this.notifyEvent(103);
                           throw new PrintException(var31.toString());
                        }
                     } else {
                        try {
                           this.instream = var1.getStreamForBytes();
                        } catch (IOException var30) {
                           this.notifyEvent(103);
                           throw new PrintException(var30.toString());
                        }
                     }

                     if (this.instream == null) {
                        this.notifyEvent(103);
                        throw new PrintException("No stream for data");
                     } else if (this.mDestination != null) {
                        try {
                           FileOutputStream var43 = new FileOutputStream(this.mDestination);
                           byte[] var45 = new byte[1024];

                           while(true) {
                              int var46;
                              if ((var46 = this.instream.read(var45, 0, var45.length)) < 0) {
                                 var43.flush();
                                 var43.close();
                                 break;
                              }

                              var43.write(var45, 0, var46);
                           }
                        } catch (FileNotFoundException var36) {
                           this.notifyEvent(103);
                           throw new PrintException(var36.toString());
                        } catch (IOException var37) {
                           this.notifyEvent(103);
                           throw new PrintException(var37.toString());
                        }

                        this.notifyEvent(106);
                        this.notifyEvent(102);
                        this.service.wakeNotifier();
                     } else if (!this.startPrintRawData(this.service.getName(), this.jobName)) {
                        this.notifyEvent(103);
                        throw new PrintException("Print job failed to start.");
                     } else {
                        BufferedInputStream var42 = new BufferedInputStream(this.instream);
                        boolean var8 = false;

                        try {
                           byte[] var9 = new byte[8192];

                           int var44;
                           while((var44 = var42.read(var9, 0, 8192)) >= 0) {
                              if (!this.printRawData(var9, var44)) {
                                 var42.close();
                                 this.notifyEvent(103);
                                 throw new PrintException("Problem while spooling data");
                              }
                           }

                           var42.close();
                           if (!this.endPrintRawData()) {
                              this.notifyEvent(103);
                              throw new PrintException("Print job failed to close properly.");
                           }

                           this.notifyEvent(106);
                        } catch (IOException var38) {
                           this.notifyEvent(103);
                           throw new PrintException(var38.toString());
                        } finally {
                           this.notifyEvent(105);
                        }

                        this.service.wakeNotifier();
                     }
                  }
               } else {
                  try {
                     this.printableJob(new com.frojasg1.sun.print.ImagePrinter((java.net.URL)var5));
                     this.service.wakeNotifier();
                  } catch (ClassCastException var32) {
                     this.notifyEvent(103);
                     throw new PrintException(var32);
                  }
               }
            } else {
               try {
                  this.instream = var1.getStreamForBytes();
                  if (this.instream == null) {
                     this.notifyEvent(103);
                     throw new PrintException("No stream for data");
                  } else {
                     this.printableJob(new com.frojasg1.sun.print.ImagePrinter(this.instream));
                     this.service.wakeNotifier();
                  }
               } catch (ClassCastException var33) {
                  this.notifyEvent(103);
                  throw new PrintException(var33);
               } catch (IOException var34) {
                  this.notifyEvent(103);
                  throw new PrintException(var34);
               }
            }
         } else {
            this.notifyEvent(103);
            throw new com.frojasg1.sun.print.PrintJobFlavorException("invalid flavor", var41);
         }
      }
   }

   public void printableJob(Printable var1) throws PrintException {
      try {
         synchronized(this) {
            if (this.job != null) {
               throw new PrintException("already printing");
            }

            this.job = new WPrinterJob();
         }

         PrintService var2 = this.getPrintService();
         this.job.setPrintService(var2);
         if (this.copies == 0) {
            Copies var3 = (Copies)var2.getDefaultAttributeValue(Copies.class);
            this.copies = var3.getValue();
         }

         if (this.mediaName == null) {
            Object var12 = var2.getDefaultAttributeValue(Media.class);
            if (var12 instanceof MediaSizeName) {
               this.mediaName = (MediaSizeName)var12;
               this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
            }
         }

         if (this.orient == null) {
            this.orient = (OrientationRequested)var2.getDefaultAttributeValue(OrientationRequested.class);
         }

         this.job.setCopies(this.copies);
         this.job.setJobName(this.jobName);
         PageFormat var13 = new PageFormat();
         if (this.mediaSize != null) {
            Paper var4 = new Paper();
            var4.setSize((double)this.mediaSize.getX(25400) * 72.0D, (double)this.mediaSize.getY(25400) * 72.0D);
            var4.setImageableArea(72.0D, 72.0D, var4.getWidth() - 144.0D, var4.getHeight() - 144.0D);
            var13.setPaper(var4);
         }

         if (this.orient == OrientationRequested.REVERSE_LANDSCAPE) {
            var13.setOrientation(2);
         } else if (this.orient == OrientationRequested.LANDSCAPE) {
            var13.setOrientation(0);
         }

         this.job.setPrintable(var1, var13);
         this.job.print(this.reqAttrSet);
         this.notifyEvent(106);
      } catch (PrinterException var10) {
         this.notifyEvent(103);
         throw new PrintException(var10);
      } finally {
         this.printReturned = true;
         this.notifyEvent(105);
      }

   }

   public void pageableJob(Pageable var1) throws PrintException {
      try {
         synchronized(this) {
            if (this.job != null) {
               throw new PrintException("already printing");
            }

            this.job = new WPrinterJob();
         }

         PrintService var2 = this.getPrintService();
         this.job.setPrintService(var2);
         if (this.copies == 0) {
            Copies var3 = (Copies)var2.getDefaultAttributeValue(Copies.class);
            this.copies = var3.getValue();
         }

         this.job.setCopies(this.copies);
         this.job.setJobName(this.jobName);
         this.job.setPageable(var1);
         this.job.print(this.reqAttrSet);
         this.notifyEvent(106);
      } catch (PrinterException var9) {
         this.notifyEvent(103);
         throw new PrintException(var9);
      } finally {
         this.printReturned = true;
         this.notifyEvent(105);
      }

   }

   private synchronized void initializeAttributeSets(Doc var1, PrintRequestAttributeSet var2) {
      this.reqAttrSet = new HashPrintRequestAttributeSet();
      this.jobAttrSet = new HashPrintJobAttributeSet();
      Attribute[] var3;
      if (var2 != null) {
         this.reqAttrSet.addAll(var2);
         var3 = var2.toArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            if (var3[var4] instanceof PrintJobAttribute) {
               this.jobAttrSet.add(var3[var4]);
            }
         }
      }

      DocAttributeSet var11 = var1.getAttributes();
      if (var11 != null) {
         var3 = var11.toArray();

         for(int var5 = 0; var5 < var3.length; ++var5) {
            if (var3[var5] instanceof PrintRequestAttribute) {
               this.reqAttrSet.add(var3[var5]);
            }

            if (var3[var5] instanceof PrintJobAttribute) {
               this.jobAttrSet.add(var3[var5]);
            }
         }
      }

      String var12 = "";

      try {
         var12 = System.getProperty("user.name");
      } catch (SecurityException var10) {
      }

      if (var12 != null && !var12.equals("")) {
         this.jobAttrSet.add(new JobOriginatingUserName(var12, (Locale)null));
      } else {
         RequestingUserName var6 = (RequestingUserName)var2.get(RequestingUserName.class);
         if (var6 != null) {
            this.jobAttrSet.add(new JobOriginatingUserName(var6.getValue(), var6.getLocale()));
         } else {
            this.jobAttrSet.add(new JobOriginatingUserName("", (Locale)null));
         }
      }

      if (this.jobAttrSet.get(JobName.class) == null) {
         JobName var13;
         if (var11 != null && var11.get(DocumentName.class) != null) {
            DocumentName var14 = (DocumentName)var11.get(DocumentName.class);
            var13 = new JobName(var14.getValue(), var14.getLocale());
            this.jobAttrSet.add(var13);
         } else {
            String var7 = "JPS Job:" + var1;

            try {
               Object var8 = var1.getPrintData();
               if (var8 instanceof java.net.URL) {
                  var7 = ((java.net.URL)((java.net.URL)var1.getPrintData())).toString();
               }
            } catch (IOException var9) {
            }

            var13 = new JobName(var7, (Locale)null);
            this.jobAttrSet.add(var13);
         }
      }

      this.jobAttrSet = AttributeSetUtilities.unmodifiableView(this.jobAttrSet);
   }

   private void getAttributeValues(DocFlavor var1) throws PrintException {
      if (this.reqAttrSet.get(Fidelity.class) == Fidelity.FIDELITY_TRUE) {
         this.fidelity = true;
      } else {
         this.fidelity = false;
      }

      Attribute[] var3 = this.reqAttrSet.toArray();

      for(int var4 = 0; var4 < var3.length; ++var4) {
         Attribute var5 = var3[var4];
         Class var2 = var5.getCategory();
         if (this.fidelity) {
            if (!this.service.isAttributeCategorySupported(var2)) {
               this.notifyEvent(103);
               throw new com.frojasg1.sun.print.PrintJobAttributeException("unsupported category: " + var2, var2, (Attribute)null);
            }

            if (!this.service.isAttributeValueSupported(var5, var1, (AttributeSet)null)) {
               this.notifyEvent(103);
               throw new com.frojasg1.sun.print.PrintJobAttributeException("unsupported attribute: " + var5, (Class)null, var5);
            }
         }

         if (var2 == Destination.class) {
            URI var6 = ((Destination)var5).getURI();
            if (!"file".equals(var6.getScheme())) {
               this.notifyEvent(103);
               throw new PrintException("Not a file: URI");
            }

            try {
               this.mDestination = (new File(var6)).getPath();
            } catch (Exception var10) {
               throw new PrintException(var10);
            }

            SecurityManager var7 = System.getSecurityManager();
            if (var7 != null) {
               try {
                  var7.checkWrite(this.mDestination);
               } catch (SecurityException var9) {
                  this.notifyEvent(103);
                  throw new PrintException(var9);
               }
            }
         } else if (var2 == JobName.class) {
            this.jobName = ((JobName)var5).getValue();
         } else if (var2 == Copies.class) {
            this.copies = ((Copies)var5).getValue();
         } else if (var2 == Media.class) {
            if (var5 instanceof MediaSizeName) {
               this.mediaName = (MediaSizeName)var5;
               if (!this.service.isAttributeValueSupported(var5, (DocFlavor)null, (AttributeSet)null)) {
                  this.mediaSize = MediaSize.getMediaSizeForName(this.mediaName);
               }
            }
         } else if (var2 == OrientationRequested.class) {
            this.orient = (OrientationRequested)var5;
         }
      }

   }

   private native boolean startPrintRawData(String var1, String var2);

   private native boolean printRawData(byte[] var1, int var2);

   private native boolean endPrintRawData();

   public void cancel() throws PrintException {
      synchronized(this) {
         if (!this.printing) {
            throw new PrintException("Job is not yet submitted.");
         } else if (this.job != null && !this.printReturned) {
            this.job.cancel();
            this.notifyEvent(101);
         } else {
            throw new PrintException("Job could not be cancelled.");
         }
      }
   }
}
