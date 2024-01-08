package com.frojasg1.sun.security.krb5.internal.tools;

import java.io.FileInputStream;
import java.io.IOException;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.RealmException;
import com.frojasg1.sun.security.krb5.internal.KerberosTime;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.ccache.CCacheInputStream;
import com.frojasg1.sun.security.krb5.internal.ccache.FileCredentialsCache;

class KinitOptions {
   public boolean validate = false;
   public short forwardable = -1;
   public short proxiable = -1;
   public boolean renew = false;
   public KerberosTime lifetime;
   public KerberosTime renewable_lifetime;
   public String target_service;
   public String keytab_file;
   public String cachename;
   private PrincipalName principal;
   public String realm;
   char[] password = null;
   public boolean keytab;
   private boolean DEBUG;
   private boolean includeAddresses;
   private boolean useKeytab;
   private String ktabName;

   public KinitOptions() throws RuntimeException, RealmException {
      this.DEBUG = Krb5.DEBUG;
      this.includeAddresses = true;
      this.useKeytab = false;
      this.cachename = FileCredentialsCache.getDefaultCacheName();
      if (this.cachename == null) {
         throw new RuntimeException("default cache name error");
      } else {
         this.principal = this.getDefaultPrincipal();
      }
   }

   public void setKDCRealm(String var1) throws RealmException {
      this.realm = var1;
   }

   public String getKDCRealm() {
      return this.realm == null && this.principal != null ? this.principal.getRealmString() : null;
   }

   public KinitOptions(String[] var1) throws KrbException, RuntimeException, IOException {
      this.DEBUG = Krb5.DEBUG;
      this.includeAddresses = true;
      this.useKeytab = false;
      String var2 = null;

      for(int var3 = 0; var3 < var1.length; ++var3) {
         if (var1[var3].equals("-f")) {
            this.forwardable = 1;
         } else if (var1[var3].equals("-p")) {
            this.proxiable = 1;
         } else if (var1[var3].equals("-c")) {
            if (var1[var3 + 1].startsWith("-")) {
               throw new IllegalArgumentException("input format  not correct:  -c  option must be followed by the cache name");
            }

            ++var3;
            this.cachename = var1[var3];
            if (this.cachename.length() >= 5 && this.cachename.substring(0, 5).equalsIgnoreCase("FILE:")) {
               this.cachename = this.cachename.substring(5);
            }
         } else if (var1[var3].equals("-A")) {
            this.includeAddresses = false;
         } else if (var1[var3].equals("-k")) {
            this.useKeytab = true;
         } else if (var1[var3].equals("-t")) {
            if (this.ktabName != null) {
               throw new IllegalArgumentException("-t option/keytab file name repeated");
            }

            if (var3 + 1 >= var1.length) {
               throw new IllegalArgumentException("-t option requires keytab file name");
            }

            ++var3;
            this.ktabName = var1[var3];
            this.useKeytab = true;
         } else if (var1[var3].equalsIgnoreCase("-help")) {
            this.printHelp();
            System.exit(0);
         } else if (var2 == null) {
            var2 = var1[var3];

            try {
               this.principal = new PrincipalName(var2);
            } catch (Exception var5) {
               throw new IllegalArgumentException("invalid Principal name: " + var2 + var5.getMessage());
            }
         } else {
            if (this.password != null) {
               throw new IllegalArgumentException("too many parameters");
            }

            this.password = var1[var3].toCharArray();
         }
      }

      if (this.cachename == null) {
         this.cachename = FileCredentialsCache.getDefaultCacheName();
         if (this.cachename == null) {
            throw new RuntimeException("default cache name error");
         }
      }

      if (this.principal == null) {
         this.principal = this.getDefaultPrincipal();
      }

   }

   PrincipalName getDefaultPrincipal() {
      try {
         CCacheInputStream var7 = new CCacheInputStream(new FileInputStream(this.cachename));
         int var8;
         if ((var8 = var7.readVersion()) == 1284) {
            var7.readTag();
         } else if (var8 == 1281 || var8 == 1282) {
            var7.setNativeByteOrder();
         }

         PrincipalName var3 = var7.readPrincipal(var8);
         var7.close();
         if (this.DEBUG) {
            System.out.println(">>>KinitOptions principal name from the cache is :" + var3);
         }

         return var3;
      } catch (IOException var5) {
         if (this.DEBUG) {
            var5.printStackTrace();
         }
      } catch (RealmException var6) {
         if (this.DEBUG) {
            var6.printStackTrace();
         }
      }

      String var1 = System.getProperty("user.name");
      if (this.DEBUG) {
         System.out.println(">>>KinitOptions default username is :" + var1);
      }

      try {
         PrincipalName var2 = new PrincipalName(var1);
         return var2;
      } catch (RealmException var4) {
         if (this.DEBUG) {
            System.out.println("Exception in getting principal name " + var4.getMessage());
            var4.printStackTrace();
         }

         return null;
      }
   }

   void printHelp() {
      System.out.println("Usage: kinit [-A] [-f] [-p] [-c cachename] [[-k [-t keytab_file_name]] [principal] [password]");
      System.out.println("\tavailable options to Kerberos 5 ticket request:");
      System.out.println("\t    -A   do not include addresses");
      System.out.println("\t    -f   forwardable");
      System.out.println("\t    -p   proxiable");
      System.out.println("\t    -c   cache name (i.e., FILE:\\d:\\myProfiles\\mykrb5cache)");
      System.out.println("\t    -k   use keytab");
      System.out.println("\t    -t   keytab file name");
      System.out.println("\t    principal   the principal name (i.e., qweadf@ATHENA.MIT.EDU qweadf)");
      System.out.println("\t    password   the principal's Kerberos password");
   }

   public boolean getAddressOption() {
      return this.includeAddresses;
   }

   public boolean useKeytabFile() {
      return this.useKeytab;
   }

   public String keytabFileName() {
      return this.ktabName;
   }

   public PrincipalName getPrincipal() {
      return this.principal;
   }
}
