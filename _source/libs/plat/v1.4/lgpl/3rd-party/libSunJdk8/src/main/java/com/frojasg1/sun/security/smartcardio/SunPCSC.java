package com.frojasg1.sun.security.smartcardio;

import com.frojasg1.sun.security.smartcardio.PCSC;
import com.frojasg1.sun.security.smartcardio.PCSCException;
import com.frojasg1.sun.security.smartcardio.PCSCTerminals;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.Provider;
import javax.smartcardio.CardTerminals;
import javax.smartcardio.TerminalFactorySpi;

public final class SunPCSC extends Provider {
   private static final long serialVersionUID = 6168388284028876579L;

   public SunPCSC() {
      super("SunPCSC", 1.8D, "Sun PC/SC provider");
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            SunPCSC.this.put("TerminalFactory.PC/SC", "sun.security.smartcardio.SunPCSC$Factory");
            return null;
         }
      });
   }

   public static final class Factory extends TerminalFactorySpi {
      public Factory(Object var1) throws com.frojasg1.sun.security.smartcardio.PCSCException {
         if (var1 != null) {
            throw new IllegalArgumentException("SunPCSC factory does not use parameters");
         } else {
            com.frojasg1.sun.security.smartcardio.PCSC.checkAvailable();
            com.frojasg1.sun.security.smartcardio.PCSCTerminals.initContext();
         }
      }

      protected CardTerminals engineTerminals() {
         return new com.frojasg1.sun.security.smartcardio.PCSCTerminals();
      }
   }
}
