package com.frojasg1.sun.rmi.runtime;

import java.security.AccessController;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

import com.frojasg1.sun.rmi.runtime.Log;
import com.frojasg1.sun.rmi.runtime.NewThreadAction;
import com.frojasg1.sun.security.action.GetIntegerAction;

public final class RuntimeUtil {
   private static final com.frojasg1.sun.rmi.runtime.Log runtimeLog = Log.getLog("sun.rmi.runtime", (String)null, false);
   private static final int schedulerThreads = (Integer)AccessController.doPrivileged(new GetIntegerAction("sun.rmi.runtime.schedulerThreads", 1));
   private static final Permission GET_INSTANCE_PERMISSION = new RuntimePermission("sun.rmi.runtime.RuntimeUtil.getInstance");
   private static final RuntimeUtil instance = new RuntimeUtil();
   private final ScheduledThreadPoolExecutor scheduler;

   private RuntimeUtil() {
      this.scheduler = new ScheduledThreadPoolExecutor(schedulerThreads, new ThreadFactory() {
         private final AtomicInteger count = new AtomicInteger(0);

         public Thread newThread(Runnable var1) {
            try {
               return (Thread)AccessController.doPrivileged(new NewThreadAction(var1, "Scheduler(" + this.count.getAndIncrement() + ")", true));
            } catch (Throwable var3) {
               RuntimeUtil.runtimeLog.log(Level.WARNING, "scheduler thread factory throws", var3);
               return null;
            }
         }
      });
   }

   private static RuntimeUtil getInstance() {
      SecurityManager var0 = System.getSecurityManager();
      if (var0 != null) {
         var0.checkPermission(GET_INSTANCE_PERMISSION);
      }

      return instance;
   }

   public ScheduledThreadPoolExecutor getScheduler() {
      return this.scheduler;
   }

   public static class GetInstanceAction implements PrivilegedAction<RuntimeUtil> {
      public GetInstanceAction() {
      }

      public RuntimeUtil run() {
         return RuntimeUtil.getInstance();
      }
   }
}
