package com.frojasg1.sun.util.locale.provider;

import com.frojasg1.sun.util.locale.provider.AvailableLanguageTags;
import com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter;

import java.util.Locale;
import java.util.Set;
import java.util.spi.CurrencyNameProvider;

public class CurrencyNameProviderImpl extends CurrencyNameProvider implements AvailableLanguageTags {
   private final com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public CurrencyNameProviderImpl(com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }

   public Locale[] getAvailableLocales() {
      return com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public String getSymbol(String var1, Locale var2) {
      return this.getString(var1.toUpperCase(Locale.ROOT), var2);
   }

   public String getDisplayName(String var1, Locale var2) {
      return this.getString(var1.toLowerCase(Locale.ROOT), var2);
   }

   private String getString(String var1, Locale var2) {
      if (var2 == null) {
         throw new NullPointerException();
      } else {
         return LocaleProviderAdapter.forType(this.type).getLocaleResources(var2).getCurrencyName(var1);
      }
   }
}
