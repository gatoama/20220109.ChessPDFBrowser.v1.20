package com.frojasg1.sun.management.snmp.util;

import com.frojasg1.sun.management.snmp.util.SnmpCachedData;
import com.frojasg1.sun.management.snmp.util.SnmpTableHandler;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public abstract class SnmpTableCache implements Serializable {
   protected long validity;
   protected transient WeakReference<com.frojasg1.sun.management.snmp.util.SnmpCachedData> datas;

   public SnmpTableCache() {
   }

   protected boolean isObsolete(com.frojasg1.sun.management.snmp.util.SnmpCachedData var1) {
      if (var1 == null) {
         return true;
      } else if (this.validity < 0L) {
         return false;
      } else {
         return System.currentTimeMillis() - var1.lastUpdated > this.validity;
      }
   }

   protected com.frojasg1.sun.management.snmp.util.SnmpCachedData getCachedDatas() {
      if (this.datas == null) {
         return null;
      } else {
         com.frojasg1.sun.management.snmp.util.SnmpCachedData var1 = (com.frojasg1.sun.management.snmp.util.SnmpCachedData)this.datas.get();
         return var1 != null && !this.isObsolete(var1) ? var1 : null;
      }
   }

   protected synchronized com.frojasg1.sun.management.snmp.util.SnmpCachedData getTableDatas(Object var1) {
      com.frojasg1.sun.management.snmp.util.SnmpCachedData var2 = this.getCachedDatas();
      if (var2 != null) {
         return var2;
      } else {
         com.frojasg1.sun.management.snmp.util.SnmpCachedData var3 = this.updateCachedDatas(var1);
         if (this.validity != 0L) {
            this.datas = new WeakReference(var3);
         }

         return var3;
      }
   }

   protected abstract SnmpCachedData updateCachedDatas(Object var1);

   public abstract SnmpTableHandler getTableHandler();
}
