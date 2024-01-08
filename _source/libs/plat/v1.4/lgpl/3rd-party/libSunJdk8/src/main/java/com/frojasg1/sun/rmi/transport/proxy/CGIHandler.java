package com.frojasg1.sun.rmi.transport.proxy;

import com.frojasg1.sun.rmi.transport.proxy.CGIClientException;
import com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler;
import com.frojasg1.sun.rmi.transport.proxy.CGIForwardCommand;
import com.frojasg1.sun.rmi.transport.proxy.CGIGethostnameCommand;
import com.frojasg1.sun.rmi.transport.proxy.CGIPingCommand;
import com.frojasg1.sun.rmi.transport.proxy.CGIServerException;
import com.frojasg1.sun.rmi.transport.proxy.CGITryHostnameCommand;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Hashtable;

public final class CGIHandler {
   static int ContentLength;
   static String QueryString;
   static String RequestMethod;
   static String ServerName;
   static int ServerPort;
   private static com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler[] commands;
   private static Hashtable<String, com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler> commandLookup;

   private CGIHandler() {
   }

   public static void main(String[] var0) {
      try {
         int var3 = QueryString.indexOf("=");
         String var1;
         String var2;
         if (var3 == -1) {
            var1 = QueryString;
            var2 = "";
         } else {
            var1 = QueryString.substring(0, var3);
            var2 = QueryString.substring(var3 + 1);
         }

         com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler var4 = (com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler)commandLookup.get(var1);
         if (var4 != null) {
            try {
               var4.execute(var2);
            } catch (com.frojasg1.sun.rmi.transport.proxy.CGIClientException var6) {
               var6.printStackTrace();
               returnClientError(var6.getMessage());
            } catch (com.frojasg1.sun.rmi.transport.proxy.CGIServerException var7) {
               var7.printStackTrace();
               returnServerError(var7.getMessage());
            }
         } else {
            returnClientError("invalid command.");
         }
      } catch (Exception var8) {
         var8.printStackTrace();
         returnServerError("internal error: " + var8.getMessage());
      }

      System.exit(0);
   }

   private static void returnClientError(String var0) {
      System.out.println("Status: 400 Bad Request: " + var0);
      System.out.println("Content-type: text/html");
      System.out.println("");
      System.out.println("<HTML><HEAD><TITLE>Java RMI Client Error</TITLE></HEAD><BODY>");
      System.out.println("<H1>Java RMI Client Error</H1>");
      System.out.println("");
      System.out.println(var0);
      System.out.println("</BODY></HTML>");
      System.exit(1);
   }

   private static void returnServerError(String var0) {
      System.out.println("Status: 500 Server Error: " + var0);
      System.out.println("Content-type: text/html");
      System.out.println("");
      System.out.println("<HTML><HEAD><TITLE>Java RMI Server Error</TITLE></HEAD><BODY>");
      System.out.println("<H1>Java RMI Server Error</H1>");
      System.out.println("");
      System.out.println(var0);
      System.out.println("</BODY></HTML>");
      System.exit(1);
   }

   static {
      AccessController.doPrivileged(new PrivilegedAction<Void>() {
         public Void run() {
            CGIHandler.ContentLength = Integer.getInteger("CONTENT_LENGTH", 0);
            CGIHandler.QueryString = System.getProperty("QUERY_STRING", "");
            CGIHandler.RequestMethod = System.getProperty("REQUEST_METHOD", "");
            CGIHandler.ServerName = System.getProperty("SERVER_NAME", "");
            CGIHandler.ServerPort = Integer.getInteger("SERVER_PORT", 0);
            return null;
         }
      });
      commands = new com.frojasg1.sun.rmi.transport.proxy.CGICommandHandler[]{new com.frojasg1.sun.rmi.transport.proxy.CGIForwardCommand(), new com.frojasg1.sun.rmi.transport.proxy.CGIGethostnameCommand(), new com.frojasg1.sun.rmi.transport.proxy.CGIPingCommand(), new com.frojasg1.sun.rmi.transport.proxy.CGITryHostnameCommand()};
      commandLookup = new Hashtable();

      for(int var0 = 0; var0 < commands.length; ++var0) {
         commandLookup.put(commands[var0].getName(), commands[var0]);
      }

   }
}
