package com.frojasg1.sun.security.x509;

import java.lang.reflect.Field;
import com.frojasg1.sun.misc.HexDumpEncoder;
import com.frojasg1.sun.security.x509.Extension;
import com.frojasg1.sun.security.x509.OIDMap;

class UnparseableExtension extends com.frojasg1.sun.security.x509.Extension {
   private String name = "";
   private Throwable why;

   public UnparseableExtension(Extension var1, Throwable var2) {
      super(var1);

      try {
         Class var3 = OIDMap.getClass(var1.getExtensionId());
         if (var3 != null) {
            Field var4 = var3.getDeclaredField("NAME");
            this.name = (String)((String)var4.get((Object)null)) + " ";
         }
      } catch (Exception var5) {
      }

      this.why = var2;
   }

   public String toString() {
      return super.toString() + "Unparseable " + this.name + "extension due to\n" + this.why + "\n\n" + (new HexDumpEncoder()).encodeBuffer(this.getExtensionValue());
   }
}
