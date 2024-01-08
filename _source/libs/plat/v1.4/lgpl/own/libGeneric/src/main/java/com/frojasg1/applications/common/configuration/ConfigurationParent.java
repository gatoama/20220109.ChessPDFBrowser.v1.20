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
package com.frojasg1.applications.common.configuration;

import com.frojasg1.applications.common.configuration.application.ChangeLanguageClientInterface;
import com.frojasg1.applications.common.configuration.application.ChangeLanguageServerInterface;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterListener;
import com.frojasg1.applications.common.configuration.listener.ConfigurationParameterObserved;
import com.frojasg1.general.CollectionFunctions;
import com.frojasg1.general.ExecutionFunctions;
import com.frojasg1.general.FileFunctions;
import com.frojasg1.general.copyable.DefaultConstructorInitCopier;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.listeners.ComposedListOfListenersImp;
import com.frojasg1.general.string.translator.FromString;
import com.frojasg1.general.string.translator.GenericStringTranslator;
import com.frojasg1.general.string.translator.ToString;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 
 *	2016-12-06. In this new version of the class, the subfolders of the final created folder may be compound folders.
 *
 * @author Usuario
 */
public abstract class ConfigurationParent extends Properties
											implements ChangeLanguageClientInterface,
														ChangeLanguageServerInterface,
														ConfigurationParameterObserved
{
/*	public static final String GLOBAL_CONF_FILE_NAME = "ConfigurationParent.properties";

	public static final String CONF_CONFIGURATION_FILE = "CONFIGURATION_FILE";
	public static final String CONF_COULD_NOT_BE_OPENED = "COULD_NOT_BE_OPENED";
	public static final String CONF_COULD_NOT_CHANGE_LANGUAGE = "COULD_NOT_CHANGE_LANGUAGE";
	public static final String CONF_ERROR_COULD_NOT_SAVE_THE_CONFIGURATION = "ERROR_COULD_NOT_SAVE_THE_CONFIGURATION";
	public static final String CONF_BECAUSE = "BECAUSE";
	public static final String CONF_IS_NOT_A_DIRECTORY = "IS_NOT_A_DIRECTORY";
	public static final String CONF_BECAUSE_OF = "BECAUSE_OF";
*/
	protected DefaultConstructorInitCopier _copier = DefaultConstructorInitCopier.instance();

	protected ChangeLanguageServerInterface _changeLanguageServer = null;
	protected List<ChangeLanguageClientInterface> _listOfObserversOfLanguageChanges = new ArrayList<ChangeLanguageClientInterface>();

	protected ParameterChangeListeners _parameterChangeListeners = null;

	boolean _areLanguageNotificationsActivated = true;

	protected Properties a_properties = null;
	protected Properties a_defaultProperties = new Properties();
	protected Properties a_defaultDefaultProperties = new Properties();

	protected String a_mainFolder = null;
	protected String a_applicationName = null;
	protected String a_group = null;
	protected String a_language = null;
	protected String a_configurationFileName = null;

	public static String  sa_dirSeparator = System.getProperty( "file.separator" );
	public static String  sa_lineSeparator = System.getProperty( "line.separator" );
//	protected String a_rootApplicationConfigurationPath = null;
	protected boolean a_existedConfigurationFile = false;
	protected boolean a_existedConfigurationFileInClassPath = false;

	protected boolean _avoidRecursion = false;

	protected boolean _isOpen = false;

	protected Map<String, List<String>> _mapOfListParameters = new HashMap<>();

	protected GenericStringTranslator _stringTranslator = null;

//	public static String FROJASG1_FOLDER = "frojasg1.apps";
/*
	protected static InternationalizedStringConfImp _internationalizedStringConf = new InternationalizedStringConfImp( GLOBAL_CONF_FILE_NAME,
																											GenericConstants.sa_PROPERTIES_PATH_IN_JAR );

	static
	{
		try
		{
			registerInternationalizedStrings();
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}
	}
*/
	protected abstract Properties M_getDefaultProperties( String language );

	protected Properties M_getUpperDefaultProperties( String language )
	{
		return( M_getDefaultProperties( language ) );
	}

	public ConfigurationParent()
	{
		a_properties = new Properties();

		_parameterChangeListeners = createParameterChangeListeners();

		_stringTranslator = GenericStringTranslator.instance();
	}

	public ConfigurationParent( String mainFolder, String applicationName, String group,
								String language, String configurationFileName )
	{
		this( mainFolder, applicationName, group, language, configurationFileName,
			GenericStringTranslator.instance() );
	}

	public ConfigurationParent( String mainFolder, String applicationName, String group,
								String language, String configurationFileName,
								GenericStringTranslator stringTranslator )
	{
		a_mainFolder = ( mainFolder != null ? FileFunctions.instance().convertFolderSeparator( mainFolder ) : null );
		a_applicationName = ( applicationName != null ? FileFunctions.instance().convertFolderSeparator( applicationName ) : null );
		setGroup( group );
		a_language = language;
		a_configurationFileName = configurationFileName;
		a_properties = new Properties();
		_stringTranslator = stringTranslator;

//		a_rootApplicationConfigurationPath = M_getRootApplicationConfigurationPath();

		_parameterChangeListeners = createParameterChangeListeners();
	}

	// function for DefaultConstructorInitCopier
	public void init( ConfigurationParent that )
	{
		registerToChangeLanguageAsObserver( that._changeLanguageServer );
		_listOfObserversOfLanguageChanges = new ArrayList<>();

		_parameterChangeListeners = createParameterChangeListeners();

		_areLanguageNotificationsActivated = true;

		a_properties = _copier.copyProperties( that.a_properties );
		a_defaultProperties = _copier.copyProperties( that.a_defaultProperties );
		a_defaultDefaultProperties = _copier.copyProperties( that.a_defaultDefaultProperties );

		a_mainFolder = that.a_mainFolder;
		a_applicationName = that.a_applicationName;
		a_group = that.a_group;
		a_language = that.a_language;
		a_configurationFileName = that.a_configurationFileName;
//		a_rootApplicationConfigurationPath = that.a_rootApplicationConfigurationPath;

		a_existedConfigurationFile = that.a_existedConfigurationFile;
		a_existedConfigurationFileInClassPath = that.a_existedConfigurationFileInClassPath;
		_avoidRecursion = that._avoidRecursion;
		_isOpen = that._isOpen;

		_mapOfListParameters = _copier.copyMap( that._mapOfListParameters );

		_stringTranslator = that._stringTranslator;
	}

	protected void setGroup( String group )
	{
		a_group = ( group != null ? FileFunctions.instance().convertFolderSeparator( group ) : null );
	}
/*
	protected static void registerInternationalizedStrings()
	{
		registerInternationalString_own(CONF_CONFIGURATION_FILE, "Configuration file" );
		registerInternationalString_own(CONF_COULD_NOT_BE_OPENED, "could not be opened" );
		registerInternationalString_own(CONF_COULD_NOT_CHANGE_LANGUAGE, "Could not change language, because of an internal error" );
		registerInternationalString_own(CONF_ERROR_COULD_NOT_SAVE_THE_CONFIGURATION, "Error. Could not save the configuration" );
		registerInternationalString_own(CONF_BECAUSE, "because" );
		registerInternationalString_own(CONF_IS_NOT_A_DIRECTORY, "is not a directory" );
		registerInternationalString_own(CONF_BECAUSE_OF, "because of" );
	}
*/
	protected ParameterChangeListeners createParameterChangeListeners()
	{
		return( new ParameterChangeListeners() );
	}

	public boolean isOpen()
	{
		return( _isOpen );
	}

	public String getMainFolder()
	{
		return( a_mainFolder );
	}

	public String getApplicationName()
	{
		return( a_applicationName );
	}

	public String getGroup()
	{
		return( a_group );
	}

	public void removeLabel( String label )
	{
		if( a_properties != null )
			a_properties.remove( label );

		if( a_defaultProperties != null )
			a_defaultProperties.remove( label );

		if( a_defaultDefaultProperties != null )
			a_defaultDefaultProperties.remove( label );
	}

	protected String M_getRootApplicationConfigurationPath()
	{
		String result = null;
		
		if( ( a_mainFolder != null ) &&
			( a_applicationName != null ) )
		{
			result = System.getProperty("user.home") + sa_dirSeparator  + 
						a_mainFolder + sa_dirSeparator + a_applicationName;

			if( a_group != null ) result = result + sa_dirSeparator + a_group;
		}

		return( result );
	}

	public boolean configurationFileExists()
	{
		String fileName = M_getConfigurationFileName( a_language );

		return( FileFunctions.instance().isFile(fileName) );
	}

	public void M_openConfiguration() throws ConfigurationException
	{
		String fileName = M_getConfigurationFileName( a_language );
		M_openConfiguration( fileName );
	}
	
	public void M_openConfiguration( String fileName ) throws ConfigurationException
	{
		if( fileName != null )
		{
			Properties properties = null;
			properties = M_loadConfigurationFile( fileName );
			a_properties = properties; // only if there is no exception, we change the values.

			if( ( a_defaultProperties == null ) || ( a_defaultProperties.size() == 0 ) )
				a_defaultProperties = M_getUpperDefaultProperties(a_language);

			if( (a_properties == null) && (a_defaultProperties == null) )
				throw( new ConfigurationException( String.format( "%s %s %s",
							"Configuration file",
							fileName,
							"could not be opened"
																)
													)
					);

			_isOpen = true;
		}
/*			throw( new ConfigurationException( String.format( "%s %s %s",
						getInternationalString_own( CONF_CONFIGURATION_FILE ),
						fileName,
						getInternationalString_own( CONF_COULD_NOT_BE_OPENED )
															)
												)
				);
*/
	}

	protected void changeLanguage_internal_simple( String language ) throws ConfigurationException
	{
		try
		{
			if( a_language != null ) M_saveConfiguration();
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();
		}

		try
		{
			a_defaultProperties=M_getUpperDefaultProperties( language );
			M_openConfiguration( M_getConfigurationFileName(language) );
			a_language = language;
		}
		catch( ConfigurationException ce )
		{
			ce.printStackTrace();

			throw( ce );
		}
	}

	@Override
	public void setLanguage( String language )
	{
	}
	
	@Override
	public String getLanguage()
	{
		return( a_language );
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException
	{
		boolean revertOnError = true;
		changeLanguage_internal( language, revertOnError );
	}

	protected void changeLanguage_internal( String language, boolean revertOnError ) throws ConfigurationException
	{
		if( !_avoidRecursion )
		{
			String oldLanguage = getLanguage();

			try
			{
				setLanguage( language );
				changeLanguage_internal_simple(language);

				if( _areLanguageNotificationsActivated )
				{
					synchronized( _listOfObserversOfLanguageChanges )
					{
						Iterator<ChangeLanguageClientInterface> it = _listOfObserversOfLanguageChanges.iterator();
						while( it.hasNext() )
						{
							ExecutionFunctions.instance().safeMethodExecution( () -> it.next().changeLanguage( language ) );
						}
					}
				}

				_avoidRecursion = true;
//				setLanguage( language );
			}
			catch( Throwable th )
			{
				th.printStackTrace();
				if( revertOnError )
				{
					_avoidRecursion = false;
					changeLanguage_internal( oldLanguage, false );
					throw( new ConfigurationException( String.format( "%s: %s",
							"Could not change language",
							th.getMessage()
																	)
														)
						);
/*						throw( new ConfigurationException( String.format( "%s: %s",
							getInternationalString_own( CONF_COULD_NOT_CHANGE_LANGUAGE ),
							th.getMessage()
																	)
														)
						);
*/
				}
			}
			finally
			{
				_avoidRecursion = false;
			}
		}
	}

	@Override
	public void unregisterFromChangeLanguageAsObserver()
	{
		if( _changeLanguageServer != null )
		{
			_changeLanguageServer.unregisterChangeLanguageObserver( this );
			_changeLanguageServer = null;
		}
	}

	@Override
	public void registerToChangeLanguageAsObserver( ChangeLanguageServerInterface conf)
	{
		unregisterFromChangeLanguageAsObserver();
		if( ( _changeLanguageServer == null ) && ( conf != null ) )
		{
			_changeLanguageServer = conf;
			_changeLanguageServer.registerChangeLanguageObserver( this );
		}
	}

	@Override
	public void activateChangeLanguageNotifications( boolean value )
	{
		_areLanguageNotificationsActivated = value;
	}

	@Override
	public boolean areChangeLanguageNotificationsActivated()
	{
		return( _areLanguageNotificationsActivated );
	}
	
	@Override
	public void registerChangeLanguageObserver( ChangeLanguageClientInterface requestor )
	{
		synchronized( _listOfObserversOfLanguageChanges )
		{
			int index = CollectionFunctions.instance().indexOfReference(_listOfObserversOfLanguageChanges, requestor);

			if( index < 0 )
			{
				_listOfObserversOfLanguageChanges.add( requestor );
			}
		}
	}

	@Override
	public void unregisterChangeLanguageObserver( ChangeLanguageClientInterface requestor )
	{
		synchronized( _listOfObserversOfLanguageChanges )
		{
			int index = CollectionFunctions.instance().indexOfReference(_listOfObserversOfLanguageChanges, requestor);

			if( index >= 0 )
			{
				_listOfObserversOfLanguageChanges.remove( requestor );
			}
		}
	}

	public String M_getConfigurationFileName()
	{
		return( M_getConfigurationFileName( a_language ) );
	}

	protected String M_getConfigurationFileName( String language )
	{
		String fileName = M_getRootApplicationConfigurationPath();
		
		if( fileName != null )
		{
			if( language != null ) fileName = fileName + sa_dirSeparator + language;
			fileName = fileName + sa_dirSeparator + a_configurationFileName;
		}

		return( fileName );
	}

	protected Properties M_loadConfigurationFile( String fileName )
	{
		Properties result = null;
		try
		{
			a_existedConfigurationFile = false;
			result = M_loadProperties( fileName );
			a_existedConfigurationFile = true;
		}
		catch( IOException ioe )
		{
//			ioe.printStackTrace();  // we do not retrhrow the exception, and we work with default values.
			System.out.println( ioe.getMessage() );
		}

		return( result );
	}

	protected Properties M_loadProperties( String filename ) throws IOException
	{
		File file = new File( filename );
		Properties result = new Properties();

		FileInputStream fis = null;
		InputStreamReader isr = null;

		try
		{
			fis = new FileInputStream(file);
			isr = new InputStreamReader( fis, StandardCharsets.UTF_8 );
			result.load( isr );
		}
		catch( Throwable th )
		{
			System.out.println( "Error reading properties file: " + th.getMessage() );
			throw( th );
		}
		finally
		{
			if( isr != null ) isr.close();
			else if( fis != null )	fis.close();
		}

		return( result );
	}

	protected String getFinalIndexedLabel( String label, int index )
	{
		return( String.format( "%s_%d", label, index ) );
	}

	public List<String> M_getListParamConfiguration_internal( String label )
	{
		List<String> result = new ArrayList<>();

		boolean hasNext = true;
		int index = 1;
		while( hasNext )
		{
			String finalLabel = getFinalIndexedLabel( label, index );
			String value = this.M_getStrParamConfiguration(finalLabel);
			hasNext = ( value != null );
			if( hasNext )
				result.add(value);

			index++;
		}

		return( result );
	}

	public List<String> M_getListParamConfiguration( String label )
	{
		List<String> result = _mapOfListParameters.get( label );
		if( result == null )
		{
			result = M_getListParamConfiguration_internal( label );

			_mapOfListParameters.put( label, result );
		}

		return( result );
	}

	public void M_setListParamConfiguration( String label, List<String> list )
	{
		_mapOfListParameters.put( label, list );
	}

	public Integer M_getIntParamConfiguration( String label )
	{
		return( genericGetParamConfiguration( label, (str) -> _stringTranslator.fromString( str, Integer.class) ) );
	}

	public Long M_getLongParamConfiguration( String label )
	{
		return( genericGetParamConfiguration( label, (str) -> _stringTranslator.fromString( str, Long.class) ) );
	}

	public Boolean M_getBoolParamConfiguration( String label )
	{
		return( genericGetParamConfiguration( label, (str) -> _stringTranslator.fromString( str, Boolean.class) ) );
	}

	public String M_getStrParamConfiguration( String label )
	{
		String result = null;
		if( a_properties != null ) result = a_properties.getProperty(label);
		if( (result == null) && (a_defaultProperties != null) ) result = a_defaultProperties.getProperty(label);
		if( (result == null) && (a_defaultDefaultProperties != null) ) result = a_defaultDefaultProperties.getProperty(label);
		return( result );
	}

	public Float M_getFloatFactor( String label )
	{
		return( genericGetParamConfiguration( label, (str) -> _stringTranslator.fromString( str, Float.class) ) );
	}

	public Double M_getDoubleParamConfiguration( String label )
	{
		return( genericGetParamConfiguration( label, (str) -> _stringTranslator.fromString( str, Double.class) ) );
	}

	public Float M_getFloatParamConfiguration( String label )
	{
		return( genericGetParamConfiguration( label, (str) -> _stringTranslator.fromString( str, Float.class) ) );
	}

	protected <CC> CC genericGetParamConfiguration( String label, FromString<CC> fromString )
	{
		CC result = null;

		String resultStr = null;
		try
		{
			resultStr = M_getStrParamConfiguration( label );

			result = fromString.fromString( resultStr );
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		return( result );
	}

	protected <CC> String getString( CC value, ToString<CC> toString )
	{
		String result = null;
		if( value == null )
			result = "";
		else
			result = toString.toString(value);

		return( result );
	}

	protected <CC> void genericSetParamConfiguration( String label, CC value, ToString<CC> toString,
														FromString<CC> fromString ) // from label
	{
		CC oldValue = null;
		try
		{
			oldValue = fromString.fromString(label);
		}
		catch( Exception ex )
		{
			ex.printStackTrace();
		}

		M_setStrParamConfiguration_internal( label, getString( value, toString ) );
		if( !areEqual( oldValue, value ) )
			reportParameterConfigurationChange( label, oldValue, value );
	}

	public void M_setIntParamConfiguration( String label, Integer value )
	{
		genericSetParamConfiguration( label, value, (val) -> _stringTranslator.toString(val),
										(lab) -> M_getIntParamConfiguration(lab) );
	}

	public void M_setLongParamConfiguration( String label, Long value )
	{
		genericSetParamConfiguration( label, value, (val) -> _stringTranslator.toString(val),
										(lab) -> M_getLongParamConfiguration(lab) );
	}

	public void M_setBoolParamConfiguration( String label, Boolean value )
	{
		genericSetParamConfiguration( label, value, (val) -> _stringTranslator.toString(val),
										(lab) -> M_getBoolParamConfiguration(lab) );
	}

	public void M_setStrParamConfiguration( String label, String value )
	{
		genericSetParamConfiguration( label, value, (val) -> val,
										(lab) -> M_getStrParamConfiguration(lab) );
	}

	public void M_setStrParamConfiguration_internal( String label, String value )
	{
		if( a_properties == null ) a_properties = new Properties();
		a_properties.setProperty( label, value );
	}

	public void M_setFloatParamConfiguration( String label, float value )
	{
		genericSetParamConfiguration( label, value, (val) -> _stringTranslator.toString(val),
										(lab) -> M_getFloatParamConfiguration(lab) );
	}

	public void M_setDoubleParamConfiguration( String label, double value )
	{
		genericSetParamConfiguration( label, value, (val) -> _stringTranslator.toString(val),
										(lab) -> M_getDoubleParamConfiguration(lab) );
	}

	protected <CC> void reportParameterConfigurationChange( String label, CC oldValue, CC value )
	{
		_parameterChangeListeners.configurationParameterChanged(this, label, oldValue, value);
	}

	protected boolean areEqual( Object o1, Object o2 )
	{
		boolean result = ( o1 == null ) && ( o2 == null );
		if( ! result )
		{
			if( ( o1 == null ) || ( o2 == null ) )
				result = false;
			else
				result = o1.equals( o2 );
		}

		return( result );
	}

	protected Properties M_makePropertiesAddingDefaults( Properties values, Properties defaults )
	{
		Properties result = null;
		
		if( defaults == null ) result = values;
		else if( values == null ) result = defaults;
		else
		{
			result = (Properties) values.clone();
			Object[] labels = defaults.stringPropertyNames().toArray();
			for( int ii=0; ii<labels.length; ii++ )
			{
				String key = (String) labels[ii];
				String value = result.getProperty(key );
				if( value == null )
				{
					value = defaults.getProperty( key );
					if( value != null ) result.setProperty( key, value );
				}
			}
		}
		
		return( result );
	}
	
	public void M_saveConfiguration() throws ConfigurationException
	{
		M_saveConfiguration( false );
	}

	public void M_saveConfiguration( boolean saveAlways ) throws ConfigurationException
	{
		if( ! saveAlways )
			M_saveConfiguration_notAlways( M_getConfigurationFileName(a_language) );
		else
			M_saveConfiguration( M_getConfigurationFileName(a_language) );
	}

	protected void M_saveConfiguration_notAlways( String fileName ) throws ConfigurationException
	{
		// we only save the language properties file once
		if( ( a_language == null ) || ( !FileFunctions.instance().isFile(fileName) ) )
			M_saveConfiguration( fileName );
	}

	public void removeList( String label )
	{
		_mapOfListParameters.remove( label );
		removeListLabelLabels( label );
	}

	protected void removeListLabelLabels( String label )
	{
		boolean hasNext = true;
		int index = 1;
		while( hasNext )
		{
			String finalLabel = getFinalIndexedLabel( label, index );
			String value = this.M_getStrParamConfiguration(finalLabel);
			hasNext = ( value != null );

			if( hasNext )
				removeLabel( finalLabel );

			index++;
		}
	}

	protected void commitList( String label, List<String> list )
	{
		removeListLabelLabels( label );

		int index = 1;
		for( String value: list )
		{
			String finalLabel = getFinalIndexedLabel( label, index );

			M_setStrParamConfiguration(finalLabel, value);
			index++;
		}
	}

	protected void commitLists()
	{
		for( Map.Entry<String, List<String>> entry: _mapOfListParameters.entrySet() )
		{
			String label = entry.getKey();
			List<String> list = entry.getValue();
			commitList( label, list );
		}
	}

	protected void commit()
	{
		commitLists();
	}

	public void M_saveConfiguration( String fileName ) throws ConfigurationException
	{
		commit();

		if( fileName != null )
		{
			File file = new File( fileName );
			File fPath = new File( file.getParent() );

			if( !fPath.exists() )
			{
				fPath.mkdirs();
			}
			else if ( !fPath.isDirectory() )
			{
				throw( new ConfigurationException( String.format( "%s %s %s %s",
							"Could not save configuration",
							"because",
							fPath.getName(),
							"is not a directory"
																	)
													)
					);
	/*			throw( new ConfigurationException( String.format( "%s %s %s %s",
							getInternationalString_own( CONF_ERROR_COULD_NOT_SAVE_THE_CONFIGURATION ),
							getInternationalString_own( CONF_BECAUSE ),
							fPath.getName(),
							getInternationalString_own( CONF_IS_NOT_A_DIRECTORY )
																	)
													)
					);
	*/
			}

			Properties prop = M_makePropertiesAddingDefaults( a_properties, a_defaultProperties );
			prop = M_makePropertiesAddingDefaults( prop, a_defaultDefaultProperties );

			if( prop != null )
			{
				FileOutputStream fos = null;
				OutputStreamWriter osw = null;
				try
				{
					fos = new FileOutputStream( file );
					osw = new OutputStreamWriter( fos, StandardCharsets.UTF_8 );
					prop.store( osw, a_configurationFileName + " Configuration" );
				}
				catch( IOException ex )
				{
					ex.printStackTrace();
					throw( new ConfigurationException( String.format( "%s %s %s %s",
								"Could not save configuration",
								"because of",
								ex.getMessage()
																		)
														)
						);
	/*				throw( new ConfigurationException( String.format( "%s %s %s %s",
								getInternationalString_own( CONF_ERROR_COULD_NOT_SAVE_THE_CONFIGURATION ),
								getInternationalString_own( CONF_BECAUSE_OF ),
								ex.getMessage()
																		)
														)
						);
	*/
				}
				finally
				{
					try
					{
						if( osw != null ) osw.close();
						else if( fos != null ) fos.close();
					}
					catch( Throwable th )
					{
						th.printStackTrace();
					}
				}
			}
		}
	}

	public String M_getLanguage()	{ return( a_language );	}

	@Override
	public String getProperty( String label )
	{
		return( M_getStrParamConfiguration( label ) );
	}

	@Override
	public Object setProperty( String key, String value )
	{
		M_setStrParamConfiguration( key, value );
		return( null );
	}

	protected Properties cargarPropertiesClassPath( String propName ) throws IOException
	{
		a_existedConfigurationFileInClassPath = false;
		Properties result = null;
		InputStream in = null;
		ClassLoader loader = ClassLoader.getSystemClassLoader ();
		in = loader.getResourceAsStream (propName);

		if( in == null )
		{
			try
			{
				in = this.getClass().getClassLoader().getResource(propName).openStream();
			}
			catch( Throwable th )
			{
				in = null;
			}
		}

		if (in != null)
		{
			InputStreamReader isr = new InputStreamReader( in, StandardCharsets.UTF_8 );
			if( isr != null )
			{
				result = new Properties ();
				result.load (isr); // It can throw IOException
				isr.close();
			}
		}
		a_existedConfigurationFileInClassPath = ( result != null );
		return( result );
	}

	public boolean M_isFirstTime()
	{
		return( !a_existedConfigurationFile );
	}

	public boolean M_existedConfigurationFileInClassPath()
	{
		return( a_existedConfigurationFileInClassPath );
	}

	public void M_setProperties( Properties prop )
	{
		a_properties = prop;
	}

	public void loadDefaultProperties()
	{
		a_defaultProperties=M_getUpperDefaultProperties( a_language );
	}

	public void registerConfigurationLabel( String label, String defaultValue )
	{
		a_defaultDefaultProperties.setProperty( label, defaultValue );
	}

	public static void main( String[] args )
	{
		String fileName = "C:\\Users\\Usuario\\frojasg1.apps\\FileEncoderApplication\\Frames\\CAT\\JDial_applicationConfiguration_LAN.properties";
		
		File file = new File( fileName );
		Properties result = new Properties();

		try
		{

			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader( fis, StandardCharsets.UTF_8 );
			result.load( isr );
			isr.close();

			boolean success = file.delete();

			System.out.println( "Delete " + file + "   Success: " + success );
		}
		catch( Throwable th )
		{
			th.printStackTrace();
		}
	}

	@Override
	public void fireChangeLanguageEvent( ) throws ConfigurationException
	{
		changeLanguage( getLanguage() );
	}

/*
	public static void registerInternationalString_own(String label, String value)
	{
		_internationalizedStringConf.registerInternationalString(label, value);
	}

	public static String getInternationalString_own(String label)
	{
		return( _internationalizedStringConf.getInternationalString(label ) );
	}
*/

	@Override
	public void addConfigurationParameterListener(String label, ConfigurationParameterListener listener)
	{
		_parameterChangeListeners.add( label, listener );
	}

	@Override
	public void removeConfigurationParameterListener(String label, ConfigurationParameterListener listener)
	{
		_parameterChangeListeners.remove( label, listener );
	}

	protected class ParameterChangeListeners extends ComposedListOfListenersImp< String, ConfigurationParameterListener >
													implements ConfigurationParameterListener
	{
		@Override
		public <CC> void configurationParameterChanged(ConfigurationParameterObserved observed, String label, CC oldValue, CC newValue)
		{
			List<ConfigurationParameterListener> list = _map.get(label);

			if( list != null )
			{
				for( ConfigurationParameterListener listener: list )
				{
					try
					{
						listener.configurationParameterChanged(observed, label, oldValue, newValue);
					}
					catch( Exception ex )
					{
						ex.printStackTrace();
					}
				}
			}
		}
	}
}
