package com.frojasg1.sun.net.ftp.impl;

import com.frojasg1.sun.net.ftp.FtpClientProvider;
import com.frojasg1.sun.net.ftp.impl.FtpClient;

public class DefaultFtpClientProvider extends FtpClientProvider {
   public DefaultFtpClientProvider() {
   }

   public com.frojasg1.sun.net.ftp.FtpClient createFtpClient() {
      return FtpClient.create();
   }
}
