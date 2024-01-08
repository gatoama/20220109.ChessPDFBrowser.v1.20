package com.frojasg1.sun.io;

import com.frojasg1.sun.misc.VM;

public class Win32ErrorMode {
   private static final long SEM_FAILCRITICALERRORS = 1L;
   private static final long SEM_NOGPFAULTERRORBOX = 2L;
   private static final long SEM_NOALIGNMENTFAULTEXCEPT = 4L;
   private static final long SEM_NOOPENFILEERRORBOX = 32768L;

   private Win32ErrorMode() {
   }

   public static void initialize() {
      if (!VM.isBooted()) {
         String var0 = System.getProperty("sun.io.allowCriticalErrorMessageBox");
         if (var0 == null || var0.equals(Boolean.FALSE.toString())) {
            long var1 = setErrorMode(0L);
            var1 |= 1L;
            setErrorMode(var1);
         }
      }

   }

   private static native long setErrorMode(long var0);
}
