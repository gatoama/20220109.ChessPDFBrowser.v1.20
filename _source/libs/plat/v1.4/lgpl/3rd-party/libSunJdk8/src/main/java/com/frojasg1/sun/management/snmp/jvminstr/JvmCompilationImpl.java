package com.frojasg1.sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;

import com.frojasg1.sun.management.snmp.jvminstr.JVM_MANAGEMENT_MIB_IMPL;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmJITCompilerTimeMonitoring;
import com.frojasg1.sun.management.snmp.jvmmib.JvmCompilationMBean;

public class JvmCompilationImpl implements JvmCompilationMBean {
   static final EnumJvmJITCompilerTimeMonitoring JvmJITCompilerTimeMonitoringSupported = new EnumJvmJITCompilerTimeMonitoring("supported");
   static final EnumJvmJITCompilerTimeMonitoring JvmJITCompilerTimeMonitoringUnsupported = new EnumJvmJITCompilerTimeMonitoring("unsupported");

   public JvmCompilationImpl(SnmpMib var1) {
   }

   public JvmCompilationImpl(SnmpMib var1, MBeanServer var2) {
   }

   private static CompilationMXBean getCompilationMXBean() {
      return ManagementFactory.getCompilationMXBean();
   }

   public EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring() throws SnmpStatusException {
      return getCompilationMXBean().isCompilationTimeMonitoringSupported() ? JvmJITCompilerTimeMonitoringSupported : JvmJITCompilerTimeMonitoringUnsupported;
   }

   public Long getJvmJITCompilerTimeMs() throws SnmpStatusException {
      long var1;
      if (getCompilationMXBean().isCompilationTimeMonitoringSupported()) {
         var1 = getCompilationMXBean().getTotalCompilationTime();
      } else {
         var1 = 0L;
      }

      return new Long(var1);
   }

   public String getJvmJITCompilerName() throws SnmpStatusException {
      return JVM_MANAGEMENT_MIB_IMPL.validJavaObjectNameTC(getCompilationMXBean().getName());
   }
}
