package com.frojasg1.sun.misc.resources;

import java.util.ListResourceBundle;

public final class Messages_es extends ListResourceBundle {
   public Messages_es() {
   }

   protected Object[][] getContents() {
      return new Object[][]{{"optpkg.versionerror", "ERROR: el formato del archivo JAR {0} pertenece a una versión no válida. Busque en la documentación el formato de una versión soportada."}, {"optpkg.attributeerror", "ERROR: el atributo obligatorio JAR manifest {0} no está definido en el archivo JAR {1}."}, {"optpkg.attributeserror", "ERROR: algunos atributos obligatorios JAR manifest no están definidos en el archivo JAR {0}."}};
   }
}