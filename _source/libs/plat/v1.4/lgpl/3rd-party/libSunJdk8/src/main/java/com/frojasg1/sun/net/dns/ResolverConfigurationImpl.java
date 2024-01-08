package com.frojasg1.sun.net.dns;

import com.frojasg1.sun.net.dns.OptionsImpl;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

public class ResolverConfigurationImpl extends com.frojasg1.sun.net.dns.ResolverConfiguration {
   private static Object lock = new Object();
   private final Options opts = new com.frojasg1.sun.net.dns.OptionsImpl();
   private static boolean changed = false;
   private static long lastRefresh = -1L;
   private static final int TIMEOUT = 120000;
   private static String os_searchlist;
   private static String os_nameservers;
   private static LinkedList<String> searchlist;
   private static LinkedList<String> nameservers;

   private LinkedList<String> stringToList(String var1) {
      LinkedList var2 = new LinkedList();
      StringTokenizer var3 = new StringTokenizer(var1, ", ");

      while(var3.hasMoreTokens()) {
         String var4 = var3.nextToken();
         if (!var2.contains(var4)) {
            var2.add(var4);
         }
      }

      return var2;
   }

   private void loadConfig() {
      assert Thread.holdsLock(lock);

      if (changed) {
         changed = false;
      } else if (lastRefresh >= 0L) {
         long var1 = System.currentTimeMillis();
         if (var1 - lastRefresh < 120000L) {
            return;
         }
      }

      loadDNSconfig0();
      lastRefresh = System.currentTimeMillis();
      searchlist = this.stringToList(os_searchlist);
      nameservers = this.stringToList(os_nameservers);
      os_searchlist = null;
      os_nameservers = null;
   }

   ResolverConfigurationImpl() {
   }

   public List<String> searchlist() {
      synchronized(lock) {
         this.loadConfig();
         return (List)searchlist.clone();
      }
   }

   public List<String> nameservers() {
      synchronized(lock) {
         this.loadConfig();
         return (List)nameservers.clone();
      }
   }

   public Options options() {
      return this.opts;
   }

   static native void init0();

   static native void loadDNSconfig0();

   static native int notifyAddrChange0();

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            System.loadLibrary("net");
            return null;
         }
      });
      init0();
      ResolverConfigurationImpl.AddressChangeListener var0 = new ResolverConfigurationImpl.AddressChangeListener();
      var0.setDaemon(true);
      var0.start();
   }

   static class AddressChangeListener extends Thread {
      AddressChangeListener() {
      }

      public void run() {
         while(ResolverConfigurationImpl.notifyAddrChange0() == 0) {
            synchronized(ResolverConfigurationImpl.lock) {
               ResolverConfigurationImpl.changed = true;
            }
         }

      }
   }
}
