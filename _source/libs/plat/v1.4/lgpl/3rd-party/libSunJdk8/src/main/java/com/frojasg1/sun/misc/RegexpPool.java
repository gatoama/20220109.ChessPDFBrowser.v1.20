package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.REException;
import com.frojasg1.sun.misc.RegexpNode;
import com.frojasg1.sun.misc.RegexpTarget;

import java.io.PrintStream;

public class RegexpPool {
   private com.frojasg1.sun.misc.RegexpNode prefixMachine = new com.frojasg1.sun.misc.RegexpNode();
   private com.frojasg1.sun.misc.RegexpNode suffixMachine = new com.frojasg1.sun.misc.RegexpNode();
   private static final int BIG = 2147483647;
   private int lastDepth = 2147483647;

   public RegexpPool() {
   }

   public void add(String var1, Object var2) throws com.frojasg1.sun.misc.REException {
      this.add(var1, var2, false);
   }

   public void replace(String var1, Object var2) {
      try {
         this.add(var1, var2, true);
      } catch (Exception var4) {
      }

   }

   public Object delete(String var1) {
      Object var2 = null;
      com.frojasg1.sun.misc.RegexpNode var3 = this.prefixMachine;
      com.frojasg1.sun.misc.RegexpNode var4 = var3;
      int var5 = var1.length() - 1;
      boolean var7 = true;
      if (!var1.startsWith("*") || !var1.endsWith("*")) {
         ++var5;
      }

      if (var5 <= 0) {
         return null;
      } else {
         int var6;
         for(var6 = 0; var3 != null; ++var6) {
            if (var3.result != null && var3.depth < 2147483647 && (!var3.exact || var6 == var5)) {
               var4 = var3;
            }

            if (var6 >= var5) {
               break;
            }

            var3 = var3.find(var1.charAt(var6));
         }

         var3 = this.suffixMachine;
         var6 = var5;

         while(true) {
            --var6;
            if (var6 < 0 || var3 == null) {
               if (var7) {
                  if (var1.equals(var4.re)) {
                     var2 = var4.result;
                     var4.result = null;
                  }
               } else if (var1.equals(var4.re)) {
                  var2 = var4.result;
                  var4.result = null;
               }

               return var2;
            }

            if (var3.result != null && var3.depth < 2147483647) {
               var7 = false;
               var4 = var3;
            }

            var3 = var3.find(var1.charAt(var6));
         }
      }
   }

   public Object match(String var1) {
      return this.matchAfter(var1, 2147483647);
   }

   public Object matchNext(String var1) {
      return this.matchAfter(var1, this.lastDepth);
   }

   private void add(String var1, Object var2, boolean var3) throws com.frojasg1.sun.misc.REException {
      int var4 = var1.length();
      com.frojasg1.sun.misc.RegexpNode var5;
      if (var1.charAt(0) == '*') {
         for(var5 = this.suffixMachine; var4 > 1; var5 = var5.add(var1.charAt(var4))) {
            --var4;
         }
      } else {
         boolean var6 = false;
         if (var1.charAt(var4 - 1) == '*') {
            --var4;
         } else {
            var6 = true;
         }

         var5 = this.prefixMachine;

         for(int var7 = 0; var7 < var4; ++var7) {
            var5 = var5.add(var1.charAt(var7));
         }

         var5.exact = var6;
      }

      if (var5.result != null && !var3) {
         throw new REException(var1 + " is a duplicate");
      } else {
         var5.re = var1;
         var5.result = var2;
      }
   }

   private Object matchAfter(String var1, int var2) {
      com.frojasg1.sun.misc.RegexpNode var3 = this.prefixMachine;
      com.frojasg1.sun.misc.RegexpNode var4 = var3;
      int var5 = 0;
      int var6 = 0;
      int var7 = var1.length();
      if (var7 <= 0) {
         return null;
      } else {
         int var8;
         for(var8 = 0; var3 != null; ++var8) {
            if (var3.result != null && var3.depth < var2 && (!var3.exact || var8 == var7)) {
               this.lastDepth = var3.depth;
               var4 = var3;
               var5 = var8;
               var6 = var7;
            }

            if (var8 >= var7) {
               break;
            }

            var3 = var3.find(var1.charAt(var8));
         }

         var3 = this.suffixMachine;
         var8 = var7;

         while(true) {
            --var8;
            if (var8 < 0 || var3 == null) {
               Object var9 = var4.result;
               if (var9 != null && var9 instanceof com.frojasg1.sun.misc.RegexpTarget) {
                  var9 = ((RegexpTarget)var9).found(var1.substring(var5, var6));
               }

               return var9;
            }

            if (var3.result != null && var3.depth < var2) {
               this.lastDepth = var3.depth;
               var4 = var3;
               var5 = 0;
               var6 = var8 + 1;
            }

            var3 = var3.find(var1.charAt(var8));
         }
      }
   }

   public void reset() {
      this.lastDepth = 2147483647;
   }

   public void print(PrintStream var1) {
      var1.print("Regexp pool:\n");
      if (this.suffixMachine.firstchild != null) {
         var1.print(" Suffix machine: ");
         this.suffixMachine.firstchild.print(var1);
         var1.print("\n");
      }

      if (this.prefixMachine.firstchild != null) {
         var1.print(" Prefix machine: ");
         this.prefixMachine.firstchild.print(var1);
         var1.print("\n");
      }

   }
}
