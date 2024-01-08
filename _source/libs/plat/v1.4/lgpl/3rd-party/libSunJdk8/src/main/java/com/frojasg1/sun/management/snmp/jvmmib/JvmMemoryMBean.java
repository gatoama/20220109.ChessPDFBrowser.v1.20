package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemoryGCCall;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemoryGCVerboseLevel;

public interface JvmMemoryMBean {
   Long getJvmMemoryNonHeapMaxSize() throws SnmpStatusException;

   Long getJvmMemoryNonHeapCommitted() throws SnmpStatusException;

   Long getJvmMemoryNonHeapUsed() throws SnmpStatusException;

   Long getJvmMemoryNonHeapInitSize() throws SnmpStatusException;

   Long getJvmMemoryHeapMaxSize() throws SnmpStatusException;

   Long getJvmMemoryHeapCommitted() throws SnmpStatusException;

   com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemoryGCCall getJvmMemoryGCCall() throws SnmpStatusException;

   void setJvmMemoryGCCall(com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemoryGCCall var1) throws SnmpStatusException;

   void checkJvmMemoryGCCall(EnumJvmMemoryGCCall var1) throws SnmpStatusException;

   Long getJvmMemoryHeapUsed() throws SnmpStatusException;

   com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemoryGCVerboseLevel getJvmMemoryGCVerboseLevel() throws SnmpStatusException;

   void setJvmMemoryGCVerboseLevel(com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemoryGCVerboseLevel var1) throws SnmpStatusException;

   void checkJvmMemoryGCVerboseLevel(EnumJvmMemoryGCVerboseLevel var1) throws SnmpStatusException;

   Long getJvmMemoryHeapInitSize() throws SnmpStatusException;

   Long getJvmMemoryPendingFinalCount() throws SnmpStatusException;
}
