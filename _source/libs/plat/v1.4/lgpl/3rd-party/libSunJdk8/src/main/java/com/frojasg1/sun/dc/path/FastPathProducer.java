package com.frojasg1.sun.dc.path;

import com.frojasg1.sun.dc.path.PathConsumer;
import com.frojasg1.sun.dc.path.PathError;
import com.frojasg1.sun.dc.path.PathException;

public interface FastPathProducer {
   void getBox(float[] var1) throws com.frojasg1.sun.dc.path.PathError;

   void sendTo(PathConsumer var1) throws PathError, PathException;
}
