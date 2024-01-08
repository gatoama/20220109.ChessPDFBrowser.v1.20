package com.frojasg1.sun.security.krb5.internal.tools;

import java.net.InetAddress;
import com.frojasg1.sun.security.krb5.EncryptionKey;
import com.frojasg1.sun.security.krb5.RealmException;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.ccache.Credentials;
import com.frojasg1.sun.security.krb5.internal.ccache.CredentialsCache;
import com.frojasg1.sun.security.krb5.internal.crypto.EType;
import com.frojasg1.sun.security.krb5.internal.ktab.KeyTab;
import com.frojasg1.sun.security.krb5.internal.ktab.KeyTabEntry;

public class Klist {
   Object target;
   char[] options = new char[4];
   String name;
   char action;
   private static boolean DEBUG;

   public Klist() {
   }

   public static void main(String[] var0) {
      Klist var1 = new Klist();
      if (var0 != null && var0.length != 0) {
         var1.processArgs(var0);
      } else {
         var1.action = 'c';
      }

      switch(var1.action) {
      case 'c':
         if (var1.name == null) {
            var1.target = CredentialsCache.getInstance();
            var1.name = CredentialsCache.cacheName();
         } else {
            var1.target = CredentialsCache.getInstance(var1.name);
         }

         if (var1.target != null) {
            var1.displayCache();
         } else {
            var1.displayMessage("Credentials cache");
            System.exit(-1);
         }
         break;
      case 'k':
         KeyTab var2 = KeyTab.getInstance(var1.name);
         if (var2.isMissing()) {
            System.out.println("KeyTab " + var1.name + " not found.");
            System.exit(-1);
         } else if (!var2.isValid()) {
            System.out.println("KeyTab " + var1.name + " format not supported.");
            System.exit(-1);
         }

         var1.target = var2;
         var1.name = var2.tabName();
         var1.displayTab();
         break;
      default:
         if (var1.name != null) {
            var1.printHelp();
            System.exit(-1);
         } else {
            var1.target = CredentialsCache.getInstance();
            var1.name = CredentialsCache.cacheName();
            if (var1.target != null) {
               var1.displayCache();
            } else {
               var1.displayMessage("Credentials cache");
               System.exit(-1);
            }
         }
      }

   }

   void processArgs(String[] var1) {
      for(int var3 = 0; var3 < var1.length; ++var3) {
         Character var2;
         if (var1[var3].length() >= 2 && var1[var3].startsWith("-")) {
            var2 = new Character(var1[var3].charAt(1));
            switch(var2) {
            case 'K':
               this.options[1] = 'K';
               break;
            case 'a':
               this.options[2] = 'a';
               break;
            case 'c':
               this.action = 'c';
               break;
            case 'e':
               this.options[0] = 'e';
               break;
            case 'f':
               this.options[1] = 'f';
               break;
            case 'k':
               this.action = 'k';
               break;
            case 'n':
               this.options[3] = 'n';
               break;
            case 't':
               this.options[2] = 't';
               break;
            default:
               this.printHelp();
               System.exit(-1);
            }
         } else if (!var1[var3].startsWith("-") && var3 == var1.length - 1) {
            this.name = var1[var3];
            var2 = null;
         } else {
            this.printHelp();
            System.exit(-1);
         }
      }

   }

   void displayTab() {
      KeyTab var1 = (KeyTab)this.target;
      KeyTabEntry[] var2 = var1.getEntries();
      if (var2.length == 0) {
         System.out.println("\nKey tab: " + this.name + ",  0 entries found.\n");
      } else {
         if (var2.length == 1) {
            System.out.println("\nKey tab: " + this.name + ", " + var2.length + " entry found.\n");
         } else {
            System.out.println("\nKey tab: " + this.name + ", " + var2.length + " entries found.\n");
         }

         for(int var3 = 0; var3 < var2.length; ++var3) {
            System.out.println("[" + (var3 + 1) + "] Service principal: " + var2[var3].getService().toString());
            System.out.println("\t KVNO: " + var2[var3].getKey().getKeyVersionNumber());
            EncryptionKey var4;
            if (this.options[0] == 'e') {
               var4 = var2[var3].getKey();
               System.out.println("\t Key type: " + var4.getEType());
            }

            if (this.options[1] == 'K') {
               var4 = var2[var3].getKey();
               System.out.println("\t Key: " + var2[var3].getKeyString());
            }

            if (this.options[2] == 't') {
               System.out.println("\t Time stamp: " + this.format(var2[var3].getTimeStamp()));
            }
         }
      }

   }

   void displayCache() {
      CredentialsCache var1 = (CredentialsCache)this.target;
      Credentials[] var2 = var1.getCredsList();
      if (var2 == null) {
         System.out.println("No credentials available in the cache " + this.name);
         System.exit(-1);
      }

      System.out.println("\nCredentials cache: " + this.name);
      String var3 = var1.getPrimaryPrincipal().toString();
      int var4 = var2.length;
      if (var4 == 1) {
         System.out.println("\nDefault principal: " + var3 + ", " + var2.length + " entry found.\n");
      } else {
         System.out.println("\nDefault principal: " + var3 + ", " + var2.length + " entries found.\n");
      }

      if (var2 != null) {
         for(int var5 = 0; var5 < var2.length; ++var5) {
            try {
               String var6;
               if (var2[var5].getStartTime() != null) {
                  var6 = this.format(var2[var5].getStartTime());
               } else {
                  var6 = this.format(var2[var5].getAuthTime());
               }

               String var7 = this.format(var2[var5].getEndTime());
               String var9 = var2[var5].getServicePrincipal().toString();
               System.out.println("[" + (var5 + 1) + "]  Service Principal:  " + var9);
               System.out.println("     Valid starting:     " + var6);
               System.out.println("     Expires:            " + var7);
               if (var2[var5].getRenewTill() != null) {
                  String var8 = this.format(var2[var5].getRenewTill());
                  System.out.println("     Renew until:        " + var8);
               }

               if (this.options[0] == 'e') {
                  String var10 = EType.toString(var2[var5].getEType());
                  String var11 = EType.toString(var2[var5].getTktEType());
                  System.out.println("     EType (skey, tkt):  " + var10 + ", " + var11);
               }

               if (this.options[1] == 'f') {
                  System.out.println("     Flags:              " + var2[var5].getTicketFlags().toString());
               }

               if (this.options[2] == 'a') {
                  boolean var18 = true;
                  InetAddress[] var19 = var2[var5].setKrbCreds().getClientAddresses();
                  if (var19 != null) {
                     InetAddress[] var12 = var19;
                     int var13 = var19.length;

                     for(int var14 = 0; var14 < var13; ++var14) {
                        InetAddress var15 = var12[var14];
                        String var16;
                        if (this.options[3] == 'n') {
                           var16 = var15.getHostAddress();
                        } else {
                           var16 = var15.getCanonicalHostName();
                        }

                        System.out.println("     " + (var18 ? "Addresses:" : "          ") + "       " + var16);
                        var18 = false;
                     }
                  } else {
                     System.out.println("     [No host addresses info]");
                  }
               }
            } catch (RealmException var17) {
               System.out.println("Error reading principal from the entry.");
               if (DEBUG) {
                  var17.printStackTrace();
               }

               System.exit(-1);
            }
         }
      } else {
         System.out.println("\nNo entries found.");
      }

   }

   void displayMessage(String var1) {
      if (this.name == null) {
         System.out.println("Default " + var1 + " not found.");
      } else {
         System.out.println(var1 + " " + this.name + " not found.");
      }

   }

   private String format(KerberosTime var1) {
      String var2 = var1.toDate().toString();
      return var2.substring(4, 7) + " " + var2.substring(8, 10) + ", " + var2.substring(24) + " " + var2.substring(11, 19);
   }

   void printHelp() {
      System.out.println("\nUsage: klist [[-c] [-f] [-e] [-a [-n]]] [-k [-t] [-K]] [name]");
      System.out.println("   name\t name of credentials cache or  keytab with the prefix. File-based cache or keytab's prefix is FILE:.");
      System.out.println("   -c specifies that credential cache is to be listed");
      System.out.println("   -k specifies that key tab is to be listed");
      System.out.println("   options for credentials caches:");
      System.out.println("\t-f \t shows credentials flags");
      System.out.println("\t-e \t shows the encryption type");
      System.out.println("\t-a \t shows addresses");
      System.out.println("\t  -n \t   do not reverse-resolve addresses");
      System.out.println("   options for keytabs:");
      System.out.println("\t-t \t shows keytab entry timestamps");
      System.out.println("\t-K \t shows keytab entry key value");
      System.out.println("\t-e \t shows keytab entry key type");
      System.out.println("\nUsage: java com.frojasg1.sun.security.krb5.tools.Klist -help for help.");
   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
