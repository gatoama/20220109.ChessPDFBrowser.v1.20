package com.frojasg1.sun.util.locale.provider;

import com.frojasg1.sun.util.locale.provider.JRELocaleProviderAdapter;
import com.frojasg1.sun.util.locale.provider.LocaleProviderAdapter;
import com.frojasg1.sun.util.locale.provider.LocaleResources;

import java.util.Collections;
import java.util.Locale;
import java.util.Set;

public class FallbackLocaleProviderAdapter extends JRELocaleProviderAdapter {
   private static final Set<String> rootTagSet;
   private final com.frojasg1.sun.util.locale.provider.LocaleResources rootLocaleResources;

   public FallbackLocaleProviderAdapter() {
      this.rootLocaleResources = new com.frojasg1.sun.util.locale.provider.LocaleResources(this, Locale.ROOT);
   }

   public Type getAdapterType() {
      return LocaleProviderAdapter.Type.FALLBACK;
   }

   public LocaleResources getLocaleResources(Locale var1) {
      return this.rootLocaleResources;
   }

   protected Set<String> createLanguageTagSet(String var1) {
      return rootTagSet;
   }

   static {
      rootTagSet = Collections.singleton(Locale.ROOT.toLanguageTag());
   }
}
