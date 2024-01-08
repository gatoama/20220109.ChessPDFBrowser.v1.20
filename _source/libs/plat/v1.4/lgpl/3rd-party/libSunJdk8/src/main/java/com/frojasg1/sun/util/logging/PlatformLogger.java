package com.frojasg1.sun.util.logging;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import com.frojasg1.sun.misc.JavaLangAccess;
import com.frojasg1.sun.misc.SharedSecrets;
import com.frojasg1.sun.util.logging.LoggingSupport;

public class PlatformLogger {
   private static final int OFF = 2147483647;
   private static final int SEVERE = 1000;
   private static final int WARNING = 900;
   private static final int INFO = 800;
   private static final int CONFIG = 700;
   private static final int FINE = 500;
   private static final int FINER = 400;
   private static final int FINEST = 300;
   private static final int ALL = -2147483648;
   private static final PlatformLogger.Level DEFAULT_LEVEL;
   private static boolean loggingEnabled;
   private static Map<String, WeakReference<PlatformLogger>> loggers;
   private volatile PlatformLogger.LoggerProxy loggerProxy;
   private volatile PlatformLogger.JavaLoggerProxy javaLoggerProxy;

   public static synchronized PlatformLogger getLogger(String var0) {
      PlatformLogger var1 = null;
      WeakReference var2 = (WeakReference)loggers.get(var0);
      if (var2 != null) {
         var1 = (PlatformLogger)var2.get();
      }

      if (var1 == null) {
         var1 = new PlatformLogger(var0);
         loggers.put(var0, new WeakReference(var1));
      }

      return var1;
   }

   public static synchronized void redirectPlatformLoggers() {
      if (!loggingEnabled && com.frojasg1.sun.util.logging.LoggingSupport.isAvailable()) {
         loggingEnabled = true;
         Iterator var0 = loggers.entrySet().iterator();

         while(var0.hasNext()) {
            Entry var1 = (Entry)var0.next();
            WeakReference var2 = (WeakReference)var1.getValue();
            PlatformLogger var3 = (PlatformLogger)var2.get();
            if (var3 != null) {
               var3.redirectToJavaLoggerProxy();
            }
         }

      }
   }

   private void redirectToJavaLoggerProxy() {
      PlatformLogger.DefaultLoggerProxy var1 = (PlatformLogger.DefaultLoggerProxy)PlatformLogger.DefaultLoggerProxy.class.cast(this.loggerProxy);
      PlatformLogger.JavaLoggerProxy var2 = new PlatformLogger.JavaLoggerProxy(var1.name, var1.level);
      this.javaLoggerProxy = var2;
      this.loggerProxy = var2;
   }

   private PlatformLogger(String var1) {
      if (loggingEnabled) {
         this.loggerProxy = this.javaLoggerProxy = new PlatformLogger.JavaLoggerProxy(var1);
      } else {
         this.loggerProxy = new PlatformLogger.DefaultLoggerProxy(var1);
      }

   }

   public boolean isEnabled() {
      return this.loggerProxy.isEnabled();
   }

   public String getName() {
      return this.loggerProxy.name;
   }

   public boolean isLoggable(PlatformLogger.Level var1) {
      if (var1 == null) {
         throw new NullPointerException();
      } else {
         PlatformLogger.JavaLoggerProxy var2 = this.javaLoggerProxy;
         return var2 != null ? var2.isLoggable(var1) : this.loggerProxy.isLoggable(var1);
      }
   }

   public PlatformLogger.Level level() {
      return this.loggerProxy.getLevel();
   }

   public void setLevel(PlatformLogger.Level var1) {
      this.loggerProxy.setLevel(var1);
   }

   public void severe(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.SEVERE, var1);
   }

   public void severe(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.SEVERE, var1, var2);
   }

   public void severe(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.SEVERE, var1, var2);
   }

   public void warning(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.WARNING, var1);
   }

   public void warning(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.WARNING, var1, var2);
   }

   public void warning(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.WARNING, var1, var2);
   }

   public void info(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.INFO, var1);
   }

   public void info(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.INFO, var1, var2);
   }

   public void info(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.INFO, var1, var2);
   }

   public void config(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.CONFIG, var1);
   }

   public void config(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.CONFIG, var1, var2);
   }

   public void config(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.CONFIG, var1, var2);
   }

   public void fine(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINE, var1);
   }

   public void fine(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINE, var1, var2);
   }

   public void fine(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINE, var1, var2);
   }

   public void finer(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINER, var1);
   }

   public void finer(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINER, var1, var2);
   }

   public void finer(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINER, var1, var2);
   }

   public void finest(String var1) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINEST, var1);
   }

   public void finest(String var1, Throwable var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINEST, var1, var2);
   }

   public void finest(String var1, Object... var2) {
      this.loggerProxy.doLog(PlatformLogger.Level.FINEST, var1, var2);
   }

   static {
      DEFAULT_LEVEL = PlatformLogger.Level.INFO;
      loggingEnabled = (Boolean)AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
         public Boolean run() {
            String var1 = System.getProperty("java.util.logging.config.class");
            String var2 = System.getProperty("java.util.logging.config.file");
            return var1 != null || var2 != null;
         }
      });

      try {
         Class.forName("sun.util.logging.PlatformLogger$DefaultLoggerProxy", false, PlatformLogger.class.getClassLoader());
         Class.forName("sun.util.logging.PlatformLogger$JavaLoggerProxy", false, PlatformLogger.class.getClassLoader());
      } catch (ClassNotFoundException var1) {
         throw new InternalError(var1);
      }

      loggers = new HashMap();
   }

   private static final class DefaultLoggerProxy extends PlatformLogger.LoggerProxy {
      volatile PlatformLogger.Level effectiveLevel = this.deriveEffectiveLevel((PlatformLogger.Level)null);
      volatile PlatformLogger.Level level = null;
      private static final String formatString = com.frojasg1.sun.util.logging.LoggingSupport.getSimpleFormat(false);
      private Date date = new Date();

      private static PrintStream outputStream() {
         return System.err;
      }

      DefaultLoggerProxy(String var1) {
         super(var1);
      }

      boolean isEnabled() {
         return this.effectiveLevel != PlatformLogger.Level.OFF;
      }

      PlatformLogger.Level getLevel() {
         return this.level;
      }

      void setLevel(PlatformLogger.Level var1) {
         PlatformLogger.Level var2 = this.level;
         if (var2 != var1) {
            this.level = var1;
            this.effectiveLevel = this.deriveEffectiveLevel(var1);
         }

      }

      void doLog(PlatformLogger.Level var1, String var2) {
         if (this.isLoggable(var1)) {
            outputStream().print(this.format(var1, var2, (Throwable)null));
         }

      }

      void doLog(PlatformLogger.Level var1, String var2, Throwable var3) {
         if (this.isLoggable(var1)) {
            outputStream().print(this.format(var1, var2, var3));
         }

      }

      void doLog(PlatformLogger.Level var1, String var2, Object... var3) {
         if (this.isLoggable(var1)) {
            String var4 = this.formatMessage(var2, var3);
            outputStream().print(this.format(var1, var4, (Throwable)null));
         }

      }

      boolean isLoggable(PlatformLogger.Level var1) {
         PlatformLogger.Level var2 = this.effectiveLevel;
         return var1.intValue() >= var2.intValue() && var2 != PlatformLogger.Level.OFF;
      }

      private PlatformLogger.Level deriveEffectiveLevel(PlatformLogger.Level var1) {
         return var1 == null ? PlatformLogger.DEFAULT_LEVEL : var1;
      }

      private String formatMessage(String var1, Object... var2) {
         try {
            if (var2 != null && var2.length != 0) {
               return var1.indexOf("{0") < 0 && var1.indexOf("{1") < 0 && var1.indexOf("{2") < 0 && var1.indexOf("{3") < 0 ? var1 : MessageFormat.format(var1, var2);
            } else {
               return var1;
            }
         } catch (Exception var4) {
            return var1;
         }
      }

      private synchronized String format(PlatformLogger.Level var1, String var2, Throwable var3) {
         this.date.setTime(System.currentTimeMillis());
         String var4 = "";
         if (var3 != null) {
            StringWriter var5 = new StringWriter();
            PrintWriter var6 = new PrintWriter(var5);
            var6.println();
            var3.printStackTrace(var6);
            var6.close();
            var4 = var5.toString();
         }

         return String.format(formatString, this.date, this.getCallerInfo(), this.name, var1.name(), var2, var4);
      }

      private String getCallerInfo() {
         String var1 = null;
         String var2 = null;
         JavaLangAccess var3 = SharedSecrets.getJavaLangAccess();
         Throwable var4 = new Throwable();
         int var5 = var3.getStackTraceDepth(var4);
         String var6 = "sun.util.logging.PlatformLogger";
         boolean var7 = true;

         for(int var8 = 0; var8 < var5; ++var8) {
            StackTraceElement var9 = var3.getStackTraceElement(var4, var8);
            String var10 = var9.getClassName();
            if (var7) {
               if (var10.equals(var6)) {
                  var7 = false;
               }
            } else if (!var10.equals(var6)) {
               var1 = var10;
               var2 = var9.getMethodName();
               break;
            }
         }

         return var1 != null ? var1 + " " + var2 : this.name;
      }
   }

   private static final class JavaLoggerProxy extends PlatformLogger.LoggerProxy {
      private final Object javaLogger;

      JavaLoggerProxy(String var1) {
         this(var1, (PlatformLogger.Level)null);
      }

      JavaLoggerProxy(String var1, PlatformLogger.Level var2) {
         super(var1);
         this.javaLogger = com.frojasg1.sun.util.logging.LoggingSupport.getLogger(var1);
         if (var2 != null) {
            com.frojasg1.sun.util.logging.LoggingSupport.setLevel(this.javaLogger, var2.javaLevel);
         }

      }

      void doLog(PlatformLogger.Level var1, String var2) {
         com.frojasg1.sun.util.logging.LoggingSupport.log(this.javaLogger, var1.javaLevel, var2);
      }

      void doLog(PlatformLogger.Level var1, String var2, Throwable var3) {
         com.frojasg1.sun.util.logging.LoggingSupport.log(this.javaLogger, var1.javaLevel, var2, var3);
      }

      void doLog(PlatformLogger.Level var1, String var2, Object... var3) {
         if (this.isLoggable(var1)) {
            int var4 = var3 != null ? var3.length : 0;
            String[] var5 = new String[var4];

            for(int var6 = 0; var6 < var4; ++var6) {
               var5[var6] = String.valueOf(var3[var6]);
            }

            com.frojasg1.sun.util.logging.LoggingSupport.log(this.javaLogger, var1.javaLevel, var2, (Object[])var5);
         }
      }

      boolean isEnabled() {
         return com.frojasg1.sun.util.logging.LoggingSupport.isLoggable(this.javaLogger, PlatformLogger.Level.OFF.javaLevel);
      }

      PlatformLogger.Level getLevel() {
         Object var1 = com.frojasg1.sun.util.logging.LoggingSupport.getLevel(this.javaLogger);
         if (var1 == null) {
            return null;
         } else {
            try {
               return PlatformLogger.Level.valueOf(com.frojasg1.sun.util.logging.LoggingSupport.getLevelName(var1));
            } catch (IllegalArgumentException var3) {
               return PlatformLogger.Level.valueOf(com.frojasg1.sun.util.logging.LoggingSupport.getLevelValue(var1));
            }
         }
      }

      void setLevel(PlatformLogger.Level var1) {
         com.frojasg1.sun.util.logging.LoggingSupport.setLevel(this.javaLogger, var1 == null ? null : var1.javaLevel);
      }

      boolean isLoggable(PlatformLogger.Level var1) {
         return com.frojasg1.sun.util.logging.LoggingSupport.isLoggable(this.javaLogger, var1.javaLevel);
      }

      static {
         PlatformLogger.Level[] var0 = PlatformLogger.Level.values();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            PlatformLogger.Level var3 = var0[var2];
            var3.javaLevel = LoggingSupport.parseLevel(var3.name());
         }

      }
   }

   public static enum Level {
      ALL,
      FINEST,
      FINER,
      FINE,
      CONFIG,
      INFO,
      WARNING,
      SEVERE,
      OFF;

      Object javaLevel;
      private static final int[] LEVEL_VALUES = new int[]{-2147483648, 300, 400, 500, 700, 800, 900, 1000, 2147483647};

      private Level() {
      }

      public int intValue() {
         return LEVEL_VALUES[this.ordinal()];
      }

      static PlatformLogger.Level valueOf(int var0) {
         switch(var0) {
         case -2147483648:
            return ALL;
         case 300:
            return FINEST;
         case 400:
            return FINER;
         case 500:
            return FINE;
         case 700:
            return CONFIG;
         case 800:
            return INFO;
         case 900:
            return WARNING;
         case 1000:
            return SEVERE;
         case 2147483647:
            return OFF;
         default:
            int var1 = Arrays.binarySearch(LEVEL_VALUES, 0, LEVEL_VALUES.length - 2, var0);
            return values()[var1 >= 0 ? var1 : -var1 - 1];
         }
      }
   }

   private abstract static class LoggerProxy {
      final String name;

      protected LoggerProxy(String var1) {
         this.name = var1;
      }

      abstract boolean isEnabled();

      abstract PlatformLogger.Level getLevel();

      abstract void setLevel(PlatformLogger.Level var1);

      abstract void doLog(PlatformLogger.Level var1, String var2);

      abstract void doLog(PlatformLogger.Level var1, String var2, Throwable var3);

      abstract void doLog(PlatformLogger.Level var1, String var2, Object... var3);

      abstract boolean isLoggable(PlatformLogger.Level var1);
   }
}