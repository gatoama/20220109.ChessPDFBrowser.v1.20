package com.frojasg1.sun.net.ftp;

import com.frojasg1.sun.net.ftp.FtpReplyCode;

public class FtpProtocolException extends Exception {
   private static final long serialVersionUID = 5978077070276545054L;
   private final com.frojasg1.sun.net.ftp.FtpReplyCode code;

   public FtpProtocolException(String var1) {
      super(var1);
      this.code = com.frojasg1.sun.net.ftp.FtpReplyCode.UNKNOWN_ERROR;
   }

   public FtpProtocolException(String var1, com.frojasg1.sun.net.ftp.FtpReplyCode var2) {
      super(var1);
      this.code = var2;
   }

   public FtpReplyCode getReplyCode() {
      return this.code;
   }
}
