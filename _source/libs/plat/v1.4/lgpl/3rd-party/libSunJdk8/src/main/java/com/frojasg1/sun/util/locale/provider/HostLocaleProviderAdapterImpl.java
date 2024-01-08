package com.frojasg1.sun.util.locale.provider;

import java.lang.ref.SoftReference;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.text.spi.DateFormatSymbolsProvider;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.text.spi.NumberFormatProvider;
import java.util.Calendar;
import java.util.Collections;
import java.util.Currency;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.Calendar.Builder;
import java.util.ResourceBundle.Control;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.spi.CalendarDataProvider;
import java.util.spi.CurrencyNameProvider;
import java.util.spi.LocaleNameProvider;

import com.frojasg1.sun.util.locale.provider.JRELocaleConstants;
import com.frojasg1.sun.util.spi.CalendarProvider;

public class HostLocaleProviderAdapterImpl {
   private static final int CAT_DISPLAY = 0;
   private static final int CAT_FORMAT = 1;
   private static final int NF_NUMBER = 0;
   private static final int NF_CURRENCY = 1;
   private static final int NF_PERCENT = 2;
   private static final int NF_INTEGER = 3;
   private static final int NF_MAX = 3;
   private static final int CD_FIRSTDAYOFWEEK = 0;
   private static final int CD_MINIMALDAYSINFIRSTWEEK = 1;
   private static final int DN_CURRENCY_NAME = 0;
   private static final int DN_CURRENCY_SYMBOL = 1;
   private static final int DN_LOCALE_LANGUAGE = 2;
   private static final int DN_LOCALE_SCRIPT = 3;
   private static final int DN_LOCALE_REGION = 4;
   private static final int DN_LOCALE_VARIANT = 5;
   private static final String[] calIDToLDML = new String[]{"", "gregory", "gregory_en-US", "japanese", "roc", "", "islamic", "buddhist", "hebrew", "gregory_fr", "gregory_ar", "gregory_en", "gregory_fr"};
   private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> dateFormatCache = new ConcurrentHashMap();
   private static ConcurrentMap<Locale, SoftReference<DateFormatSymbols>> dateFormatSymbolsCache = new ConcurrentHashMap();
   private static ConcurrentMap<Locale, SoftReference<AtomicReferenceArray<String>>> numberFormatCache = new ConcurrentHashMap();
   private static ConcurrentMap<Locale, SoftReference<DecimalFormatSymbols>> decimalFormatSymbolsCache = new ConcurrentHashMap();
   private static final Set<Locale> supportedLocaleSet;
   private static final String nativeDisplayLanguage;
   private static final Locale[] supportedLocale;

   public HostLocaleProviderAdapterImpl() {
   }

   public static DateFormatProvider getDateFormatProvider() {
      return new DateFormatProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public DateFormat getDateInstance(int var1, Locale var2) {
            AtomicReferenceArray var3 = this.getDateTimePatterns(var2);
            return new SimpleDateFormat((String)var3.get(var1 / 2), HostLocaleProviderAdapterImpl.getCalendarLocale(var2));
         }

         public DateFormat getTimeInstance(int var1, Locale var2) {
            AtomicReferenceArray var3 = this.getDateTimePatterns(var2);
            return new SimpleDateFormat((String)var3.get(var1 / 2 + 2), HostLocaleProviderAdapterImpl.getCalendarLocale(var2));
         }

         public DateFormat getDateTimeInstance(int var1, int var2, Locale var3) {
            AtomicReferenceArray var4 = this.getDateTimePatterns(var3);
            String var5 = (String)var4.get(var1 / 2) + " " + (String)var4.get(var2 / 2 + 2);
            return new SimpleDateFormat(var5, HostLocaleProviderAdapterImpl.getCalendarLocale(var3));
         }

         private AtomicReferenceArray<String> getDateTimePatterns(Locale var1) {
            SoftReference var3 = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatCache.get(var1);
            AtomicReferenceArray var2;
            if (var3 == null || (var2 = (AtomicReferenceArray)var3.get()) == null) {
               String var4 = HostLocaleProviderAdapterImpl.removeExtensions(var1).toLanguageTag();
               var2 = new AtomicReferenceArray(4);
               var2.compareAndSet(0, (Object)null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(1, -1, var4)));
               var2.compareAndSet(1, (Object)null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(3, -1, var4)));
               var2.compareAndSet(2, (Object)null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(-1, 1, var4)));
               var2.compareAndSet(3, (Object)null, HostLocaleProviderAdapterImpl.convertDateTimePattern(HostLocaleProviderAdapterImpl.getDateTimePattern(-1, 3, var4)));
               var3 = new SoftReference(var2);
               HostLocaleProviderAdapterImpl.dateFormatCache.put(var1, var3);
            }

            return var2;
         }
      };
   }

   public static DateFormatSymbolsProvider getDateFormatSymbolsProvider() {
      return new DateFormatSymbolsProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public DateFormatSymbols getInstance(Locale var1) {
            SoftReference var3 = (SoftReference)HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.get(var1);
            DateFormatSymbols var2;
            if (var3 == null || (var2 = (DateFormatSymbols)var3.get()) == null) {
               var2 = new DateFormatSymbols(var1);
               String var4 = HostLocaleProviderAdapterImpl.removeExtensions(var1).toLanguageTag();
               var2.setAmPmStrings(HostLocaleProviderAdapterImpl.getAmPmStrings(var4, var2.getAmPmStrings()));
               var2.setEras(HostLocaleProviderAdapterImpl.getEras(var4, var2.getEras()));
               var2.setMonths(HostLocaleProviderAdapterImpl.getMonths(var4, var2.getMonths()));
               var2.setShortMonths(HostLocaleProviderAdapterImpl.getShortMonths(var4, var2.getShortMonths()));
               var2.setWeekdays(HostLocaleProviderAdapterImpl.getWeekdays(var4, var2.getWeekdays()));
               var2.setShortWeekdays(HostLocaleProviderAdapterImpl.getShortWeekdays(var4, var2.getShortWeekdays()));
               var3 = new SoftReference(var2);
               HostLocaleProviderAdapterImpl.dateFormatSymbolsCache.put(var1, var3);
            }

            return (DateFormatSymbols)var2.clone();
         }
      };
   }

   public static NumberFormatProvider getNumberFormatProvider() {
      return new NumberFormatProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedNativeDigitLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(var1);
         }

         public NumberFormat getCurrencyInstance(Locale var1) {
            AtomicReferenceArray var2 = this.getNumberPatterns(var1);
            return new DecimalFormat((String)var2.get(1), DecimalFormatSymbols.getInstance(var1));
         }

         public NumberFormat getIntegerInstance(Locale var1) {
            AtomicReferenceArray var2 = this.getNumberPatterns(var1);
            return new DecimalFormat((String)var2.get(3), DecimalFormatSymbols.getInstance(var1));
         }

         public NumberFormat getNumberInstance(Locale var1) {
            AtomicReferenceArray var2 = this.getNumberPatterns(var1);
            return new DecimalFormat((String)var2.get(0), DecimalFormatSymbols.getInstance(var1));
         }

         public NumberFormat getPercentInstance(Locale var1) {
            AtomicReferenceArray var2 = this.getNumberPatterns(var1);
            return new DecimalFormat((String)var2.get(2), DecimalFormatSymbols.getInstance(var1));
         }

         private AtomicReferenceArray<String> getNumberPatterns(Locale var1) {
            SoftReference var3 = (SoftReference)HostLocaleProviderAdapterImpl.numberFormatCache.get(var1);
            AtomicReferenceArray var2;
            if (var3 == null || (var2 = (AtomicReferenceArray)var3.get()) == null) {
               String var4 = var1.toLanguageTag();
               var2 = new AtomicReferenceArray(4);

               for(int var5 = 0; var5 <= 3; ++var5) {
                  var2.compareAndSet(var5, (Object)null, HostLocaleProviderAdapterImpl.getNumberPattern(var5, var4));
               }

               var3 = new SoftReference(var2);
               HostLocaleProviderAdapterImpl.numberFormatCache.put(var1, var3);
            }

            return var2;
         }
      };
   }

   public static DecimalFormatSymbolsProvider getDecimalFormatSymbolsProvider() {
      return new DecimalFormatSymbolsProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedNativeDigitLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedNativeDigitLocale(var1);
         }

         public DecimalFormatSymbols getInstance(Locale var1) {
            SoftReference var3 = (SoftReference)HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.get(var1);
            DecimalFormatSymbols var2;
            if (var3 == null || (var2 = (DecimalFormatSymbols)var3.get()) == null) {
               var2 = new DecimalFormatSymbols(HostLocaleProviderAdapterImpl.getNumberLocale(var1));
               String var4 = HostLocaleProviderAdapterImpl.removeExtensions(var1).toLanguageTag();
               var2.setInternationalCurrencySymbol(HostLocaleProviderAdapterImpl.getInternationalCurrencySymbol(var4, var2.getInternationalCurrencySymbol()));
               var2.setCurrencySymbol(HostLocaleProviderAdapterImpl.getCurrencySymbol(var4, var2.getCurrencySymbol()));
               var2.setDecimalSeparator(HostLocaleProviderAdapterImpl.getDecimalSeparator(var4, var2.getDecimalSeparator()));
               var2.setGroupingSeparator(HostLocaleProviderAdapterImpl.getGroupingSeparator(var4, var2.getGroupingSeparator()));
               var2.setInfinity(HostLocaleProviderAdapterImpl.getInfinity(var4, var2.getInfinity()));
               var2.setMinusSign(HostLocaleProviderAdapterImpl.getMinusSign(var4, var2.getMinusSign()));
               var2.setMonetaryDecimalSeparator(HostLocaleProviderAdapterImpl.getMonetaryDecimalSeparator(var4, var2.getMonetaryDecimalSeparator()));
               var2.setNaN(HostLocaleProviderAdapterImpl.getNaN(var4, var2.getNaN()));
               var2.setPercent(HostLocaleProviderAdapterImpl.getPercent(var4, var2.getPercent()));
               var2.setPerMill(HostLocaleProviderAdapterImpl.getPerMill(var4, var2.getPerMill()));
               var2.setZeroDigit(HostLocaleProviderAdapterImpl.getZeroDigit(var4, var2.getZeroDigit()));
               var3 = new SoftReference(var2);
               HostLocaleProviderAdapterImpl.decimalFormatSymbolsCache.put(var1, var3);
            }

            return (DecimalFormatSymbols)var2.clone();
         }
      };
   }

   public static CalendarDataProvider getCalendarDataProvider() {
      return new CalendarDataProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public int getFirstDayOfWeek(Locale var1) {
            int var2 = HostLocaleProviderAdapterImpl.getCalendarDataValue(HostLocaleProviderAdapterImpl.removeExtensions(var1).toLanguageTag(), 0);
            return var2 != -1 ? (var2 + 1) % 7 + 1 : 0;
         }

         public int getMinimalDaysInFirstWeek(Locale var1) {
            return 0;
         }
      };
   }

   public static CalendarProvider getCalendarProvider() {
      return new CalendarProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.getSupportedCalendarLocales();
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.isSupportedCalendarLocale(var1);
         }

         public Calendar getInstance(TimeZone var1, Locale var2) {
            return (new Builder()).setLocale(HostLocaleProviderAdapterImpl.getCalendarLocale(var2)).setTimeZone(var1).setInstant(System.currentTimeMillis()).build();
         }
      };
   }

   public static CurrencyNameProvider getCurrencyNameProvider() {
      return new CurrencyNameProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions()) && var1.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage);
         }

         public String getSymbol(String var1, Locale var2) {
            try {
               if (Currency.getInstance(var2).getCurrencyCode().equals(var1)) {
                  return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 1, var1);
               }
            } catch (IllegalArgumentException var4) {
            }

            return null;
         }

         public String getDisplayName(String var1, Locale var2) {
            try {
               if (Currency.getInstance(var2).getCurrencyCode().equals(var1)) {
                  return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 0, var1);
               }
            } catch (IllegalArgumentException var4) {
            }

            return null;
         }
      };
   }

   public static LocaleNameProvider getLocaleNameProvider() {
      return new LocaleNameProvider() {
         public Locale[] getAvailableLocales() {
            return HostLocaleProviderAdapterImpl.supportedLocale;
         }

         public boolean isSupportedLocale(Locale var1) {
            return HostLocaleProviderAdapterImpl.supportedLocaleSet.contains(var1.stripExtensions()) && var1.getLanguage().equals(HostLocaleProviderAdapterImpl.nativeDisplayLanguage);
         }

         public String getDisplayLanguage(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 2, var1);
         }

         public String getDisplayCountry(String var1, Locale var2) {
            return HostLocaleProviderAdapterImpl.getDisplayString(var2.toLanguageTag(), 4, HostLocaleProviderAdapterImpl.nativeDisplayLanguage + "-" + var1);
         }

         public String getDisplayScript(String var1, Locale var2) {
            return null;
         }

         public String getDisplayVariant(String var1, Locale var2) {
            return null;
         }
      };
   }

   private static String convertDateTimePattern(String var0) {
      String var1 = var0.replaceAll("dddd", "EEEE");
      var1 = var1.replaceAll("ddd", "EEE");
      var1 = var1.replaceAll("tt", "aa");
      var1 = var1.replaceAll("g", "GG");
      return var1;
   }

   private static Locale[] getSupportedCalendarLocales() {
      if (supportedLocale.length != 0 && supportedLocaleSet.contains(Locale.JAPAN) && isJapaneseCalendar()) {
         Locale[] var0 = new Locale[supportedLocale.length + 1];
         var0[0] = com.frojasg1.sun.util.locale.provider.JRELocaleConstants.JA_JP_JP;
         System.arraycopy(supportedLocale, 0, var0, 1, supportedLocale.length);
         return var0;
      } else {
         return supportedLocale;
      }
   }

   private static boolean isSupportedCalendarLocale(Locale var0) {
      Locale var1 = var0;
      if (var0.hasExtensions() || var0.getVariant() != "") {
         var1 = (new java.util.Locale.Builder()).setLocale(var0).clearExtensions().build();
      }

      if (!supportedLocaleSet.contains(var1)) {
         return false;
      } else {
         int var2 = getCalendarID(var1.toLanguageTag());
         if (var2 > 0 && var2 < calIDToLDML.length) {
            String var3 = var0.getUnicodeLocaleType("ca");
            String var4 = calIDToLDML[var2].replaceFirst("_.*", "");
            return var3 == null ? Calendar.getAvailableCalendarTypes().contains(var4) : var3.equals(var4);
         } else {
            return false;
         }
      }
   }

   private static Locale[] getSupportedNativeDigitLocales() {
      if (supportedLocale.length != 0 && supportedLocaleSet.contains(com.frojasg1.sun.util.locale.provider.JRELocaleConstants.TH_TH) && isNativeDigit("th-TH")) {
         Locale[] var0 = new Locale[supportedLocale.length + 1];
         var0[0] = com.frojasg1.sun.util.locale.provider.JRELocaleConstants.TH_TH_TH;
         System.arraycopy(supportedLocale, 0, var0, 1, supportedLocale.length);
         return var0;
      } else {
         return supportedLocale;
      }
   }

   private static boolean isSupportedNativeDigitLocale(Locale var0) {
      if (com.frojasg1.sun.util.locale.provider.JRELocaleConstants.TH_TH_TH.equals(var0)) {
         return isNativeDigit("th-TH");
      } else {
         String var1 = null;
         Locale var2 = var0;
         if (var0.hasExtensions()) {
            var1 = var0.getUnicodeLocaleType("nu");
            var2 = var0.stripExtensions();
         }

         if (supportedLocaleSet.contains(var2)) {
            if (var1 == null || var1.equals("latn")) {
               return true;
            }

            if (var0.getLanguage().equals("th")) {
               return "thai".equals(var1) && isNativeDigit(var0.toLanguageTag());
            }
         }

         return false;
      }
   }

   private static Locale removeExtensions(Locale var0) {
      return (new java.util.Locale.Builder()).setLocale(var0).clearExtensions().build();
   }

   private static boolean isJapaneseCalendar() {
      return getCalendarID("ja-JP") == 3;
   }

   private static Locale getCalendarLocale(Locale var0) {
      int var1 = getCalendarID(var0.toLanguageTag());
      if (var1 > 0 && var1 < calIDToLDML.length) {
         java.util.Locale.Builder var2 = new java.util.Locale.Builder();
         String[] var3 = calIDToLDML[var1].split("_");
         if (var3.length > 1) {
            var2.setLocale(Locale.forLanguageTag(var3[1]));
         } else {
            var2.setLocale(var0);
         }

         var2.setUnicodeLocaleKeyword("ca", var3[0]);
         return var2.build();
      } else {
         return var0;
      }
   }

   private static Locale getNumberLocale(Locale var0) {
      if (JRELocaleConstants.TH_TH.equals(var0) && isNativeDigit("th-TH")) {
         java.util.Locale.Builder var1 = (new java.util.Locale.Builder()).setLocale(var0);
         var1.setUnicodeLocaleKeyword("nu", "thai");
         return var1.build();
      } else {
         return var0;
      }
   }

   private static native boolean initialize();

   private static native String getDefaultLocale(int var0);

   private static native String getDateTimePattern(int var0, int var1, String var2);

   private static native int getCalendarID(String var0);

   private static native String[] getAmPmStrings(String var0, String[] var1);

   private static native String[] getEras(String var0, String[] var1);

   private static native String[] getMonths(String var0, String[] var1);

   private static native String[] getShortMonths(String var0, String[] var1);

   private static native String[] getWeekdays(String var0, String[] var1);

   private static native String[] getShortWeekdays(String var0, String[] var1);

   private static native String getNumberPattern(int var0, String var1);

   private static native boolean isNativeDigit(String var0);

   private static native String getCurrencySymbol(String var0, String var1);

   private static native char getDecimalSeparator(String var0, char var1);

   private static native char getGroupingSeparator(String var0, char var1);

   private static native String getInfinity(String var0, String var1);

   private static native String getInternationalCurrencySymbol(String var0, String var1);

   private static native char getMinusSign(String var0, char var1);

   private static native char getMonetaryDecimalSeparator(String var0, char var1);

   private static native String getNaN(String var0, String var1);

   private static native char getPercent(String var0, char var1);

   private static native char getPerMill(String var0, char var1);

   private static native char getZeroDigit(String var0, char var1);

   private static native int getCalendarDataValue(String var0, int var1);

   private static native String getDisplayString(String var0, int var1, String var2);

   static {
      HashSet var0 = new HashSet();
      if (initialize()) {
         Control var1 = Control.getNoFallbackControl(Control.FORMAT_DEFAULT);
         String var2 = getDefaultLocale(0);
         Locale var3 = Locale.forLanguageTag(var2.replace('_', '-'));
         var0.addAll(var1.getCandidateLocales("", var3));
         nativeDisplayLanguage = var3.getLanguage();
         String var4 = getDefaultLocale(1);
         if (!var4.equals(var2)) {
            var3 = Locale.forLanguageTag(var4.replace('_', '-'));
            var0.addAll(var1.getCandidateLocales("", var3));
         }
      } else {
         nativeDisplayLanguage = "";
      }

      supportedLocaleSet = Collections.unmodifiableSet(var0);
      supportedLocale = (Locale[])supportedLocaleSet.toArray(new Locale[0]);
   }
}
