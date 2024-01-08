package com.frojasg1.sun.applet;

import com.frojasg1.sun.applet.AppletMessageHandler;

public class AppletIllegalArgumentException extends IllegalArgumentException {
   private String key = null;
   private static com.frojasg1.sun.applet.AppletMessageHandler amh = new com.frojasg1.sun.applet.AppletMessageHandler("appletillegalargumentexception");

   public AppletIllegalArgumentException(String var1) {
      super(var1);
      this.key = var1;
   }

   public String getLocalizedMessage() {
      return amh.getMessage(this.key);
   }
}
