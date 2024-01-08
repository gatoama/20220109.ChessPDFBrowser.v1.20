package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.Ref;

class CacheEntry extends Ref {
   int hash;
   Object key;
   CacheEntry next;

   CacheEntry() {
   }

   public Object reconstitute() {
      return null;
   }
}
