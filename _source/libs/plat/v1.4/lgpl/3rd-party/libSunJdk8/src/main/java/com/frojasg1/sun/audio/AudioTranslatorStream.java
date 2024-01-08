package com.frojasg1.sun.audio;

import com.frojasg1.sun.audio.InvalidAudioFormatException;
import com.frojasg1.sun.audio.NativeAudioStream;

import java.io.IOException;
import java.io.InputStream;

public final class AudioTranslatorStream extends NativeAudioStream {
   private final int length = 0;

   public AudioTranslatorStream(InputStream var1) throws IOException {
      super(var1);
      throw new com.frojasg1.sun.audio.InvalidAudioFormatException();
   }

   public int getLength() {
      return 0;
   }
}
