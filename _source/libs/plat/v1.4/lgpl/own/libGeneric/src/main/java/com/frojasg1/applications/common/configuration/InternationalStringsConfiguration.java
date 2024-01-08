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

import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.applications.common.configuration.imp.FormLanguageConfiguration;
import com.frojasg1.general.exceptions.ConfigurationException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Usuario
 */
public abstract class InternationalStringsConfiguration extends FormLanguageConfiguration
{
	public static final String GLOBAL_CONF_FILE_NAME = "UtilsStringsConf.properties";

	protected InternationalStringsConfiguration( BaseApplicationConfigurationInterface appliConf,
													String fileName,
													String folderInJar )
	{
		this( appliConf,
				appliConf.getConfigurationMainFolder(),
				appliConf.getApplicationNameFolder(),
				appliConf.getApplicationGroup(),
				fileName,
				folderInJar,
				null );
	}

	protected InternationalStringsConfiguration( BaseApplicationConfigurationInterface appliConf,
													String mainFolder,
													String applicationName, String group,
													String configurationFileName,
													String pathPropertiesInJar,
													Properties textValuesFromFrom_properties )
	{
		super( mainFolder,
				applicationName,
				group,
				configurationFileName,
				pathPropertiesInJar,
				textValuesFromFrom_properties );

		registerToChangeLanguageAsObserver(appliConf);
	}

	protected abstract Properties M_getDefaultProperties2( String language );

	protected String M_getPropertiesNameFromClassPath( String language )
	{
		String result;
		result = a_pathPropertiesInJar + "/" + language + "/" + a_configurationFileName;
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
