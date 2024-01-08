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


import java.io.IOException;
import java.util.Properties;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class ConfigurationFromClassPath extends ConfigurationParent
{
	protected static String	sa_dirSeparator = System.getProperty( "file.separator" );
	protected String a_pathPropertiesInJar = null;
	protected Properties a_textValuesFrom_properties = null;

	
	public ConfigurationFromClassPath(	String mainFolder,
										String applicationName, String group,
										String configurationFileName,
										String pathPropertiesInJar,
										Properties textValuesFromFrom_properties )
	{
		super( mainFolder, applicationName, group, null, configurationFileName );
		a_pathPropertiesInJar = pathPropertiesInJar;
		a_textValuesFrom_properties = textValuesFromFrom_properties;
	}

	protected String M_getPropertiesNameFromClassPath( String language )
	{
		String result;

		if( language != null )
			result = a_pathPropertiesInJar + "/" + language + "/" + a_configurationFileName;
		else
			result = a_pathPropertiesInJar + "/" + a_configurationFileName;

		return( result );
	}

	@Override
	protected Properties M_getDefaultProperties( String language )
	{
		return( null );
	}

	@Override
	protected Properties M_getUpperDefaultProperties( String language )
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

		if( a_textValuesFrom_properties == null )
		{
			a_textValuesFrom_properties = M_getDefaultProperties( language );
		}

		if( result == null )
		{
			result = a_textValuesFrom_properties;
		}
		else
		{
			result = M_makePropertiesAddingDefaults(result, a_textValuesFrom_properties );
		}

		return( result );
	}
}
