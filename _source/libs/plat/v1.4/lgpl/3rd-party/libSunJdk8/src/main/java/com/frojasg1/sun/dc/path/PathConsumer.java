package com.frojasg1.sun.dc.path;

import com.frojasg1.sun.dc.path.FastPathProducer;
import com.frojasg1.sun.dc.path.PathError;
import com.frojasg1.sun.dc.path.PathException;

public interface PathConsumer {
   void beginPath() throws com.frojasg1.sun.dc.path.PathError;

   void beginSubpath(float var1, float var2) throws com.frojasg1.sun.dc.path.PathError;

   void appendLine(float var1, float var2) throws com.frojasg1.sun.dc.path.PathError;

   void appendQuadratic(float var1, float var2, float var3, float var4) throws com.frojasg1.sun.dc.path.PathError;

   void appendCubic(float var1, float var2, float var3, float var4, float var5, float var6) throws com.frojasg1.sun.dc.path.PathError;

   void closedSubpath() throws com.frojasg1.sun.dc.path.PathError;

   void endPath() throws com.frojasg1.sun.dc.path.PathError, com.frojasg1.sun.dc.path.PathException;

   void useProxy(FastPathProducer var1) throws PathError, PathException;

   long getCPathConsumer();

   void dispose();

   PathConsumer getConsumer();
}
