package com.frojasg1.sun.applet;

import com.frojasg1.sun.applet.AppletEvent;

import java.util.EventListener;

public interface AppletListener extends EventListener {
   void appletStateChanged(AppletEvent var1);
}
