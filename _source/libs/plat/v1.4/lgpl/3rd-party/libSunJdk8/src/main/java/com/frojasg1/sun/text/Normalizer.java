package com.frojasg1.sun.text;

import java.text.Normalizer.Form;
import com.frojasg1.sun.text.normalizer.NormalizerBase;
import com.frojasg1.sun.text.normalizer.NormalizerImpl;

public final class Normalizer {
   public static final int UNICODE_3_2 = 262432;

   private Normalizer() {
   }

   public static String normalize(CharSequence var0, Form var1, int var2) {
      return NormalizerBase.normalize(var0.toString(), var1, var2);
   }

   public static boolean isNormalized(CharSequence var0, Form var1, int var2) {
      return NormalizerBase.isNormalized(var0.toString(), var1, var2);
   }

   public static final int getCombiningClass(int var0) {
      return NormalizerImpl.getCombiningClass(var0);
   }
}
