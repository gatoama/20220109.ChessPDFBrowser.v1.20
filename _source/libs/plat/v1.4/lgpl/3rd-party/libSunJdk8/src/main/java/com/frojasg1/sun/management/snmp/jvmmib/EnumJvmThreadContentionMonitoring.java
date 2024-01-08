package com.frojasg1.sun.management.snmp.jvmmib;

import com.sun.jmx.snmp.Enumerated;
import java.io.Serializable;
import java.util.Hashtable;

public class EnumJvmThreadContentionMonitoring extends Enumerated implements Serializable {
   static final long serialVersionUID = -6411827583604137210L;
   protected static Hashtable<Integer, String> intTable = new Hashtable();
   protected static Hashtable<String, Integer> stringTable = new Hashtable();

   public EnumJvmThreadContentionMonitoring(int var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmThreadContentionMonitoring(Integer var1) throws IllegalArgumentException {
      super(var1);
   }

   public EnumJvmThreadContentionMonitoring() throws IllegalArgumentException {
   }

   public EnumJvmThreadContentionMonitoring(String var1) throws IllegalArgumentException {
      super(var1);
   }

   protected Hashtable<Integer, String> getIntTable() {
      return intTable;
   }

   protected Hashtable<String, Integer> getStringTable() {
      return stringTable;
   }

   static {
      intTable.put(new Integer(3), "enabled");
      intTable.put(new Integer(4), "disabled");
      intTable.put(new Integer(1), "unsupported");
      stringTable.put("enabled", new Integer(3));
      stringTable.put("disabled", new Integer(4));
      stringTable.put("unsupported", new Integer(1));
   }
}
