package com.frojasg1.sun.nio.fs;

import com.frojasg1.sun.nio.fs.WindowsPathType;

import java.nio.file.InvalidPathException;

class WindowsPathParser {
   private static final String reservedChars = "<>:\"|?*";

   private WindowsPathParser() {
   }

   static WindowsPathParser.Result parse(String var0) {
      return parse(var0, true);
   }

   static WindowsPathParser.Result parseNormalizedPath(String var0) {
      return parse(var0, false);
   }

   private static WindowsPathParser.Result parse(String var0, boolean var1) {
      String var2 = "";
      com.frojasg1.sun.nio.fs.WindowsPathType var3 = null;
      int var4 = var0.length();
      int var5 = 0;
      if (var4 > 1) {
         char var6 = var0.charAt(0);
         char var7 = var0.charAt(1);
         boolean var8 = false;
         byte var9 = 2;
         if (isSlash(var6) && isSlash(var7)) {
            var3 = com.frojasg1.sun.nio.fs.WindowsPathType.UNC;
            var5 = nextNonSlash(var0, var9, var4);
            int var12 = nextSlash(var0, var5, var4);
            if (var5 == var12) {
               throw new InvalidPathException(var0, "UNC path is missing hostname");
            }

            String var13 = var0.substring(var5, var12);
            var5 = nextNonSlash(var0, var12, var4);
            var12 = nextSlash(var0, var5, var4);
            if (var5 == var12) {
               throw new InvalidPathException(var0, "UNC path is missing sharename");
            }

            var2 = "\\\\" + var13 + "\\" + var0.substring(var5, var12) + "\\";
            var5 = var12;
         } else if (isLetter(var6) && var7 == ':') {
            char var10;
            if (var4 > 2 && isSlash(var10 = var0.charAt(2))) {
               if (var10 == '\\') {
                  var2 = var0.substring(0, 3);
               } else {
                  var2 = var0.substring(0, 2) + '\\';
               }

               var5 = 3;
               var3 = com.frojasg1.sun.nio.fs.WindowsPathType.ABSOLUTE;
            } else {
               var2 = var0.substring(0, 2);
               var5 = 2;
               var3 = com.frojasg1.sun.nio.fs.WindowsPathType.DRIVE_RELATIVE;
            }
         }
      }

      if (var5 == 0) {
         if (var4 > 0 && isSlash(var0.charAt(0))) {
            var3 = com.frojasg1.sun.nio.fs.WindowsPathType.DIRECTORY_RELATIVE;
            var2 = "\\";
         } else {
            var3 = com.frojasg1.sun.nio.fs.WindowsPathType.RELATIVE;
         }
      }

      if (var1) {
         StringBuilder var11 = new StringBuilder(var0.length());
         var11.append(var2);
         return new WindowsPathParser.Result(var3, var2, normalize(var11, var0, var5));
      } else {
         return new WindowsPathParser.Result(var3, var2, var0);
      }
   }

   private static String normalize(StringBuilder var0, String var1, int var2) {
      int var3 = var1.length();
      var2 = nextNonSlash(var1, var2, var3);
      int var4 = var2;
      char var5 = 0;

      while(var2 < var3) {
         char var6 = var1.charAt(var2);
         if (isSlash(var6)) {
            if (var5 == ' ') {
               throw new InvalidPathException(var1, "Trailing char <" + var5 + ">", var2 - 1);
            }

            var0.append(var1, var4, var2);
            var2 = nextNonSlash(var1, var2, var3);
            if (var2 != var3) {
               var0.append('\\');
            }

            var4 = var2;
         } else {
            if (isInvalidPathChar(var6)) {
               throw new InvalidPathException(var1, "Illegal char <" + var6 + ">", var2);
            }

            var5 = var6;
            ++var2;
         }
      }

      if (var4 != var2) {
         if (var5 == ' ') {
            throw new InvalidPathException(var1, "Trailing char <" + var5 + ">", var2 - 1);
         }

         var0.append(var1, var4, var2);
      }

      return var0.toString();
   }

   private static final boolean isSlash(char var0) {
      return var0 == '\\' || var0 == '/';
   }

   private static final int nextNonSlash(String var0, int var1, int var2) {
      while(var1 < var2 && isSlash(var0.charAt(var1))) {
         ++var1;
      }

      return var1;
   }

   private static final int nextSlash(String var0, int var1, int var2) {
      char var3;
      while(var1 < var2 && !isSlash(var3 = var0.charAt(var1))) {
         if (isInvalidPathChar(var3)) {
            throw new InvalidPathException(var0, "Illegal character [" + var3 + "] in path", var1);
         }

         ++var1;
      }

      return var1;
   }

   private static final boolean isLetter(char var0) {
      return var0 >= 'a' && var0 <= 'z' || var0 >= 'A' && var0 <= 'Z';
   }

   private static final boolean isInvalidPathChar(char var0) {
      return var0 < ' ' || "<>:\"|?*".indexOf(var0) != -1;
   }

   static class Result {
      private final com.frojasg1.sun.nio.fs.WindowsPathType type;
      private final String root;
      private final String path;

      Result(com.frojasg1.sun.nio.fs.WindowsPathType var1, String var2, String var3) {
         this.type = var1;
         this.root = var2;
         this.path = var3;
      }

      com.frojasg1.sun.nio.fs.WindowsPathType type() {
         return this.type;
      }

      String root() {
         return this.root;
      }

      String path() {
         return this.path;
      }
   }
}
