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
import com.frojasg1.chesspdfbrowser.model.regex.whole.RegexWholeFileModel;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.xml.persistency.container.SimpleMapContainerOfModels;
import com.frojasg1.general.xml.persistency.container.SimpleMapContainerOfModelsKeyString;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ListOfRegexWholeFiles extends SimpleMapContainerOfModelsKeyString<RegexWholeFileModel>
{
	public static final String GLOBAL_CONF_FILE_NAME = "ListOfRegexWholeFiles.properties";

	// function for DefaultConstructorInitCopier
	public ListOfRegexWholeFiles()
	{
	}

	@Override
	public void init( BaseApplicationConfigurationInterface appliConf,
						String languageGlobalConfFileName,
						String languagePropertiesFolderInJar,
						Function<String, String> fileNameCreatorFunction )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	public void init( BaseApplicationConfigurationInterface appliConf,
						Function<String, String> fileNameCreatorFunction )
	{
		super.init( appliConf, GLOBAL_CONF_FILE_NAME,
					appliConf.getInternationalPropertiesPathInJar(),
					fileNameCreatorFunction );
	}

	@Override
	public void init( SimpleMapContainerOfModels<String, RegexWholeFileModel> that )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	// function for DefaultConstructorInitCopier
	public void init( ListOfRegexWholeFiles that )
	{
		super.init( that );
	}

	protected ParameterListConfiguration createListOfModelNamesConfiguration()
	{
		return( new ListOfRegexWholeFilesConfiguration( getAppliConf() ) );
	}

	@Override
	public void add( RegexWholeFileModel rwc )
	{
		super.add( rwc );
	}

	@Override
	protected RegexWholeFileModel createModelObject()
	{
		RegexWholeFileModel result = new RegexWholeFileModel();
		result.init();

		return( result );
	}

	@Override
	public RegexWholeFileModel createAndAddEmptyFileModel( String newSingleFileName )
	{
		return( super.createAndAddEmptyFileModel(newSingleFileName) );
	}

	@Override
	public RegexWholeFileModel get( String fileName )
	{
		return( super.get( fileName ) );
	}

	@Override
	public RegexWholeFileModel remove( String fileName )
	{
		return( super.remove( fileName ) );
	}

	@Override
	public RegexWholeFileModel rename( String oldFileName, String newFileName )
	{
		return( super.rename( oldFileName, newFileName ) );
	}

	@Override
	public void loadItemList() throws ConfigurationException
	{
		if( _itemsConf.configurationFileExists() )
		{
			super.loadItemList();
		}
		else
		{
			getComboBoxContent().addItem( getDefaultGlobalRegexConfigurationFileName() );
		}
	}

	public String getDefaultGlobalRegexConfigurationFileName()
	{
		return( DefaultConstantsForRegexConf.DEFAULT_GLOBAL_CONF_FILE_NAME );
	}
}
