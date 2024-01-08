package com.frojasg1.sun.security.x509;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import com.frojasg1.sun.security.util.DerInputStream;
import com.frojasg1.sun.security.util.DerOutputStream;
import com.frojasg1.sun.security.util.DerValue;
import com.frojasg1.sun.security.x509.CertAttrSet;
import com.frojasg1.sun.security.x509.Extension;
import com.frojasg1.sun.security.x509.OIDMap;
import com.frojasg1.sun.security.x509.X509AttributeName;

public class CRLExtensions {
   private Map<String, com.frojasg1.sun.security.x509.Extension> map = Collections.synchronizedMap(new TreeMap());
   private boolean unsupportedCritExt = false;
   private static final Class[] PARAMS = new Class[]{Boolean.class, Object.class};

   public CRLExtensions() {
   }

   public CRLExtensions(DerInputStream var1) throws CRLException {
      this.init(var1);
   }

   private void init(DerInputStream var1) throws CRLException {
      try {
         DerInputStream var2 = var1;
         byte var3 = (byte)var1.peekByte();
         if ((var3 & 192) == 128 && (var3 & 31) == 0) {
            DerValue var4 = var1.getDerValue();
            var2 = var4.data;
         }

         DerValue[] var8 = var2.getSequence(5);

         for(int var5 = 0; var5 < var8.length; ++var5) {
            com.frojasg1.sun.security.x509.Extension var6 = new com.frojasg1.sun.security.x509.Extension(var8[var5]);
            this.parseExtension(var6);
         }

      } catch (IOException var7) {
         throw new CRLException("Parsing error: " + var7.toString());
      }
   }

   private void parseExtension(com.frojasg1.sun.security.x509.Extension var1) throws CRLException {
      try {
         Class var2 = OIDMap.getClass(var1.getExtensionId());
         if (var2 == null) {
            if (var1.isCritical()) {
               this.unsupportedCritExt = true;
            }

            if (this.map.put(var1.getExtensionId().toString(), var1) != null) {
               throw new CRLException("Duplicate extensions not allowed");
            }
         } else {
            Constructor var3 = var2.getConstructor(PARAMS);
            Object[] var4 = new Object[]{var1.isCritical(), var1.getExtensionValue()};
            com.frojasg1.sun.security.x509.CertAttrSet var5 = (com.frojasg1.sun.security.x509.CertAttrSet)var3.newInstance(var4);
            if (this.map.put(var5.getName(), (com.frojasg1.sun.security.x509.Extension)var5) != null) {
               throw new CRLException("Duplicate extensions not allowed");
            }
         }
      } catch (InvocationTargetException var6) {
         throw new CRLException(var6.getTargetException().getMessage());
      } catch (Exception var7) {
         throw new CRLException(var7.toString());
      }
   }

   public void encode(OutputStream var1, boolean var2) throws CRLException {
      try {
         DerOutputStream var3 = new DerOutputStream();
         Collection var4 = this.map.values();
         Object[] var5 = var4.toArray();

         for(int var6 = 0; var6 < var5.length; ++var6) {
            if (var5[var6] instanceof com.frojasg1.sun.security.x509.CertAttrSet) {
               ((com.frojasg1.sun.security.x509.CertAttrSet)var5[var6]).encode(var3);
            } else {
               if (!(var5[var6] instanceof com.frojasg1.sun.security.x509.Extension)) {
                  throw new CRLException("Illegal extension object");
               }

               ((com.frojasg1.sun.security.x509.Extension)var5[var6]).encode(var3);
            }
         }

         DerOutputStream var10 = new DerOutputStream();
         var10.write((byte)48, (DerOutputStream)var3);
         DerOutputStream var7 = new DerOutputStream();
         if (var2) {
            var7.write(DerValue.createTag((byte)-128, true, (byte)0), var10);
         } else {
            var7 = var10;
         }

         var1.write(var7.toByteArray());
      } catch (IOException var8) {
         throw new CRLException("Encoding error: " + var8.toString());
      } catch (CertificateException var9) {
         throw new CRLException("Encoding error: " + var9.toString());
      }
   }

   public com.frojasg1.sun.security.x509.Extension get(String var1) {
      com.frojasg1.sun.security.x509.X509AttributeName var2 = new X509AttributeName(var1);
      String var4 = var2.getPrefix();
      String var3;
      if (var4.equalsIgnoreCase("x509")) {
         int var5 = var1.lastIndexOf(".");
         var3 = var1.substring(var5 + 1);
      } else {
         var3 = var1;
      }

      return (com.frojasg1.sun.security.x509.Extension)this.map.get(var3);
   }

   public void set(String var1, Object var2) {
      this.map.put(var1, (com.frojasg1.sun.security.x509.Extension)var2);
   }

   public void delete(String var1) {
      this.map.remove(var1);
   }

   public Enumeration<com.frojasg1.sun.security.x509.Extension> getElements() {
      return Collections.enumeration(this.map.values());
   }

   public Collection<com.frojasg1.sun.security.x509.Extension> getAllExtensions() {
      return this.map.values();
   }

   public boolean hasUnsupportedCriticalExtension() {
      return this.unsupportedCritExt;
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CRLExtensions)) {
         return false;
      } else {
         Collection var2 = ((CRLExtensions)var1).getAllExtensions();
         Object[] var3 = var2.toArray();
         int var4 = var3.length;
         if (var4 != this.map.size()) {
            return false;
         } else {
            String var7 = null;

            for(int var8 = 0; var8 < var4; ++var8) {
               if (var3[var8] instanceof com.frojasg1.sun.security.x509.CertAttrSet) {
                  var7 = ((CertAttrSet)var3[var8]).getName();
               }

               com.frojasg1.sun.security.x509.Extension var5 = (com.frojasg1.sun.security.x509.Extension)var3[var8];
               if (var7 == null) {
                  var7 = var5.getExtensionId().toString();
               }

               com.frojasg1.sun.security.x509.Extension var6 = (Extension)this.map.get(var7);
               if (var6 == null) {
                  return false;
               }

               if (!var6.equals(var5)) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int hashCode() {
      return this.map.hashCode();
   }

   public String toString() {
      return this.map.toString();
   }
}
