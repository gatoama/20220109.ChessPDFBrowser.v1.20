package com.frojasg1.sun.security.krb5.internal;

import java.io.IOException;
import java.util.Vector;
import com.frojasg1.sun.security.krb5.Asn1Exception;
import com.frojasg1.sun.security.krb5.internal.LastReqEntry;
import com.frojasg1.sun.security.util.DerInputStream;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;

public class LastReq {
   private com.frojasg1.sun.security.krb5.internal.LastReqEntry[] entry = null;

   public LastReq(com.frojasg1.sun.security.krb5.internal.LastReqEntry[] var1) throws IOException {
      if (var1 != null) {
         this.entry = new com.frojasg1.sun.security.krb5.internal.LastReqEntry[var1.length];

         for(int var2 = 0; var2 < var1.length; ++var2) {
            if (var1[var2] == null) {
               throw new IOException("Cannot create a LastReqEntry");
            }

            this.entry[var2] = (com.frojasg1.sun.security.krb5.internal.LastReqEntry)var1[var2].clone();
         }
      }

   }

   public LastReq(DerValue var1) throws Asn1Exception, IOException {
      Vector var2 = new Vector();
      if (var1.getTag() != 48) {
         throw new Asn1Exception(906);
      } else {
         while(var1.getData().available() > 0) {
            var2.addElement(new com.frojasg1.sun.security.krb5.internal.LastReqEntry(var1.getData().getDerValue()));
         }

         if (var2.size() > 0) {
            this.entry = new LastReqEntry[var2.size()];
            var2.copyInto(this.entry);
         }

      }
   }

   public byte[] asn1Encode() throws Asn1Exception, IOException {
      DerOutputStream var1 = new DerOutputStream();
      if (this.entry != null && this.entry.length > 0) {
         DerOutputStream var2 = new DerOutputStream();

         for(int var3 = 0; var3 < this.entry.length; ++var3) {
            var2.write(this.entry[var3].asn1Encode());
         }

         var1.write((byte)48, (DerOutputStream)var2);
         return var1.toByteArray();
      } else {
         return null;
      }
   }

   public static LastReq parse(DerInputStream var0, byte var1, boolean var2) throws Asn1Exception, IOException {
      if (var2 && ((byte)var0.peekByte() & 31) != var1) {
         return null;
      } else {
         DerValue var3 = var0.getDerValue();
         if (var1 != (var3.getTag() & 31)) {
            throw new Asn1Exception(906);
         } else {
            DerValue var4 = var3.getData().getDerValue();
            return new LastReq(var4);
         }
      }
   }
}
