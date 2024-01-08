package com.frojasg1.sun.net.spi.nameservice;

import com.frojasg1.sun.net.spi.nameservice.NameService;

public interface NameServiceDescriptor {
   NameService createNameService() throws Exception;

   String getProviderName();

   String getType();
}
