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
package com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items;

import com.frojasg1.applications.common.configuration.ParameterListConfiguration;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.general.xml.persistency.container.ContainerOfModels;
import com.frojasg1.general.xml.persistency.container.SimpleMapContainerOfModels;
import com.frojasg1.general.xml.persistency.container.SimpleMapContainerOfModelsKeyString;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineConfigurationMap  extends SimpleMapContainerOfModelsKeyString<EngineInstanceConfiguration>
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessEngineConfigurationList.properties";

	// function for DefaultConstructorInitCopier
	public ChessEngineConfigurationMap()
	{
	}

	@Override
	public void init( ContainerOfModels that )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	@Override
	public void init( BaseApplicationConfigurationInterface appliConf,
						String languageGlobalConfFileName,
						String languagePropertiesFolderInJar,
						Function<String, String> fileNameCreatorFunction )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	@Override
	public <KK, VV> Map<KK, VV> createMap()
	{
		return( new LinkedHashMap<>() );
	}

	// function for DefaultConstructorInitCopier
	public void init( BaseApplicationConfigurationInterface appliConf,
						Function<String, String> fileNameCreatorFunction )
	{
		super.init( appliConf, GLOBAL_CONF_FILE_NAME,
					appliConf.getInternationalPropertiesPathInJar(),
					fileNameCreatorFunction );
	}

	// function for DefaultConstructorInitCopier
	@Override
	public void init( SimpleMapContainerOfModels<String, EngineInstanceConfiguration> that )
	{
		throw( new RuntimeException( "Non usable init function" ) );
	}

	// function for DefaultConstructorInitCopier
	public void init( ChessEngineConfigurationMap that )
	{
		super.init( that );
	}

	@Override
	protected ParameterListConfiguration createListOfModelNamesConfiguration()
	{
		return( new ListOfChessEngineNamesConfiguration( getAppliConf() ) );
	}

	@Override
	protected EngineInstanceConfiguration createModelObject()
	{
		EngineInstanceConfiguration result = new EngineInstanceConfiguration();

		return( result );
	}
}
