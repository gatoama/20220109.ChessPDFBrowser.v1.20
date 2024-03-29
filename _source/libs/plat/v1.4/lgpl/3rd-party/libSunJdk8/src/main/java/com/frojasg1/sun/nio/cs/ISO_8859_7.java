package com.frojasg1.sun.nio.cs;

import com.frojasg1.sun.nio.cs.HistoricallyNamedCharset;
import com.frojasg1.sun.nio.cs.SingleByte;
import com.frojasg1.sun.nio.cs.StandardCharsets;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;

public class ISO_8859_7 extends Charset implements HistoricallyNamedCharset {
   private static final String b2cTable = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f ‘’£€₯¦§¨©ͺ«¬\u00ad�―°±²³΄΅Ά·ΈΉΊ»Ό½ΎΏΐΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡ�ΣΤΥΦΧΨΩΪΫάέήίΰαβγδεζηθικλμνξοπρςστυφχψωϊϋόύώ�\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f";
   private static final char[] b2c = "\u0080\u0081\u0082\u0083\u0084\u0085\u0086\u0087\u0088\u0089\u008a\u008b\u008c\u008d\u008e\u008f\u0090\u0091\u0092\u0093\u0094\u0095\u0096\u0097\u0098\u0099\u009a\u009b\u009c\u009d\u009e\u009f ‘’£€₯¦§¨©ͺ«¬\u00ad�―°±²³΄΅Ά·ΈΉΊ»Ό½ΎΏΐΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡ�ΣΤΥΦΧΨΩΪΫάέήίΰαβγδεζηθικλμνξοπρςστυφχψωϊϋόύώ�\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\u007f".toCharArray();
   private static final char[] c2b = new char[1024];
   private static final char[] c2bIndex = new char[256];

   public ISO_8859_7() {
      super("ISO-8859-7", StandardCharsets.aliases_ISO_8859_7);
   }

   public String historicalName() {
      return "ISO8859_7";
   }

   public boolean contains(Charset var1) {
      return var1.name().equals("US-ASCII") || var1 instanceof ISO_8859_7;
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
