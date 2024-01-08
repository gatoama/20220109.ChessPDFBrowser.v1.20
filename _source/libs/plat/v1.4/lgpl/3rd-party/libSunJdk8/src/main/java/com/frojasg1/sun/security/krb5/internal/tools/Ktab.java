package com.frojasg1.sun.security.krb5.internal.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.internal.crypto.EType;
import com.frojasg1.sun.security.krb5.internal.ktab.KeyTab;
import com.frojasg1.sun.security.krb5.internal.ktab.KeyTabEntry;

public class Ktab {
   KeyTab table;
   char action;
   String name;
   String principal;
   boolean showEType;
   boolean showTime;
   int etype = -1;
   char[] password = null;
   boolean forced = false;
   boolean append = false;
   int vDel = -1;
   int vAdd = -1;

   public Ktab() {
   }

   public static void main(String[] var0) {
      Ktab var1 = new Ktab();
      if (var0.length == 1 && var0[0].equalsIgnoreCase("-help")) {
         var1.printHelp();
      } else {
         if (var0 != null && var0.length != 0) {
            var1.processArgs(var0);
         } else {
            var1.action = 'l';
         }

         var1.table = KeyTab.getInstance(var1.name);
         if (var1.table.isMissing() && var1.action != 'a') {
            if (var1.name == null) {
               System.out.println("No default key table exists.");
            } else {
               System.out.println("Key table " + var1.name + " does not exist.");
            }

            System.exit(-1);
         }

         if (!var1.table.isValid()) {
            if (var1.name == null) {
               System.out.println("The format of the default key table  is incorrect.");
            } else {
               System.out.println("The format of key table " + var1.name + " is incorrect.");
            }

            System.exit(-1);
         }

         switch(var1.action) {
         case 'a':
            var1.addEntry();
            break;
         case 'd':
            var1.deleteEntry();
            break;
         case 'l':
            var1.listKt();
            break;
         default:
            var1.error("A command must be provided");
         }

      }
   }

   void processArgs(String[] var1) {
      boolean var2 = false;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         String var4;
         byte var5;
         if (var1[var3].startsWith("-")) {
            var4 = var1[var3].toLowerCase(Locale.US);
            var5 = -1;
            switch(var4.hashCode()) {
            case -128108153:
               if (var4.equals("-append")) {
                  var5 = 8;
               }
               break;
            case 1492:
               if (var4.equals("-a")) {
                  var5 = 1;
               }
               break;
            case 1495:
               if (var4.equals("-d")) {
                  var5 = 2;
               }
               break;
            case 1496:
               if (var4.equals("-e")) {
                  var5 = 3;
               }
               break;
            case 1497:
               if (var4.equals("-f")) {
                  var5 = 7;
               }
               break;
            case 1502:
               if (var4.equals("-k")) {
                  var5 = 5;
               }
               break;
            case 1503:
               if (var4.equals("-l")) {
                  var5 = 0;
               }
               break;
            case 1505:
               if (var4.equals("-n")) {
                  var5 = 4;
               }
               break;
            case 1511:
               if (var4.equals("-t")) {
                  var5 = 6;
               }
            }

            switch(var5) {
            case 0:
               this.action = 'l';
               break;
            case 1:
               this.action = 'a';
               ++var3;
               if (var3 >= var1.length || var1[var3].startsWith("-")) {
                  this.error("A principal name must be specified after -a");
               }

               this.principal = var1[var3];
               break;
            case 2:
               this.action = 'd';
               ++var3;
               if (var3 >= var1.length || var1[var3].startsWith("-")) {
                  this.error("A principal name must be specified after -d");
               }

               this.principal = var1[var3];
               break;
            case 3:
               if (this.action == 'l') {
                  this.showEType = true;
               } else if (this.action != 'd') {
                  this.error(var1[var3] + " is not valid after -" + this.action);
               } else {
                  ++var3;
                  if (var3 >= var1.length || var1[var3].startsWith("-")) {
                     this.error("An etype must be specified after -e");
                  }

                  try {
                     this.etype = Integer.parseInt(var1[var3]);
                     if (this.etype <= 0) {
                        throw new NumberFormatException();
                     }
                  } catch (NumberFormatException var8) {
                     this.error(var1[var3] + " is not a valid etype");
                  }
               }
               break;
            case 4:
               ++var3;
               if (var3 >= var1.length || var1[var3].startsWith("-")) {
                  this.error("A KVNO must be specified after -n");
               }

               try {
                  this.vAdd = Integer.parseInt(var1[var3]);
                  if (this.vAdd < 0) {
                     throw new NumberFormatException();
                  }
               } catch (NumberFormatException var7) {
                  this.error(var1[var3] + " is not a valid KVNO");
               }
               break;
            case 5:
               ++var3;
               if (var3 >= var1.length || var1[var3].startsWith("-")) {
                  this.error("A keytab name must be specified after -k");
               }

               if (var1[var3].length() >= 5 && var1[var3].substring(0, 5).equalsIgnoreCase("FILE:")) {
                  this.name = var1[var3].substring(5);
                  break;
               }

               this.name = var1[var3];
               break;
            case 6:
               this.showTime = true;
               break;
            case 7:
               this.forced = true;
               break;
            case 8:
               this.append = true;
               break;
            default:
               this.error("Unknown command: " + var1[var3]);
            }
         } else {
            if (var2) {
               this.error("Useless extra argument " + var1[var3]);
            }

            if (this.action == 'a') {
               this.password = var1[var3].toCharArray();
            } else if (this.action == 'd') {
               var4 = var1[var3];
               var5 = -1;
               switch(var4.hashCode()) {
               case 96673:
                  if (var4.equals("all")) {
                     var5 = 0;
                  }
                  break;
               case 110119:
                  if (var4.equals("old")) {
                     var5 = 1;
                  }
               }

               switch(var5) {
               case 0:
                  this.vDel = -1;
                  break;
               case 1:
                  this.vDel = -2;
                  break;
               default:
                  try {
                     this.vDel = Integer.parseInt(var1[var3]);
                     if (this.vDel < 0) {
                        throw new NumberFormatException();
                     }
                  } catch (NumberFormatException var9) {
                     this.error(var1[var3] + " is not a valid KVNO");
                  }
               }
            } else {
               this.error("Useless extra argument " + var1[var3]);
            }

            var2 = true;
         }
      }

   }

   void addEntry() {
      PrincipalName var1 = null;

      try {
         var1 = new PrincipalName(this.principal);
      } catch (KrbException var6) {
         System.err.println("Failed to add " + this.principal + " to keytab.");
         var6.printStackTrace();
         System.exit(-1);
      }

      if (this.password == null) {
         try {
            BufferedReader var2 = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Password for " + var1.toString() + ":");
            System.out.flush();
            this.password = var2.readLine().toCharArray();
         } catch (IOException var5) {
            System.err.println("Failed to read the password.");
            var5.printStackTrace();
            System.exit(-1);
         }
      }

      try {
         this.table.addEntry(var1, this.password, this.vAdd, this.append);
         Arrays.fill(this.password, '0');
         this.table.save();
         System.out.println("Done!");
         System.out.println("Service key for " + this.principal + " is saved in " + this.table.tabName());
      } catch (KrbException var3) {
         System.err.println("Failed to add " + this.principal + " to keytab.");
         var3.printStackTrace();
         System.exit(-1);
      } catch (IOException var4) {
         System.err.println("Failed to save new entry.");
         var4.printStackTrace();
         System.exit(-1);
      }

   }

   void listKt() {
      System.out.println("Keytab name: " + this.table.tabName());
      KeyTabEntry[] var1 = this.table.getEntries();
      if (var1 != null && var1.length > 0) {
         String[][] var2 = new String[var1.length + 1][this.showTime ? 3 : 2];
         byte var3 = 0;
         String[] var10000 = var2[0];
         int var7 = var3 + 1;
         var10000[var3] = "KVNO";
         if (this.showTime) {
            var2[0][var7++] = "Timestamp";
         }

         var2[0][var7++] = "Principal";

         int var6;
         for(int var4 = 0; var4 < var1.length; ++var4) {
            var3 = 0;
            var10000 = var2[var4 + 1];
            var7 = var3 + 1;
            var10000[var3] = var1[var4].getKey().getKeyVersionNumber().toString();
            if (this.showTime) {
               var2[var4 + 1][var7++] = DateFormat.getDateTimeInstance(3, 3).format(new Date(var1[var4].getTimeStamp().getTime()));
            }

            String var5 = var1[var4].getService().toString();
            if (this.showEType) {
               var6 = var1[var4].getKey().getEType();
               var2[var4 + 1][var7++] = var5 + " (" + var6 + ":" + EType.toString(var6) + ")";
            } else {
               var2[var4 + 1][var7++] = var5;
            }
         }

         int[] var8 = new int[var7];

         int var9;
         for(var9 = 0; var9 < var7; ++var9) {
            for(var6 = 0; var6 <= var1.length; ++var6) {
               if (var2[var6][var9].length() > var8[var9]) {
                  var8[var9] = var2[var6][var9].length();
               }
            }

            if (var9 != 0) {
               var8[var9] = -var8[var9];
            }
         }

         for(var9 = 0; var9 < var7; ++var9) {
            System.out.printf("%" + var8[var9] + "s ", var2[0][var9]);
         }

         System.out.println();

         for(var9 = 0; var9 < var7; ++var9) {
            for(var6 = 0; var6 < Math.abs(var8[var9]); ++var6) {
               System.out.print("-");
            }

            System.out.print(" ");
         }

         System.out.println();

         for(var9 = 0; var9 < var1.length; ++var9) {
            for(var6 = 0; var6 < var7; ++var6) {
               System.out.printf("%" + var8[var6] + "s ", var2[var9 + 1][var6]);
            }

            System.out.println();
         }
      } else {
         System.out.println("0 entry.");
      }

   }

   void deleteEntry() {
      PrincipalName var1 = null;

      try {
         var1 = new PrincipalName(this.principal);
         if (!this.forced) {
            BufferedReader var3 = new BufferedReader(new InputStreamReader(System.in));
            System.out.print("Are you sure you want to delete service key(s) for " + var1.toString() + " (" + (this.etype == -1 ? "all etypes" : "etype=" + this.etype) + ", " + (this.vDel == -1 ? "all kvno" : (this.vDel == -2 ? "old kvno" : "kvno=" + this.vDel)) + ") in " + this.table.tabName() + "? (Y/[N]): ");
            System.out.flush();
            String var2 = var3.readLine();
            if (!var2.equalsIgnoreCase("Y") && !var2.equalsIgnoreCase("Yes")) {
               System.exit(0);
            }
         }
      } catch (KrbException var5) {
         System.err.println("Error occurred while deleting the entry. Deletion failed.");
         var5.printStackTrace();
         System.exit(-1);
      } catch (IOException var6) {
         System.err.println("Error occurred while deleting the entry.  Deletion failed.");
         var6.printStackTrace();
         System.exit(-1);
      }

      int var7 = this.table.deleteEntries(var1, this.etype, this.vDel);
      if (var7 == 0) {
         System.err.println("No matched entry in the keytab. Deletion fails.");
         System.exit(-1);
      } else {
         try {
            this.table.save();
         } catch (IOException var4) {
            System.err.println("Error occurs while saving the keytab. Deletion fails.");
            var4.printStackTrace();
            System.exit(-1);
         }

         System.out.println("Done! " + var7 + " entries removed.");
      }

   }

   void error(String... var1) {
      String[] var2 = var1;
      int var3 = var1.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String var5 = var2[var4];
         System.out.println("Error: " + var5 + ".");
      }

      this.printHelp();
      System.exit(-1);
   }

   void printHelp() {
      System.out.println("\nUsage: ktab <commands> <options>");
      System.out.println();
      System.out.println("Available commands:");
      System.out.println();
      System.out.println("-l [-e] [-t]\n    list the keytab name and entries. -e with etype, -t with timestamp.");
      System.out.println("-a <principal name> [<password>] [-n <kvno>] [-append]\n    add new key entries to the keytab for the given principal name with\n    optional <password>. If a <kvno> is specified, new keys' Key Version\n    Numbers equal to the value, otherwise, automatically incrementing\n    the Key Version Numbers. If -append is specified, new keys are\n    appended to the keytab, otherwise, old keys for the\n    same principal are removed.");
      System.out.println("-d <principal name> [-f] [-e <etype>] [<kvno> | all | old]\n    delete key entries from the keytab for the specified principal. If\n    <kvno> is specified, delete keys whose Key Version Numbers match\n    kvno. If \"all\" is specified, delete all keys. If \"old\" is specified,\n    delete all keys except those with the highest kvno. Default action\n    is \"all\". If <etype> is specified, only keys of this encryption type\n    are deleted. <etype> should be specified as the numberic value etype\n    defined in RFC 3961, section 8. A prompt to confirm the deletion is\n    displayed unless -f is specified.");
      System.out.println();
      System.out.println("Common option(s):");
      System.out.println();
      System.out.println("-k <keytab name>\n    specify keytab name and path with prefix FILE:");
   }
}
