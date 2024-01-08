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
package com.frojasg1.applications.common.configuration.start;

import com.frojasg1.applications.common.configuration.ConfigurationParent;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public class StartStringsConf extends ConfigurationParent
{

	public static final String CONF_HAS_BEEN_IMPORTED = "HAS_BEEN_IMPORTED";
	public static final String CONF_INDEPENDENT_CONF = "INDEPENDENT_CONF";
	public static final String CONF_NOTICE = "NOTICE";


//	protected static final String APPLICATION_GROUP = "general";
//	protected static final String APPLICATION_NAME = "FileEncoder";
	protected static final String GLOBAL_CONF_FILE_NAME = "StartStringsConf.properties";

//	protected String a_pathPropertiesInJar = null;
	private static StartStringsConf a_instance = null;

	BaseApplicationConfigurationInterface _baseConfiguration = null;

	protected StartStringsConf( BaseApplicationConfigurationInterface appliConf )
	{
		super( appliConf.getConfigurationMainFolder(),
				appliConf.getApplicationNameFolder(),
				appliConf.getApplicationGroup(),
				null,
				GLOBAL_CONF_FILE_NAME );

		registerToChangeLanguageAsObserver(appliConf);

		_baseConfiguration = appliConf;
//		a_pathPropertiesInJar = pathPropertiesInJar;
	}

	public static StartStringsConf createInstance( BaseApplicationConfigurationInterface appliConf ) throws ConfigurationException
	{
		if( a_instance == null )
		{
			a_instance = new StartStringsConf( appliConf );
			if( appliConf != null )
			{
				a_instance.changeLanguage( appliConf.getLanguage() );
			}
		}

		return( a_instance );
	}

	public static StartStringsConf instance()
	{
		return( a_instance );
	}

	protected Properties M_getDefaultProperties2( String language )
	{
		Properties result = new Properties();

		result.setProperty(CONF_HAS_BEEN_IMPORTED, "Configuration has been imported from a prevoius version.");
		result.setProperty(CONF_INDEPENDENT_CONF, "Now the new configuration will be independent from the configurations of previous versions.");
		result.setProperty(CONF_NOTICE, "Notice");

		return( result );
	}

	protected String M_getPropertiesNameFromClassPath( String language )
	{
		String result;
//		result = a_pathPropertiesInJar + sa_dirSeparator + language + sa_dirSeparator + a_configurationFileName;
		result = _baseConfiguration.getInternationalPropertiesPathInJar() + "/" + language + "/" + a_configurationFileName;
		return( result );
	}

	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = null;

		try
		{
			result = cargarPropertiesClassPath( M_getPropertiesNameFromClassPath( language ) );
		}
		catch( IOException ioe )
		{
			ioe.printStackTrace();
			result = null;
		}

		if( result == null )
		{
			result = M_getDefaultProperties2(language);
		}
		else
		{
			result = M_makePropertiesAddingDefaults( result, M_getDefaultProperties2(language) );
		}

		return( result );
	}

	@Override
	public void changeLanguage( String language ) throws ConfigurationException
	{
		super.changeLanguage(language);
	}
	
}
