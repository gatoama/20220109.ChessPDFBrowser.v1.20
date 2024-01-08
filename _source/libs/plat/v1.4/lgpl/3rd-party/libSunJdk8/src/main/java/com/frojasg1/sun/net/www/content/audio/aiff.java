package com.frojasg1.sun.net.www.content.audio;

import java.io.IOException;
import java.net.ContentHandler;
import java.net.URLConnection;
import com.frojasg1.sun.applet.AppletAudioClip;

public class aiff extends ContentHandler {
   public aiff() {
   }

   public Object getContent(URLConnection var1) throws IOException {
      return new AppletAudioClip(var1);
   }
}
