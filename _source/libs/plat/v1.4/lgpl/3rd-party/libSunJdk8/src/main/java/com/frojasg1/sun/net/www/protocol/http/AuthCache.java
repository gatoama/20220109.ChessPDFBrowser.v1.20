package com.frojasg1.sun.net.www.protocol.http;

import com.frojasg1.sun.net.www.protocol.http.AuthCacheValue;

public interface AuthCache {
   void put(String var1, com.frojasg1.sun.net.www.protocol.http.AuthCacheValue var2);

   com.frojasg1.sun.net.www.protocol.http.AuthCacheValue get(String var1, String var2);

   void remove(String var1, AuthCacheValue var2);
}
