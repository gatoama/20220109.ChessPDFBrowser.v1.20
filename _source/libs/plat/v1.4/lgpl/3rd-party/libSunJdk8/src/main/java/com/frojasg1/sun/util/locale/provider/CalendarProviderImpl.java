package com.frojasg1.sun.util.locale.provider;

import java.util.Calendar;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Calendar.Builder;

import com.frojasg1.sun.util.locale.provider.AvailableLanguageTags;
import com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter;
import com.frojasg1.sun.util.spi.CalendarProvider;

public class CalendarProviderImpl extends CalendarProvider implements AvailableLanguageTags {
   private final com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter.Type type;
   private final Set<String> langtags;

   public CalendarProviderImpl(com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter.Type var1, Set<String> var2) {
      this.type = var1;
      this.langtags = var2;
   }

   public Locale[] getAvailableLocales() {
      return LocaleProviderAdapter.toLocaleArray(this.langtags);
   }

   public boolean isSupportedLocale(Locale var1) {
      return true;
   }

   public Calendar getInstance(TimeZone var1, Locale var2) {
      return (new Builder()).setLocale(var2).setTimeZone(var1).setInstant(System.currentTimeMillis()).build();
   }

   public Set<String> getAvailableLanguageTags() {
      return this.langtags;
   }
}
