package com.frojasg1.sun.print;

import java.awt.Window;
import java.awt.print.PrinterJob;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.ServiceUIFactory;
import javax.print.DocFlavor.BYTE_ARRAY;
import javax.print.DocFlavor.INPUT_STREAM;
import javax.print.DocFlavor.SERVICE_FORMATTED;
import javax.print.DocFlavor.URL;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.AttributeSetUtilities;
import javax.print.attribute.HashAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttribute;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.ColorSupported;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.CopiesSupported;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Fidelity;
import javax.print.attribute.standard.JobName;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterIsAcceptingJobs;
import javax.print.attribute.standard.PrinterName;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.PrinterState;
import javax.print.attribute.standard.PrinterStateReason;
import javax.print.attribute.standard.PrinterStateReasons;
import javax.print.attribute.standard.QueuedJobCount;
import javax.print.attribute.standard.RequestingUserName;
import javax.print.attribute.standard.Severity;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import javax.print.event.PrintServiceAttributeListener;
import com.frojasg1.sun.awt.windows.WPrinterJob;
import com.frojasg1.sun.print.AttributeUpdater;
import com.frojasg1.sun.print.DocumentPropertiesUI;
import com.frojasg1.sun.print.ServiceNotifier;
import com.frojasg1.sun.print.SunAlternateMedia;
import com.frojasg1.sun.print.SunPrinterJobService;
import com.frojasg1.sun.print.Win32MediaSize;
import com.frojasg1.sun.print.Win32MediaTray;
import com.frojasg1.sun.print.Win32PrintJob;

public class Win32PrintService implements PrintService, com.frojasg1.sun.print.AttributeUpdater, SunPrinterJobService {
   public static MediaSize[] predefMedia = com.frojasg1.sun.print.Win32MediaSize.getPredefMedia();
   private static final DocFlavor[] supportedFlavors;
   private static final Class[] serviceAttrCats;
   private static Class[] otherAttrCats;
   public static final MediaSizeName[] dmPaperToPrintService;
   private static final MediaTray[] dmPaperBinToPrintService;
   private static int DM_PAPERSIZE;
   private static int DM_PRINTQUALITY;
   private static int DM_YRESOLUTION;
   private static final int DMRES_MEDIUM = -3;
   private static final int DMRES_HIGH = -4;
   private static final int DMORIENT_LANDSCAPE = 2;
   private static final int DMDUP_VERTICAL = 2;
   private static final int DMDUP_HORIZONTAL = 3;
   private static final int DMCOLLATE_TRUE = 1;
   private static final int DMCOLOR_MONOCHROME = 1;
   private static final int DMCOLOR_COLOR = 2;
   private static final int DMPAPER_A2 = 66;
   private static final int DMPAPER_A6 = 70;
   private static final int DMPAPER_B6_JIS = 88;
   private static final int DEVCAP_COLOR = 1;
   private static final int DEVCAP_DUPLEX = 2;
   private static final int DEVCAP_COLLATE = 4;
   private static final int DEVCAP_QUALITY = 8;
   private static final int DEVCAP_POSTSCRIPT = 16;
   private String printer;
   private PrinterName name;
   private String port;
   private transient PrintServiceAttributeSet lastSet;
   private transient com.frojasg1.sun.print.ServiceNotifier notifier = null;
   private MediaSizeName[] mediaSizeNames;
   private MediaPrintableArea[] mediaPrintables;
   private MediaTray[] mediaTrays;
   private PrinterResolution[] printRes;
   private HashMap mpaMap;
   private int nCopies;
   private int prnCaps;
   private int[] defaultSettings;
   private boolean gotTrays;
   private boolean gotCopies;
   private boolean mediaInitialized;
   private boolean mpaListInitialized;
   private ArrayList idList;
   private MediaSize[] mediaSizes;
   private boolean isInvalid;
   private Win32PrintService.Win32DocumentPropertiesUI docPropertiesUI = null;
   private Win32PrintService.Win32ServiceUIFactory uiFactory = null;

   Win32PrintService(String var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("null printer name");
      } else {
         this.printer = var1;
         this.mediaInitialized = false;
         this.gotTrays = false;
         this.gotCopies = false;
         this.isInvalid = false;
         this.printRes = null;
         this.prnCaps = 0;
         this.defaultSettings = null;
         this.port = null;
      }
   }

   public void invalidateService() {
      this.isInvalid = true;
   }

   public String getName() {
      return this.printer;
   }

   private PrinterName getPrinterName() {
      if (this.name == null) {
         this.name = new PrinterName(this.printer, (Locale)null);
      }

      return this.name;
   }

   public int findPaperID(MediaSizeName var1) {
      if (var1 instanceof com.frojasg1.sun.print.Win32MediaSize) {
         com.frojasg1.sun.print.Win32MediaSize var3 = (com.frojasg1.sun.print.Win32MediaSize)var1;
         return var3.getDMPaper();
      } else {
         int var2;
         for(var2 = 0; var2 < dmPaperToPrintService.length; ++var2) {
            if (dmPaperToPrintService[var2].equals(var1)) {
               return var2 + 1;
            }
         }

         if (var1.equals(MediaSizeName.ISO_A2)) {
            return 66;
         } else if (var1.equals(MediaSizeName.ISO_A6)) {
            return 70;
         } else if (var1.equals(MediaSizeName.JIS_B6)) {
            return 88;
         } else {
            this.initMedia();
            if (this.idList != null && this.mediaSizes != null && this.idList.size() == this.mediaSizes.length) {
               for(var2 = 0; var2 < this.idList.size(); ++var2) {
                  if (this.mediaSizes[var2].getMediaSizeName() == var1) {
                     return (Integer)this.idList.get(var2);
                  }
               }
            }

            return 0;
         }
      }
   }

   public int findTrayID(MediaTray var1) {
      this.getMediaTrays();
      if (var1 instanceof com.frojasg1.sun.print.Win32MediaTray) {
         com.frojasg1.sun.print.Win32MediaTray var3 = (com.frojasg1.sun.print.Win32MediaTray)var1;
         return var3.getDMBinID();
      } else {
         for(int var2 = 0; var2 < dmPaperBinToPrintService.length; ++var2) {
            if (var1.equals(dmPaperBinToPrintService[var2])) {
               return var2 + 1;
            }
         }

         return 0;
      }
   }

   public MediaTray findMediaTray(int var1) {
      if (var1 >= 1 && var1 <= dmPaperBinToPrintService.length) {
         return dmPaperBinToPrintService[var1 - 1];
      } else {
         MediaTray[] var2 = this.getMediaTrays();
         if (var2 != null) {
            for(int var3 = 0; var3 < var2.length; ++var3) {
               if (var2[var3] instanceof com.frojasg1.sun.print.Win32MediaTray) {
                  com.frojasg1.sun.print.Win32MediaTray var4 = (com.frojasg1.sun.print.Win32MediaTray)var2[var3];
                  if (var4.winID == var1) {
                     return var4;
                  }
               }
            }
         }

         return com.frojasg1.sun.print.Win32MediaTray.AUTO;
      }
   }

   public MediaSizeName findWin32Media(int var1) {
      if (var1 >= 1 && var1 <= dmPaperToPrintService.length) {
         return dmPaperToPrintService[var1 - 1];
      } else {
         switch(var1) {
         case 66:
            return MediaSizeName.ISO_A2;
         case 70:
            return MediaSizeName.ISO_A6;
         case 88:
            return MediaSizeName.JIS_B6;
         default:
            return null;
         }
      }
   }

   private boolean addToUniqueList(ArrayList var1, MediaSizeName var2) {
      for(int var4 = 0; var4 < var1.size(); ++var4) {
         MediaSizeName var3 = (MediaSizeName)var1.get(var4);
         if (var3 == var2) {
            return false;
         }
      }

      var1.add(var2);
      return true;
   }

   private synchronized void initMedia() {
      if (!this.mediaInitialized) {
         this.mediaInitialized = true;
         int[] var1 = this.getAllMediaIDs(this.printer, this.getPort());
         if (var1 != null) {
            ArrayList var2 = new ArrayList();
            ArrayList var3 = new ArrayList();
            new ArrayList();
            boolean var7 = false;
            this.idList = new ArrayList();

            for(int var9 = 0; var9 < var1.length; ++var9) {
               this.idList.add(var1[var9]);
            }

            ArrayList var14 = new ArrayList();
            this.mediaSizes = this.getMediaSizes(this.idList, var1, var14);

            for(int var10 = 0; var10 < this.idList.size(); ++var10) {
               MediaSizeName var5 = this.findWin32Media((Integer)this.idList.get(var10));
               if (var5 != null && this.idList.size() == this.mediaSizes.length) {
                  MediaSize var11 = MediaSize.getMediaSizeForName(var5);
                  MediaSize var12 = this.mediaSizes[var10];
                  short var13 = 2540;
                  if (Math.abs(var11.getX(1) - var12.getX(1)) > (float)var13 || Math.abs(var11.getY(1) - var12.getY(1)) > (float)var13) {
                     var5 = null;
                  }
               }

               boolean var16 = var5 != null;
               if (var5 == null && this.idList.size() == this.mediaSizes.length) {
                  var5 = this.mediaSizes[var10].getMediaSizeName();
               }

               boolean var6 = false;
               if (var5 != null) {
                  var6 = this.addToUniqueList(var2, var5);
               }

               if ((!var16 || !var6) && this.idList.size() == var14.size()) {
                  com.frojasg1.sun.print.Win32MediaSize var18 = com.frojasg1.sun.print.Win32MediaSize.findMediaName((String)var14.get(var10));
                  if (var18 == null && this.idList.size() == this.mediaSizes.length) {
                     var18 = new com.frojasg1.sun.print.Win32MediaSize((String)var14.get(var10), (Integer)this.idList.get(var10));
                     this.mediaSizes[var10] = new MediaSize(this.mediaSizes[var10].getX(1000), this.mediaSizes[var10].getY(1000), 1000, var18);
                  }

                  if (var18 != null && var18 != var5) {
                     if (!var6) {
                        this.addToUniqueList(var2, var18);
                     } else {
                        var3.add(var18);
                     }
                  }
               }
            }

            Iterator var15 = var3.iterator();

            while(var15.hasNext()) {
               com.frojasg1.sun.print.Win32MediaSize var17 = (com.frojasg1.sun.print.Win32MediaSize)var15.next();
               this.addToUniqueList(var2, var17);
            }

            this.mediaSizeNames = new MediaSizeName[var2.size()];
            var2.toArray(this.mediaSizeNames);
         }
      }
   }

   private synchronized MediaPrintableArea[] getMediaPrintables(MediaSizeName var1) {
      if (var1 == null) {
         if (this.mpaListInitialized) {
            return this.mediaPrintables;
         }
      } else if (this.mpaMap != null && this.mpaMap.get(var1) != null) {
         MediaPrintableArea[] var12 = new MediaPrintableArea[]{(MediaPrintableArea)this.mpaMap.get(var1)};
         return var12;
      }

      this.initMedia();
      if (this.mediaSizeNames != null && this.mediaSizeNames.length != 0) {
         MediaSizeName[] var2;
         if (var1 != null) {
            var2 = new MediaSizeName[]{var1};
         } else {
            var2 = this.mediaSizeNames;
         }

         if (this.mpaMap == null) {
            this.mpaMap = new HashMap();
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            MediaSizeName var4 = var2[var3];
            if (this.mpaMap.get(var4) == null && var4 != null) {
               int var5 = this.findPaperID(var4);
               float[] var6 = var5 != 0 ? this.getMediaPrintableArea(this.printer, var5) : null;
               MediaPrintableArea var7 = null;
               if (var6 != null) {
                  try {
                     var7 = new MediaPrintableArea(var6[0], var6[1], var6[2], var6[3], 25400);
                     this.mpaMap.put(var4, var7);
                  } catch (IllegalArgumentException var10) {
                  }
               } else {
                  MediaSize var8 = MediaSize.getMediaSizeForName(var4);
                  if (var8 != null) {
                     try {
                        var7 = new MediaPrintableArea(0.0F, 0.0F, var8.getX(25400), var8.getY(25400), 25400);
                        this.mpaMap.put(var4, var7);
                     } catch (IllegalArgumentException var11) {
                     }
                  }
               }
            }
         }

         if (this.mpaMap.size() == 0) {
            return null;
         } else if (var1 != null) {
            if (this.mpaMap.get(var1) == null) {
               return null;
            } else {
               MediaPrintableArea[] var13 = new MediaPrintableArea[]{(MediaPrintableArea)this.mpaMap.get(var1)};
               return var13;
            }
         } else {
            this.mediaPrintables = (MediaPrintableArea[])((MediaPrintableArea[])this.mpaMap.values().toArray(new MediaPrintableArea[0]));
            this.mpaListInitialized = true;
            return this.mediaPrintables;
         }
      } else {
         return null;
      }
   }

   private synchronized MediaTray[] getMediaTrays() {
      if (this.gotTrays && this.mediaTrays != null) {
         return this.mediaTrays;
      } else {
         String var1 = this.getPort();
         int[] var2 = this.getAllMediaTrays(this.printer, var1);
         String[] var3 = this.getAllMediaTrayNames(this.printer, var1);
         if (var2 != null && var3 != null) {
            int var4 = 0;

            for(int var5 = 0; var5 < var2.length; ++var5) {
               if (var2[var5] > 0) {
                  ++var4;
               }
            }

            MediaTray[] var9 = new MediaTray[var4];
            int var7 = 0;

            for(int var8 = 0; var7 < Math.min(var2.length, var3.length); ++var7) {
               int var6 = var2[var7];
               if (var6 > 0) {
                  if (var6 <= dmPaperBinToPrintService.length && dmPaperBinToPrintService[var6 - 1] != null) {
                     var9[var8++] = dmPaperBinToPrintService[var6 - 1];
                  } else {
                     var9[var8++] = new com.frojasg1.sun.print.Win32MediaTray(var6, var3[var7]);
                  }
               }
            }

            this.mediaTrays = var9;
            this.gotTrays = true;
            return this.mediaTrays;
         } else {
            return null;
         }
      }
   }

   private boolean isSameSize(float var1, float var2, float var3, float var4) {
      float var5 = var1 - var3;
      float var6 = var2 - var4;
      float var7 = var1 - var4;
      float var8 = var2 - var3;
      return Math.abs(var5) <= 1.0F && Math.abs(var6) <= 1.0F || Math.abs(var7) <= 1.0F && Math.abs(var8) <= 1.0F;
   }

   public MediaSizeName findMatchingMediaSizeNameMM(float var1, float var2) {
      if (predefMedia != null) {
         for(int var3 = 0; var3 < predefMedia.length; ++var3) {
            if (predefMedia[var3] != null && this.isSameSize(predefMedia[var3].getX(1000), predefMedia[var3].getY(1000), var1, var2)) {
               return predefMedia[var3].getMediaSizeName();
            }
         }
      }

      return null;
   }

   private MediaSize[] getMediaSizes(ArrayList var1, int[] var2, ArrayList<String> var3) {
      if (var3 == null) {
         var3 = new ArrayList();
      }

      String var4 = this.getPort();
      int[] var5 = this.getAllMediaSizes(this.printer, var4);
      String[] var6 = this.getAllMediaNames(this.printer, var4);
      MediaSizeName var7 = null;
      MediaSize var8 = null;
      if (var5 != null && var6 != null) {
         int var11 = var5.length / 2;
         ArrayList var12 = new ArrayList();

         for(int var13 = 0; var13 < var11; var8 = null) {
            float var9 = (float)var5[var13 * 2] / 10.0F;
            float var10 = (float)var5[var13 * 2 + 1] / 10.0F;
            if (!(var9 <= 0.0F) && !(var10 <= 0.0F)) {
               var7 = this.findMatchingMediaSizeNameMM(var9, var10);
               if (var7 != null) {
                  var8 = MediaSize.getMediaSizeForName(var7);
               }

               if (var8 != null) {
                  var12.add(var8);
                  var3.add(var6[var13]);
               } else {
                  com.frojasg1.sun.print.Win32MediaSize var19 = com.frojasg1.sun.print.Win32MediaSize.findMediaName(var6[var13]);
                  if (var19 == null) {
                     var19 = new com.frojasg1.sun.print.Win32MediaSize(var6[var13], var2[var13]);
                  }

                  try {
                     var8 = new MediaSize(var9, var10, 1000, var19);
                     var12.add(var8);
                     var3.add(var6[var13]);
                  } catch (IllegalArgumentException var17) {
                     if (var11 == var2.length) {
                        Integer var16 = var2[var13];
                        var1.remove(var1.indexOf(var16));
                     }
                  }
               }
            } else if (var11 == var2.length) {
               Integer var14 = var2[var13];
               var1.remove(var1.indexOf(var14));
            }

            ++var13;
         }

         MediaSize[] var18 = new MediaSize[var12.size()];
         var12.toArray(var18);
         return var18;
      } else {
         return null;
      }
   }

   private PrinterIsAcceptingJobs getPrinterIsAcceptingJobs() {
      return this.getJobStatus(this.printer, 2) != 1 ? PrinterIsAcceptingJobs.NOT_ACCEPTING_JOBS : PrinterIsAcceptingJobs.ACCEPTING_JOBS;
   }

   private PrinterState getPrinterState() {
      return this.isInvalid ? PrinterState.STOPPED : null;
   }

   private PrinterStateReasons getPrinterStateReasons() {
      if (this.isInvalid) {
         PrinterStateReasons var1 = new PrinterStateReasons();
         var1.put(PrinterStateReason.SHUTDOWN, Severity.ERROR);
         return var1;
      } else {
         return null;
      }
   }

   private QueuedJobCount getQueuedJobCount() {
      int var1 = this.getJobStatus(this.printer, 1);
      return var1 != -1 ? new QueuedJobCount(var1) : new QueuedJobCount(0);
   }

   private boolean isSupportedCopies(Copies var1) {
      synchronized(this) {
         if (!this.gotCopies) {
            this.nCopies = this.getCopiesSupported(this.printer, this.getPort());
            this.gotCopies = true;
         }
      }

      int var2 = var1.getValue();
      return var2 > 0 && var2 <= this.nCopies;
   }

   private boolean isSupportedMedia(MediaSizeName var1) {
      this.initMedia();
      if (this.mediaSizeNames != null) {
         for(int var2 = 0; var2 < this.mediaSizeNames.length; ++var2) {
            if (var1.equals(this.mediaSizeNames[var2])) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isSupportedMediaPrintableArea(MediaPrintableArea var1) {
      this.getMediaPrintables((MediaSizeName)null);
      if (this.mediaPrintables != null) {
         for(int var2 = 0; var2 < this.mediaPrintables.length; ++var2) {
            if (var1.equals(this.mediaPrintables[var2])) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isSupportedMediaTray(MediaTray var1) {
      MediaTray[] var2 = this.getMediaTrays();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.equals(var2[var3])) {
               return true;
            }
         }
      }

      return false;
   }

   private int getPrinterCapabilities() {
      if (this.prnCaps == 0) {
         this.prnCaps = this.getCapabilities(this.printer, this.getPort());
      }

      return this.prnCaps;
   }

   private String getPort() {
      if (this.port == null) {
         this.port = this.getPrinterPort(this.printer);
      }

      return this.port;
   }

   private int[] getDefaultPrinterSettings() {
      if (this.defaultSettings == null) {
         this.defaultSettings = this.getDefaultSettings(this.printer, this.getPort());
      }

      return this.defaultSettings;
   }

   private PrinterResolution[] getPrintResolutions() {
      if (this.printRes == null) {
         int[] var1 = this.getAllResolutions(this.printer, this.getPort());
         if (var1 == null) {
            this.printRes = new PrinterResolution[0];
         } else {
            int var2 = var1.length / 2;
            ArrayList var3 = new ArrayList();

            for(int var5 = 0; var5 < var2; ++var5) {
               try {
                  PrinterResolution var4 = new PrinterResolution(var1[var5 * 2], var1[var5 * 2 + 1], 100);
                  var3.add(var4);
               } catch (IllegalArgumentException var7) {
               }
            }

            this.printRes = (PrinterResolution[])((PrinterResolution[])var3.toArray(new PrinterResolution[var3.size()]));
         }
      }

      return this.printRes;
   }

   private boolean isSupportedResolution(PrinterResolution var1) {
      PrinterResolution[] var2 = this.getPrintResolutions();
      if (var2 != null) {
         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.equals(var2[var3])) {
               return true;
            }
         }
      }

      return false;
   }

   public DocPrintJob createPrintJob() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         var1.checkPrintJobAccess();
      }

      return new Win32PrintJob(this);
   }

   private PrintServiceAttributeSet getDynamicAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(this.getPrinterIsAcceptingJobs());
      var1.add(this.getQueuedJobCount());
      return var1;
   }

   public PrintServiceAttributeSet getUpdatedAttributes() {
      PrintServiceAttributeSet var1 = this.getDynamicAttributes();
      if (this.lastSet == null) {
         this.lastSet = var1;
         return AttributeSetUtilities.unmodifiableView(var1);
      } else {
         HashPrintServiceAttributeSet var2 = new HashPrintServiceAttributeSet();
         Attribute[] var3 = var1.toArray();

         for(int var4 = 0; var4 < var3.length; ++var4) {
            Attribute var5 = var3[var4];
            if (!this.lastSet.containsValue(var5)) {
               var2.add(var5);
            }
         }

         this.lastSet = var1;
         return AttributeSetUtilities.unmodifiableView(var2);
      }
   }

   public void wakeNotifier() {
      synchronized(this) {
         if (this.notifier != null) {
            this.notifier.wake();
         }

      }
   }

   public void addPrintServiceAttributeListener(PrintServiceAttributeListener var1) {
      synchronized(this) {
         if (var1 != null) {
            if (this.notifier == null) {
               this.notifier = new com.frojasg1.sun.print.ServiceNotifier(this);
            }

            this.notifier.addListener(var1);
         }
      }
   }

   public void removePrintServiceAttributeListener(PrintServiceAttributeListener var1) {
      synchronized(this) {
         if (var1 != null && this.notifier != null) {
            this.notifier.removeListener(var1);
            if (this.notifier.isEmpty()) {
               this.notifier.stopNotifier();
               this.notifier = null;
            }

         }
      }
   }

   public <T extends PrintServiceAttribute> T getAttribute(Class<T> var1) {
      if (var1 == null) {
         throw new NullPointerException("category");
      } else if (!PrintServiceAttribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException("Not a PrintServiceAttribute");
      } else if (var1 == ColorSupported.class) {
         int var2 = this.getPrinterCapabilities();
         return (var2 & 1) != 0 ? (T) ColorSupported.SUPPORTED : (T) ColorSupported.NOT_SUPPORTED;
      } else if (var1 == PrinterName.class) {
         return (T) this.getPrinterName();
      } else if (var1 == PrinterState.class) {
         return (T) this.getPrinterState();
      } else if (var1 == PrinterStateReasons.class) {
         return (T) this.getPrinterStateReasons();
      } else if (var1 == QueuedJobCount.class) {
         return (T) this.getQueuedJobCount();
      } else {
         return var1 == PrinterIsAcceptingJobs.class ? (T) this.getPrinterIsAcceptingJobs() : null;
      }
   }

   public PrintServiceAttributeSet getAttributes() {
      HashPrintServiceAttributeSet var1 = new HashPrintServiceAttributeSet();
      var1.add(this.getPrinterName());
      var1.add(this.getPrinterIsAcceptingJobs());
      PrinterState var2 = this.getPrinterState();
      if (var2 != null) {
         var1.add(var2);
      }

      PrinterStateReasons var3 = this.getPrinterStateReasons();
      if (var3 != null) {
         var1.add(var3);
      }

      var1.add(this.getQueuedJobCount());
      int var4 = this.getPrinterCapabilities();
      if ((var4 & 1) != 0) {
         var1.add(ColorSupported.SUPPORTED);
      } else {
         var1.add(ColorSupported.NOT_SUPPORTED);
      }

      return AttributeSetUtilities.unmodifiableView(var1);
   }

   public DocFlavor[] getSupportedDocFlavors() {
      int var1 = supportedFlavors.length;
      int var3 = this.getPrinterCapabilities();
      DocFlavor[] var2;
      if ((var3 & 16) != 0) {
         var2 = new DocFlavor[var1 + 3];
         System.arraycopy(supportedFlavors, 0, var2, 0, var1);
         var2[var1] = BYTE_ARRAY.POSTSCRIPT;
         var2[var1 + 1] = INPUT_STREAM.POSTSCRIPT;
         var2[var1 + 2] = URL.POSTSCRIPT;
      } else {
         var2 = new DocFlavor[var1];
         System.arraycopy(supportedFlavors, 0, var2, 0, var1);
      }

      return var2;
   }

   public boolean isDocFlavorSupported(DocFlavor var1) {
      DocFlavor[] var2;
      if (this.isPostScriptFlavor(var1)) {
         var2 = this.getSupportedDocFlavors();
      } else {
         var2 = supportedFlavors;
      }

      for(int var3 = 0; var3 < var2.length; ++var3) {
         if (var1.equals(var2[var3])) {
            return true;
         }
      }

      return false;
   }

   public Class<?>[] getSupportedAttributeCategories() {
      ArrayList var1 = new ArrayList(otherAttrCats.length + 3);

      int var2;
      for(var2 = 0; var2 < otherAttrCats.length; ++var2) {
         var1.add(otherAttrCats[var2]);
      }

      var2 = this.getPrinterCapabilities();
      if ((var2 & 2) != 0) {
         var1.add(Sides.class);
      }

      if ((var2 & 8) != 0) {
         int[] var3 = this.getDefaultPrinterSettings();
         if (var3[3] >= -4 && var3[3] < 0) {
            var1.add(PrintQuality.class);
         }
      }

      PrinterResolution[] var4 = this.getPrintResolutions();
      if (var4 != null && var4.length > 0) {
         var1.add(PrinterResolution.class);
      }

      return (Class[])((Class[])var1.toArray(new Class[var1.size()]));
   }

   public boolean isAttributeCategorySupported(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else {
         Class[] var2 = this.getSupportedAttributeCategories();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            if (var1.equals(var2[var3])) {
               return true;
            }
         }

         return false;
      }
   }

   public Object getDefaultAttributeValue(Class<? extends Attribute> var1) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " is not an Attribute");
      } else if (!this.isAttributeCategorySupported(var1)) {
         return null;
      } else {
         int[] var2 = this.getDefaultPrinterSettings();
         int var3 = var2[0];
         int var4 = var2[2];
         int var5 = var2[3];
         int var6 = var2[4];
         int var7 = var2[5];
         int var8 = var2[6];
         int var9 = var2[7];
         int var10 = var2[8];
         if (var1 == Copies.class) {
            return var6 > 0 ? new Copies(var6) : new Copies(1);
         } else if (var1 == Chromaticity.class) {
            return var10 == 2 ? Chromaticity.COLOR : Chromaticity.MONOCHROME;
         } else if (var1 == JobName.class) {
            return new JobName("Java Printing", (Locale)null);
         } else if (var1 == OrientationRequested.class) {
            return var7 == 2 ? OrientationRequested.LANDSCAPE : OrientationRequested.PORTRAIT;
         } else if (var1 == PageRanges.class) {
            return new PageRanges(1, 2147483647);
         } else {
            MediaSizeName var11;
            int var13;
            if (var1 == Media.class) {
               var11 = this.findWin32Media(var3);
               if (var11 != null) {
                  if (!this.isSupportedMedia(var11) && this.mediaSizeNames != null) {
                     var11 = this.mediaSizeNames[0];
                     this.findPaperID(var11);
                  }

                  return var11;
               }

               this.initMedia();
               if (this.mediaSizeNames != null && this.mediaSizeNames.length > 0) {
                  if (this.idList != null && this.mediaSizes != null && this.idList.size() == this.mediaSizes.length) {
                     Integer var12 = var3;
                     var13 = this.idList.indexOf(var12);
                     if (var13 >= 0 && var13 < this.mediaSizes.length) {
                        return this.mediaSizes[var13].getMediaSizeName();
                     }
                  }

                  return this.mediaSizeNames[0];
               }
            } else {
               if (var1 == MediaPrintableArea.class) {
                  var11 = this.findWin32Media(var3);
                  if (var11 != null && !this.isSupportedMedia(var11) && this.mediaSizeNames != null) {
                     var3 = this.findPaperID(this.mediaSizeNames[0]);
                  }

                  float[] var20 = this.getMediaPrintableArea(this.printer, var3);
                  if (var20 != null) {
                     MediaPrintableArea var22 = null;

                     try {
                        var22 = new MediaPrintableArea(var20[0], var20[1], var20[2], var20[3], 25400);
                     } catch (IllegalArgumentException var16) {
                     }

                     return var22;
                  }

                  return null;
               }

               if (var1 == com.frojasg1.sun.print.SunAlternateMedia.class) {
                  return null;
               }

               if (var1 == Destination.class) {
                  try {
                     return new Destination((new File("out.prn")).toURI());
                  } catch (SecurityException var17) {
                     try {
                        return new Destination(new URI("file:out.prn"));
                     } catch (URISyntaxException var15) {
                        return null;
                     }
                  }
               }

               if (var1 == Sides.class) {
                  switch(var8) {
                  case 2:
                     return Sides.TWO_SIDED_LONG_EDGE;
                  case 3:
                     return Sides.TWO_SIDED_SHORT_EDGE;
                  default:
                     return Sides.ONE_SIDED;
                  }
               }

               if (var1 == PrinterResolution.class) {
                  if (var5 >= 0 && var4 >= 0) {
                     return new PrinterResolution(var5, var4, 100);
                  }

                  var13 = var4 > var5 ? var4 : var5;
                  if (var13 > 0) {
                     return new PrinterResolution(var13, var13, 100);
                  }
               } else {
                  if (var1 == ColorSupported.class) {
                     int var21 = this.getPrinterCapabilities();
                     if ((var21 & 1) != 0) {
                        return ColorSupported.SUPPORTED;
                     }

                     return ColorSupported.NOT_SUPPORTED;
                  }

                  if (var1 == PrintQuality.class) {
                     if (var5 < 0 && var5 >= -4) {
                        switch(var5) {
                        case -4:
                           return PrintQuality.HIGH;
                        case -3:
                           return PrintQuality.NORMAL;
                        default:
                           return PrintQuality.DRAFT;
                        }
                     }
                  } else {
                     if (var1 == RequestingUserName.class) {
                        String var19 = "";

                        try {
                           var19 = System.getProperty("user.name", "");
                        } catch (SecurityException var18) {
                        }

                        return new RequestingUserName(var19, (Locale)null);
                     }

                     if (var1 == SheetCollate.class) {
                        if (var9 == 1) {
                           return SheetCollate.COLLATED;
                        }

                        return SheetCollate.UNCOLLATED;
                     }

                     if (var1 == Fidelity.class) {
                        return Fidelity.FIDELITY_FALSE;
                     }
                  }
               }
            }

            return null;
         }
      }
   }

   private boolean isPostScriptFlavor(DocFlavor var1) {
      return var1.equals(BYTE_ARRAY.POSTSCRIPT) || var1.equals(INPUT_STREAM.POSTSCRIPT) || var1.equals(URL.POSTSCRIPT);
   }

   private boolean isPSDocAttr(Class var1) {
      return var1 == OrientationRequested.class || var1 == Copies.class;
   }

   private boolean isAutoSense(DocFlavor var1) {
      return var1.equals(BYTE_ARRAY.AUTOSENSE) || var1.equals(INPUT_STREAM.AUTOSENSE) || var1.equals(URL.AUTOSENSE);
   }

   public Object getSupportedAttributeValues(Class<? extends Attribute> var1, DocFlavor var2, AttributeSet var3) {
      if (var1 == null) {
         throw new NullPointerException("null category");
      } else if (!Attribute.class.isAssignableFrom(var1)) {
         throw new IllegalArgumentException(var1 + " does not implement Attribute");
      } else {
         if (var2 != null) {
            if (!this.isDocFlavorSupported(var2)) {
               throw new IllegalArgumentException(var2 + " is an unsupported flavor");
            }

            if (this.isAutoSense(var2) || this.isPostScriptFlavor(var2) && this.isPSDocAttr(var1)) {
               return null;
            }
         }

         if (!this.isAttributeCategorySupported(var1)) {
            return null;
         } else if (var1 == JobName.class) {
            return new JobName("Java Printing", (Locale)null);
         } else if (var1 == RequestingUserName.class) {
            String var23 = "";

            try {
               var23 = System.getProperty("user.name", "");
            } catch (SecurityException var8) {
            }

            return new RequestingUserName(var23, (Locale)null);
         } else {
            int var21;
            if (var1 == ColorSupported.class) {
               var21 = this.getPrinterCapabilities();
               if ((var21 & 1) != 0) {
                  return ColorSupported.SUPPORTED;
               } else {
                  return ColorSupported.NOT_SUPPORTED;
               }
            } else if (var1 == Chromaticity.class) {
               if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE) && !var2.equals(BYTE_ARRAY.GIF) && !var2.equals(INPUT_STREAM.GIF) && !var2.equals(URL.GIF) && !var2.equals(BYTE_ARRAY.JPEG) && !var2.equals(INPUT_STREAM.JPEG) && !var2.equals(URL.JPEG) && !var2.equals(BYTE_ARRAY.PNG) && !var2.equals(INPUT_STREAM.PNG) && !var2.equals(URL.PNG)) {
                  return null;
               } else {
                  var21 = this.getPrinterCapabilities();
                  Chromaticity[] var18;
                  if ((var21 & 1) == 0) {
                     var18 = new Chromaticity[]{Chromaticity.MONOCHROME};
                     return var18;
                  } else {
                     var18 = new Chromaticity[]{Chromaticity.MONOCHROME, Chromaticity.COLOR};
                     return var18;
                  }
               }
            } else if (var1 == Destination.class) {
               try {
                  return new Destination((new File("out.prn")).toURI());
               } catch (SecurityException var9) {
                  try {
                     return new Destination(new URI("file:out.prn"));
                  } catch (URISyntaxException var7) {
                     return null;
                  }
               }
            } else if (var1 == OrientationRequested.class) {
               if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE) && !var2.equals(INPUT_STREAM.GIF) && !var2.equals(INPUT_STREAM.JPEG) && !var2.equals(INPUT_STREAM.PNG) && !var2.equals(BYTE_ARRAY.GIF) && !var2.equals(BYTE_ARRAY.JPEG) && !var2.equals(BYTE_ARRAY.PNG) && !var2.equals(URL.GIF) && !var2.equals(URL.JPEG) && !var2.equals(URL.PNG)) {
                  return null;
               } else {
                  OrientationRequested[] var22 = new OrientationRequested[]{OrientationRequested.PORTRAIT, OrientationRequested.LANDSCAPE, OrientationRequested.REVERSE_LANDSCAPE};
                  return var22;
               }
            } else if (var1 != Copies.class && var1 != CopiesSupported.class) {
               if (var1 == Media.class) {
                  this.initMedia();
                  var21 = this.mediaSizeNames == null ? 0 : this.mediaSizeNames.length;
                  MediaTray[] var16 = this.getMediaTrays();
                  var21 += var16 == null ? 0 : var16.length;
                  Media[] var20 = new Media[var21];
                  if (this.mediaSizeNames != null) {
                     System.arraycopy(this.mediaSizeNames, 0, var20, 0, this.mediaSizeNames.length);
                  }

                  if (var16 != null) {
                     System.arraycopy(var16, 0, var20, var21 - var16.length, var16.length);
                  }

                  return var20;
               } else if (var1 == MediaPrintableArea.class) {
                  Media var19 = null;
                  if (var3 != null && (var19 = (Media)var3.get(Media.class)) != null && !(var19 instanceof MediaSizeName)) {
                     var19 = null;
                  }

                  MediaPrintableArea[] var14 = this.getMediaPrintables((MediaSizeName)var19);
                  if (var14 != null) {
                     MediaPrintableArea[] var6 = new MediaPrintableArea[var14.length];
                     System.arraycopy(var14, 0, var6, 0, var14.length);
                     return var6;
                  } else {
                     return null;
                  }
               } else if (var1 == com.frojasg1.sun.print.SunAlternateMedia.class) {
                  return new com.frojasg1.sun.print.SunAlternateMedia((Media)this.getDefaultAttributeValue(Media.class));
               } else if (var1 == PageRanges.class) {
                  if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE)) {
                     return null;
                  } else {
                     PageRanges[] var17 = new PageRanges[]{new PageRanges(1, 2147483647)};
                     return var17;
                  }
               } else if (var1 == PrinterResolution.class) {
                  PrinterResolution[] var15 = this.getPrintResolutions();
                  if (var15 == null) {
                     return null;
                  } else {
                     PrinterResolution[] var5 = new PrinterResolution[var15.length];
                     System.arraycopy(var15, 0, var5, 0, var15.length);
                     return var5;
                  }
               } else if (var1 == Sides.class) {
                  if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE)) {
                     return null;
                  } else {
                     Sides[] var13 = new Sides[]{Sides.ONE_SIDED, Sides.TWO_SIDED_LONG_EDGE, Sides.TWO_SIDED_SHORT_EDGE};
                     return var13;
                  }
               } else if (var1 == PrintQuality.class) {
                  PrintQuality[] var12 = new PrintQuality[]{PrintQuality.DRAFT, PrintQuality.HIGH, PrintQuality.NORMAL};
                  return var12;
               } else if (var1 == SheetCollate.class) {
                  if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE)) {
                     return null;
                  } else {
                     SheetCollate[] var11 = new SheetCollate[]{SheetCollate.COLLATED, SheetCollate.UNCOLLATED};
                     return var11;
                  }
               } else if (var1 == Fidelity.class) {
                  Fidelity[] var4 = new Fidelity[]{Fidelity.FIDELITY_FALSE, Fidelity.FIDELITY_TRUE};
                  return var4;
               } else {
                  return null;
               }
            } else {
               synchronized(this) {
                  if (!this.gotCopies) {
                     this.nCopies = this.getCopiesSupported(this.printer, this.getPort());
                     this.gotCopies = true;
                  }
               }

               return new CopiesSupported(1, this.nCopies);
            }
         }
      }
   }

   public boolean isAttributeValueSupported(Attribute var1, DocFlavor var2, AttributeSet var3) {
      if (var1 == null) {
         throw new NullPointerException("null attribute");
      } else {
         Class var4 = var1.getCategory();
         if (var2 != null) {
            if (!this.isDocFlavorSupported(var2)) {
               throw new IllegalArgumentException(var2 + " is an unsupported flavor");
            }

            if (this.isAutoSense(var2) || this.isPostScriptFlavor(var2) && this.isPSDocAttr(var4)) {
               return false;
            }
         }

         if (!this.isAttributeCategorySupported(var4)) {
            return false;
         } else {
            int var5;
            if (var4 == Chromaticity.class) {
               if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE) && !var2.equals(BYTE_ARRAY.GIF) && !var2.equals(INPUT_STREAM.GIF) && !var2.equals(URL.GIF) && !var2.equals(BYTE_ARRAY.JPEG) && !var2.equals(INPUT_STREAM.JPEG) && !var2.equals(URL.JPEG) && !var2.equals(BYTE_ARRAY.PNG) && !var2.equals(INPUT_STREAM.PNG) && !var2.equals(URL.PNG)) {
                  return false;
               } else {
                  var5 = this.getPrinterCapabilities();
                  if ((var5 & 1) != 0) {
                     return true;
                  } else {
                     return var1 == Chromaticity.MONOCHROME;
                  }
               }
            } else if (var4 == Copies.class) {
               return this.isSupportedCopies((Copies)var1);
            } else if (var4 == Destination.class) {
               URI var8 = ((Destination)var1).getURI();
               if ("file".equals(var8.getScheme()) && !var8.getSchemeSpecificPart().equals("")) {
                  return true;
               } else {
                  return false;
               }
            } else {
               if (var4 == Media.class) {
                  if (var1 instanceof MediaSizeName) {
                     return this.isSupportedMedia((MediaSizeName)var1);
                  }

                  if (var1 instanceof MediaTray) {
                     return this.isSupportedMediaTray((MediaTray)var1);
                  }
               } else {
                  if (var4 == MediaPrintableArea.class) {
                     return this.isSupportedMediaPrintableArea((MediaPrintableArea)var1);
                  }

                  if (var4 == com.frojasg1.sun.print.SunAlternateMedia.class) {
                     Media var7 = ((com.frojasg1.sun.print.SunAlternateMedia)var1).getMedia();
                     return this.isAttributeValueSupported(var7, var2, var3);
                  }

                  if (var4 != PageRanges.class && var4 != SheetCollate.class && var4 != Sides.class) {
                     if (var4 == PrinterResolution.class) {
                        if (var1 instanceof PrinterResolution) {
                           return this.isSupportedResolution((PrinterResolution)var1);
                        }
                     } else if (var4 == OrientationRequested.class) {
                        if (var1 == OrientationRequested.REVERSE_PORTRAIT || var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE) && !var2.equals(INPUT_STREAM.GIF) && !var2.equals(INPUT_STREAM.JPEG) && !var2.equals(INPUT_STREAM.PNG) && !var2.equals(BYTE_ARRAY.GIF) && !var2.equals(BYTE_ARRAY.JPEG) && !var2.equals(BYTE_ARRAY.PNG) && !var2.equals(URL.GIF) && !var2.equals(URL.JPEG) && !var2.equals(URL.PNG)) {
                           return false;
                        }
                     } else if (var4 == ColorSupported.class) {
                        var5 = this.getPrinterCapabilities();
                        boolean var6 = (var5 & 1) != 0;
                        if (!var6 && var1 == ColorSupported.SUPPORTED || var6 && var1 == ColorSupported.NOT_SUPPORTED) {
                           return false;
                        }
                     }
                  } else if (var2 != null && !var2.equals(SERVICE_FORMATTED.PAGEABLE) && !var2.equals(SERVICE_FORMATTED.PRINTABLE)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   public AttributeSet getUnsupportedAttributes(DocFlavor var1, AttributeSet var2) {
      if (var1 != null && !this.isDocFlavorSupported(var1)) {
         throw new IllegalArgumentException("flavor " + var1 + "is not supported");
      } else if (var2 == null) {
         return null;
      } else {
         HashAttributeSet var4 = new HashAttributeSet();
         Attribute[] var5 = var2.toArray();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            try {
               Attribute var3 = var5[var6];
               if (!this.isAttributeCategorySupported(var3.getCategory())) {
                  var4.add(var3);
               } else if (!this.isAttributeValueSupported(var3, var1, var2)) {
                  var4.add(var3);
               }
            } catch (ClassCastException var8) {
            }
         }

         if (var4.isEmpty()) {
            return null;
         } else {
            return var4;
         }
      }
   }

   private synchronized com.frojasg1.sun.print.DocumentPropertiesUI getDocumentPropertiesUI() {
      return new Win32PrintService.Win32DocumentPropertiesUI(this);
   }

   public synchronized ServiceUIFactory getServiceUIFactory() {
      if (this.uiFactory == null) {
         this.uiFactory = new Win32PrintService.Win32ServiceUIFactory(this);
      }

      return this.uiFactory;
   }

   public String toString() {
      return "Win32 Printer : " + this.getName();
   }

   public boolean equals(Object var1) {
      return var1 == this || var1 instanceof Win32PrintService && ((Win32PrintService)var1).getName().equals(this.getName());
   }

   public int hashCode() {
      return this.getClass().hashCode() + this.getName().hashCode();
   }

   public boolean usesClass(Class var1) {
      return var1 == WPrinterJob.class;
   }

   private native int[] getAllMediaIDs(String var1, String var2);

   private native int[] getAllMediaSizes(String var1, String var2);

   private native int[] getAllMediaTrays(String var1, String var2);

   private native float[] getMediaPrintableArea(String var1, int var2);

   private native String[] getAllMediaNames(String var1, String var2);

   private native String[] getAllMediaTrayNames(String var1, String var2);

   private native int getCopiesSupported(String var1, String var2);

   private native int[] getAllResolutions(String var1, String var2);

   private native int getCapabilities(String var1, String var2);

   private native int[] getDefaultSettings(String var1, String var2);

   private native int getJobStatus(String var1, int var2);

   private native String getPrinterPort(String var1);

   static {
      supportedFlavors = new DocFlavor[]{BYTE_ARRAY.GIF, INPUT_STREAM.GIF, URL.GIF, BYTE_ARRAY.JPEG, INPUT_STREAM.JPEG, URL.JPEG, BYTE_ARRAY.PNG, INPUT_STREAM.PNG, URL.PNG, SERVICE_FORMATTED.PAGEABLE, SERVICE_FORMATTED.PRINTABLE, BYTE_ARRAY.AUTOSENSE, URL.AUTOSENSE, INPUT_STREAM.AUTOSENSE};
      serviceAttrCats = new Class[]{PrinterName.class, PrinterIsAcceptingJobs.class, QueuedJobCount.class, ColorSupported.class};
      otherAttrCats = new Class[]{JobName.class, RequestingUserName.class, Copies.class, Destination.class, OrientationRequested.class, PageRanges.class, Media.class, MediaPrintableArea.class, Fidelity.class, SheetCollate.class, SunAlternateMedia.class, Chromaticity.class};
      dmPaperToPrintService = new MediaSizeName[]{MediaSizeName.NA_LETTER, MediaSizeName.NA_LETTER, MediaSizeName.TABLOID, MediaSizeName.LEDGER, MediaSizeName.NA_LEGAL, MediaSizeName.INVOICE, MediaSizeName.EXECUTIVE, MediaSizeName.ISO_A3, MediaSizeName.ISO_A4, MediaSizeName.ISO_A4, MediaSizeName.ISO_A5, MediaSizeName.JIS_B4, MediaSizeName.JIS_B5, MediaSizeName.FOLIO, MediaSizeName.QUARTO, MediaSizeName.NA_10X14_ENVELOPE, MediaSizeName.B, MediaSizeName.NA_LETTER, MediaSizeName.NA_NUMBER_9_ENVELOPE, MediaSizeName.NA_NUMBER_10_ENVELOPE, MediaSizeName.NA_NUMBER_11_ENVELOPE, MediaSizeName.NA_NUMBER_12_ENVELOPE, MediaSizeName.NA_NUMBER_14_ENVELOPE, MediaSizeName.C, MediaSizeName.D, MediaSizeName.E, MediaSizeName.ISO_DESIGNATED_LONG, MediaSizeName.ISO_C5, MediaSizeName.ISO_C3, MediaSizeName.ISO_C4, MediaSizeName.ISO_C6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.ISO_B4, MediaSizeName.ISO_B5, MediaSizeName.ISO_B6, MediaSizeName.ITALY_ENVELOPE, MediaSizeName.MONARCH_ENVELOPE, MediaSizeName.PERSONAL_ENVELOPE, MediaSizeName.NA_10X15_ENVELOPE, MediaSizeName.NA_9X12_ENVELOPE, MediaSizeName.FOLIO, MediaSizeName.ISO_B4, MediaSizeName.JAPANESE_POSTCARD, MediaSizeName.NA_9X11_ENVELOPE};
      dmPaperBinToPrintService = new MediaTray[]{MediaTray.TOP, MediaTray.BOTTOM, MediaTray.MIDDLE, MediaTray.MANUAL, MediaTray.ENVELOPE, com.frojasg1.sun.print.Win32MediaTray.ENVELOPE_MANUAL, com.frojasg1.sun.print.Win32MediaTray.AUTO, com.frojasg1.sun.print.Win32MediaTray.TRACTOR, com.frojasg1.sun.print.Win32MediaTray.SMALL_FORMAT, com.frojasg1.sun.print.Win32MediaTray.LARGE_FORMAT, MediaTray.LARGE_CAPACITY, null, null, MediaTray.MAIN, Win32MediaTray.FORMSOURCE};
      DM_PAPERSIZE = 2;
      DM_PRINTQUALITY = 1024;
      DM_YRESOLUTION = 8192;
   }

   private static class Win32DocumentPropertiesUI extends com.frojasg1.sun.print.DocumentPropertiesUI {
      Win32PrintService service;

      private Win32DocumentPropertiesUI(Win32PrintService var1) {
         this.service = var1;
      }

      public PrintRequestAttributeSet showDocumentProperties(PrinterJob var1, Window var2, PrintService var3, PrintRequestAttributeSet var4) {
         if (!(var1 instanceof WPrinterJob)) {
            return null;
         } else {
            WPrinterJob var5 = (WPrinterJob)var1;
            return var5.showDocumentProperties(var2, var3, var4);
         }
      }
   }

   private static class Win32ServiceUIFactory extends ServiceUIFactory {
      Win32PrintService service;

      Win32ServiceUIFactory(Win32PrintService var1) {
         this.service = var1;
      }

      public Object getUI(int var1, String var2) {
         if (var1 <= 3) {
            return null;
         } else if (var1 == 199 && com.frojasg1.sun.print.DocumentPropertiesUI.DOCPROPERTIESCLASSNAME.equals(var2)) {
            return this.service.getDocumentPropertiesUI();
         } else {
            throw new IllegalArgumentException("Unsupported role");
         }
      }

      public String[] getUIClassNamesForRole(int var1) {
         if (var1 <= 3) {
            return null;
         } else if (var1 == 199) {
            String[] var2 = new String[0];
            var2[0] = DocumentPropertiesUI.DOCPROPERTIESCLASSNAME;
            return var2;
         } else {
            throw new IllegalArgumentException("Unsupported role");
         }
      }
   }
}
