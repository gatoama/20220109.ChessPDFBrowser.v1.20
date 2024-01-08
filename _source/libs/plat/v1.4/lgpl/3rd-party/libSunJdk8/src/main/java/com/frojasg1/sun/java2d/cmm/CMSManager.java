package com.frojasg1.sun.java2d.cmm;

import java.awt.color.CMMException;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.ServiceLoader;

import com.frojasg1.sun.java2d.cmm.CMMServiceProvider;
import com.frojasg1.sun.java2d.cmm.ColorTransform;
import com.frojasg1.sun.java2d.cmm.PCMM;
import com.frojasg1.sun.java2d.cmm.Profile;
import com.frojasg1.sun.security.action.GetPropertyAction;

public class CMSManager {
   public static ColorSpace GRAYspace;
   public static ColorSpace LINEAR_RGBspace;
   private static com.frojasg1.sun.java2d.cmm.PCMM cmmImpl = null;

   public CMSManager() {
   }

   public static synchronized com.frojasg1.sun.java2d.cmm.PCMM getModule() {
      if (cmmImpl != null) {
         return cmmImpl;
      } else {
         com.frojasg1.sun.java2d.cmm.CMMServiceProvider var0 = (com.frojasg1.sun.java2d.cmm.CMMServiceProvider)AccessController.doPrivileged(new PrivilegedAction<com.frojasg1.sun.java2d.cmm.CMMServiceProvider>() {
            public com.frojasg1.sun.java2d.cmm.CMMServiceProvider run() {
               String var1 = System.getProperty("sun.java2d.cmm", "sun.java2d.cmm.lcms.LcmsServiceProvider");
               ServiceLoader var2 = ServiceLoader.loadInstalled(com.frojasg1.sun.java2d.cmm.CMMServiceProvider.class);
               com.frojasg1.sun.java2d.cmm.CMMServiceProvider var3 = null;
               Iterator var4 = var2.iterator();

               while(var4.hasNext()) {
                  com.frojasg1.sun.java2d.cmm.CMMServiceProvider var5 = (CMMServiceProvider)var4.next();
                  var3 = var5;
                  if (var5.getClass().getName().equals(var1)) {
                     break;
                  }
               }

               return var3;
            }
         });
         cmmImpl = var0.getColorManagementModule();
         if (cmmImpl == null) {
            throw new CMMException("Cannot initialize Color Management System.No CM module found");
         } else {
            GetPropertyAction var1 = new GetPropertyAction("sun.java2d.cmm.trace");
            String var2 = (String)AccessController.doPrivileged(var1);
            if (var2 != null) {
               cmmImpl = new CMSManager.CMMTracer(cmmImpl);
            }

            return cmmImpl;
         }
      }
   }

   static synchronized boolean canCreateModule() {
      return cmmImpl == null;
   }

   public static class CMMTracer implements com.frojasg1.sun.java2d.cmm.PCMM {
      com.frojasg1.sun.java2d.cmm.PCMM tcmm;
      String cName;

      public CMMTracer(PCMM var1) {
         this.tcmm = var1;
         this.cName = var1.getClass().getName();
      }

      public com.frojasg1.sun.java2d.cmm.Profile loadProfile(byte[] var1) {
         System.err.print(this.cName + ".loadProfile");
         com.frojasg1.sun.java2d.cmm.Profile var2 = this.tcmm.loadProfile(var1);
         System.err.printf("(ID=%s)\n", var2.toString());
         return var2;
      }

      public void freeProfile(com.frojasg1.sun.java2d.cmm.Profile var1) {
         System.err.printf(this.cName + ".freeProfile(ID=%s)\n", var1.toString());
         this.tcmm.freeProfile(var1);
      }

      public int getProfileSize(com.frojasg1.sun.java2d.cmm.Profile var1) {
         System.err.print(this.cName + ".getProfileSize(ID=" + var1 + ")");
         int var2 = this.tcmm.getProfileSize(var1);
         System.err.println("=" + var2);
         return var2;
      }

      public void getProfileData(com.frojasg1.sun.java2d.cmm.Profile var1, byte[] var2) {
         System.err.print(this.cName + ".getProfileData(ID=" + var1 + ") ");
         System.err.println("requested " + var2.length + " byte(s)");
         this.tcmm.getProfileData(var1, var2);
      }

      public int getTagSize(com.frojasg1.sun.java2d.cmm.Profile var1, int var2) {
         System.err.printf(this.cName + ".getTagSize(ID=%x, TagSig=%s)", var1, signatureToString(var2));
         int var3 = this.tcmm.getTagSize(var1, var2);
         System.err.println("=" + var3);
         return var3;
      }

      public void getTagData(com.frojasg1.sun.java2d.cmm.Profile var1, int var2, byte[] var3) {
         System.err.printf(this.cName + ".getTagData(ID=%x, TagSig=%s)", var1, signatureToString(var2));
         System.err.println(" requested " + var3.length + " byte(s)");
         this.tcmm.getTagData(var1, var2, var3);
      }

      public void setTagData(Profile var1, int var2, byte[] var3) {
         System.err.print(this.cName + ".setTagData(ID=" + var1 + ", TagSig=" + var2 + ")");
         System.err.println(" sending " + var3.length + " byte(s)");
         this.tcmm.setTagData(var1, var2, var3);
      }

      public com.frojasg1.sun.java2d.cmm.ColorTransform createTransform(ICC_Profile var1, int var2, int var3) {
         System.err.println(this.cName + ".createTransform(ICC_Profile,int,int)");
         return this.tcmm.createTransform(var1, var2, var3);
      }

      public com.frojasg1.sun.java2d.cmm.ColorTransform createTransform(ColorTransform[] var1) {
         System.err.println(this.cName + ".createTransform(ColorTransform[])");
         return this.tcmm.createTransform(var1);
      }

      private static String signatureToString(int var0) {
         return String.format("%c%c%c%c", (char)(255 & var0 >> 24), (char)(255 & var0 >> 16), (char)(255 & var0 >> 8), (char)(255 & var0));
      }
   }
}
