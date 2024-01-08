package com.frojasg1.sun.security.tools.policytool;

import com.frojasg1.sun.security.tools.policytool.Perm;

class AudioPerm extends com.frojasg1.sun.security.tools.policytool.Perm {
   public AudioPerm() {
      super("AudioPermission", "javax.sound.sampled.AudioPermission", new String[]{"play", "record"}, (String[])null);
   }
}
