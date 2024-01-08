package com.frojasg1.sun.text;

import com.frojasg1.sun.text.CharArrayCodePointIterator;
import com.frojasg1.sun.text.CharSequenceCodePointIterator;
import com.frojasg1.sun.text.CharacterIteratorCodePointIterator;

import java.text.CharacterIterator;

public abstract class CodePointIterator {
   public static final int DONE = -1;

   public CodePointIterator() {
   }

   public abstract void setToStart();

   public abstract void setToLimit();

   public abstract int next();

   public abstract int prev();

   public abstract int charIndex();

   public static CodePointIterator create(char[] var0) {
      return new com.frojasg1.sun.text.CharArrayCodePointIterator(var0);
   }

   public static CodePointIterator create(char[] var0, int var1, int var2) {
      return new com.frojasg1.sun.text.CharArrayCodePointIterator(var0, var1, var2);
   }

   public static CodePointIterator create(CharSequence var0) {
      return new com.frojasg1.sun.text.CharSequenceCodePointIterator(var0);
   }

   public static CodePointIterator create(CharacterIterator var0) {
      return new com.frojasg1.sun.text.CharacterIteratorCodePointIterator(var0);
   }
}
