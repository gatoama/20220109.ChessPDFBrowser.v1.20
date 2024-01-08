package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;

public interface JvmRTInputArgsEntryMBean {
   String getJvmRTInputArgsItem() throws SnmpStatusException;

   Integer getJvmRTInputArgsIndex() throws SnmpStatusException;
}
