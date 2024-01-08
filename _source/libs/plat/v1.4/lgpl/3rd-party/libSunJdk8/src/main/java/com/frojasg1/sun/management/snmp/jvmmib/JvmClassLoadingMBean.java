package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmClassesVerboseLevel;

public interface JvmClassLoadingMBean {
   com.frojasg1.sun.management.snmp.jvmmib.EnumJvmClassesVerboseLevel getJvmClassesVerboseLevel() throws SnmpStatusException;

   void setJvmClassesVerboseLevel(com.frojasg1.sun.management.snmp.jvmmib.EnumJvmClassesVerboseLevel var1) throws SnmpStatusException;

   void checkJvmClassesVerboseLevel(EnumJvmClassesVerboseLevel var1) throws SnmpStatusException;

   Long getJvmClassesUnloadedCount() throws SnmpStatusException;

   Long getJvmClassesTotalLoadedCount() throws SnmpStatusException;

   Long getJvmClassesLoadedCount() throws SnmpStatusException;
}
