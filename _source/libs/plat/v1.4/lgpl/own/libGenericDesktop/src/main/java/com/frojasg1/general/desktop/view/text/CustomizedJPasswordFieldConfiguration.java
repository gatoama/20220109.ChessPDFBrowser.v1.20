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
package com.frojasg1.general.desktop.view.text;

import com.frojasg1.applications.common.configuration.imp.FormLanguageConfiguration;
import java.util.Properties;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class CustomizedJPasswordFieldConfiguration extends FormLanguageConfiguration
{
    public static final String CONF_PASSWORD_FIELD_HINT_FOR_CAPS_ACTIVATED = "PASSWORD_FIELD_HINT_FOR_CAPS_ACTIVATED";

	public CustomizedJPasswordFieldConfiguration(	String mainFolder,
										String applicationName, String group,
										String configurationFileName,
										String languagePackage )
	{
		super(	mainFolder,
				applicationName, group,
				configurationFileName,
				languagePackage,
				null);
	}

	protected Properties M_getDefaultProperties( String language )
	{
		Properties result = new Properties();

		result.setProperty( CONF_PASSWORD_FIELD_HINT_FOR_CAPS_ACTIVATED, "CAPS lock active" );

//		a_defaultPropertiesReadFromComponents = M_makePropertiesAddingDefaults(a_defaultPropertiesReadFromComponents, a_defaultPropertiesReadFromComponents );

		return( result );
	}

	public String getHint()
	{
		return( M_getStrParamConfiguration(CONF_PASSWORD_FIELD_HINT_FOR_CAPS_ACTIVATED) );
	}
}
