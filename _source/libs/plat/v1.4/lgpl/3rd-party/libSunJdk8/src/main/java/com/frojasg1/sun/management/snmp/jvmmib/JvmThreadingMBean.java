package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmThreadContentionMonitoring;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmThreadCpuTimeMonitoring;

public interface JvmThreadingMBean {
   com.frojasg1.sun.management.snmp.jvmmib.EnumJvmThreadCpuTimeMonitoring getJvmThreadCpuTimeMonitoring() throws SnmpStatusException;

   void setJvmThreadCpuTimeMonitoring(com.frojasg1.sun.management.snmp.jvmmib.EnumJvmThreadCpuTimeMonitoring var1) throws SnmpStatusException;

   void checkJvmThreadCpuTimeMonitoring(EnumJvmThreadCpuTimeMonitoring var1) throws SnmpStatusException;

   com.frojasg1.sun.management.snmp.jvmmib.EnumJvmThreadContentionMonitoring getJvmThreadContentionMonitoring() throws SnmpStatusException;

   void setJvmThreadContentionMonitoring(com.frojasg1.sun.management.snmp.jvmmib.EnumJvmThreadContentionMonitoring var1) throws SnmpStatusException;

   void checkJvmThreadContentionMonitoring(EnumJvmThreadContentionMonitoring var1) throws SnmpStatusException;

   Long getJvmThreadTotalStartedCount() throws SnmpStatusException;

   Long getJvmThreadPeakCount() throws SnmpStatusException;

   Long getJvmThreadDaemonCount() throws SnmpStatusException;

   Long getJvmThreadCount() throws SnmpStatusException;

   Long getJvmThreadPeakCountReset() throws SnmpStatusException;

   void setJvmThreadPeakCountReset(Long var1) throws SnmpStatusException;

   void checkJvmThreadPeakCountReset(Long var1) throws SnmpStatusException;
}
