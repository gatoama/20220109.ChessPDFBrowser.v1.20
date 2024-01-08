package com.frojasg1.sun.awt;

import com.frojasg1.sun.awt.FontDescriptor;

public class CharsetString {
   public char[] charsetChars;
   public int offset;
   public int length;
   public com.frojasg1.sun.awt.FontDescriptor fontDescriptor;

   public CharsetString(char[] var1, int var2, int var3, FontDescriptor var4) {
      this.charsetChars = var1;
      this.offset = var2;
      this.length = var3;
      this.fontDescriptor = var4;
   }
}
