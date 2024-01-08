package com.frojasg1.sun.java2d.cmm.lcms;

import com.frojasg1.sun.java2d.cmm.CMMServiceProvider;
import com.frojasg1.sun.java2d.cmm.PCMM;
import com.frojasg1.sun.java2d.cmm.lcms.LCMS;

public final class LcmsServiceProvider extends CMMServiceProvider {
   public LcmsServiceProvider() {
   }

   protected PCMM getModule() {
      return LCMS.getModule();
   }
}
