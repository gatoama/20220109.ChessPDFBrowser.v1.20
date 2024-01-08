package com.frojasg1.sun.java2d.cmm;

import com.frojasg1.sun.java2d.cmm.ProfileActivator;

import java.awt.color.ProfileDataException;
import java.util.Iterator;
import java.util.Vector;

public class ProfileDeferralMgr {
   public static boolean deferring = true;
   private static Vector<com.frojasg1.sun.java2d.cmm.ProfileActivator> aVector;

   public ProfileDeferralMgr() {
   }

   public static void registerDeferral(com.frojasg1.sun.java2d.cmm.ProfileActivator var0) {
      if (deferring) {
         if (aVector == null) {
            aVector = new Vector(3, 3);
         }

         aVector.addElement(var0);
      }
   }

   public static void unregisterDeferral(com.frojasg1.sun.java2d.cmm.ProfileActivator var0) {
      if (deferring) {
         if (aVector != null) {
            aVector.removeElement(var0);
         }
      }
   }

   public static void activateProfiles() {
      deferring = false;
      if (aVector != null) {
         int var1 = aVector.size();
         Iterator var2 = aVector.iterator();

         while(var2.hasNext()) {
            com.frojasg1.sun.java2d.cmm.ProfileActivator var3 = (ProfileActivator)var2.next();

            try {
               var3.activate();
            } catch (ProfileDataException var5) {
            }
         }

         aVector.removeAllElements();
         aVector = null;
      }
   }
}
