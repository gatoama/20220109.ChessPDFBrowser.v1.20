package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.HistoricallyNamedCharset;
import com.frojasg1.sun.nio.cs.SingleByte;
import com.frojasg1.sun.nio.cs.StandardCharsets;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class KOI8_U extends Charset implements HistoricallyNamedCharset {
   private static final String b2cTable = "─│┌┐└┘├┤┬┴┼▀▄█▌▐░▒▓⌠■∙√≈≤≥ ⌡°²·÷═║╒ёє╔ії╗╘╙╚╛ґ╝╞╟╠╡ЁЄ╣ІЇ╦╧╨╩╪Ґ╬©юабцдефгхийклмнопярстужвьызшэщчъЮАБЦДЕФГХИЙКЛМНОПЯРСТУЖВЬЫЗШЭЩЧЪ\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
   private static final char[] b2c = "─│┌┐└┘├┤┬┴┼▀▄█▌▐░▒▓⌠■∙√≈≤≥ ⌡°²·÷═║╒ёє╔ії╗╘╙╚╛ґ╝╞╟╠╡ЁЄ╣ІЇ╦╧╨╩╪Ґ╬©юабцдефгхийклмнопярстужвьызшэщчъЮАБЦДЕФГХИЙКЛМНОПЯРСТУЖВЬЫЗШЭЩЧЪ\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
   private static final char[] c2b = new char[1280];
   private static final char[] c2bIndex = new char[256];

   public KOI8_U() {
      super("KOI8-U", StandardCharsets.aliases_KOI8_U);
   }

   public String historicalName() {
      return "KOI8_U";
   }

   public boolean contains(Charset var1) {
      return var1.name().equals("US-ASCII") || var1 instanceof KOI8_U;
   }

   public CharsetDecoder newDecoder() {
      return new com.frojasg1.sun.nio.cs.SingleByte.Decoder(this, b2c);
   }

   public CharsetEncoder newEncoder() {
      return new com.frojasg1.sun.nio.cs.SingleByte.Encoder(this, c2b, c2bIndex);
   }

   static {
      char[] var0 = b2c;
      Object var1 = null;
      SingleByte.initC2B(var0, (char[])var1, c2b, c2bIndex);
   }
}