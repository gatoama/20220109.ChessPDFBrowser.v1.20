package com.frojasg1.sun.java2d.cmm.kcms;

import com.frojasg1.sun.java2d.cmm.CMMServiceProvider;
import com.frojasg1.sun.java2d.cmm.PCMM;
import com.frojasg1.sun.java2d.cmm.kcms.CMM;

public final class KcmsServiceProvider extends CMMServiceProvider {
   public KcmsServiceProvider() {
   }

   protected PCMM getModule() {
      return CMM.getModule();
   }
}
