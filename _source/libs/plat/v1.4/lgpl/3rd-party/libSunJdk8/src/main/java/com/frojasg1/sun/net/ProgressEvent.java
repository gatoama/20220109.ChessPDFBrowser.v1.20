package com.frojasg1.sun.net;

import com.frojasg1.sun.net.ProgressSource;

import java.net.URL;
import java.util.EventObject;

public class ProgressEvent extends EventObject {
   private URL url;
   private String contentType;
   private String method;
   private long progress;
   private long expected;
   private com.frojasg1.sun.net.ProgressSource.State state;

   public ProgressEvent(com.frojasg1.sun.net.ProgressSource var1, URL var2, String var3, String var4, com.frojasg1.sun.net.ProgressSource.State var5, long var6, long var8) {
      super(var1);
      this.url = var2;
      this.method = var3;
      this.contentType = var4;
      this.progress = var6;
      this.expected = var8;
      this.state = var5;
   }

   public URL getURL() {
      return this.url;
   }

   public String getMethod() {
      return this.method;
   }

   public String getContentType() {
      return this.contentType;
   }

   public long getProgress() {
      return this.progress;
   }

   public long getExpected() {
      return this.expected;
   }

   public ProgressSource.State getState() {
      return this.state;
   }

   public String toString() {
      return this.getClass().getName() + "[url=" + this.url + ", method=" + this.method + ", state=" + this.state + ", content-type=" + this.contentType + ", progress=" + this.progress + ", expected=" + this.expected + "]";
   }
}
