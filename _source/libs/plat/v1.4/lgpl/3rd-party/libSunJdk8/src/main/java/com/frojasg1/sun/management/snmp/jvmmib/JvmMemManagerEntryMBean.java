package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.frojasg1.sun.management.snmp.jvmmib.EnumJvmMemManagerState;

public interface JvmMemManagerEntryMBean {
   EnumJvmMemManagerState getJvmMemManagerState() throws SnmpStatusException;

   String getJvmMemManagerName() throws SnmpStatusException;

   Integer getJvmMemManagerIndex() throws SnmpStatusException;
}
