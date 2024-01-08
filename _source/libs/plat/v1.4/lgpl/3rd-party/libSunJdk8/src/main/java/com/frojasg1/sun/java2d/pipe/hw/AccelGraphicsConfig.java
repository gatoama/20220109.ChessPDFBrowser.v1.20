package com.frojasg1.sun.java2d.pipe.hw;

import com.frojasg1.sun.java2d.pipe.hw.AccelDeviceEventListener;
import com.frojasg1.sun.java2d.pipe.hw.BufferedContextProvider;
import com.frojasg1.sun.java2d.pipe.hw.ContextCapabilities;

import java.awt.image.VolatileImage;

public interface AccelGraphicsConfig extends BufferedContextProvider {
   VolatileImage createCompatibleVolatileImage(int var1, int var2, int var3, int var4);

   ContextCapabilities getContextCapabilities();

   void addDeviceEventListener(com.frojasg1.sun.java2d.pipe.hw.AccelDeviceEventListener var1);

   void removeDeviceEventListener(AccelDeviceEventListener var1);
}
