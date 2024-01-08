package com.frojasg1.sun.security.krb5.internal.tools;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.security.auth.kerberos.KeyTab;
import com.frojasg1.sun.security.krb5.Config;
import com.frojasg1.sun.security.krb5.KrbAsReqBuilder;
import com.frojasg1.sun.security.krb5.KrbException;
import com.frojasg1.sun.security.krb5.PrincipalName;
import com.frojasg1.sun.security.krb5.RealmException;
import com.frojasg1.sun.security.krb5.internal.HostAddresses;
import com.frojasg1.sun.security.krb5.internal.KDCOptions;
import com.frojasg1.sun.security.krb5.internal.Krb5;
import com.frojasg1.sun.security.krb5.internal.ccache.Credentials;
import com.frojasg1.sun.security.krb5.internal.ccache.CredentialsCache;
import com.frojasg1.sun.security.krb5.internal.tools.KinitOptions;
import com.frojasg1.sun.security.util.Password;

public class Kinit {
   private com.frojasg1.sun.security.krb5.internal.tools.KinitOptions options;
   private static final boolean DEBUG;

   public static void main(String[] var0) {
      try {
         new Kinit(var0);
      } catch (Exception var3) {
         String var2 = null;
         if (var3 instanceof KrbException) {
            var2 = ((KrbException)var3).krbErrorMessage() + " " + ((KrbException)var3).returnCodeMessage();
         } else {
            var2 = var3.getMessage();
         }

         if (var2 != null) {
            System.err.println("Exception: " + var2);
         } else {
            System.out.println("Exception: " + var3);
         }

         var3.printStackTrace();
         System.exit(-1);
      }

   }

   private Kinit(String[] var1) throws IOException, RealmException, KrbException {
      if (var1 != null && var1.length != 0) {
         this.options = new com.frojasg1.sun.security.krb5.internal.tools.KinitOptions(var1);
      } else {
         this.options = new com.frojasg1.sun.security.krb5.internal.tools.KinitOptions();
      }

      String var2 = null;
      PrincipalName var3 = this.options.getPrincipal();
      if (var3 != null) {
         var2 = var3.toString();
      }

      if (DEBUG) {
         System.out.println("Principal is " + var3);
      }

      char[] var5 = this.options.password;
      boolean var6 = this.options.useKeytabFile();
      KrbAsReqBuilder var4;
      if (!var6) {
         if (var2 == null) {
            throw new IllegalArgumentException(" Can not obtain principal name");
         }

         if (var5 == null) {
            System.out.print("Password for " + var2 + ":");
            System.out.flush();
            var5 = Password.readPassword(System.in);
            if (DEBUG) {
               System.out.println(">>> Kinit console input " + new String(var5));
            }
         }

         var4 = new KrbAsReqBuilder(var3, var5);
      } else {
         if (DEBUG) {
            System.out.println(">>> Kinit using keytab");
         }

         if (var2 == null) {
            throw new IllegalArgumentException("Principal name must be specified.");
         }

         String var7 = this.options.keytabFileName();
         if (var7 != null && DEBUG) {
            System.out.println(">>> Kinit keytab file name: " + var7);
         }

         var4 = new KrbAsReqBuilder(var3, var7 == null ? KeyTab.getInstance() : KeyTab.getInstance(new File(var7)));
      }

      KDCOptions var12 = new KDCOptions();
      setOptions(1, this.options.forwardable, var12);
      setOptions(3, this.options.proxiable, var12);
      var4.setOptions(var12);
      String var8 = this.options.getKDCRealm();
      if (var8 == null) {
         var8 = Config.getInstance().getDefaultRealm();
      }

      if (DEBUG) {
         System.out.println(">>> Kinit realm name is " + var8);
      }

      PrincipalName var9 = PrincipalName.tgsService(var8, var8);
      var4.setTarget(var9);
      if (DEBUG) {
         System.out.println(">>> Creating KrbAsReq");
      }

      if (this.options.getAddressOption()) {
         var4.setAddresses(HostAddresses.getLocalAddresses());
      }

      var4.action();
      Credentials var10 = var4.getCCreds();
      var4.destroy();
      CredentialsCache var11 = CredentialsCache.create(var3, this.options.cachename);
      if (var11 == null) {
         throw new IOException("Unable to create the cache file " + this.options.cachename);
      } else {
         var11.update(var10);
         var11.save();
         if (this.options.password == null) {
            System.out.println("New ticket is stored in cache file " + this.options.cachename);
         } else {
            Arrays.fill(this.options.password, '0');
         }

         if (var5 != null) {
            Arrays.fill(var5, '0');
         }

         this.options = null;
      }
   }

   private static void setOptions(int var0, int var1, KDCOptions var2) {
      switch(var1) {
      case -1:
         var2.set(var0, false);
      case 0:
      default:
         break;
      case 1:
         var2.set(var0, true);
      }

   }

   static {
      DEBUG = Krb5.DEBUG;
   }
}
