package com.frojasg1.sun.corba;

import com.sun.corba.se.impl.io.ValueHandlerImpl;

public interface JavaCorbaAccess {
   ValueHandlerImpl newValueHandlerImpl();

   Class<?> loadClass(String var1) throws ClassNotFoundException;
}
