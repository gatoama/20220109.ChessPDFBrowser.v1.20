package com.frojasg1.sun.misc;

import com.frojasg1.sun.misc.ExtensionInfo;
import com.frojasg1.sun.misc.ExtensionInstallationException;

public interface ExtensionInstallationProvider {
   boolean installExtension(com.frojasg1.sun.misc.ExtensionInfo var1, ExtensionInfo var2) throws ExtensionInstallationException;
}
