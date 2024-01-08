package com.frojasg1.sun.java2d.cmm.lcms;

import java.awt.color.CMMException;
import java.awt.color.ICC_Profile;
import java.security.AccessController;
import java.security.PrivilegedAction;
import com.frojasg1.sun.java2d.cmm.ColorTransform;
import com.frojasg1.sun.java2d.cmm.PCMM;
import com.frojasg1.sun.java2d.cmm.Profile;
import com.frojasg1.sun.java2d.cmm.lcms.LCMSImageLayout;
import com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile;
import com.frojasg1.sun.java2d.cmm.lcms.LCMSTransform;

public class LCMS implements PCMM {
   private static LCMS theLcms = null;

   public Profile loadProfile(byte[] var1) {
      Object var2 = new Object();
      long var3 = this.loadProfileNative(var1, var2);
      return var3 != 0L ? new com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile(var3, var2) : null;
   }

   private native long loadProfileNative(byte[] var1, Object var2);

   private com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile getLcmsProfile(Profile var1) {
      if (var1 instanceof com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile) {
         return (com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile)var1;
      } else {
         throw new CMMException("Invalid profile: " + var1);
      }
   }

   public void freeProfile(Profile var1) {
   }

   public int getProfileSize(Profile var1) {
      synchronized(var1) {
         return this.getProfileSizeNative(this.getLcmsProfile(var1).getLcmsPtr());
      }
   }

   private native int getProfileSizeNative(long var1);

   public void getProfileData(Profile var1, byte[] var2) {
      synchronized(var1) {
         this.getProfileDataNative(this.getLcmsProfile(var1).getLcmsPtr(), var2);
      }
   }

   private native void getProfileDataNative(long var1, byte[] var3);

   public int getTagSize(Profile var1, int var2) {
      com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile var3 = this.getLcmsProfile(var1);
      synchronized(var3) {
         com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile.TagData var5 = var3.getTag(var2);
         return var5 == null ? 0 : var5.getSize();
      }
   }

   static native byte[] getTagNative(long var0, int var2);

   public void getTagData(Profile var1, int var2, byte[] var3) {
      com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile var4 = this.getLcmsProfile(var1);
      synchronized(var4) {
         com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile.TagData var6 = var4.getTag(var2);
         if (var6 != null) {
            var6.copyDataTo(var3);
         }

      }
   }

   public synchronized void setTagData(Profile var1, int var2, byte[] var3) {
      com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile var4 = this.getLcmsProfile(var1);
      synchronized(var4) {
         var4.clearTagCache();
         this.setTagDataNative(var4.getLcmsPtr(), var2, var3);
      }
   }

   private native void setTagDataNative(long var1, int var3, byte[] var4);

   public static synchronized native com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile getProfileID(ICC_Profile var0);

   static long createTransform(com.frojasg1.sun.java2d.cmm.lcms.LCMSProfile[] var0, int var1, int var2, boolean var3, int var4, boolean var5, Object var6) {
      long[] var7 = new long[var0.length];

      for(int var8 = 0; var8 < var0.length; ++var8) {
         if (var0[var8] == null) {
            throw new CMMException("Unknown profile ID");
         }

         var7[var8] = var0[var8].getLcmsPtr();
      }

      return createNativeTransform(var7, var1, var2, var3, var4, var5, var6);
   }

   private static native long createNativeTransform(long[] var0, int var1, int var2, boolean var3, int var4, boolean var5, Object var6);

   public ColorTransform createTransform(ICC_Profile var1, int var2, int var3) {
      return new com.frojasg1.sun.java2d.cmm.lcms.LCMSTransform(var1, var2, var2);
   }

   public synchronized ColorTransform createTransform(ColorTransform[] var1) {
      return new com.frojasg1.sun.java2d.cmm.lcms.LCMSTransform(var1);
   }

   public static native void colorConvert(com.frojasg1.sun.java2d.cmm.lcms.LCMSTransform var0, com.frojasg1.sun.java2d.cmm.lcms.LCMSImageLayout var1, com.frojasg1.sun.java2d.cmm.lcms.LCMSImageLayout var2);

   public static native void freeTransform(long var0);

   public static native void initLCMS(Class var0, Class var1, Class var2);

   private LCMS() {
   }

   static synchronized PCMM getModule() {
      if (theLcms != null) {
         return theLcms;
      } else {
         AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
               System.loadLibrary("awt");
               System.loadLibrary("lcms");
               return null;
            }
         });
         initLCMS(LCMSTransform.class, com.frojasg1.sun.java2d.cmm.lcms.LCMSImageLayout.class, ICC_Profile.class);
         theLcms = new LCMS();
         return theLcms;
      }
   }
}
