/* 
 * Copyright (C) 2021 Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 */
package com.frojasg1.general.locale;

import com.frojasg1.generic.GenericFunctions;
import com.frojasg1.generic.languages.ListOfLanguagesResult_int;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LocaleFunctions
{
	protected final static Map< String, Locale > _mapIsoCountryToCountryLocale;
	protected final static Map< String, Locale > _mapIsoLanguagesToLanguageLocale;
	protected final static Map<String, String> _mapCountries;
	protected static LocaleFunctions _instance;

	protected List<ExtendedLocale> _fixedLocalesList;

	protected Map<String, ExtendedLocale> _mapOfLocales;

	protected String _defaultLocaleCode = "en_US";

	static
	{
		_mapIsoCountryToCountryLocale = new HashMap<>();

		String[] countries = Locale.getISOCountries();
		for( int ii=0; ii<countries.length; ii++ )
		{
			_mapIsoCountryToCountryLocale.put( countries[ii], new Locale( "", countries[ii] ) );
		}
		_mapIsoLanguagesToLanguageLocale = new HashMap<>();

		String[] languages = Locale.getISOLanguages();
		for( int ii=0; ii<languages.length; ii++ )
		{
			try
			{
				Locale locale = new Locale.Builder().setLanguage(languages[ii]).build();
				_mapIsoLanguagesToLanguageLocale.put( languages[ii], locale );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}

		_mapCountries = new HashMap<>();
		for (String iso : Locale.getISOCountries())
		{
			Locale l = new Locale("", iso);
			_mapCountries.put(l.getDisplayCountry(), iso);
		}
	}

	public static void changeInstance( LocaleFunctions inst )
	{
		_instance = inst;
	}

	public static LocaleFunctions instance()
	{
		if( _instance == null )
			_instance = new LocaleFunctions();
		return( _instance );
	}

	public LocaleFunctions()
	{
		_mapOfLocales = createMapOfAllLocales();
	}

	protected final Map<String, ExtendedLocale> createMapOfAllLocales()
	{
		HashMap< String, ExtendedLocale > result = new HashMap<>();
		
		Locale locales[] = SimpleDateFormat.getAvailableLocales();

		for( int ii=0; ii<locales.length; ii++ )
		{
			result.put( locales[ii].toString(), new ExtendedLocale( locales[ii] ) );
		}

		return( result );
	}

	public void setDefaultLocaleCode( String defaultLocaleCode )
	{
		_defaultLocaleCode = defaultLocaleCode;
	}

	public ExtendedLocale getDefaultLocale()
	{
		ExtendedLocale result = null;

		String localeCode = _defaultLocaleCode;
		if( localeCode == null )
		{
			Locale locale = JComponent.getDefaultLocale();
			if( locale != null )
			{
				localeCode = locale.getISO3Language();
				getLocaleOfLanguage( locale.getLanguage() );
			}
		}

		if( localeCode != null )
			result = getExtendedLocale( localeCode );

		return( result );
	}

	public Locale getLocaleOfLanguage( String javaLocaleLanguage )
	{
		Locale result = null;

		ExtendedLocale eLocale = _mapOfLocales.get(javaLocaleLanguage);

		if( eLocale == null )
		{
/*
			if( language.equals( "English" ) )
				result = new Locale.Builder().setLanguage("en"/*English).build();
			else if( language.equals( "Espanyol" ) )
				result = new Locale.Builder().setLanguage("es"/*Espanyol).build();
			else if( language.equals( "Catala" ) )
				result = new Locale.Builder().setLanguage("ca"/*Catala).build();
			else
				result = new Locale.Builder().setLanguage("en"/*English).build();
*/
			try
			{
				result = new Locale.Builder().setLanguage( javaLocaleLanguage ).build();

				eLocale = new ExtendedLocale( result );
				_mapOfLocales.put( javaLocaleLanguage, eLocale );
			}
			catch( Exception ex )
			{
				ex.printStackTrace();
			}
		}
		else
		{
			result = eLocale.getLocale();
		}

		return( result );
	}

	public Locale getLocaleOfLanguageWithDefault( String javaLocaleLanguage, String defaultJavaLocaleLanguage )
	{
		Locale result = null;

		try
		{
			result = getLocaleOfLanguage( javaLocaleLanguage );
		}
		catch( Exception ex )
		{
			result = null;
		}

		if( result == null )
			result = getLocaleOfLanguage( defaultJavaLocaleLanguage );

		return( result );
	}

	public String getCountryName( Locale languageForOutputLocale, String isoCountryCode )
	{
		String result = null;

		if( ( isoCountryCode != null ) && ( languageForOutputLocale != null ) )
		{
			Locale countryLocale = _mapIsoCountryToCountryLocale.get( isoCountryCode );
			if( countryLocale != null )
			{
				result = countryLocale.getDisplayCountry( languageForOutputLocale );
			}
		}

		return( result );
	}

	public String getLanguageName( Locale languageForOutputLocale, String isoLanguageCode )
	{
		String result = null;

		if( ( isoLanguageCode != null ) && ( languageForOutputLocale != null ) )
		{
			Locale languageLocale = _mapIsoLanguagesToLanguageLocale.get( isoLanguageCode );
			if( languageLocale != null )
			{
				result = languageLocale.getDisplayLanguage( languageForOutputLocale );
			}
		}

		return( result );
	}

	public String getCountryIsoCode( Locale locale )
	{
		String result = null;

		if( locale != null )
			result = _mapCountries.get( locale.getDisplayCountry() );

		return( result );
	}

	public String getLanguageIsoCode( Locale locale )
	{
		String result = null;
		
		if( locale != null )
		{
			String[] localeStrings = (locale.getLanguage().split("[-_]+"));
			result = localeStrings[0];

			if( ( result != null ) && ( result.length() > 2 ) )
			{
				result = result.substring( 0, 2 );
			}
		}

		return( result );
	}

	public List<ExtendedLocale> getListOfLocales()
	{
		List<ExtendedLocale> result = new ArrayList<ExtendedLocale>();
		result.addAll( _mapOfLocales.values() );

		return( result );
	}

	protected List<ExtendedLocale> createFixedLocaleList()
	{
		List<ExtendedLocale> result = new ArrayList<>();

		ListOfLanguagesResult_int lolr = GenericFunctions.instance().getObtainAvailableLanguages().getTotalListOfAvailableLanguages();

		if( ( lolr != null ) && ( lolr.getListOfFixedLanguages() != null ) )
		{
			Iterator<String> it = lolr.getListOfFixedLanguages().iterator();
			while( it.hasNext() )
			{
				Locale locale = GenericFunctions.instance().getObtainAvailableLanguages().getLocaleOfLanguageFromJar( it.next() );
				ExtendedLocale el = new ExtendedLocale( locale, locale );
				result.add( el );
			}
		}

		return( result );
	}

	protected List<ExtendedLocale> getListOfFixedLocales_internal()
	{
		if( _fixedLocalesList == null )
		{
			_fixedLocalesList = createFixedLocaleList();
		}

		return( _fixedLocalesList );
	}

	public List<ExtendedLocale> getListOfFixedLocales()
	{
		ArrayList< ExtendedLocale > result = new ArrayList<>();

		result.addAll( getListOfFixedLocales_internal() );

		return( result );
	}

	public ExtendedLocale getExtendedLocale( String localeCode )
	{
		return( _mapOfLocales.get( localeCode ) );
	}

	public Map<String, Locale> getMapOfLanguageLocales()
	{
		Map<String, Locale> result = new HashMap<>();

		Iterator< Map.Entry< String, Locale > > it = _mapIsoLanguagesToLanguageLocale.entrySet().iterator();
		while( it.hasNext() )
		{
			Map.Entry< String, Locale > entry = it.next();

			result.put( entry.getKey(), entry.getValue() );
		}

		return( result );
	}

	public static void main(String[] args) {
		
		//returns array of all locales
		Locale locales[] = SimpleDateFormat.getAvailableLocales();

		//iterate through each locale and print 
		// locale code, display name and country
		for (int i = 0; i < locales.length; i++) {

			System.out.printf("%10s - %s, %s \n" , locales[i].toString(), 
				locales[i].getDisplayName(), locales[i].getDisplayCountry());

		}
    }
}
