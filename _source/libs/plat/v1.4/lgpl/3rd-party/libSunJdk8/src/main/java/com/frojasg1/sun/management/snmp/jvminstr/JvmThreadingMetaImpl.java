package com.frojasg1.sun.management.snmp.jvminstr;

import com.sun.jmx.snmp.agent.SnmpMib;
import com.sun.jmx.snmp.agent.SnmpStandardObjectServer;
import javax.management.MBeanServer;

import com.frojasg1.sun.management.snmp.jvminstr.JvmThreadInstanceTableMetaImpl;
import com.frojasg1.sun.management.snmp.jvmmib.JvmThreadInstanceTableMeta;
import com.frojasg1.sun.management.snmp.jvmmib.JvmThreadingMeta;

public class JvmThreadingMetaImpl extends JvmThreadingMeta {
   static final long serialVersionUID = -2104788458393251457L;

   public JvmThreadingMetaImpl(SnmpMib var1, SnmpStandardObjectServer var2) {
      super(var1, var2);
   }

   protected JvmThreadInstanceTableMeta createJvmThreadInstanceTableMetaNode(String var1, String var2, SnmpMib var3, MBeanServer var4) {
      return new JvmThreadInstanceTableMetaImpl(var3, this.objectserver);
   }
}
