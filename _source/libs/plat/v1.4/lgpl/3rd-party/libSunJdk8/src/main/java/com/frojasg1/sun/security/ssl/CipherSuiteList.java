package com.frojasg1.sun.security.ssl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import javax.net.ssl.SSLException;

final class CipherSuiteList {
   private final Collection<CipherSuite> cipherSuites;
   private String[] suiteNames;
   private volatile Boolean containsEC;

   CipherSuiteList(Collection<CipherSuite> var1) {
      this.cipherSuites = var1;
   }

   CipherSuiteList(CipherSuite var1) {
      this.cipherSuites = new ArrayList(1);
      this.cipherSuites.add(var1);
   }

   CipherSuiteList(String[] var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("CipherSuites may not be null");
      } else {
         this.cipherSuites = new ArrayList(var1.length);

         for(int var2 = 0; var2 < var1.length; ++var2) {
            String var3 = var1[var2];
            CipherSuite var4 = CipherSuite.valueOf(var3);
            if (!var4.isAvailable()) {
               throw new IllegalArgumentException("Cannot support " + var3 + " with currently installed providers");
            }

            this.cipherSuites.add(var4);
         }

      }
   }

   CipherSuiteList(HandshakeInStream var1) throws IOException {
      byte[] var2 = var1.getBytes16();
      if ((var2.length & 1) != 0) {
         throw new SSLException("Invalid ClientHello message");
      } else {
         this.cipherSuites = new ArrayList(var2.length >> 1);

         for(int var3 = 0; var3 < var2.length; var3 += 2) {
            this.cipherSuites.add(CipherSuite.valueOf(var2[var3], var2[var3 + 1]));
         }

      }
   }

   boolean contains(CipherSuite var1) {
      return this.cipherSuites.contains(var1);
   }

   boolean containsEC() {
      if (this.containsEC == null) {
         Iterator var1 = this.cipherSuites.iterator();

         while(var1.hasNext()) {
            CipherSuite var2 = (CipherSuite)var1.next();
            if (var2.keyExchange.isEC) {
               this.containsEC = true;
               return true;
            }
         }

         this.containsEC = false;
      }

      return this.containsEC;
   }

   Iterator<CipherSuite> iterator() {
      return this.cipherSuites.iterator();
   }

   Collection<CipherSuite> collection() {
      return this.cipherSuites;
   }

   int size() {
      return this.cipherSuites.size();
   }

   synchronized String[] toStringArray() {
      if (this.suiteNames == null) {
         this.suiteNames = new String[this.cipherSuites.size()];
         int var1 = 0;

         CipherSuite var3;
         for(Iterator var2 = this.cipherSuites.iterator(); var2.hasNext(); this.suiteNames[var1++] = var3.name) {
            var3 = (CipherSuite)var2.next();
         }
      }

      return (String[])this.suiteNames.clone();
   }

   public String toString() {
      return this.cipherSuites.toString();
   }

   void send(HandshakeOutStream var1) throws IOException {
      byte[] var2 = new byte[this.cipherSuites.size() * 2];
      int var3 = 0;

      for(Iterator var4 = this.cipherSuites.iterator(); var4.hasNext(); var3 += 2) {
         CipherSuite var5 = (CipherSuite)var4.next();
         var2[var3] = (byte)(var5.id >> 8);
         var2[var3 + 1] = (byte)var5.id;
      }

      var1.putBytes16(var2);
   }
}
