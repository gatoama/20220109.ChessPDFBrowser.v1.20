package com.frojasg1.sun.awt.windows;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Label;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.awt.peer.ComponentPeer;
import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Paper;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FilePermission;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.Attribute;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Chromaticity;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.Destination;
import javax.print.attribute.standard.Media;
import javax.print.attribute.standard.MediaSize;
import javax.print.attribute.standard.MediaSizeName;
import javax.print.attribute.standard.MediaTray;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PageRanges;
import javax.print.attribute.standard.PrintQuality;
import javax.print.attribute.standard.PrinterResolution;
import javax.print.attribute.standard.SheetCollate;
import javax.print.attribute.standard.Sides;
import com.frojasg1.sun.awt.Win32FontManager;
import com.frojasg1.sun.awt.windows.WPageDialog;
import com.frojasg1.sun.awt.windows.WPathGraphics;
import com.frojasg1.sun.awt.windows.WPrintDialog;
import com.frojasg1.sun.awt.windows.WWindowPeer;
import com.frojasg1.sun.java2d.Disposer;
import com.frojasg1.sun.java2d.DisposerRecord;
import com.frojasg1.sun.java2d.DisposerTarget;
import com.frojasg1.sun.print.DialogOwner;
import com.frojasg1.sun.print.PeekGraphics;
import com.frojasg1.sun.print.PeekMetrics;
import com.frojasg1.sun.print.RasterPrinterJob;
import com.frojasg1.sun.print.ServiceDialog;
import com.frojasg1.sun.print.SunAlternateMedia;
import com.frojasg1.sun.print.SunPageSelection;
import com.frojasg1.sun.print.Win32MediaTray;
import com.frojasg1.sun.print.Win32PrintService;
import com.frojasg1.sun.print.Win32PrintServiceLookup;

public final class WPrinterJob extends RasterPrinterJob implements DisposerTarget {
   protected static final long PS_ENDCAP_ROUND = 0L;
   protected static final long PS_ENDCAP_SQUARE = 256L;
   protected static final long PS_ENDCAP_FLAT = 512L;
   protected static final long PS_JOIN_ROUND = 0L;
   protected static final long PS_JOIN_BEVEL = 4096L;
   protected static final long PS_JOIN_MITER = 8192L;
   protected static final int POLYFILL_ALTERNATE = 1;
   protected static final int POLYFILL_WINDING = 2;
   private static final int MAX_WCOLOR = 255;
   private static final int SET_DUP_VERTICAL = 16;
   private static final int SET_DUP_HORIZONTAL = 32;
   private static final int SET_RES_HIGH = 64;
   private static final int SET_RES_LOW = 128;
   private static final int SET_COLOR = 512;
   private static final int SET_ORIENTATION = 16384;
   private static final int SET_COLLATED = 32768;
   private static final int PD_COLLATE = 16;
   private static final int PD_PRINTTOFILE = 32;
   private static final int DM_ORIENTATION = 1;
   private static final int DM_PAPERSIZE = 2;
   private static final int DM_COPIES = 256;
   private static final int DM_DEFAULTSOURCE = 512;
   private static final int DM_PRINTQUALITY = 1024;
   private static final int DM_COLOR = 2048;
   private static final int DM_DUPLEX = 4096;
   private static final int DM_YRESOLUTION = 8192;
   private static final int DM_COLLATE = 32768;
   private static final short DMCOLLATE_FALSE = 0;
   private static final short DMCOLLATE_TRUE = 1;
   private static final short DMORIENT_PORTRAIT = 1;
   private static final short DMORIENT_LANDSCAPE = 2;
   private static final short DMCOLOR_MONOCHROME = 1;
   private static final short DMCOLOR_COLOR = 2;
   private static final short DMRES_DRAFT = -1;
   private static final short DMRES_LOW = -2;
   private static final short DMRES_MEDIUM = -3;
   private static final short DMRES_HIGH = -4;
   private static final short DMDUP_SIMPLEX = 1;
   private static final short DMDUP_VERTICAL = 2;
   private static final short DMDUP_HORIZONTAL = 3;
   private static final int MAX_UNKNOWN_PAGES = 9999;
   private boolean driverDoesMultipleCopies = false;
   private boolean driverDoesCollation = false;
   private boolean userRequestedCollation = false;
   private boolean noDefaultPrinter = false;
   private WPrinterJob.HandleRecord handleRecord = new WPrinterJob.HandleRecord();
   private int mPrintPaperSize;
   private int mPrintXRes;
   private int mPrintYRes;
   private int mPrintPhysX;
   private int mPrintPhysY;
   private int mPrintWidth;
   private int mPrintHeight;
   private int mPageWidth;
   private int mPageHeight;
   private int mAttSides;
   private int mAttChromaticity;
   private int mAttXRes;
   private int mAttYRes;
   private int mAttQuality;
   private int mAttCollate;
   private int mAttCopies;
   private int mAttMediaSizeName;
   private int mAttMediaTray;
   private String mDestination = null;
   private Color mLastColor;
   private Color mLastTextColor;
   private String mLastFontFamily;
   private float mLastFontSize;
   private int mLastFontStyle;
   private int mLastRotation;
   private float mLastAwScale;
   private PrinterJob pjob;
   private ComponentPeer dialogOwnerPeer = null;
   private Object disposerReferent = new Object();
   private String lastNativeService = null;
   private boolean defaultCopies = true;

   public WPrinterJob() {
      Disposer.addRecord(this.disposerReferent, this.handleRecord = new WPrinterJob.HandleRecord());
      this.initAttributeMembers();
   }

   public Object getDisposerReferent() {
      return this.disposerReferent;
   }

   public PageFormat pageDialog(PageFormat var1) throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else if (!(this.getPrintService() instanceof Win32PrintService)) {
         return super.pageDialog(var1);
      } else {
         PageFormat var2 = (PageFormat)var1.clone();
         boolean var3 = false;
         com.frojasg1.sun.awt.windows.WPageDialog var4 = new com.frojasg1.sun.awt.windows.WPageDialog((Frame)null, this, var2, (Printable)null);
         var4.setRetVal(false);
         var4.setVisible(true);
         var3 = var4.getRetVal();
         var4.dispose();
         if (var3 && this.myService != null) {
            String var5 = this.getNativePrintService();
            if (!this.myService.getName().equals(var5)) {
               try {
                  this.setPrintService(Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(var5));
               } catch (PrinterException var7) {
               }
            }

            this.updatePageAttributes(this.myService, var2);
            return var2;
         } else {
            return var1;
         }
      }
   }

   private boolean displayNativeDialog() {
      if (this.attributes == null) {
         return false;
      } else {
         DialogOwner var1 = (DialogOwner)this.attributes.get(DialogOwner.class);
         Frame var2 = var1 != null ? var1.getOwner() : null;
         com.frojasg1.sun.awt.windows.WPrintDialog var3 = new com.frojasg1.sun.awt.windows.WPrintDialog(var2, this);
         var3.setRetVal(false);
         var3.setVisible(true);
         boolean var4 = var3.getRetVal();
         var3.dispose();
         Destination var5 = (Destination)this.attributes.get(Destination.class);
         if (var5 != null && var4) {
            String var6 = null;
            String var7 = "sun.print.resources.serviceui";
            ResourceBundle var8 = ResourceBundle.getBundle(var7);

            try {
               var6 = var8.getString("dialog.printtofile");
            } catch (MissingResourceException var16) {
            }

            FileDialog var9 = new FileDialog(var2, var6, 1);
            URI var10 = var5.getURI();
            String var11 = var10 != null ? var10.getSchemeSpecificPart() : null;
            if (var11 != null) {
               File var12 = new File(var11);
               var9.setFile(var12.getName());
               File var13 = var12.getParentFile();
               if (var13 != null) {
                  var9.setDirectory(var13.getPath());
               }
            } else {
               var9.setFile("out.prn");
            }

            var9.setVisible(true);
            String var17 = var9.getFile();
            if (var17 == null) {
               var9.dispose();
               return false;
            } else {
               String var18 = var9.getDirectory() + var17;
               File var14 = new File(var18);

               for(File var15 = var14.getParentFile(); var14.exists() && (!var14.isFile() || !var14.canWrite()) || var15 != null && (!var15.exists() || var15.exists() && !var15.canWrite()); var15 = var14.getParentFile()) {
                  (new WPrinterJob.PrintToFileErrorDialog(var2, ServiceDialog.getMsg("dialog.owtitle"), ServiceDialog.getMsg("dialog.writeerror") + " " + var18, ServiceDialog.getMsg("button.ok"))).setVisible(true);
                  var9.setVisible(true);
                  var17 = var9.getFile();
                  if (var17 == null) {
                     var9.dispose();
                     return false;
                  }

                  var18 = var9.getDirectory() + var17;
                  var14 = new File(var18);
               }

               var9.dispose();
               this.attributes.add(new Destination(var14.toURI()));
               return true;
            }
         } else {
            return var4;
         }
      }
   }

   public boolean printDialog() throws HeadlessException {
      if (GraphicsEnvironment.isHeadless()) {
         throw new HeadlessException();
      } else {
         if (this.attributes == null) {
            this.attributes = new HashPrintRequestAttributeSet();
         }

         if (!(this.getPrintService() instanceof Win32PrintService)) {
            return super.printDialog(this.attributes);
         } else {
            return this.noDefaultPrinter ? false : this.displayNativeDialog();
         }
      }
   }

   public void setPrintService(PrintService var1) throws PrinterException {
      super.setPrintService(var1);
      if (var1 instanceof Win32PrintService) {
         this.driverDoesMultipleCopies = false;
         this.driverDoesCollation = false;
         this.setNativePrintServiceIfNeeded(var1.getName());
      }
   }

   private native void setNativePrintService(String var1) throws PrinterException;

   private void setNativePrintServiceIfNeeded(String var1) throws PrinterException {
      if (var1 != null && !var1.equals(this.lastNativeService)) {
         this.setNativePrintService(var1);
         this.lastNativeService = var1;
      }

   }

   public PrintService getPrintService() {
      if (this.myService == null) {
         String var1 = this.getNativePrintService();
         if (var1 != null) {
            this.myService = Win32PrintServiceLookup.getWin32PrintLUS().getPrintServiceByName(var1);
            if (this.myService != null) {
               return this.myService;
            }
         }

         this.myService = PrintServiceLookup.lookupDefaultPrintService();
         if (this.myService instanceof Win32PrintService) {
            try {
               this.setNativePrintServiceIfNeeded(this.myService.getName());
            } catch (Exception var3) {
               this.myService = null;
            }
         }
      }

      return this.myService;
   }

   private native String getNativePrintService();

   private void initAttributeMembers() {
      this.mAttSides = 0;
      this.mAttChromaticity = 0;
      this.mAttXRes = 0;
      this.mAttYRes = 0;
      this.mAttQuality = 0;
      this.mAttCollate = -1;
      this.mAttCopies = 0;
      this.mAttMediaTray = 0;
      this.mAttMediaSizeName = 0;
      this.mDestination = null;
   }

   protected void setAttributes(PrintRequestAttributeSet var1) throws PrinterException {
      this.initAttributeMembers();
      super.setAttributes(var1);
      this.mAttCopies = this.getCopiesInt();
      this.mDestination = this.destinationAttr;
      if (var1 != null) {
         Attribute[] var2 = var1.toArray();

         for(int var3 = 0; var3 < var2.length; ++var3) {
            Object var4 = var2[var3];

            try {
               if (((Attribute)var4).getCategory() == Sides.class) {
                  this.setSidesAttrib((Attribute)var4);
               } else if (((Attribute)var4).getCategory() == Chromaticity.class) {
                  this.setColorAttrib((Attribute)var4);
               } else if (((Attribute)var4).getCategory() == PrinterResolution.class) {
                  this.setResolutionAttrib((Attribute)var4);
               } else if (((Attribute)var4).getCategory() == PrintQuality.class) {
                  this.setQualityAttrib((Attribute)var4);
               } else if (((Attribute)var4).getCategory() == SheetCollate.class) {
                  this.setCollateAttrib((Attribute)var4);
               } else if (((Attribute)var4).getCategory() == Media.class || ((Attribute)var4).getCategory() == SunAlternateMedia.class) {
                  if (((Attribute)var4).getCategory() == SunAlternateMedia.class) {
                     Media var5 = (Media)var1.get(Media.class);
                     if (var5 == null || !(var5 instanceof MediaTray)) {
                        var4 = ((SunAlternateMedia)var4).getMedia();
                     }
                  }

                  if (var4 instanceof MediaSizeName) {
                     this.setWin32MediaAttrib((Attribute)var4);
                  }

                  if (var4 instanceof MediaTray) {
                     this.setMediaTrayAttrib((Attribute)var4);
                  }
               }
            } catch (ClassCastException var6) {
            }
         }

      }
   }

   private native void getDefaultPage(PageFormat var1);

   public PageFormat defaultPage(PageFormat var1) {
      PageFormat var2 = (PageFormat)var1.clone();
      this.getDefaultPage(var2);
      return var2;
   }

   protected native void validatePaper(Paper var1, Paper var2);

   protected Graphics2D createPathGraphics(PeekGraphics var1, PrinterJob var2, Printable var3, PageFormat var4, int var5) {
      PeekMetrics var7 = var1.getMetrics();
      com.frojasg1.sun.awt.windows.WPathGraphics var6;
      if (forcePDL || !forceRaster && !var7.hasNonSolidColors() && !var7.hasCompositing()) {
         BufferedImage var8 = new BufferedImage(8, 8, 1);
         Graphics2D var9 = var8.createGraphics();
         boolean var10 = !var1.getAWTDrawingOnly();
         var6 = new com.frojasg1.sun.awt.windows.WPathGraphics(var9, var2, var3, var4, var5, var10);
      } else {
         var6 = null;
      }

      return var6;
   }

   protected double getXRes() {
      return this.mAttXRes != 0 ? (double)this.mAttXRes : (double)this.mPrintXRes;
   }

   protected double getYRes() {
      return this.mAttYRes != 0 ? (double)this.mAttYRes : (double)this.mPrintYRes;
   }

   protected double getPhysicalPrintableX(Paper var1) {
      return (double)this.mPrintPhysX;
   }

   protected double getPhysicalPrintableY(Paper var1) {
      return (double)this.mPrintPhysY;
   }

   protected double getPhysicalPrintableWidth(Paper var1) {
      return (double)this.mPrintWidth;
   }

   protected double getPhysicalPrintableHeight(Paper var1) {
      return (double)this.mPrintHeight;
   }

   protected double getPhysicalPageWidth(Paper var1) {
      return (double)this.mPageWidth;
   }

   protected double getPhysicalPageHeight(Paper var1) {
      return (double)this.mPageHeight;
   }

   protected boolean isCollated() {
      return this.userRequestedCollation;
   }

   protected int getCollatedCopies() {
      this.debug_println("driverDoesMultipleCopies=" + this.driverDoesMultipleCopies + " driverDoesCollation=" + this.driverDoesCollation);
      if (super.isCollated() && !this.driverDoesCollation) {
         this.mAttCollate = 0;
         this.mAttCopies = 1;
         return this.getCopies();
      } else {
         return 1;
      }
   }

   protected int getNoncollatedCopies() {
      return !this.driverDoesMultipleCopies && !super.isCollated() ? this.getCopies() : 1;
   }

   private long getPrintDC() {
      return this.handleRecord.mPrintDC;
   }

   private void setPrintDC(long var1) {
      this.handleRecord.mPrintDC = var1;
   }

   private long getDevMode() {
      return this.handleRecord.mPrintHDevMode;
   }

   private void setDevMode(long var1) {
      this.handleRecord.mPrintHDevMode = var1;
   }

   private long getDevNames() {
      return this.handleRecord.mPrintHDevNames;
   }

   private void setDevNames(long var1) {
      this.handleRecord.mPrintHDevNames = var1;
   }

   protected void beginPath() {
      this.beginPath(this.getPrintDC());
   }

   protected void endPath() {
      this.endPath(this.getPrintDC());
   }

   protected void closeFigure() {
      this.closeFigure(this.getPrintDC());
   }

   protected void fillPath() {
      this.fillPath(this.getPrintDC());
   }

   protected void moveTo(float var1, float var2) {
      this.moveTo(this.getPrintDC(), var1, var2);
   }

   protected void lineTo(float var1, float var2) {
      this.lineTo(this.getPrintDC(), var1, var2);
   }

   protected void polyBezierTo(float var1, float var2, float var3, float var4, float var5, float var6) {
      this.polyBezierTo(this.getPrintDC(), var1, var2, var3, var4, var5, var6);
   }

   protected void setPolyFillMode(int var1) {
      this.setPolyFillMode(this.getPrintDC(), var1);
   }

   protected void selectSolidBrush(Color var1) {
      if (!var1.equals(this.mLastColor)) {
         this.mLastColor = var1;
         float[] var2 = var1.getRGBColorComponents((float[])null);
         this.selectSolidBrush(this.getPrintDC(), (int)(var2[0] * 255.0F), (int)(var2[1] * 255.0F), (int)(var2[2] * 255.0F));
      }

   }

   protected int getPenX() {
      return this.getPenX(this.getPrintDC());
   }

   protected int getPenY() {
      return this.getPenY(this.getPrintDC());
   }

   protected void selectClipPath() {
      this.selectClipPath(this.getPrintDC());
   }

   protected void frameRect(float var1, float var2, float var3, float var4) {
      this.frameRect(this.getPrintDC(), var1, var2, var3, var4);
   }

   protected void fillRect(float var1, float var2, float var3, float var4, Color var5) {
      float[] var6 = var5.getRGBColorComponents((float[])null);
      this.fillRect(this.getPrintDC(), var1, var2, var3, var4, (int)(var6[0] * 255.0F), (int)(var6[1] * 255.0F), (int)(var6[2] * 255.0F));
   }

   protected void selectPen(float var1, Color var2) {
      float[] var3 = var2.getRGBColorComponents((float[])null);
      this.selectPen(this.getPrintDC(), var1, (int)(var3[0] * 255.0F), (int)(var3[1] * 255.0F), (int)(var3[2] * 255.0F));
   }

   protected boolean selectStylePen(int var1, int var2, float var3, Color var4) {
      float[] var9 = var4.getRGBColorComponents((float[])null);
      long var5;
      switch(var1) {
      case 0:
         var5 = 512L;
         break;
      case 1:
         var5 = 0L;
         break;
      case 2:
      default:
         var5 = 256L;
      }

      long var7;
      switch(var2) {
      case 0:
      default:
         var7 = 8192L;
         break;
      case 1:
         var7 = 0L;
         break;
      case 2:
         var7 = 4096L;
      }

      return this.selectStylePen(this.getPrintDC(), var5, var7, var3, (int)(var9[0] * 255.0F), (int)(var9[1] * 255.0F), (int)(var9[2] * 255.0F));
   }

   protected boolean setFont(String var1, float var2, int var3, int var4, float var5) {
      boolean var6 = true;
      if (!var1.equals(this.mLastFontFamily) || var2 != this.mLastFontSize || var3 != this.mLastFontStyle || var4 != this.mLastRotation || var5 != this.mLastAwScale) {
         var6 = this.setFont(this.getPrintDC(), var1, var2, (var3 & 1) != 0, (var3 & 2) != 0, var4, var5);
         if (var6) {
            this.mLastFontFamily = var1;
            this.mLastFontSize = var2;
            this.mLastFontStyle = var3;
            this.mLastRotation = var4;
            this.mLastAwScale = var5;
         }
      }

      return var6;
   }

   protected void setTextColor(Color var1) {
      if (!var1.equals(this.mLastTextColor)) {
         this.mLastTextColor = var1;
         float[] var2 = var1.getRGBColorComponents((float[])null);
         this.setTextColor(this.getPrintDC(), (int)(var2[0] * 255.0F), (int)(var2[1] * 255.0F), (int)(var2[2] * 255.0F));
      }

   }

   protected String removeControlChars(String var1) {
      return super.removeControlChars(var1);
   }

   protected void textOut(String var1, float var2, float var3, float[] var4) {
      String var5 = this.removeControlChars(var1);

      assert var4 == null || var5.length() == var1.length();

      if (var5.length() != 0) {
         this.textOut(this.getPrintDC(), var5, var5.length(), false, var2, var3, var4);
      }
   }

   protected void glyphsOut(int[] var1, float var2, float var3, float[] var4) {
      char[] var5 = new char[var1.length];

      for(int var6 = 0; var6 < var1.length; ++var6) {
         var5[var6] = (char)(var1[var6] & '\uffff');
      }

      String var7 = new String(var5);
      this.textOut(this.getPrintDC(), var7, var1.length, true, var2, var3, var4);
   }

   protected int getGDIAdvance(String var1) {
      var1 = this.removeControlChars(var1);
      return var1.length() == 0 ? 0 : this.getGDIAdvance(this.getPrintDC(), var1);
   }

   protected void drawImage3ByteBGR(byte[] var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9) {
      this.drawDIBImage(this.getPrintDC(), var1, var2, var3, var4, var5, var6, var7, var8, var9, 24, (byte[])null);
   }

   protected void drawDIBImage(byte[] var1, float var2, float var3, float var4, float var5, float var6, float var7, float var8, float var9, int var10, IndexColorModel var11) {
      int var12 = 24;
      byte[] var13 = null;
      if (var11 != null) {
         var12 = var10;
         var13 = new byte[(1 << var11.getPixelSize()) * 4];

         for(int var14 = 0; var14 < var11.getMapSize(); ++var14) {
            var13[var14 * 4 + 0] = (byte)(var11.getBlue(var14) & 255);
            var13[var14 * 4 + 1] = (byte)(var11.getGreen(var14) & 255);
            var13[var14 * 4 + 2] = (byte)(var11.getRed(var14) & 255);
         }
      }

      this.drawDIBImage(this.getPrintDC(), var1, var2, var3, var4, var5, var6, var7, var8, var9, var12, var13);
   }

   protected void startPage(PageFormat var1, Printable var2, int var3, boolean var4) {
      this.invalidateCachedState();
      this.deviceStartPage(var1, var2, var3, var4);
   }

   protected void endPage(PageFormat var1, Printable var2, int var3) {
      this.deviceEndPage(var1, var2, var3);
   }

   private void invalidateCachedState() {
      this.mLastColor = null;
      this.mLastTextColor = null;
      this.mLastFontFamily = null;
   }

   public void setCopies(int var1) {
      super.setCopies(var1);
      this.defaultCopies = false;
      this.mAttCopies = var1;
      this.setNativeCopies(var1);
   }

   private native void setNativeCopies(int var1);

   private native boolean jobSetup(Pageable var1, boolean var2);

   protected native void initPrinter();

   private native boolean _startDoc(String var1, String var2) throws PrinterException;

   protected void startDoc() throws PrinterException {
      if (!this._startDoc(this.mDestination, this.getJobName())) {
         this.cancel();
      }

   }

   protected native void endDoc();

   protected native void abortDoc();

   private static native void deleteDC(long var0, long var2, long var4);

   protected native void deviceStartPage(PageFormat var1, Printable var2, int var3, boolean var4);

   protected native void deviceEndPage(PageFormat var1, Printable var2, int var3);

   protected native void printBand(byte[] var1, int var2, int var3, int var4, int var5);

   protected native void beginPath(long var1);

   protected native void endPath(long var1);

   protected native void closeFigure(long var1);

   protected native void fillPath(long var1);

   protected native void moveTo(long var1, float var3, float var4);

   protected native void lineTo(long var1, float var3, float var4);

   protected native void polyBezierTo(long var1, float var3, float var4, float var5, float var6, float var7, float var8);

   protected native void setPolyFillMode(long var1, int var3);

   protected native void selectSolidBrush(long var1, int var3, int var4, int var5);

   protected native int getPenX(long var1);

   protected native int getPenY(long var1);

   protected native void selectClipPath(long var1);

   protected native void frameRect(long var1, float var3, float var4, float var5, float var6);

   protected native void fillRect(long var1, float var3, float var4, float var5, float var6, int var7, int var8, int var9);

   protected native void selectPen(long var1, float var3, int var4, int var5, int var6);

   protected native boolean selectStylePen(long var1, long var3, long var5, float var7, int var8, int var9, int var10);

   protected native boolean setFont(long var1, String var3, float var4, boolean var5, boolean var6, int var7, float var8);

   protected native void setTextColor(long var1, int var3, int var4, int var5);

   protected native void textOut(long var1, String var3, int var4, boolean var5, float var6, float var7, float[] var8);

   private native int getGDIAdvance(long var1, String var3);

   private native void drawDIBImage(long var1, byte[] var3, float var4, float var5, float var6, float var7, float var8, float var9, float var10, float var11, int var12, byte[] var13);

   private final String getPrinterAttrib() {
      PrintService var1 = this.getPrintService();
      String var2 = var1 != null ? var1.getName() : null;
      return var2;
   }

   private final int getCollateAttrib() {
      return this.mAttCollate;
   }

   private void setCollateAttrib(Attribute var1) {
      if (var1 == SheetCollate.COLLATED) {
         this.mAttCollate = 1;
      } else {
         this.mAttCollate = 0;
      }

   }

   private void setCollateAttrib(Attribute var1, PrintRequestAttributeSet var2) {
      this.setCollateAttrib(var1);
      var2.add(var1);
   }

   private final int getOrientAttrib() {
      byte var1 = 1;
      OrientationRequested var2 = this.attributes == null ? null : (OrientationRequested)this.attributes.get(OrientationRequested.class);
      if (var2 == null) {
         var2 = (OrientationRequested)this.myService.getDefaultAttributeValue(OrientationRequested.class);
      }

      if (var2 != null) {
         if (var2 == OrientationRequested.REVERSE_LANDSCAPE) {
            var1 = 2;
         } else if (var2 == OrientationRequested.LANDSCAPE) {
            var1 = 0;
         }
      }

      return var1;
   }

   private void setOrientAttrib(Attribute var1, PrintRequestAttributeSet var2) {
      if (var2 != null) {
         var2.add(var1);
      }

   }

   private final int getCopiesAttrib() {
      return this.defaultCopies ? 0 : this.getCopiesInt();
   }

   private final void setRangeCopiesAttribute(int var1, int var2, boolean var3, int var4) {
      if (this.attributes != null) {
         if (var3) {
            this.attributes.add(new PageRanges(var1, var2));
            this.setPageRange(var1, var2);
         }

         this.defaultCopies = false;
         this.attributes.add(new Copies(var4));
         super.setCopies(var4);
         this.mAttCopies = var4;
      }

   }

   private final boolean getDestAttrib() {
      return this.mDestination != null;
   }

   private final int getQualityAttrib() {
      return this.mAttQuality;
   }

   private void setQualityAttrib(Attribute var1) {
      if (var1 == PrintQuality.HIGH) {
         this.mAttQuality = -4;
      } else if (var1 == PrintQuality.NORMAL) {
         this.mAttQuality = -3;
      } else {
         this.mAttQuality = -2;
      }

   }

   private void setQualityAttrib(Attribute var1, PrintRequestAttributeSet var2) {
      this.setQualityAttrib(var1);
      var2.add(var1);
   }

   private final int getColorAttrib() {
      return this.mAttChromaticity;
   }

   private void setColorAttrib(Attribute var1) {
      if (var1 == Chromaticity.COLOR) {
         this.mAttChromaticity = 2;
      } else {
         this.mAttChromaticity = 1;
      }

   }

   private void setColorAttrib(Attribute var1, PrintRequestAttributeSet var2) {
      this.setColorAttrib(var1);
      var2.add(var1);
   }

   private final int getSidesAttrib() {
      return this.mAttSides;
   }

   private void setSidesAttrib(Attribute var1) {
      if (var1 == Sides.TWO_SIDED_LONG_EDGE) {
         this.mAttSides = 2;
      } else if (var1 == Sides.TWO_SIDED_SHORT_EDGE) {
         this.mAttSides = 3;
      } else {
         this.mAttSides = 1;
      }

   }

   private void setSidesAttrib(Attribute var1, PrintRequestAttributeSet var2) {
      this.setSidesAttrib(var1);
      var2.add(var1);
   }

   private final int[] getWin32MediaAttrib() {
      int[] var1 = new int[]{0, 0};
      if (this.attributes != null) {
         Media var2 = (Media)this.attributes.get(Media.class);
         if (var2 instanceof MediaSizeName) {
            MediaSizeName var3 = (MediaSizeName)var2;
            MediaSize var4 = MediaSize.getMediaSizeForName(var3);
            if (var4 != null) {
               var1[0] = (int)((double)var4.getX(25400) * 72.0D);
               var1[1] = (int)((double)var4.getY(25400) * 72.0D);
            }
         }
      }

      return var1;
   }

   private void setWin32MediaAttrib(Attribute var1) {
      if (var1 instanceof MediaSizeName) {
         MediaSizeName var2 = (MediaSizeName)var1;
         this.mAttMediaSizeName = ((Win32PrintService)this.myService).findPaperID(var2);
      }
   }

   private void addPaperSize(PrintRequestAttributeSet var1, int var2, int var3, int var4) {
      if (var1 != null) {
         MediaSizeName var5 = ((Win32PrintService)this.myService).findWin32Media(var2);
         if (var5 == null) {
            var5 = ((Win32PrintService)this.myService).findMatchingMediaSizeNameMM((float)var3, (float)var4);
         }

         if (var5 != null) {
            var1.add(var5);
         }

      }
   }

   private void setWin32MediaAttrib(int var1, int var2, int var3) {
      this.addPaperSize(this.attributes, var1, var2, var3);
      this.mAttMediaSizeName = var1;
   }

   private void setMediaTrayAttrib(Attribute var1) {
      if (var1 == MediaTray.BOTTOM) {
         this.mAttMediaTray = 2;
      } else if (var1 == MediaTray.ENVELOPE) {
         this.mAttMediaTray = 5;
      } else if (var1 == MediaTray.LARGE_CAPACITY) {
         this.mAttMediaTray = 11;
      } else if (var1 == MediaTray.MAIN) {
         this.mAttMediaTray = 1;
      } else if (var1 == MediaTray.MANUAL) {
         this.mAttMediaTray = 4;
      } else if (var1 == MediaTray.MIDDLE) {
         this.mAttMediaTray = 3;
      } else if (var1 == MediaTray.SIDE) {
         this.mAttMediaTray = 7;
      } else if (var1 == MediaTray.TOP) {
         this.mAttMediaTray = 1;
      } else if (var1 instanceof Win32MediaTray) {
         this.mAttMediaTray = ((Win32MediaTray)var1).winID;
      } else {
         this.mAttMediaTray = 1;
      }

   }

   private void setMediaTrayAttrib(int var1) {
      this.mAttMediaTray = var1;
      MediaTray var2 = ((Win32PrintService)this.myService).findMediaTray(var1);
   }

   private int getMediaTrayAttrib() {
      return this.mAttMediaTray;
   }

   private final boolean getPrintToFileEnabled() {
      SecurityManager var1 = System.getSecurityManager();
      if (var1 != null) {
         FilePermission var2 = new FilePermission("<<ALL FILES>>", "read,write");

         try {
            var1.checkPermission(var2);
         } catch (SecurityException var4) {
            return false;
         }
      }

      return true;
   }

   private final void setNativeAttributes(int var1, int var2, int var3) {
      if (this.attributes != null) {
         if ((var1 & 32) != 0) {
            Destination var4 = (Destination)this.attributes.get(Destination.class);
            if (var4 == null) {
               try {
                  this.attributes.add(new Destination((new File("./out.prn")).toURI()));
               } catch (SecurityException var8) {
                  try {
                     this.attributes.add(new Destination(new URI("file:out.prn")));
                  } catch (URISyntaxException var7) {
                  }
               }
            }
         } else {
            this.attributes.remove(Destination.class);
         }

         if ((var1 & 16) != 0) {
            this.setCollateAttrib(SheetCollate.COLLATED, this.attributes);
         } else {
            this.setCollateAttrib(SheetCollate.UNCOLLATED, this.attributes);
         }

         if ((var1 & 2) != 0) {
            this.attributes.add(SunPageSelection.RANGE);
         } else if ((var1 & 1) != 0) {
            this.attributes.add(SunPageSelection.SELECTION);
         } else {
            this.attributes.add(SunPageSelection.ALL);
         }

         if ((var2 & 1) != 0) {
            if ((var3 & 16384) != 0) {
               this.setOrientAttrib(OrientationRequested.LANDSCAPE, this.attributes);
            } else {
               this.setOrientAttrib(OrientationRequested.PORTRAIT, this.attributes);
            }
         }

         if ((var2 & 2048) != 0) {
            if ((var3 & 512) != 0) {
               this.setColorAttrib(Chromaticity.COLOR, this.attributes);
            } else {
               this.setColorAttrib(Chromaticity.MONOCHROME, this.attributes);
            }
         }

         if ((var2 & 1024) != 0) {
            PrintQuality var9;
            if ((var3 & 128) != 0) {
               var9 = PrintQuality.DRAFT;
            } else if ((var2 & 64) != 0) {
               var9 = PrintQuality.HIGH;
            } else {
               var9 = PrintQuality.NORMAL;
            }

            this.setQualityAttrib(var9, this.attributes);
         }

         if ((var2 & 4096) != 0) {
            Sides var10;
            if ((var3 & 16) != 0) {
               var10 = Sides.TWO_SIDED_LONG_EDGE;
            } else if ((var3 & 32) != 0) {
               var10 = Sides.TWO_SIDED_SHORT_EDGE;
            } else {
               var10 = Sides.ONE_SIDED;
            }

            this.setSidesAttrib(var10, this.attributes);
         }

      }
   }

   private void getDevModeValues(PrintRequestAttributeSet var1, WPrinterJob.DevModeValues var2) {
      Copies var3 = (Copies)var1.get(Copies.class);
      if (var3 != null) {
         var2.dmFields |= 256;
         var2.copies = (short)var3.getValue();
      }

      SheetCollate var4 = (SheetCollate)var1.get(SheetCollate.class);
      if (var4 != null) {
         var2.dmFields |= 32768;
         var2.collate = (short)(var4 == SheetCollate.COLLATED ? 1 : 0);
      }

      Chromaticity var5 = (Chromaticity)var1.get(Chromaticity.class);
      if (var5 != null) {
         var2.dmFields |= 2048;
         if (var5 == Chromaticity.COLOR) {
            var2.color = 2;
         } else {
            var2.color = 1;
         }
      }

      Sides var6 = (Sides)var1.get(Sides.class);
      if (var6 != null) {
         var2.dmFields |= 4096;
         if (var6 == Sides.TWO_SIDED_LONG_EDGE) {
            var2.duplex = 2;
         } else if (var6 == Sides.TWO_SIDED_SHORT_EDGE) {
            var2.duplex = 3;
         } else {
            var2.duplex = 1;
         }
      }

      OrientationRequested var7 = (OrientationRequested)var1.get(OrientationRequested.class);
      if (var7 != null) {
         var2.dmFields |= 1;
         var2.orient = (short)(var7 == OrientationRequested.LANDSCAPE ? 2 : 1);
      }

      Media var8 = (Media)var1.get(Media.class);
      if (var8 instanceof MediaSizeName) {
         var2.dmFields |= 2;
         MediaSizeName var9 = (MediaSizeName)var8;
         var2.paper = (short)((Win32PrintService)this.myService).findPaperID(var9);
      }

      MediaTray var12 = null;
      if (var8 instanceof MediaTray) {
         var12 = (MediaTray)var8;
      }

      if (var12 == null) {
         SunAlternateMedia var10 = (SunAlternateMedia)var1.get(SunAlternateMedia.class);
         if (var10 != null && var10.getMedia() instanceof MediaTray) {
            var12 = (MediaTray)var10.getMedia();
         }
      }

      if (var12 != null) {
         var2.dmFields |= 512;
         var2.bin = (short)((Win32PrintService)this.myService).findTrayID(var12);
      }

      PrintQuality var13 = (PrintQuality)var1.get(PrintQuality.class);
      if (var13 != null) {
         var2.dmFields |= 1024;
         if (var13 == PrintQuality.DRAFT) {
            var2.xres_quality = -1;
         } else if (var13 == PrintQuality.HIGH) {
            var2.xres_quality = -4;
         } else {
            var2.xres_quality = -3;
         }
      }

      PrinterResolution var11 = (PrinterResolution)var1.get(PrinterResolution.class);
      if (var11 != null) {
         var2.dmFields |= 9216;
         var2.xres_quality = (short)var11.getCrossFeedResolution(100);
         var2.yres = (short)var11.getFeedResolution(100);
      }

   }

   private final void setJobAttributes(PrintRequestAttributeSet var1, int var2, int var3, short var4, short var5, short var6, short var7, short var8, short var9, short var10) {
      if (var1 != null) {
         if ((var2 & 256) != 0) {
            var1.add(new Copies(var4));
         }

         if ((var2 & '耀') != 0) {
            if ((var3 & '耀') != 0) {
               var1.add(SheetCollate.COLLATED);
            } else {
               var1.add(SheetCollate.UNCOLLATED);
            }
         }

         if ((var2 & 1) != 0) {
            if ((var3 & 16384) != 0) {
               var1.add(OrientationRequested.LANDSCAPE);
            } else {
               var1.add(OrientationRequested.PORTRAIT);
            }
         }

         if ((var2 & 2048) != 0) {
            if ((var3 & 512) != 0) {
               var1.add(Chromaticity.COLOR);
            } else {
               var1.add(Chromaticity.MONOCHROME);
            }
         }

         if ((var2 & 1024) != 0) {
            if (var9 < 0) {
               PrintQuality var11;
               if ((var3 & 128) != 0) {
                  var11 = PrintQuality.DRAFT;
               } else if ((var2 & 64) != 0) {
                  var11 = PrintQuality.HIGH;
               } else {
                  var11 = PrintQuality.NORMAL;
               }

               var1.add(var11);
            } else if (var9 > 0 && var10 > 0) {
               var1.add(new PrinterResolution(var9, var10, 100));
            }
         }

         if ((var2 & 4096) != 0) {
            Sides var12;
            if ((var3 & 16) != 0) {
               var12 = Sides.TWO_SIDED_LONG_EDGE;
            } else if ((var3 & 32) != 0) {
               var12 = Sides.TWO_SIDED_SHORT_EDGE;
            } else {
               var12 = Sides.ONE_SIDED;
            }

            var1.add(var12);
         }

         if ((var2 & 2) != 0) {
            this.addPaperSize(var1, var5, var6, var7);
         }

         if ((var2 & 512) != 0) {
            MediaTray var13 = ((Win32PrintService)this.myService).findMediaTray(var8);
            var1.add(new SunAlternateMedia(var13));
         }

      }
   }

   private native boolean showDocProperties(long var1, PrintRequestAttributeSet var3, int var4, short var5, short var6, short var7, short var8, short var9, short var10, short var11, short var12, short var13);

   public PrintRequestAttributeSet showDocumentProperties(Window var1, PrintService var2, PrintRequestAttributeSet var3) {
      try {
         this.setNativePrintServiceIfNeeded(var2.getName());
      } catch (PrinterException var8) {
      }

      long var4 = ((com.frojasg1.sun.awt.windows.WWindowPeer)((WWindowPeer)var1.getPeer())).getHWnd();
      WPrinterJob.DevModeValues var6 = new WPrinterJob.DevModeValues();
      this.getDevModeValues(var3, var6);
      boolean var7 = this.showDocProperties(var4, var3, var6.dmFields, var6.copies, var6.collate, var6.color, var6.duplex, var6.orient, var6.paper, var6.bin, var6.xres_quality, var6.yres);
      return var7 ? var3 : null;
   }

   private final void setResolutionDPI(int var1, int var2) {
      if (this.attributes != null) {
         PrinterResolution var3 = new PrinterResolution(var1, var2, 100);
         this.attributes.add(var3);
      }

      this.mAttXRes = var1;
      this.mAttYRes = var2;
   }

   private void setResolutionAttrib(Attribute var1) {
      PrinterResolution var2 = (PrinterResolution)var1;
      this.mAttXRes = var2.getCrossFeedResolution(100);
      this.mAttYRes = var2.getFeedResolution(100);
   }

   private void setPrinterNameAttrib(String var1) {
      PrintService var2 = this.getPrintService();
      if (var1 != null) {
         if (var2 == null || !var1.equals(var2.getName())) {
            PrintService[] var3 = PrinterJob.lookupPrintServices();

            for(int var4 = 0; var4 < var3.length; ++var4) {
               if (var1.equals(var3[var4].getName())) {
                  try {
                     this.setPrintService(var3[var4]);
                  } catch (PrinterException var6) {
                  }

                  return;
               }
            }

         }
      }
   }

   private static native void initIDs();

   static {
      Toolkit.getDefaultToolkit();
      initIDs();
      Win32FontManager.registerJREFontsForPrinting();
   }

   private static final class DevModeValues {
      int dmFields;
      short copies;
      short collate;
      short color;
      short duplex;
      short orient;
      short paper;
      short bin;
      short xres_quality;
      short yres;

      private DevModeValues() {
      }
   }

   static class HandleRecord implements DisposerRecord {
      private long mPrintDC;
      private long mPrintHDevMode;
      private long mPrintHDevNames;

      HandleRecord() {
      }

      public void dispose() {
         WPrinterJob.deleteDC(this.mPrintDC, this.mPrintHDevMode, this.mPrintHDevNames);
      }
   }

   class PrintToFileErrorDialog extends Dialog implements ActionListener {
      public PrintToFileErrorDialog(Frame var2, String var3, String var4, String var5) {
         super(var2, var3, true);
         this.init(var2, var3, var4, var5);
      }

      public PrintToFileErrorDialog(Dialog var2, String var3, String var4, String var5) {
         super(var2, var3, true);
         this.init(var2, var3, var4, var5);
      }

      private void init(Component var1, String var2, String var3, String var4) {
         Panel var5 = new Panel();
         this.add("Center", new Label(var3));
         Button var6 = new Button(var4);
         var6.addActionListener(this);
         var5.add(var6);
         this.add("South", var5);
         this.pack();
         Dimension var7 = this.getSize();
         if (var1 != null) {
            Rectangle var8 = var1.getBounds();
            this.setLocation(var8.x + (var8.width - var7.width) / 2, var8.y + (var8.height - var7.height) / 2);
         }

      }

      public void actionPerformed(ActionEvent var1) {
         this.setVisible(false);
         this.dispose();
      }
   }
}
