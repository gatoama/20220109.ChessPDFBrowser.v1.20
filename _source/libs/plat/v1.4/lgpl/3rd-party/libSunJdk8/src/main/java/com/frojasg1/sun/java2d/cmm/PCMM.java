package com.frojasg1.sun.java2d.cmm;

import com.frojasg1.sun.java2d.cmm.ColorTransform;
import com.frojasg1.sun.java2d.cmm.Profile;

import java.awt.color.ICC_Profile;

public interface PCMM {
   com.frojasg1.sun.java2d.cmm.Profile loadProfile(byte[] var1);

   void freeProfile(com.frojasg1.sun.java2d.cmm.Profile var1);

   int getProfileSize(com.frojasg1.sun.java2d.cmm.Profile var1);

   void getProfileData(com.frojasg1.sun.java2d.cmm.Profile var1, byte[] var2);

   void getTagData(com.frojasg1.sun.java2d.cmm.Profile var1, int var2, byte[] var3);

   int getTagSize(com.frojasg1.sun.java2d.cmm.Profile var1, int var2);

   void setTagData(Profile var1, int var2, byte[] var3);

   com.frojasg1.sun.java2d.cmm.ColorTransform createTransform(ICC_Profile var1, int var2, int var3);

   com.frojasg1.sun.java2d.cmm.ColorTransform createTransform(ColorTransform[] var1);
}
