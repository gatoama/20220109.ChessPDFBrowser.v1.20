package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.SnmpStatusException;
import com.sun.jmx.snmp.SnmpString;
import com.sun.jmx.snmp.SnmpValue;
import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpMibEntry;
import com.sun.jmx.snmp.agent.SnmpMibNode;
import com.sun.jmx.snmp.agent.SnmpMibSubRequest;
import com.sun.jmx.snmp.agent.SnmpStandardMetaServer;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemMgrPoolRelEntryMBean;

import java.io.Serializable;

public class JvmMemMgrPoolRelEntryMeta extends SnmpMibEntry implements Serializable, SnmpStandardMetaServer {
   static final long serialVersionUID = 7414270971113459798L;
   protected com.frojasg1.sun.management.snmp.jvmmib.JvmMemMgrPoolRelEntryMBean node;
   protected SnmpStandardObjectServer objectserver = null;

   public JvmMemMgrPoolRelEntryMeta(SnmpMib var1, SnmpStandardObjectServer var2) {
      this.objectserver = var2;
      this.varList = new int[2];
      this.varList[0] = 3;
      this.varList[1] = 2;
      SnmpMibNode.sort(this.varList);
   }

   public SnmpValue get(long var1, Object var3) throws SnmpStatusException {
      switch((int)var1) {
      case 2:
         return new SnmpString(this.node.getJvmMemMgrRelManagerName());
      case 3:
         return new SnmpString(this.node.getJvmMemMgrRelPoolName());
      default:
         throw new SnmpStatusException(225);
      }
   }

   public SnmpValue set(SnmpValue var1, long var2, Object var4) throws SnmpStatusException {
      switch((int)var2) {
      case 2:
         throw new SnmpStatusException(17);
      case 3:
         throw new SnmpStatusException(17);
      default:
         throw new SnmpStatusException(17);
      }
   }

   public void check(SnmpValue var1, long var2, Object var4) throws SnmpStatusException {
      switch((int)var2) {
      case 2:
         throw new SnmpStatusException(17);
      case 3:
         throw new SnmpStatusException(17);
      default:
         throw new SnmpStatusException(17);
      }
   }

   protected void setInstance(JvmMemMgrPoolRelEntryMBean var1) {
      this.node = var1;
   }

   public void get(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      this.objectserver.get(this, var1, var2);
   }

   public void set(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      this.objectserver.set(this, var1, var2);
   }

   public void check(SnmpMibSubRequest var1, int var2) throws SnmpStatusException {
      this.objectserver.check(this, var1, var2);
   }

   public boolean isVariable(long var1) {
      switch((int)var1) {
      case 2:
      case 3:
         return true;
      default:
         return false;
      }
   }

   public boolean isReadable(long var1) {
      switch((int)var1) {
      case 2:
      case 3:
         return true;
      default:
         return false;
      }
   }

   public boolean skipVariable(long var1, Object var3, int var4) {
      return super.skipVariable(var1, var3, var4);
   }

   public String getAttributeName(long var1) throws SnmpStatusException {
      switch((int)var1) {
      case 2:
         return "JvmMemMgrRelManagerName";
      case 3:
         return "JvmMemMgrRelPoolName";
      default:
         throw new SnmpStatusException(225);
      }
   }
}
