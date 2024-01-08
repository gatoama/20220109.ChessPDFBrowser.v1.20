package com.frojasg1.sun.security.util;

class NullCache<K, V> extends com.frojasg1.sun.security.util.Cache<K, V> {
   static final com.frojasg1.sun.security.util.Cache<Object, Object> INSTANCE = new NullCache();

   private NullCache() {
   }

   public int size() {
      return 0;
   }

   public void clear() {
   }

   public void put(K var1, V var2) {
   }

   public V get(Object var1) {
      return null;
   }

   public void remove(Object var1) {
   }

   public void setCapacity(int var1) {
   }

   public void setTimeout(int var1) {
   }

   public void accept(CacheVisitor<K, V> var1) {
   }
}
