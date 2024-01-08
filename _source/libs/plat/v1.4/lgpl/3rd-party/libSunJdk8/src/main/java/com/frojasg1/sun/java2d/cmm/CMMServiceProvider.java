package com.frojasg1.sun.java2d.cmm;

import com.frojasg1.sun.java2d.cmm.CMSManager;
import com.frojasg1.sun.java2d.cmm.PCMM;

public abstract class CMMServiceProvider {
   public CMMServiceProvider() {
   }

   public final com.frojasg1.sun.java2d.cmm.PCMM getColorManagementModule() {
      return CMSManager.canCreateModule() ? this.getModule() : null;
   }

   protected abstract PCMM getModule();
}
