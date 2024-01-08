package com.frojasg1.sun.applet;

import com.frojasg1.sun.applet.AppletMessageHandler;

public class AppletSecurityException extends SecurityException {
   private String key;
   private Object[] msgobj;
   private static com.frojasg1.sun.applet.AppletMessageHandler amh = new com.frojasg1.sun.applet.AppletMessageHandler("appletsecurityexception");

   public AppletSecurityException(String var1) {
      super(var1);
      this.key = null;
      this.msgobj = null;
      this.key = var1;
   }

   public AppletSecurityException(String var1, String var2) {
      this(var1);
      this.msgobj = new Object[1];
      this.msgobj[0] = var2;
   }

   public AppletSecurityException(String var1, String var2, String var3) {
      this(var1);
      this.msgobj = new Object[2];
      this.msgobj[0] = var2;
      this.msgobj[1] = var3;
   }

   public String getLocalizedMessage() {
      return this.msgobj != null ? amh.getMessage(this.key, this.msgobj) : amh.getMessage(this.key);
   }
}
