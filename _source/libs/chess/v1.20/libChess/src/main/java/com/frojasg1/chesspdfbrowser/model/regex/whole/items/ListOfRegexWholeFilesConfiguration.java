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
package com.frojasg1.chesspdfbrowser.model.regex.whole.items;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.model.regex.conf.DefaultConstantsForRegexConf;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfRegexWholeFilesConfiguration extends ParameterListConfiguration
{
	public static final String GLOBAL_CONF_FILE_NAME = "RegexFiles.properties";


	// function for DefaultConstructorInitCopier
	public ListOfRegexWholeFilesConfiguration()
	{
		
	}

	public ListOfRegexWholeFilesConfiguration( BaseApplicationConfigurationInterface appConf )
	{
		this(appConf,
			DefaultConstantsForRegexConf.CONF_REGEX_MAIN_FOLDER_DEFAULT,
			GLOBAL_CONF_FILE_NAME );
	}

	public ListOfRegexWholeFilesConfiguration( BaseApplicationConfigurationInterface appConf,
								String confRegexMainFolder, String configurationFileName )
	{
		super( appConf.getConfigurationMainFolder(),
				appConf.getApplicationNameFolder(),
				confRegexMainFolder,
				null,
				configurationFileName );
	}

	@Override
	public void init( ParameterListConfiguration that )
	{
		throw( new RuntimeException( "This init function cannot be used. Use init( ListOfRegexWholeFilesConfiguration that ) instead" ) );
	}

	// function for DefaultConstructorInitCopier
	public void init( ListOfRegexWholeFilesConfiguration that )
	{
		super.init( (ParameterListConfiguration) that );
	}
}
