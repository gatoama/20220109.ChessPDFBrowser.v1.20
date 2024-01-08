package com.frojasg1.sun.applet;

import com.frojasg1.sun.applet.AppletViewer;

import java.awt.MenuBar;
import java.net.URL;
import java.util.Hashtable;

public interface AppletViewerFactory {
   AppletViewer createAppletViewer(int var1, int var2, URL var3, Hashtable var4);

   MenuBar getBaseMenuBar();

   boolean isStandalone();
}
