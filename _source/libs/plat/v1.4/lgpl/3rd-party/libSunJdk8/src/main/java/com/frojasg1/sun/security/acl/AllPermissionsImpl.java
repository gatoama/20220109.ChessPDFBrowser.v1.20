package com.frojasg1.sun.security.acl;

import com.frojasg1.sun.security.acl.PermissionImpl;

import java.security.acl.Permission;

public class AllPermissionsImpl extends PermissionImpl {
   public AllPermissionsImpl(String var1) {
      super(var1);
   }

   public boolean equals(Permission var1) {
      return true;
   }
}
