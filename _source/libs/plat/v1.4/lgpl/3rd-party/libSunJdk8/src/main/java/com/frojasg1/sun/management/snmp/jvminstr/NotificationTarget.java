package com.frojasg1.sun.management.snmp.jvminstr;

import java.net.InetAddress;

public interface NotificationTarget {
   InetAddress getAddress();

   int getPort();

   String getCommunity();
}
