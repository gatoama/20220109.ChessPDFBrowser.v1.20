package com.frojasg1.sun.nio.fs;

import java.util.regex.PatternSyntaxException;

public class Globs {
   private static final String regexMetaChars = ".^$+{[]|()";
   private static final String globMetaChars = "\\*?[{";
   private static char EOL = 0;

   private Globs() {
   }

   private static boolean isRegexMeta(char var0) {
      return ".^$+{[]|()".indexOf(var0) != -1;
   }

   private static boolean isGlobMeta(char var0) {
      return "\\*?[{".indexOf(var0) != -1;
   }

   private static char next(String var0, int var1) {
      return var1 < var0.length() ? var0.charAt(var1) : EOL;
   }

   private static String toRegexPattern(String var0, boolean var1) {
      boolean var2 = false;
      StringBuilder var3 = new StringBuilder("^");
      int var4 = 0;

      while(true) {
         while(var4 < var0.length()) {
            char var5 = var0.charAt(var4++);
            switch(var5) {
            case '*':
               if (next(var0, var4) == '*') {
                  var3.append(".*");
                  ++var4;
               } else if (var1) {
                  var3.append("[^\\\\]*");
               } else {
                  var3.append("[^/]*");
               }
               break;
            case ',':
               if (var2) {
                  var3.append(")|(?:");
               } else {
                  var3.append(',');
               }
               break;
            case '/':
               if (var1) {
                  var3.append("\\\\");
               } else {
                  var3.append(var5);
               }
               break;
            case '?':
               if (var1) {
                  var3.append("[^\\\\]");
               } else {
                  var3.append("[^/]");
               }
               break;
            case '[':
               if (var1) {
                  var3.append("[[^\\\\]&&[");
               } else {
                  var3.append("[[^/]&&[");
               }

               if (next(var0, var4) == '^') {
                  var3.append("\\^");
                  ++var4;
               } else {
                  if (next(var0, var4) == '!') {
                     var3.append('^');
                     ++var4;
                  }

                  if (next(var0, var4) == '-') {
                     var3.append('-');
                     ++var4;
                  }
               }

               boolean var7 = false;
               char var8 = 0;

               while(var4 < var0.length()) {
                  var5 = var0.charAt(var4++);
                  if (var5 == ']') {
                     break;
                  }

                  if (var5 == '/' || var1 && var5 == '\\') {
                     throw new PatternSyntaxException("Explicit 'name separator' in class", var0, var4 - 1);
                  }

                  if (var5 == '\\' || var5 == '[' || var5 == '&' && next(var0, var4) == '&') {
                     var3.append('\\');
                  }

                  var3.append(var5);
                  if (var5 == '-') {
                     if (!var7) {
                        throw new PatternSyntaxException("Invalid range", var0, var4 - 1);
                     }

                     if ((var5 = next(var0, var4++)) == EOL || var5 == ']') {
                        break;
                     }

                     if (var5 < var8) {
                        throw new PatternSyntaxException("Invalid range", var0, var4 - 3);
                     }

                     var3.append(var5);
                     var7 = false;
                  } else {
                     var7 = true;
                     var8 = var5;
                  }
               }

               if (var5 != ']') {
                  throw new PatternSyntaxException("Missing ']", var0, var4 - 1);
               }

               var3.append("]]");
               break;
            case '\\':
               if (var4 == var0.length()) {
                  throw new PatternSyntaxException("No character to escape", var0, var4 - 1);
               }

               char var6 = var0.charAt(var4++);
               if (isGlobMeta(var6) || isRegexMeta(var6)) {
                  var3.append('\\');
               }

               var3.append(var6);
               break;
            case '{':
               if (var2) {
                  throw new PatternSyntaxException("Cannot nest groups", var0, var4 - 1);
               }

               var3.append("(?:(?:");
               var2 = true;
               break;
            case '}':
               if (var2) {
                  var3.append("))");
                  var2 = false;
               } else {
                  var3.append('}');
               }
               break;
            default:
               if (isRegexMeta(var5)) {
                  var3.append('\\');
               }

               var3.append(var5);
            }
         }

         if (var2) {
            throw new PatternSyntaxException("Missing '}", var0, var4 - 1);
         }

         return var3.append('$').toString();
      }
   }

   static String toUnixRegexPattern(String var0) {
      return toRegexPattern(var0, false);
   }

   static String toWindowsRegexPattern(String var0) {
      return toRegexPattern(var0, true);
   }
}
