package com.frojasg1.sun.applet;

import java.awt.Image;
import java.net.URL;

import com.frojasg1.sun.applet.AppletViewer;
import com.frojasg1.sun.misc.Ref;

public class AppletResourceLoader {
   public AppletResourceLoader() {
   }

   public static Image getImage(URL var0) {
      return com.frojasg1.sun.applet.AppletViewer.getCachedImage(var0);
   }

   public static Ref getImageRef(URL var0) {
      return com.frojasg1.sun.applet.AppletViewer.getCachedImageRef(var0);
   }

   public static void flushImages() {
      AppletViewer.flushImageCache();
   }
}
