package com.frojasg1.sun.misc;

import java.io.File;
import java.net.URL;
import com.frojasg1.sun.net.www.ParseUtil;

public class FileURLMapper {
   URL url;
   String file;

   public FileURLMapper(URL var1) {
      this.url = var1;
   }

   public String getPath() {
      if (this.file != null) {
         return this.file;
      } else {
         String var1 = this.url.getHost();
         String var2;
         if (var1 != null && !var1.equals("") && !"localhost".equalsIgnoreCase(var1)) {
            var2 = this.url.getFile();
            String var3 = var1 + ParseUtil.decode(this.url.getFile());
            this.file = "\\\\" + var3.replace('/', '\\');
            return this.file;
         } else {
            var2 = this.url.getFile().replace('/', '\\');
            this.file = ParseUtil.decode(var2);
            return this.file;
         }
      }
   }

   public boolean exists() {
      String var1 = this.getPath();
      File var2 = new File(var1);
      return var2.exists();
   }
}
