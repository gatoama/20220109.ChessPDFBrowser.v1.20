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
package com.frojasg1.chesspdfbrowser.enginewrapper.persistency;

import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.items.ChessEngineConfigurationMap;
import com.frojasg1.applications.common.configuration.application.BaseApplicationConfigurationInterface;
import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.EngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.DefaultConstantsForChessEngineConf;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.loader.EngineInstanceConfigurationToXml;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.loader.XmlToEngineInstanceConfiguration;
import com.frojasg1.general.exceptions.ConfigurationException;
import com.frojasg1.general.xml.XmlElement;
import com.frojasg1.general.xml.persistency.XmlFileListPersistency;
import com.frojasg1.general.xml.persistency.loader.ModelToXml;
import com.frojasg1.general.xml.persistency.loader.XmlToModel;
import java.io.IOException;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class ChessEngineConfigurationPersistency  extends XmlFileListPersistency<String, EngineInstanceConfiguration>
{
	public static final String GLOBAL_CONF_FILE_NAME = "ChessEngineConfigurationPersistency.properties";

	public static final String CONFIGURATION_PATH_IN_JAR = null;

//	protected ListOfRegexWholeFiles _listOfRegexFiles = null;
//	protected ListOfRegexWholeFiles _lastListOfRegexFiles = null;

	public ChessEngineConfigurationPersistency()//( BaseApplicationConfigurationInterface appConf )
	{
	}

	@Override
	public void init( String singleXmlFileName,
						String languageFolderResourceName,
						String languageFolderDiskFileName )
	{
		throw( new RuntimeException( "Do not init with this function. try calling: " +
									"init( BaseApplicationConfigurationInterface appliConf )" ) );
	}

	@Override
	public void init( BaseApplicationConfigurationInterface appliConf,
						String configurationPathInJar,
						String languageGlobalConfFileName,
						String languagePropertiesFolderInJar )
	{
		throw( new RuntimeException( "Do not init with this init function. try calling: " +
									"init( BaseApplicationConfigurationInterface appliConf )" ) );
	}

	public void init( BaseApplicationConfigurationInterface appliConf )
	{
		super.init( appliConf, CONFIGURATION_PATH_IN_JAR, GLOBAL_CONF_FILE_NAME,
					LibConstants.sa_PROPERTIES_PATH_IN_JAR );
	}

	@Override
	public ChessEngineConfigurationMap getModelContainer()
	{
		return( (ChessEngineConfigurationMap) super.getModelContainer() );
	}

	protected ChessEngineConfigurationMap getPreviousModelContainer()
	{
		return( (ChessEngineConfigurationMap) super.getPreviousModelContainer() );
	}

	@Override
	protected String getDefaultGlobalFolderSingleName()
	{
		return( DefaultConstantsForChessEngineConf.CONF_CHESS_ENGINE_MAIN_FOLDER_DEFAULT );
	}

	@Override
	protected ChessEngineConfigurationMap createContainerOfXmlModels()
	{
		ChessEngineConfigurationMap result = new ChessEngineConfigurationMap();
		result.init( getAppliConf(), key -> createRelativeFileName(key) );

		return( result );
	}

	protected String getLibOrApplicationVersionFolder()
	{
		return( LibConstants.LIB_VERSION_MAIN_FOLDER );
	}

	@Override
	protected EngineInstanceConfiguration createFileModel( String name, XmlElement elem )
	{
		EngineInstanceConfiguration result = super.createFileModel( name, elem );
		result.setName(name);

		return( result );
	}

	@Override
	public void save() throws ConfigurationException, IOException
	{
		super.save();

		eraseNonExisting( );
	}

	@Override
	protected ModelToXml<EngineInstanceConfiguration> createModelToXmlTranslator()
	{
		return( new EngineInstanceConfigurationToXml() );
	}

	@Override
	protected XmlToModel<EngineInstanceConfiguration> createXmlToModelTranslator()
	{
		XmlToEngineInstanceConfiguration result = new XmlToEngineInstanceConfiguration();
		result.init();

		return( result );
	}

	protected String preprocessFileName( String rawFileName )
	{
		String result = null;
		if( rawFileName != null )
			result = rawFileName.replaceAll( "[\\s:/\\\\]", "_" );

		return( result );
	}

	@Override
	protected String createRelativeFileName(String key) {
		return( preprocessFileName( key ) + ".xml" );
	}
}
