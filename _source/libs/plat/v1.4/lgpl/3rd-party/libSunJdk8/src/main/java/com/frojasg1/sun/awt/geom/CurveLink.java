package com.frojasg1.sun.awt.geom;

import com.frojasg1.sun.awt.geom.Curve;
import com.frojasg1.sun.awt.geom.Order0;

final class CurveLink {
   com.frojasg1.sun.awt.geom.Curve curve;
   double ytop;
   double ybot;
   int etag;
   CurveLink next;

   public CurveLink(com.frojasg1.sun.awt.geom.Curve var1, double var2, double var4, int var6) {
      this.curve = var1;
      this.ytop = var2;
      this.ybot = var4;
      this.etag = var6;
      if (this.ytop < var1.getYTop() || this.ybot > var1.getYBot()) {
         throw new InternalError("bad curvelink [" + this.ytop + "=>" + this.ybot + "] for " + var1);
      }
   }

   public boolean absorb(CurveLink var1) {
      return this.absorb(var1.curve, var1.ytop, var1.ybot, var1.etag);
   }

   public boolean absorb(com.frojasg1.sun.awt.geom.Curve var1, double var2, double var4, int var6) {
      if (this.curve == var1 && this.etag == var6 && !(this.ybot < var2) && !(this.ytop > var4)) {
         if (!(var2 < var1.getYTop()) && !(var4 > var1.getYBot())) {
            this.ytop = Math.min(this.ytop, var2);
            this.ybot = Math.max(this.ybot, var4);
            return true;
         } else {
            throw new InternalError("bad curvelink [" + var2 + "=>" + var4 + "] for " + var1);
         }
      } else {
         return false;
      }
   }

   public boolean isEmpty() {
      return this.ytop == this.ybot;
   }

   public com.frojasg1.sun.awt.geom.Curve getCurve() {
      return this.curve;
   }

   public com.frojasg1.sun.awt.geom.Curve getSubCurve() {
      return this.ytop == this.curve.getYTop() && this.ybot == this.curve.getYBot() ? this.curve.getWithDirection(this.etag) : this.curve.getSubCurve(this.ytop, this.ybot, this.etag);
   }

   public Curve getMoveto() {
      return new com.frojasg1.sun.awt.geom.Order0(this.getXTop(), this.getYTop());
   }

   public double getXTop() {
      return this.curve.XforY(this.ytop);
   }

   public double getYTop() {
      return this.ytop;
   }

   public double getXBot() {
      return this.curve.XforY(this.ybot);
   }

   public double getYBot() {
      return this.ybot;
   }

   public double getX() {
      return this.curve.XforY(this.ytop);
   }

   public int getEdgeTag() {
      return this.etag;
   }

   public void setNext(CurveLink var1) {
      this.next = var1;
   }

   public CurveLink getNext() {
      return this.next;
   }
}
