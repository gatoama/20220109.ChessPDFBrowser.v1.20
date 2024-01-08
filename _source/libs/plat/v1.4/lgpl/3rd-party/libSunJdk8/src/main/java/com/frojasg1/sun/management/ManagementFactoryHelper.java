package com.frojasg1.sun.management;

import com.sun.management.DiagnosticCommandMBean;
import com.sun.management.HotSpotDiagnosticMXBean;
import java.lang.Thread.State;
import java.lang.management.BufferPoolMXBean;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.CompilationMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryManagerMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.PlatformLoggingMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.management.DynamicMBean;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

import com.frojasg1.sun.management.ClassLoadingImpl;
import com.frojasg1.sun.management.CompilationImpl;
import com.frojasg1.sun.management.DiagnosticCommandImpl;
import com.frojasg1.sun.management.HotSpotDiagnostic;
import com.frojasg1.sun.management.HotspotClassLoading;
import com.frojasg1.sun.management.HotspotClassLoadingMBean;
import com.frojasg1.sun.management.HotspotCompilation;
import com.frojasg1.sun.management.HotspotCompilationMBean;
import com.frojasg1.sun.management.HotspotMemory;
import com.frojasg1.sun.management.HotspotMemoryMBean;
import com.frojasg1.sun.management.HotspotRuntime;
import com.frojasg1.sun.management.HotspotRuntimeMBean;
import com.frojasg1.sun.management.HotspotThread;
import com.frojasg1.sun.management.HotspotThreadMBean;
import com.frojasg1.sun.management.MemoryImpl;
import com.frojasg1.sun.management.OperatingSystemImpl;
import com.frojasg1.sun.management.RuntimeImpl;
import com.frojasg1.sun.management.ThreadImpl;
import com.frojasg1.sun.management.Util;
import com.frojasg1.sun.management.VMManagement;
import com.frojasg1.sun.management.VMManagementImpl;
import com.frojasg1.sun.misc.JavaNioAccess;
import com.frojasg1.sun.misc.SharedSecrets;
import com.frojasg1.sun.misc.VM;
import com.frojasg1.sun.nio.ch.FileChannelImpl;
import com.frojasg1.sun.util.logging.LoggingSupport;

public class ManagementFactoryHelper {
   private static VMManagement jvm;
   private static com.frojasg1.sun.management.ClassLoadingImpl classMBean = null;
   private static com.frojasg1.sun.management.MemoryImpl memoryMBean = null;
   private static com.frojasg1.sun.management.ThreadImpl threadMBean = null;
   private static com.frojasg1.sun.management.RuntimeImpl runtimeMBean = null;
   private static com.frojasg1.sun.management.CompilationImpl compileMBean = null;
   private static com.frojasg1.sun.management.OperatingSystemImpl osMBean = null;
   private static List<BufferPoolMXBean> bufferPools = null;
   private static final String BUFFER_POOL_MXBEAN_NAME = "java.nio:type=BufferPool";
   private static com.frojasg1.sun.management.HotSpotDiagnostic hsDiagMBean = null;
   private static com.frojasg1.sun.management.HotspotRuntime hsRuntimeMBean = null;
   private static com.frojasg1.sun.management.HotspotClassLoading hsClassMBean = null;
   private static com.frojasg1.sun.management.HotspotThread hsThreadMBean = null;
   private static com.frojasg1.sun.management.HotspotCompilation hsCompileMBean = null;
   private static com.frojasg1.sun.management.HotspotMemory hsMemoryMBean = null;
   private static com.frojasg1.sun.management.DiagnosticCommandImpl hsDiagCommandMBean = null;
   private static final String HOTSPOT_CLASS_LOADING_MBEAN_NAME = "sun.management:type=HotspotClassLoading";
   private static final String HOTSPOT_COMPILATION_MBEAN_NAME = "sun.management:type=HotspotCompilation";
   private static final String HOTSPOT_MEMORY_MBEAN_NAME = "sun.management:type=HotspotMemory";
   private static final String HOTSPOT_RUNTIME_MBEAN_NAME = "sun.management:type=HotspotRuntime";
   private static final String HOTSPOT_THREAD_MBEAN_NAME = "sun.management:type=HotspotThreading";
   static final String HOTSPOT_DIAGNOSTIC_COMMAND_MBEAN_NAME = "com.sun.management:type=DiagnosticCommand";
   private static final int JMM_THREAD_STATE_FLAG_MASK = -1048576;
   private static final int JMM_THREAD_STATE_FLAG_SUSPENDED = 1048576;
   private static final int JMM_THREAD_STATE_FLAG_NATIVE = 4194304;

   private ManagementFactoryHelper() {
   }

   public static synchronized ClassLoadingMXBean getClassLoadingMXBean() {
      if (classMBean == null) {
         classMBean = new com.frojasg1.sun.management.ClassLoadingImpl(jvm);
      }

      return classMBean;
   }

   public static synchronized MemoryMXBean getMemoryMXBean() {
      if (memoryMBean == null) {
         memoryMBean = new com.frojasg1.sun.management.MemoryImpl(jvm);
      }

      return memoryMBean;
   }

   public static synchronized ThreadMXBean getThreadMXBean() {
      if (threadMBean == null) {
         threadMBean = new com.frojasg1.sun.management.ThreadImpl(jvm);
      }

      return threadMBean;
   }

   public static synchronized RuntimeMXBean getRuntimeMXBean() {
      if (runtimeMBean == null) {
         runtimeMBean = new com.frojasg1.sun.management.RuntimeImpl(jvm);
      }

      return runtimeMBean;
   }

   public static synchronized CompilationMXBean getCompilationMXBean() {
      if (compileMBean == null && jvm.getCompilerName() != null) {
         compileMBean = new com.frojasg1.sun.management.CompilationImpl(jvm);
      }

      return compileMBean;
   }

   public static synchronized OperatingSystemMXBean getOperatingSystemMXBean() {
      if (osMBean == null) {
         osMBean = new com.frojasg1.sun.management.OperatingSystemImpl(jvm);
      }

      return osMBean;
   }

   public static List<MemoryPoolMXBean> getMemoryPoolMXBeans() {
      MemoryPoolMXBean[] var0 = com.frojasg1.sun.management.MemoryImpl.getMemoryPools();
      ArrayList var1 = new ArrayList(var0.length);
      MemoryPoolMXBean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MemoryPoolMXBean var5 = var2[var4];
         var1.add(var5);
      }

      return var1;
   }

   public static List<MemoryManagerMXBean> getMemoryManagerMXBeans() {
      MemoryManagerMXBean[] var0 = com.frojasg1.sun.management.MemoryImpl.getMemoryManagers();
      ArrayList var1 = new ArrayList(var0.length);
      MemoryManagerMXBean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MemoryManagerMXBean var5 = var2[var4];
         var1.add(var5);
      }

      return var1;
   }

   public static List<GarbageCollectorMXBean> getGarbageCollectorMXBeans() {
      MemoryManagerMXBean[] var0 = com.frojasg1.sun.management.MemoryImpl.getMemoryManagers();
      ArrayList var1 = new ArrayList(var0.length);
      MemoryManagerMXBean[] var2 = var0;
      int var3 = var0.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         MemoryManagerMXBean var5 = var2[var4];
         if (GarbageCollectorMXBean.class.isInstance(var5)) {
            var1.add(GarbageCollectorMXBean.class.cast(var5));
         }
      }

      return var1;
   }

   public static PlatformLoggingMXBean getPlatformLoggingMXBean() {
      return LoggingSupport.isAvailable() ? ManagementFactoryHelper.PlatformLoggingImpl.instance : null;
   }

   public static synchronized List<BufferPoolMXBean> getBufferPoolMXBeans() {
      if (bufferPools == null) {
         bufferPools = new ArrayList(2);
         bufferPools.add(createBufferPoolMXBean(SharedSecrets.getJavaNioAccess().getDirectBufferPool()));
         bufferPools.add(createBufferPoolMXBean(FileChannelImpl.getMappedBufferPool()));
      }

      return bufferPools;
   }

   private static BufferPoolMXBean createBufferPoolMXBean(final JavaNioAccess.BufferPool var0) {
      return new BufferPoolMXBean() {
         private volatile ObjectName objname;

         public ObjectName getObjectName() {
            ObjectName var1 = this.objname;
            if (var1 == null) {
               synchronized(this) {
                  var1 = this.objname;
                  if (var1 == null) {
                     var1 = com.frojasg1.sun.management.Util.newObjectName("java.nio:type=BufferPool,name=" + var0.getName());
                     this.objname = var1;
                  }
               }
            }

            return var1;
         }

         public String getName() {
            return var0.getName();
         }

         public long getCount() {
            return var0.getCount();
         }

         public long getTotalCapacity() {
            return var0.getTotalCapacity();
         }

         public long getMemoryUsed() {
            return var0.getMemoryUsed();
         }
      };
   }

   public static synchronized HotSpotDiagnosticMXBean getDiagnosticMXBean() {
      if (hsDiagMBean == null) {
         hsDiagMBean = new HotSpotDiagnostic();
      }

      return hsDiagMBean;
   }

   public static synchronized HotspotRuntimeMBean getHotspotRuntimeMBean() {
      if (hsRuntimeMBean == null) {
         hsRuntimeMBean = new com.frojasg1.sun.management.HotspotRuntime(jvm);
      }

      return hsRuntimeMBean;
   }

   public static synchronized HotspotClassLoadingMBean getHotspotClassLoadingMBean() {
      if (hsClassMBean == null) {
         hsClassMBean = new com.frojasg1.sun.management.HotspotClassLoading(jvm);
      }

      return hsClassMBean;
   }

   public static synchronized HotspotThreadMBean getHotspotThreadMBean() {
      if (hsThreadMBean == null) {
         hsThreadMBean = new com.frojasg1.sun.management.HotspotThread(jvm);
      }

      return hsThreadMBean;
   }

   public static synchronized HotspotMemoryMBean getHotspotMemoryMBean() {
      if (hsMemoryMBean == null) {
         hsMemoryMBean = new com.frojasg1.sun.management.HotspotMemory(jvm);
      }

      return hsMemoryMBean;
   }

   public static synchronized DiagnosticCommandMBean getDiagnosticCommandMBean() {
      if (hsDiagCommandMBean == null && jvm.isRemoteDiagnosticCommandsSupported()) {
         hsDiagCommandMBean = new com.frojasg1.sun.management.DiagnosticCommandImpl(jvm);
      }

      return hsDiagCommandMBean;
   }

   public static synchronized HotspotCompilationMBean getHotspotCompilationMBean() {
      if (hsCompileMBean == null) {
         hsCompileMBean = new com.frojasg1.sun.management.HotspotCompilation(jvm);
      }

      return hsCompileMBean;
   }

   private static void addMBean(final MBeanServer var0, final Object var1, String var2) {
      try {
         final ObjectName var3 = com.frojasg1.sun.management.Util.newObjectName(var2);
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws MBeanRegistrationException, NotCompliantMBeanException {
               try {
                  var0.registerMBean(var1, var3);
                  return null;
               } catch (InstanceAlreadyExistsException var2) {
                  return null;
               }
            }
         });
      } catch (PrivilegedActionException var6) {
         throw com.frojasg1.sun.management.Util.newException(var6.getException());
      }
   }

   public static HashMap<ObjectName, DynamicMBean> getPlatformDynamicMBeans() {
      HashMap var0 = new HashMap();
      DiagnosticCommandMBean var1 = getDiagnosticCommandMBean();
      if (var1 != null) {
         var0.put(com.frojasg1.sun.management.Util.newObjectName("com.sun.management:type=DiagnosticCommand"), var1);
      }

      return var0;
   }

   static void registerInternalMBeans(MBeanServer var0) {
      addMBean(var0, getHotspotClassLoadingMBean(), "sun.management:type=HotspotClassLoading");
      addMBean(var0, getHotspotMemoryMBean(), "sun.management:type=HotspotMemory");
      addMBean(var0, getHotspotRuntimeMBean(), "sun.management:type=HotspotRuntime");
      addMBean(var0, getHotspotThreadMBean(), "sun.management:type=HotspotThreading");
      if (getCompilationMXBean() != null) {
         addMBean(var0, getHotspotCompilationMBean(), "sun.management:type=HotspotCompilation");
      }

   }

   private static void unregisterMBean(final MBeanServer var0, String var1) {
      try {
         final ObjectName var2 = com.frojasg1.sun.management.Util.newObjectName(var1);
         AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
            public Void run() throws MBeanRegistrationException, RuntimeOperationsException {
               try {
                  var0.unregisterMBean(var2);
               } catch (InstanceNotFoundException var2x) {
               }

               return null;
            }
         });
      } catch (PrivilegedActionException var4) {
         throw com.frojasg1.sun.management.Util.newException(var4.getException());
      }
   }

   static void unregisterInternalMBeans(MBeanServer var0) {
      unregisterMBean(var0, "sun.management:type=HotspotClassLoading");
      unregisterMBean(var0, "sun.management:type=HotspotMemory");
      unregisterMBean(var0, "sun.management:type=HotspotRuntime");
      unregisterMBean(var0, "sun.management:type=HotspotThreading");
      if (getCompilationMXBean() != null) {
         unregisterMBean(var0, "sun.management:type=HotspotCompilation");
      }

   }

   public static boolean isThreadSuspended(int var0) {
      return (var0 & 1048576) != 0;
   }

   public static boolean isThreadRunningNative(int var0) {
      return (var0 & 4194304) != 0;
   }

   public static State toThreadState(int var0) {
      int var1 = var0 & 1048575;
      return VM.toThreadState(var1);
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("management");
            return null;
         }
      });
      jvm = new com.frojasg1.sun.management.VMManagementImpl();
   }

   public interface LoggingMXBean extends PlatformLoggingMXBean, java.util.logging.LoggingMXBean {
   }

   static class PlatformLoggingImpl implements ManagementFactoryHelper.LoggingMXBean {
      static final PlatformLoggingMXBean instance = new ManagementFactoryHelper.PlatformLoggingImpl();
      static final String LOGGING_MXBEAN_NAME = "java.util.logging:type=Logging";
      private volatile ObjectName objname;

      PlatformLoggingImpl() {
      }

      public ObjectName getObjectName() {
         ObjectName var1 = this.objname;
         if (var1 == null) {
            synchronized(this) {
               var1 = this.objname;
               if (var1 == null) {
                  var1 = Util.newObjectName("java.util.logging:type=Logging");
                  this.objname = var1;
               }
            }
         }

         return var1;
      }

      public List<String> getLoggerNames() {
         return LoggingSupport.getLoggerNames();
      }

      public String getLoggerLevel(String var1) {
         return LoggingSupport.getLoggerLevel(var1);
      }

      public void setLoggerLevel(String var1, String var2) {
         LoggingSupport.setLoggerLevel(var1, var2);
      }

      public String getParentLoggerName(String var1) {
         return LoggingSupport.getParentLoggerName(var1);
      }
   }
}
