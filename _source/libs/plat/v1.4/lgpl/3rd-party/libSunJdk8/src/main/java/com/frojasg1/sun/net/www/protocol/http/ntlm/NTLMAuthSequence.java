package com.frojasg1.sun.net.www.protocol.http.ntlm;

import java.io.IOException;
import java.util.Base64;

public class NTLMAuthSequence {
   private String username;
   private String password;
   private String ntdomain;
   private int state;
   private long crdHandle;
   private long ctxHandle;
   NTLMAuthSequence.Status status;

   NTLMAuthSequence(String var1, String var2, String var3) throws IOException {
      this.username = var1;
      this.password = var2;
      this.ntdomain = var3;
      this.status = new NTLMAuthSequence.Status();
      this.state = 0;
      this.crdHandle = this.getCredentialsHandle(var1, var3, var2);
      if (this.crdHandle == 0L) {
         throw new IOException("could not get credentials handle");
      }
   }

   public String getAuthHeader(String var1) throws IOException {
      byte[] var2 = null;

      assert !this.status.sequenceComplete;

      if (var1 != null) {
         var2 = Base64.getDecoder().decode(var1);
      }

      byte[] var3 = this.getNextToken(this.crdHandle, var2, this.status);
      if (var3 == null) {
         throw new IOException("Internal authentication error");
      } else {
         return Base64.getEncoder().encodeToString(var3);
      }
   }

   public boolean isComplete() {
      return this.status.sequenceComplete;
   }

   private static native void initFirst(Class<NTLMAuthSequence.Status> var0);

   private native long getCredentialsHandle(String var1, String var2, String var3);

   private native byte[] getNextToken(long var1, byte[] var3, NTLMAuthSequence.Status var4);

   static {
      initFirst(NTLMAuthSequence.Status.class);
   }

   class Status {
      boolean sequenceComplete;

      Status() {
      }
   }
}
