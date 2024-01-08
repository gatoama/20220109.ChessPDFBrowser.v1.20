package com.frojasg1.sun.font;

import com.frojasg1.sun.font.AttributeValues;
import com.frojasg1.sun.font.GlyphLayout;
import com.frojasg1.sun.font.StandardGlyphVector;
import com.frojasg1.sun.font.TextLabel;
import com.frojasg1.sun.font.TextSource;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Float;

public class TextSourceLabel extends TextLabel {
   com.frojasg1.sun.font.TextSource source;
   Rectangle2D lb;
   Rectangle2D ab;
   Rectangle2D vb;
   Rectangle2D ib;
   GlyphVector gv;

   public TextSourceLabel(com.frojasg1.sun.font.TextSource var1) {
      this(var1, (Rectangle2D)null, (Rectangle2D)null, (GlyphVector)null);
   }

   public TextSourceLabel(com.frojasg1.sun.font.TextSource var1, Rectangle2D var2, Rectangle2D var3, GlyphVector var4) {
      this.source = var1;
      this.lb = var2;
      this.ab = var3;
      this.gv = var4;
   }

   public TextSource getSource() {
      return this.source;
   }

   public final Rectangle2D getLogicalBounds(float var1, float var2) {
      if (this.lb == null) {
         this.lb = this.createLogicalBounds();
      }

      return new Float((float)(this.lb.getX() + (double)var1), (float)(this.lb.getY() + (double)var2), (float)this.lb.getWidth(), (float)this.lb.getHeight());
   }

   public final Rectangle2D getVisualBounds(float var1, float var2) {
      if (this.vb == null) {
         this.vb = this.createVisualBounds();
      }

      return new Float((float)(this.vb.getX() + (double)var1), (float)(this.vb.getY() + (double)var2), (float)this.vb.getWidth(), (float)this.vb.getHeight());
   }

   public final Rectangle2D getAlignBounds(float var1, float var2) {
      if (this.ab == null) {
         this.ab = this.createAlignBounds();
      }

      return new Float((float)(this.ab.getX() + (double)var1), (float)(this.ab.getY() + (double)var2), (float)this.ab.getWidth(), (float)this.ab.getHeight());
   }

   public Rectangle2D getItalicBounds(float var1, float var2) {
      if (this.ib == null) {
         this.ib = this.createItalicBounds();
      }

      return new Float((float)(this.ib.getX() + (double)var1), (float)(this.ib.getY() + (double)var2), (float)this.ib.getWidth(), (float)this.ib.getHeight());
   }

   public Rectangle getPixelBounds(FontRenderContext var1, float var2, float var3) {
      return this.getGV().getPixelBounds(var1, var2, var3);
   }

   public AffineTransform getBaselineTransform() {
      Font var1 = this.source.getFont();
      return var1.hasLayoutAttributes() ? AttributeValues.getBaselineTransform(var1.getAttributes()) : null;
   }

   public Shape getOutline(float var1, float var2) {
      return this.getGV().getOutline(var1, var2);
   }

   public void draw(Graphics2D var1, float var2, float var3) {
      var1.drawGlyphVector(this.getGV(), var2, var3);
   }

   protected Rectangle2D createLogicalBounds() {
      return this.getGV().getLogicalBounds();
   }

   protected Rectangle2D createVisualBounds() {
      return this.getGV().getVisualBounds();
   }

   protected Rectangle2D createItalicBounds() {
      return this.getGV().getLogicalBounds();
   }

   protected Rectangle2D createAlignBounds() {
      return this.createLogicalBounds();
   }

   private final GlyphVector getGV() {
      if (this.gv == null) {
         this.gv = this.createGV();
      }

      return this.gv;
   }

   protected GlyphVector createGV() {
      Font var1 = this.source.getFont();
      FontRenderContext var2 = this.source.getFRC();
      int var3 = this.source.getLayoutFlags();
      char[] var4 = this.source.getChars();
      int var5 = this.source.getStart();
      int var6 = this.source.getLength();
      com.frojasg1.sun.font.GlyphLayout var7 = com.frojasg1.sun.font.GlyphLayout.get((com.frojasg1.sun.font.GlyphLayout.LayoutEngineFactory)null);
      com.frojasg1.sun.font.StandardGlyphVector var8 = var7.layout(var1, var2, var4, var5, var6, var3, (StandardGlyphVector)null);
      GlyphLayout.done(var7);
      return var8;
   }
}
