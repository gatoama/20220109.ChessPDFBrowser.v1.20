package com.frojasg1.sun.audio;

import com.frojasg1.sun.audio.AudioData;

import java.io.ByteArrayInputStream;

public class AudioDataStream extends ByteArrayInputStream {
   private final com.frojasg1.sun.audio.AudioData ad;

   public AudioDataStream(com.frojasg1.sun.audio.AudioData var1) {
      super(var1.buffer);
      this.ad = var1;
   }

   final AudioData getAudioData() {
      return this.ad;
   }
}
