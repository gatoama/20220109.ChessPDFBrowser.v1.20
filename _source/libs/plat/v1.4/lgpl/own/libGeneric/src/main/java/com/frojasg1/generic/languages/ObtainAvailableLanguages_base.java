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
package com.frojasg1.generic.languages;

import com.frojasg1.general.ArrayFunctions;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.ResourceFunctions;
import com.frojasg1.general.getters.GenericGetter;
import com.frojasg1.general.locale.ExtendedLocale;
import com.frojasg1.general.locale.LocaleFunctions;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import javax.swing.JComponent;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ObtainAvailableLanguages_base implements ObtainAvailableLanguages_int
{
	protected static final GenericGetter<LocaleLanguage, Locale> LOCALE_LOCALE_LANGUAGE_GETTER = ( localeLanguage ) -> localeLanguage.getLocale();
	protected static final GenericGetter<LocaleLanguage, String> WEB_LANGUAGE_NAME_LOCALE_LANGUAGE_GETTER = ( localeLanguage ) -> localeLanguage.getWebLanguageName();

	protected static final String JAVA_LOCALE_LANGUAGE_PROPERTIES_FILE_NAME = "_JavaLocaleLanguage.properties";

	protected static final String CONF_JAVA_LOCALE_LANGUAGE = "JAVA_LOCALE_LANGUAGE";
	protected static final String CONF_WEB_LANGUAGE_NAME = "WEB_LANGUAGE_NAME";
	protected static final String DEFAULT_WEB_LANGUAGE_NAME = "English";

	protected static ObtainAvailableLanguages_base _instance = null;

	protected String _defaultWebLanguage = "Espanyol";

	protected String _rootLanguagePackage = null;
	protected String _rootLanguageConfigurationPathInDisk = null;

	protected Map< String, LocaleLanguage > _mapOfLocaleLanguages = new HashMap<>();

//	protected String[] _basicLanguages = null;
	protected ArrayList<String> _basicLanguagesList = new ArrayList<String>( );
//	protected String[] _availableLanguagesInJar = null;
	protected ArrayList<String> _availableLanguagesInJarList = new ArrayList<String>( );

	protected List<String> _availableLanguagesInDiskList = null;

	protected List<String> _availableWebLanguageNames = null;

	protected ListOfLanguagesResult _listsOfLanguagesAvailable = null;

	public static ObtainAvailableLanguages_base create( String[] basicLanguages,
													String[] availableLanguagesInJar,
													String rootLanguageConfigurationPathInDisk,
													String rootLanguagePackageInJar )
	{
		_instance = new ObtainAvailableLanguages_base();

		_instance.setRootLanguageConfigurationPathInDisk( rootLanguageConfigurationPathInDisk );
		_instance.setRootPackageLanguageInJar( rootLanguagePackageInJar );

		if( basicLanguages != null )
			_instance.setBasicLanguages( basicLanguages );

		if( availableLanguagesInJar != null )
			_instance.setAvailableLanguagesInJar( availableLanguagesInJar );

		_instance.reloadLanguages();

		return( _instance );
	}

	public static ObtainAvailableLanguages_base create( String[] basicLanguages,
													String[] availableLanguagesInJar,
													String[] availableWebLanguageNames,
													String rootLanguageConfigurationPathInDisk,
													String rootLanguagePackageInJar )
	{
		_instance = create( basicLanguages, availableLanguagesInJar,
							rootLanguageConfigurationPathInDisk,
							rootLanguagePackageInJar );

		_instance.setAvailableWebLanguageNames( availableWebLanguageNames );

		return( _instance );
	}

	public static ObtainAvailableLanguages_base instance()
	{
		return( _instance );
	}

	public void setRootPackageLanguageInJar( String rootLanguagePackage )
	{
		_rootLanguagePackage = rootLanguagePackage;
	}

	public void setBasicLanguages( String[] basicLanguages )
	{
		if( basicLanguages != null )
		{
//			_basicLanguages = new String[basicLanguages.length];
//			System.arraycopy( basicLanguages, 0, _basicLanguages, 0, basicLanguages.length );
			_basicLanguagesList = new ArrayList<String>( Arrays.asList( basicLanguages ) );
		}
	}

	public void setAvailableLanguagesInJar( String[] availableLanguagesInJar )
	{
		if( availableLanguagesInJar != null )
		{
//			_availableLanguagesInJar = new String[availableLanguagesInJar.length];
//			System.arraycopy( availableLanguagesInJar, 0, _availableLanguagesInJar, 0, availableLanguagesInJar.length );
			_availableLanguagesInJarList = new ArrayList<String>( Arrays.asList( availableLanguagesInJar ) );
		}
	}

	public void reloadLanguages()
	{
		_listsOfLanguagesAvailable = null;
		_mapOfLocaleLanguages = new HashMap<>();

		getTotalListOfAvailableLanguages();
	}

	@Override
	public String getDefaultWebLanguage()
	{
		return( _defaultWebLanguage );
	}

	public void setDefaultWebLanguage( String webLanguage )
	{
		_defaultWebLanguage = webLanguage;
	}

	public void setRootLanguageConfigurationPathInDisk( String rootLanguageConfigurationPathInDisk )
	{
		_rootLanguageConfigurationPathInDisk = rootLanguageConfigurationPathInDisk;
	}

	public String getRootLanguageConfigurationPathInDisk()
	{
		return( _rootLanguageConfigurationPathInDisk );
	}

	public ListOfLanguagesResult_int getTotalListOfAvailableLanguages()
	{
		return( getTotalListOfAvailableLanguages( _rootLanguagePackage, _rootLanguageConfigurationPathInDisk ) );
	}

	public ListOfLanguagesResult_int getTotalListOfAvailableLanguages(String rootLanguagePackage,
													String rootLanguageConfigurationPathInDisk )
	{
		if( _listsOfLanguagesAvailable == null )
		{
			List<String> list1 = getListOfAvailableLanguagesInPackage();
			List<String> list2 = getListOfAvailableLanguagesConfiguredInDisk( rootLanguageConfigurationPathInDisk );

			list1.addAll( list2 );

			CollectionFunctions.instance().sortNoCaseSensitive(list1);
			CollectionFunctions.instance().removeRepeatedSortedPreviously(list1);

			List<String> removedItems = CollectionFunctions.instance().removeItemsThatExistInTheSecondList( list1, _basicLanguagesList );
			List<String> fixedLanguageList = CollectionFunctions.instance().leaveOnlyExistingInTheSecondList( _basicLanguagesList, removedItems );
			List<String> otherLanguagesList = list1;

			_listsOfLanguagesAvailable = new ListOfLanguagesResult( fixedLanguageList, otherLanguagesList );
		}

		return( _listsOfLanguagesAvailable );
	}

	public List<String> getListOfAvailableLanguagesInPackage()
	{
		addLocaleLanguagesFromJar( _availableLanguagesInJarList );

		return( new ArrayList<String>( _availableLanguagesInJarList ) );
	}

	protected void addLocaleLanguagesFromJar( Collection<String> collectionOfLanguages )
	{
		if( collectionOfLanguages != null )
		{
			Iterator<String> it = collectionOfLanguages.iterator();
			while( it.hasNext() )
			{
				String language = it.next();
				
				boolean hasChanged = false;
				Properties prop = this.loadLocaleLanguageFromDisk(language);

				if( prop == null )
				{
					prop = this.loadLocaleLanguageFromJar(language);
					hasChanged = true;
				}

				if( prop != null )
				{
					createOrUpdateLocaleLanguage( language, getJavaLocalLanguage( prop ),
													getWebLanguageName( prop ), hasChanged );
				}
			}
		}
	}
	
	public List<String> getListOfAvailableLanguagesConfiguredInDisk()
	{
		if( _availableLanguagesInDiskList == null )
			_availableLanguagesInDiskList = getListOfAvailableLanguagesConfiguredInDisk( _rootLanguageConfigurationPathInDisk );

		return( _availableLanguagesInDiskList );
	}

	public List<String> getListOfAvailableLanguagesConfiguredInDisk( String rootLanguageConfigurationPathInDisk )
	{
		ArrayList<String> result = new ArrayList<String>();

		File folder = new File( rootLanguageConfigurationPathInDisk );
		if( folder.isDirectory() )
		{
			File[] contents = folder.listFiles();

			for( int ii=0; ii<contents.length; ii++ )
			{
				if( contents[ii].isDirectory() )
				{
					String language = contents[ii].getName();
					result.add( language );

					boolean hasChanged = false;
					Properties prop = this.loadLocaleLanguageFromDisk(language);

					if( prop != null )
						createOrUpdateLocaleLanguage( language, getJavaLocalLanguage( prop ),
														getWebLanguageName( prop ), hasChanged );
				}
			}

			CollectionFunctions.instance().sortNoCaseSensitive(result);
		}

		return( result );
	}

	// ordered arrayOfLanguages
	public String[] getTotalArrayOfAvailableLanguages()
	{
		ListOfLanguagesResult_int lolr = getTotalListOfAvailableLanguages();
		return( ArrayFunctions.instance().getArrayJoiningLists( lolr.getListOfFixedLanguages(), lolr.getListOfOtherLanguages() ) );
	}

	@Override
	public void newLanguageSetToConfiguration( String language, String javaLocaleLanguage,
												String webLanguageName )
	{
		ListOfLanguagesResult_int lofr = getTotalListOfAvailableLanguages();

		if( ! lofr.isPresent( language ) )
		{
			lofr.getListOfOtherLanguages().add( language );

			createLanguageFolder( language, javaLocaleLanguage, webLanguageName );
		}
	}

	public void createOrUpdateLocaleLanguage( String language, String javaLocaleLanguage,
												String webLanguageName, boolean hasChanged )
	{
		LocaleLanguage ll = _mapOfLocaleLanguages.get( language );
		if( ll == null )
		{
			ll = new LocaleLanguage( language, javaLocaleLanguage, webLanguageName, hasChanged );
			_mapOfLocaleLanguages.put( language, ll );
		}
		else
		{
			ll.setJavaLocaleLanguage(javaLocaleLanguage);
		}
	}

	public void createLanguageFolder( String language, String javaLocaleLanguage,
										String webLanguageName )
	{
		saveLocaleLanguage( language, javaLocaleLanguage, webLanguageName );

		boolean hasChanged = false;
		createOrUpdateLocaleLanguage( language, javaLocaleLanguage,
										webLanguageName, hasChanged );
	}

	protected boolean existsLanguageFolderInDisk( String language )
	{
		String dirName = getLanguageFolderInDisk( language );
		boolean result = FileFunctions.instance().isDirectory(dirName);

		return( result );
	}

	protected void createLanguageFolder_basic( String language )
	{
		String dirName = getLanguageFolderInDisk( language );
		if( ! FileFunctions.instance().isDirectory(dirName) )
		{
			if( FileFunctions.instance().createFolder( dirName ) )
			{
				
			}
		}
	}

	protected String getLanguageFolderInDisk( String language )
	{
		return( FileFunctions.instance().convertFolderSeparator(
						_rootLanguageConfigurationPathInDisk + "/" + language ) );
	}

	protected String getJavaLocaleLanguagePropertiesFileNameInDisk( String language )
	{
		return( FileFunctions.instance().convertFolderSeparator(
						getLanguageFolderInDisk( language ) + "/" +
						JAVA_LOCALE_LANGUAGE_PROPERTIES_FILE_NAME ) );
	}

	protected String getJavaLocaleLanguagePropertiesFileNameInJar( String language )
	{
		return( _rootLanguagePackage + "/" + language + "/" +
				JAVA_LOCALE_LANGUAGE_PROPERTIES_FILE_NAME );
	}

	public Locale getLocale( String language )
	{
		Locale result = null;
		LocaleLanguage ll = _mapOfLocaleLanguages.get( language );
		if( ll != null )
		{
			result = ll.getLocale();
		}
		return( result );
	}

	protected Properties loadLocaleLanguageFromDisk( String language )
	{
		String fileName = getJavaLocaleLanguagePropertiesFileNameInDisk( language );
		Properties result = FileFunctions.instance().loadPropertiesFromDisk(fileName);

		return( result );
	}

	protected Properties loadLocaleLanguageFromJar( String language )
	{
		String resourceName = getJavaLocaleLanguagePropertiesFileNameInJar( language );
		Properties result = ResourceFunctions.instance().loadPropertiesFromJar(resourceName);

		return( result );
	}

	@Override
	public void updateLocaleLanguagesToDisk()
	{
		Iterator<LocaleLanguage> it = _mapOfLocaleLanguages.values().iterator();
		while( it.hasNext() )
		{
			LocaleLanguage ll = it.next();

			if( existsLanguageFolderInDisk( ll.getLanguage() ) )
				saveLocaleLanguage( ll );
		}
	}

	protected String getPropertyValue( Properties prop, String propertyLabel )
	{
		String result = null;

		if( prop != null )
			result = prop.getProperty( propertyLabel );

		return( result );
	}

	protected String getJavaLocalLanguage( Properties prop )
	{
		return( getPropertyValue( prop, CONF_JAVA_LOCALE_LANGUAGE ) );
	}

	protected String getWebLanguageName( Properties prop )
	{
		return( getPropertyValue( prop, CONF_WEB_LANGUAGE_NAME ) );
	}

	protected void saveLocaleLanguage( String language, String javaLocaleLanguage,
										String webLanguageName )
	{
		Properties prop = new Properties();

		prop.put( CONF_JAVA_LOCALE_LANGUAGE, javaLocaleLanguage );
		prop.put( CONF_WEB_LANGUAGE_NAME, webLanguageName );

		createLanguageFolder_basic( language );
	
		String fileName = getJavaLocaleLanguagePropertiesFileNameInDisk( language );
		try
		{
			FileFunctions.instance().savePropertiesInFile( prop, fileName );
		}
		catch( Exception ex )
		{}
	}

	protected void saveLocaleLanguage( LocaleLanguage ll )
	{
		if( ( ll != null ) && ( ll.hasChanged() ) )
		{
			saveLocaleLanguage( ll.getLanguage(), ll.getLocale().toString(),
								ll.getWebLanguageName() );
		}
	}

	public <CC> CC getAttributeOfLocaleLanguage( String language, GenericGetter<LocaleLanguage, CC> getter )
	{
		CC result = null;

		if( language != null )
		{
			LocaleLanguage ll = _mapOfLocaleLanguages.get( language );
			if( ll != null )
				result = getter.get(ll);
		}

		return( result );
	}

	@Override
	public Locale getLocaleOfLanguage( String language )
	{
		return( getAttributeOfLocaleLanguage( language, LOCALE_LOCALE_LANGUAGE_GETTER ) );
	}

	@Override
	public String getWebLanguageName( String language )
	{
		String result = getAttributeOfLocaleLanguage( language, WEB_LANGUAGE_NAME_LOCALE_LANGUAGE_GETTER );
		if( ( result == null ) && ( _defaultWebLanguage != null ) )
		{
			if( _availableWebLanguageNames.contains( _defaultWebLanguage ) )
				result = _defaultWebLanguage;
		}

		return( result );
	}

	@Override
	public Locale getLocaleOfLanguageFromJar( String language )
	{
		Locale result = null;

		String localeCode = null;
		Properties prop = loadLocaleLanguageFromJar(language);

		if( prop != null )
		{
			localeCode = getJavaLocalLanguage( prop );
		}

		if( localeCode != null )
		{
			ExtendedLocale el = LocaleFunctions.instance().getExtendedLocale(localeCode);
			if( el != null )
				result = el.getLocale();
		}

		return( result );
	}

	@Override
	public void setLocaleLanguageOfLanguage( String language, String javaLocaleLanguage,
												String webLanguageName )
	{
		if( ( language != null ) && ( javaLocaleLanguage != null ) )
		{
			LocaleLanguage ll = _mapOfLocaleLanguages.get( language );
			if( ll != null )
			{
				ll.setJavaLocaleLanguage(javaLocaleLanguage);
				ll.setWebLanguageName(webLanguageName);
			}
		}
	}

	@Override
	public String getLanguageOfLocale( Locale locale )
	{
		String result = "EN";

		if( locale != null )
		{
			String localeIsoLanguage = LocaleFunctions.instance().getLanguageIsoCode(locale);
			
			Iterator< Map.Entry< String, LocaleLanguage > > it = _mapOfLocaleLanguages.entrySet().iterator();
			while( it.hasNext() )
			{
				Map.Entry< String, LocaleLanguage > entry = it.next();

				Locale tmpLocale = entry.getValue().getLocale();

				if( tmpLocale.equals( locale ) )
				{
					result = entry.getKey();
					break;
				}
				else
				{
					String tmpLocaleIsoLanguage = LocaleFunctions.instance().getLanguageIsoCode(tmpLocale);
					
					if( tmpLocaleIsoLanguage.equals( localeIsoLanguage ) )
						result = entry.getKey();
				}
			}
		}

		return( result );
	}

	public String getDefaultLanguage()
	{
		String result = getLanguageOfLocale( JComponent.getDefaultLocale() );

		return( result );
	}

	@Override
	public void setAvailableWebLanguageNames(String[] availableWebLanguagesNames)
	{
		_availableWebLanguageNames = Arrays.asList(availableWebLanguagesNames);
	}

	@Override
	public List<String> getListOfAvailableWebLanguageNames()
	{
		return( _availableWebLanguageNames );
	}

	public static class ListOfLanguagesResult implements ListOfLanguagesResult_int
	{
		protected List<String> _listOfFixedLanguages = null;
		protected List<String> _listOfOtherLanguages = null;

		public ListOfLanguagesResult( List<String> listOfFixedLanguages, List<String> listOfOtherLanguages )
		{
			_listOfFixedLanguages = listOfFixedLanguages;
			_listOfOtherLanguages = listOfOtherLanguages;
		}

		public List<String> getListOfFixedLanguages()
		{
			return( _listOfFixedLanguages );
		}

		public List<String> getListOfOtherLanguages()
		{
			return( _listOfOtherLanguages );
		}

		@Override
		public boolean isPresent(String language)
		{
			return( CollectionFunctions.instance().elementExists( _listOfFixedLanguages, language ) ||
					CollectionFunctions.instance().elementExists( _listOfOtherLanguages, language ) );
		}
	}

	protected static class LocaleLanguage
	{
		protected static final String DEFAULT_JAVA_LOCALE_LANGUAGE = "en";

		protected String _language;
		protected Locale _locale;
		protected String _webLanguageName;
		protected boolean _hasChanged;

		public LocaleLanguage( String language, String javaLocaleLanguage,
								String webLanguageName, boolean hasChanged )
		{
			_language = language;
			_webLanguageName = webLanguageName;
			_hasChanged = hasChanged;
			setJavaLocaleLanguage_basic_final( javaLocaleLanguage );
		}

		public final void setJavaLocaleLanguage_basic_final( String javaLocaleLanguage )
		{
			_locale = LocaleFunctions.instance().getLocaleOfLanguageWithDefault( javaLocaleLanguage,
																					DEFAULT_JAVA_LOCALE_LANGUAGE );
		}

		public void setJavaLocaleLanguage( String javaLocaleLanguage )
		{
			if( ( _locale == null ) ||
				!_locale.getLanguage().equals( javaLocaleLanguage ) )
			{
				_hasChanged = true;
				setJavaLocaleLanguage_basic_final( javaLocaleLanguage );
			}
		}

		public void setWebLanguageName( String webLanguageName )
		{
			if( ( _webLanguageName == null ) ||
				!_webLanguageName.equals( webLanguageName ) )
			{
				_hasChanged = true;
				_webLanguageName = webLanguageName;
			}
		}

		public String getJavaLocaleLanguage()
		{
			String javaLocaleLanguage = null;
			if( _locale != null )
				javaLocaleLanguage = _locale.getLanguage();

			return( javaLocaleLanguage );
		}

		public String getLanguage()
		{
			return( _language );
		}

		public boolean hasChanged()
		{
			return( _hasChanged );
		}

		public Locale getLocale()
		{
			return( _locale );
		}

		public String getWebLanguageName()
		{
			return( _webLanguageName );
		}
	}
}
