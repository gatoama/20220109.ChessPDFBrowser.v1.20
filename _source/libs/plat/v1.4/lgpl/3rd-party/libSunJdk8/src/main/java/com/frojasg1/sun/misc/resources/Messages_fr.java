package com.frojasg1.sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages_fr extends ListResourceBundle {
   public Messages_fr() {
   }

   protected Object[][] getContents() {
      return new Object[][]{{"optpkg.versionerror", "ERREUR : le format de version utilisé pour le fichier JAR {0} n''est pas valide. Pour connaître le format de version pris en charge, consultez la documentation."}, {"optpkg.attributeerror", "ERREUR : l''attribut manifest JAR {0} obligatoire n''est pas défini dans le fichier JAR {1}."}, {"optpkg.attributeserror", "ERREUR : certains attributs manifest JAR obligatoires ne sont pas définis dans le fichier JAR {0}."}};
   }
}
