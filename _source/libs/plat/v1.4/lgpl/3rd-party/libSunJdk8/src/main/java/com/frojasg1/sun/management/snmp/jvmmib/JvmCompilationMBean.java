package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmJITCompilerTimeMonitoring;

public interface JvmCompilationMBean {
   EnumJvmJITCompilerTimeMonitoring getJvmJITCompilerTimeMonitoring() throws SnmpStatusException;

   Long getJvmJITCompilerTimeMs() throws SnmpStatusException;

   String getJvmJITCompilerName() throws SnmpStatusException;
}
