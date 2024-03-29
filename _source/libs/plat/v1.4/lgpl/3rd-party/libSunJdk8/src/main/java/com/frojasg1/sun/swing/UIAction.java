package com.frojasg1.sun.swing;

import java.beans.PropertyChangeListener;
import javax.swing.Action;

public abstract class UIAction implements Action {
   private String name;

   public UIAction(String var1) {
      this.name = var1;
   }

   public final String getName() {
      return this.name;
   }

   public Object getValue(String var1) {
      return var1 == "Name" ? this.name : null;
   }

   public void putValue(String var1, Object var2) {
   }

   public void setEnabled(boolean var1) {
   }

   public final boolean isEnabled() {
      return this.isEnabled((Object)null);
   }

   public boolean isEnabled(Object var1) {
      return true;
   }

   public void addPropertyChangeListener(PropertyChangeListener var1) {
   }

   public void removePropertyChangeListener(PropertyChangeListener var1) {
   }
}
