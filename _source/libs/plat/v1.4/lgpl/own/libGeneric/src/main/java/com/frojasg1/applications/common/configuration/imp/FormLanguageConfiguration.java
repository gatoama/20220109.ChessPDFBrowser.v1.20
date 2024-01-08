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
package com.frojasg1.applications.common.configuration.imp;


import com.frojasg1.general.string.CreateCustomString;
import com.frojasg1.applications.common.configuration.ConfigurationFromClassPath;
import java.util.Properties;
import com.frojasg1.applications.common.configuration.InternationalizedStringConf;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Usuario
 */
public class FormLanguageConfiguration extends ConfigurationFromClassPath
										implements InternationalizedStringConf
{
	public static final String CONF_WINDOW_TITLE = "WINDOW_TITLE";

	public FormLanguageConfiguration(	String mainFolder,
										String applicationName, String group,
										String configurationFileName,
										String pathPropertiesInJar,
										Properties textValuesFromFrom_properties )
	{
		super( mainFolder, applicationName, group, configurationFileName, pathPropertiesInJar, textValuesFromFrom_properties );
	}

	@Override
	public void registerInternationalString(String label, String value)
	{
		registerConfigurationLabel( label, value );
	}

	@Override
	public String getInternationalString(String label)
	{
		return( M_getStrParamConfiguration( label ) );
	}

	@Override
	public String createCustomInternationalString( String label, Object ... args )
	{
		return( CreateCustomString.instance().createCustomString( getInternationalString( label ), args) );
	}
}
