package com.frojasg1.sun.awt.im;

import com.frojasg1.sun.awt.im.ExecutableInputMethodManager;
import com.frojasg1.sun.awt.im.InputContext;
import com.frojasg1.sun.awt.im.InputMethodLocator;

import java.awt.Component;
import java.util.Locale;

public abstract class InputMethodManager {
   private static final String threadName = "AWT-InputMethodManager";
   private static final Object LOCK = new Object();
   private static InputMethodManager inputMethodManager;

   public InputMethodManager() {
   }

   public static final InputMethodManager getInstance() {
      if (inputMethodManager != null) {
         return inputMethodManager;
      } else {
         synchronized(LOCK) {
            if (inputMethodManager == null) {
               com.frojasg1.sun.awt.im.ExecutableInputMethodManager var1 = new com.frojasg1.sun.awt.im.ExecutableInputMethodManager();
               if (var1.hasMultipleInputMethods()) {
                  var1.initialize();
                  Thread var2 = new Thread(var1, "AWT-InputMethodManager");
                  var2.setDaemon(true);
                  var2.setPriority(6);
                  var2.start();
               }

               inputMethodManager = var1;
            }
         }

         return inputMethodManager;
      }
   }

   public abstract String getTriggerMenuString();

   public abstract void notifyChangeRequest(Component var1);

   public abstract void notifyChangeRequestByHotKey(Component var1);

   abstract void setInputContext(InputContext var1);

   abstract com.frojasg1.sun.awt.im.InputMethodLocator findInputMethod(Locale var1);

   abstract Locale getDefaultKeyboardLocale();

   abstract boolean hasMultipleInputMethods();
}
