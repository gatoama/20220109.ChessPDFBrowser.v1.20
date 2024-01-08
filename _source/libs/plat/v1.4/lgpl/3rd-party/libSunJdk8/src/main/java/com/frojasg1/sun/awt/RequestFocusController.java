package com.frojasg1.sun.awt;

import com.frojasg1.sun.awt.CausedFocusEvent;

import java.awt.Component;

public interface RequestFocusController {
   boolean acceptRequestFocus(Component var1, Component var2, boolean var3, boolean var4, CausedFocusEvent.Cause var5);
}
