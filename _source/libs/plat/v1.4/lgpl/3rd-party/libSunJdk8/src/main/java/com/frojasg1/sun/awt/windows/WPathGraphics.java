package com.frojasg1.sun.awt.windows;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Point2D.Float;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.ImageObserver;
import java.awt.image.IndexColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.security.AccessController;
import java.util.Arrays;
import java.util.Locale;
import com.frojasg1.sun.awt.image.ByteComponentRaster;
import com.frojasg1.sun.awt.image.BytePackedRaster;
import com.frojasg1.sun.awt.windows.WPrinterJob;
import com.frojasg1.sun.font.CompositeFont;
import com.frojasg1.sun.font.Font2D;
import com.frojasg1.sun.font.FontUtilities;
import com.frojasg1.sun.font.PhysicalFont;
import com.frojasg1.sun.font.TrueTypeFont;
import com.frojasg1.sun.print.PathGraphics;
import com.frojasg1.sun.print.ProxyGraphics2D;
import com.frojasg1.sun.security.action.GetPropertyAction;

final class WPathGraphics extends PathGraphics {
   private static final int DEFAULT_USER_RES = 72;
   private static final float MIN_DEVICE_LINEWIDTH = 1.2F;
   private static final float MAX_THINLINE_INCHES = 0.014F;
   private static boolean useGDITextLayout = true;
   private static boolean preferGDITextLayout = false;

   WPathGraphics(Graphics2D var1, PrinterJob var2, Printable var3, PageFormat var4, int var5, boolean var6) {
      super(var1, var2, var3, var4, var5, var6);
   }

   public Graphics create() {
      return new WPathGraphics((Graphics2D)this.getDelegate().create(), this.getPrinterJob(), this.getPrintable(), this.getPageFormat(), this.getPageIndex(), this.canDoRedraws());
   }

   public void draw(Shape var1) {
      Stroke var2 = this.getStroke();
      if (var2 instanceof BasicStroke) {
         BasicStroke var4 = null;
         BasicStroke var3 = (BasicStroke)var2;
         float var6 = var3.getLineWidth();
         Float var8 = new Float(var6, var6);
         AffineTransform var7 = this.getTransform();
         var7.deltaTransform(var8, var8);
         float var5 = Math.min(Math.abs(var8.x), Math.abs(var8.y));
         if (var5 < 1.2F) {
            Float var9 = new Float(1.2F, 1.2F);

            try {
               AffineTransform var10 = var7.createInverse();
               var10.deltaTransform(var9, var9);
               float var11 = Math.max(Math.abs(var9.x), Math.abs(var9.y));
               var4 = new BasicStroke(var11, var3.getEndCap(), var3.getLineJoin(), var3.getMiterLimit(), var3.getDashArray(), var3.getDashPhase());
               this.setStroke(var4);
            } catch (NoninvertibleTransformException var12) {
            }
         }

         super.draw(var1);
         if (var4 != null) {
            this.setStroke(var3);
         }
      } else {
         super.draw(var1);
      }

   }

   public void drawString(String var1, int var2, int var3) {
      this.drawString(var1, (float)var2, (float)var3);
   }

   public void drawString(String var1, float var2, float var3) {
      this.drawString(var1, var2, var3, this.getFont(), this.getFontRenderContext(), 0.0F);
   }

   protected int platformFontCount(Font var1, String var2) {
      AffineTransform var3 = this.getTransform();
      AffineTransform var4 = new AffineTransform(var3);
      var4.concatenate(this.getFont().getTransform());
      int var5 = var4.getType();
      boolean var6 = var5 != 32 && (var5 & 64) == 0;
      if (!var6) {
         return 0;
      } else {
         Font2D var7 = FontUtilities.getFont2D(var1);
         return !(var7 instanceof CompositeFont) && !(var7 instanceof TrueTypeFont) ? 0 : 1;
      }
   }

   private static boolean isXP() {
      String var0 = System.getProperty("os.version");
      if (var0 != null) {
         java.lang.Float var1 = java.lang.Float.valueOf(var0);
         return var1 >= 5.1F;
      } else {
         return false;
      }
   }

   private boolean strNeedsTextLayout(String var1, Font var2) {
      char[] var3 = var1.toCharArray();
      boolean var4 = FontUtilities.isComplexText(var3, 0, var3.length);
      if (!var4) {
         return false;
      } else if (!useGDITextLayout) {
         return true;
      } else {
         return !preferGDITextLayout && (!isXP() || !FontUtilities.textLayoutIsCompatible(var2));
      }
   }

   private int getAngle(Double var1) {
      double var2 = Math.toDegrees(Math.atan2(var1.y, var1.x));
      if (var2 < 0.0D) {
         var2 += 360.0D;
      }

      if (var2 != 0.0D) {
         var2 = 360.0D - var2;
      }

      return (int)Math.round(var2 * 10.0D);
   }

   private float getAwScale(double var1, double var3) {
      float var5 = (float)(var1 / var3);
      if (var5 > 0.999F && var5 < 1.001F) {
         var5 = 1.0F;
      }

      return var5;
   }

   public void drawString(String var1, float var2, float var3, Font var4, FontRenderContext var5, float var6) {
      if (var1.length() != 0) {
         if (com.frojasg1.sun.awt.windows.WPrinterJob.shapeTextProp) {
            super.drawString(var1, var2, var3, var4, var5, var6);
         } else {
            boolean var7 = this.strNeedsTextLayout(var1, var4);
            if ((var4.hasLayoutAttributes() || var7) && !this.printingGlyphVector) {
               TextLayout var58 = new TextLayout(var1, var4, var5);
               var58.draw(this, var2, var3);
            } else if (var7) {
               super.drawString(var1, var2, var3, var4, var5, var6);
            } else {
               AffineTransform var8 = this.getTransform();
               AffineTransform var9 = new AffineTransform(var8);
               var9.concatenate(var4.getTransform());
               int var10 = var9.getType();
               boolean var11 = var10 != 32 && (var10 & 64) == 0;
               com.frojasg1.sun.awt.windows.WPrinterJob var12 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();

               try {
                  var12.setTextColor((Color)this.getPaint());
               } catch (ClassCastException var56) {
                  var11 = false;
               }

               if (!var11) {
                  super.drawString(var1, var2, var3, var4, var5, var6);
               } else {
                  Float var13 = new Float(var2, var3);
                  Float var14 = new Float();
                  if (var4.isTransformed()) {
                     AffineTransform var15 = var4.getTransform();
                     float var16 = (float)var15.getTranslateX();
                     float var17 = (float)var15.getTranslateY();
                     if ((double)Math.abs(var16) < 1.0E-5D) {
                        var16 = 0.0F;
                     }

                     if ((double)Math.abs(var17) < 1.0E-5D) {
                        var17 = 0.0F;
                     }

                     var13.x += var16;
                     var13.y += var17;
                  }

                  var8.transform(var13, var14);
                  if (this.getClip() != null) {
                     this.deviceClip(this.getClip().getPathIterator(var8));
                  }

                  float var60 = var4.getSize2D();
                  double var61 = var12.getXRes();
                  double var18 = var12.getYRes();
                  double var20 = var18 / 72.0D;
                  int var22 = this.getPageFormat().getOrientation();
                  double var23;
                  if (var22 == 0 || var22 == 2) {
                     var23 = var61;
                     var61 = var18;
                     var18 = var23;
                  }

                  var23 = var61 / 72.0D;
                  double var25 = var18 / 72.0D;
                  var9.scale(1.0D / var23, 1.0D / var25);
                  Double var27 = new Double(0.0D, 1.0D);
                  var9.deltaTransform(var27, var27);
                  double var28 = Math.sqrt(var27.x * var27.x + var27.y * var27.y);
                  float var30 = (float)((double)var60 * var28 * var20);
                  Double var31 = new Double(1.0D, 0.0D);
                  var9.deltaTransform(var31, var31);
                  double var32 = Math.sqrt(var31.x * var31.x + var31.y * var31.y);
                  float var34 = this.getAwScale(var32, var28);
                  int var35 = this.getAngle(var31);
                  var31 = new Double(1.0D, 0.0D);
                  var8.deltaTransform(var31, var31);
                  double var36 = Math.sqrt(var31.x * var31.x + var31.y * var31.y);
                  var27 = new Double(0.0D, 1.0D);
                  var8.deltaTransform(var27, var27);
                  double var38 = Math.sqrt(var27.x * var27.x + var27.y * var27.y);
                  Font2D var40 = FontUtilities.getFont2D(var4);
                  if (var40 instanceof TrueTypeFont) {
                     this.textOut(var1, var4, (TrueTypeFont)var40, var5, var30, var35, var34, var36, var38, var2, var3, var14.x, var14.y, var6);
                  } else if (var40 instanceof CompositeFont) {
                     CompositeFont var41 = (CompositeFont)var40;
                     float var42 = var2;
                     float var43 = var3;
                     float var44 = var14.x;
                     float var45 = var14.y;
                     char[] var46 = var1.toCharArray();
                     int var47 = var46.length;
                     int[] var48 = new int[var47];
                     var41.getMapper().charsToGlyphs(var47, var46, var48);
                     boolean var49 = false;
                     int var50 = 0;

                     for(boolean var51 = false; var50 < var47; var45 = var14.y) {
                        int var57 = var50;

                        int var59;
                        for(var59 = var48[var50] >>> 24; var50 < var47 && var48[var50] >>> 24 == var59; ++var50) {
                        }

                        String var52 = new String(var46, var57, var50 - var57);
                        PhysicalFont var53 = var41.getSlotFont(var59);
                        this.textOut(var52, var4, var53, var5, var30, var35, var34, var36, var38, var42, var43, var44, var45, 0.0F);
                        Rectangle2D var54 = var4.getStringBounds(var52, var5);
                        float var55 = (float)var54.getWidth();
                        var42 += var55;
                        var13.x += var55;
                        var8.transform(var13, var14);
                        var44 = var14.x;
                     }
                  } else {
                     super.drawString(var1, var2, var3, var4, var5, var6);
                  }

               }
            }
         }
      }
   }

   protected boolean printGlyphVector(GlyphVector var1, float var2, float var3) {
      if ((var1.getLayoutFlags() & 1) != 0) {
         return false;
      } else if (var1.getNumGlyphs() == 0) {
         return true;
      } else {
         AffineTransform var4 = this.getTransform();
         AffineTransform var5 = new AffineTransform(var4);
         Font var6 = var1.getFont();
         var5.concatenate(var6.getTransform());
         int var7 = var5.getType();
         boolean var8 = var7 != 32 && (var7 & 64) == 0;
         com.frojasg1.sun.awt.windows.WPrinterJob var9 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();

         try {
            var9.setTextColor((Color)this.getPaint());
         } catch (ClassCastException var59) {
            var8 = false;
         }

         if (!com.frojasg1.sun.awt.windows.WPrinterJob.shapeTextProp && var8) {
            Float var10 = new Float(var2, var3);
            Point2D var11 = var1.getGlyphPosition(0);
            var10.x += (float)var11.getX();
            var10.y += (float)var11.getY();
            Float var12 = new Float();
            if (var6.isTransformed()) {
               AffineTransform var13 = var6.getTransform();
               float var14 = (float)var13.getTranslateX();
               float var15 = (float)var13.getTranslateY();
               if ((double)Math.abs(var14) < 1.0E-5D) {
                  var14 = 0.0F;
               }

               if ((double)Math.abs(var15) < 1.0E-5D) {
                  var15 = 0.0F;
               }

               var10.x += var14;
               var10.y += var15;
            }

            var4.transform(var10, var12);
            if (this.getClip() != null) {
               this.deviceClip(this.getClip().getPathIterator(var4));
            }

            float var60 = var6.getSize2D();
            double var61 = var9.getXRes();
            double var16 = var9.getYRes();
            double var18 = var16 / 72.0D;
            int var20 = this.getPageFormat().getOrientation();
            double var21;
            if (var20 == 0 || var20 == 2) {
               var21 = var61;
               var61 = var16;
               var16 = var21;
            }

            var21 = var61 / 72.0D;
            double var23 = var16 / 72.0D;
            var5.scale(1.0D / var21, 1.0D / var23);
            Double var25 = new Double(0.0D, 1.0D);
            var5.deltaTransform(var25, var25);
            double var26 = Math.sqrt(var25.x * var25.x + var25.y * var25.y);
            float var28 = (float)((double)var60 * var26 * var18);
            Double var29 = new Double(1.0D, 0.0D);
            var5.deltaTransform(var29, var29);
            double var30 = Math.sqrt(var29.x * var29.x + var29.y * var29.y);
            float var32 = this.getAwScale(var30, var26);
            int var33 = this.getAngle(var29);
            var29 = new Double(1.0D, 0.0D);
            var4.deltaTransform(var29, var29);
            double var34 = Math.sqrt(var29.x * var29.x + var29.y * var29.y);
            var25 = new Double(0.0D, 1.0D);
            var4.deltaTransform(var25, var25);
            double var36 = Math.sqrt(var25.x * var25.x + var25.y * var25.y);
            int var38 = var1.getNumGlyphs();
            int[] var39 = var1.getGlyphCodes(0, var38, (int[])null);
            float[] var40 = var1.getGlyphPositions(0, var38, (float[])null);
            int var41 = 0;

            int var42;
            for(var42 = 0; var42 < var38; ++var42) {
               if ((var39[var42] & '\uffff') >= 65534) {
                  ++var41;
               }
            }

            int var46;
            if (var41 > 0) {
               var42 = var38 - var41;
               int[] var43 = new int[var42];
               float[] var44 = new float[var42 * 2];
               int var45 = 0;

               for(var46 = 0; var46 < var38; ++var46) {
                  if ((var39[var46] & '\uffff') < 65534) {
                     var43[var45] = var39[var46];
                     var44[var45 * 2] = var40[var46 * 2];
                     var44[var45 * 2 + 1] = var40[var46 * 2 + 1];
                     ++var45;
                  }
               }

               var38 = var42;
               var39 = var43;
               var40 = var44;
            }

            AffineTransform var62 = AffineTransform.getScaleInstance(var34, var36);
            float[] var63 = new float[var40.length];
            var62.transform(var40, 0, var63, 0, var40.length / 2);
            Font2D var64 = FontUtilities.getFont2D(var6);
            if (var64 instanceof TrueTypeFont) {
               String var65 = var64.getFamilyName((Locale)null);
               var46 = var6.getStyle() | var64.getStyle();
               if (!var9.setFont(var65, var28, var46, var33, var32)) {
                  return false;
               }

               var9.glyphsOut(var39, var12.x, var12.y, var63);
            } else {
               if (!(var64 instanceof CompositeFont)) {
                  return false;
               }

               CompositeFont var66 = (CompositeFont)var64;
               float var48 = var12.x;
               float var49 = var12.y;
               boolean var50 = false;
               int var51 = 0;

               int[] var56;
               float[] var57;
               for(boolean var52 = false; var51 < var38; var9.glyphsOut(var56, var48, var49, var57)) {
                  int var67 = var51;

                  int var68;
                  for(var68 = var39[var51] >>> 24; var51 < var38 && var39[var51] >>> 24 == var68; ++var51) {
                  }

                  PhysicalFont var53 = var66.getSlotFont(var68);
                  if (!(var53 instanceof TrueTypeFont)) {
                     return false;
                  }

                  String var54 = var53.getFamilyName((Locale)null);
                  int var55 = var6.getStyle() | var53.getStyle();
                  if (!var9.setFont(var54, var28, var55, var33, var32)) {
                     return false;
                  }

                  var56 = Arrays.copyOfRange(var39, var67, var51);
                  var57 = Arrays.copyOfRange(var63, var67 * 2, var51 * 2);
                  if (var67 != 0) {
                     Float var58 = new Float(var2 + var40[var67 * 2], var3 + var40[var67 * 2 + 1]);
                     var4.transform(var58, var58);
                     var48 = var58.x;
                     var49 = var58.y;
                  }
               }
            }

            return true;
         } else {
            return false;
         }
      }
   }

   private void textOut(String var1, Font var2, PhysicalFont var3, FontRenderContext var4, float var5, int var6, float var7, double var8, double var10, float var12, float var13, float var14, float var15, float var16) {
      String var17 = var3.getFamilyName((Locale)null);
      int var18 = var2.getStyle() | var3.getStyle();
      com.frojasg1.sun.awt.windows.WPrinterJob var19 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
      boolean var20 = var19.setFont(var17, var5, var18, var6, var7);
      if (!var20) {
         super.drawString(var1, var12, var13, var2, var4, var16);
      } else {
         float[] var21 = null;
         if (!this.okGDIMetrics(var1, var2, var4, var8)) {
            var1 = var19.removeControlChars(var1);
            char[] var22 = var1.toCharArray();
            int var23 = var22.length;
            GlyphVector var24 = null;
            if (!FontUtilities.isComplexText(var22, 0, var23)) {
               var24 = var2.createGlyphVector(var4, var1);
            }

            if (var24 == null) {
               super.drawString(var1, var12, var13, var2, var4, var16);
               return;
            }

            var21 = var24.getGlyphPositions(0, var23, (float[])null);
            Point2D var25 = var24.getGlyphPosition(var24.getNumGlyphs());
            AffineTransform var26 = AffineTransform.getScaleInstance(var8, var10);
            float[] var27 = new float[var21.length];
            var26.transform(var21, 0, var27, 0, var21.length / 2);
            var21 = var27;
         }

         var19.textOut(var1, var14, var15, var21);
      }
   }

   private boolean okGDIMetrics(String var1, Font var2, FontRenderContext var3, double var4) {
      Rectangle2D var6 = var2.getStringBounds(var1, var3);
      double var7 = var6.getWidth();
      var7 = (double)Math.round(var7 * var4);
      int var9 = ((com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob()).getGDIAdvance(var1);
      if (var7 > 0.0D && var9 > 0) {
         double var10 = Math.abs((double)var9 - var7);
         double var12 = (double)var9 / var7;
         if (var12 < 1.0D) {
            var12 = 1.0D / var12;
         }

         return var10 <= 1.0D || var12 < 1.01D;
      } else {
         return true;
      }
   }

   protected boolean drawImageToPlatform(Image var1, AffineTransform var2, Color var3, int var4, int var5, int var6, int var7, boolean var8) {
      BufferedImage var9 = this.getBufferedImage(var1);
      if (var9 == null) {
         return true;
      } else {
         com.frojasg1.sun.awt.windows.WPrinterJob var10 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
         AffineTransform var11 = this.getTransform();
         if (var2 == null) {
            var2 = new AffineTransform();
         }

         var11.concatenate(var2);
         double[] var12 = new double[6];
         var11.getMatrix(var12);
         Float var13 = new Float(1.0F, 0.0F);
         Float var14 = new Float(0.0F, 1.0F);
         var11.deltaTransform(var13, var13);
         var11.deltaTransform(var14, var14);
         Float var15 = new Float(0.0F, 0.0F);
         double var16 = var13.distance(var15);
         double var18 = var14.distance(var15);
         double var20 = var10.getXRes();
         double var22 = var10.getYRes();
         double var24 = var20 / 72.0D;
         double var26 = var22 / 72.0D;
         int var28 = var11.getType();
         boolean var29 = (var28 & 48) != 0;
         if (var29) {
            if (var16 > var24) {
               var16 = var24;
            }

            if (var18 > var26) {
               var18 = var26;
            }
         }

         if (var16 != 0.0D && var18 != 0.0D) {
            AffineTransform var30 = new AffineTransform(var12[0] / var16, var12[1] / var18, var12[2] / var16, var12[3] / var18, var12[4] / var16, var12[5] / var18);
            java.awt.geom.Rectangle2D.Float var31 = new java.awt.geom.Rectangle2D.Float((float)var4, (float)var5, (float)var6, (float)var7);
            Shape var32 = var30.createTransformedShape(var31);
            Rectangle2D var33 = var32.getBounds2D();
            var33.setRect(var33.getX(), var33.getY(), var33.getWidth() + 0.001D, var33.getHeight() + 0.001D);
            int var34 = (int)var33.getWidth();
            int var35 = (int)var33.getHeight();
            if (var34 > 0 && var35 > 0) {
               boolean var36 = true;
               if (!var8 && this.hasTransparentPixels(var9)) {
                  var36 = false;
                  if (this.isBitmaskTransparency(var9)) {
                     if (var3 == null) {
                        if (this.drawBitmaskImage(var9, var2, var3, var4, var5, var6, var7)) {
                           return true;
                        }
                     } else if (var3.getTransparency() == 1) {
                        var36 = true;
                     }
                  }

                  if (!this.canDoRedraws()) {
                     var36 = true;
                  }
               } else {
                  var3 = null;
               }

               if ((var4 + var6 > var9.getWidth((ImageObserver)null) || var5 + var7 > var9.getHeight((ImageObserver)null)) && this.canDoRedraws()) {
                  var36 = false;
               }

               int var43;
               int var60;
               int var61;
               if (!var36) {
                  var11.getMatrix(var12);
                  new AffineTransform(var12[0] / var24, var12[1] / var26, var12[2] / var24, var12[3] / var26, var12[4] / var24, var12[5] / var26);
                  java.awt.geom.Rectangle2D.Float var58 = new java.awt.geom.Rectangle2D.Float((float)var4, (float)var5, (float)var6, (float)var7);
                  Shape var57 = var11.createTransformedShape(var58);
                  Rectangle2D var59 = var57.getBounds2D();
                  var59.setRect(var59.getX(), var59.getY(), var59.getWidth() + 0.001D, var59.getHeight() + 0.001D);
                  var60 = (int)var59.getWidth();
                  var61 = (int)var59.getHeight();
                  var43 = var60 * var61 * 3;
                  int var63 = 8388608;
                  double var65 = var20 < var22 ? var20 : var22;
                  int var66 = (int)var65;
                  double var67 = 1.0D;
                  double var71 = (double)var60 / (double)var34;
                  double var52 = (double)var61 / (double)var35;
                  double var54 = var71 > var52 ? var52 : var71;
                  int var56 = (int)((double)var66 / var54);
                  if (var56 < 72) {
                     var56 = 72;
                  }

                  while(var43 > var63 && var66 > var56) {
                     var67 *= 2.0D;
                     var66 /= 2;
                     var43 /= 4;
                  }

                  if (var66 < var56) {
                     var67 = var65 / (double)var56;
                  }

                  var59.setRect(var59.getX() / var67, var59.getY() / var67, var59.getWidth() / var67, var59.getHeight() / var67);
                  var10.saveState(this.getTransform(), this.getClip(), var59, var67, var67);
                  return true;
               }

               int var37 = 5;
               IndexColorModel var38 = null;
               ColorModel var39 = var9.getColorModel();
               int var40 = var9.getType();
               if (var39 instanceof IndexColorModel && var39.getPixelSize() <= 8 && (var40 == 12 || var40 == 13)) {
                  var38 = (IndexColorModel)var39;
                  var37 = var40;
                  if (var40 == 12 && var39.getPixelSize() == 2) {
                     int[] var41 = new int[16];
                     var38.getRGBs(var41);
                     boolean var42 = var38.getTransparency() != 1;
                     var43 = var38.getTransparentPixel();
                     var38 = new IndexColorModel(4, 16, var41, 0, var42, var43, 0);
                  }
               }

               var60 = (int)var33.getWidth();
               var61 = (int)var33.getHeight();
               BufferedImage var62 = null;
               boolean var44 = true;
               if (var44) {
                  if (var38 == null) {
                     var62 = new BufferedImage(var60, var61, var37);
                  } else {
                     var62 = new BufferedImage(var60, var61, var37, var38);
                  }

                  Graphics2D var45 = var62.createGraphics();
                  var45.clipRect(0, 0, var62.getWidth(), var62.getHeight());
                  var45.translate(-var33.getX(), -var33.getY());
                  var45.transform(var30);
                  if (var3 == null) {
                     var3 = Color.white;
                  }

                  var45.drawImage(var9, var4, var5, var4 + var6, var5 + var7, var4, var5, var4 + var6, var5 + var7, var3, (ImageObserver)null);
                  var45.dispose();
               } else {
                  var62 = var9;
               }

               java.awt.geom.Rectangle2D.Float var64 = new java.awt.geom.Rectangle2D.Float((float)(var33.getX() * var16), (float)(var33.getY() * var18), (float)(var33.getWidth() * var16), (float)(var33.getHeight() * var18));
               WritableRaster var46 = var62.getRaster();
               byte[] var47;
               if (var46 instanceof ByteComponentRaster) {
                  var47 = ((ByteComponentRaster)var46).getDataStorage();
               } else {
                  if (!(var46 instanceof BytePackedRaster)) {
                     return false;
                  }

                  var47 = ((BytePackedRaster)var46).getDataStorage();
               }

               int var48 = 24;
               SampleModel var49 = var62.getSampleModel();
               if (var49 instanceof ComponentSampleModel) {
                  ComponentSampleModel var50 = (ComponentSampleModel)var49;
                  var48 = var50.getPixelStride() * 8;
               } else if (var49 instanceof MultiPixelPackedSampleModel) {
                  MultiPixelPackedSampleModel var68 = (MultiPixelPackedSampleModel)var49;
                  var48 = var68.getPixelBitStride();
               } else if (var38 != null) {
                  int var69 = var62.getWidth();
                  int var51 = var62.getHeight();
                  if (var69 > 0 && var51 > 0) {
                     var48 = var47.length * 8 / var69 / var51;
                  }
               }

               Shape var70 = this.getClip();
               this.clip(var2.createTransformedShape(var31));
               this.deviceClip(this.getClip().getPathIterator(this.getTransform()));
               var10.drawDIBImage(var47, var64.x, var64.y, (float)Math.rint((double)var64.width + 0.5D), (float)Math.rint((double)var64.height + 0.5D), 0.0F, 0.0F, (float)var62.getWidth(), (float)var62.getHeight(), var48, var38);
               this.setClip(var70);
            }
         }

         return true;
      }
   }

   public void redrawRegion(Rectangle2D var1, double var2, double var4, Shape var6, AffineTransform var7) throws PrinterException {
      com.frojasg1.sun.awt.windows.WPrinterJob var8 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
      Printable var9 = this.getPrintable();
      PageFormat var10 = this.getPageFormat();
      int var11 = this.getPageIndex();
      BufferedImage var12 = new BufferedImage((int)var1.getWidth(), (int)var1.getHeight(), 5);
      Graphics2D var13 = var12.createGraphics();
      ProxyGraphics2D var14 = new ProxyGraphics2D(var13, var8);
      var14.setColor(Color.white);
      var14.fillRect(0, 0, var12.getWidth(), var12.getHeight());
      var14.clipRect(0, 0, var12.getWidth(), var12.getHeight());
      var14.translate(-var1.getX(), -var1.getY());
      float var15 = (float)(var8.getXRes() / var2);
      float var16 = (float)(var8.getYRes() / var4);
      var14.scale((double)(var15 / 72.0F), (double)(var16 / 72.0F));
      var14.translate(-var8.getPhysicalPrintableX(var10.getPaper()) / var8.getXRes() * 72.0D, -var8.getPhysicalPrintableY(var10.getPaper()) / var8.getYRes() * 72.0D);
      var14.transform(new AffineTransform(this.getPageFormat().getMatrix()));
      var14.setPaint(Color.black);
      var9.print(var14, var10, var11);
      var13.dispose();
      if (var6 != null) {
         this.deviceClip(var6.getPathIterator(var7));
      }

      java.awt.geom.Rectangle2D.Float var17 = new java.awt.geom.Rectangle2D.Float((float)(var1.getX() * var2), (float)(var1.getY() * var4), (float)(var1.getWidth() * var2), (float)(var1.getHeight() * var4));
      ByteComponentRaster var18 = (ByteComponentRaster)var12.getRaster();
      var8.drawImage3ByteBGR(var18.getDataStorage(), var17.x, var17.y, var17.width, var17.height, 0.0F, 0.0F, (float)var12.getWidth(), (float)var12.getHeight());
   }

   protected void deviceFill(PathIterator var1, Color var2) {
      com.frojasg1.sun.awt.windows.WPrinterJob var3 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
      this.convertToWPath(var1);
      var3.selectSolidBrush(var2);
      var3.fillPath();
   }

   protected void deviceClip(PathIterator var1) {
      com.frojasg1.sun.awt.windows.WPrinterJob var2 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
      this.convertToWPath(var1);
      var2.selectClipPath();
   }

   protected void deviceFrameRect(int var1, int var2, int var3, int var4, Color var5) {
      AffineTransform var6 = this.getTransform();
      int var7 = var6.getType();
      boolean var8 = (var7 & 48) != 0;
      if (var8) {
         this.draw(new java.awt.geom.Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
      } else {
         Stroke var9 = this.getStroke();
         if (var9 instanceof BasicStroke) {
            BasicStroke var10 = (BasicStroke)var9;
            int var11 = var10.getEndCap();
            int var12 = var10.getLineJoin();
            if (var11 == 2 && var12 == 0 && var10.getMiterLimit() == 10.0F) {
               float var13 = var10.getLineWidth();
               Float var14 = new Float(var13, var13);
               var6.deltaTransform(var14, var14);
               float var15 = Math.min(Math.abs(var14.x), Math.abs(var14.y));
               Float var16 = new Float((float)var1, (float)var2);
               var6.transform(var16, var16);
               Float var17 = new Float((float)(var1 + var3), (float)(var2 + var4));
               var6.transform(var17, var17);
               float var18 = (float)(var17.getX() - var16.getX());
               float var19 = (float)(var17.getY() - var16.getY());
               com.frojasg1.sun.awt.windows.WPrinterJob var20 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
               if (var20.selectStylePen(var11, var12, var15, var5)) {
                  var20.frameRect((float)var16.getX(), (float)var16.getY(), var18, var19);
               } else {
                  double var21 = Math.min(var20.getXRes(), var20.getYRes());
                  if ((double)var15 / var21 < 0.014000000432133675D) {
                     var20.selectPen(var15, var5);
                     var20.frameRect((float)var16.getX(), (float)var16.getY(), var18, var19);
                  } else {
                     this.draw(new java.awt.geom.Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
                  }
               }
            } else {
               this.draw(new java.awt.geom.Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
            }
         }

      }
   }

   protected void deviceFillRect(int var1, int var2, int var3, int var4, Color var5) {
      AffineTransform var6 = this.getTransform();
      int var7 = var6.getType();
      boolean var8 = (var7 & 48) != 0;
      if (var8) {
         this.fill(new java.awt.geom.Rectangle2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
      } else {
         Float var9 = new Float((float)var1, (float)var2);
         var6.transform(var9, var9);
         Float var10 = new Float((float)(var1 + var3), (float)(var2 + var4));
         var6.transform(var10, var10);
         float var11 = (float)(var10.getX() - var9.getX());
         float var12 = (float)(var10.getY() - var9.getY());
         com.frojasg1.sun.awt.windows.WPrinterJob var13 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
         var13.fillRect((float)var9.getX(), (float)var9.getY(), var11, var12, var5);
      }
   }

   protected void deviceDrawLine(int var1, int var2, int var3, int var4, Color var5) {
      Stroke var6 = this.getStroke();
      if (var6 instanceof BasicStroke) {
         BasicStroke var7 = (BasicStroke)var6;
         if (var7.getDashArray() != null) {
            this.draw(new java.awt.geom.Line2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
            return;
         }

         float var8 = var7.getLineWidth();
         Float var9 = new Float(var8, var8);
         AffineTransform var10 = this.getTransform();
         var10.deltaTransform(var9, var9);
         float var11 = Math.min(Math.abs(var9.x), Math.abs(var9.y));
         Float var12 = new Float((float)var1, (float)var2);
         var10.transform(var12, var12);
         Float var13 = new Float((float)var3, (float)var4);
         var10.transform(var13, var13);
         int var14 = var7.getEndCap();
         int var15 = var7.getLineJoin();
         if (var13.getX() == var12.getX() && var13.getY() == var12.getY()) {
            var14 = 1;
         }

         com.frojasg1.sun.awt.windows.WPrinterJob var16 = (com.frojasg1.sun.awt.windows.WPrinterJob)this.getPrinterJob();
         if (var16.selectStylePen(var14, var15, var11, var5)) {
            var16.moveTo((float)var12.getX(), (float)var12.getY());
            var16.lineTo((float)var13.getX(), (float)var13.getY());
         } else {
            double var17 = Math.min(var16.getXRes(), var16.getYRes());
            if (var14 != 1 && (var1 != var3 && var2 != var4 || !((double)var11 / var17 < 0.014000000432133675D))) {
               this.draw(new java.awt.geom.Line2D.Float((float)var1, (float)var2, (float)var3, (float)var4));
            } else {
               var16.selectPen(var11, var5);
               var16.moveTo((float)var12.getX(), (float)var12.getY());
               var16.lineTo((float)var13.getX(), (float)var13.getY());
            }
         }
      }

   }

   private void convertToWPath(PathIterator var1) {
      float[] var2 = new float[6];
      com.frojasg1.sun.awt.windows.WPrinterJob var4 = (WPrinterJob)this.getPrinterJob();
      byte var5;
      if (var1.getWindingRule() == 0) {
         var5 = 1;
      } else {
         var5 = 2;
      }

      var4.setPolyFillMode(var5);
      var4.beginPath();

      for(; !var1.isDone(); var1.next()) {
         int var3 = var1.currentSegment(var2);
         switch(var3) {
         case 0:
            var4.moveTo(var2[0], var2[1]);
            break;
         case 1:
            var4.lineTo(var2[0], var2[1]);
            break;
         case 2:
            int var6 = var4.getPenX();
            int var7 = var4.getPenY();
            float var8 = (float)var6 + (var2[0] - (float)var6) * 2.0F / 3.0F;
            float var9 = (float)var7 + (var2[1] - (float)var7) * 2.0F / 3.0F;
            float var10 = var2[2] - (var2[2] - var2[0]) * 2.0F / 3.0F;
            float var11 = var2[3] - (var2[3] - var2[1]) * 2.0F / 3.0F;
            var4.polyBezierTo(var8, var9, var10, var11, var2[2], var2[3]);
            break;
         case 3:
            var4.polyBezierTo(var2[0], var2[1], var2[2], var2[3], var2[4], var2[5]);
            break;
         case 4:
            var4.closeFigure();
         }
      }

      var4.endPath();
   }

   static {
      String var0 = (String)AccessController.doPrivileged(new GetPropertyAction("sun.java2d.print.enableGDITextLayout"));
      if (var0 != null) {
         useGDITextLayout = Boolean.getBoolean(var0);
         if (!useGDITextLayout && var0.equalsIgnoreCase("prefer")) {
            useGDITextLayout = true;
            preferGDITextLayout = true;
         }
      }

   }
}
