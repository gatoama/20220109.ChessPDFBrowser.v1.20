package com.frojasg1.sun.misc;

import com.frojasg1.sun.usagetracker.UsageTrackerClient;

public class PostVMInitHook {
   public PostVMInitHook() {
   }

   public static void run() {
      trackJavaUsage();
   }

   private static void trackJavaUsage() {
      UsageTrackerClient var0 = new UsageTrackerClient();
      var0.run("VM start", System.getProperty("sun.java.command"));
   }
}
