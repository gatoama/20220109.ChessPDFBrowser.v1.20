package com.frojasg1.sun.security.ssl;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.AlgorithmConstraints;
import java.security.GeneralSecurityException;
import java.security.PrivilegedAction;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import javax.crypto.BadPaddingException;
import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLProtocolException;
import javax.net.ssl.SSLSession;
import com.frojasg1.sun.misc.JavaNetAccess;
import com.frojasg1.sun.misc.SharedSecrets;

public final class SSLSocketImpl extends BaseSSLSocketImpl {
   private static final int cs_START = 0;
   private static final int cs_HANDSHAKE = 1;
   private static final int cs_DATA = 2;
   private static final int cs_RENEGOTIATE = 3;
   private static final int cs_ERROR = 4;
   private static final int cs_SENT_CLOSE = 5;
   private static final int cs_CLOSED = 6;
   private static final int cs_APP_CLOSED = 7;
   private volatile int connectionState;
   private boolean expectingFinished;
   private SSLException closeReason;
   private byte doClientAuth;
   private boolean roleIsServer;
   private boolean enableSessionCreation = true;
   private String host;
   private boolean autoClose = true;
   private AccessControlContext acc;
   private CipherSuiteList enabledCipherSuites;
   private String identificationProtocol = null;
   private AlgorithmConstraints algorithmConstraints = null;
   List<SNIServerName> serverNames = Collections.emptyList();
   Collection<SNIMatcher> sniMatchers = Collections.emptyList();
   private boolean noSniExtension = false;
   private boolean noSniMatcher = false;
   private final Object handshakeLock = new Object();
   final ReentrantLock writeLock = new ReentrantLock();
   private final Object readLock = new Object();
   private InputRecord inrec;
   private Authenticator readAuthenticator;
   private Authenticator writeAuthenticator;
   private CipherBox readCipher;
   private CipherBox writeCipher;
   private boolean secureRenegotiation;
   private byte[] clientVerifyData;
   private byte[] serverVerifyData;
   private SSLContextImpl sslContext;
   private Handshaker handshaker;
   private SSLSessionImpl sess;
   private volatile SSLSessionImpl handshakeSession;
   private HashMap<HandshakeCompletedListener, AccessControlContext> handshakeListeners;
   private InputStream sockInput;
   private OutputStream sockOutput;
   private AppInputStream input;
   private AppOutputStream output;
   private ProtocolList enabledProtocols;
   private ProtocolVersion protocolVersion;
   private static final Debug debug = Debug.getInstance("ssl");
   private boolean isFirstAppOutputRecord;
   private ByteArrayOutputStream heldRecordBuffer;
   private boolean preferLocalCipherSuites;
   static final boolean trustNameService = Debug.getBooleanProperty("jdk.tls.trustNameService", false);

   SSLSocketImpl(SSLContextImpl var1, String var2, int var3) throws IOException, UnknownHostException {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      this.host = var2;
      this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
      this.init(var1, false);
      InetSocketAddress var4 = var2 != null ? new InetSocketAddress(var2, var3) : new InetSocketAddress(InetAddress.getByName((String)null), var3);
      this.connect(var4, 0);
   }

   SSLSocketImpl(SSLContextImpl var1, InetAddress var2, int var3) throws IOException {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      this.init(var1, false);
      InetSocketAddress var4 = new InetSocketAddress(var2, var3);
      this.connect(var4, 0);
   }

   SSLSocketImpl(SSLContextImpl var1, String var2, int var3, InetAddress var4, int var5) throws IOException, UnknownHostException {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      this.host = var2;
      this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
      this.init(var1, false);
      this.bind(new InetSocketAddress(var4, var5));
      InetSocketAddress var6 = var2 != null ? new InetSocketAddress(var2, var3) : new InetSocketAddress(InetAddress.getByName((String)null), var3);
      this.connect(var6, 0);
   }

   SSLSocketImpl(SSLContextImpl var1, InetAddress var2, int var3, InetAddress var4, int var5) throws IOException {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      this.init(var1, false);
      this.bind(new InetSocketAddress(var4, var5));
      InetSocketAddress var6 = new InetSocketAddress(var2, var3);
      this.connect(var6, 0);
   }

   SSLSocketImpl(SSLContextImpl var1, boolean var2, CipherSuiteList var3, byte var4, boolean var5, ProtocolList var6, String var7, AlgorithmConstraints var8, Collection<SNIMatcher> var9, boolean var10) throws IOException {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      this.doClientAuth = var4;
      this.enableSessionCreation = var5;
      this.identificationProtocol = var7;
      this.algorithmConstraints = var8;
      this.sniMatchers = var9;
      this.preferLocalCipherSuites = var10;
      this.init(var1, var2);
      this.enabledCipherSuites = var3;
      this.enabledProtocols = var6;
   }

   SSLSocketImpl(SSLContextImpl var1) {
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      this.init(var1, false);
   }

   SSLSocketImpl(SSLContextImpl var1, Socket var2, String var3, int var4, boolean var5) throws IOException {
      super(var2);
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      if (!var2.isConnected()) {
         throw new SocketException("Underlying socket is not connected");
      } else {
         this.host = var3;
         this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
         this.init(var1, false);
         this.autoClose = var5;
         this.doneConnect();
      }
   }

   SSLSocketImpl(SSLContextImpl var1, Socket var2, InputStream var3, boolean var4) throws IOException {
      super(var2, var3);
      this.protocolVersion = ProtocolVersion.DEFAULT;
      this.isFirstAppOutputRecord = true;
      this.heldRecordBuffer = null;
      this.preferLocalCipherSuites = false;
      if (!var2.isConnected()) {
         throw new SocketException("Underlying socket is not connected");
      } else {
         this.init(var1, true);
         this.autoClose = var4;
         this.doneConnect();
      }
   }

   private void init(SSLContextImpl var1, boolean var2) {
      this.sslContext = var1;
      this.sess = SSLSessionImpl.nullSession;
      this.handshakeSession = null;
      this.roleIsServer = var2;
      this.connectionState = 0;
      this.readCipher = CipherBox.NULL;
      this.readAuthenticator = MAC.NULL;
      this.writeCipher = CipherBox.NULL;
      this.writeAuthenticator = MAC.NULL;
      this.secureRenegotiation = false;
      this.clientVerifyData = new byte[0];
      this.serverVerifyData = new byte[0];
      this.enabledCipherSuites = this.sslContext.getDefaultCipherSuiteList(this.roleIsServer);
      this.enabledProtocols = this.sslContext.getDefaultProtocolList(this.roleIsServer);
      this.inrec = null;
      this.acc = AccessController.getContext();
      this.input = new AppInputStream(this);
      this.output = new AppOutputStream(this);
   }

   public void connect(SocketAddress var1, int var2) throws IOException {
      if (this.isLayered()) {
         throw new SocketException("Already connected");
      } else if (!(var1 instanceof InetSocketAddress)) {
         throw new SocketException("Cannot handle non-Inet socket addresses.");
      } else {
         super.connect(var1, var2);
         if (this.host == null || this.host.length() == 0) {
            this.useImplicitHost(false);
         }

         this.doneConnect();
      }
   }

   void doneConnect() throws IOException {
      this.sockInput = super.getInputStream();
      this.sockOutput = super.getOutputStream();
      this.initHandshaker();
   }

   private synchronized int getConnectionState() {
      return this.connectionState;
   }

   private synchronized void setConnectionState(int var1) {
      this.connectionState = var1;
   }

   AccessControlContext getAcc() {
      return this.acc;
   }

   void writeRecord(OutputRecord var1) throws IOException {
      this.writeRecord(var1, false);
   }

   void writeRecord(OutputRecord var1, boolean var2) throws IOException {
      while(true) {
         if (var1.contentType() == 23) {
            switch(this.getConnectionState()) {
            case 1:
               this.performInitialHandshake();
               continue;
            case 2:
            case 3:
               break;
            case 4:
               this.fatal((byte)0, (String)"error while writing to socket");
               continue;
            case 5:
            case 6:
            case 7:
               if (this.closeReason != null) {
                  throw this.closeReason;
               }

               throw new SocketException("Socket closed");
            default:
               throw new SSLProtocolException("State error, send app data");
            }
         }

         if (!var1.isEmpty()) {
            if (var1.isAlert((byte)0) && this.getSoLinger() >= 0) {
               boolean var3 = Thread.interrupted();

               try {
                  if (this.writeLock.tryLock((long)this.getSoLinger(), TimeUnit.SECONDS)) {
                     try {
                        this.writeRecordInternal(var1, var2);
                     } finally {
                        this.writeLock.unlock();
                     }
                  } else {
                     SSLException var4 = new SSLException("SO_LINGER timeout, close_notify message cannot be sent.");
                     if (this.isLayered() && !this.autoClose) {
                        this.fatal((byte)-1, (Throwable)var4);
                     } else if (debug != null && Debug.isOn("ssl")) {
                        System.out.println(Thread.currentThread().getName() + ", received Exception: " + var4);
                     }

                     this.sess.invalidate();
                  }
               } catch (InterruptedException var14) {
                  var3 = true;
               }

               if (var3) {
                  Thread.currentThread().interrupt();
               }
            } else {
               this.writeLock.lock();

               try {
                  this.writeRecordInternal(var1, var2);
               } finally {
                  this.writeLock.unlock();
               }
            }
         }

         return;
      }
   }

   private void writeRecordInternal(OutputRecord var1, boolean var2) throws IOException {
      var1.encrypt(this.writeAuthenticator, this.writeCipher);
      if (var2) {
         if (this.getTcpNoDelay()) {
            var2 = false;
         } else if (this.heldRecordBuffer == null) {
            this.heldRecordBuffer = new ByteArrayOutputStream(40);
         }
      }

      var1.write(this.sockOutput, var2, this.heldRecordBuffer);
      if (this.connectionState < 4) {
         this.checkSequenceNumber(this.writeAuthenticator, var1.contentType());
      }

      if (this.isFirstAppOutputRecord && var1.contentType() == 23) {
         this.isFirstAppOutputRecord = false;
      }

   }

   boolean needToSplitPayload() {
      this.writeLock.lock();

      boolean var1;
      try {
         var1 = this.protocolVersion.v <= ProtocolVersion.TLS10.v && this.writeCipher.isCBCMode() && !this.isFirstAppOutputRecord && Record.enableCBCProtection;
      } finally {
         this.writeLock.unlock();
      }

      return var1;
   }

   void readDataRecord(InputRecord var1) throws IOException {
      if (this.getConnectionState() == 1) {
         this.performInitialHandshake();
      }

      this.readRecord(var1, true);
   }

   private void readRecord(InputRecord var1, boolean var2) throws IOException {
      synchronized(this.readLock) {
         while(true) {
            int var3;
            if ((var3 = this.getConnectionState()) != 6 && var3 != 4 && var3 != 7) {
               try {
                  var1.setAppDataValid(false);
                  var1.read(this.sockInput, this.sockOutput);
               } catch (SSLProtocolException var12) {
                  SSLProtocolException var5 = var12;

                  try {
                     this.fatal((byte)10, (Throwable)var5);
                  } catch (IOException var11) {
                  }

                  throw var12;
               } catch (EOFException var13) {
                  boolean var6 = this.getConnectionState() <= 1;
                  boolean var7 = requireCloseNotify || var6;
                  if (debug != null && Debug.isOn("ssl")) {
                     System.out.println(Thread.currentThread().getName() + ", received EOFException: " + (var7 ? "error" : "ignored"));
                  }

                  if (var7) {
                     SSLException var8;
                     if (var6) {
                        var8 = new SSLHandshakeException("Remote host closed connection during handshake");
                     } else {
                        var8 = new SSLProtocolException("Remote host closed connection incorrectly");
                     }

                     var8.initCause(var13);
                     throw var8;
                  }

                  this.closeInternal(false);
                  continue;
               }

               try {
                  var1.decrypt(this.readAuthenticator, this.readCipher);
               } catch (BadPaddingException var15) {
                  int var17 = var1.contentType() == 22 ? 40 : 20;
                  this.fatal((byte)var17, var15.getMessage(), var15);
               }

               synchronized(this) {
                  switch(var1.contentType()) {
                  case 20:
                     if (this.connectionState != 1 && this.connectionState != 3) {
                        this.fatal((byte)10, (String)("illegal change cipher spec msg, conn state = " + this.connectionState));
                     } else if (var1.available() != 1 || var1.read() != 1) {
                        this.fatal((byte)10, (String)"Malformed change cipher spec msg");
                     }

                     this.handshaker.receiveChangeCipherSpec();
                     this.changeReadCiphers();
                     this.expectingFinished = true;
                     continue;
                  case 21:
                     this.recvAlert(var1);
                     continue;
                  case 22:
                     this.initHandshaker();
                     if (!this.handshaker.activated()) {
                        if (this.connectionState == 3) {
                           this.handshaker.activate(this.protocolVersion);
                        } else {
                           this.handshaker.activate((ProtocolVersion)null);
                        }
                     }

                     this.handshaker.process_record(var1, this.expectingFinished);
                     this.expectingFinished = false;
                     if (this.handshaker.invalidated) {
                        this.handshaker = null;
                        this.inrec.setHandshakeHash((HandshakeHash)null);
                        if (this.connectionState == 3) {
                           this.connectionState = 2;
                        }
                     } else if (this.handshaker.isDone()) {
                        this.secureRenegotiation = this.handshaker.isSecureRenegotiation();
                        this.clientVerifyData = this.handshaker.getClientVerifyData();
                        this.serverVerifyData = this.handshaker.getServerVerifyData();
                        this.sess = this.handshaker.getSession();
                        this.handshakeSession = null;
                        this.handshaker = null;
                        this.connectionState = 2;
                        if (this.handshakeListeners != null) {
                           HandshakeCompletedEvent var18 = new HandshakeCompletedEvent(this, this.sess);
                           SSLSocketImpl.NotifyHandshakeThread var19 = new SSLSocketImpl.NotifyHandshakeThread(this.handshakeListeners.entrySet(), var18);
                           var19.start();
                        }
                     }

                     if (var2 || this.connectionState != 2) {
                        continue;
                     }
                     break;
                  case 23:
                     if (this.connectionState != 2 && this.connectionState != 3 && this.connectionState != 5) {
                        throw new SSLProtocolException("Data received in non-data state: " + this.connectionState);
                     }

                     if (this.expectingFinished) {
                        throw new SSLProtocolException("Expecting finished message, received data");
                     }

                     if (!var2) {
                        throw new SSLException("Discarding app data");
                     }

                     var1.setAppDataValid(true);
                     break;
                  default:
                     if (debug != null && Debug.isOn("ssl")) {
                        System.out.println(Thread.currentThread().getName() + ", Received record type: " + var1.contentType());
                     }
                     continue;
                  }

                  if (this.connectionState < 4) {
                     this.checkSequenceNumber(this.readAuthenticator, var1.contentType());
                  }
               }

               return;
            }

            var1.close();
            return;
         }
      }
   }

   private void checkSequenceNumber(Authenticator var1, byte var2) throws IOException {
      if (this.connectionState < 4 && var1 != MAC.NULL) {
         if (var1.seqNumOverflow()) {
            if (debug != null && Debug.isOn("ssl")) {
               System.out.println(Thread.currentThread().getName() + ", sequence number extremely close to overflow (2^64-1 packets). Closing connection.");
            }

            this.fatal((byte)40, (String)"sequence number overflow");
         }

         if (var2 != 22 && var1.seqNumIsHuge()) {
            if (debug != null && Debug.isOn("ssl")) {
               System.out.println(Thread.currentThread().getName() + ", request renegotiation to avoid sequence number overflow");
            }

            this.startHandshake();
         }

      }
   }

   AppInputStream getAppInputStream() {
      return this.input;
   }

   AppOutputStream getAppOutputStream() {
      return this.output;
   }

   private void initHandshaker() {
      switch(this.connectionState) {
      case 0:
      case 2:
         if (this.connectionState == 0) {
            this.connectionState = 1;
         } else {
            this.connectionState = 3;
         }

         if (this.roleIsServer) {
            this.handshaker = new ServerHandshaker(this, this.sslContext, this.enabledProtocols, this.doClientAuth, this.protocolVersion, this.connectionState == 1, this.secureRenegotiation, this.clientVerifyData, this.serverVerifyData);
            this.handshaker.setSNIMatchers(this.sniMatchers);
            this.handshaker.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
         } else {
            this.handshaker = new ClientHandshaker(this, this.sslContext, this.enabledProtocols, this.protocolVersion, this.connectionState == 1, this.secureRenegotiation, this.clientVerifyData, this.serverVerifyData);
            this.handshaker.setSNIServerNames(this.serverNames);
         }

         this.handshaker.setEnabledCipherSuites(this.enabledCipherSuites);
         this.handshaker.setEnableSessionCreation(this.enableSessionCreation);
         return;
      case 1:
      case 3:
         return;
      default:
         throw new IllegalStateException("Internal error");
      }
   }

   private void performInitialHandshake() throws IOException {
      synchronized(this.handshakeLock) {
         if (this.getConnectionState() == 1) {
            this.kickstartHandshake();
            if (this.inrec == null) {
               this.inrec = new InputRecord();
               this.inrec.setHandshakeHash(this.input.r.getHandshakeHash());
               this.inrec.setHelloVersion(this.input.r.getHelloVersion());
               this.inrec.enableFormatChecks();
            }

            this.readRecord(this.inrec, false);
            this.inrec = null;
         }

      }
   }

   public void startHandshake() throws IOException {
      this.startHandshake(true);
   }

   private void startHandshake(boolean var1) throws IOException {
      this.checkWrite();

      try {
         if (this.getConnectionState() == 1) {
            this.performInitialHandshake();
         } else {
            this.kickstartHandshake();
         }
      } catch (Exception var3) {
         this.handleException(var3, var1);
      }

   }

   private synchronized void kickstartHandshake() throws IOException {
      switch(this.connectionState) {
      case 0:
         throw new SocketException("handshaking attempted on unconnected socket");
      case 2:
         if (!this.secureRenegotiation && !Handshaker.allowUnsafeRenegotiation) {
            throw new SSLHandshakeException("Insecure renegotiation is not allowed");
         } else {
            if (!this.secureRenegotiation && debug != null && Debug.isOn("handshake")) {
               System.out.println("Warning: Using insecure renegotiation");
            }

            this.initHandshaker();
         }
      case 1:
         if (!this.handshaker.activated()) {
            if (this.connectionState == 3) {
               this.handshaker.activate(this.protocolVersion);
            } else {
               this.handshaker.activate((ProtocolVersion)null);
            }

            if (this.handshaker instanceof ClientHandshaker) {
               this.handshaker.kickstart();
            } else if (this.connectionState != 1) {
               this.handshaker.kickstart();
               this.handshaker.handshakeHash.reset();
            }
         }

         return;
      case 3:
         return;
      default:
         throw new SocketException("connection is closed");
      }
   }

   public boolean isClosed() {
      return this.connectionState == 7;
   }

   boolean checkEOF() throws IOException {
      switch(this.getConnectionState()) {
      case 0:
         throw new SocketException("Socket is not connected");
      case 1:
      case 2:
      case 3:
      case 5:
         return false;
      case 4:
      case 6:
      default:
         if (this.closeReason == null) {
            return true;
         }

         SSLException var1 = new SSLException("Connection has been shutdown: " + this.closeReason);
         var1.initCause(this.closeReason);
         throw var1;
      case 7:
         throw new SocketException("Socket is closed");
      }
   }

   void checkWrite() throws IOException {
      if (this.checkEOF() || this.getConnectionState() == 5) {
         throw new SocketException("Connection closed by remote host");
      }
   }

   protected void closeSocket() throws IOException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", called closeSocket()");
      }

      super.close();
   }

   private void closeSocket(boolean var1) throws IOException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", called closeSocket(" + var1 + ")");
      }

      if (this.isLayered() && !this.autoClose) {
         if (var1) {
            this.waitForClose(false);
         }
      } else {
         super.close();
      }

   }

   public void close() throws IOException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", called close()");
      }

      this.closeInternal(true);
      this.setConnectionState(7);
   }

   private void closeInternal(boolean var1) throws IOException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", called closeInternal(" + var1 + ")");
      }

      int var2 = this.getConnectionState();
      boolean var3 = false;
      Throwable var4 = null;
      boolean var26 = false;

      label486: {
         label487: {
            label488: {
               try {
                  var26 = true;
                  switch(var2) {
                  case 0:
                     this.closeSocket(var1);
                     var26 = false;
                     break;
                  case 1:
                  case 2:
                  case 3:
                  case 5:
                  default:
                     synchronized(this) {
                        if ((var2 = this.getConnectionState()) == 6 || var2 == 4 || var2 == 7) {
                           var26 = false;
                           break label487;
                        }

                        if (var2 != 5) {
                           try {
                              this.warning((byte)0);
                              this.connectionState = 5;
                           } catch (Throwable var32) {
                              this.connectionState = 4;
                              var4 = var32;
                              var3 = true;
                              this.closeSocket(var1);
                           }
                        }
                     }

                     if (var2 == 5) {
                        if (debug != null && Debug.isOn("ssl")) {
                           System.out.println(Thread.currentThread().getName() + ", close invoked again; state = " + this.getConnectionState());
                        }

                        if (!var1) {
                           var26 = false;
                           break label486;
                        }

                        synchronized(this) {
                           while(this.connectionState < 6) {
                              try {
                                 this.wait();
                              } catch (InterruptedException var30) {
                              }
                           }
                        }

                        if (debug != null) {
                           if (Debug.isOn("ssl")) {
                              System.out.println(Thread.currentThread().getName() + ", after primary close; state = " + this.getConnectionState());
                              var26 = false;
                           } else {
                              var26 = false;
                           }
                        } else {
                           var26 = false;
                        }
                        break label488;
                     }

                     if (!var3) {
                        var3 = true;
                        this.closeSocket(var1);
                        var26 = false;
                     } else {
                        var26 = false;
                     }
                     break;
                  case 4:
                     this.closeSocket();
                     var26 = false;
                     break;
                  case 6:
                  case 7:
                     var26 = false;
                  }
               } finally {
                  if (var26) {
                     synchronized(this) {
                        this.connectionState = this.connectionState == 7 ? 7 : 6;
                        this.notifyAll();
                     }

                     if (var3) {
                        this.disposeCiphers();
                     }

                     if (var4 != null) {
                        if (var4 instanceof Error) {
                           throw (Error)var4;
                        }

                        if (var4 instanceof RuntimeException) {
                           throw (RuntimeException)var4;
                        }
                     }

                  }
               }

               synchronized(this) {
                  this.connectionState = this.connectionState == 7 ? 7 : 6;
                  this.notifyAll();
               }

               if (var3) {
                  this.disposeCiphers();
               }

               if (var4 != null) {
                  if (var4 instanceof Error) {
                     throw (Error)var4;
                  }

                  if (var4 instanceof RuntimeException) {
                     throw (RuntimeException)var4;
                  }
               }

               return;
            }

            synchronized(this) {
               this.connectionState = this.connectionState == 7 ? 7 : 6;
               this.notifyAll();
            }

            if (var3) {
               this.disposeCiphers();
            }

            if (var4 != null) {
               if (var4 instanceof Error) {
                  throw (Error)var4;
               }

               if (var4 instanceof RuntimeException) {
                  throw (RuntimeException)var4;
               }
            }

            return;
         }

         synchronized(this) {
            this.connectionState = this.connectionState == 7 ? 7 : 6;
            this.notifyAll();
         }

         if (var3) {
            this.disposeCiphers();
         }

         if (var4 != null) {
            if (var4 instanceof Error) {
               throw (Error)var4;
            }

            if (var4 instanceof RuntimeException) {
               throw (RuntimeException)var4;
            }
         }

         return;
      }

      synchronized(this) {
         this.connectionState = this.connectionState == 7 ? 7 : 6;
         this.notifyAll();
      }

      if (var3) {
         this.disposeCiphers();
      }

      if (var4 != null) {
         if (var4 instanceof Error) {
            throw (Error)var4;
         }

         if (var4 instanceof RuntimeException) {
            throw (RuntimeException)var4;
         }
      }

   }

   void waitForClose(boolean var1) throws IOException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", waiting for close_notify or alert: state " + this.getConnectionState());
      }

      try {
         int var2;
         while((var2 = this.getConnectionState()) != 6 && var2 != 4 && var2 != 7) {
            if (this.inrec == null) {
               this.inrec = new InputRecord();
            }

            try {
               this.readRecord(this.inrec, true);
            } catch (SocketTimeoutException var4) {
               if (debug != null && Debug.isOn("ssl")) {
                  System.out.println(Thread.currentThread().getName() + ", received Exception: " + var4);
               }

               this.fatal((byte)-1, "Did not receive close_notify from peer", var4);
            }
         }

         this.inrec = null;
      } catch (IOException var5) {
         if (debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", Exception while waiting for close " + var5);
         }

         if (var1) {
            throw var5;
         }
      }

   }

   private void disposeCiphers() {
      synchronized(this.readLock) {
         this.readCipher.dispose();
      }

      this.writeLock.lock();

      try {
         this.writeCipher.dispose();
      } finally {
         this.writeLock.unlock();
      }

   }

   void handleException(Exception var1) throws IOException {
      this.handleException(var1, true);
   }

   private synchronized void handleException(Exception var1, boolean var2) throws IOException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", handling exception: " + var1.toString());
      }

      if (var1 instanceof InterruptedIOException && var2) {
         throw (IOException)var1;
      } else if (this.closeReason != null) {
         if (var1 instanceof IOException) {
            throw (IOException)var1;
         } else {
            throw Alerts.getSSLException((byte)80, var1, "Unexpected exception");
         }
      } else {
         boolean var3 = var1 instanceof SSLException;
         if (!var3 && var1 instanceof IOException) {
            try {
               this.fatal((byte)10, (Throwable)var1);
            } catch (IOException var5) {
            }

            throw (IOException)var1;
         } else {
            byte var4;
            if (var3) {
               if (var1 instanceof SSLHandshakeException) {
                  var4 = 40;
               } else {
                  var4 = 10;
               }
            } else {
               var4 = 80;
            }

            this.fatal(var4, (Throwable)var1);
         }
      }
   }

   void warning(byte var1) {
      this.sendAlert((byte)1, var1);
   }

   synchronized void fatal(byte var1, String var2) throws IOException {
      this.fatal(var1, var2, (Throwable)null);
   }

   synchronized void fatal(byte var1, Throwable var2) throws IOException {
      this.fatal(var1, (String)null, var2);
   }

   synchronized void fatal(byte var1, String var2, Throwable var3) throws IOException {
      if (this.input != null && this.input.r != null) {
         this.input.r.close();
      }

      this.sess.invalidate();
      if (this.handshakeSession != null) {
         this.handshakeSession.invalidate();
      }

      int var4 = this.connectionState;
      if (this.connectionState < 4) {
         this.connectionState = 4;
      }

      if (this.closeReason == null) {
         if (var4 == 1) {
            this.sockInput.skip((long)this.sockInput.available());
         }

         if (var1 != -1) {
            this.sendAlert((byte)2, var1);
         }

         if (var3 instanceof SSLException) {
            this.closeReason = (SSLException)var3;
         } else {
            this.closeReason = Alerts.getSSLException(var1, var3, var2);
         }
      }

      this.closeSocket();
      if (this.connectionState < 6) {
         this.connectionState = var4 == 7 ? 7 : 6;
         this.readCipher.dispose();
         this.writeCipher.dispose();
      }

      throw this.closeReason;
   }

   private void recvAlert(InputRecord var1) throws IOException {
      byte var2 = (byte)var1.read();
      byte var3 = (byte)var1.read();
      if (var3 == -1) {
         this.fatal((byte)47, (String)"Short alert message");
      }

      if (debug != null && (Debug.isOn("record") || Debug.isOn("handshake"))) {
         synchronized(System.out) {
            System.out.print(Thread.currentThread().getName());
            System.out.print(", RECV " + this.protocolVersion + " ALERT:  ");
            if (var2 == 2) {
               System.out.print("fatal, ");
            } else if (var2 == 1) {
               System.out.print("warning, ");
            } else {
               System.out.print("<level " + (255 & var2) + ">, ");
            }

            System.out.println(Alerts.alertDescription(var3));
         }
      }

      if (var2 == 1) {
         if (var3 == 0) {
            if (this.connectionState == 1) {
               this.fatal((byte)10, (String)"Received close_notify during handshake");
            } else {
               this.closeInternal(false);
            }
         } else if (this.handshaker != null) {
            this.handshaker.handshakeAlert(var3);
         }
      } else {
         String var4 = "Received fatal alert: " + Alerts.alertDescription(var3);
         if (this.closeReason == null) {
            this.closeReason = Alerts.getSSLException(var3, var4);
         }

         this.fatal((byte)10, (String)var4);
      }

   }

   private void sendAlert(byte var1, byte var2) {
      if (this.connectionState < 5) {
         if (this.connectionState != 1 || this.handshaker != null && this.handshaker.started()) {
            OutputRecord var3 = new OutputRecord((byte)21);
            var3.setVersion(this.protocolVersion);
            boolean var4 = debug != null && Debug.isOn("ssl");
            if (var4) {
               synchronized(System.out) {
                  System.out.print(Thread.currentThread().getName());
                  System.out.print(", SEND " + this.protocolVersion + " ALERT:  ");
                  if (var1 == 2) {
                     System.out.print("fatal, ");
                  } else if (var1 == 1) {
                     System.out.print("warning, ");
                  } else {
                     System.out.print("<level = " + (255 & var1) + ">, ");
                  }

                  System.out.println("description = " + Alerts.alertDescription(var2));
               }
            }

            var3.write(var1);
            var3.write(var2);

            try {
               this.writeRecord(var3);
            } catch (IOException var8) {
               if (var4) {
                  System.out.println(Thread.currentThread().getName() + ", Exception sending alert: " + var8);
               }
            }

         }
      }
   }

   private void changeReadCiphers() throws SSLException {
      if (this.connectionState != 1 && this.connectionState != 3) {
         throw new SSLProtocolException("State error, change cipher specs");
      } else {
         CipherBox var1 = this.readCipher;

         try {
            this.readCipher = this.handshaker.newReadCipher();
            this.readAuthenticator = this.handshaker.newReadAuthenticator();
         } catch (GeneralSecurityException var3) {
            throw new SSLException("Algorithm missing:  ", var3);
         }

         var1.dispose();
      }
   }

   void changeWriteCiphers() throws SSLException {
      if (this.connectionState != 1 && this.connectionState != 3) {
         throw new SSLProtocolException("State error, change cipher specs");
      } else {
         CipherBox var1 = this.writeCipher;

         try {
            this.writeCipher = this.handshaker.newWriteCipher();
            this.writeAuthenticator = this.handshaker.newWriteAuthenticator();
         } catch (GeneralSecurityException var3) {
            throw new SSLException("Algorithm missing:  ", var3);
         }

         var1.dispose();
         this.isFirstAppOutputRecord = true;
      }
   }

   synchronized void setVersion(ProtocolVersion var1) {
      this.protocolVersion = var1;
      this.output.r.setVersion(var1);
   }

   synchronized String getHost() {
      if (this.host == null || this.host.length() == 0) {
         this.useImplicitHost(true);
      }

      return this.host;
   }

   private synchronized void useImplicitHost(boolean var1) {
      InetAddress var2 = this.getInetAddress();
      if (var2 != null) {
         JavaNetAccess var3 = SharedSecrets.getJavaNetAccess();
         String var4 = var3.getOriginalHostName(var2);
         if (var4 != null && var4.length() != 0) {
            this.host = var4;
            if (!var1 && this.serverNames.isEmpty() && !this.noSniExtension) {
               this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
               if (!this.roleIsServer && this.handshaker != null && !this.handshaker.started()) {
                  this.handshaker.setSNIServerNames(this.serverNames);
               }
            }

         } else {
            if (!trustNameService) {
               this.host = var2.getHostAddress();
            } else {
               this.host = this.getInetAddress().getHostName();
            }

         }
      }
   }

   public synchronized void setHost(String var1) {
      this.host = var1;
      this.serverNames = Utilities.addToSNIServerNameList(this.serverNames, this.host);
      if (!this.roleIsServer && this.handshaker != null && !this.handshaker.started()) {
         this.handshaker.setSNIServerNames(this.serverNames);
      }

   }

   public synchronized InputStream getInputStream() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (this.connectionState == 0) {
         throw new SocketException("Socket is not connected");
      } else {
         return this.input;
      }
   }

   public synchronized OutputStream getOutputStream() throws IOException {
      if (this.isClosed()) {
         throw new SocketException("Socket is closed");
      } else if (this.connectionState == 0) {
         throw new SocketException("Socket is not connected");
      } else {
         return this.output;
      }
   }

   public SSLSession getSession() {
      if (this.getConnectionState() == 1) {
         try {
            this.startHandshake(false);
         } catch (IOException var4) {
            if (debug != null && Debug.isOn("handshake")) {
               System.out.println(Thread.currentThread().getName() + ", IOException in getSession():  " + var4);
            }
         }
      }

      synchronized(this) {
         return this.sess;
      }
   }

   public synchronized SSLSession getHandshakeSession() {
      return this.handshakeSession;
   }

   synchronized void setHandshakeSession(SSLSessionImpl var1) {
      this.handshakeSession = var1;
   }

   public synchronized void setEnableSessionCreation(boolean var1) {
      this.enableSessionCreation = var1;
      if (this.handshaker != null && !this.handshaker.activated()) {
         this.handshaker.setEnableSessionCreation(this.enableSessionCreation);
      }

   }

   public synchronized boolean getEnableSessionCreation() {
      return this.enableSessionCreation;
   }

   public synchronized void setNeedClientAuth(boolean var1) {
      this.doClientAuth = (byte)(var1 ? 2 : 0);
      if (this.handshaker != null && this.handshaker instanceof ServerHandshaker && !this.handshaker.activated()) {
         ((ServerHandshaker)this.handshaker).setClientAuth(this.doClientAuth);
      }

   }

   public synchronized boolean getNeedClientAuth() {
      return this.doClientAuth == 2;
   }

   public synchronized void setWantClientAuth(boolean var1) {
      this.doClientAuth = (byte)(var1 ? 1 : 0);
      if (this.handshaker != null && this.handshaker instanceof ServerHandshaker && !this.handshaker.activated()) {
         ((ServerHandshaker)this.handshaker).setClientAuth(this.doClientAuth);
      }

   }

   public synchronized boolean getWantClientAuth() {
      return this.doClientAuth == 1;
   }

   public synchronized void setUseClientMode(boolean var1) {
      switch(this.connectionState) {
      case 0:
         if (this.roleIsServer != !var1 && this.sslContext.isDefaultProtocolList(this.enabledProtocols)) {
            this.enabledProtocols = this.sslContext.getDefaultProtocolList(!var1);
         }

         this.roleIsServer = !var1;
         break;
      case 1:
         assert this.handshaker != null;

         if (!this.handshaker.activated()) {
            if (this.roleIsServer != !var1 && this.sslContext.isDefaultProtocolList(this.enabledProtocols)) {
               this.enabledProtocols = this.sslContext.getDefaultProtocolList(!var1);
            }

            this.roleIsServer = !var1;
            this.connectionState = 0;
            this.initHandshaker();
            break;
         }
      default:
         if (debug != null && Debug.isOn("ssl")) {
            System.out.println(Thread.currentThread().getName() + ", setUseClientMode() invoked in state = " + this.connectionState);
         }

         throw new IllegalArgumentException("Cannot change mode after SSL traffic has started");
      }

   }

   public synchronized boolean getUseClientMode() {
      return !this.roleIsServer;
   }

   public String[] getSupportedCipherSuites() {
      return this.sslContext.getSupportedCipherSuiteList().toStringArray();
   }

   public synchronized void setEnabledCipherSuites(String[] var1) {
      this.enabledCipherSuites = new CipherSuiteList(var1);
      if (this.handshaker != null && !this.handshaker.activated()) {
         this.handshaker.setEnabledCipherSuites(this.enabledCipherSuites);
      }

   }

   public synchronized String[] getEnabledCipherSuites() {
      return this.enabledCipherSuites.toStringArray();
   }

   public String[] getSupportedProtocols() {
      return this.sslContext.getSuportedProtocolList().toStringArray();
   }

   public synchronized void setEnabledProtocols(String[] var1) {
      this.enabledProtocols = new ProtocolList(var1);
      if (this.handshaker != null && !this.handshaker.activated()) {
         this.handshaker.setEnabledProtocols(this.enabledProtocols);
      }

   }

   public synchronized String[] getEnabledProtocols() {
      return this.enabledProtocols.toStringArray();
   }

   public void setSoTimeout(int var1) throws SocketException {
      if (debug != null && Debug.isOn("ssl")) {
         System.out.println(Thread.currentThread().getName() + ", setSoTimeout(" + var1 + ") called");
      }

      super.setSoTimeout(var1);
   }

   public synchronized void addHandshakeCompletedListener(HandshakeCompletedListener var1) {
      if (var1 == null) {
         throw new IllegalArgumentException("listener is null");
      } else {
         if (this.handshakeListeners == null) {
            this.handshakeListeners = new HashMap(4);
         }

         this.handshakeListeners.put(var1, AccessController.getContext());
      }
   }

   public synchronized void removeHandshakeCompletedListener(HandshakeCompletedListener var1) {
      if (this.handshakeListeners == null) {
         throw new IllegalArgumentException("no listeners");
      } else if (this.handshakeListeners.remove(var1) == null) {
         throw new IllegalArgumentException("listener not registered");
      } else {
         if (this.handshakeListeners.isEmpty()) {
            this.handshakeListeners = null;
         }

      }
   }

   public synchronized SSLParameters getSSLParameters() {
      SSLParameters var1 = super.getSSLParameters();
      var1.setEndpointIdentificationAlgorithm(this.identificationProtocol);
      var1.setAlgorithmConstraints(this.algorithmConstraints);
      if (this.sniMatchers.isEmpty() && !this.noSniMatcher) {
         var1.setSNIMatchers((Collection)null);
      } else {
         var1.setSNIMatchers(this.sniMatchers);
      }

      if (this.serverNames.isEmpty() && !this.noSniExtension) {
         var1.setServerNames((List)null);
      } else {
         var1.setServerNames(this.serverNames);
      }

      var1.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
      return var1;
   }

   public synchronized void setSSLParameters(SSLParameters var1) {
      super.setSSLParameters(var1);
      this.identificationProtocol = var1.getEndpointIdentificationAlgorithm();
      this.algorithmConstraints = var1.getAlgorithmConstraints();
      this.preferLocalCipherSuites = var1.getUseCipherSuitesOrder();
      List var2 = var1.getServerNames();
      if (var2 != null) {
         this.noSniExtension = var2.isEmpty();
         this.serverNames = var2;
      }

      Collection var3 = var1.getSNIMatchers();
      if (var3 != null) {
         this.noSniMatcher = var3.isEmpty();
         this.sniMatchers = var3;
      }

      if (this.handshaker != null && !this.handshaker.started()) {
         this.handshaker.setIdentificationProtocol(this.identificationProtocol);
         this.handshaker.setAlgorithmConstraints(this.algorithmConstraints);
         if (this.roleIsServer) {
            this.handshaker.setSNIMatchers(this.sniMatchers);
            this.handshaker.setUseCipherSuitesOrder(this.preferLocalCipherSuites);
         } else {
            this.handshaker.setSNIServerNames(this.serverNames);
         }
      }

   }

   public String toString() {
      StringBuffer var1 = new StringBuffer(80);
      var1.append(Integer.toHexString(this.hashCode()));
      var1.append("[");
      var1.append(this.sess.getCipherSuite());
      var1.append(": ");
      var1.append(super.toString());
      var1.append("]");
      return var1.toString();
   }

   private static class NotifyHandshakeThread extends Thread {
      private Set<Entry<HandshakeCompletedListener, AccessControlContext>> targets;
      private HandshakeCompletedEvent event;

      NotifyHandshakeThread(Set<Entry<HandshakeCompletedListener, AccessControlContext>> var1, HandshakeCompletedEvent var2) {
         super("HandshakeCompletedNotify-Thread");
         this.targets = new HashSet(var1);
         this.event = var2;
      }

      public void run() {
         Iterator var1 = this.targets.iterator();

         while(var1.hasNext()) {
            Entry var2 = (Entry)var1.next();
            final HandshakeCompletedListener var3 = (HandshakeCompletedListener)var2.getKey();
            AccessControlContext var4 = (AccessControlContext)var2.getValue();
            AccessController.doPrivileged(new PrivilegedAction<Void>() {
               public Void run() {
                  var3.handshakeCompleted(NotifyHandshakeThread.this.event);
                  return null;
               }
            }, var4);
         }

      }
   }
}
