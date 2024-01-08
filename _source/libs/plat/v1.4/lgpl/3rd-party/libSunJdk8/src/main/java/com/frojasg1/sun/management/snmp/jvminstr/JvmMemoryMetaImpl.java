package com.frojasg1.sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;

import com.frojasg1.sun.management.snmp.jvminstr.JvmMemGCTableMetaImpl;
import com.frojasg1.sun.management.snmp.jvminstr.JvmMemManagerTableMetaImpl;
import com.frojasg1.sun.management.snmp.jvminstr.JvmMemMgrPoolRelTableMetaImpl;
import com.frojasg1.sun.management.snmp.jvminstr.JvmMemPoolTableMetaImpl;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemGCTableMeta;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemManagerTableMeta;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemMgrPoolRelTableMeta;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemPoolTableMeta;
import com.frojasg1.sun.management.snmp.jvmmib.JvmMemoryMeta;

public class JvmMemoryMetaImpl extends JvmMemoryMeta {
   static final long serialVersionUID = -6500448253825893071L;

   public JvmMemoryMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
   }

   protected JvmMemManagerTableMeta createJvmMemManagerTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemManagerTableMetaImpl(var3, this.objectserver);
   }

   protected JvmMemGCTableMeta createJvmMemGCTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemGCTableMetaImpl(var3, this.objectserver);
   }

   protected JvmMemPoolTableMeta createJvmMemPoolTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemPoolTableMetaImpl(var3, this.objectserver);
   }

   protected JvmMemMgrPoolRelTableMeta createJvmMemMgrPoolRelTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmMemMgrPoolRelTableMetaImpl(var3, this.objectserver);
   }
}
