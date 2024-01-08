package com.frojasg1.sun.net.ftp;

import com.frojasg1.sun.net.ftp.FtpClientProvider;
import com.frojasg1.sun.net.ftp.FtpDirEntry;
import com.frojasg1.sun.net.ftp.FtpDirParser;
import com.frojasg1.sun.net.ftp.FtpProtocolException;
import com.frojasg1.sun.net.ftp.FtpReplyCode;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public abstract class FtpClient implements Closeable {
   private static final int FTP_PORT = 21;

   public static final int defaultPort() {
      return 21;
   }

   protected FtpClient() {
   }

   public static FtpClient create() {
      com.frojasg1.sun.net.ftp.FtpClientProvider var0 = FtpClientProvider.provider();
      return var0.createFtpClient();
   }

   public static FtpClient create(InetSocketAddress var0) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException {
      FtpClient var1 = create();
      if (var0 != null) {
         var1.connect(var0);
      }

      return var1;
   }

   public static FtpClient create(String var0) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException {
      return create(new InetSocketAddress(var0, 21));
   }

   public abstract FtpClient enablePassiveMode(boolean var1);

   public abstract boolean isPassiveModeEnabled();

   public abstract FtpClient setConnectTimeout(int var1);

   public abstract int getConnectTimeout();

   public abstract FtpClient setReadTimeout(int var1);

   public abstract int getReadTimeout();

   public abstract FtpClient setProxy(Proxy var1);

   public abstract Proxy getProxy();

   public abstract boolean isConnected();

   public abstract FtpClient connect(SocketAddress var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient connect(SocketAddress var1, int var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract SocketAddress getServerAddress();

   public abstract FtpClient login(String var1, char[] var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient login(String var1, char[] var2, String var3) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract void close() throws IOException;

   public abstract boolean isLoggedIn();

   public abstract FtpClient changeDirectory(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient changeToParentDirectory() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract String getWorkingDirectory() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient setRestartOffset(long var1);

   public abstract FtpClient getFile(String var1, OutputStream var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract InputStream getFileStream(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public OutputStream putFileStream(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException {
      return this.putFileStream(var1, false);
   }

   public abstract OutputStream putFileStream(String var1, boolean var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public FtpClient putFile(String var1, InputStream var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException {
      return this.putFile(var1, var2, false);
   }

   public abstract FtpClient putFile(String var1, InputStream var2, boolean var3) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient appendFile(String var1, InputStream var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient rename(String var1, String var2) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient deleteFile(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient makeDirectory(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient removeDirectory(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient noop() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract String getStatus(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract List<String> getFeatures() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient abort() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient completePending() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient reInit() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient setType(FtpClient.TransferType var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public FtpClient setBinaryType() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException {
      this.setType(FtpClient.TransferType.BINARY);
      return this;
   }

   public FtpClient setAsciiType() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException {
      this.setType(FtpClient.TransferType.ASCII);
      return this;
   }

   public abstract InputStream list(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract InputStream nameList(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract long getSize(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract Date getLastModified(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient setDirParser(FtpDirParser var1);

   public abstract Iterator<FtpDirEntry> listFiles(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient useKerberos() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract String getWelcomeMsg();

   public abstract FtpReplyCode getLastReplyCode();

   public abstract String getLastResponseString();

   public abstract long getLastTransferSize();

   public abstract String getLastFileName();

   public abstract FtpClient startSecureSession() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient endSecureSession() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient allocate(long var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient structureMount(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract String getSystem() throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract String getHelp(String var1) throws com.frojasg1.sun.net.ftp.FtpProtocolException, IOException;

   public abstract FtpClient siteCmd(String var1) throws FtpProtocolException, IOException;

   public static enum TransferType {
      ASCII,
      BINARY,
      EBCDIC;

      private TransferType() {
      }
   }
}
