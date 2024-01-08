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
package com.frojasg1.chesspdfbrowser.enginewrapper.language;

import com.frojasg1.chesspdfbrowser.enginewrapper.configuration.items.ConfigurationItem;
import com.frojasg1.chesspdfbrowser.enginewrapper.constants.LibConstants;
import com.frojasg1.chesspdfbrowser.enginewrapper.move.LongAlgebraicNotationMove;
import com.frojasg1.chesspdfbrowser.enginewrapper.persistency.loader.XmlToEngineInstanceConfiguration;
import com.frojasg1.chesspdfbrowser.enginewrapper.view.configuration.EngineInstanceConfiguration_JDialog;
import com.frojasg1.general.language.LanguageResources;

/**
 *
 * @author Francisco Javier Rojas Garrido <frojasg1@hotmail.com>
 */
public class LibChessEngineLanguageResources extends LanguageResources
{
	protected static LibChessEngineLanguageResources _instance;

	public static LibChessEngineLanguageResources instance()
	{
		if( _instance == null )
		{
			_instance = new LibChessEngineLanguageResources();
			LanguageResources.instance().addLanguageResource(_instance);
		}
		return( _instance );
	}

	@Override
	public void copyOwnLanguageConfigurationFilesFromJar( String newFolder )
	{
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", XmlToEngineInstanceConfiguration.GLOBAL_CONF_FILE_NAME );
		copyFormLanguageConfigurationFileFromJarToFolder( newFolder, "EN", EngineInstanceConfiguration_JDialog.a_configurationBaseFileName );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", LongAlgebraicNotationMove.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", ConfigurationItem.GLOBAL_CONF_FILE_NAME );
		copyLanguageConfigurationFileFromJarToFolder( newFolder, "EN", "ChessEngineConfiguration_JDialog_LAN.properties" );
	}

	protected String getPropertiesPathInJar()
	{
		return( LibConstants.sa_PROPERTIES_PATH_IN_JAR );
	}
}
