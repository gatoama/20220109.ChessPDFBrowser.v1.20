package com.frojasg1.sun.util.locale;

import com.frojasg1.sun.util.locale.Extension;
import com.frojasg1.sun.util.locale.InternalLocaleBuilder;
import com.frojasg1.sun.util.locale.LanguageTag;
import com.frojasg1.sun.util.locale.LocaleUtils;
import com.frojasg1.sun.util.locale.UnicodeLocaleExtension;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class LocaleExtensions {
   private final Map<Character, com.frojasg1.sun.util.locale.Extension> extensionMap;
   private final String id;
   public static final LocaleExtensions CALENDAR_JAPANESE;
   public static final LocaleExtensions NUMBER_THAI;

   private LocaleExtensions(String var1, Character var2, com.frojasg1.sun.util.locale.Extension var3) {
      this.id = var1;
      this.extensionMap = Collections.singletonMap(var2, var3);
   }

   LocaleExtensions(Map<com.frojasg1.sun.util.locale.InternalLocaleBuilder.CaseInsensitiveChar, String> var1, Set<com.frojasg1.sun.util.locale.InternalLocaleBuilder.CaseInsensitiveString> var2, Map<com.frojasg1.sun.util.locale.InternalLocaleBuilder.CaseInsensitiveString, String> var3) {
      boolean var4 = !com.frojasg1.sun.util.locale.LocaleUtils.isEmpty(var1);
      boolean var5 = !com.frojasg1.sun.util.locale.LocaleUtils.isEmpty(var2);
      boolean var6 = !com.frojasg1.sun.util.locale.LocaleUtils.isEmpty(var3);
      if (!var4 && !var5 && !var6) {
         this.id = "";
         this.extensionMap = Collections.emptyMap();
      } else {
         TreeMap var7 = new TreeMap();
         if (var4) {
            Iterator var8 = var1.entrySet().iterator();

            label80:
            while(true) {
               char var10;
               String var11;
               do {
                  if (!var8.hasNext()) {
                     break label80;
                  }

                  Entry var9 = (Entry)var8.next();
                  var10 = com.frojasg1.sun.util.locale.LocaleUtils.toLower(((com.frojasg1.sun.util.locale.InternalLocaleBuilder.CaseInsensitiveChar)var9.getKey()).value());
                  var11 = (String)var9.getValue();
                  if (!com.frojasg1.sun.util.locale.LanguageTag.isPrivateusePrefixChar(var10)) {
                     break;
                  }

                  var11 = com.frojasg1.sun.util.locale.InternalLocaleBuilder.removePrivateuseVariant(var11);
               } while(var11 == null);

               var7.put(var10, new com.frojasg1.sun.util.locale.Extension(var10, com.frojasg1.sun.util.locale.LocaleUtils.toLowerString(var11)));
            }
         }

         if (var5 || var6) {
            TreeSet var14 = null;
            TreeMap var15 = null;
            Iterator var16;
            if (var5) {
               var14 = new TreeSet();
               var16 = var2.iterator();

               while(var16.hasNext()) {
                  com.frojasg1.sun.util.locale.InternalLocaleBuilder.CaseInsensitiveString var18 = (com.frojasg1.sun.util.locale.InternalLocaleBuilder.CaseInsensitiveString)var16.next();
                  var14.add(com.frojasg1.sun.util.locale.LocaleUtils.toLowerString(var18.value()));
               }
            }

            if (var6) {
               var15 = new TreeMap();
               var16 = var3.entrySet().iterator();

               while(var16.hasNext()) {
                  Entry var19 = (Entry)var16.next();
                  String var12 = com.frojasg1.sun.util.locale.LocaleUtils.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)var19.getKey()).value());
                  String var13 = com.frojasg1.sun.util.locale.LocaleUtils.toLowerString((String)var19.getValue());
                  var15.put(var12, var13);
               }
            }

            com.frojasg1.sun.util.locale.UnicodeLocaleExtension var17 = new com.frojasg1.sun.util.locale.UnicodeLocaleExtension(var14, var15);
            var7.put('u', var17);
         }

         if (var7.isEmpty()) {
            this.id = "";
            this.extensionMap = Collections.emptyMap();
         } else {
            this.id = toID(var7);
            this.extensionMap = var7;
         }

      }
   }

   public Set<Character> getKeys() {
      return this.extensionMap.isEmpty() ? Collections.emptySet() : Collections.unmodifiableSet(this.extensionMap.keySet());
   }

   public com.frojasg1.sun.util.locale.Extension getExtension(Character var1) {
      return (com.frojasg1.sun.util.locale.Extension)this.extensionMap.get(com.frojasg1.sun.util.locale.LocaleUtils.toLower(var1));
   }

   public String getExtensionValue(Character var1) {
      com.frojasg1.sun.util.locale.Extension var2 = (com.frojasg1.sun.util.locale.Extension)this.extensionMap.get(com.frojasg1.sun.util.locale.LocaleUtils.toLower(var1));
      return var2 == null ? null : var2.getValue();
   }

   public Set<String> getUnicodeLocaleAttributes() {
      com.frojasg1.sun.util.locale.Extension var1 = (com.frojasg1.sun.util.locale.Extension)this.extensionMap.get('u');
      if (var1 == null) {
         return Collections.emptySet();
      } else {
         assert var1 instanceof com.frojasg1.sun.util.locale.UnicodeLocaleExtension;

         return ((com.frojasg1.sun.util.locale.UnicodeLocaleExtension)var1).getUnicodeLocaleAttributes();
      }
   }

   public Set<String> getUnicodeLocaleKeys() {
      com.frojasg1.sun.util.locale.Extension var1 = (com.frojasg1.sun.util.locale.Extension)this.extensionMap.get('u');
      if (var1 == null) {
         return Collections.emptySet();
      } else {
         assert var1 instanceof com.frojasg1.sun.util.locale.UnicodeLocaleExtension;

         return ((com.frojasg1.sun.util.locale.UnicodeLocaleExtension)var1).getUnicodeLocaleKeys();
      }
   }

   public String getUnicodeLocaleType(String var1) {
      com.frojasg1.sun.util.locale.Extension var2 = (com.frojasg1.sun.util.locale.Extension)this.extensionMap.get('u');
      if (var2 == null) {
         return null;
      } else {
         assert var2 instanceof com.frojasg1.sun.util.locale.UnicodeLocaleExtension;

         return ((com.frojasg1.sun.util.locale.UnicodeLocaleExtension)var2).getUnicodeLocaleType(LocaleUtils.toLowerString(var1));
      }
   }

   public boolean isEmpty() {
      return this.extensionMap.isEmpty();
   }

   public static boolean isValidKey(char var0) {
      return com.frojasg1.sun.util.locale.LanguageTag.isExtensionSingletonChar(var0) || com.frojasg1.sun.util.locale.LanguageTag.isPrivateusePrefixChar(var0);
   }

   public static boolean isValidUnicodeLocaleKey(String var0) {
      return com.frojasg1.sun.util.locale.UnicodeLocaleExtension.isKey(var0);
   }

   private static String toID(SortedMap<Character, com.frojasg1.sun.util.locale.Extension> var0) {
      StringBuilder var1 = new StringBuilder();
      com.frojasg1.sun.util.locale.Extension var2 = null;
      Iterator var3 = var0.entrySet().iterator();

      while(var3.hasNext()) {
         Entry var4 = (Entry)var3.next();
         char var5 = (Character)var4.getKey();
         com.frojasg1.sun.util.locale.Extension var6 = (com.frojasg1.sun.util.locale.Extension)var4.getValue();
         if (LanguageTag.isPrivateusePrefixChar(var5)) {
            var2 = var6;
         } else {
            if (var1.length() > 0) {
               var1.append("-");
            }

            var1.append(var6);
         }
      }

      if (var2 != null) {
         if (var1.length() > 0) {
            var1.append("-");
         }

         var1.append(var2);
      }

      return var1.toString();
   }

   public String toString() {
      return this.id;
   }

   public String getID() {
      return this.id;
   }

   public int hashCode() {
      return this.id.hashCode();
   }

   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else {
         return !(var1 instanceof LocaleExtensions) ? false : this.id.equals(((LocaleExtensions)var1).id);
      }
   }

   static {
      CALENDAR_JAPANESE = new LocaleExtensions("u-ca-japanese", 'u', com.frojasg1.sun.util.locale.UnicodeLocaleExtension.CA_JAPANESE);
      NUMBER_THAI = new LocaleExtensions("u-nu-thai", 'u', UnicodeLocaleExtension.NU_THAI);
   }
}
