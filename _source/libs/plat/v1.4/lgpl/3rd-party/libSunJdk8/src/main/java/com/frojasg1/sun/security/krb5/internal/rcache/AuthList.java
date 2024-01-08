package com.frojasg1.sun.security.krb5.internal.rcache;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.KrbApErrException;
import com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash;

public class AuthList {
   private final LinkedList<com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash> entries;
   private final int lifespan;
   private volatile int oldestTime = -2147483648;

   public AuthList(int var1) {
      this.lifespan = var1;
      this.entries = new LinkedList();
   }

   public synchronized void put(com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash var1, KerberosTime var2) throws KrbApErrException {
      if (this.entries.isEmpty()) {
         this.entries.addFirst(var1);
         this.oldestTime = var1.ctime;
      } else {
         com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash var3 = (com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash)this.entries.getFirst();
         int var4 = var3.compareTo(var1);
         if (var4 < 0) {
            this.entries.addFirst(var1);
         } else {
            if (var4 == 0) {
               throw new KrbApErrException(34);
            }

            ListIterator var5 = this.entries.listIterator(1);
            boolean var6 = false;

            while(var5.hasNext()) {
               var3 = (com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash)var5.next();
               var4 = var3.compareTo(var1);
               if (var4 < 0) {
                  this.entries.add(this.entries.indexOf(var3), var1);
                  var6 = true;
                  break;
               }

               if (var4 == 0) {
                  throw new KrbApErrException(34);
               }
            }

            if (!var6) {
               this.entries.addLast(var1);
            }
         }

         long var7 = (long)(var2.getSeconds() - this.lifespan);
         if ((long)this.oldestTime <= var7 - 5L) {
            com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash var8;
            do {
               if (this.entries.isEmpty()) {
                  this.oldestTime = -2147483648;
                  return;
               }

               var8 = (com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash)this.entries.removeLast();
            } while((long)var8.ctime < var7);

            this.entries.addLast(var8);
            this.oldestTime = var8.ctime;
         }
      }
   }

   public boolean isEmpty() {
      return this.entries.isEmpty();
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      Iterator var2 = this.entries.descendingIterator();
      int var3 = this.entries.size();

      while(var2.hasNext()) {
         com.frojasg1.sun.security.krb5.internal.rcache.AuthTimeWithHash var4 = (AuthTimeWithHash)var2.next();
         var1.append('#').append(var3--).append(": ").append(var4.toString()).append('\n');
      }

      return var1.toString();
   }
}
