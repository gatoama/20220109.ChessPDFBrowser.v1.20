package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class AWTPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public AWTPerm() {
      super("AWTPermission", "java.awt.AWTPermission", new String[]{"accessClipboard", "accessEventQueue", "accessSystemTray", "createRobot", "fullScreenExclusive", "listenToAllAWTEvents", "readDisplayPixels", "replaceKeyboardFocusManager", "setAppletStub", "setWindowAlwaysOnTop", "showWindowWithoutWarningBanner", "toolkitModality", "watchMousePointer"}, (String[])null);
   }
}
