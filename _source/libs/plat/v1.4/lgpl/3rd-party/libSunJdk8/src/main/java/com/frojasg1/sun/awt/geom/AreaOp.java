package com.frojasg1.sun.awt.geom;

import com.frojasg1.sun.awt.geom.ChainEnd;
import com.frojasg1.sun.awt.geom.Curve;
import com.frojasg1.sun.awt.geom.CurveLink;
import com.frojasg1.sun.awt.geom.Edge;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;

public abstract class AreaOp {
   public static final int CTAG_LEFT = 0;
   public static final int CTAG_RIGHT = 1;
   public static final int ETAG_IGNORE = 0;
   public static final int ETAG_ENTER = 1;
   public static final int ETAG_EXIT = -1;
   public static final int RSTAG_INSIDE = 1;
   public static final int RSTAG_OUTSIDE = -1;
   private static Comparator YXTopComparator = new Comparator() {
      public int compare(Object var1, Object var2) {
         com.frojasg1.sun.awt.geom.Curve var3 = ((com.frojasg1.sun.awt.geom.Edge)var1).getCurve();
         com.frojasg1.sun.awt.geom.Curve var4 = ((com.frojasg1.sun.awt.geom.Edge)var2).getCurve();
         double var5;
         double var7;
         if ((var5 = var3.getYTop()) == (var7 = var4.getYTop()) && (var5 = var3.getXTop()) == (var7 = var4.getXTop())) {
            return 0;
         } else {
            return var5 < var7 ? -1 : 1;
         }
      }
   };
   private static com.frojasg1.sun.awt.geom.CurveLink[] EmptyLinkList = new com.frojasg1.sun.awt.geom.CurveLink[2];
   private static com.frojasg1.sun.awt.geom.ChainEnd[] EmptyChainList = new com.frojasg1.sun.awt.geom.ChainEnd[2];

   private AreaOp() {
   }

   public abstract void newRow();

   public abstract int classify(com.frojasg1.sun.awt.geom.Edge var1);

   public abstract int getState();

   public Vector calculate(Vector var1, Vector var2) {
      Vector var3 = new Vector();
      addEdges(var3, var1, 0);
      addEdges(var3, var2, 1);
      var3 = this.pruneEdges(var3);
      return var3;
   }

   private static void addEdges(Vector var0, Vector var1, int var2) {
      Enumeration var3 = var1.elements();

      while(var3.hasMoreElements()) {
         com.frojasg1.sun.awt.geom.Curve var4 = (Curve)var3.nextElement();
         if (var4.getOrder() > 0) {
            var0.add(new com.frojasg1.sun.awt.geom.Edge(var4, var2));
         }
      }

   }

   private Vector pruneEdges(Vector var1) {
      int var2 = var1.size();
      if (var2 < 2) {
         return var1;
      } else {
         com.frojasg1.sun.awt.geom.Edge[] var3 = (com.frojasg1.sun.awt.geom.Edge[])((com.frojasg1.sun.awt.geom.Edge[])var1.toArray(new com.frojasg1.sun.awt.geom.Edge[var2]));
         Arrays.sort(var3, YXTopComparator);
         int var5 = 0;
         int var6 = 0;
         boolean var7 = false;
         boolean var8 = false;
         double[] var9 = new double[2];
         Vector var10 = new Vector();
         Vector var11 = new Vector();

         double var33;
         for(Vector var12 = new Vector(); var5 < var2; var9[0] = var33) {
            double var13 = var9[0];

            com.frojasg1.sun.awt.geom.Edge var4;
            int var27;
            int var28;
            for(var27 = var28 = var6 - 1; var27 >= var5; --var27) {
               var4 = var3[var27];
               if (var4.getCurve().getYBot() > var13) {
                  if (var28 > var27) {
                     var3[var28] = var4;
                  }

                  --var28;
               }
            }

            var5 = var28 + 1;
            if (var5 >= var6) {
               if (var6 >= var2) {
                  break;
               }

               var13 = var3[var6].getCurve().getYTop();
               if (var13 > var9[0]) {
                  finalizeSubCurves(var10, var11);
               }

               var9[0] = var13;
            }

            while(var6 < var2) {
               var4 = var3[var6];
               if (var4.getCurve().getYTop() > var13) {
                  break;
               }

               ++var6;
            }

            var9[1] = var3[var5].getCurve().getYBot();
            if (var6 < var2) {
               var13 = var3[var6].getCurve().getYTop();
               if (var9[1] > var13) {
                  var9[1] = var13;
               }
            }

            int var15 = 1;

            for(var27 = var5; var27 < var6; ++var27) {
               var4 = var3[var27];
               var4.setEquivalence(0);

               for(var28 = var27; var28 > var5; --var28) {
                  com.frojasg1.sun.awt.geom.Edge var16 = var3[var28 - 1];
                  int var17 = var4.compareTo(var16, var9);
                  if (var9[1] <= var9[0]) {
                     throw new InternalError("backstepping to " + var9[1] + " from " + var9[0]);
                  }

                  if (var17 >= 0) {
                     if (var17 == 0) {
                        int var18 = var16.getEquivalence();
                        if (var18 == 0) {
                           var18 = var15++;
                           var16.setEquivalence(var18);
                        }

                        var4.setEquivalence(var18);
                     }
                     break;
                  }

                  var3[var28] = var16;
               }

               var3[var28] = var4;
            }

            this.newRow();
            double var31 = var9[0];
            var33 = var9[1];

            int var20;
            for(var27 = var5; var27 < var6; ++var27) {
               var4 = var3[var27];
               int var21 = var4.getEquivalence();
               if (var21 == 0) {
                  var20 = this.classify(var4);
               } else {
                  int var22 = this.getState();
                  var20 = var22 == 1 ? -1 : 1;
                  com.frojasg1.sun.awt.geom.Edge var23 = null;
                  com.frojasg1.sun.awt.geom.Edge var24 = var4;
                  double var25 = var33;

                  do {
                     this.classify(var4);
                     if (var23 == null && var4.isActiveFor(var31, var20)) {
                        var23 = var4;
                     }

                     var13 = var4.getCurve().getYBot();
                     if (var13 > var25) {
                        var24 = var4;
                        var25 = var13;
                     }

                     ++var27;
                  } while(var27 < var6 && (var4 = var3[var27]).getEquivalence() == var21);

                  --var27;
                  if (this.getState() == var22) {
                     var20 = 0;
                  } else {
                     var4 = var23 != null ? var23 : var24;
                  }
               }

               if (var20 != 0) {
                  var4.record(var33, var20);
                  var12.add(new com.frojasg1.sun.awt.geom.CurveLink(var4.getCurve(), var31, var33, var20));
               }
            }

            if (this.getState() != -1) {
               System.out.println("Still inside at end of active edge list!");
               System.out.println("num curves = " + (var6 - var5));
               System.out.println("num links = " + var12.size());
               System.out.println("y top = " + var9[0]);
               if (var6 < var2) {
                  System.out.println("y top of next curve = " + var3[var6].getCurve().getYTop());
               } else {
                  System.out.println("no more curves");
               }

               for(var27 = var5; var27 < var6; ++var27) {
                  var4 = var3[var27];
                  System.out.println(var4);
                  var20 = var4.getEquivalence();
                  if (var20 != 0) {
                     System.out.println("  was equal to " + var20 + "...");
                  }
               }
            }

            resolveLinks(var10, var11, var12);
            var12.clear();
         }

         finalizeSubCurves(var10, var11);
         Vector var29 = new Vector();
         Enumeration var14 = var10.elements();

         while(var14.hasMoreElements()) {
            com.frojasg1.sun.awt.geom.CurveLink var30 = (com.frojasg1.sun.awt.geom.CurveLink)var14.nextElement();
            var29.add(var30.getMoveto());
            com.frojasg1.sun.awt.geom.CurveLink var32 = var30;

            while((var32 = var32.getNext()) != null) {
               if (!var30.absorb(var32)) {
                  var29.add(var30.getSubCurve());
                  var30 = var32;
               }
            }

            var29.add(var30.getSubCurve());
         }

         return var29;
      }
   }

   public static void finalizeSubCurves(Vector var0, Vector var1) {
      int var2 = var1.size();
      if (var2 != 0) {
         if ((var2 & 1) != 0) {
            throw new InternalError("Odd number of chains!");
         } else {
            com.frojasg1.sun.awt.geom.ChainEnd[] var3 = new com.frojasg1.sun.awt.geom.ChainEnd[var2];
            var1.toArray(var3);

            for(int var4 = 1; var4 < var2; var4 += 2) {
               com.frojasg1.sun.awt.geom.ChainEnd var5 = var3[var4 - 1];
               com.frojasg1.sun.awt.geom.ChainEnd var6 = var3[var4];
               com.frojasg1.sun.awt.geom.CurveLink var7 = var5.linkTo(var6);
               if (var7 != null) {
                  var0.add(var7);
               }
            }

            var1.clear();
         }
      }
   }

   public static void resolveLinks(Vector var0, Vector var1, Vector var2) {
      int var3 = var2.size();
      com.frojasg1.sun.awt.geom.CurveLink[] var4;
      if (var3 == 0) {
         var4 = EmptyLinkList;
      } else {
         if ((var3 & 1) != 0) {
            throw new InternalError("Odd number of new curves!");
         }

         var4 = new com.frojasg1.sun.awt.geom.CurveLink[var3 + 2];
         var2.toArray(var4);
      }

      int var5 = var1.size();
      com.frojasg1.sun.awt.geom.ChainEnd[] var6;
      if (var5 == 0) {
         var6 = EmptyChainList;
      } else {
         if ((var5 & 1) != 0) {
            throw new InternalError("Odd number of chains!");
         }

         var6 = new com.frojasg1.sun.awt.geom.ChainEnd[var5 + 2];
         var1.toArray(var6);
      }

      int var7 = 0;
      int var8 = 0;
      var1.clear();
      com.frojasg1.sun.awt.geom.ChainEnd var9 = var6[0];
      com.frojasg1.sun.awt.geom.ChainEnd var10 = var6[1];
      com.frojasg1.sun.awt.geom.CurveLink var11 = var4[0];
      com.frojasg1.sun.awt.geom.CurveLink var12 = var4[1];

      while(var9 != null || var11 != null) {
         boolean var13 = var11 == null;
         boolean var14 = var9 == null;
         if (!var13 && !var14) {
            var13 = (var7 & 1) == 0 && var9.getX() == var10.getX();
            var14 = (var8 & 1) == 0 && var11.getX() == var12.getX();
            if (!var13 && !var14) {
               double var15 = var9.getX();
               double var17 = var11.getX();
               var13 = var10 != null && var15 < var17 && obstructs(var10.getX(), var17, var7);
               var14 = var12 != null && var17 < var15 && obstructs(var12.getX(), var15, var8);
            }
         }

         if (var13) {
            com.frojasg1.sun.awt.geom.CurveLink var19 = var9.linkTo(var10);
            if (var19 != null) {
               var0.add(var19);
            }

            var7 += 2;
            var9 = var6[var7];
            var10 = var6[var7 + 1];
         }

         if (var14) {
            com.frojasg1.sun.awt.geom.ChainEnd var20 = new com.frojasg1.sun.awt.geom.ChainEnd(var11, (com.frojasg1.sun.awt.geom.ChainEnd)null);
            com.frojasg1.sun.awt.geom.ChainEnd var16 = new com.frojasg1.sun.awt.geom.ChainEnd(var12, var20);
            var20.setOtherEnd(var16);
            var1.add(var20);
            var1.add(var16);
            var8 += 2;
            var11 = var4[var8];
            var12 = var4[var8 + 1];
         }

         if (!var13 && !var14) {
            var9.addLink(var11);
            var1.add(var9);
            ++var7;
            var9 = var10;
            var10 = var6[var7 + 1];
            ++var8;
            var11 = var12;
            var12 = var4[var8 + 1];
         }
      }

      if ((var1.size() & 1) != 0) {
         System.out.println("Odd number of chains!");
      }

   }

   public static boolean obstructs(double var0, double var2, int var4) {
      return (var4 & 1) == 0 ? var0 <= var2 : var0 < var2;
   }

   public static class AddOp extends AreaOp.CAGOp {
      public AddOp() {
      }

      public boolean newClassification(boolean var1, boolean var2) {
         return var1 || var2;
      }
   }

   public abstract static class CAGOp extends AreaOp {
      boolean inLeft;
      boolean inRight;
      boolean inResult;

      public CAGOp() {
         super();
      }

      public void newRow() {
         this.inLeft = false;
         this.inRight = false;
         this.inResult = false;
      }

      public int classify(com.frojasg1.sun.awt.geom.Edge var1) {
         if (var1.getCurveTag() == 0) {
            this.inLeft = !this.inLeft;
         } else {
            this.inRight = !this.inRight;
         }

         boolean var2 = this.newClassification(this.inLeft, this.inRight);
         if (this.inResult == var2) {
            return 0;
         } else {
            this.inResult = var2;
            return var2 ? 1 : -1;
         }
      }

      public int getState() {
         return this.inResult ? 1 : -1;
      }

      public abstract boolean newClassification(boolean var1, boolean var2);
   }

   public static class EOWindOp extends AreaOp {
      private boolean inside;

      public EOWindOp() {
         super();
      }

      public void newRow() {
         this.inside = false;
      }

      public int classify(com.frojasg1.sun.awt.geom.Edge var1) {
         boolean var2 = !this.inside;
         this.inside = var2;
         return var2 ? 1 : -1;
      }

      public int getState() {
         return this.inside ? 1 : -1;
      }
   }

   public static class IntOp extends AreaOp.CAGOp {
      public IntOp() {
      }

      public boolean newClassification(boolean var1, boolean var2) {
         return var1 && var2;
      }
   }

   public static class NZWindOp extends AreaOp {
      private int count;

      public NZWindOp() {
         super();
      }

      public void newRow() {
         this.count = 0;
      }

      public int classify(com.frojasg1.sun.awt.geom.Edge var1) {
         int var2 = this.count;
         int var3 = var2 == 0 ? 1 : 0;
         var2 += var1.getCurve().getDirection();
         this.count = var2;
         return var2 == 0 ? -1 : var3;
      }

      public int getState() {
         return this.count == 0 ? -1 : 1;
      }
   }

   public static class SubOp extends AreaOp.CAGOp {
      public SubOp() {
      }

      public boolean newClassification(boolean var1, boolean var2) {
         return var1 && !var2;
      }
   }

   public static class XorOp extends AreaOp.CAGOp {
      public XorOp() {
      }

      public boolean newClassification(boolean var1, boolean var2) {
         return var1 != var2;
      }
   }
}
