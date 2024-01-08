package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpOid;
import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpMibTable;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMeta;

import java.io.Serializable;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class JvmMemManagerTableMeta extends SnmpMibTable implements Serializable {
   static final long serialVersionUID = 5026520607518015233L;
   private com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMeta node;
   protected SnmpStandardObjectServer objectserver;

   public JvmMemManagerTableMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1);
      this.objectserver = var2;
   }

   protected com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMeta createJvmMemManagerEntryMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemManagerEntryMeta(var3, this.objectserver);
   }

   public void createNewEntry(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
      if (this.factory != null) {
         this.factory.createNewEntry(var1, var2, var3, this);
      } else {
         throw new SnmpStatusException(6);
      }
   }

   public boolean isRegistrationRequired() {
      return false;
   }

   public void registerEntryNode(SnmpMib var1, MBeanServer var2) {
      this.node = this.createJvmMemManagerEntryMetaNode("JvmMemManagerEntry", "JvmMemManagerTable", var1, var2);
   }

   public synchronized void addEntry(SnmpOid var1, ObjectName var2, Object var3) throws SnmpStatusException {
      if (!(var3 instanceof com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean)) {
         throw new ClassCastException("Entries for Table \"JvmMemManagerTable\" must implement the \"JvmMemManagerEntryMBean\" interface.");
      } else {
         super.addEntry(var1, var2, var3);
      }
   }

   public void get(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
      com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean var4 = (com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean)this.getEntry(var2);
      synchronized(this) {
         this.node.setInstance(var4);
         this.node.get(var1, var3);
      }
   }

   public void set(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
      if (var1.getSize() != 0) {
         com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean var4 = (com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean)this.getEntry(var2);
         synchronized(this) {
            this.node.setInstance(var4);
            this.node.set(var1, var3);
         }
      }
   }

   public void check(SnmpMibSubRequest var1, SnmpOid var2, int var3) throws SnmpStatusException {
      if (var1.getSize() != 0) {
         com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean var4 = (com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean)this.getEntry(var2);
         synchronized(this) {
            this.node.setInstance(var4);
            this.node.check(var1, var3);
         }
      }
   }

   public void validateVarEntryId(SnmpOid var1, long var2, Object var4) throws SnmpStatusException {
      this.node.validateVarId(var2, var4);
   }

   public boolean isReadableEntryId(SnmpOid var1, long var2, Object var4) throws SnmpStatusException {
      return this.node.isReadable(var2);
   }

   public long getNextVarEntryId(SnmpOid var1, long var2, Object var4) throws SnmpStatusException {
      long var5;
      for(var5 = this.node.getNextVarId(var2, var4); !this.isReadableEntryId(var1, var5, var4); var5 = this.node.getNextVarId(var5, var4)) {
      }

      return var5;
   }

   public boolean skipEntryVariable(SnmpOid var1, long var2, Object var4, int var5) {
      try {
         com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerEntryMBean var6 = (JvmMemManagerEntryMBean)this.getEntry(var1);
         synchronized(this) {
            this.node.setInstance(var6);
            return this.node.skipVariable(var2, var4, var5);
         }
      } catch (SnmpStatusException var10) {
         return false;
      }
   }
}
